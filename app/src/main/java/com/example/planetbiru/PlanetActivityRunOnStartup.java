package com.example.planetbiru;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.planetbiru.config.Config;
import com.example.planetbiru.utility.NetworkUtility;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class PlanetActivityRunOnStartup extends BroadcastReceiver {
    private static final String TAG = "PlanetActivityRunOnStartup";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && Config.isUseNotification())
        {
            NetworkUtility.updateConnectivityStatus(context);
            PlanetService.stopWork();
            PlanetService.disconnect();
            PlanetService.reset();
            PlanetService.enqueueWork(context, new Intent(context, PlanetService.class));
        }
        if(intent.getAction().contains("ActivityRecognition.RestartSensor") && Config.isUseNotification())
        {
            PlanetService.stopWork();
            PlanetService.disconnect();
            PlanetService.reset();
            PlanetService.enqueueWork(context, new Intent(context, PlanetService.class));
        }
        if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE") || intent.getAction().equals("android.net.conn.WIFI_STATE_CHANGED"))
        {
            NetworkUtility.updateConnectivityStatus(context);
        }
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String sender;
            String message = "";
            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        this.openWebPage(context, msgs[i]);
                    }
                }
                catch(Exception e)
                {
                    Log.d("Exception caught", e.getMessage());
                }
            }
        }
    }
    public static boolean isNumeric(final String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private String validOTP(String message)
    {
        String otp = null;
        message = message.replace("\r", " ");
        message = message.replace("\n", " ");
        message = message.replace("\t", " ");
        message = message.replaceAll("\\s+", " ");
        int i;
        String msg = "";
        if(message.contains(">>>") && message.contains("<<<"))
        {
            String[] messages = message.split(" ");
            for(i = 0; i<messages.length; i++)
            {
                if(isNumeric(messages[i]) && messages[i].length() == 6)
                {
                    otp = messages[i];
                    break;
                }
            }
        }
        return otp;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void openWebPage(Context context, SmsMessage smsMessage)
    {
        String sender = smsMessage.getOriginatingAddress();
        String message = smsMessage.getMessageBody();
        smsMessage.getStatus();

        if(message.length() > 0) {
            String otp = this.validOTP(message);
            if (otp != null) {
                /*
                if (otp.length() == 6) {
                    String url = String.format(Config.getShowPinURL(), this.base64Encode(otp));
                    Intent intentHome = new Intent(context, MainActivity.class);
                    intentHome.putExtra("url", url);
                    intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentHome);
                }
                 */
                SMSCache.setMessage(message);
                SMSCache.setSender(sender);
                SMSCache.setTime((new Date()).getTime());

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String base64Encode(String input)
    {
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }


}
