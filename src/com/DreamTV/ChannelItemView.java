package com.DreamTV;

import com.DreamTV.ProgramInfo.Channel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;



public class ChannelItemView extends TextView
{
	private boolean previousChannelEnabled = true;

	public ChannelItemView(Context context)
	{
		super(context);
		setGravity(Gravity.BOTTOM);
		setTextColor(Color.WHITE);
		setPadding(0,10, 10, 0);
		setImage(R.drawable.ic_button_play);
	}
	
	public void setText(Channel channel)
	{
		if(channel.mEnabled)
		{
			setText(Html.fromHtml("<h4>" + channel.mName + "</h4>" +  
					"<small>" + channel.mLanguage.mLangDefinitions.mName + "</small>" + "<br />"));
			setTextColor(Color.WHITE);
			if(!previousChannelEnabled)
			{
				setImage(R.drawable.ic_button_play);
				previousChannelEnabled = true;
			}
		}
		else
		{
			setText(Html.fromHtml("<h4><i>" + channel.mName + "<i></h4>" +  
					"<small>" + channel.mLanguage.mLangDefinitions.mName + "</small>" + "<br />"));
			setTextColor(Color.GRAY);
			if(previousChannelEnabled)
			{
				setImage(R.drawable.ic_button_lock);
				previousChannelEnabled = false;
			}
			
		}
	}
	
	private void setImage(int id)
	{
		Drawable img = getContext().getResources().getDrawable(id);
		setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		setCompoundDrawablePadding(20);
	}
	
}
