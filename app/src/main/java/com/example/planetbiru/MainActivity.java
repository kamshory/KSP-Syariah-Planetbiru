package com.example.planetbiru;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.planetbiru.config.Config;
import com.example.planetbiru.stat.ConstantString;
import com.example.planetbiru.utility.NetworkUtility;
import com.example.planetbiru.utility.Utility;
import com.example.planetbiru.utility.WebAppInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private static final String TAG = "MainActivity";
    public static final int REQUEST_SELECT_FILE = 100;
    public ValueCallback<Uri[]> pubUploadMessage;
    public ValueCallback<Uri> priUploadMessage;
    public final static int FILECHOOSER_RESULTCODE = 1;

    private String cameraFileData = null;

    private final String[] permissionList = {
            "android.permission.INTERNET",
            "android.permission.FINE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_GPS",
            "android.permission.ACCESS_ASSISTED_GPS",
            "android.permission.ACCESS_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA",
            "android.permission.RECORD_AUDIO",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.FOREGROUND_SERVICE",
            "android.permission.READ_CALENDAR",
            "android.permission.WRITE_CALENDAR",
            "android.permission.BIND_JOB_SERVICE",
            "android.permission.RECEIVE_BOOT_COMPLETED",
            "android.permission.RECEIVE_SMS"
    };
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        SharedPreferences prefs = this.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        switch(permsRequestCode){
            case 200:
                if(grantResults.length > 10) {
                    boolean internetAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean fineLocationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean accessFineLocationAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean accessGPSAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean accessAssistedAccepted = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean accessLocationAccepted = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean accessCoarseAccepted = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhoneStateAccepted = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalStorageAccepted = grantResults[8] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalStorageAccepted = grantResults[9] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[10] == PackageManager.PERMISSION_GRANTED;
                    boolean recordAudioAccepted = grantResults[11] == PackageManager.PERMISSION_GRANTED;
                    boolean modifyAudioSettingAccepted = grantResults[12] == PackageManager.PERMISSION_GRANTED;
                    boolean accessNetworkStateAccepted = grantResults[13] == PackageManager.PERMISSION_GRANTED;
                    boolean accessWifiStateAccepted = grantResults[14] == PackageManager.PERMISSION_GRANTED;
                    boolean foregroundServiceAccepted = grantResults[15] == PackageManager.PERMISSION_GRANTED;
                    boolean readCalendarAccepted = grantResults[16] == PackageManager.PERMISSION_GRANTED;
                    boolean writeCalendarAccepted = grantResults[17] == PackageManager.PERMISSION_GRANTED;
                    boolean jobServiceAccepted = grantResults[18] == PackageManager.PERMISSION_GRANTED;
                    boolean receiveBootCompletedAccepted = grantResults[19] == PackageManager.PERMISSION_GRANTED;
                    boolean receiveSMSAccepted = grantResults[20] == PackageManager.PERMISSION_GRANTED;

                    prefs.edit().putString("PERMS_INTERNET", internetAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_FINE_LOCATION", fineLocationAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_ACCESS_FINE_LOCATION", accessFineLocationAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_ACCESS_GPS", accessGPSAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_ACCESS_ASSISTED_GPS", accessAssistedAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_ACCESS_LOCATION", accessLocationAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_ACCESS_COARSE_LOCATION", accessCoarseAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_READ_PHONE_STATE", readPhoneStateAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_READ_EXTERNAL_STORAGE", readExternalStorageAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_WRITE_EXTERNAL_STORAGE", writeExternalStorageAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_CAMERA", cameraAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_RECORD_AUDIO", recordAudioAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_MODIFY_AUDIO_SETTINGS", modifyAudioSettingAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_ACCESS_NETWORK_STATE", accessNetworkStateAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_ACCESS_WIFI_STATE", accessWifiStateAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_FOREGROUND_SERVICE", foregroundServiceAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_READ_CALENDAR", readCalendarAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_WRITE_CALENDAR", writeCalendarAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_BIND_JOB_SERVICE", jobServiceAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_RECEIVE_BOOT_COMPLETED", receiveBootCompletedAccepted ? "YES" : "NO").apply();
                    prefs.edit().putString("PERMS_SMS_RECEIVED", receiveSMSAccepted ? "YES" : "NO").apply();
                }
                break;
        }
    }

    public WebView getWebView()
    {
        return this.webView;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        String internalHost = prefs.getString(ConstantString.INTERNAL_HOST, "");
        Config.setInternalHosts(internalHost);

        String homeURL = prefs.getString(ConstantString.HOME_URL, "");
        if(homeURL.contains("://"))
        {
            Config.setHomeURL(homeURL);
        }


        /*
         * Hide title bar
         */
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        this.broadcastReceiver = new PlanetActivityRunOnStartup();
        /*
         * Register intent filter for receiver
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.conn.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("com.example.planetbiru.ActivityRecognition.RestartSensor");
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(this.broadcastReceiver, intentFilter);

        /*
         * Get connectivity status and put it into cache and HTTP request header
         */
        NetworkUtility.updateConnectivityStatus(getApplicationContext());
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(ConstantString.X_APPLICATION_NAME, Config.getApplicationName());
        int connectionType = NetworkUtility.getCachedConnectivityStatus();
        requestHeaders.put(ConstantString.X_CONNECTION_TYPE, NetworkUtility.getConnectionName(connectionType));

        /*
         * Set device ID
         */
        Config.setAndroidID(this.getDeviceID());

        /*
         * Set layout
         */
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);

        /**
         * Set webView client
         */
        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Map<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put(ConstantString.X_APPLICATION_NAME, Config.getApplicationName());
                int connectionType = NetworkUtility.getCachedConnectivityStatus();
                requestHeaders.put(ConstantString.X_CONNECTION_TYPE, NetworkUtility.getConnectionName(connectionType));
                view.loadUrl(request.getUrl().toString(), requestHeaders);
                return false;
            }
        });

        /*
         * WebView setting
         */
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setDatabasePath(new StringBuilder().append(this.getFilesDir()).append("/").append(webView.getContext().getPackageName()).append("/databases/").toString());
        }

        webView.addJavascriptInterface(new WebAppInterface(getApplicationContext(), this.webView, this), "Android");

        /*
         * Set webChrome client
         */
        webView.setWebChromeClient(new WebChromeClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        webView.setWebViewClient(new PlanetWebClient(this, this, MainActivity.class));
        webView.setWebChromeClient(new WebChromeClient() {
            private View mCustomView;
            private CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            // Remove ugly video poster
            @Override
            public Bitmap getDefaultVideoPoster() {
                final Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawARGB(255, 0, 0, 0);
                return bitmap;
            }
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                priUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, ConstantString.FILE_BROWSER), FILECHOOSER_RESULTCODE);
            }

            @Override
            public void onHideCustomView()
            {
                ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            @Override
            public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
            {
                if(this.mCustomView != null) {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                pubUploadMessage = filePathCallback;
                if (filePermission()) {
                    Intent intent = new Intent(Intent.ACTION_CHOOSER);
                    Intent[] intentArray;
                    Intent takePictureIntent;
                    if(Config.isAllowTakeImage()) {
                        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImage();
                                takePictureIntent.putExtra("PhotoPath", cameraFileData);
                            } catch (IOException ex) {
                                Log.e(TAG, ConstantString.IMAGE_FILE_CREATION_FAILED, ex);
                            }

                            if (photoFile != null) {
                                cameraFileData = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                cameraFileData = null;
                                takePictureIntent = null;
                            }
                        }
                        intentArray = new Intent[]{takePictureIntent};
                    }
                    else
                    {
                        intentArray = new Intent[0];
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType(Config.getAcceptedFileType());
                    if (Config.isAllowMultipleFile()) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }
                    intent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    intent.putExtra(Intent.EXTRA_TITLE, ConstantString.FILE_CHOOSER);
                    intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    try {
                        startActivityForResult(intent, REQUEST_SELECT_FILE);
                    } catch (ActivityNotFoundException e) {
                        pubUploadMessage = null;
                        Toast.makeText(getActivity().getApplicationContext(), ConstantString.CAN_NOT_OPEN_FILE_CHOOSER, Toast.LENGTH_LONG).show();
                        return false;
                    }
                    return true;
                }
                else
                {
                    return false;
                }
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                priUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, ConstantString.FILE_BROWSER), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                priUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, ConstantString.FILE_CHOOSER), FILECHOOSER_RESULTCODE);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
                final boolean remember = false;
                if(locationPermission()) {
                    if (Config.isAskPermissionGetLocation()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Locations");
                        builder.setMessage("Would like to use your Current Location ")
                                .setCancelable(true)
                                .setPositiveButton(ConstantString.ALLOW, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // origin, allow, remember
                                        callback.invoke(origin, true, remember);
                                    }
                                })
                                .setNegativeButton(ConstantString.DENY, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // origin, allow, remember
                                        callback.invoke(origin, false, remember);
                                    }
                                })
                        ;
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        callback.invoke(origin, true, false);
                    }
                }
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        Uri uri = Uri.parse(request.getOrigin().toString());
                        if (Utility.isInternalLink(uri.getHost())) {
                            request.grant(request.getResources());
                        } else {
                            request.deny();
                        }
                    }
                });
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        Log.d(TAG, "SslError : The certificate authority is not trusted.");
                        break;
                    case SslError.SSL_EXPIRED:
                        Log.d(TAG, "SslError : The certificate has expired.");
                        break;
                    case SslError.SSL_IDMISMATCH:
                        Log.d(TAG, "The certificate Hostname mismatch.");
                        break;
                    case SslError.SSL_NOTYETVALID:
                        Log.d(TAG, "The certificate is not yet valid.");
                        break;
                }
                handler.proceed();
            }
        });

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(this.webView, true);
        }

        BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("LongLogTag")
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
            }
        };

        String savedURL = prefs.getString(ConstantString.LAST_URL_LOADED, null);
        Log.d(TAG, "savedURL : "+savedURL);
        if (savedURL == null) {
            savedURL = "";
        }
        Log.d(TAG, "savedURL : "+savedURL);
        if(!savedURL.startsWith("http"))
        {
            savedURL = "";
        }
        Log.d(TAG, "savedURL : "+savedURL);
        if (savedURL.equals("")) {
            savedURL = Config.getHomeURL();
        }
        Log.d(TAG, "savedURL : "+savedURL);
        //savedURL= "";
        String url = this.getIntent().getStringExtra("url");
        Log.d(TAG, "url : "+url);

        if(url == null || !url.contains("http") || !url.contains(":/")) {
            url = "";
        }
        Log.d(TAG, url);
        if(savedInstanceState == null) {
            if (url.equals("")) {
                if(!savedURL.equals("")) {
                    url = savedURL;
                }
                else {
                    url = Config.getHomeURL();
                }
                webView.loadUrl(url, requestHeaders);
            }
            else if (url.contains("://")) {
                webView.loadUrl(url, requestHeaders);
            }
            else {
                webView.loadUrl(url, requestHeaders);
            }
        }
        else
        {
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(this.permissionList, 200);
        }

        if(Config.isUseNotification()) {
            Intent mIntent = new Intent(this, PlanetService.class);
            //startService(mIntent);
            if (!isMyServiceRunning(PlanetService.class)) {
                PlanetService.disconnect();
                PlanetService.reset();
                PlanetService.enqueueWork(getApplicationContext(), mIntent);
            } else {
                PlanetService.stopWork();
                PlanetService.disconnect();
                PlanetService.reset();
                PlanetService.enqueueWork(getApplicationContext(), mIntent);
                PlanetService.endForeground();
            }
        }

    }

    public void windowLocation(String url)
    {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(ConstantString.X_APPLICATION_NAME, Config.getApplicationName());
        int connectionType = NetworkUtility.getCachedConnectivityStatus();
        requestHeaders.put(ConstantString.X_CONNECTION_TYPE, NetworkUtility.getConnectionName(connectionType));
        webView.loadUrl(url, requestHeaders);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onDestroy()
    {
        unregisterReceiver(this.broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        super.onResume();
        PlanetService.endForeground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent mIntent = new Intent(this, PlanetService.class);
        PlanetService.beginForeground();
        PlanetService.enqueueWork(this, mIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    public boolean filePermission() {
        if(Build.VERSION.SDK_INT >=23 && (
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
        )) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, 1);
            return false;
        } else {
            return true;
        }
    }

    public boolean locationPermission() {
        if(Build.VERSION.SDK_INT >=23 && (
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
            return false;
        } else {
            return true;
        }
    }

    @org.jetbrains.annotations.NotNull
    private File createImage() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String tmpFileName = "img_"+timeStamp+"_";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(tmpFileName,".jpg",storageDirectory);
    }

    @org.jetbrains.annotations.NotNull
    private File createVideo() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String tmpFileName    = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
        String tmpNewName     = "file_"+tmpFileName+"_";
        File storageDirectory   = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(tmpNewName, ".mp4", storageDirectory);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        Uri[] results = null;
        System.out.println(requestCode);
        if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_SELECT_FILE) {
                pubUploadMessage.onReceiveValue(null);
                return;
            }
        }
        if(resultCode== Activity.RESULT_OK || true){
            if(requestCode == REQUEST_SELECT_FILE){
                if(null == pubUploadMessage){
                    return;
                }
                ClipData clipData;
                String stringData;
                try {
                    clipData = intent.getClipData();
                    stringData = intent.getDataString();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    clipData = null;
                    stringData = null;
                }
                if (clipData == null && stringData == null && cameraFileData != null) {
                    results = new Uri[]{Uri.parse(cameraFileData)};
                } else {
                    if (clipData != null) {
                        // checking if multiple files selected or not
                        final int numSelectedFiles = clipData.getItemCount();
                        results = new Uri[numSelectedFiles];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            results[i] = clipData.getItemAt(i).getUri();
                        }
                    } else {
                        results = new Uri[]{Uri.parse(stringData)};
                    }
                }
            }
        }
        try {
            pubUploadMessage.onReceiveValue(results);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if(intent != null)
            {
                pubUploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
            }
        }
        pubUploadMessage = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        this.finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        }
    }
    private Context getActivity() {
        return MainActivity.this;
    }

    public String getDeviceID()
    {
        String androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidID;
    }

    public void setDataToDevice(String key, String data) {
        SharedPreferences prefs = this.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        prefs.edit().putString(key, data).apply();
    }

    public String getDataFromDevice(String key) {
        SharedPreferences prefs = this.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}