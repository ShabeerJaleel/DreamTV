package com.DreamTV;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Spinner;

public class BuildVersion11Helper {

	public static void SetLowUIProfile(View view)
	{
		//view.setSystemUiVisibility(1);
	}
	
	public static void SetHardwareAcceleration(Window window, Boolean enable)
	{
		window.setFlags(16777216,16777216);
	}
	
	public static Spinner createSpinner(Context context)
	{
		//return new Spinner(context, 1);
		return new Spinner(context);
		
	}
}
