package com.example.planetbiru.notification;

/**
 * NotificationException
 * @author Kamshory, MT
 *
 */
@SuppressWarnings("serial")
public class NotificationException extends Exception
{
	public NotificationException() 
	{ 
		super(); 
	}
	public NotificationException(String message) 
	{ 
		super(message); 
	}
	public NotificationException(String message, Throwable cause) 
	{ 
		super(message, cause); 
	}
	public NotificationException(Throwable cause) 
	{ 
		super(cause); 
	}

}
