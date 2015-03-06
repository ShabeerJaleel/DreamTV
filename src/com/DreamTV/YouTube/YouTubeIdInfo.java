package com.DreamTV.YouTube;

import java.util.ArrayList;
import java.util.List;

public class YouTubeIdInfo 
{
	public YouTubeId mYouTubeId;
	public List<YouTubeSingleVideoIdInfo> mVideoInfoCollection = new ArrayList<YouTubeIdInfo.YouTubeSingleVideoIdInfo>();
	
	
	public YouTubeIdInfo( YouTubeId id)
	{
		mYouTubeId = id;
	}
	
	public Boolean isPlayList()
	{
		return mYouTubeId.getClass() == PlaylistId.class;
	}
	
	public class YouTubeSingleVideoIdInfo
	{
		public String mVideoId;
		public List<VideoStreamFormatPair> mStreamFormatCollection = new ArrayList<YouTubeIdInfo.VideoStreamFormatPair>();
		
		public YouTubeSingleVideoIdInfo(String videoId)
		{
			mVideoId = videoId;
		}
		
		public Boolean isOnlyFlashVersionAvailable()
		{
			for(int i = 0; i < mStreamFormatCollection.size();i++)
			{
				Format format = mStreamFormatCollection.get(i).mFormat;
				if(format.getId() != 5 && format.getId() != 6 && format.getId() != 34 && format.getId() != 35)
					return false;
			}
			
			return true;
		}
		
		public Boolean isFlashVersionAvailable()
		{
			for(int i = 0; i < mStreamFormatCollection.size();i++)
			{
				Format format = mStreamFormatCollection.get(i).mFormat;
				if(format.getId() == 5 || format.getId() == 6 || format.getId() == 34 || format.getId() == 35)
					return true;
			}
			
			return false;
		}
		
		public VideoStream getFlashVersion()
		{
			for(int i = 0; i < mStreamFormatCollection.size();i++)
			{
				Format format = mStreamFormatCollection.get(i).mFormat;
				if(format.getId() == 5 || format.getId() == 6 || format.getId() == 34 || format.getId() == 35)
					return mStreamFormatCollection.get(i).mUrl;
			}
			
			return null;
		}
		
		public VideoStream getSuitableStream()
		{
			int orderedFmtList []  = {17,18,22,37,5,6,34,35};
			for(int i = 0; i <orderedFmtList.length;i++)
			{
				int format = orderedFmtList[i];
				for(int j = 0; j < mStreamFormatCollection.size();j++)
				{
					if(mStreamFormatCollection.get(j).mFormat.getId() == format)
						return mStreamFormatCollection.get(j).mUrl;
				}
			}
			
			return null;
		}
	}
	
	public class VideoStreamFormatPair
	{
		public Format mFormat;
		public VideoStream mUrl;
		
		public VideoStreamFormatPair( Format format, VideoStream url)
		{
			mFormat = format;
			mUrl = url;
		}
	
	}

}
