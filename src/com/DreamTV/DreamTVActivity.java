package com.DreamTV;



import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class DreamTVActivity extends TabActivity  {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ProgramInfo.init(this);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost();  // The activity TabHost
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		Intent intent;  // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, LiveTVActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("livetv").setIndicator(ProgramInfo.Me.mLiveTv.toString(),
				res.getDrawable(R.drawable.ic_tab_livetv))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, LiveRadioActivity.class);
		spec = tabHost.newTabSpec("liveradio").setIndicator("Radio",
				res.getDrawable(R.drawable.ic_tab_liveradio))
				.setContent(intent);
		tabHost.addTab(spec);
				
//		// Do the same for the other tabs
//		intent = new Intent().setClass(this, MoviesActivity.class);
//		spec = tabHost.newTabSpec("movie").setIndicator("Movies",
//				res.getDrawable(R.drawable.ic_tab_movies))
//				.setContent(intent);
//		tabHost.addTab(spec);
		
		// Do the same for the other tabs
		intent = new Intent().setClass(this, FavouriteActivity.class);
		spec = tabHost.newTabSpec("favourite").setIndicator("Fav",
				res.getDrawable(R.drawable.ic_tab_movies))
				.setContent(intent);
		tabHost.addTab(spec);		

		//tabHost.setCurrentTab(2);
	}
	
	
}