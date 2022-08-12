package com.example.planetbiru.notification;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.util.Map.Entry;

public class SocketIOSSL {
	private static final String TAG = "SocketIOSSL";
	/**
	 * Client socket
	 */
	private Socket socket;
	/**
	 * Header
	 */
	private String header = "";
	/**
	 * Body
	 */
	private String body = "";
	/**
	 * Content length
	 */
	private long contentLength = 0;
	/**
	 * Response header
	 */
	private Map<String, String> responseHeader = new HashMap<>();
	/**
	 * Request header
	 */
	private Map<String, String> requestHeader = new HashMap<>();
	/**
	 * Raw request header
	 */
	private String rawResponseHeader = "";
	/**
	 * Data input stream
	 */
	private DataInputStream in = null;
	/**
	 * Default constructor
	 */
	public SocketIOSSL()
	{
		
	}

	public Socket getSocket() {
		return socket;
	}

	public String getHeader() {
		return header;
	}

	public long getContentLength() {
		return contentLength;
	}

	public String getRawResponseHeader() {
		return rawResponseHeader;
	}

	/**
	 * Constructor with socket
	 * @param socket Socket
	 */
	public SocketIOSSL(Socket socket){
		this.socket = socket;
		try {
			this.in = new DataInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Reset response header
	 */
	public void resetResponseHeader()
	{
		this.responseHeader = new HashMap<>();
	}
	/**
	 * Add response header
	 * @param key Key
	 * @param value Value
	 */
	public void addResponseHeader(String key, String value)
	{
		this.responseHeader.put(key, value);
	}
	/**
	 * Get response header
	 * @return Map contains response header
	 */
	public Map<String, String> getResponseHeader()
	{
		return this.responseHeader;
	}
	/**
	 * Get response header value by key
	 * @param key Key
	 * @return Response header value
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public String getResponseHeader(String key)
	{
		return this.responseHeader.getOrDefault(key, "");
	}
	/**
	 * Get response header value by key. If header not exists, it will return default value
	 * @param key Key
	 * @param defaultValue Default value
	 * @return Response header value
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public String getResponseHeader(String key, String defaultValue)
	{
		return this.responseHeader.getOrDefault(key, defaultValue);
	}
	/**
	 * Get response header
	 * @return Array string contains request header
	 */
	public String[] getResponseHeaderArray()
	{
	    return this.rawResponseHeader.split("\\r\\n");
	}
	/**
	 * Reset request header
	 */
	public void resetRequestHeader()
	{
		this.requestHeader = new HashMap<String, String>();
	}
	/**
	 * Add request header
	 * @param key Key
	 * @param value Value
	 */
	public void addRequestHeader(String key, long value)
	{
		this.requestHeader.put(key, value+"");
	}
	/**
	 * Add request header
	 * @param key Key
	 * @param value Value
	 */
	public void addRequestHeader(String key, String value)
	{
		this.requestHeader.put(key, value);
	}
	/**
	 * Get request header
	 * @return Map contains request header
	 */
	public Map<String, String> getRequestHeader()
	{
		return this.requestHeader;
	}
	/**
	 * Get request header
	 * @return Array string contains request header
	 */
	public String[] getRequestHeaderArray()
	{
		int i = 0;
		String[] headers = new String[this.requestHeader.size()];
		for(Entry<String, String> pair: this.requestHeader.entrySet())
		{
	        headers[i] = pair.getKey() + ": " + pair.getValue();
	        i++;
		}
	    return headers;
	}
	/**
	 * Get request header value with key
	 * @param key Key
	 * @return Request header value
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public String getRequestHeader(String key)
	{
		return this.requestHeader.getOrDefault(key, "");
	}

	/**
	 * Get request header value with key. If header not exists, it will return default value
	 * @param key Key
	 * @param defaultValue Default value
	 * @return Request header value
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public String getRequestHeader(String key, String defaultValue)
	{
		return this.requestHeader.getOrDefault(key, defaultValue);
	}
	/**
	 * Read response header
	 * @return String contains response header
	 * @throws IOException -f any IO errors
	 */

	public String readResponseHeader() throws IOException, SocketException, RuntimeException
	{
		this.responseHeader = new HashMap<>();
		int buf;
		String data = "";
		if(this.socket.isClosed() || !this.socket.isConnected())
		{
			throw new SocketException("Socket closed");
		}
		do
		{
			buf = this.in.read();
			if(buf >= 0)
			{
				data += String.format("%c", buf);
			}
		}
		while(!data.contains("\r\n\r\n") && buf > -1);
		data = data.trim();
		this.rawResponseHeader = data;
		this.header = data;
		return data;
	}
	/*
	public String readResponseHeader() throws IOException, SocketException, RuntimeException
	{
		this.responseHeader = new HashMap<>();
		int buf;
		String data = "";
		if(this.socket.isClosed() || !this.socket.isConnected())
		{
			throw new SocketException("Socket closed");
		}
		InputStream iStream = this.socket.getInputStream();
		byte[] buffer = new byte[1];
		do
		{
			buf = iStream.read(buffer);
			if(buf >= 0)
			{
				data += String.format("%c", buffer[0]);
			}
		}
		while(!data.contains("\r\n\r\n") && buf > -1);
		data = data.trim();
		this.rawResponseHeader = data;
		this.header = data;
		return data;
	}
	*/
	/**
	 * Get first value of header
	 * @param headers Headers
	 * @param key Key
	 * @return The first value 
	 */
	public String getFirst(String[] headers, String key)
	{
		int i;
		String value = "";
		String line = "";
		String[] arr;
		String key2 = "";
		for(i = 0; i<headers.length; i++)
		{
			line = headers[i].trim();
			if(line.contains(":"))
			{
				arr = line.split("\\:", 2);
				key2 = arr[0].trim();
				if(key2.compareToIgnoreCase(key) == 0)
				{
					value = arr[1].trim();
				}
			}
		}
		return value;
	}
	/**
	 * Read request header
	 * @return Request header
	 * @throws IOException if any IO errors
	 */
	public Map<String, String> readRequestHeader() throws IOException
	{
		String data = this.readResponseHeader();
	    String[] lines = data.split("\\r\\n"); // split on new lines
	    Map<String, String> header = new HashMap<>();
	    int i;
	    String line = "";
	    String[] arr;
	    for(i = 0; i < lines.length; i++)
	    {
	    	line = lines[i].trim();
	    	if(line.contains(":"))
	    	{
	    		arr = line.split("\\:", 2);
	    		header.put(arr[0].trim(), arr[1].trim());
	    	}
	    }
	    this.requestHeader = header;
		return header;
	}
	/**
	 * Get request data length
	 * @return Request data length
	 */
	public long getRequestDataLength()
	{
		String header = this.rawResponseHeader;
		long length = 0;
		if(header.length() > 1)
		{
			String[] lines = header.split("\\r?\\n");
			int i;
			String line;
			String x;
			for(i = 0; i<lines.length; i++)
			{
				line = lines[i];
				if(line.toLowerCase().contains("content-length") && line.toLowerCase().contains(":"))
				{
					x = line.trim();
					x = x.toLowerCase();
					x = x.replace("content-length", "").replace(":", "").trim();
					length = Long.parseLong(x.replace("\r", "").replace("\n", "").trim());
				}
			}
			this.contentLength = length;
		}
		else
		{
			length = -1;
		}
		return length;
	}
	/**
	 * Read body
	 * @param length Length of data to be read
	 * @return Body
	 * @throws IOException if any IO errors
	 */
	private String getBody(long length) throws IOException, SocketException
	{
		int buf;
		long i;
		String data = "";
		for(i = 0; i < length; i++)
		{
			buf = this.in.read();
			data += String.format("%c", buf);
		}
		this.contentLength = length;
		return data;
	}
	/**
	 * Get headers read before
	 * @return Array string contains headers
	 */
	public String[] getHeaders()
	{
		String[] lines = this.header.split("\\r\\n");
		return lines;
	}
	/**
	 * Get body
	 * @return Body
	 */
	public String getBody()
	{
		return this.body;
	}
	/**
	 * Read data from socket
	 * @return Data read from the socket
	 * @throws IOException if any IO errors
	 */
	public boolean read() throws IOException, SocketException, RuntimeException
	{
		return this.read(this.socket);
	}
	/**
	 * Read data from socket
	 * @param socket Socket
	 * @return Data read from the socket
	 * @throws IOException if any IO errors
	 */
	public boolean read(Socket socket) throws IOException, SocketException
	{
		this.socket = socket;
		long length = 0;
		do {
			try {
				this.readResponseHeader();
				length = this.getRequestDataLength();
				if (length > 0) {
					this.body = this.getBody(length);
				}
			}
			catch(SocketException e)
			{
				if(Configuration.isDebugMode())
				{
					e.printStackTrace();
				}
				throw e;
			}
			if(length == -1)
			{
				try {
					Thread.sleep(Configuration.getNextRead());
				}
				catch(InterruptedException e)
				{
					if(Configuration.isDebugMode())
					{
						e.printStackTrace();
					}
				}
			}
		}
		while(length == -1);
		return true;
	}
	/**
	 * Write data to the socket
	 * @param data Data to be wrote
	 * @return true if success and false if failed
	 * @throws IOException if any IO errors
	 */
	public boolean write(String data) throws IOException
	{
		return this.write(this.socket, data);
	}
	/**
	 * Write data to the socket
	 * @param socket Socket
	 * @param data Data to be wrote
	 * @return true if success and false if failed
	 * @throws IOException if any IO errors
	 */
	public boolean write(Socket socket, String data) throws IOException
	{
		if(socket.isConnected() && !socket.isClosed())
		{
			String hdr = "";
			this.addRequestHeader("Content-Length", data.length());
			String data2sent = "";
			if(!this.requestHeader.isEmpty())
			{
				 Set<Map.Entry<String, String>> entrySet = this.requestHeader.entrySet();
				 for(Entry<String, String> entry : entrySet) 
				 {
				    hdr = entry.getKey().toString().trim()+": "+entry.getValue().toString().trim()+"\r\n";
				    data2sent += hdr;
				 }
			}
			data2sent += "\r\n";
			data2sent += data;
			socket.getOutputStream().write(data2sent.getBytes());
			return true;
		}
		else
		{
			return false;
		}
	}
}
