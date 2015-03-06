package com.DreamTV;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

public class WebViewEx extends WebView {

	private boolean paused = false;

	public WebViewEx(Context context)
	{
		super(context);
		setBackgroundColor(Color.BLACK);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) 
	{
		return false;
	}
	
		
	public void pause()
	{
		callHiddenWebViewMethod("onPause");
		paused = true;
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public void resume()
	{
		callHiddenWebViewMethod("onResume");
		paused = false;
	}
	
	private void callHiddenWebViewMethod(String name)
	{
	    
	        try 
	        {
	            Method method = WebView.class.getMethod(name);
	            method.invoke(this);
	        } catch (NoSuchMethodException e) {
	            Log.e("No such method: " + name, e.toString());
	        } catch (IllegalAccessException e) {
	            Log.e("Illegal Access: " + name, e.toString());
	        } catch (InvocationTargetException e) {
	            Log.e("Invocation Target Exception: " + name, e.toString());
	        }
	}
	
}