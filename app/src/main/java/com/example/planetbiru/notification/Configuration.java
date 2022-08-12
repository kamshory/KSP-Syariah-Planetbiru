package com.example.planetbiru.notification;

import com.example.planetbiru.config.Config;

public class Configuration {

    private static long nextRead = 200;
    private static long inspectionPeriod = 10000;
    /**
     * Server address. Change this address according to production server address
     */
    private static String serverAddress = "127.0.0.1";
    /**
     * Server port number. Change this port number according to production server port number
     */
    private static int serverPort = 92;
    /**
     * Connection timeout
     */
    private static int timeout = 5000;
    /**
     * API Key. Change this key with your own key
     */
    private static String apiKey = "THE-API-KEY";
    /**
     * Group key
     */
    private static String groupKey = "THE-GROUP-KEY";
    /**
     * API Password
     */
    private static String password = "THE-API-PASSWORD";
    /**
     * Wait to reconnect to the server
     */
    private static long delayReconnect = 10000;
    /**
     * Wait to restart API
     */
    private static long delayRestart = 5000;
    /**
     * Count down reconnect
     */
    private static int countDownReconnect = 5;
    /**
     * Debug mode
     */
    private static boolean debugMode = false;
    /**
     * Re check connection
     */
    private static boolean recheckConnection = true;
    /**
     * User agent
     */
    private static String userAgent = "Push-Notification-Client version 1.0.0";
    private static long heartbeatInterval = 6000;

    public static String getServerAddress() {
        return serverAddress;
    }

    public static void setServerAddress(String serverAddress) {
        Configuration.serverAddress = serverAddress;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int serverPort) {
        Configuration.serverPort = serverPort;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static void setTimeout(int timeout) {
        Configuration.timeout = timeout;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String apiKey) {
        Configuration.apiKey = apiKey;
    }

    public static String getGroupKey() {
        return groupKey;
    }

    public static void setGroupKey(String groupKey) {
        Configuration.groupKey = groupKey;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Configuration.password = password;
    }

    public static long getDelayReconnect() {
        return delayReconnect;
    }

    public static void setDelayReconnect(long delayReconnect) {
        Configuration.delayReconnect = delayReconnect;
    }

    public static long getDelayRestart() {
        return delayRestart;
    }

    public static void setDelayRestart(long delayRestart) {
        Configuration.delayRestart = delayRestart;
    }

    public static int getCountDownReconnect() {
        return countDownReconnect;
    }

    public static void setCountDownReconnect(int countDownReconnect) {
        Configuration.countDownReconnect = countDownReconnect;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Configuration.debugMode = debugMode;
    }

    public static String getUserAgent() {
        return userAgent;
    }

    public static void setUserAgent(String userAgent) {
        Configuration.userAgent = userAgent;
    }

    public static long getInspectionPeriod() {
        return inspectionPeriod;
    }

    public static long getHeartbeatInterval() {
        return Configuration.heartbeatInterval;
    }

    public static long getNextRead() {
        return nextRead;
    }
}
