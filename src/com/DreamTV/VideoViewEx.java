package com.DreamTV;

import android.content.Context;
import android.widget.VideoView;

public class VideoViewEx extends VideoView {
	
	public boolean mPause;
	public boolean mFullScreen;

	public VideoViewEx(Context context) 
	{
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		if(mFullScreen)
		{
			int width = getDefaultSize(0, widthMeasureSpec);
			int height = getDefaultSize(0, heightMeasureSpec);

			setMeasuredDimension(width, height);
		}
		else
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void start(boolean fullScreen) 
	{
		mFullScreen = fullScreen;
		
		super.start();
	}
	
	@Override
	public void pause()
	{
		super.pause();
		mPause = true;
	}
	}
