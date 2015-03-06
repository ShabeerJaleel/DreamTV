package com.DreamTV;

import java.util.ArrayList;
import java.util.List;

import com.DreamTV.ProgramInfo.Channel;
import com.DreamTV.ProgramInfo.Language;

public class DisplayItem {
	
	public Language mLanguage;
	public List<Channel> mChannels = new ArrayList<Channel>();

	public DisplayItem(Language language){mLanguage=language;}

}
