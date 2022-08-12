package com.example.planetbiru.notification;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * RemoteMessage
 * @author Kamshory, S.T,M.T.
 *
 */
public class RemoteMessage 
{
	/**
	 * Notification
	 */
	private Notification notification = new Notification();

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	/**
	 * Content type
	 */
	public String contentType = "";
	/**
	 * Raw message
	 */
	JSONObject rawMessage = new JSONObject();
	/**
	 * Default constructor
	 */
	public RemoteMessage()
	{
	}
	/**
	 * Constructor with content type
	 * @param messageType Message type
	 */
	public RemoteMessage(String messageType)
	{
		this.contentType = messageType;
	}
	/**
	 * Constructor with content type and data
	 * @param messageType Content type
	 * @param data JSONObject sent by server
	 * @throws JSONException if any JSON errors
	 */
	public RemoteMessage(String messageType, JSONObject data) throws JSONException
	{
		this.contentType = messageType;
		this.buildNotification(data);
	}
	/**
	 * Get notification
	 * @return Notification
	 */
	public Notification getNotification()
	{
		return this.notification;
	}
	/**
	 * Get content type
	 * @return Content type
	 */
	public String getContentType()
	{
		return this.contentType ;
	}
	/**
	 * Get message ID
	 * @return Message ID
	 */
	public long getMessageId()
	{
		return this.notification.id ;
	}
	/**
	 * Get raw message
	 * @return Raw message
	 */
	public JSONObject getMessageRaw()
	{
		return this.rawMessage;
	}
	/**
	 * Build notification
	 * @param data Notification
	 * @throws JSONException
	 * @throws URISyntaxException 
	 */
	private void buildNotification(JSONObject data) throws JSONException
	{
		this.rawMessage = new JSONObject(data.toString());
		this.notification = new Notification();
		this.notification.id = data.optLong("id", 0);
		this.notification.type = data.optString("type", "");
		this.notification.title = data.optString("title", "");
		this.notification.body = data.optString("message", "");
		this.notification.subtitle  = data.optString("subtitle", "");
		this.notification.smallIcon = data.optString("smallIcon", "");
		this.notification.largeIcon  = data.optString("largeIcon", "");
		this.notification.icon = data.optString("smallIcon", "");
		this.notification.sound = data.optString("sound", "");
		String vibrate = data.optString("vibrate", "");
		vibrate = vibrate.replaceAll("[^\\d]", " ");
		vibrate = vibrate.replaceAll("\\s+", " ").trim();
		String arr[] = vibrate.split(" ");
		for(int i = 0; i<arr.length; i++)
		{
			this.notification.vibrate.put(Integer.parseInt(arr[i]));
		}
		this.notification.color  = data.optString("color", "");
		this.notification.tickerText  = data.optString("tickerText", "");

		long timeLong = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String dateTimeTo = "";
		String dateTimeFrom = data.optString("timeGMT", "");
		if(dateTimeFrom.length() == 19)
		{
			dateTimeFrom += ".000";
		}
		if(dateTimeFrom.length() > 23)
		{
			dateTimeFrom = dateTimeFrom.substring(0, 23);
		}
		try
		{
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date date = format.parse(dateTimeFrom);
			timeLong = date.getTime();
		}
		catch (ParseException e)
		{
			timeLong = (new Date()).getTime();
		}
		this.notification.time  = timeLong;
		this.notification.timeZone  = data.optInt("timeZone", 0);
		this.notification.badge  = data.optString("badge", "");
		this.notification.clickAction   = data.optString("clickAction", "");
		this.notification.miscData    = data.optString("miscData", "");
		this.notification.channelID = data.optString("channelID", "CH");
		try 
		{
			this.notification.uri = new URI(data.optString("uri", ""));
		} 
		catch (URISyntaxException e) 
		{
			try 
			{
				this.notification.uri = new URI("");
			} 
			catch (URISyntaxException e1) 
			{
				if(Configuration.isDebugMode())
				{
					e1.printStackTrace();
				}
			}
		}
	}
	/**
	 * NotificationData
	 * @author Kamshory, S.T,M.T.
	 */
	public class Notification
	{
		/**
		 * Miscellaneous data. You can define yourself
		 */
		private String miscData = "";
		/**
		 * Notification type. It can be the category of the notification
		 */
		private String type = "";
		/**
		 * Creation time of the notification
		 */
		private long time = 0;
		/**
		 * Ticket text
		 */
		private String tickerText = "";
		/**
		 * Large icon. You can use local icon, remote icon, or encoded icon data. The format is according to your application
		 */
		private String largeIcon = "";
		/**
		 * Small icon. You can use local icon, remote icon, or encoded icon data. The format is according to your application
		 */
		private String smallIcon = "";
		/**
		 * Subtitle of the notification
		 */
		private String subtitle = "";
		/**
		 * Vibration of the notification. You can customize the vibration
		 */
		private JSONArray vibrate = new JSONArray();
		/**
		 * The body of the message
		 */
		private String body = "";
		/**
		 * Action doing when user click the notification
		 */
		private String clickAction = "";
		/**
		 * Color of the notification. You can customize the color
		 */
		private String color = "";
		/**
		 * Small icon. You can use local icon, remote icon, or encoded icon data. The format is according to your application
		 */
		private String icon = "";
		/**
		 * The related URI to the notification
		 */
		private URI uri;
		/**
		 * Sound of the notification. You can customize the sound
		 */
		private String sound = "";
		/**
		 * Tag of the notification
		 */
		private String tag = "";
		/**
		 * Title of the notification
		 */
		private String title = "";
		/**
		 * Message ID
		 */
		private long id = 0;
		/**
		 * Badge of the notification. You can customize the badge
		 */
		private String badge = "";
		/**
		 * Time zone offset of the server. All time on the message is in local. You can convert it in to local time according to the device time zone
		 */
		private int timeZone = 0;
		private String channelID = "";

