package com.example.planetbiru.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtility {
    public static final int NETWORK_STATUS_NOT_CONNECTED = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;

    private static int currentConnectivityStatus = 0;

    public static void updateConnectivityStatus(Context context)
    {
        int result = NETWORK_STATUS_NOT_CONNECTED;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = NETWORK_STATUS_WIFI;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = NETWORK_STATUS_MOBILE;
                    }
                }
            }
        }
        else
        {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        result = NETWORK_STATUS_WIFI;
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        result = NETWORK_STATUS_MOBILE;
                    }
                }
            }
        }
        NetworkUtility.currentConnectivityStatus = result;
    }

    public static int getCachedConnectivityStatus() {
        return NetworkUtility.currentConnectivityStatus;
    }

    public static String getConnectionName(int connectionType) {
        String name = "none";
        if(connectionType == NETWORK_STATUS_WIFI)
        {
            name = "wifi";
        }
        else if(connectionType == NETWORK_STATUS_MOBILE)
        {
            name = "mobile data";
        }
        return name;
    }
}
