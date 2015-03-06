package com.DreamTV;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

public class PopupWindowEx extends PopupWindow {
	
	public PopupWindowEx(Context context, View content)
	{
		//super(content,LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, false);
		super(context);
		setContentView(content);
		setHeight(LayoutParams.WRAP_CONTENT);
		setWidth(LayoutParams.MATCH_PARENT);
		setAnimationStyle(android.R.style.Animation_Dialog);
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(false);
	}
}
