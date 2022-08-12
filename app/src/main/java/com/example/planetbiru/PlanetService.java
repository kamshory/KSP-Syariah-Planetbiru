package com.example.planetbiru;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.example.planetbiru.config.Config;
import com.example.planetbiru.notification.HTTPClient;
import com.example.planetbiru.notification.HTTPResponse;
import com.example.planetbiru.notification.Notification;
import com.example.planetbiru.notification.RemoteMessage;
import com.example.planetbiru.stat.ConstantString;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLSocket;

public class PlanetService extends JobIntentService {
    private static final String TAG = "PlanetService";
    private static boolean started;
    private Intent intent;
    private Context context;
    private Runnable runnable;
    private Thread tread;
    public static boolean destroyed = false;
    private static final int JOB_ID = 1000;
    private Timer timer;
    private TimerTask timerTask;
    public int counter = 0;
    public static boolean loggedIn = false;
    public static Notification notif = new Notification();
    private static boolean foreground = true;

    public static void beginForeground()
    {
        foreground = true;
    }
    public static void endForeground()
    {
        foreground = true;
    }
    public static void reset()
    {
        PlanetService.started = false;
        PlanetService.destroyed = true;
    }
    public static void enqueueWork(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        loggedIn = prefs.getBoolean(ConstantString.RECEIVE_NOTIFICATION, false);
        if(!PlanetService.started) {
            notif.stop();
            PlanetService.started = true;
            PlanetService.destroyed = false;
            Log.d(TAG, "Start");
            enqueueWork(context, PlanetService.class, JOB_ID, intent);
        }
        else
        {
            Log.d(TAG,"Not start");
        }
    }

    public static void stopWork() {
        PlanetService.started = false;
        PlanetService.destroyed = true;
        notif.stop();;
    }

