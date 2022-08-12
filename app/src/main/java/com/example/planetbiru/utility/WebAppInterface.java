package com.example.planetbiru.utility;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.planetbiru.SMSCache;
import com.example.planetbiru.config.Config;
import com.example.planetbiru.stat.ConstantString;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class WebAppInterface {
    private static final String TAG = "WebAppInterface";
    private Context context;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public boolean setEncryptedDataToDevice(String key, String encryptedData)
    {
        AESUtil util = new AESUtil();
        String decryptedData = util.decrypt(Config.getEncryptionKey(), encryptedData);

        List<TLV> list = TLVUtils.builderTlvList(decryptedData);
        for(int i = 0; i<list.size(); i++)
        {
            String tag = list.get(i).getTag();
            String value = list.get(i).getValue();
            context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE).edit().putString(tag, value).apply();
        }


        return true;
    }

    private Map<String, TLV> parseTLV(String decryptedData) {
        return TLVUtils.builderTlvMap(decryptedData);
    }

    @JavascriptInterface
    public void setDataToDevice(String key, String data)
    {
        context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE).edit().putString(key, data).apply();
    }

    @JavascriptInterface
    public String getDataFromDevice(String key)
    {
        return this.context.getSharedPreferences(ConstantString.CACHE_DATA, Context.MODE_PRIVATE).getString(key, null);
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(this.context, toast, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public String getSMS()
    {
        Log.d(TAG, "getSMS() ...");
        String sender = SMSCache.getSender();
        String message = SMSCache.getMessage();
        long time = SMSCache.getTime();
        JSONObject json = new JSONObject();
        String result = "";
        try {
            if(sender.length() > 0) {
                json.put("sender", sender);
                json.put("message", message);
                json.put("time", time);
                result = json.toString();
            }
            else
            {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @JavascriptInterface
    public void getCalendar(String source)
    {
        JSONObject miscData = null;
        try {
            miscData = new JSONObject(source);
            JSONObject calendarData = new JSONObject(miscData.optString("data", "{}"));
            this.addCalendar(
                    calendarData.optString("title", "Event"),
                    calendarData.optString("description", "Event"),
                    calendarData.optString("eventLocation", "Lokasi"),
                    calendarData.optString("beginTime", ""),
                    calendarData.optString("endTime", ""),
                    calendarData.optString("displayColor", "#339944")
            );
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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

    @JavascriptInterface
    public void clearSMS()
    {
        SMSCache.setSender("");
        SMSCache.setMessage("");
        SMSCache.setTime(0);
    }

}
