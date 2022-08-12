package com.example.planetbiru.notification;

import org.json.JSONException;

public interface NotificationInterface
{
	public void onNotificationReceived(RemoteMessage notification) throws JSONException;
	public void onNotificationDeleted(String notificationID);
	public void onMessageReceived(RemoteMessage message);
	public void onMessageDeleted(String messageID);
	public void onDataReceived(String[] headers, String command, String body);
	public void onDataSent(String[] headers, String command, String body);
	public void onNewToken(String token, String time, long waitToNext, int timeZone);
	public void onChangeSetting(String name, String type, Object value);
	public void onError(Exception exception);	
	public void onRegisterDeviceSendSuccess(String deviceID, String message);
	public void onRegisterDeviceSendError(String deviceID, String message, String cause);
	public void onUnregisterDeviceSendSuccess(String deviceID, String message);
	public void onUnregisterDeviceSendError(String deviceID, String message, String cause);
	public void onRegisterDeviceSuccess(String deviceID, int responseCode, String message);
	public void onRegisterDeviceError(String deviceID, int responseCode, String message, String cause);
	public void onUnregisterDeviceSuccess(String deviceID, int responseCode, String message);
	public void onUnregisterDeviceError(String deviceID, int responseCode, String message, String cause);
	public void onConnected();
	public void onDisconnected();
	public void onStopped();
	void heartbeat();
}
