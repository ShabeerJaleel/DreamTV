package com.DreamTV;


import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.DreamTV.ChannelSelectorDialog.OnChannelSelectedListener;
import com.DreamTV.PlayerView.OnPlayerEventsListener;
import com.DreamTV.ProgramInfo.Channel;
import com.DreamTV.ProgramInfo.ChannelStream;
import com.DreamTV.ProgramInfo.StreamingProtocol;
import com.DreamTV.YouTube.YouTubeIdInfo;
import com.DreamTV.YouTube.YouTubeIdInfo.YouTubeSingleVideoIdInfo;
import com.DreamTV.YouTube.YouTubeUtility;


import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class PlayerActivity extends Activity {
	
	private PlayerView mPlayerView;

	private ProgressBar mProgressBar;
	private TextView mProgressMessage;
	//private ImageView mLogoImage;
	
	private LinearLayout mPanelBottom;
	private LinearLayout mPanelBottomProgress;
	private LinearLayout mPanelBottomControls;
	private ChannelStream mActiveChannelStream;
	private GestureDetector mGestureDetector;
	private PopupWindowEx mPopupBottom;
	private ChannelSelectorDialog mChannelSurfDialog;
	private RelativeLayout mMainLayout;
	private SeekBarEx mVideoSeekbar;

	private TextView mCurrentPositionTextView;
	private TextView mDurationTextView;
	private ImageButton mLockButton;
	private ImageButton mFavouriteButton;
	private ImageButton mPreviousButton;
	private ImageButton mPlayButton;
	private ImageButton mNextButton;
	private ImageButton mSizeButton;
	private ImageButton mChannelButton;

	private ImageButton mNumberOneButton;
	private ImageButton mNumberTwoeButton;
	private ImageButton mNumberThreeButton;
	private ImageButton mNumberFourButton;
	private ImageButton mNumberFiveButton;
	private ImageButton mChannelButtons[] = new ImageButton[5];
	private VideoProgress mVideoProgress;
	
	private LinearLayout mPanelTop;
	private TextView mChannelInfoTextView;
	private TextView mBatteryInfoTextView;
	private TextView mTimeTextView;
	private PopupWindowEx mPopupTop;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init()
	{
		mGestureDetector = new GestureDetector(this, new GestureListener());
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if(Build.VERSION.SDK_INT >= 11)
		{
			BuildVersion11Helper.SetLowUIProfile(getWindow().getDecorView());
			BuildVersion11Helper.SetHardwareAcceleration(getWindow(), true);
		}

		setupView(ProgramInfo.Me.ActiveChannel.mChannelStreamCollection.get(0));

		play(ProgramInfo.Me.ActiveChannel.mChannelStreamCollection.get(0));
	}

	private void setupView(ChannelStream stream) 
	{
		mMainLayout = new RelativeLayout(this);
		mMainLayout.setId(2);
		mMainLayout.setGravity(Gravity.FILL);
		mMainLayout.setBackgroundColor(Color.BLACK);
		android.widget.RelativeLayout.LayoutParams lRelLayoutParms = 
				new android.widget.RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		mMainLayout.setLayoutParams(lRelLayoutParms);

		createLogo();
		createProgressIndicators();
		createTopPanel();
		createBottomPanel();
		createChannelSelector();
		//createPlayer(stream);
		initVideoProgessThread();


		mMainLayout.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event) 
			{
				return false;
			}
		});

		setContentView(mMainLayout);
	}

	private void initVideoProgessThread()
	{
		if(mVideoProgress != null)
			mVideoProgress.cancel();

		mVideoProgress = new VideoProgress(this, 1000);

	}

	private void createLogo()
	{
//		mLogoImage = new ImageView(this);
//		android.widget.RelativeLayout.LayoutParams lProgressMsgLayoutParms = 
//				new android.widget.RelativeLayout.LayoutParams(60,60);
//		lProgressMsgLayoutParms.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		lProgressMsgLayoutParms.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//		mLogoImage.setLayoutParams(lProgressMsgLayoutParms);
//		Drawable image = getResources().getDrawable(R.drawable.ic_tvlogo);
//		mLogoImage.setImageDrawable(image);
//		mLogoImage.setAlpha(127);
	}

	private void createProgressIndicators()
	{
		mProgressBar = new ProgressBar(this);
		mProgressBar.setIndeterminate(true);
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.setEnabled(true);
		mProgressBar.setId(3);
		android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		mProgressBar.setLayoutParams(lp);

		mProgressMessage = new TextView(this);
		mProgressMessage.setId(4);
		lp = new android.widget.RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lp.addRule(RelativeLayout.BELOW, 3);
		mProgressMessage.setLayoutParams(lp);
		mProgressMessage.setTextColor(Color.LTGRAY);
		mProgressMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		mProgressMessage.setText("Please wait while loading...");

	}

	private void createChannelSelector()
	{
		mChannelSurfDialog  = new ChannelSelectorDialog(this, 
				android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		mChannelSurfDialog.setOnChannelSelectedListener(new OnChannelSelectedListener() 
		{
			public void onActiveChannelChanged(Channel selectedChannel) 
			{
				play(selectedChannel.mChannelStreamCollection.get(0));
			}
		});
	}
	
	private void createTopPanel()
	{
		mPanelTop = new LinearLayout(this);
		mPanelTop.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP));
		mPanelTop.setOrientation(LinearLayout.HORIZONTAL);
		mPanelTop.setBackgroundColor(0x64969696);
		mPanelTop.setWeightSum(3);
		
		mChannelInfoTextView = new TextView(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mChannelInfoTextView.setLayoutParams(lp);
		mChannelInfoTextView.setTextAppearance(this, android.R.attr.textAppearanceSmall);
		mChannelInfoTextView.setText(">> " + ProgramInfo.Me.ActiveChannel.mName);
		mChannelInfoTextView.setTextColor(Color.WHITE);
		
		mBatteryInfoTextView = new TextView(this);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mBatteryInfoTextView.setLayoutParams(lp);
		mBatteryInfoTextView.setTextAppearance(this, android.R.attr.textAppearanceSmall);
		mBatteryInfoTextView.setText("78%");
		mBatteryInfoTextView.setTextColor(Color.WHITE);
		
		mTimeTextView = new TextView(this);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mTimeTextView.setLayoutParams(lp);
		mTimeTextView.setTextAppearance(this, android.R.attr.textAppearanceSmall);
		mTimeTextView.setText(DateFormat.getTimeInstance().format(new Date()));
		mTimeTextView.setTextColor(Color.WHITE);
		
		mPanelTop.addView(mChannelInfoTextView);
		mPanelTop.addView(mBatteryInfoTextView);
		mPanelTop.addView(mTimeTextView);
		mPopupTop = new PopupWindowEx(this, mPanelTop);
	}

	private void createBottomPanel()
	{
		mPanelBottom = new LinearLayout(this);
		mPanelBottom.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));
		mPanelBottom.setOrientation(LinearLayout.VERTICAL);
		mPanelBottom.setBackgroundColor(0x64969696);


		//top panel part
		mPanelBottomProgress = new LinearLayout(this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 8, 0, 8);
		mPanelBottomProgress.setWeightSum(10);
		mPanelBottomProgress.setOrientation(LinearLayout.HORIZONTAL);
		mPanelBottomProgress.setLayoutParams(lp);
		mPanelBottomProgress.setGravity(Gravity.CENTER);


		//bottom panel part
		mPanelBottomControls= new LinearLayout(this);
		lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mPanelBottomControls.setLayoutParams(lp);
		mPanelBottomControls.setOrientation(LinearLayout.HORIZONTAL);
		mPanelBottomControls.setWeightSum(7);


		createVideoSeekPanelControls();
		createChannelNumberButtons();
		createPlayControls();


		mPanelBottomControls.addView(mLockButton);
		mPanelBottomControls.addView(mFavouriteButton);
		mPanelBottomControls.addView(mPreviousButton);
		mPanelBottomControls.addView(mPlayButton);
		mPanelBottomControls.addView(mNextButton);
		mPanelBottomControls.addView(mSizeButton);
		mPanelBottomControls.addView(mChannelButton);

		mPanelBottom.addView(mPanelBottomProgress);
		mPanelBottom.addView(mPanelBottomControls);

		mPopupBottom = new PopupWindowEx(this, mPanelBottom);
	}

	private void createVideoSeekPanelControls()
	{
		//
		//bottom panel progress controls
		mCurrentPositionTextView = new TextView(this);
		LinearLayout.LayoutParams lp  = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mCurrentPositionTextView.setLayoutParams(lp);
		mCurrentPositionTextView.setTextAppearance(this, android.R.attr.textAppearanceSmall);
		mCurrentPositionTextView.setText("00:00:00");
		mCurrentPositionTextView.setGravity(Gravity.CENTER);
		mCurrentPositionTextView.setTextColor(Color.WHITE);

		mVideoSeekbar = new SeekBarEx(this);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 8);
		lp.setMargins(4, 0, 4, 0);
		mVideoSeekbar.setLayoutParams(lp);

		mVideoSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) 
			{
				mPlayerView.seek(seekBar.getProgress());
			}

			public void onStartTrackingTouch(SeekBar seekBar) {	}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
			{
				mCurrentPositionTextView.setText(getDisplayTime(progress));
			}
		});



		mDurationTextView = new TextView(this);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mDurationTextView.setLayoutParams(lp);
		mDurationTextView.setTextAppearance(this, android.R.attr.textAppearanceSmall);
		mDurationTextView.setText("00:00:00");
		mDurationTextView.setTextColor(Color.WHITE);

	}

	private void createChannelNumberButtons()
	{
		//bottom panel channel controls 

		mNumberOneButton = new ImageButton(this);
		mNumberOneButton.setBackgroundColor(Color.TRANSPARENT);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mNumberOneButton.setLayoutParams(lp);
		mNumberOneButton.setImageResource(R.drawable.ic_number_one);
		mNumberOneButton.setOnClickListener(new ChannelClickListener(0));
		mChannelButtons[0] =mNumberOneButton; 

		mNumberTwoeButton = new ImageButton(this);
		mNumberTwoeButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mNumberTwoeButton.setLayoutParams(lp);
		mNumberTwoeButton.setImageResource(R.drawable.ic_number_two);
		mNumberTwoeButton.setOnClickListener(new ChannelClickListener(1));
		mChannelButtons[1] =mNumberTwoeButton;

		mNumberThreeButton = new ImageButton(this);
		mNumberThreeButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mNumberThreeButton.setLayoutParams(lp);
		mNumberThreeButton.setImageResource(R.drawable.ic_number_three);
		mNumberThreeButton.setOnClickListener(new ChannelClickListener(2));
		mChannelButtons[2] =mNumberThreeButton;

		mNumberFourButton = new ImageButton(this);
		mNumberFourButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mNumberFourButton.setLayoutParams(lp);
		mNumberFourButton.setImageResource(R.drawable.ic_number_four);
		mNumberFourButton.setOnClickListener(new ChannelClickListener(3));
		mChannelButtons[3] =mNumberFourButton;

		mNumberFiveButton = new ImageButton(this);
		mNumberFiveButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mNumberFiveButton.setLayoutParams(lp);
		mNumberFiveButton.setImageResource(R.drawable.ic_number_five);
		mNumberFiveButton.setOnClickListener(new ChannelClickListener(4));
		mChannelButtons[4] =mNumberFiveButton;


	}

	private void createPlayControls()
	{
		//bottom panel play controls

		//bottom panel
		// [Lock] [Favorite] [Previous] [Play/Pause/stop/reload] [Next] [Size]  [Channel]   

		mLockButton = new ImageButton(this);
		mLockButton.setBackgroundColor(Color.TRANSPARENT);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mLockButton.setLayoutParams(lp);
		mLockButton.setImageResource(R.drawable.ic_button_lock);

		mFavouriteButton = new ImageButton(this);
		mFavouriteButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mFavouriteButton.setLayoutParams(lp);
		mFavouriteButton.setImageResource(R.drawable.ic_button_favourite);

		mPreviousButton = new ImageButton(this);
		mPreviousButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mPreviousButton.setLayoutParams(lp);
		mPreviousButton.setImageResource(R.drawable.ic_button_rewind);
		mPreviousButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Channel channel = ProgramInfo.Me.GetPreviousChannel(mActiveChannelStream);
				if(channel != null)
				{
					ProgramInfo.Me.ActiveChannel = channel;
					play(channel.mChannelStreamCollection.get(0));
				}
			}
		});

		mPlayButton = new ImageButton(this);
		mPlayButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mPlayButton.setLayoutParams(lp);
		
		mPlayButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v)
			{
				if(ProgramInfo.Me.ActiveChannel.mIsLive)
				{
					play(mActiveChannelStream);
				}
				else
				{
					if(mPlayerView.isPaused())
						mPlayerView.resume();
					else
						mPlayerView.pause();
					setPlayButtonImage();
				}
			}
		});

		mNextButton = new ImageButton(this);
		mNextButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mNextButton.setLayoutParams(lp);
		mNextButton.setImageResource(R.drawable.ic_button_forward);
		mNextButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Channel channel = ProgramInfo.Me.GetNextChannel(mActiveChannelStream);
				if(channel != null)
				{
					ProgramInfo.Me.ActiveChannel = channel;
					play(channel.mChannelStreamCollection.get(0));
				}
			}
		});

		mSizeButton = new ImageButton(this);
		mSizeButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mSizeButton.setLayoutParams(lp);
		mSizeButton.setImageResource(R.drawable.ic_button_framesize);

		mChannelButton = new ImageButton(this);
		mChannelButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
		mChannelButton.setLayoutParams(lp);
		mChannelButton.setImageResource(R.drawable.ic_button_browsechannel);

	}
	
	private void setPlayButtonImage()
	{
		if(ProgramInfo.Me.ActiveChannel.mIsLive)
			mPlayButton.setImageResource(R.drawable.ic_button_reload);
		else
		{
			if(mPlayerView.isPaused())
				mPlayButton.setImageResource(R.drawable.ic_button_play);
			else
				mPlayButton.setImageResource(R.drawable.ic_button_pause);
		}
	}

	private void createPlayer(ChannelStream stream)
	{
		if(mPlayerView != null)
		{
			mPlayerView.removeOnplayerEventListener();
			mPlayerView.destroy();
		}

		if(stream.isYouTubeVideo())
			new NetworkTask().execute(new String[2]);

		if (stream.mProtocol == StreamingProtocol.Rtmp)
			mPlayerView = new RtmpPlayerView(this);
		else
			mPlayerView = new RtspPlayerView(this);

		setPlayerViewProperties();
	}

	private void setPlayerViewProperties()
	{
		mPlayerView.setBackgroundColor(Color.BLACK);

		mPlayerView.setOnPlayerEventsListener(new OnPlayerEventsListener()
		{
			public void onStreamLoaded(int duration) 
			{
//				mProgressBar.setVisibility(View.GONE);
//				mProgressMessage.setVisibility(View.GONE);
				mMainLayout.removeView(mProgressBar);
				mMainLayout.removeView(mProgressMessage);
				if(duration == PlayerView.LIVE_STREAM_DURATION)
				{
					mVideoSeekbar.setVisibility(View.INVISIBLE);
				}
				else
				{
					mVideoSeekbar.setVisibility(View.VISIBLE);
					mVideoSeekbar.setMax(duration);
				}
			}
		});

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		mChannelSurfDialog.show();
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		finish();
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		mPlayerView.destroy();
		mPlayerView = null;
		mProgressBar = null;
		mProgressMessage = null;
		//mLogoImage = null;
		mPanelBottom = null;
		mPanelBottomProgress = null;
		mPanelBottomControls = null;
		mActiveChannelStream = null;
		mGestureDetector = null;
		mPopupBottom = null;
		mChannelSurfDialog.removeOnChannelSelectedListener();
		mChannelSurfDialog.dismiss();
		mChannelSurfDialog = null;
		mMainLayout = null;
		mVideoSeekbar = null;
		mCurrentPositionTextView = null;
		mDurationTextView = null;
		mLockButton = null;
		mFavouriteButton = null;
		mPreviousButton = null;
		mPlayButton = null;
		mNextButton = null;
		mSizeButton = null;
		mChannelButton = null;

		mNumberOneButton = null;
		mNumberTwoeButton = null;
		mNumberThreeButton = null;
		mNumberFourButton = null;
		mNumberFiveButton = null;
		
		mVideoProgress.cancel();
		mVideoProgress = null;
		
		mPanelTop = null;
		mChannelInfoTextView = null;
		mBatteryInfoTextView = null;
		mTimeTextView = null;
		mPopupTop = null;

	}

	private void play(ChannelStream channelStream) 
	{
		
		mActiveChannelStream = channelStream;
		mMainLayout.removeAllViews();
		createPlayer(channelStream);

		mMainLayout.addView(mPlayerView);
		//mMainLayout.addView(mLogoImage);
		mMainLayout.addView(mProgressBar);
		mMainLayout.addView(mProgressMessage);
		
		mPlayerView.play(channelStream, channelStream.mChannel.mIsLive, channelStream.mFullScreen);

		updateBottomPanelView();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return mGestureDetector.onTouchEvent(event);
	}

	private void updateBottomPanelView()
	{
		if(mPopupBottom.isShowing())
		{
			if(mPlayerView.isLiveStream())
			{
				mPanelBottomProgress.removeAllViews();

				for(int i =0; i < ProgramInfo.Me.ActiveChannel.mChannelStreamCollection.size(); i++)
					mPanelBottomProgress.addView(mChannelButtons[i]);
				mChannelInfoTextView.setText(">> " + ProgramInfo.Me.ActiveChannel.mName);
			}
			else
			{
				mPanelBottomProgress.removeAllViews();
				mPanelBottomProgress.addView(mCurrentPositionTextView);
				mPanelBottomProgress.addView(mVideoSeekbar);
				mPanelBottomProgress.addView(mDurationTextView);

				mDurationTextView.setText(getDisplayTime(mPlayerView.getDuration()));
				mCurrentPositionTextView.setText(getDisplayTime(mPlayerView.getCurrentPosition()));
				//mVideoProgress.run();
			}
			
			setPlayButtonImage();
		}
	}

	public void setProgress()
	{
		if(mPlayerView.isPlaying())
		{
			mVideoSeekbar.setProgress(mPlayerView.getCurrentPosition());
			mCurrentPositionTextView.setText(PlayerActivity.this.getDisplayTime(mPlayerView.getCurrentPosition()));
			mDurationTextView.setText(PlayerActivity.this.getDisplayTime(mPlayerView.getDuration()));
		}
		
		mTimeTextView.setText(DateFormat.getTimeInstance().format(new Date()));
	}

	private String getDisplayTime(int milliSeconds)
	{

		int seconds = (int) (milliSeconds / 1000) % 60 ;
		int minutes = (int) ((milliSeconds / (1000*60)) % 60);
		int hours   = (int) ((milliSeconds / (1000*60*60)) % 24);
		return String.format("%02d:%02d:%02d", hours, minutes, seconds); 
	}

	private void showHideControlPanels()
	{

		if(mPopupBottom.isShowing())
		{
			mPopupBottom.dismiss();
			mPopupTop.dismiss();
		}
		else
		{
			mPopupBottom.showAtLocation(mMainLayout, Gravity.BOTTOM | Gravity.LEFT, 0,0);
			mPopupBottom.update(0, 0, mMainLayout.getWidth(),  LayoutParams.WRAP_CONTENT);
			
			mPopupTop.showAtLocation(mMainLayout, Gravity.NO_GRAVITY, 0,0);
			mPopupTop.update(0, 0, mMainLayout.getWidth(),  LayoutParams.WRAP_CONTENT);
			
			updateBottomPanelView();
		}
		if(Build.VERSION.SDK_INT >= 11)
			BuildVersion11Helper.SetLowUIProfile(getWindow().getDecorView());
	}


	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onSingleTapUp(MotionEvent ev) 
		{
			showHideControlPanels();
			return true;
		}
		@Override
		public void onShowPress(MotionEvent ev)
		{
		}
		@Override
		public void onLongPress(MotionEvent ev)
		{
		}
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			return true;
		}
		@Override
		public boolean onDown(MotionEvent ev)
		{
			return true;
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			return true;
		}
	}

	private class ChannelClickListener implements OnClickListener
	{
		private int mIndex;

		public ChannelClickListener(int index)
		{
			mIndex = index;
		}

		public void onClick(View v)
		{
			play(ProgramInfo.Me.ActiveChannel.mChannelStreamCollection.get(mIndex));

		}
	}


	protected class VideoProgress extends TimerTask 
	{
		PlayerActivity context;
		Timer timer;

		public VideoProgress(PlayerActivity context, int milliSeconds) 
		{
			this.context = context;
			timer = new Timer();
			timer.schedule(this, milliSeconds, milliSeconds);
		}

		@Override
		public void run()
		{
			if(context == null || context.isFinishing()) 
			{
				this.cancel();
				return;
			}

			context.runOnUiThread(new Runnable()
			{
				public void run() 
				{
					context.setProgress();

				}
			});
		}
	}

	private class NetworkTask extends AsyncTask<String, String, YouTubeIdInfo> 
	{

		@Override
		protected YouTubeIdInfo doInBackground(String... params) {
			try
			{
				return null;//YouTubeUtility.getYouTubeVideoInfo(mActiveChannelStream.mStreamInfo);
			}
			catch(Exception ex)
			{
				Log.d("Dream TV", ex.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(YouTubeIdInfo result) {
			super.onPostExecute(result);

			//get the first
			if(result.mVideoInfoCollection.size() > 0)
			{
				YouTubeSingleVideoIdInfo ysvi = result.mVideoInfoCollection.get(0);
				mPlayerView.play(ysvi.getSuitableStream().getUrl(), 
						mActiveChannelStream.mChannel.mIsLive, mActiveChannelStream.mFullScreen);
			}
		}

	}

}
