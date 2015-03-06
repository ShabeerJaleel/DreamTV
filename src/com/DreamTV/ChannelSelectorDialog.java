package com.DreamTV;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import com.DreamTV.ProgramInfo.Channel;
import com.DreamTV.ProgramInfo.ChannelStream;
import com.DreamTV.ProgramInfo.CountryDefinition;
import com.DreamTV.ProgramInfo.LanguageDefinition;

import android.app.Dialog;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

public class ChannelSelectorDialog extends Dialog
{
	
	private ExpandableListAdapterEx mChannelInfoAdapter;
	private ListView mCountryList;
    private OnChannelSelectedListener mOnChannelSelectedListener;

	public ChannelSelectorDialog(Context context, int theme)
	{
		super(context, theme);
		init();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		dismiss();
		return super.onPrepareOptionsMenu(menu);
	}
	
	
	private ExpandableListView getChannelListView()
	{
	 return (ExpandableListView) findViewById(R.id.listViewChannel);
	}
	
	
	private void init()
	{
		setContentView(R.layout.channelselectordialog);
		mCountryList = (ListView) findViewById(R.id.listViewCountry);
		//mCountryList.setItemsCanFocus(false);
		mCountryList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		getChannelListView().setOnChildClickListener(new OnChildClickListener() 
		{
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id)
			{
				if(mOnChannelSelectedListener != null)
				{
					mOnChannelSelectedListener.onActiveChannelChanged((Channel)((View)v).getTag());
				}
				
				return true;
			}
		});
		
		ArrayAdapter<CountryDefinition> adapterCountry = new ArrayAdapter<CountryDefinition>(
				getContext(), android.R.layout.simple_list_item_1,
				ProgramInfo.Me.mContryDefinitions);
		
		
		
		mCountryList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mChannelInfoAdapter.SetDataSource(ProgramInfo.Me.mContryDefinitions.get(arg2));
			}
		});
		
		mCountryList.setAdapter(adapterCountry);
		
		mChannelInfoAdapter = new ExpandableListAdapterEx(getContext(), true);
		getChannelListView().setAdapter(mChannelInfoAdapter);
		mChannelInfoAdapter.SetDataSource(ProgramInfo.Me.mContryDefinitions.get(0));
	}
	
	public interface OnChannelSelectedListener extends EventListener {
	    public void onActiveChannelChanged(Channel selectedChannel);
	}
	
	public void setOnChannelSelectedListener(OnChannelSelectedListener listener){
		mOnChannelSelectedListener = listener;
	}
	
	public void removeOnChannelSelectedListener()
	{
		mOnChannelSelectedListener = null;
	}
	

}
