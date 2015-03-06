package com.DreamTV;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class NetworkTaskHandler
{
	
	public static void downloadChannelList(Context context)
	{
		//(new NetworkTaskHandler()).new ChannelListDownloadTask(context).execute();
		
	}
	
	public static GetLocationTask locateCountry(Context context)
	{
		GetLocationTask task = (new NetworkTaskHandler()).new GetLocationTask(context);
		task.execute();
		return task;
	}
	
	
	private class ChannelListDownloadTask extends AsyncTask<Void, Void, Void> 
	{
		private Context mContext;
		public ChannelListDownloadTask(Context context)
		{
			mContext = context;
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			try
			{
				URL url = new URL("https://sites.google.com/site/dreamlivetv/configuration/progdata.zip");
				URLConnection connection = url.openConnection();
				InputStream iStream = connection.getInputStream();
				
	             ZipInputStream zipStream = new ZipInputStream(iStream);
	             zipStream.getNextEntry();
	             
	             ByteArrayOutputStream baos = new ByteArrayOutputStream();
	             byte[] buffer = new byte[1024];
	             int count;
	             while ((count = zipStream.read(buffer)) != -1) {
	                 baos.write(buffer, 0, count);
	             }
	             
	             
	             /* Convert the Bytes read to a String. */
	             FileOutputStream fos = mContext.openFileOutput(ProgramInfo.FILENAME, Context.MODE_PRIVATE);
	             fos.write(baos.toByteArray());
	             fos.close();
	             zipStream.close();
	             Log.d("Dream Tv", "Channel List downloaded");
				
			}
			catch(Exception e)
			{
				Log.d("Dream Tv", "********Channel List download error*****************");
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
	public class GetLocationTask extends AsyncTask<Void, Void, Void> 
	{
		private Context mContext;
		public String mUserCountryCode;
		public GetLocationTask(Context context)
		{
			mContext = context;
		}

		@Override
		protected Void doInBackground(Void... params) 
		{
			try
			{
				 HttpClient lClient = new DefaultHttpClient();
	                
	                HttpGet lGetMethod = new HttpGet("http://freegeoip.net/json/");
	                
	                HttpResponse lResp = null;

	                lResp = lClient.execute(lGetMethod);
	                
	                ByteArrayOutputStream lBOS = new ByteArrayOutputStream();
	                String lInfoStr = null;
                    lResp.getEntity().writeTo(lBOS);
                    lInfoStr = lBOS.toString("UTF-8");
                    JSONObject response = new JSONObject(lInfoStr);
                    mUserCountryCode = response.get("country_code").toString();
			}
			catch(Exception e)
			{
				Log.d("Dream Tv", "********Channel List download error*****************");
				e.printStackTrace();
			}
			return null;
		}
	}

}
