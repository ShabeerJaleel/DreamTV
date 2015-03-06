package com.DreamTV;

import com.DreamTV.ProgramInfo.Channel;
import com.DreamTV.ProgramInfo.CountryDefinition;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;


public class LiveTVActivity extends Activity
{
	private Spinner mCountrySelector;
	private ExpandableListAdapterEx mChannelInfoAdapter;
	private final boolean IsAdmin = true; 

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		initUI();
		readProgramInfo();
	}

	private ExpandableListView getChannelListView()
	{
		return (ExpandableListView) findViewById(R.id.expandableListView1);
	}

	private void initUI()
	{
		setContentView(R.layout.tvchannelselector);
		
		if(Build.VERSION.SDK_INT >= 11)
			mCountrySelector = BuildVersion11Helper.createSpinner(this);
		else
			mCountrySelector = new Spinner(this);
		
		mCountrySelector.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		mCountrySelector.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) 
			{
				CountryDefinition cd = (CountryDefinition) mCountrySelector.getSelectedItem();
				mChannelInfoAdapter.SetDataSource(cd);
				
				try
				{
				getChannelListView().expandGroup(mChannelInfoAdapter.mSelectedCountry.getChannelLanguageIndex(cd.mDefaultLanguage));
				}
				catch(Exception ex){}
			}

			public void onNothingSelected(AdapterView<?> arg0)
			{

			}

		});
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.channelSelectorSubLayout);
		layout.addView(mCountrySelector);

		getChannelListView().setOnChildClickListener(new OnChildClickListener() 
		{
			@SuppressWarnings("unused")
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id)
			{
				Channel selectedChannel = (Channel)((View)v).getTag();
				
				if(!selectedChannel.mEnabled && !IsAdmin)
				{
					Toast.makeText(getBaseContext(), "Channel is temporarily unavailable", Toast.LENGTH_SHORT).show();
					return false;
				}
				else
				{
					ProgramInfo.Me.ActiveChannel = selectedChannel; 
					Intent intent = new Intent().setClass(LiveTVActivity.this, PlayerActivity.class);
					startActivity(intent);
					return true;
				}
			}
		});
		
		getChannelListView().setChildDivider(new ColorDrawable(Color.YELLOW));
	}

	private void readProgramInfo()
	{
		try 
		{
			mChannelInfoAdapter = new ExpandableListAdapterEx(this, false);
			getChannelListView().setAdapter(mChannelInfoAdapter);

			ArrayAdapter<CountryDefinition> adapter = new ArrayAdapter<CountryDefinition>(this, android.R.layout.simple_spinner_item, 
					ProgramInfo.Me.mContryDefinitions);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			mCountrySelector.setAdapter(adapter);
			
			try
			{
			int index = ProgramInfo.Me.getCountryDefinitionIndex();
			if(index != -1)
				mCountrySelector.setSelection(index);
			}
			catch(Exception e)
			{
			}
			
		}
		catch(Exception ex)
		{
			Log.d("Dream TV", ex.toString());
		}

	}



	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Sample menu");
		menu.add(0, 0, 0, "test");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

		String title = ((TextView) info.targetView).getText().toString();

		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
			int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
			int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
			Toast.makeText(this, title + ": Child " + childPos + " clicked in group " + groupPos,
					Toast.LENGTH_SHORT).show();
			return true;
		} else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
			int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
			Toast.makeText(this, title + ": Group " + groupPos + " clicked", Toast.LENGTH_SHORT).show();
			return true;
		}

		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("Dream TV", "onPause() in LiveTVActivity");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("Dream TV", "onStop() in LiveTVActivity");
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("Dream TV", "onRestart() in LiveTVActivity");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("Dream TV", "onDestroy() in LiveTVActivity");
	}

	@Override
	public void onBackPressed() {
		android.os.Process.killProcess(android.os.Process.myPid());
		return;
	}

}
