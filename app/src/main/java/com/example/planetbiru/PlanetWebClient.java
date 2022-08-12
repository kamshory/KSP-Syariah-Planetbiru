package com.example.planetbiru;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.planetbiru.config.Config;
import com.example.planetbiru.stat.ConstantString;
import com.example.planetbiru.utility.NetworkUtility;
import com.example.planetbiru.utility.NotifTool;
import com.example.planetbiru.utility.Utility;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PlanetWebClient extends WebViewClient {
    private static final String TAG = "PlanetWebClient";
    private final Context context;
    private MainActivity myActivity;
    private Class<?> activityClass;

    public PlanetWebClient(Context context, MainActivity myActivity, Class<?> activityClass) {
        this.context = context;
        this.myActivity = myActivity;
        this.activityClass = activityClass;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        // Make sure there is no existing message
        if (myActivity.pubUploadMessage != null) {
            this.myActivity.pubUploadMessage.onReceiveValue(null);
            this.myActivity.pubUploadMessage = null;
        }
        this.myActivity.pubUploadMessage = filePathCallback;
        Intent intent = fileChooserParams.createIntent();
        try {
            this.myActivity.startActivityForResult(intent, MainActivity.REQUEST_SELECT_FILE);
        } catch (ActivityNotFoundException e) {
            this.myActivity.pubUploadMessage = null;
            Toast.makeText(this.myActivity, ConstantString.CAN_NOT_OPEN_FILE_CHOOSER, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        Uri uri = Uri.parse(url);
        if (Utility.isInternalLink(uri.getHost())) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(ConstantString.X_APPLICATION_NAME, Config.getApplicationName());
            map.put(ConstantString.X_APPLICATION_VERSION, Config.getApplicationVersion());
            view.loadUrl(url, map);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            this.context.startActivity(intent);
        }

        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Uri uri = request.getUrl();
        String url = uri.toString();
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(ConstantString.X_APPLICATION_NAME, Config.getApplicationName());
        requestHeaders.put(ConstantString.X_APPLICATION_VERSION, Config.getApplicationVersion());
        int connectionType = NetworkUtility.getCachedConnectivityStatus();
        requestHeaders.put(ConstantString.X_CONNECTION_TYPE, NetworkUtility.getConnectionName(connectionType));
        SharedPreferences prefs = this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        if(url.contains("unregister-device.php"))
        {
            this.unregisterDevice();
        }
        if(url.endsWith(".php"))
        {
            this.loading();
        }

        if(Utility.isInternalLink(uri.getHost())) {
            view.loadUrl(uri.toString(), requestHeaders);
        }
        else if(url.contains(":///android_"))
        {
            File file = context.getExternalFilesDir(url);
            String path = file.getAbsolutePath();
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) {
                Uri apkUri = FileProvider.getUriForFile(context, Config.getAppProvider(), file);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "text/html");
            }
            else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "text/html");
            }
            String newURL = prefs.getString(ConstantString.LAST_URL_TO_BE_LOADED, null);
            if(newURL == null)
            {
                newURL = "";
            }
            view.loadUrl(newURL, requestHeaders);
            return false;
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            this.context.startActivity(intent);
            return true;
        }
        return true;
    }

    private void loading() {
        this.myActivity.getWebView().loadUrl("javascript:showLoading()");
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
        Uri uri = Uri.parse(url);
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(ConstantString.X_APPLICATION_NAME, Config.getApplicationName());
        int connectionType = NetworkUtility.getCachedConnectivityStatus();
        requestHeaders.put(ConstantString.X_CONNECTION_TYPE, NetworkUtility.getConnectionName(connectionType));
        SharedPreferences prefs = this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);

        prefs.edit().putString(ConstantString.LAST_URL_TO_BE_LOADED, url).apply();
        if(connectionType < 1 && !url.equals(Config.getReloadURL()) && !url.equals(Config.getErrorPageURL()))
        {
            view.loadUrl(Config.getReloadURL(), requestHeaders);
        }
        super.onPageStarted(view, url, favicon);
    }

    public void loginNotification()
    {
        SharedPreferences prefs = this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(ConstantString.RECEIVE_NOTIFICATION, true).commit();
        PlanetService.stopWork();
        PlanetService.enqueueWork(this.context, new Intent(this.context, PlanetService.class));
    }
    public void logoutNotification()
    {
        SharedPreferences prefs = this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(ConstantString.RECEIVE_NOTIFICATION, false).commit();
        PlanetService.stopWork();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        String cookies = CookieManager.getInstance().getCookie(url);
        Config.setCookie(cookies);
        SharedPreferences prefs = this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        prefs.edit().putString(ConstantString.LAST_URL_LOADED, url).apply();
        if(url.contains("login.php") || url.contains("register-device.php"))
        {
            this.registerDevice();
            this.loginNotification();
        }
        if(url.contains("logout.php"))
        {
            this.logoutNotification();
        }
       super.onPageFinished(view, url);
    }

    private boolean registerDevice() {
        SharedPreferences prefs = this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
        String registered = prefs.getString(ConstantString.DEVICE_REGISTERED, null);
        if(registered == null)
        {
            registered = "";
        }
        if(!registered.equals(ConstantString.YES) || Config.isAlwaysRegisterDevice()) {
            String action = "register-device";
            JSONObject data = new JSONObject();
            PlanetAsyncTaskUserRegistration task = new PlanetAsyncTaskUserRegistration(this.context, this.activityClass, action, data);
            task.start();
        }
        return false;
    }

    private boolean unregisterDevice() {
        String action = "unregister-device";
        JSONObject data = new JSONObject();
        PlanetAsyncTaskUserRegistration task = new PlanetAsyncTaskUserRegistration(this.context, this.activityClass, action, data);
        task.start();
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onPermissionRequest(final PermissionRequest request) {
        request.grant(request.getResources());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        Uri uri = request.getUrl();
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(ConstantString.X_APPLICATION_NAME, Config.getApplicationName());
        int connectionType = NetworkUtility.getCachedConnectivityStatus();
        requestHeaders.put(ConstantString.X_CONNECTION_TYPE, NetworkUtility.getConnectionName(connectionType));
        String format = Config.getErrorPageMessageFormat();
        String message = String.format(format, uri.toString());
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show();
        view.loadUrl(Config.getErrorPageURL(), requestHeaders);
        super.onReceivedError(view, request, error);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        Uri uri = request.getUrl();
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(ConstantString.X_APPLICATION_NAME, Config.getApplicationName());
        int connectionType = NetworkUtility.getCachedConnectivityStatus();
        requestHeaders.put(ConstantString.X_CONNECTION_TYPE, NetworkUtility.getConnectionName(connectionType));
        String format = Config.getErrorPageMessageFormat();
        String message = String.format(format, uri.toString());
        Toast.makeText(this.context, message, Toast.LENGTH_LONG).show();
        view.loadUrl(Config.getErrorPageURL(), requestHeaders);
        super.onReceivedHttpError(view, request, errorResponse);
    }

    private class PlanetAsyncTaskUserRegistration extends Thread {
        private JSONObject data;
        private String action;
        private Context context;
        private Class<?> activityClass;
        String result;

        public PlanetAsyncTaskUserRegistration(Context context, Class<?> activityClass, String action, JSONObject data)
        {
            this.action = action;
            this.context = context;
            this.activityClass = activityClass;
            this.data = data;
        }

        @Override
        public void run() {
            Looper.prepare();
            try {
                if(action.equals("register-device")) {
                    this.registerDevice(this.data);
                }
                else if(this.action.equals("unregister-device")) {
                    this.unregisterDevice(this.data);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        private void registerDevice(JSONObject data)
        {
            SharedPreferences prefs = this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
            if(Config.isToastDeviceRegistration())
            {
                Toast.makeText(this.context, ConstantString.REGISTERING_DEVICE, Toast.LENGTH_LONG).show();
            }
            JSONObject info = NotifTool.registerDevice(Config.getUrlDeviceRegistration(), Config.getCookie(), ((MainActivity) this.context).getDeviceID());
            if(info.optBoolean("success", false))
            {
                if(Config.isToastDeviceRegistration())
                {
                    Toast.makeText(this.context, ConstantString.DEVICE_REGISTRATION_SUCCESSFULLY, Toast.LENGTH_LONG).show();
                }
                prefs.edit().putString(ConstantString.DEVICE_REGISTERED, ConstantString.YES).apply();
            }
            String groupKey = NotifTool.getGroupKey(Config.getUrlUserInfo(), Config.getCookie());
            if(!groupKey.equals(""))
            {
                prefs.edit().putString(ConstantString.GROUP_KEY, groupKey).apply();
            }
        }
        private void unregisterDevice(JSONObject data) {
            SharedPreferences prefs = this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE);
            if(Config.isToastDeviceRegistration())
            {
                Toast.makeText(this.context, ConstantString.UNREGISTERING_DEVICE, Toast.LENGTH_LONG).show();
            }
            JSONObject info = NotifTool.unregisterDevice(Config.getUrlDeviceRegistration(), Config.getCookie(), ((MainActivity) this.context).getDeviceID());
            if(info.optBoolean("success", false))
            {
                prefs.edit().putString(ConstantString.DEVICE_REGISTERED, "").apply();
                if(Config.isToastDeviceRegistration())
                {
                    Toast.makeText(this.context, ConstantString.DEVICE_UNREGISTRATION_SUCCESSFULLY, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