		public String getMiscData() {
			return miscData;
		}

		public String getType() {
			return type;
		}

		public long getTime() {
			return time;
		}

		public String getTickerText() {
			return tickerText;
		}

		public String getLargeIcon() {
			return largeIcon;
		}

		public String getSmallIcon() {
			return smallIcon;
		}

		public String getSubtitle() {
			return subtitle;
		}

		public JSONArray getVibrate() {
			return vibrate;
		}

		public String getBody() {
			return body;
		}

		public String getClickAction() {
			return clickAction;
		}

		public String getColor() {
			return color;
		}

		public String getIcon() {
			return icon;
		}

		public URI getUri() {
			return uri;
		}

		public String getSound() {
			return sound;
		}

		public String getTag() {
			return tag;
		}

		public String getTitle() {
			return title;
		}

		public long getId() {
			return id;
		}

		public String getBadge() {
			return badge;
		}

		public int getTimeZone() {
			return timeZone;
		}

		public String getChannelID() {
			return channelID;
		}

		@Override
		public String toString()
		{
			Field[] fields = this.getClass().getDeclaredFields();
			int i, max = fields.length;
			String fieldName = "";
			String fieldType = "";
			int j = 0;
			JSONObject jo = new JSONObject();
			String res = "";
			try {
				for (i = 0; i < max; i++) {
					fieldName = fields[i].getName();
					fieldType = fields[i].getType().toString();
					if (fieldType.equals("String") || fieldType.equals("int") || fieldType.equals("long") || fieldType.equals("float") || fieldType.equals("double") || fieldType.equals("boolean")) {
						jo.put(fieldName, fields[i].get(this).toString());
					} else if (fieldType.contains("URI")) {
						jo.put(fieldName, fields[i].get(this).toString());
					}
				}
				res = jo.toString(4);
			} catch (JSONException | IllegalAccessException e) {
				if(Configuration.isDebugMode())
				{
					e.printStackTrace();
				}
			}
			return res;
		}
	}
}
