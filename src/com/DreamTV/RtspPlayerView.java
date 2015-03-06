package com.DreamTV;

import com.DreamTV.ProgramInfo.ChannelStream;
import com.DreamTV.ProgramInfo.StreamingProtocol;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class RtspPlayerView extends PlayerView {

	private VideoViewEx mVideoView;
	private Uri mCurrentUri;
	private MediaPlayer mMediaPlayer;
	private boolean mPasued;
	private int mPausedPosition;
	
	public RtspPlayerView(Context context) 
	{
		super(context);

		init();
	}

	private void init()
	{
		mVideoView = new VideoViewEx(getContext());
		mVideoView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.CENTER));

		mVideoView.setOnPreparedListener(new OnPreparedListener() 
		{
			public void onPrepared(MediaPlayer mp) 
			{
				mMediaPlayer = mp;

				mMediaPlayer.setOnInfoListener(new OnInfoListener() 
				{

					public boolean onInfo(MediaPlayer mp, int what, int extra)
					{
						return false;
					}
				});

				if(RtspPlayerView.this.mOnPlayerEventsListener != null)
					RtspPlayerView.this.mOnPlayerEventsListener.onStreamLoaded(mp.getDuration());
			}
		});

		mVideoView.setOnCompletionListener(new OnCompletionListener() 
		{

			public void onCompletion(MediaPlayer mp)
			{
			}
		});

		mVideoView.setOnErrorListener(new OnErrorListener() 
		{
			public boolean onError(MediaPlayer mp, int what, int extra) 
			{
				Log.d("Dream TV", "Errorrrrrrrrrrrrrrrrrrrrr");
				return false;
			}
		});

		addView(mVideoView);

		

	}

	@Override
	public void play(String streamInfo, boolean isLive, boolean fullScreen)
	{
		super.play(streamInfo, isLive, fullScreen);
		mCurrentUri = Uri.parse(streamInfo);
		mVideoView.setVideoURI(mCurrentUri);
		mVideoView.start(fullScreen);
		mVideoView.requestFocus();
	}
	
	@Override
	public void play(ChannelStream stream, boolean isLive, boolean fullScreen)
	{
		super.play(stream, isLive, fullScreen);
		play(stream.mLink, isLive, fullScreen);
	}

	@Override
	public void stop() 
	{
		mVideoView.stopPlayback();
	}

	@Override
	public void seek(int position) 
	{
		mVideoView.seekTo( position);
	}

	@Override
	public void destroy()
	{
		super.destroy();
		stop();
		mVideoView = null;
		mMediaPlayer = null;
	}
	
	@Override
	public void pause() 
	{
		mVideoView.pause();
		mPasued = true;
		mPausedPosition = mVideoView.getCurrentPosition();
	}
	
	@Override
	public void resume() 
	{
		if(mPasued)
		{
			//mVideoView.resume();
			mVideoView.seekTo(mPausedPosition);
			mVideoView.start();
			mPasued = false;
		}
	}
	
	@Override
	public boolean isPaused() {
		return mPasued;
	}

	@Override
	public StreamingProtocol getProtocol() 
	{
		return StreamingProtocol.Rtsp;
	}

	@Override
	public int getCurrentPosition() 
	{

		return mVideoView.getCurrentPosition();
	}

	@Override
	public int getDuration() 
	{
		return mVideoView.getDuration();
	}
	
	@Override
	public boolean isPlaying() 
	{
		return mVideoView.isPlaying();
	
	}

}
