package com.example.planetbiru.notification;


import com.example.planetbiru.utility.Utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class HTTPClient
{
	/**
	 * Send GET request to HTTP server and return the response 
	 * @param location URL
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse get(String location) throws IOException 
	{
		return this.get(location, "");
	}
	/**
	 * Send GET request to HTTP server and return the response 
	 * @param location URL
	 * @param cookie Cookie from the previous request
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse get(String location, String cookie) throws IOException 
	{
		URL url = null;
		try 
		{
			url = new URL(location);				
		} 
		catch (MalformedURLException e) 
		{
		     e.printStackTrace();
	    } 
		return this.get(url, cookie);
	}
	/**
	 * Send GET request to HTTP server and return the response 
	 * @param url URL
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse get(URL url) throws IOException 
	{
		return this.get(url, "");
	}
	/**
	 * Send GET request to HTTP server and return the response 
	 * @param url URL
	 * @param cookie Cookie from the previous request
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse get(URL url, String cookie) throws IOException
	{
		HTTPResponse httpResponse = new HTTPResponse();
		if(url.getProtocol().toLowerCase().trim().equals("https"))
		{
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
			httpsURLConnection.setRequestMethod("GET");
			httpsURLConnection.setRequestProperty("User-Agent", Configuration.getUserAgent());
			httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if(!cookie.equals(""))
			{
				httpsURLConnection.setRequestProperty("Cookie", cookie);
			}
			httpsURLConnection.setDoOutput(true);
			httpResponse.responseCode = httpsURLConnection.getResponseCode();
			httpResponse.cipherSuite = httpsURLConnection.getCipherSuite();
			httpResponse.serverCertificates = httpsURLConnection.getServerCertificates();
			Map<String, List<String>> map = httpsURLConnection.getHeaderFields();
			String headers = "";
			String key = "";
			for (Map.Entry<String, List<String>> entry : map.entrySet()) 
			{
				key = entry.getKey();
				if(key == null)
				{
					int i;
					for(i = 0; i < entry.getValue().size(); i++)
					{
						if(i > 0)
						{
							headers += "\r\n";
						}
						headers += entry.getValue().get(i).toString();
					}
				}
			}	
			for (Map.Entry<String, List<String>> entry : map.entrySet()) 
			{
				key = entry.getKey();
				if(key != null)
				{
					int i;
					for(i = 0; i < entry.getValue().size(); i++)
					{
						headers += "\r\n"+entry.getKey()+": "+entry.getValue().get(i).toString();
					}
				}
			}	
			BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();	
			while ((inputLine = in.readLine()) != null) 
			{
				response.append(inputLine+"\r\n");
			}
			in.close();
			String body = response.toString();
			httpResponse.header = headers;
			httpResponse.cookie = HTTPClient.extractCookie(headers);
			httpResponse.body = body;
		}
		else
		{
			httpResponse = this.httpGet(url, cookie);
		}
		return httpResponse;
	}
	/**
	 * Send HEAD request to HTTP server and return the response 
	 * @param location URL
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse head(String location) throws IOException 
	{
		return this.get(location, "");
	}
	/**
	 * Send HEAD request to HTTP server and return the response 
	 * @param location URL
	 * @param cookie Cookie from the previous request
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse head(String location, String cookie) throws IOException 
	{
		URL url = null;
		try 
		{
			url = new URL(location);				
		} 
		catch (MalformedURLException e) 
		{
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
	    } 
		return this.head(url, cookie);
	}
	/**
	 * Send HEAD request to HTTP server and return the response 
	 * @param url URL
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse head(URL url) throws IOException 
	{
		return this.head(url, "");
	}
	/**
	 * Send HEAD request to HTTP server and return the response 
	 * @param url URL
	 * @param cookie Cookie from the previous request
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse head(URL url, String cookie) throws IOException
	{
		HTTPResponse httpResponse = new HTTPResponse();
		if(url.getProtocol().toLowerCase().trim().equals("https"))
		{
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
			httpsURLConnection.setRequestMethod("HEAD");
			httpsURLConnection.setRequestProperty("User-Agent", Configuration.getUserAgent());
			httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if(!cookie.equals(""))
			{
				httpsURLConnection.setRequestProperty("Cookie", cookie);
			}
			httpsURLConnection.setDoOutput(true);
			httpResponse.responseCode = httpsURLConnection.getResponseCode();
			httpResponse.cipherSuite = httpsURLConnection.getCipherSuite();
			httpResponse.serverCertificates = httpsURLConnection.getServerCertificates();
			Map<String, List<String>> map = httpsURLConnection.getHeaderFields();
			String headers = "";
			String key = "";
			for (Map.Entry<String, List<String>> entry : map.entrySet()) 
			{
				key = entry.getKey();
				if(key == null)
				{
					int i;
					for(i = 0; i < entry.getValue().size(); i++)
					{
						if(i > 0)
						{
							headers += "\r\n";
						}
						headers += entry.getValue().get(i).toString();
					}
				}
			}	
			for (Map.Entry<String, List<String>> entry : map.entrySet()) 
			{
				key = entry.getKey();
				if(key != null)
				{
					int i;
					for(i = 0; i < entry.getValue().size(); i++)
					{
						headers += "\r\n"+entry.getKey()+": "+entry.getValue().get(i).toString();
					}
				}
			}	
			httpResponse.header = headers;
			httpResponse.cookie = HTTPClient.extractCookie(headers);
			httpResponse.body = "";
		}
		else
		{
			httpResponse = this.httpHead(url, cookie);
		}
		return httpResponse;
	}
	/**
	 * Send POST request to HTTP server and return the response 
	 * @param location URL
	 * @param data Post data
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse post(String location, String data) throws IOException
	{
		URL url = null;
		try 
		{
			url = new URL(location);				
		} 
		catch (MalformedURLException e) 
		{
		     e.printStackTrace();
	    } 
		return this.post(url, data, "");
	}
	/**
	 * Send POST request to HTTP server and return the response 
	 * @param location URL
	 * @param data Post data
	 * @param cookie Cookie from the previous request
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse post(String location, String data, String cookie) throws IOException
	{
		URL url = null;
		try 
		{
			url = new URL(location);				
		} 
		catch (MalformedURLException e) 
		{
			if(Configuration.isDebugMode())
			{
				e.printStackTrace();
			}
	    } 
		return this.post(url, data, cookie);
	}
	/**
	 * Send POST request to HTTP server and return the response 
	 * @param url URL
	 * @param data Post data
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse post(URL url, String data) throws IOException
	{
		return this.post(url, data, "");
	}
	/**
	 * Send POST request to HTTP server and return the response 
	 * @param url URL
	 * @param data Post data
	 * @param cookie Cookie from the previous request
	 * @return HTTPResponse
	 * @throws IOException if any IO errors
	 */
	public HTTPResponse post(URL url, String data, String cookie) throws IOException
	{
		HTTPResponse httpResponse = new HTTPResponse();
		if(url.getProtocol().toLowerCase().trim().equals("https"))
		{
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
			httpsURLConnection.setRequestMethod("POST");
			httpsURLConnection.setRequestProperty("User-Agent", "Planetbiru");
			httpsURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			httpsURLConnection.setRequestProperty("User-Agent", Configuration.getUserAgent());
			httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if(!cookie.equals(""))
			{
				httpsURLConnection.setRequestProperty("Cookie", cookie);
			}
			httpsURLConnection.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(httpsURLConnection.getOutputStream());
			wr.writeBytes(data);
			wr.flush();
			wr.close();
			httpResponse.responseCode = httpsURLConnection.getResponseCode();		
			httpResponse.cipherSuite = httpsURLConnection.getCipherSuite();
			httpResponse.serverCertificates = httpsURLConnection.getServerCertificates();
			Map<String, List<String>> map = httpsURLConnection.getHeaderFields();
			String headers = "";
			String key = "";
			for (Map.Entry<String, List<String>> entry : map.entrySet()) 
			{
				key = entry.getKey();
				if(key == null)
				{
					int i;
					for(i = 0; i < entry.getValue().size(); i++)
					{
						if(i > 0)
						{
							headers += "\r\n";
						}
						headers += entry.getValue().get(i).toString();
					}
				}
			}	
			for (Map.Entry<String, List<String>> entry : map.entrySet()) 
			{
				key = entry.getKey();
				if(key != null)
				{
					int i;
					for(i = 0; i < entry.getValue().size(); i++)
					{
						headers += "\r\n"+entry.getKey()+": "+entry.getValue().get(i).toString();
					}
				}
			}		
			BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) 
			{
				response.append(inputLine+"\r\n");
			}
			in.close();
			String body = response.toString();
			httpResponse.header = headers;
			httpResponse.cookie = HTTPClient.extractCookie(headers);
			httpResponse.body = body;
		}
		else
		{
			httpResponse = this.httpPost(url, data, cookie);
		}
		return httpResponse;
	}
	/**
	 * Send data to the server HTTP server. This is the simple HTTP post without any security
	 * @param uri URI
	 * @param postData Post data
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	@SuppressWarnings("unused")
	private HTTPResponse httpPost(String uri, String postData) throws IOException
	{
		return this.httpPost(uri, postData, "");
	}
	/**
	 * Send data to the server HTTP server. This is the simple HTTP post without any security
	 * @param uri URI
	 * @param postData Post data
	 * @param cookie Cookie from the previous request
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	private HTTPResponse httpPost(String uri, String postData, String cookie) throws IOException, MalformedURLException
	{
		URL url = new URL(uri);
		String serverAddress = url.getHost();
		int serverPort = url.getPort();
		if(serverPort == -1)
		{
			serverPort = 80;
		}
		String serverContext = url.getPath()+"?"+url.getQuery();
		return this.httpPost(serverAddress, serverPort, serverContext, postData, cookie);
	}
	private HTTPResponse httpPost(URL url, String postData, String cookie) throws IOException 
	{
		String serverAddress = url.getHost();
		int serverPort = url.getPort();
		if(serverPort == -1)
		{
			serverPort = 80;
		}
		String serverContext = url.getPath()+"?"+url.getQuery();
		return this.httpPost(serverAddress, serverPort, serverContext, postData, cookie);
	}
	/**
	 * Send data to the server HTTP server. This is the simple HTTP post without any security
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param serverContext Server context
	 * @param postData Post data
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
    @SuppressWarnings("unused")
	private HTTPResponse httpPost(String serverAddress, int serverPort, String serverContext, String postData) throws IOException
    {
    	return this.httpPost(serverAddress, serverPort, serverContext, postData, "");
    }
	/**
	 * Send data to the server HTTP server. This is the simple HTTP post without any security
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param serverContext Server context
	 * @param postData Post data
	 * @param cookie Cookie from the previous request
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	private HTTPResponse httpPost(String serverAddress, int serverPort, String serverContext, String postData, String cookie) throws IOException
	{
		HTTPResponse responseMap = new HTTPResponse();
		String headers = "";
		headers += "POST "+serverContext+" HTTP/1.1\r\n";
		headers += "Host: "+serverAddress+"\r\n";
		headers += "Api-Key: "+ Utility.urlEncode(Configuration.getApiKey())+"\r\n";
		headers += "User-Agent: "+ Configuration.getUserAgent()+"\r\n";
		headers += "Content-Type: application/x-www-form-urlencoded\r\n";
		if(!cookie.equals(""))
		{
			headers += "Cookie: "+cookie+"\r\n";
		}
		headers += "Content-Length: "+postData.length()+"\r\n";
		String dataToSent = "";
		dataToSent = headers+"\r\n"+postData;
		Socket socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(serverAddress, serverPort);
		String response = "";
		String headerResponse = "";
		String bodyResponse = "";
		String[] headerArray;
		socket.connect(socketAddress, Configuration.getTimeout());
		socket.getOutputStream().write(dataToSent.getBytes());
		int chr = 0;
		do
		{
			chr = socket.getInputStream().read();
			if(chr > -1)
			{
				response += String.format("%c", chr);
			}
		}
		while(!response.contains("\r\n\r\n"));
		headerResponse = response.replace("\r\n\r\n", "");
		headerArray = headerResponse.split("\\r\\n");
		String contentLength = Utility.getFirst(headerArray, "Content-Length");
		String transferEncoding = Utility.getFirst(headerArray, "Transfer-Encoding").trim().toLowerCase();
		if(contentLength.equals(""))
		{
			BufferedReader input;
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String firstLine = input.readLine();
			String line = "";
			String currentLength = "";
			if(transferEncoding.equals("chunked"))
			{
				if(firstLine.equals("0"))
				{
					do 
					{
						line = input.readLine();
						line = line.replaceAll("\r", "").replaceAll("\n", "");
						bodyResponse += line;
						currentLength = String.format("%x", bodyResponse.length());
						if(line.equals(currentLength) || line == null)
						{
							break;
						}
						bodyResponse += "\n";
					}
					while(true);
				}
				else if(firstLine.matches("-?[0-9a-fA-F]+"))
				{
					try
					{
						String buff = "";
						long dataToRead = 0;
						String lines = "";
						while(!firstLine.equals("0") && !firstLine.equals(""))
						{
							dataToRead = Long.parseLong(firstLine, 16);
							buff = "";
							while(buff.length() < dataToRead)
							{
								lines = input.readLine();
								buff += lines+"\r\n";
							}
							if(buff.length() > dataToRead)
							{
								buff = buff.substring(0, (int)dataToRead);
							}
							bodyResponse += buff;
							firstLine = input.readLine();
						}
					}
					catch(NumberFormatException e)
					{
						if(Configuration.isDebugMode())
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					bodyResponse += firstLine;
				}
			}
			else
			{
				String buff = "";
				do
				{
					buff = input.readLine();
					if(buff != null)
					{
						bodyResponse += buff;
					}
					else
					{
						break;
					}
				}
				while(true);
			}

		}
		else
		{
			long length = Long.parseLong(contentLength);			
			long i;
			for(i = 0; i<length; i++)
			{
				chr = socket.getInputStream().read();
				if(chr > -1)
				{
					bodyResponse += String.format("%c", chr);
				}				
			}
		}			
		responseMap.header = headerResponse;
		responseMap.cookie = HTTPClient.extractCookie(headerResponse);
		responseMap.body = bodyResponse;
		socket.close();
		return responseMap;		
	}
	/**
	 * Get data from the HTTP server by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param uri URI
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	@SuppressWarnings("unused")
	private HTTPResponse httpGet(String uri) throws IOException
	{
		return this.httpGet(uri, "");
	}
	/**
	 * Get data from the HTTP server by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param uri URI
	 * @param cookie Cookie from the previous request
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	private HTTPResponse httpGet(String uri, String cookie) throws IOException, MalformedURLException
	{
		URL url = new URL(uri);
		String serverAddress = url.getHost();
		int serverPort = url.getPort();
		if(serverPort == -1)
		{
			serverPort = 80;
		}
		String serverContext = url.getPath()+"?"+url.getQuery();
		return this.httpGet(serverAddress, serverPort, serverContext, cookie);
	}
	/**
	 * Get data from the HTTP server by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param url URL
	 * @param cookie Cookie from the previous request
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	private HTTPResponse httpGet(URL url, String cookie) throws IOException 
	{
		String serverAddress = url.getHost();
		int serverPort = url.getPort();
		if(serverPort == -1)
		{
			serverPort = 80;
		}
		String serverContext = url.getPath()+"?"+url.getQuery();
		return this.httpGet(serverAddress, serverPort, serverContext, cookie);
	}
	/**
	 * Get data from the HTTP server by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param serverContext Server context
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	@SuppressWarnings("unused")
	private HTTPResponse httpGet(String serverAddress, int serverPort, String serverContext) throws IOException
	{
		return this.httpGet(serverAddress, serverPort, serverContext, "");
	}
	/**
	 * Get data from the HTTP server by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param serverContext Server context
	 * @param cookie Cookie from the previous request
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	private HTTPResponse httpGet(String serverAddress, int serverPort, String serverContext, String cookie) throws IOException
	{
		HTTPResponse responseMap = new HTTPResponse();
		String headers = "";
		headers += "GET "+serverContext+" HTTP/1.1\r\n";
		headers += "Host: "+serverAddress+"\r\n";
		headers += "Api-Key: "+Utility.urlEncode(Configuration.getApiKey())+"\r\n";
		headers += "User-Agent: "+ Configuration.getUserAgent()+"\r\n";
		headers += "Content-Type: application/x-www-form-urlencoded\r\n";
		if(!cookie.equals(""))
		{
			headers += "Cookie: "+cookie+"\r\n";
		}
		String dataToSent = "";
		dataToSent = headers+"\r\n";
		Socket socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(serverAddress, serverPort);
		String response = "";
		String headerResponse = "";
		String bodyResponse = "";
		String[] headerArray;

		socket.connect(socketAddress, Configuration.getTimeout());
		socket.getOutputStream().write(dataToSent.getBytes());
		int chr = 0;
		do
		{
			chr = socket.getInputStream().read();
			if(chr > -1)
			{
				response += String.format("%c", chr);					
			}
		}
		while(!response.contains("\r\n\r\n"));
		headerResponse = response.replace("\r\n\r\n", "");
		headerArray = headerResponse.split("\\r\\n");
		String contentLength = Utility.getFirst(headerArray, "Content-Length");
		String transferEncoding = Utility.getFirst(headerArray, "Transfer-Encoding").trim().toLowerCase();
		if(contentLength.equals(""))
		{
			BufferedReader input;
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String firstLine = input.readLine();
			String line = "";
			String currentLength = "";
			if(transferEncoding.equals("chunked"))
			{
				if(firstLine.equals("0"))
				{
					do 
					{
						line = input.readLine();
						line = line.replaceAll("\r", "").replaceAll("\n", "");
						bodyResponse += line;
						currentLength = String.format("%x", bodyResponse.length());
						if(line.equals(currentLength) || line == null)
						{
							break;
						}
						bodyResponse += "\n";
					}
					while(true);
				}
				else if(firstLine.matches("-?[0-9a-fA-F]+"))
				{
					try
					{
						String buff = "";
						long dataToRead = 0;
						String lines = "";
						while(!firstLine.equals("0") && !firstLine.equals(""))
						{
							dataToRead = Long.parseLong(firstLine, 16);
							buff = "";
							while(buff.length() < dataToRead)
							{
								lines = input.readLine();
								buff += lines+"\r\n";
							}
							if(buff.length() > dataToRead)
							{
								buff = buff.substring(0, (int)dataToRead);
							}
							bodyResponse += buff;
							firstLine = input.readLine();
						}
					}
					catch(NumberFormatException e)
					{
						if(Configuration.isDebugMode())
						{
							e.printStackTrace();
						}
					}
				}
				else
				{
					bodyResponse += firstLine;
				}
			}
			else
			{
				String buff = "";
				int i = 0;
				do
				{
					buff = input.readLine();
					if(buff != null)
					{
						if(i > 0)
						{
							bodyResponse += "\r\n";
						}
						bodyResponse += buff;
						System.out.println(buff);
					}
					else
					{
						break;
					}
					i++;
				}
				while(true);
			}
		}
		else
		{
			long length = Long.parseLong(contentLength);
			
			long i;
			for(i = 0; i<length; i++)
			{
				chr = socket.getInputStream().read();
				if(chr > -1)
				{
					bodyResponse += String.format("%c", chr);
				}				
			}
		}			
		responseMap.header = headerResponse;
		responseMap.cookie = HTTPClient.extractCookie(headerResponse);
		responseMap.body = bodyResponse;
		socket.close();
		return responseMap;		
	}
	/**
	 * Get response header from the HTTP server without body by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param uri URI
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	@SuppressWarnings("unused")
	private HTTPResponse httpHead(String uri) throws IOException
	{
		return this.httpHead(uri, "");
	}
	/**
	 * Get response header from the HTTP server without body by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param uri URI
	 * @param cookie Cookie from the previous request
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	private HTTPResponse httpHead(String uri, String cookie) throws IOException, MalformedURLException
	{
		URL url = new URL(uri);
		String serverAddress = url.getHost();
		int serverPort = url.getPort();
		if(serverPort == -1)
		{
			serverPort = 80;
		}
		String serverContext = url.getPath()+"?"+url.getQuery();
		return this.httpHead(serverAddress, serverPort, serverContext, cookie);
	}
	/**
	 * Get response header from the HTTP server without body by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param serverContext Server context
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	@SuppressWarnings("unused")
	private HTTPResponse httpHead(String serverAddress, int serverPort, String serverContext) throws IOException
	{
		return this.httpHead(serverAddress, serverPort, serverContext, "");
	}
	/**
	 * Get response header from the HTTP server without body by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param serverAddress Server address
	 * @param serverPort Server port
	 * @param serverContext Server context
	 * @param cookie Cookie from the previous request
	 * @return Server response
	 * @throws IOException if any socket errors
	 */
	private HTTPResponse httpHead(String serverAddress, int serverPort, String serverContext, String cookie) throws IOException
	{
		HTTPResponse responseMap = new HTTPResponse();
		String headers = "";
		headers += "HEAD "+serverContext+" HTTP/1.1\r\n";
		headers += "Host: "+serverAddress+"\r\n";
		headers += "Api-Key: "+Utility.urlEncode(Configuration.getApiKey())+"\r\n";
		headers += "User-Agent: "+ Configuration.getUserAgent()+"\r\n";
		headers += "Content-Type: application/x-www-form-urlencoded\r\n";
		if(!cookie.equals(""))
		{
			headers += "Cookie: "+cookie+"\r\n";
		}
		String dataToSent = "";
		dataToSent = headers+"\r\n";
		Socket socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(serverAddress, serverPort);
		String response = "";
		String headerResponse = "";
		String bodyResponse = "";
		socket.connect(socketAddress, Configuration.getTimeout());
		socket.getOutputStream().write(dataToSent.getBytes());
		int chr = 0;
		do
		{
			chr = socket.getInputStream().read();
			if(chr > -1)
			{
				response += String.format("%c", chr);					
			}
		}
		while(!response.contains("\r\n\r\n"));
		headerResponse = response.replace("\r\n\r\n", "");
		responseMap.header = headerResponse;
		responseMap.cookie = HTTPClient.extractCookie(headerResponse);
		responseMap.body = bodyResponse;
		socket.close();
		return responseMap;		
	}
	/**
	 * Get response header from the HTTP server without body by sending request URI and cookie. This is the simple HTTP get without any security
	 * @param url URL
	 * @param cookie Cookie from the previous request
	 * @return HTTPResponse
	 * @throws IOException
	 */
	private HTTPResponse httpHead(URL url, String cookie) throws IOException 
	{
		String serverAddress = url.getHost();
		int serverPort = url.getPort();
		if(serverPort == -1)
		{
			serverPort = 80;
		}
		String serverContext = url.getPath()+"?"+url.getQuery();
		return this.httpHead(serverAddress, serverPort, serverContext, cookie);
	}
	/**
	 * Extract cookie
	 * @param header HTTP response header
	 * @return Cookie
	 */
	public static String extractCookie(String header)
	{
		String[] headerArray = header.split("\\r\\n");
		int i;
		int maxLine = 0;
		maxLine = headerArray.length;
		String[] arr; 
		String cookie = "";
		String cookies = "";
		String[] arr2;
		int cookielen = 0;
		for(i = 0; i<maxLine; i++)
		{
			if(headerArray[i].contains(":"))
			{
				arr = headerArray[i].split("\\:", 2);
				if(arr[0].contains("Set-Cookie"))
				{
					cookie = arr[1].trim();
					arr2 = cookie.split(";\\s*", 2);
					if(cookielen > 0)
					{
						cookies += "; ";
					}
					cookies += arr2[0].trim();
					cookielen++;
				}
			}
		}
		return cookies;
	}
}
