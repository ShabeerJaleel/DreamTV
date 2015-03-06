package com.DreamTV;

import com.DreamTV.ProgramInfo.ChannelStream;

public final class UriGenerator {
	
	public static String Generate(ChannelStream channelStream)
	{
		String uri = null;
		if(!channelStream.mEmbedd)
		{
//			if(!Utility.isStringNullOrEmpty(channelStream.mLink) || !Utility.isStringNullOrEmpty(channelStream.mFileName) ||
//					!Utility.isStringNullOrEmpty(channelStream.mExtra))
				uri = channelStream.mPlayer + "?";
//			else
//				uri = channelStream.mPlayer;
			
			if(!Utility.isStringNullOrEmpty(channelStream.mLink))
				uri += channelStream.mLink;
			
			if(!Utility.isStringNullOrEmpty(channelStream.mFileName))
				uri += "&" + channelStream.mFileName;
			
			if(!Utility.isStringNullOrEmpty(channelStream.mExtra))
				uri += "&" + channelStream.mExtra;
			
			if(!channelStream.mExclude.contains("screencolor"))
				uri += "&screencolor=000000";
			if(!channelStream.mExclude.contains("autostart"))
				uri += "&autostart=true";
			if(!channelStream.mExclude.contains("controlbar"))
				uri += "&controlbar=none";
			if(!channelStream.mExclude.contains("wmode"))
				uri += "&wmode=opaque";
			if(!channelStream.mExclude.contains("type"))
				uri += "&type=rtmp";
		}
		else
		{
			
			uri = "<html>" +  
			"<body style=\"background-color:black;\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\" leftmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\" scroll=\"no\" allowscriptaccess=\"always\">" +
			"<embed type=\"application/x-shockwave-flash\"  width=\"100%\" height=\"100%\" id=\"playerid\" " +
						"src=\"";
			
			
			uri +=  channelStream.mPlayer + "\" flashvars=\"stretching=exactfit&allowfullscreen=true&allowscriptaccess=always&provider=rtmp&screencolor=000000&autostart=true&wmode=opaque";
			
			if(!Utility.isStringNullOrEmpty(channelStream.mLink))
				uri += "&" + channelStream.mLink;
			if(!Utility.isStringNullOrEmpty( channelStream.mFileName))
				uri += "&" + channelStream.mFileName;
			if(!Utility.isStringNullOrEmpty(channelStream.mExtra))
				uri += "&" + channelStream.mExtra;
			
			if(!channelStream.mExclude.contains("controlbar"))
				uri += "&controlbar=none";
			
			uri += "\" /></body></html>";
			
			
		}

		return uri;
	}
	
	

}
