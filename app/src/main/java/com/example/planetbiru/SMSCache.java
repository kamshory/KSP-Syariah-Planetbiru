package com.example.planetbiru;

public class SMSCache {
    private static String sender = "";
    private static String message = "";
    private static long time = 0;

    public static String getSender() {
        return sender;
    }

    public static void setSender(String sender) {
        SMSCache.sender = sender;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        SMSCache.message = message;
    }

    public static long getTime() {
        return time;
    }

    public static void setTime(long time) {
        SMSCache.time = time;
    }
}
