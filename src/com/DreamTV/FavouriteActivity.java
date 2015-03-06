package com.DreamTV;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FavouriteActivity extends Activity {
	
	private LinearLayout mMainLayout;
	
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
		
		setContentView(mMainLayout);
	}
	
		
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}
	
}