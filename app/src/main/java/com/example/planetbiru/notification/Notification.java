package com.example.planetbiru.notification;

import android.content.Context;
import android.os.Build;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.planetbiru.utility.Utility;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Notification class
 * @author Kamshory
 */
public class Notification implements NotificationInterface
{
	protected SocketIOSSL socketIO;
	protected static final String TAG = "Notification";
	protected Class<?> activityClass;
	protected Context context;
	/**
	 * Client socket
	 */
	protected Socket socket = new Socket();
	/**
	 * Client socket
	 */
	protected SSLSocket socketSSL = null;
	/**
	 * Indicate that process is stopped
	 */
	protected boolean stoped = false;
	/**
	 * Indicate that the client is connected to the server
	 */
	protected boolean connected = false;
	/**
	 * Indicate that the client is connected to the server
	 */
	protected boolean socketOK = false;
	/**
	 * Server port
	 */
	protected int serverPort = 9012;
	/**
	 * Server address
	 */
	protected String serverAddress = "localhost";
	/**
	 * Device ID
	 */
	protected String deviceID = "";
	/**
	 * API key
	 */
	protected String apiKey = "";
	/**
	 * Group key
	 */
	protected String groupKey = "";
	/**
	 * Password
	 */
	protected String password = "";
	/**
	 * Indicate that client run in debug mode
	 */
	protected boolean debugMode = false;
	/**
	 * Time out
	 */
	protected int timeout = Configuration.getTimeout();
	/**
	 * Count down to reconnect
	 */
	protected int countDownReconnect = Configuration.getCountDownReconnect();
	/**
	 * Delay to restart the client
	 */
	protected long delayRestart = Configuration.getDelayRestart();
	/**
	 * Delay to reconnect to the server
	 */
	protected long delayReconnect = Configuration.getDelayReconnect();
	/**
	 * Last error
	 */
	protected Exception lastError = new Exception();
	/**
	 * Connection checker
	 */
	protected ConnectionChecker connectionChecker = new ConnectionChecker();
	/**
	 * Flag to force stop
	 */
	protected boolean forceStop = false;
	/**
	 * Use SSL connection or not
	 */
	protected boolean ssl = false;

	/**
	 * Default constructor
	 */
	public Notification()
	{
	}
	/**
	 * Constructor with initialization
	 * @param apiKey API key of the push notification
	 * @param password API password of the push notification
	 * @param deviceID Device ID
	 * @param groupKey Group key of the push notification
	 */
	public Notification(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey)
	{
		this.context = context;
		this.activityClass = activityClass;
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Configuration.setApiKey(apiKey);
		Configuration.setPassword(password);
		Configuration.setGroupKey(groupKey);
	}
	public Notification(Context context, String apiKey, String password, String deviceID, String groupKey)
	{
		this.context = context;
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Configuration.setApiKey(apiKey);
		Configuration.setPassword(password);
		Configuration.setGroupKey(groupKey);
	}
	/**
	 * Constructor with initialization
	 * @param apiKey API Key
	 * @param password Password
	 * @param deviceID Device ID
	 * @param groupKey Group Key
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 */
	public Notification(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		this.context = context;
		this.activityClass = activityClass;
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Configuration.setApiKey(apiKey);
		Configuration.setPassword(password);
		Configuration.setGroupKey(groupKey);
		this.setSsl(false);
		this.setServerAddress(serverAddress);
		this.setServerPort(serverPort);
	}
	public Notification(Context context, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort)
	{
		this.context = context;
		this.activityClass = activityClass;
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Configuration.setApiKey(apiKey);
		Configuration.setPassword(password);
		Configuration.setGroupKey(groupKey);
		this.setSsl(false);
		this.setServerAddress(serverAddress);
		this.setServerPort(serverPort);
	}
	/**
	 * Constructor with initialization
	 * @param apiKey API Key
	 * @param password Password
	 * @param deviceID Device ID
	 * @param groupKey Group Key
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param ssl SSL connection
	 */
	public Notification(Context context, Class<?> activityClass, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl)
	{
		this.context = context;
		this.activityClass = activityClass;
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Configuration.setApiKey(apiKey);
		Configuration.setPassword(password);
		Configuration.setGroupKey(groupKey);
		this.setSsl(ssl);
		this.setServerAddress(serverAddress);
		this.setServerPort(serverPort);
	}
	public Notification(Context context, String apiKey, String password, String deviceID, String groupKey, String serverAddress, int serverPort, boolean ssl)
	{
		this.context = context;
		this.activityClass = activityClass;
		this.apiKey = apiKey;
		this.deviceID = deviceID;
		this.password = password;
		this.groupKey = groupKey;
		Configuration.setApiKey(apiKey);
		Configuration.setPassword(password);
		Configuration.setGroupKey(groupKey);
		this.setSsl(ssl);
		this.setServerAddress(serverAddress);
		this.setServerPort(serverPort);
	}
	public <T> Object getSocket()
	{
		if(this.isSsl())
		{
			return this.socketSSL;
		}
		else
		{
			return this.socket;
		}
	}

