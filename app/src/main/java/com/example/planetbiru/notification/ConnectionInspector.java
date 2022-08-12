package com.example.planetbiru.notification;

import org.json.JSONObject;

import java.io.IOException;

public class ConnectionInspector extends Thread{
    private Notification notification;
    private SocketIOSSL socketIO;
    private long period;
    private boolean running = true;

    public ConnectionInspector(SocketIOSSL socketIO, Notification notification, long period) {
        this.socketIO = socketIO;
        this.notification = notification;
        this.period = period;
    }

    @Override
    public void run() {
        if(false) {
            try {
                while (this.running) {
                    Thread.sleep(this.period);
                    if (!this.isConnected(this.socketIO)) {
                        this.running = false;
                        try {
                            this.socketIO.getSocket().close();
                        } catch (IOException e) {
                            if (Configuration.isDebugMode()) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (Configuration.isDebugMode()) {
                    if (Configuration.isDebugMode()) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean isConnected(SocketIOSSL socketIO) {
        return (!socketIO.getSocket().isClosed() && socketIO.getSocket().isConnected());
    }

    public void ping()
    {
        try {
            if (this.notification.connected) {
                this.socketIO.resetRequestHeader();
                this.socketIO.addRequestHeader("Command", "ping");
                this.socketIO.addRequestHeader("Content-Type", "application/json");
                JSONObject pingData = new JSONObject();
                String data = pingData.toString();
                boolean connected = this.socketIO.write(data);
                if (!connected) {
                    notification.stop();
                }
            }
        }
        catch (IOException e) {
            try {
                socketIO.getSocket().close();
                this.notification.stop();
            } catch (IOException ex) {
                if(Configuration.isDebugMode())
                {
                    ex.printStackTrace();
                }
            }
            if(Configuration.isDebugMode())
            {
                if(Configuration.isDebugMode())
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopThread() {
        this.running = false;
    }
}
