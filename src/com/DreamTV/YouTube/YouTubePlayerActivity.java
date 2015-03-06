//package com.DreamTV.YouTube;
//import java.util.Queue;
//
//import com.DreamTV.PlayerView;
//import com.DreamTV.RtmpPlayerView;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.net.Uri;
//import android.net.wifi.WifiManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.util.MonthDisplayHelper;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.LinearLayout;
//import android.widget.MediaController;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.VideoView;
//import android.widget.LinearLayout.LayoutParams;
//
///**
// * <p>Activity that will play a video from YouTube.  A specific video or the latest video in a YouTube playlist 
// * can be specified in the intent used to invoke this activity.  The data of the intent can be set to a 
// * specific video by using an Intent data URL of the form:</p>
// * 
// * <pre>
// *     ytv://videoid
// * </pre>  
// *    
// * <p>where <pre>videoid</pre> is the ID of the YouTube video to be played.</p>
// * 
// * <p>If the user wishes to play the latest video in a YouTube playlist, the Intent data URL should be of the 
// * form:</p>
// * 
// * <pre>
// *     ytpl://playlistid
// * </pre>
// * 
// * <p>where <pre>playlistid</pre> is the ID of the YouTube playlist from which the latest video is to be played.</p>
// * 
// * <p>Code used to invoke this intent should look something like the following:</p>
// * 
// * <pre>
// *      Intent lVideoIntent = new Intent(null, Uri.parse("ytpl://"+YOUTUBE_PLAYLIST_ID), this, OpenYouTubePlayerActivity.class);
// *              startActivity(lVideoIntent);
// * </pre>
// * 
// * <p>There are several messages that are displayed to the user during various phases of the video load process.  If 
// * you wish to supply text other than the default english messages (e.g., internationalization, etc.), you can pass 
// * the text to be used via the Intent's extended data.  The messages that can be customized include:
// * 
// * <ul>
// *   <li>com.keyes.video.msg.init        - activity is initializing.</li>
// *   <li>com.keyes.video.msg.detect      - detecting the bandwidth available to download video.</li>
// *   <li>com.keyes.video.msg.playlist    - getting latest video from playlist.</li>
// *   <li>com.keyes.video.msg.token       - retrieving token from YouTube.</li>
// *   <li>com.keyes.video.msg.loband      - buffering low-bandwidth.</li>
// *   <li>com.keyes.video.msg.hiband      - buffering hi-bandwidth.</li>
// *   <li>com.keyes.video.msg.error.title - dialog title displayed if anything goes wrong.</li>
// *   <li>com.keyes.video.msg.error.msg   - message displayed if anything goes wrong.</li>
// * </ul>
// * 
// * <p>For example:</p>
// * 
// * <pre>
// *      Intent lVideoIntent = new Intent(null, Uri.parse("ytpl://"+YOUTUBE_PLAYLIST_ID), this, OpenYouTubePlayerActivity.class);
// *      lVideoIntent.putExtra("com.keyes.video.msg.init", getString("str_video_intro"));
// *      lVideoIntent.putExtra("com.keyes.video.msg.detect", getString("str_video_detecting_bandwidth"));
// *      ...
// *      startActivity(lVideoIntent);
// * </pre>
// * 
// * @author David Keyes
// *
// */
//public class YouTubePlayerActivity extends PlayerView {
//        
//     
//		public static final String SCHEME_YOUTUBE_VIDEO = "ytv";
//        public static final String SCHEME_YOUTUBE_PLAYLIST = "ytpl";
//        
//        static final String YOUTUBE_VIDEO_INFORMATION_URL = "http://www.youtube.com/get_video_info?&video_id=";
//        static final String YOUTUBE_PLAYLIST_ATOM_FEED_URL = "http://gdata.youtube.com/feeds/api/playlists/";
////        
////        protected ProgressBar mProgressBar;
////        protected TextView    mProgressMessage;
//        protected VideoView   mVideoView;
//        
//        public final static String MSG_INIT = "com.keyes.video.msg.init";
//        protected String      mMsgInit       = "Initializing";
//        
//        public final static String MSG_DETECT = "com.keyes.video.msg.detect";
//        protected String      mMsgDetect     = "Detecting Bandwidth";
//
//        public final static String MSG_PLAYLIST = "com.keyes.video.msg.playlist";
//        protected String      mMsgPlaylist   = "Determining Latest Video in YouTube Playlist";
//
//        public final static String MSG_TOKEN = "com.keyes.video.msg.token";
//        protected String      mMsgToken      = "Retrieving YouTube Video Token";
//        
//        public final static String MSG_LO_BAND = "com.keyes.video.msg.loband";
//        protected String      mMsgLowBand    = "Buffering Low-bandwidth Video";
//        
//        public final static String MSG_HI_BAND = "com.keyes.video.msg.hiband";
//        protected String      mMsgHiBand     = "Buffering High-bandwidth Video";
//        
//        public final static String MSG_ERROR_TITLE = "com.keyes.video.msg.error.title";
//        protected String      mMsgErrorTitle = "Communications Error";
//        
//        public final static String MSG_ERROR_MSG = "com.keyes.video.msg.error.msg";
//        protected String      mMsgError      = "An error occurred during the retrieval of the video.  This could be due to network issues or YouTube protocols.  Please try again later.";
//        
//        /** Background task on which all of the interaction with YouTube is done */
//        protected QueryYouTubeTask mQueryYouTubeTask;
//        
//        protected String mVideoId = null;
//        
//        public YouTubePlayerActivity(Context context) 
//        {
//        	super(context);
//        	
//        	// create the layout of the view
//        	setupView();
//        
//           // determine the messages to be displayed as the view loads the video
//        	extractMessages();
//        }
//        
//        
//        @Override
//        public Boolean play(String streamInfo) 
//        {
//        	 mVideoView.setVideoURI(Uri.parse(streamInfo));
//           
//        	 mVideoView.setOnCompletionListener(new OnCompletionListener()
//        	 {
//                     public void onCompletion(MediaPlayer pMp) 
//                     {
//                     }
//        	 });
//         
//             final MediaController lMediaController = new MediaController(YouTubePlayerActivity.this.getContext());
//             mVideoView.setMediaController(lMediaController);
//             lMediaController.show(0);
//             //mVideoView.setKeepScreenOn(true);
//             mVideoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener() 
//             {
//                     public void onPrepared(MediaPlayer pMp) 
//                     {
//                             YouTubePlayerActivity.this.notifyOnStreamLoaded();
//                     }
//             
//             });
//         
//             mVideoView.requestFocus();
//             mVideoView.start();
//             return true;
//        }
//
//        /**
//         * Determine the messages to display during video load and initialization. 
//         */
//        private void extractMessages() 
//        {
//
////        	Intent lInvokingIntent = getIntent();
////        	String lMsgInit = lInvokingIntent.getStringExtra(MSG_INIT);
////        	if(lMsgInit != null){
////        		mMsgInit = lMsgInit;
////        	}
////        	String lMsgDetect = lInvokingIntent.getStringExtra(MSG_DETECT);
////        	if(lMsgDetect != null){
////        		mMsgDetect = lMsgDetect;
////        	}
////        	String lMsgPlaylist = lInvokingIntent.getStringExtra(MSG_PLAYLIST);
////        	if(lMsgPlaylist != null){
////        		mMsgPlaylist = lMsgPlaylist;
////        	}
////        	String lMsgToken = lInvokingIntent.getStringExtra(MSG_TOKEN);
////        	if(lMsgToken != null){
////        		mMsgToken = lMsgToken;
////        	}
////        	String lMsgLoBand = lInvokingIntent.getStringExtra(MSG_LO_BAND);
////        	if(lMsgLoBand != null){
////        		mMsgLowBand = lMsgLoBand;
////        	}
////        	String lMsgHiBand = lInvokingIntent.getStringExtra(MSG_HI_BAND);
////        	if(lMsgHiBand != null){
////        		mMsgHiBand = lMsgHiBand;
////        	}
////        	String lMsgErrTitle = lInvokingIntent.getStringExtra(MSG_ERROR_TITLE);
////        	if(lMsgErrTitle != null){
////        		mMsgErrorTitle = lMsgErrTitle;
////        	}
////        	String lMsgErrMsg = lInvokingIntent.getStringExtra(MSG_ERROR_MSG);
////        	if(lMsgErrMsg != null){
////        		mMsgError = lMsgErrMsg;
////        	}
//        }
//
//        /**
//         * Create the view in which the video will be rendered.
//         */
//        private void setupView() 
//        {
//            
//            mVideoView = new VideoView(getContext());
//            mVideoView.setId(3);
//            LinearLayout.LayoutParams lVidViewLayoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
//            mVideoView.setLayoutParams(lVidViewLayoutParams);
//            addView(mVideoView);
//        }
//        
//        @Override
//        public void stop() 
//        {
//            super.stop();
//            //YouTubeUtility.markVideoAsViewed(getContext(), mVideoId);
//            
//            if(mQueryYouTubeTask != null)
//            	mQueryYouTubeTask.cancel(true);
//            
//            
//            if(mVideoView != null)
//            	mVideoView.stopPlayback();
//        }
//        
//        @Override
//        public void destroy() 
//        {
//        	super.destroy();
//        	stop();
//        	this.mQueryYouTubeTask = null;
//            this.mVideoView = null;
//        }
//
//        
//        public void updateProgress(String pProgressMsg){
//                try {
//                      //  mProgressMessage.setText(pProgressMsg);
//                } catch(Exception e) {
//                        Log.e(this.getClass().getSimpleName(), "Error updating video status!", e);
//                }
//        }
//        
//        private class ProgressUpdateInfo {
//        
//                public String mMsg;
//        
//                public ProgressUpdateInfo(String pMsg){
//                        mMsg = pMsg;
//                }
//        }
//        
//        public void notifyOnStreamLoaded()
//        {
//        	if(mOnPlayerEventsListener != null)
//        		mOnPlayerEventsListener.onStreamLoaded();
//        }
//        
//        /**
//         * Task to figure out details by calling out to YouTube GData API.  We only use public methods that
//         * don't require authentication.
//         * 
//         */
//        private class QueryYouTubeTask extends AsyncTask<YouTubeId, ProgressUpdateInfo, Uri> 
//        {
//
//                private boolean mShowedError = false;
//               
//                @Override
//                protected Uri doInBackground(YouTubeId... pParams) {
//                        String lUriStr = null;
//                        String lYouTubeFmtQuality = "17";   // 3gpp medium quality, which should be fast enough to view over EDGE connection
//                        String lYouTubeVideoId = null;
//                        
//                        if(isCancelled())
//                                return null;
//                        
//                        try {
//                        
//                                publishProgress(new ProgressUpdateInfo(mMsgDetect));
//                                
//                                WifiManager lWifiManager = (WifiManager) YouTubePlayerActivity.this.getContext().getSystemService(Context.WIFI_SERVICE);
//                                TelephonyManager lTelephonyManager = (TelephonyManager) YouTubePlayerActivity.this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
//                                
//                                ////////////////////////////
//                                // if we have a fast connection (wifi or 3g), then we'll get a high quality YouTube video
//                                if( (lWifiManager.isWifiEnabled() && lWifiManager.getConnectionInfo() != null && lWifiManager.getConnectionInfo().getIpAddress() != 0) ||
//                                        ( (lTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS ||
//                                                        
//                                           /* icky... using literals to make backwards compatible with 1.5 and 1.6 */           
//                                           lTelephonyManager.getNetworkType() == 9 /*HSUPA*/  ||
//                                           lTelephonyManager.getNetworkType() == 10 /*HSPA*/  ||
//                                           lTelephonyManager.getNetworkType() == 8 /*HSDPA*/  ||
//                                           lTelephonyManager.getNetworkType() == 5 /*EVDO_0*/  ||
//                                           lTelephonyManager.getNetworkType() == 6 /*EVDO A*/) 
//                                          
//                                         && lTelephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) 
//                                   ){
//                                        lYouTubeFmtQuality = "18";
//                                }
//                                
//
//                                ///////////////////////////////////
//                                // if the intent is to show a playlist, get the latest video id from the playlist, otherwise the video
//                                // id was explicitly declared.
//                                if(pParams[0] instanceof PlaylistId){
//                                        publishProgress(new ProgressUpdateInfo(mMsgPlaylist));
//                                        lYouTubeVideoId = YouTubeUtility.queryLatestPlaylistVideo((PlaylistId) pParams[0]);
//                                        //String results[] = YouTubeUtility.queryVideoIdListFromPlayList((PlaylistId) pParams[0]);
//                                        //lYouTubeVideoId = results[0];
//                                }
//                                
//                                else if(pParams[0] instanceof VideoId){
//                                        lYouTubeVideoId = pParams[0].getId();
//                                }
//                                
//                                mVideoId = lYouTubeVideoId;
//                                
//                                publishProgress(new ProgressUpdateInfo(mMsgToken));
//                                
//                                if(isCancelled())
//                                        return null;
//
//                                ////////////////////////////////////
//                                // calculate the actual URL of the video, encoded with proper YouTube token
//                                lUriStr = YouTubeUtility.calculateYouTubeUrl(lYouTubeFmtQuality, true, lYouTubeVideoId);
//                                
//                                if(isCancelled())
//                                        return null;
//        
//                                if(lYouTubeFmtQuality.equals("17")){
//                                        publishProgress(new ProgressUpdateInfo(mMsgLowBand));
//                                } else {
//                                        publishProgress(new ProgressUpdateInfo(mMsgHiBand));
//                                }
//        
//                        } catch(Exception e) {
//                                Log.e(this.getClass().getSimpleName(), "Error occurred while retrieving information from YouTube.", e);
//                        }
//
//                        if(lUriStr != null){
//                                return Uri.parse(lUriStr);
//                        } else {
//                                return null;
//                        }
//                }
//
//
//
//                @Override
//                protected void onPostExecute(Uri pResult) {
//                        super.onPostExecute(pResult);
//                        
//                        try {
//                                if(isCancelled())
//                                        return;
//                                
//                                if(pResult == null){
//                                        throw new RuntimeException("Invalid NULL Url.");
//                                }
//                                
//                                Log.d("Dream TV", pResult.toString());
//                                mVideoView.setVideoURI(pResult);
//                            
//                            
//                                if(isCancelled())
//                                        return;
//                            
//                            // TODO:  add listeners for finish of video
//                            mVideoView.setOnCompletionListener(new OnCompletionListener(){
//
//                                        public void onCompletion(MediaPlayer pMp) {
//                                                if(isCancelled())
//                                                        return;
//                                                //OpenYouTubePlayerActivity.this.finish();
//                                        }
//                                
//                            });
//                            
//                                if(isCancelled())
//                                        return;
//        
//                                final MediaController lMediaController = new MediaController(YouTubePlayerActivity.this.getContext());
//                                mVideoView.setMediaController(lMediaController);
//                                lMediaController.show(0);
//                                //mVideoView.setKeepScreenOn(true);
//                                mVideoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
//
//                                        public void onPrepared(MediaPlayer pMp) 
//                                        {
//                                                if(isCancelled())
//                                                        return;
//                                                YouTubePlayerActivity.this.notifyOnStreamLoaded();
//                                        }
//                                
//                            });
//                            
//                                if(isCancelled())
//                                        return;
//        
//                                mVideoView.requestFocus();
//                                mVideoView.start();
//                        } catch(Exception e){
//                                Log.e(this.getClass().getSimpleName(), "Error playing video!", e);
//                                
//                                if(!mShowedError){
//                                        showErrorAlert();
//                                }
//                        }
//                }
//
//                private void showErrorAlert() {
//                        
//                        try {
//                                Builder lBuilder = new AlertDialog.Builder(YouTubePlayerActivity.this.getContext());
//                                lBuilder.setTitle(mMsgErrorTitle);
//                                lBuilder.setCancelable(false);
//                                lBuilder.setMessage(mMsgError);
//        
//                                lBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
//        
//                                       
//                                        public void onClick(DialogInterface pDialog, int pWhich) {
//                                               // OpenYouTubePlayerActivity.this.finish();
//                                        }
//                                        
//                                });
//        
//                                AlertDialog lDialog = lBuilder.create();
//                                lDialog.show();
//                        } catch(Exception e){
//                                Log.e(this.getClass().getSimpleName(), "Problem showing error dialog.", e);
//                        }
//                }
//
//                @Override
//                protected void onProgressUpdate(ProgressUpdateInfo... pValues) {
//                        super.onProgressUpdate(pValues);
//                        
//                        YouTubePlayerActivity.this.updateProgress(pValues[0].mMsg);
//                }
//                
//        }
//}
