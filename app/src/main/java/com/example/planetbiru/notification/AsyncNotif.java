package com.example.planetbiru.notification;

import android.content.Context;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import com.example.planetbiru.Notif;

/**
 * Asynchronous notification
 * <p>This class is useful to get asynchronous notification. So that the application can run normally hanged.</p>
 * @author Kamshory, MT
 *
 */
public class AsyncNotif extends Thread
{
	/**
	 * Notif
	 */
	Notif notif = new Notif();
	/**
	 * API key
	 */
	protected String apiKey;
	/**
	 * API password
	 */
	protected String password;
	/**
	 * Device ID
	 */
	protected String deviceID;
	/**
	 * Interval
	 */
	protected long interval;
	/**
	 * Register device
	 */
	protected boolean isRegisterDevice = false;
	/**
	 * Unregister device
	 */
	protected boolean isUnregisterDevice = false;
	/**
	 * Constructor
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public AsyncNotif(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey)
	{
		this.notif = new Notif(context, activityClass, apiKey, password, deviceID, groupKey);
		this.notif.connect();
	}
	@RequiresApi(api = Build.VERSION_CODES.O)
	public AsyncNotif(Context context, String apiKey, String password, String deviceID, String groupKey)
	{
		this.notif = new Notif(context, apiKey, password, deviceID, groupKey);
		this.notif.connect();
	}

	/**
	 * Constructor
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public AsyncNotif(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey, String serverAddress)
	{
		this.notif = new Notif(context, activityClass, apiKey, password, deviceID, groupKey, serverAddress);
		this.notif.connect();
	}
	/**
	 * Constructor
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public AsyncNotif(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		this.notif = new Notif(context, activityClass, apiKey, password, deviceID, groupKey, serverAddress, serverPort, false);
		this.notif.connect();
	}
	@RequiresApi(api = Build.VERSION_CODES.O)
	public AsyncNotif(Context context, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		this.notif = new Notif(context, apiKey, password, deviceID, groupKey, serverAddress, serverPort, false);
		this.notif.connect();
	}
	/**
	 * Constructor
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 * @param ssl 
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public AsyncNotif(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl)
	{
		this.notif = new Notif(context, activityClass, apiKey, password, deviceID, groupKey, serverAddress, serverPort, ssl);
		this.notif.connect();
	}
	@RequiresApi(api = Build.VERSION_CODES.O)
	public AsyncNotif(Context context, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl)
	{
		this.notif = new Notif(context, apiKey, password, deviceID, groupKey, serverAddress, serverPort, ssl);
		this.notif.connect();
	}
	/**
	 * Constructor
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 * @param serverAddress Server address of the push notification
	 * @param serverPort Server port of the push notification
	 * @param timeout Timeout of the push notification
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public AsyncNotif(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl, int timeout)
	{
		this.notif = new Notif(context, activityClass, apiKey, password, deviceID, groupKey, serverAddress, serverPort, ssl);
		this.notif.setTimeout(timeout);
		this.notif.connect();
	}

	/**
	 * Set debug mode
	 * @param debugMode Debug mode
	 */
	public void setDebugMode(boolean debugMode)
	{
		this.setDebugMode(debugMode);
	}
	/**
	 * Override run method
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public void run()
	{
		Looper.prepare();
		while(true)
		{
			this.notif.start();
		}
	}
	/**
	 * Register device ID to the push server
	 * @param deviceID Device ID
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public void registerDevice(String deviceID)
	{
		this.notif.registerDevice(deviceID);
	}
	/**
	 * Unregister device from the push server
	 * @param deviceID Device ID
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public void unregisterDevice(String deviceID)
	{
		this.notif.unregisterDevice(deviceID);
	}
	/**
	 * Register device to application server
	 * @param url URL of the application server
	 * @param deviceID Device ID
	 * @param group User group
	 * @param cookie Cookie
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse registerDeviceApps(String url, String deviceID, String group, String cookie) 
	{
		return this.notif.registerDeviceApps(url, deviceID, group, cookie, "", "");
	}
	/**
	 * Register device to application server
	 * @param url URL of the application server
	 * @param deviceID Device ID
	 * @param cookie Cookie
	 * @param userID User ID
	 * @param password User password
	 * @param group User group
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse registerDeviceApps(String url, String deviceID, String group, String cookie, String userID, String password) 
	{
		return this.notif.registerDeviceApps(url, deviceID, group, cookie, userID, password);
	}
	/**
	 * Register device to application server
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @param userID User ID
	 * @param password User password
	 * @param group User group
	 * @param deviceID Device ID
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse unregisterDeviceApps(String url, String deviceID, String group, String cookie, String userID, String password) 
	{
		return this.notif.unregisterDeviceApps(url, deviceID, group, cookie, userID, password);
	}
	/**
	 * Check user authentication without user ID, password and group. The server will use session data saved according to cookie from the client.
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse auth(String url, String cookie) 
	{
		return this.notif.auth(url, cookie);
	}
	/**
	 * Check user authentication with user ID, password and group
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @param userID User ID
	 * @param password Password
	 * @param group Group
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse login(String url, String cookie, String userID, String password, String group) 
	{
		return this.notif.login(url, cookie, userID, password, group);
	}
	/**
	 * Check user authentication with user ID, password and group
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse logout(String url, String cookie) 
	{
		return this.notif.logout(url, cookie);
	}
}
