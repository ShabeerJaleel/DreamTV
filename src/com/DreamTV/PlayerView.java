package com.DreamTV;

import java.util.EventListener;

import com.DreamTV.ProgramInfo.ChannelStream;
import com.DreamTV.ProgramInfo.StreamingProtocol;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public abstract class PlayerView extends FrameLayout {

	protected OnPlayerEventsListener mOnPlayerEventsListener;
	public static int LIVE_STREAM_DURATION = -1;
	private boolean mIsLive = false;
	
	
	public PlayerView(Context context) 
	{
		super(context);
		setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ));
		setBackgroundColor(Color.BLACK);
		setPadding(0, 0, 0, 0);
		
	}
	
	public void play(String streamInfo, boolean isLive, boolean fullScreen){ mIsLive= isLive; }
	public void play(ChannelStream stream, boolean isLive, boolean fullScreen){mIsLive= isLive;}
	public void stop()throws Exception{throw new Exception();}
	public void pause(){}
	public void resume(){}
	public void refresh(){}
	public void seek(int position){}
	public void destroy(){	mOnPlayerEventsListener = null; }
	public boolean isLiveStream(){	return mIsLive; }
	public boolean isPlaying(){return false;}
	public int getCurrentPosition(){return -1;}
	public int getDuration(){return -1;}
	public boolean isPaused(){return false;}
	
	public StreamingProtocol getProtocol() throws Exception{throw new Exception();}
	
	public void setOnPlayerEventsListener(OnPlayerEventsListener listener){
		mOnPlayerEventsListener = listener;
	}
	
	public void removeOnplayerEventListener()
	{
		mOnPlayerEventsListener = null;
	}
	
	public interface OnPlayerEventsListener extends EventListener {
	    public void onStreamLoaded(int duration);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return false;
	}
	

	
}
