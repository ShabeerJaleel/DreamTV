package com.DreamTV;

import java.util.ArrayList;
import java.util.List;

import com.DreamTV.ProgramInfo.Channel;
import com.DreamTV.ProgramInfo.LanguageDefinition;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class LiveRadioActivity extends Activity {

	private Spinner mLanguageSelector;
	private LinearLayout mMainLayout;
	private LinearLayout mPlayControlsLayout;
	private ListView mMoviesListView;
	private RadioAdapter mRadioAdapter;
	private ImageButton mPlayButton;
	private MediaPlayerEx mMediaPlayer;
	private Channel mActiveChannel;
	private TextView mInfoTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		initUI();
	}

	private void initUI()
	{

		mMainLayout = new LinearLayout(this);
		mMainLayout.setBackgroundColor(Color.BLACK);
		LinearLayout.LayoutParams lRelLayoutParms = new android.widget.LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		mMainLayout.setLayoutParams(lRelLayoutParms);
		mMainLayout.setOrientation(LinearLayout.VERTICAL);
		
		if(Build.VERSION.SDK_INT >= 11)
			mLanguageSelector = BuildVersion11Helper.createSpinner(this);
		else
			mLanguageSelector = new Spinner(this);
		
		mLanguageSelector.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		mLanguageSelector.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) 
			{
				//mChannelInfoAdapter.SetDataSource((LanguageDefinition) mLanguageSelector.getSelectedItem());
				
				//find the language
				mRadioAdapter.SetDataSource(ProgramInfo.Me,
						(LanguageDefinition) mLanguageSelector.getSelectedItem());
			}

			public void onNothingSelected(AdapterView<?> arg0)
			{

			}

		});
		
		ArrayAdapter<LanguageDefinition> adapterLanguage = new ArrayAdapter<LanguageDefinition>(
				this, android.R.layout.simple_spinner_item,
				filterLanguage());
		adapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mLanguageSelector.setAdapter(adapterLanguage);
		
		mMoviesListView = new ListView(this);
		mMoviesListView.setBackgroundColor(Color.BLACK);
		lRelLayoutParms = new android.widget.LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.FILL);
		mMoviesListView.setLayoutParams(lRelLayoutParms);
		mRadioAdapter = new RadioAdapter();
		mMoviesListView.setAdapter(mRadioAdapter);
		mMoviesListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				play((Channel)((View)arg1).getTag());
			}
		});
		
		createPlayControls();
		
		mMainLayout.addView(mLanguageSelector);
		mMainLayout.addView(mMoviesListView);
		mMainLayout.addView(mPlayControlsLayout);
		
		setContentView(mMainLayout);

	}
	
	private void play(Channel channel)
	{
		mActiveChannel = channel;
		mMediaPlayer.play(channel.mChannelStreamCollection.get(0));
		mPlayButton.setEnabled(false);
		mInfoTextView.setText("Connecting to " + mActiveChannel.mName +"...");
	}
	
	private void createPlayControls()
	{
		mMediaPlayer = new MediaPlayerEx();
		
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() 
		{
			public void onPrepared(MediaPlayer mp) 
			{
				((MediaPlayerEx)mp).start();
				setPlayButtonImage();
				mPlayButton.setEnabled(true);
			}
		});
		
		mMediaPlayer.setOnErrorListener(new OnErrorListener() 
		{
			
			public boolean onError(MediaPlayer mp, int what, int extra)
			{
				Toast.makeText(getBaseContext(), "Couldn't connect to station", 
						Toast.LENGTH_SHORT).show(); 
				setPlayButtonImage();
				mPlayButton.setEnabled(true);
				return false;
			}
		});
		
		mMediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener()
		{
			public void onBufferingUpdate(MediaPlayer mp, int percent)
			{
				//mInfoTextView.setText("Buffering (" + Integer.toString(percent) +"%)...");
			}
		});
		
		
		mPlayControlsLayout = new LinearLayout(this);
		mPlayControlsLayout.setBackgroundColor(Color.BLACK);
		LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mPlayControlsLayout.setLayoutParams(lp);
		mPlayControlsLayout.setOrientation(LinearLayout.VERTICAL); 
		
		mPlayButton = new ImageButton(this);
		mPlayButton.setBackgroundColor(Color.TRANSPARENT);
		lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mPlayButton.setLayoutParams(lp);
		mPlayButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(mActiveChannel == null)
				{
					Toast.makeText(getBaseContext(), "Select a station", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(mMediaPlayer.isPlaying())
					mMediaPlayer.stop();
				else
					play(mActiveChannel);
				setPlayButtonImage();
			}
		});
		

		mInfoTextView = new TextView(this);
		lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mInfoTextView.setLayoutParams(lp);
		mInfoTextView.setTextAppearance(this, android.R.attr.textAppearanceSmall);
		mInfoTextView.setTextColor(Color.WHITE);
		mInfoTextView.setBackgroundColor(Color.BLUE);
		

		mPlayControlsLayout.addView(mPlayButton);
		mPlayControlsLayout.addView(mInfoTextView);
		setPlayButtonImage();
	}
	
	private List<LanguageDefinition> filterLanguage()
	{
		List<LanguageDefinition> languageDefinitions = new ArrayList<ProgramInfo.LanguageDefinition>();
		for(int i =0; i < ProgramInfo.Me.mLanguageDefinitions.size(); i++)
		{
			for(int j = 0; j < ProgramInfo.Me.mLiveRadio.mRadioLanguageCollection.size(); j++)
			{
				if(ProgramInfo.Me.mLiveRadio.mRadioLanguageCollection.get(j).mLangDefinitions.mId
						== ProgramInfo.Me.mLanguageDefinitions.get(i).mId)
				{
					languageDefinitions.add(ProgramInfo.Me.mLanguageDefinitions.get(i));
					break;
				}
			}
		}
		return languageDefinitions;
	}

	
	private void setPlayButtonImage()
	{
		if(mMediaPlayer.isPlaying())
		{
			mPlayButton.setImageResource(R.drawable.ic_button_stop);
			mInfoTextView.setText("Now playing >> " + mActiveChannel.mName);
		}
		else
		{
			mPlayButton.setImageResource(R.drawable.ic_button_play);
			mInfoTextView.setText("Now playing >> ");
		}
		
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		mMediaPlayer.stop();
		mMediaPlayer.release();
	}
	
	private class RadioAdapter extends BaseAdapter
	{
		private List<Channel> mChannelCollection = new ArrayList<ProgramInfo.Channel>();;
		
		public void SetDataSource(ProgramInfo programInfo, LanguageDefinition ld)
		{
			mChannelCollection = new ArrayList<ProgramInfo.Channel>();
			for(int i = 0; i < ProgramInfo.Me.mLiveRadio.mRadioLanguageCollection.size(); i++)
			{
				if(ld ==  ProgramInfo.Me.mLiveRadio.mRadioLanguageCollection.get(i).mLangDefinitions)
				{
					mChannelCollection =  ProgramInfo.Me.mLiveRadio.mRadioLanguageCollection.get(i).mChannelCollection;
					notifyDataSetChanged();
					break;
				}
			}
		}

		public int getCount() {
			return mChannelCollection.size();
		}

		public Channel getItem(int position) {
			return mChannelCollection.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{

			ChannelItemView channelItemView = null;
			
			if(convertView == null)
				channelItemView = new ChannelItemView(getBaseContext());
			else
				channelItemView = (ChannelItemView)convertView;
			
			Channel channel = getItem(position); 
			channelItemView.setText(channel);
			channelItemView.setTag(channel);
			return channelItemView;
		}
	}
}
