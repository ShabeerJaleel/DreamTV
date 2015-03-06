package com.DreamTV;

import java.util.ArrayList;
import java.util.List;

import com.DreamTV.ProgramInfo.Channel;
import com.DreamTV.ProgramInfo.LanguageDefinition;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MoviesActivity extends Activity {

	private Spinner mLanguageSelector;
	private LinearLayout mMainLayout;
	private ListView mMoviesListView;
	private MoviesAdapter mMoviesAdapter;

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
				mMoviesAdapter.SetDataSource(ProgramInfo.Me, (LanguageDefinition) mLanguageSelector.getSelectedItem());
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
		mMoviesAdapter = new MoviesAdapter();
		mMoviesListView.setAdapter(mMoviesAdapter);
		mMoviesListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ProgramInfo.Me.ActiveChannel = (Channel)((View)arg1).getTag();
				Intent intent = new Intent().setClass(MoviesActivity.this, PlayerActivity.class);
				startActivity(intent);
			}
		});
		
		mMainLayout.addView(mLanguageSelector);
		mMainLayout.addView(mMoviesListView);
		setContentView(mMainLayout);

	}
	
	private List<LanguageDefinition> filterLanguage()
	{
		List<LanguageDefinition> languageDefinitions = new ArrayList<ProgramInfo.LanguageDefinition>();
		for(int i =0; i < ProgramInfo.Me.mLanguageDefinitions.size(); i++)
		{
			for(int j = 0; j < ProgramInfo.Me.mMovies.mMovieLanguageCollection.size(); j++)
			{
				if(ProgramInfo.Me.mMovies.mMovieLanguageCollection.get(j).mLangDefinitions.mId
						== ProgramInfo.Me.mLanguageDefinitions.get(i).mId)
				{
					languageDefinitions.add(ProgramInfo.Me.mLanguageDefinitions.get(i));
					break;
				}
			}
		}
		return languageDefinitions;
	}

	



	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
		return;
	}
	
	private class MoviesAdapter extends BaseAdapter
	{
		private List<Channel> mChannelCollection = new ArrayList<ProgramInfo.Channel>();;
		
		public void SetDataSource(ProgramInfo programInfo, LanguageDefinition ld)
		{
			mChannelCollection = new ArrayList<ProgramInfo.Channel>();
			for(int i = 0; i < ProgramInfo.Me.mMovies.mMovieLanguageCollection.size(); i++)
			{
				if(ld ==  ProgramInfo.Me.mMovies.mMovieLanguageCollection.get(i).mLangDefinitions)
				{
					mChannelCollection =  ProgramInfo.Me.mMovies.mMovieLanguageCollection.get(i).mChannelCollection;
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
			//channelItemView.setText(channel.mName, channel.mLanguage.mLangDefinitions.mName);
			channelItemView.setTag(channel);
			return channelItemView;
		}
	}

}
