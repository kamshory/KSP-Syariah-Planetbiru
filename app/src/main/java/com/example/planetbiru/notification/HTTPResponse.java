package com.example.planetbiru.notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.security.cert.Certificate;

public class HTTPResponse 
{
	/**
	 * Header
	 */
	public String header = "";
	/**
	 * Cookie
	 */
	public String cookie = "";
	/**
	 * Body
	 */
	public String body = "";
	/**
	 * Response code
	 */
	public int responseCode = 200;
	/**
	 * Cipher suite
	 */
	public String cipherSuite = "";
	/**
	 * Certificate
	 */
	public Certificate[] serverCertificates = null;
	/**
	 * To string
	 */
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
