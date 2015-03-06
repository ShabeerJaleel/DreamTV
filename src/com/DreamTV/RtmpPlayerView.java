package com.DreamTV;

import com.DreamTV.ProgramInfo.ChannelStream;
import com.DreamTV.ProgramInfo.StreamingProtocol;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.LinearLayout;

public class RtmpPlayerView extends PlayerView 
{
	private WebViewEx mWebView;
	private boolean mIsPlaying;
	
	public RtmpPlayerView(Context context) 
	{
		super(context);

		init();
	}

	private void init() 
	{
		createWebView();
		mWebView.setVisibility(View.INVISIBLE);
		addView(mWebView);
	}

	
	private void createWebView()
	{

		mWebView = new WebViewEx(getContext());
		mWebView.setBackgroundColor(Color.BLACK);
		LinearLayout.LayoutParams lVidViewLayoutParams =new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setLayoutParams(lVidViewLayoutParams);
		
		WebSettings settings = mWebView.getSettings();
		settings.setAllowFileAccess(true);
		settings.setJavaScriptEnabled(true);
		settings.setPluginState(PluginState.ON);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		settings.setSupportZoom(false);
		

		mWebView.setWebChromeClient(new WebChromeClient()
		{
			

		});

		mWebView.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onLoadResource(WebView view, String url)
			{

				super.onLoadResource(view, url);
				mWebView.setBackgroundColor(Color.BLACK);
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) 
			{
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) 
			{
				super.onPageFinished(view, url);
				if(mWebView == null)
					return;
				mIsPlaying = true;
				if(RtmpPlayerView.this.mOnPlayerEventsListener != null)
					RtmpPlayerView.this.mOnPlayerEventsListener.onStreamLoaded(PlayerView.LIVE_STREAM_DURATION);
				mWebView.setVisibility(View.VISIBLE);
				mWebView.setBackgroundColor(Color.BLACK);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) 
			{
				super.onReceivedError(view, errorCode, description, failingUrl);
				Log.d("Dream TV", description);
			}
			
		});


	}

	@Override
	public void play(String streamInfo, boolean isLive, boolean fullScreen) 
	{
		super.play(streamInfo, isLive, fullScreen);
		mIsPlaying = false;
		mWebView.loadUrl(streamInfo);
	}
	
	@Override
	public void play(ChannelStream stream, boolean isLive, boolean fullScreen) 
	{
			super.play(stream, isLive, fullScreen);
			
			String str =  stream.getStream();
			Log.d("Dream TV", str);
			mIsPlaying = false;
			if(stream.mEmbedd )
				mWebView.loadDataWithBaseURL(stream.mUrl, 
						str, null, "text/html", stream.mUrl);
			else
				play(str, isLive, stream.mFullScreen);
				
	}

	@Override
	public void stop()
	{
		mIsPlaying = false;
		mWebView.stopLoading();
	}

	@Override
	public void pause() 
	{
		mWebView.pause();
	}
	
	@Override
	public void refresh()
	{
		mWebView.reload();
	}
	
	@Override
	public boolean isPaused() 
	{
		return mWebView.isPaused();
	}
	
	@Override
	public boolean isPlaying() 
	{
		return mIsPlaying;
	}
	
	@Override
	public void resume() 
	{
		mWebView.resume();
	}
	
	

	@Override
	public void destroy() 
	{
		stop();
		removeAllViews();
		mWebView.freeMemory();
		mWebView.destroy();
		mWebView = null;
	}

	@Override
	public StreamingProtocol getProtocol()
	{
		return StreamingProtocol.Rtmp;
	}

}