    public static void disconnect() {
        notif.disconnect();
        PlanetService.started = false;
        PlanetService.destroyed = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        //startTimer();
        return Service.START_STICKY;
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 10000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                checkConnection();
            }
        };
    }
    public void checkConnection()
    {
        Log.d(TAG, "checkConnection()");
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "protected void onHandleWork(@NonNull Intent intent)");
        this.intent = intent;
        if(loggedIn)
        {
            receiveNotificationSync();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void receiveNotificationSync() {
        String serverAddress = Config.getPushServerAddress();
        int serverPort = Config.getPushServerPort();
        boolean ssl = Config.isPushSSL();
        String cookie = Config.getCookie();
        String apiKey = Config.getApiKey();
        String apiPassword = Config.getApiClientPassword();
        String deviceID = getDeviceID();
        String urlUserInfo = Config.getUrlUserInfo();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        String groupKey = prefs.getString(ConstantString.GROUP_KEY, null);
        if(groupKey == null) {
            groupKey = "";
        }
        if(groupKey.equals("")) {
            groupKey = getGroupKey(urlUserInfo, cookie);
        }

        notif = new Notification(getApplicationContext(), MainActivity.class, apiKey, apiPassword, deviceID, groupKey, serverAddress, serverPort, ssl){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onNotificationReceived(RemoteMessage remoteMessage) throws JSONException {
                createNotification(remoteMessage);
                if(remoteMessage.getNotification().getMiscData() != null) {
                    processMiscData(remoteMessage);
                }
            }

            @Override
            public void onConnected()
            {
            }

            @Override
            public void onDisconnected()
            {
                this.stop();
                reset();
                enqueueWork(getApplicationContext(), new Intent(getApplicationContext(), PlanetService.class));
            }
        };
        notif.connect();
        notif.start();
    }

    public JSONObject getUserInfo(String urlUserInfo, String cookie) {
        HTTPClient httpClient = new HTTPClient();
        JSONObject info = new JSONObject();
        try {
            HTTPResponse response = httpClient.get(urlUserInfo, cookie);
            info = new JSONObject(response.body);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public String getGroupKey(String urlUserInfo, String cookie) {
        JSONObject info = this.getUserInfo(urlUserInfo, cookie);
        if(info.has("data")) {
            JSONObject data = info.optJSONObject("data");
            return data.optString("groupKey", "");
        }
        return "";
    }

    public void receiveNotificationAsync() {
        System.err.println("startService()");
        runnable = new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void startS(String pushServerAddress,
                               int pushServerPort,
                               boolean ssl,
                               String cookie,
                               String apiKey,
                               String apiPassword,
                               String deviceID,
                               String urlUserInfo
            ) {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
                String groupKey = prefs.getString(ConstantString.GROUP_KEY, null);
                if(groupKey == null) {
                    groupKey = "";
                }
                if(groupKey.equals("")) {
                    groupKey = this.getGroupKey(urlUserInfo, cookie);
                }
                Notif asyncNotif = new Notif(getApplicationContext(), MainActivity.class, apiKey, apiPassword, deviceID, groupKey, pushServerAddress, pushServerPort, ssl);
                asyncNotif.start();
            }

            public JSONObject getUserInfo(String urlUserInfo, String cookie)
            {
                HTTPClient httpClient = new HTTPClient();
                JSONObject info = new JSONObject();
                try {
                    HTTPResponse response = httpClient.get(urlUserInfo, cookie);
                    info = new JSONObject(response.body);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return info;
            }

            public String getGroupKey(String urlUserInfo, String cookie)
            {
                JSONObject info = this.getUserInfo(urlUserInfo, cookie);
                if(info.has("data")) {
                    JSONObject data = info.optJSONObject("data");
                    return data.optString("groupKey", "");
                }
                return "";
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                System.err.println("run()");
                String serverAddress = Config.getPushServerAddress();
                int serverPort = Config.getPushServerPort();
                boolean ssl = Config.isPushSSL();
                String cookie = Config.getCookie();
                String apiKey = Config.getApiKey();
                String apiPassword = Config.getApiClientPassword();
                String deviceID = getDeviceID();
                String urlUserInfo = Config.getUrlUserInfo();
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
                String groupKey = prefs.getString(ConstantString.GROUP_KEY, null);
                if(groupKey == null) {
                    groupKey = "";
                }
                if(groupKey.equals("")) {
                    groupKey = this.getGroupKey(urlUserInfo, cookie);
                }
                Notification notif = new Notification(getApplicationContext(), MainActivity.class, apiKey, apiPassword, deviceID, groupKey, serverAddress, serverPort, ssl){
                    @Override
                    public void onDataReceived(String[] headers, String command, String body) {
                        super.onDataReceived(headers, command, body);
                    }

                    @Override
                    public void onNotificationReceived(RemoteMessage remoteMessage) throws JSONException {
                        createNotification(remoteMessage);
                        if(remoteMessage.getNotification().getMiscData() != null)
                        {
                            processMiscData(remoteMessage);
                        }
                    }

                    @Override
                    public void onConnected()
                    {
                        Handler mainHandler = new Handler(getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Notification connected", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onDisconnected()
                    {
                        Handler mainHandler = new Handler(getMainLooper());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Notification connected", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };
                notif.connect();
                notif.start();
            }
        };
        tread = new Thread(runnable);
        tread.start();
    }

    public NotificationManager getNotificationHandler(NotifChannel notifChannel)
    {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(notifChannel.id, notifChannel.name, NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription(notifChannel.description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(notifChannel.vibration);
            notificationChannel.enableVibration(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotification(RemoteMessage remoteMessage)
    {
        NotifChannel notifChannel = new NotifChannel();
        notifChannel.id = remoteMessage.getNotification().getChannelID();
        if(remoteMessage.getNotification().getVibrate().length() > 0) {
            notifChannel.vibration = new long[remoteMessage.getNotification().getVibrate().length()];
            for (int i = 0; i < remoteMessage.getNotification().getVibrate().length(); ++i) {
                notifChannel.vibration[i] = remoteMessage.getNotification().getVibrate().optLong(i, 0);
            }
        }
        long currentMilliseconds = System.currentTimeMillis();
        NotificationManager notificationManager = this.getNotificationHandler(notifChannel);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), notifChannel.id);
        PendingIntent contentIntent;
        Intent notificationIntent;

        notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(Long.toString(currentMilliseconds));
        notificationIntent.putExtra("url", remoteMessage.getNotification().getUri().toString());
        contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setDefaults(android.app.Notification.DEFAULT_ALL);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setPriority(android.app.Notification.PRIORITY_HIGH);

        if(remoteMessage.getNotification().getTime() > 0)
        {
            notificationBuilder.setWhen(remoteMessage.getNotification().getTime());
        }

        if(remoteMessage.getNotification().getTickerText().length() > 0)
        {
            notificationBuilder.setTicker(remoteMessage.getNotification().getTickerText());
        }
        if(remoteMessage.getNotification().getTitle().length() > 0)
        {
            notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
        }
        if(remoteMessage.getNotification().getSubtitle().length() > 0)
        {
            notificationBuilder.setContentInfo(remoteMessage.getNotification().getSubtitle());
        }
        if(remoteMessage.getNotification().getBody().length() > 0)
        {
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
        }
        if(foreground)
        {
            startForeground((int) remoteMessage.getNotification().getId(), notificationBuilder.build());
        }
        else
        {
            notificationManager.notify((int) remoteMessage.getNotification().getId(), notificationBuilder.build());
            startForegroundService(notificationIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean addCalendar(String title, String description, String eventLocation, String beginTime, String endTime, String displayColor) {
        Timestamp from = Timestamp.valueOf(beginTime);
        Timestamp to = Timestamp.valueOf(endTime);
        return this.addCalendar(title, description, eventLocation, from.getTime(), to.getTime(), displayColor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean addCalendar(String title, String description, String eventLocation, long beginTime, long endTime, String displayColor) {
        Calendar calendarEvent = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        int color = 0;
        if(displayColor.contains("#"))
        {
            displayColor = displayColor.replaceAll("[^0-9A-Fa-f]", "");
            if(displayColor.equals(""))
            {
                displayColor = "0";
            }
            color = Integer.parseInt(displayColor, 16);
        }
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", beginTime);
        intent.putExtra("eventLocation", eventLocation);
        intent.putExtra("allDay", false);
        intent.putExtra("rule", "FREQ=DAILY;COUNT=1");
        intent.putExtra("endTime", endTime);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        getApplicationContext().startActivity(intent);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processMiscData(RemoteMessage remoteMessage) throws JSONException {
        JSONObject miscData = new JSONObject(remoteMessage.getNotification().getMiscData());
        if(miscData == null) {
            miscData = new JSONObject();
        }
        String type = miscData.optString("type", "");
        String mime = miscData.optString("mime", "");
        String data = miscData.optString("data", "");
        if(type.equals("calendar")) {
            JSONObject calendarData = new JSONObject();
            if(mime.contains("json")) {
                calendarData = new JSONObject(miscData.optString("data", "{}"));
                this.addCalendar(
                        calendarData.optString("title", "Event"),
                        calendarData.optString("description", "Event"),
                        calendarData.optString("eventLocation", "Lokasi"),
                        calendarData.optString("beginTime", ""),
                        calendarData.optString("endTime", ""),
                        calendarData.optString("displayColor", "#339944")
                );
            }
        }
    }

    public String getDeviceID() {
        String androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidID;
    }
 }
