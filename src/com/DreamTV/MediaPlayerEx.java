package com.DreamTV;

import com.DreamTV.ProgramInfo.ChannelStream;

import android.media.MediaPlayer;

public class MediaPlayerEx extends MediaPlayer 
{
	public MediaPlayerEx()
	{
		super();
		
	}
	
	public boolean play(ChannelStream channelStream)
	{
		try 
		{
			stop();
			
			setDataSource(channelStream.mLink);
			prepareAsync();
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		} 
		
		return true;
		
	}
	
	public void stop()
	{
		if(isPlaying())
			super.stop();
		reset();
	}
	
}
