package com.example.planetbiru.notification;

import android.util.Log;

public class Monitor extends Thread {
    private final Notification notification;
    private long interval = 60000;
    public Monitor(Notification notification, long interval) {
        Log.d("Notification", "Monitor");
        this.notification = notification;
        this.interval = interval;
    }
    @Override
    public void run()
    {
        do {
            try {
                Thread.sleep(this.interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.notification.callHeartbeat();
        }
        while(!this.notification.isForceStop());
    }
}
