package com.example.planetbiru.utility;

import com.example.planetbiru.notification.HTTPClient;
import com.example.planetbiru.notification.HTTPResponse;

import org.json.JSONObject;

public class NotifTool {
    private static final String TAG = "NotifTool";

    public static JSONObject registerDevice(String urlDeviceRegistration, String cookie, String deviceID)
    {
        HTTPClient httpClient = new HTTPClient();
        HTTPResponse response;
        JSONObject info = new JSONObject();
        try
        {
            String data = "action=register-device&device_id="+deviceID;
            response = httpClient.post(urlDeviceRegistration, data, cookie);
            info = new JSONObject(response.body);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return info;
    }

    public static JSONObject unregisterDevice(String urlDeviceRegistration, String cookie, String deviceID)
    {
        HTTPClient httpClient = new HTTPClient();
        JSONObject info = new JSONObject();
        try
        {
            String data = "action=unregister-device&device_id="+deviceID;
            HTTPResponse response = httpClient.post(urlDeviceRegistration, data, cookie);
            info = new JSONObject(response.body);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return info;
    }
    public static String getGroupKey(String urlUserInfo, String cookie)
    {
        JSONObject info = getUserInfo(urlUserInfo, cookie);
        String groupKey = "";
        if(info.has("data"))
        {
            JSONObject data = info.optJSONObject("data");
            groupKey = data.optString("groupKey", "");
        }
        return groupKey;
    }
    public static JSONObject getUserInfo(String urlUserInfo, String cookie)
    {
        HTTPClient httpClient = new HTTPClient();
        JSONObject info = new JSONObject();
        try
        {
            HTTPResponse response = httpClient.get(urlUserInfo, cookie);
            info = new JSONObject(response.body);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return info;
    }
}
