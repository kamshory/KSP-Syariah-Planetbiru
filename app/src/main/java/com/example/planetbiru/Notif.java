package com.example.planetbiru;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.planetbiru.notification.Notification;
import com.example.planetbiru.notification.NotificationInterface;
import com.example.planetbiru.notification.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

/**
 * Notif class derived from Notification
 * <p>Here you can override some methods to get data from the server.</p>
 * @author Kamshory
 *
 */
public class Notif extends Notification implements NotificationInterface
{
	private static final String TAG = "Notif";
	protected Context context;
	/**
	 * Default constructor
	 */
	public Notif()
	{
		super();
	}
	/**
	 * Constructor with API key, API password, device ID and group key
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 */
	public Notif(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey)
	{
		super(context, activityClass, apiKey, password, deviceID, groupKey);
		this.context = context;
	}
	public Notif(Context context, String apiKey, String password, String deviceID, String groupKey)
	{
		super(context, apiKey, password, deviceID, groupKey);
		this.context = context;
	}
	/**
	 * Constructor with API key, API password, device ID, group key, server address and server port
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 */
	public Notif(Context context, Class<?> activityClass,  String apiKey, String password, String deviceID, String groupKey, String serverAddress)
	{
		super(context, activityClass, apiKey, password, deviceID, groupKey, serverAddress, 0);
		this.context = context;
		int serverPort = -1;
		boolean ssl = false;
		if(serverAddress.contains("://"))
		{
			try 
			{
				URL url = new URL(serverAddress);
				serverAddress = url.getHost();
				serverPort = url.getPort();
				if(serverAddress.contains("https://"))
				{
					ssl = true;
				}
				if(serverPort == -1)
				{
					if(ssl)
					{
						serverPort = 443;
					}
					else
					{
						serverPort = 80;
					}
				}
				this.serverAddress = serverAddress;
				this.serverPort = serverPort;
				this.ssl = ssl;
			} 
			catch (MalformedURLException e) 
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * Constructor with API key, API password, device ID, group key, server address and server port
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 */
	public Notif(Context context, Class<?> activityClass,  String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		super(context, activityClass, apiKey, password, deviceID, groupKey, serverAddress, serverPort);
		this.context = context;
	}
	public Notif(Context context, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		super(context, apiKey, password, deviceID, groupKey, serverAddress, serverPort);
		this.context = context;
	}
	/**
	 * Constructor with API key, API password, device ID, group key, server address and server port
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 * @param ssl SSL connection
	 */
	public Notif(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl)
	{
		super(context, activityClass, apiKey, password, deviceID, groupKey, serverAddress, serverPort, ssl);
		this.context = context;
	}
	public Notif(Context context, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl)
	{
		super(context, apiKey, password, deviceID, groupKey, serverAddress, serverPort, ssl);
		this.context = context;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public void onNotificationReceived(RemoteMessage remoteMessage) throws JSONException {
		// TODO: Add your code here to insert message into local database
		this.createNotification(remoteMessage);
		if(remoteMessage.getNotification().getMiscData() != null)
		{
			this.processMiscData(remoteMessage);
		}
	}

	@Override
	public void onNotificationDeleted(String notificationID)
	{
		// TODO: Add your code here to delete message from local database
	}

	@Override
	public void onDataReceived(String[] headers, String command, String body)
	{
		//TODO: Add your code here when data received
	}

	@Override
	public void onDataSent(String[] headers, String command, String body)
	{
		//TODO: Add your code here when data sent
	}

	@Override
	public void onNewToken(String token, String time, long waitToNext, int timeZone)
	{
		// TODO: Add your code here when token is changed
	}

	@Override
	public void onChangeSetting(String name, String type, Object value)
	{
		// TODO: Add your code here when setting was changed
		/**
		 * The name of setting can be:
		 * timeout
		 * delayReconnect
		 * delayRestart
		 */
	}

	@Override
	public void onError(Exception exception)
	{
		// TODO: Add your code here to get error message
	}

	@Override
	public void onRegisterDeviceSendSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when registration device data sent
	}

	@Override
	public void onRegisterDeviceSendError(String deviceID, String message, String cause)
	{
		// TODO: Add your code here when registration device data can not be sent
	}

	@Override
	public void onUnregisterDeviceSendSuccess(String deviceID, String message)
	{
		// TODO: Add your code here when unregistration device data sent
	}

	@Override
	public void onUnregisterDeviceSendError(String deviceID, String message, String cause)
	{
		// TODO: Add your code here when unregistration device data can not be sent
	}

	@Override
	public void onRegisterDeviceSuccess(String deviceID, int responseCode, String message)
	{
		// TODO: Add your code here when register device is success
	}
	public void onRegisterDeviceError(String deviceID, int responseCode, String message, String cause)
	{
		// TODO: Add your code here when register device is error
	}

	@Override
	public void onUnregisterDeviceSuccess(String deviceID, int responseCode, String message)
	{
		// TODO: Add your code here when unregister device is success
	}

	@Override
	public void onUnregisterDeviceError(String deviceID, int responseCode, String message, String cause)
	{
		// TODO: Add your code here when unregister device is error
	}

	public NotificationManager getNotificationHandler(NotifChannel notifChannel)
	{
		NotificationManager notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			@SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(notifChannel.id, notifChannel.name, NotificationManager.IMPORTANCE_MAX);
			notificationChannel.setDescription(notifChannel.description);
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setVibrationPattern(notifChannel.vibration);
			notificationChannel.enableVibration(true);
			notificationManager.createNotificationChannel(notificationChannel);
		}
		return notificationManager;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public void createNotification(RemoteMessage remoteMessage)
	{
		System.out.println("createNotification()");
		NotifChannel notifChannel = new NotifChannel();
		notifChannel.id = remoteMessage.getNotification().getChannelID();
		if(remoteMessage.getNotification().getVibrate().length() > 0)
		{
			notifChannel.vibration = new long[remoteMessage.getNotification().getVibrate().length()];
			for (int i = 0; i < remoteMessage.getNotification().getVibrate().length(); ++i) {
				notifChannel.vibration[i] = remoteMessage.getNotification().getVibrate().optLong(i, 0);
			}
		}
		long currentMilliseconds = System.currentTimeMillis();
		NotificationManager notificationManager = this.getNotificationHandler(notifChannel);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.context, notifChannel.id);
		PendingIntent contentIntent;
		Intent notificationIntent;

		notificationIntent = new Intent(this.context, MainActivity.class);
		notificationIntent.setAction(Long.toString(currentMilliseconds));
		notificationIntent.putExtra("url", remoteMessage.getNotification().getUri().toString());
		contentIntent = PendingIntent.getActivity(this.context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.setContentIntent(contentIntent);
		notificationBuilder.setAutoCancel(false);
		notificationBuilder.setDefaults(android.app.Notification.DEFAULT_ALL);
		notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
		notificationBuilder.setPriority(android.app.Notification.PRIORITY_MAX);

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
		notificationManager.notify((int) remoteMessage.getNotification().getId(), notificationBuilder.build());
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public boolean addCalendar(String title, String description, String eventLocation, String beginTime, String endTime, String displayColor)
	{
		Timestamp from = Timestamp.valueOf(beginTime);
		Timestamp to = Timestamp.valueOf(endTime);
		return this.addCalendar(title, description, eventLocation, from.getTime(), to.getTime(), displayColor);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public boolean addCalendar(String title, String description, String eventLocation, long beginTime, long endTime, String displayColor)
	{
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
		this.context.startActivity(intent);
		return true;
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private void processMiscData(RemoteMessage remoteMessage) throws JSONException {
		JSONObject miscData = new JSONObject(remoteMessage.getNotification().getMiscData());
		if(miscData == null)
		{
			miscData = new JSONObject();
		}
		String type = miscData.optString("type", "");
		String mime = miscData.optString("mime", "");
		String data = miscData.optString("data", "");
		if(type.equals("calendar"))
		{
			JSONObject calendarData = new JSONObject();
			if(mime.contains("json"))
			{
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
}
