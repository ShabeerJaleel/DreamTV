package com.DreamTV;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.DreamTV.ProgramInfo.Channel;
import com.DreamTV.ProgramInfo.Country;
import com.DreamTV.ProgramInfo.CountryDefinition;
import com.DreamTV.ProgramInfo.Language;


public class ExpandableListAdapterEx extends BaseExpandableListAdapter 
{
	private  List<DisplayItem> mDisplayItems = new ArrayList<DisplayItem>();
	private Context mContext;
	private Boolean mHighlightColor;
	public Country mSelectedCountry;

	public ExpandableListAdapterEx(Context context, Boolean highlightColor)
	{
		mContext = context;
		mHighlightColor = highlightColor;

	}

	public void SetDataSource(CountryDefinition cd)
	{
		for(int i = 0; i < ProgramInfo.Me.mLiveTv.mChannelCountryCollection.size(); i++)
		{
			Country c = ProgramInfo.Me.mLiveTv.mChannelCountryCollection.get(i);
			
			if(c.mCountryDefinition == cd)
			{
				mSelectedCountry = c;
				break;
			}
		}
		
		mDisplayItems.clear();
		List<Language> channelLangs = mSelectedCountry.mChannelLanguageCollection;
		for(int i = 0; i < channelLangs.size(); i++)
		{
			Language language = channelLangs.get(i);
			DisplayItem item = new DisplayItem(language);
			mDisplayItems.add(item);

			for(int j = 0; j < language.mChannelCollection.size();j++)
				item.mChannels.add(language.mChannelCollection.get(j));
		}

		notifyDataSetChanged();
	}

	public Channel getChild(int groupPosition, int childPosition)
	{
		return mDisplayItems.get(groupPosition).mChannels.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	public int getChildrenCount(int groupPosition)
	{			
		return this.mDisplayItems.get(groupPosition).mChannels.size();
	}

	public TextView getGenericView() 
	{
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 80);

		TextView textView = new TextView(mContext);
		textView.setLayoutParams(lp);
		// Center the text vertically
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		// Set the text starting position
		textView.setPadding(80, 0, 0, 0);
		
		textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD), Typeface.BOLD);
		textView.setTextColor(Color.WHITE);
		textView.setBackgroundColor(0xff776F68);
		//textView.setBackgroundColor(Color.GRAY);
		return textView;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) 
	{

		Channel channel = getChild(groupPosition, childPosition);
		ChannelItemView channelItemView = null;
		
		if(convertView == null)
		{
			channelItemView = new ChannelItemView(mContext);
			if(mHighlightColor)
				channelItemView.setTextColor(Color.WHITE);
	
		}
		else
			channelItemView = (ChannelItemView)convertView;
		
		channelItemView.setBackgroundColor(Color.DKGRAY);
		channelItemView.setText(channel);
		channelItemView.setTag(channel);
		return channelItemView;
	}

	public DisplayItem getGroup(int groupPosition)
	{
		return this.mDisplayItems.get(groupPosition);
	}

	public int getGroupCount() 
	{
		return this.mDisplayItems.size();
	}

	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) 
	{
		TextView textView = null;
		if(convertView == null)
			textView = getGenericView();
		else
			textView = (TextView)convertView;

		
		textView.setText(getGroup(groupPosition).mLanguage.mLangDefinitions.mName.toUpperCase());
		return textView;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}
	
}
