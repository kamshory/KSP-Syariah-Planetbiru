package com.example.planetbiru.notification;

import java.io.IOException;

public class ConnectionChecker extends Thread
{
	private Notification notification;
	private long waitToNext;
	public ConnectionChecker(Notification notification, long waitToNext)
	{
		this.notification = notification;
		this.waitToNext = waitToNext;
	}
	public ConnectionChecker() 
	{
	}
	public void run()
	{
		if(this.notification.isSsl())
		{
			if(this.waitToNext > 0)
			{
				try
				{
					Thread.sleep(this.waitToNext);
					this.notification.socketSSL.close();
				} 
				catch (IOException e) 
				{
					if(Configuration.isDebugMode())
					{
						e.printStackTrace();
					}
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
					if(Configuration.isDebugMode())
					{
						e.printStackTrace();
					}
				}
			}			
		}
		else
		{
			if(this.waitToNext > 0)
			{
				try
				{
					Thread.sleep(this.waitToNext);
					this.notification.socket.close();
				} 
				catch (IOException e) 
				{
					if(Configuration.isDebugMode())
					{
						e.printStackTrace();
					}
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
					if(Configuration.isDebugMode())
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