	/**
	 * Start notification client
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public void start() {
		this.forceStop = false;
		while(!this.forceStop)
		{
			this.stoped = false;
			if(!this.connected)
			{
				this.connected = this.connect();
			}
			if(this.connected)
			{
				if(this.isSsl())
				{
					socketIO = new SocketIOSSL((SSLSocket) this.getSocket());
				}
				else
				{
					socketIO = new SocketIOSSL((Socket) this.getSocket());
				}
				boolean success = false;
				while(!this.stoped && this.socketOK && this.connected)
				{
					try
					{
						success = this.socketIO.read();
						if(success)
						{
							this.processPush(socketIO.getResponseHeaderArray(), this.socketIO.getBody());
						}
					}
					catch (NotificationException e)
					{
						this.stoped = true;
						this.connected = false;
						this.socketOK = false;
						this.sendError(e);
						if(this.debugMode)
						{
							e.printStackTrace();
						}
					}
					catch (NegativeArraySizeException e)
					{
						this.stoped = true;
						this.connected = false;
						this.socketOK = false;
						this.sendError(e);
						if(Configuration.isDebugMode())
						{
							e.printStackTrace();
						}
					}
					catch (NullPointerException e)
					{
						this.stoped = true;
						this.connected = false;
						this.socketOK = false;
						this.sendError(e);
						if(Configuration.isDebugMode())
						{
							e.printStackTrace();
						}
					}
					catch(SocketException e)
					{
						this.stoped = true;
						this.connected = false;
						this.socketOK = false;
						this.sendError(e);
						if(this.debugMode)
						{
							e.printStackTrace();
						}
						this.onDisconnected();
					}
					catch(IOException e)
					{
						this.stoped = true;
						this.connected = false;
						this.socketOK = false;
						this.sendError(e);
						if(this.debugMode)
						{
							e.printStackTrace();
						}
						this.onDisconnected();
					}
					if(this.socketIO.getSocket().isClosed() || !this.socketIO.getSocket().isConnected())
					{
						this.stoped = true;
						this.connected = false;
						this.socketOK = false;
						this.onDisconnected();
					}
				}
			}
			if(!this.forceStop)
			{
				try
				{
					Thread.sleep(Configuration.getDelayRestart());
				}
				catch(InterruptedException e)
				{
					this.sendError(e);
					if(this.debugMode)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Stop notification client
	 */
	public void stop()
	{
		this.forceStop = true;
		this.stoped = true;
		this.connected = false;
	}
	public void stopAndClose()
	{
		this.forceStop = true;
		this.stoped = true;
		this.connected = false;
		if(this.isSsl())
		{
			try
			{
				this.socketSSL.close();
			}
			catch (IOException e)
			{
				this.sendError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			try
			{
				this.socket.close();
			}
			catch (IOException e)
			{
				this.sendError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
			}
		}
		this.onDisconnected();
	}
	/**
	 * Create socket
	 * @return true if success and false if failed
	 * @throws IOException
	 * @throws SocketException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NetworkOnMainThreadException
	 */
	private boolean createSocket() throws IOException, SocketException, IllegalArgumentException, SecurityException, NetworkOnMainThreadException
	{
		if(this.ssl)
		{
			SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			this.socketSSL = (SSLSocket)factory.createSocket(this.serverAddress, this.serverPort);
			if(this.socketSSL.isConnected() && !this.socketSSL.isClosed())
			{
				this.socketSSL.setKeepAlive(true);
				this.connected = true;
				return true;
			}
			else
			{
				this.connected = false;
			}
		}
		else
		{
			SocketAddress socketAddress = new InetSocketAddress(this.serverAddress, this.serverPort);
			this.socket = new Socket();
			this.socket.connect(socketAddress, this.timeout);
			if(this.socket.isConnected() && !this.socket.isClosed())
			{
				this.socket.setKeepAlive(true);
				this.connected = true;
				return true;
			}
			else
			{
				this.connected = false;
			}
		}
		return false;
	}
	/**
	 * Reconnect client
	 * @param countDown Count down
	 * @return true if success and false if failed
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private boolean reconnect(int countDown){
		try
		{
			Thread.sleep(this.getDelayReconnect());
		}
		catch (InterruptedException e)
		{
			this.sendError(e);
		}
		if(countDown > 0)
		{
			if(this.connect())
			{
				return true;
			}
			else
			{
				return this.reconnect(countDown--);
			}
		}
		else
		{
			return false;
		}
	}
	/**
	 * Connect to notification server
	 * @param apiKey API key
	 * @param password API password
	 * @param groupKey API group key
	 * @return true if success and false if failed
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public boolean connect(String apiKey, String password, String groupKey) throws IOException {
		this.apiKey = apiKey;
		this.password = password;
		this.groupKey = groupKey;
		return this.connect();
	}
	/**
	 * Connect to notification server
	 * @return true if success and false if failed
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public boolean connect(){
		if(this.connected)
		{
			return true;
		}
		if(this.apiKey.equals(""))
		{
			return false;
		}
		else
		{

			try
			{
				this.socketOK  = this.createSocket();
			}
			catch (IllegalArgumentException e)
			{
				if(Configuration.isDebugMode())
				{
					e.printStackTrace();
				}
			}
			catch (SecurityException e)
			{
				if(Configuration.isDebugMode())
				{
					e.printStackTrace();
				}
			}
			catch(SocketException e)
			{
				if(Configuration.isDebugMode())
				{
					e.printStackTrace();
				}
			}
			catch (IOException e)
			{
				if(Configuration.isDebugMode())
				{
					e.printStackTrace();
				}
			}
			catch (NetworkOnMainThreadException e)
			{
				if(Configuration.isDebugMode())
				{
					e.printStackTrace();
				}
			}

			if(this.socketOK)
			{
				String command = "singin";
				long unixTime = Utility.unixTime();
				String token = "";
				try
				{
					token = Utility.sha1(unixTime+this.apiKey);
				}
				catch (NoSuchAlgorithmException e2)
				{
					if(Configuration.isDebugMode())
					{
						e2.printStackTrace();
					}
				}
				String hash =  "";
				try
				{
					hash = Utility.sha1(Utility.sha1(this.password)+"-"+token+"-"+this.apiKey);
				}
				catch (NoSuchAlgorithmException e2)
				{
					if(Configuration.isDebugMode())
					{
						e2.printStackTrace();
					}
				}
				String groupKey;
				try
				{
					groupKey = Utility.urlEncode(this.groupKey);
				}
				catch (UnsupportedEncodingException e1)
				{
					groupKey = this.groupKey;
					this.sendError(e1);
					if(Configuration.isDebugMode())
					{
						e1.printStackTrace();
					}
				}

				SocketIOSSL socketIO;
				if(this.isSsl())
				{
					socketIO = new SocketIOSSL((SSLSocket) this.getSocket());
				}
				else
				{
					socketIO = new SocketIOSSL((Socket) this.getSocket());
				}
				String data = "";
				socketIO.resetRequestHeader();
				socketIO.addRequestHeader("Command", command);
				socketIO.addRequestHeader("Authorization", "Bearer key="+this.apiKey+"&token="+token+"&hash="+hash+"&time="+unixTime+"&group="+groupKey);
				try
				{
					data = this.createRequest();
					String[] headers = socketIO.getRequestHeaderArray();
					this.onDataSent(headers, command, data);
					this.connected = socketIO.write(data);

					if(this.connected)
					{
						this.onConnected();
					}
				}
				catch (IOException e)
				{
					this.sendError(e);
					if(this.debugMode)
					{
						e.printStackTrace();
					}
					return this.reconnect(this.countDownReconnect);
				}
			}
			else
			{
				return this.reconnect(this.countDownReconnect);
			}
			return this.socketOK;
		}
	}
	/**
	 * Disconnect from notification server
	 * @return true if success and false if failed
	 */
	public boolean disconnect()
	{
		if(this.isSsl())
		{
			if(this.socketSSL != null) {
				try {
					this.socketSSL.close();
					this.connected = false;
				} catch (IOException e) {
					this.sendError(e);
				}
			}
		}
		else
		{
			if(this.socket != null) {
				try {
					this.socket.close();
					this.connected = false;
				} catch (IOException e) {
					this.sendError(e);
				}
			}
		}
		this.onDisconnected();
		return true;
	}
	/**
	 * Process incoming data
	 * @param headers Headers
	 * @param body Body
	 * @throws NotificationException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws IOException
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	private void processPush(String[] headers, String body) throws NotificationException, NegativeArraySizeException, NullPointerException, IOException
	{
		String command = Utility.getFirst(headers, "Command");
		String secure = Utility.getFirst(headers, "Content-Secure");
		this.onDataReceived(headers, command, body);
		String messageType = Utility.getFirst(headers, "Content-Type");
		String commandLower = command.toLowerCase().trim();
		if(commandLower.equals("notification"))
		{
			RemoteMessage remoteMessage = new RemoteMessage();
			JSONArray data;
			try
			{
				data = new JSONArray(body);
				int i;
				int j = data.length();
				for(i = 0; i < j; i++)
				{
					remoteMessage = new RemoteMessage(messageType, data.getJSONObject(i));
					this.onNotificationReceived(remoteMessage);
				}
			}
			catch(JSONException e)
			{
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONArray. \r\n==========\r\n"+body+"\r\n==========");
			}
		}
		else if(commandLower.equals("delete-notification"))
		{
			JSONArray data;
			try
			{
				JSONObject jo;
				data = new JSONArray(body);
				int i;
				int j = data.length();
				String messageID = "";
				for(i = 0; i < j; i++)
				{
					jo = data.getJSONObject(i);
					messageID = jo.optString("id", "");
					this.onNotificationDeleted(messageID);
				}
			}
			catch(JSONException e)
			{
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONArray");
			}
		}

		else if(commandLower.equals("unregister-device-success"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				int responseCode = jo.optInt("responseCode", 0);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				this.onUnregisterDeviceSuccess(deviceID, responseCode, message);
			}
			catch(JSONException e)
			{
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONArray");
			}
		}
		else if(commandLower.equals("register-device-success"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				int responseCode = jo.optInt("responseCode", 0);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				this.onRegisterDeviceSuccess(deviceID, responseCode, message);
			}
			catch(JSONException e)
			{
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONArray");
			}
		}
		else if(commandLower.equals("register-device-error"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				int responseCode = jo.optInt("responseCode", 0);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				String cause = jo.optString("cause", "");
				this.onRegisterDeviceError(deviceID, responseCode, message, cause);
			}
			catch(JSONException e)
			{
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONArray");
			}
		}
		else if(commandLower.equals("unregister-device-error"))
		{
			JSONObject jo;
			try
			{
				jo = new JSONObject(body);
				int responseCode = jo.optInt("responseCode", 0);
				String message = jo.optString("message", "");
				String deviceID = jo.optString("deviceID", "");
				String cause = jo.optString("cause", "");
				this.onUnregisterDeviceError(deviceID, responseCode, message, cause);
			}
			catch(JSONException e)
			{
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONArray");
			}
		}
		else if(commandLower.equals("setting"))
		{
			JSONArray data;
			JSONObject setting = new JSONObject();
			String name = "";
			String type = "";
			String valueString = "";
			Object value = new Object();
			try
			{
				data = new JSONArray(body);
				int i;
				int j = data.length();
				for(i = 0; i < j; i++)
				{
					setting = data.getJSONObject(i);
					name = setting.optString("name", "").trim();
					type = setting.optString("type", "").trim();
					valueString = setting.optString("value", "").trim();
					if(type.equals("int"))
					{
						if(valueString.equals(""))
						{
							valueString = "0";
						}
						value = Integer.parseInt(valueString);
					}
					else if(type.equals("long"))
					{
						if(valueString.equals(""))
						{
							valueString = "0";
						}
						value = Long.parseLong(valueString);
					}
					else if(type.equals("float"))
					{
						if(valueString.equals(""))
						{
							valueString = "0";
						}
						value = Float.parseFloat(valueString);
					}
					else if(type.equals("double"))
					{
						if(valueString.equals(""))
						{
							valueString = "0";
						}
						value = Double.parseDouble(valueString);
					}
					else
					{
						value = valueString;
					}
					this.applySetting(name, type, value);
					this.onChangeSetting(name, type, value);
				}
			}
			catch(JSONException e)
			{
				this.sendError(e);
				throw new NotificationException("Data must be a valid JSONArray");
			}
		}
	}
	/**
	 * Apply new setting sent from the server
	 * @param name Setting name
	 * @param type Data type
	 * @param value Setting value
	 */
	private void applySetting(String name, String type, Object value)
	{
		if(name.equals("delayRestart"))
		{
			this.delayRestart = (long) value;
		}
		else if(name.equals("delayReconnect"))
		{
			this.delayReconnect = (long) value;
		}
		else if(name.equals("timeout"))
		{
			this.timeout = (int) value;
		}
	}

	/**
	 * Create request
	 * @return Request to the notification server
	 */
	private String createRequest()
	{
		JSONObject jo = new JSONObject();
		String result = "";
		try
		{
			jo.put("deviceID", this.deviceID );
			jo.put("apiKey", this.apiKey);
		}
		catch (JSONException e)
		{
			this.sendError(e);
		}
		result = jo.toString();
		return result;
	}
	/**
	 * Register device
	 * @param deviceID Device ID
	 * @return true if success and false if failed
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public boolean registerDevice(String deviceID)
	{
		if(!this.socketOK || !this.connected)
		{
			this.connect();
		}
		if(this.socketOK)
		{
			String command = "register-device";
			SocketIOSSL socketIO = new SocketIOSSL();
			if(this.isSsl())
			{
				socketIO = new SocketIOSSL((SSLSocket) this.getSocket());
			}
			else
			{
				socketIO = new SocketIOSSL((Socket) this.getSocket());
			}
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", command);
			socketIO.addRequestHeader("Authorization", "Bearer key="+this.apiKey);
			boolean success = false;
			try
			{
				JSONObject data = new JSONObject();
				data.put("deviceID", deviceID);
				success = socketIO.write(data.toString());
				String[] headers = socketIO.getRequestHeaderArray();
				this.onDataSent(headers, command, data.toString());
				if(success)
				{
					this.onRegisterDeviceSendSuccess(deviceID, "Data sent to notification server");
					return true;
				}
				else
				{
					this.onRegisterDeviceSendError(deviceID, "Failed", "Data not sent to notification server");
					return false;
				}
			}
			catch (IOException | JSONException e)
			{
				this.onRegisterDeviceSendError(deviceID, "Failed", e.getMessage());
				this.sendError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
				try
				{
					this.socketIO.getSocket().close();
				}
				catch (IOException e1)
				{
					this.sendError(e1);
					if(this.debugMode)
					{
						e1.printStackTrace();
					}
				}
				return false;
			}
		}
		else
		{
			this.onRegisterDeviceSendError(deviceID, "Failed", "Not connected");
			return false;
		}
	}
	/**
	 * Unregister device
	 * @param deviceID Device ID
	 * @return true if success and false if failed
	 * @throws IOException if any IO errors
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public boolean unregisterDevice(String deviceID)
	{
		if(!this.socketOK || !this.connected)
		{
			this.connect();
		}
		if(this.socketOK)
		{
			String command = "unregister-device";
			SocketIOSSL socketIO = new SocketIOSSL();
			if(this.isSsl())
			{
				socketIO = new SocketIOSSL((SSLSocket) this.getSocket());
			}
			else
			{
				socketIO = new SocketIOSSL((Socket) this.getSocket());
			}
			socketIO.resetRequestHeader();
			socketIO.addRequestHeader("Command", command);
			socketIO.addRequestHeader("Authorization", "Bearer key="+this.apiKey);
			boolean success = false;
			try
			{
				JSONObject data = new JSONObject();
				data.put("deviceID", deviceID);
				String[] headers = socketIO.getRequestHeaderArray();
				this.onDataSent(headers, command, data.toString());
				success = socketIO.write(data.toString());
				if(success)
				{
					this.onUnregisterDeviceSendSuccess(deviceID, "Data sent to notification server");
					return true;
				}
				else
				{
					this.onUnregisterDeviceSendError(deviceID, "Failed", "Data not sent to notification server");
					return false;
				}
			}
			catch (IOException | JSONException e)
			{
				this.onUnregisterDeviceSendError(deviceID, "Failed", e.getMessage());
				this.sendError(e);
				if(this.debugMode)
				{
					e.printStackTrace();
				}
				try
				{
					this.socketIO.getSocket().close();
				}
				catch (IOException e1)
				{
					this.sendError(e1);
					if(this.debugMode)
					{
						e1.printStackTrace();
					}
				}
				return false;
			}
		}
		else
		{
			this.onUnregisterDeviceSendError(deviceID, "Failed", "Not connected");
			return false;
		}
	}
	/**
	 * Register device ID and user ID to application server
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @param deviceID Device ID
	 * @param userID User ID
	 * @param password User password
	 * @param group User group
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse registerDeviceApps(String url, String deviceID, String group, String cookie, String userID, String password)
	{
		String postData = "";
		try
		{
			deviceID = Utility.urlEncode(deviceID);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		try
		{
			userID = Utility.urlEncode(userID);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		try
		{
			password = Utility.urlEncode(password);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		try
		{
			group = Utility.urlEncode(group);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		postData += "device_id="+deviceID+"&";
		if(userID.length() > 0)
		{
			postData += "user_id="+userID+"&";
		}
		if(password.length() > 0)
		{
			postData += "password="+password+"&";
		}
		if(group.length() > 0)
		{
			postData += "group="+group+"&";
		}
		postData += "action=register";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try
		{
			response = httpClient.post(url, postData, cookie);
		}
		catch (IOException e)
		{
			this.sendError(e);
		}
		return response;
	}
	/**
	 * Unregister device ID and user ID to application server
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @param deviceID Device ID
	 * @param userID User ID
	 * @param password User password
	 * @param group User group
	 * @return HTTPResponse contains server response
	 */
	public HTTPResponse unregisterDeviceApps(String url, String deviceID, String group, String cookie, String userID, String password)
	{
		String postData = "";
		try
		{
			deviceID = Utility.urlEncode(deviceID);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		try
		{
			userID = Utility.urlEncode(userID);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		try
		{
			password = Utility.urlEncode(password);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		try
		{
			group = Utility.urlEncode(group);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		postData += "device_id="+deviceID+"&";
		postData += "user_id="+userID+"&";
		postData += "password="+password+"&";
		postData += "group="+group+"&";
		postData += "action=unregister";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try
		{
			response = httpClient.post(url, postData, cookie);
		}
		catch (IOException e)
		{
			this.sendError(e);
		}
		return response;
	}
	/**
	 * Check user authentication without user ID, password and group. The server will use session data saved according to cookie from the client.
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse auth(String url, String cookie)
	{
		String postData = "";
		postData += "action=login";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try
		{
			response = httpClient.post(url, postData, cookie);
		}
		catch (IOException e)
		{
			this.sendError(e);
		}
		return response;
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
		String postData = "";
		try
		{
			userID = Utility.urlEncode(userID);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		try
		{
			password = Utility.urlEncode(password);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}
		try
		{
			group = Utility.urlEncode(group);
		}
		catch (UnsupportedEncodingException e)
		{
			this.sendError(e);
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
		}

		postData += "user_id="+userID+"&";
		postData += "password="+password+"&";
		postData += "group="+group+"&";
		postData += "action=login";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try
		{
			response = httpClient.post(url, postData, cookie);
		}
		catch (IOException e)
		{
			this.sendError(e);
		}
		return response;
	}
	/**
	 * Logout by destroying session data on the server
	 * @param url URL of the application server
	 * @param cookie Cookie
	 * @return HTTPResponse contains header, body and cookie
	 */
	public HTTPResponse logout(String url, String cookie)
	{
		String postData = "";
		postData += "action=logout";
		HTTPResponse response = new HTTPResponse();
		HTTPClient httpClient = new HTTPClient();
		try
		{
			response = httpClient.post(url, postData, cookie);
		}
		catch (IOException e)
		{
			this.sendError(e);
		}
		return response;
	}
	/**
	 * Get last error
	 * @return Last exception sent via sendError
	 */
	public Exception getLastError()
	{
		return this.lastError;
	}
	/**
	 * Send error
	 * @param exception Exception
	 */
	private void sendError(Exception exception)
	{
		this.lastError = exception;
		this.onError(exception);
	}
	/**
	 * Get server address
	 * @return Server address
	 */
	public String getServerAddress()
	{
		return this.serverAddress;
	}
	/*
	 * Set server address
	 */
	public void setServerAddress(String serverAddress)
	{
		this.serverAddress = serverAddress;
	}
	/**
	 * Get server port
	 * @return Server port
	 */
	public int getServerPort()
	{
		return this.serverPort;
	}
	/**
	 * Set server port
	 * @param serverPort Server port
	 */
	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}
	/**
	 * Get delay to restart the client
	 * @return Delay to restart the client
	 */
	public long getDelayRestart()
	{
		return this.delayRestart;
	}
	/**
	 * Set delay to restart the client
	 * @param delayRestart Delay to restart the client
	 */
	public void setDelayRestart(int delayRestart)
	{
		this.delayRestart = delayRestart;
	}
	/**
	 * Get delay to reconnect to the server
	 * @return Delay to reconnect to the server
	 */
	public long getDelayReconnect()
	{
		return this.delayReconnect;
	}
	/**
	 * Set delay to reconnect to the server
	 * @param delayReconnect Delay to reconnect to the server
	 */
	public void setDelayReconnect(int delayReconnect)
	{
		this.delayReconnect = delayReconnect;
	}
	/**
	 * Get time out
	 * @return Time out
	 */
	public int getTimeout()
	{
		return timeout;
	}
	/**
	 * Set time out
	 * @param timeout Time out
	 */
	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}
	/**
	 * Set API to run on debug mode
	 * @param debugMode Debug mode
	 */
	public void setDebugMode(boolean debugMode)
	{
		this.debugMode = debugMode;
	}
	public void onNotificationReceived(RemoteMessage notification) throws JSONException {

	}
	public void onNotificationDeleted(String notificationID)
	{

	}
	public void onMessageReceived(RemoteMessage message)
	{

	}
	public void onMessageDeleted(String messageID)
	{

	}
	public void onDataReceived(String[] headers, String command, String body)
	{

	}
	public void onDataSent(String[] headers, String command, String body)
	{

	}
	public void onNewToken(String token, String time, long waitToNext, int timeZone)
	{

	}
	public void onChangeSetting(String name, String type, Object value)
	{

	}
	public void onError(Exception exception)
	{

	}
	public void onRegisterDeviceSendSuccess(String deviceID, String message)
	{

	}
	public void onRegisterDeviceSendError(String deviceID, String message, String cause)
	{

	}
	public void onUnregisterDeviceSendSuccess(String deviceID, String message)
	{

	}
	public void onUnregisterDeviceSendError(String deviceID, String message, String cause)
	{

	}
	public void onRegisterDeviceSuccess(String deviceID, int responseCode, String message)
	{

	}
	public void onRegisterDeviceError(String deviceID, int responseCode, String message, String cause)
	{

	}
	public void onUnregisterDeviceSuccess(String deviceID, int responseCode, String message)
	{

	}
	public void onUnregisterDeviceError(String deviceID, int responseCode, String message, String cause)
	{

	}

	@Override
	public void onConnected() {

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onStopped() {

	}

	@Override
	public void heartbeat() {

	}

	public boolean isSsl() {
		return ssl;
	}
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isStoped() {
		return stoped;
	}

	public boolean isForceStop() {
		return forceStop;
	}

	public void callHeartbeat() {
		this.heartbeat();
	}
}
