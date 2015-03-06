package com.DreamTV;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class SeekBarEx extends SeekBar {

	public SeekBarEx(Context context) {
		super(context);
		init();
	}
	
	public SeekBarEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init()
	{
		setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
	}

}
