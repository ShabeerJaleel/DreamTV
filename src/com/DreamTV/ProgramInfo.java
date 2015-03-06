package com.DreamTV;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore.LoadStoreParameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.DreamTV.NetworkTaskHandler.GetLocationTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.GeolocationPermissions;

public class ProgramInfo {

	public static final String FILENAME = "progdata.xml";
	public static final String COMPRESSEDFILENAME = "progdata.zip";
	public List<LanguageDefinition> mLanguageDefinitions = new ArrayList<ProgramInfo.LanguageDefinition>();
	public List<CountryDefinition> mContryDefinitions = new ArrayList<ProgramInfo.CountryDefinition>();
	public List<FlashPlayer> mFlashPlayers = new ArrayList<ProgramInfo.FlashPlayer>();
	public List<BaseHost> mBaseHosts = new ArrayList<ProgramInfo.BaseHost>();
	public List<StreamServer> mStreamServers = new ArrayList<ProgramInfo.StreamServer>();
	public LiveTv mLiveTv;
	public Movies mMovies;
	public Radio mLiveRadio;
	public Channel ActiveChannel;
	
	public List<Channel> mAllChannels = new ArrayList<ProgramInfo.Channel>();
	public List<Channel> mAllMovies = new ArrayList<ProgramInfo.Channel>();
	
	public static ProgramInfo Me;
	
	private GetLocationTask locationTask;
	private String mUserCountryCode;

	private ProgramInfo(){
		
	}
	

	public static void  init(Context context)
	{
		if(Me != null)
			return;
		
		Me = new ProgramInfo();

		try 
		{
			Me.locationTask = NetworkTaskHandler.locateCountry(context);
			
			InputStream stream = null;
//			File file = context.getFileStreamPath(FILENAME);
//			
//			if(file.exists()) 
//				stream = new FileInputStream(file);
//			else
				stream = context.getAssets().open(FILENAME);
					
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(stream);

			doc.getDocumentElement().normalize();

			//Language defs
			NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) 
			{
				Node childNode = nodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE)
				{
					if(childNode.getNodeName().equalsIgnoreCase("Languages"))
					{
						NodeList langNodes = childNode.getChildNodes();
						for(int j=0; j < langNodes.getLength(); j++)
						{
							if(langNodes.item(j).getNodeType() == Node.ELEMENT_NODE)
								Me.mLanguageDefinitions.add( 
										Me.new LanguageDefinition(langNodes.item(j)));
						}
					}
					else if(childNode.getNodeName().equalsIgnoreCase("Countries"))
					{
						NodeList cntNodes = childNode.getChildNodes();
						for(int j=0; j < cntNodes.getLength(); j++)
						{
							Node countryDefNode = cntNodes.item(j);
							if(countryDefNode.getNodeType() == Node.ELEMENT_NODE )
							{
								int enabled = Integer.parseInt(countryDefNode.getAttributes().
										getNamedItem("enabled").getNodeValue());
								
								if(enabled == 1)
									Me.mContryDefinitions.add(
											Me.new CountryDefinition(cntNodes.item(j), Me));
							}
						}
					}
					else if(childNode.getNodeName().equalsIgnoreCase("LiveTv"))
					{
						Me.mLiveTv = Me.new LiveTv(childNode, Me);
					}
					else if(childNode.getNodeName().equalsIgnoreCase("Radio"))
					{
						Me.mLiveRadio = Me.new Radio(childNode, Me);
					}
				}
			}
			
			stream.close();
			
			NetworkTaskHandler.downloadChannelList(context);

		}
		catch(Exception ex)
		{
			Log.d("Dream TV", ex.toString());
		}
		
		Collections.sort(Me.mContryDefinitions);
		Collections.sort(Me.mLanguageDefinitions);
		Collections.sort(Me.mLiveTv.mChannelCountryCollection);
		
		for(int i =0; i < Me.mLiveTv.mChannelCountryCollection.size(); i++)
		{
			Country c = Me.mLiveTv.mChannelCountryCollection.get(i);
			Collections.sort(c.mChannelLanguageCollection);
			for(int j =0; j < c.mChannelLanguageCollection.size(); j++)
			{
				Language l = c.mChannelLanguageCollection.get(j);
				Collections.sort(l.mChannelCollection);
			}
		}

	
		
		Collections.sort(Me.mLiveRadio.mRadioLanguageCollection);
		
		for(int i = Me.mLiveRadio.mRadioLanguageCollection.size() -1; i > -1; i-- )
		{
			Language l = Me.mLiveRadio.mRadioLanguageCollection.get(i);
			Collections.sort(l.mChannelCollection);
		}

		Me.flattenHierarchy();
	}
	
	
	
	private void flattenHierarchy()
	{
		for(int i =0; i < Me.mLiveTv.mChannelCountryCollection.size(); i++)
		{
			Country c = Me.mLiveTv.mChannelCountryCollection.get(i);
			for(int j =0; j < c.mChannelLanguageCollection.size(); j++)
			{
				Language l = c.mChannelLanguageCollection.get(j);
				copyChannelsToList(l, Me.mAllChannels);
			}
		}
		
	
	}
	
	private void copyChannelsToList(Language language, List<Channel> channelCollection)
	{
		for(int i =0; i <language.mChannelCollection.size(); i++)
			channelCollection.add(language.mChannelCollection.get(i));
	}

	public Channel GetPreviousChannel(ChannelStream channelStream)
	{
		Channel channel = null;
		if(channelStream.mChannel.mIsLive) //TV
		{
			int index  = mAllChannels.indexOf(channelStream.mChannel);
			if(index > 0)
				channel = mAllChannels.get(index - 1);
			
		}
	
		
		return channel;
	}
	
	public Channel GetNextChannel(ChannelStream channelStream)
	{
		Channel channel = null;
		if(channelStream.mChannel.mIsLive) //TV
		{
			int index  = mAllChannels.indexOf(channelStream.mChannel);
			if(index < mAllChannels.size() - 1)
				channel = mAllChannels.get(index + 1);
			
		}
		
		return channel;
	}
	

	public int getCountryDefinitionIndex()
	{
		resolveUserCountryCode();
		if(!Utility.isStringNullOrEmpty(mUserCountryCode))
		{
			for(int i =0; i < mContryDefinitions.size(); i++)
			{
				if(mUserCountryCode.toLowerCase().endsWith(mContryDefinitions.get(i).mId.toLowerCase()))
					return i;
			}
		}
		
		return -1;
	}
	
	public void resolveUserCountryCode()
	{
		if(locationTask == null || !Utility.isStringNullOrEmpty(mUserCountryCode))
			return ;
		
		if(locationTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			try 
			{
				locationTask.get(5, TimeUnit.SECONDS);
			} catch (Exception e)
			{
				e.printStackTrace();
			} 
		}
		
		mUserCountryCode = locationTask.mUserCountryCode;
		locationTask = null;
		return ;
	}
	

	public class CountryDefinition extends GenericComparer
	{
		public String mId;
		public String mName;
		public String mDescription;
		public ProgramInfo mProgramInfo;
		public String mDefaultLanguage;
		private int mChannelCount = 0;

		public CountryDefinition(Node node, ProgramInfo pi)
		{
			mProgramInfo = pi;
			mId = node.getAttributes().getNamedItem("id").getNodeValue().trim();
			mName = node.getAttributes().getNamedItem("name").getNodeValue();
			mDescription = node.getAttributes().getNamedItem("desc").getNodeValue();
			mDefaultLanguage = node.getAttributes().getNamedItem("lang").getNodeValue();
		}
		
		@Override
		public String toString() 
		{
			return mName + " (" + Integer.toString(getChannelCount()) + ")";
		}
		
		public int getChannelCount()
		{
			if(mChannelCount == 0)
			{
				for(int i =0; i < mProgramInfo.mLiveTv.mChannelCountryCollection.size(); i++)
				{
					Country c = mProgramInfo.mLiveTv.mChannelCountryCollection.get(i);
					if(c.mCountryDefinition == this)
					{
						for(int j =0; j < c.mChannelLanguageCollection.size(); j++)
						{
							mChannelCount += c.mChannelLanguageCollection.get(j).mChannelCollection.size();
						}
						
						break;
					}
				}
			}
			
			return mChannelCount;
		}
		
		

	}

	public class LanguageDefinition extends GenericComparer
	{
		public String mId;
		public String mName;
		public String mDescription;

		public LanguageDefinition(Node node)
		{
			mId = node.getAttributes().getNamedItem("id").getNodeValue().trim();
			mName = node.getAttributes().getNamedItem("name").getNodeValue();
			mDescription = node.getAttributes().getNamedItem("desc").getNodeValue();
		}
		
		
		@Override
		public String toString() {
			return mName;
		}
	}

	public class FlashPlayer extends GenericComparer
	{
		
		public int mId;
		public String mName;
		public String mSource;

		public FlashPlayer(Node node)
		{
			mId = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
			mName = node.getAttributes().getNamedItem("name").getNodeValue();
			mSource = node.getAttributes().getNamedItem("src").getNodeValue();
		}
		
		
		@Override
		public String toString() {
			return mName;
		}
	}
	
	public class BaseHost extends GenericComparer
	{
		
		public int mId;
		public String mName;
		public String mSource;

		public BaseHost(Node node)
		{
			mId = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
			mName = node.getAttributes().getNamedItem("name").getNodeValue();
			mSource = node.getAttributes().getNamedItem("src").getNodeValue();
		}
		
		
		@Override
		public String toString() {
			return mName;
		}
	}
	
	public class StreamServer extends GenericComparer
	{
		
		public int mId;
		public String mName;
		public String mSource;

		public StreamServer(Node node)
		{
			mId = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
			mName = node.getAttributes().getNamedItem("name").getNodeValue();
			mSource = node.getAttributes().getNamedItem("src").getNodeValue();
		}
		
		
		@Override
		public String toString() {
			return mName;
		}
	}
	
	public class LiveTv
	{
		public List<Country> mChannelCountryCollection = new ArrayList<ProgramInfo.Country>();
		private int mCount =0;

		public LiveTv(Node node, ProgramInfo parent)
		{
			NodeList nodeList = node.getChildNodes();

			for(int i = 0; i<nodeList.getLength(); i++)
			{
				Node childNode = nodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("Country"))
				{
					String id = childNode.getAttributes().getNamedItem("id").getNodeValue().trim();

					//get country def
					for(int j = 0; j < parent.mContryDefinitions.size(); j++)
					{
						if(parent.mContryDefinitions.get(j).mId.equalsIgnoreCase(id))
						{
							mChannelCountryCollection.add(new Country(childNode, 
									parent.mContryDefinitions.get(j), parent.mLanguageDefinitions));
							break;
						}
					}
				}
			}
			
			
		}
		
		@Override
		public String toString() 
		{
			if(mCount == 0)
			{
				for(int i =0; i < ProgramInfo.Me.mContryDefinitions.size();i++)
				{
					mCount +=  ProgramInfo.Me.mContryDefinitions.get(i).getChannelCount();
				}
			}
			
			return "Live TV (" + Integer.toString(mCount) + ")";
		}
	}
	
	public class Movies
	{
		public List<Language> mMovieLanguageCollection = new ArrayList<ProgramInfo.Language>();
		
		public Movies(Node node, ProgramInfo parent)
		{
			NodeList nodeList = node.getChildNodes();

			for(int i = 0; i<nodeList.getLength(); i++)
			{
				Node childNode = nodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("Language"))
				{
					String id = childNode.getAttributes().getNamedItem("id").getNodeValue().trim();

					//get country def
					for(int j = 0; j < parent.mLanguageDefinitions.size(); j++)
					{
						if(parent.mLanguageDefinitions.get(j).mId.equalsIgnoreCase(id))
						{
							mMovieLanguageCollection.add(new Language(childNode, (parent.mLanguageDefinitions.get(j)), null));
							break;
						}
					}
				}
			}
		}
	}
	
	public class Radio
	{
		public List<Language> mRadioLanguageCollection = new ArrayList<ProgramInfo.Language>();
		
		public Radio(Node node, ProgramInfo parent)
		{
			NodeList nodeList = node.getChildNodes();

			for(int i = 0; i<nodeList.getLength(); i++)
			{
				Node childNode = nodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("Language"))
				{
					String id = childNode.getAttributes().getNamedItem("id").getNodeValue();

					//get country def
					for(int j = 0; j < parent.mLanguageDefinitions.size(); j++)
					{
						if(parent.mLanguageDefinitions.get(j).mId.equalsIgnoreCase(id))
						{
							mRadioLanguageCollection.add(new Language(childNode, (parent.mLanguageDefinitions.get(j)), null));
							break;
						}
					}
				}
			}
		}
	}

	public class Country extends GenericComparer
	{
		public CountryDefinition mCountryDefinition;
		public List<Language> mChannelLanguageCollection =  new ArrayList<ProgramInfo.Language>();

		public Country(Node node, CountryDefinition countryDefinition, List<LanguageDefinition> langDefs)
		{
			mCountryDefinition = countryDefinition;

			NodeList nodeList = node.getChildNodes();

			for(int i = 0; i<nodeList.getLength(); i++)
			{
				Node childNode = nodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("Language"))
				{
					String id = childNode.getAttributes().getNamedItem("id").getNodeValue().trim();

					//get country def
					for(int j = 0; j < langDefs.size(); j++)
					{
						if(langDefs.get(j).mId.equalsIgnoreCase(id))
						{
							mChannelLanguageCollection.add(new Language(childNode, langDefs.get(j), this));
							break;
						}
					}
				}
			}

		}
		
		@Override
		public String toString() {
			return mCountryDefinition.mName;
		}
		
		public int getChannelLanguageIndex(String langID)
		{
			for(int i = 0; i < mChannelLanguageCollection.size(); i++)
			{
				if(mChannelLanguageCollection.get(i).mLangDefinitions.mId.toLowerCase().equals(langID.toLowerCase()))
						return i;
			}
			
			return 0;
		}
		
		
	}

	public class Language extends GenericComparer
	{
		public LanguageDefinition mLangDefinitions;
		public Country mCountry;
		public List<Channel> mChannelCollection = new ArrayList<ProgramInfo.Channel>();

		public Language(Node node, LanguageDefinition languageDef, Country country)
		{
			mLangDefinitions = languageDef;
			mCountry = country;

			NodeList nodeList = node.getChildNodes();

			for(int i = 0; i<nodeList.getLength(); i++)
			{
				Node childNode = nodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("Channel"))
				{
					mChannelCollection.add(new Channel(childNode, this));
				}
			}
		}
		
		@Override
		public String toString() {
			return mLangDefinitions.mName;
		}
	}

	public class Channel  extends GenericComparer
	{
		public String mId;
		public String mName;
		public String mDescription;
		public Language mLanguage;
		public boolean mIsLive=true;
		public boolean mEnabled=true;
		public List<ChannelStream> mChannelStreamCollection = new ArrayList<ChannelStream>();

		public Channel(Node node, Language parent)
		{
			mLanguage = parent;
			mId = node.getAttributes().getNamedItem("id").getNodeValue();
			mName = node.getAttributes().getNamedItem("name").getNodeValue();
			mDescription = node.getAttributes().getNamedItem("desc").getNodeValue();
			mIsLive = node.getAttributes().getNamedItem("live") != null ?  
					Boolean.parseBoolean(node.getAttributes().getNamedItem("live").getNodeValue()) : true;
			
			Node temp = node.getAttributes().getNamedItem("enabled");
			if(temp != null)
				mEnabled = Boolean.parseBoolean(temp.getNodeValue()); 

			NodeList nodeList = node.getChildNodes();

			for(int i = 0; i<nodeList.getLength(); i++)
			{
				Node childNode = nodeList.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equalsIgnoreCase("Stream"))
				{
					mChannelStreamCollection.add(new ChannelStream(childNode, this));
				}
			}
		}
		
		@Override
		public String toString() {
			return mName;
		}
	}

	public class ChannelStream
	{
		public StreamingProtocol mProtocol;
		public boolean mEmbedd;
		public String mPlayer;
		public String mLink;
		public String mUrl;
		public String mFileName;
		public String mExtra ="";
		public String mExclude ="";
		
		
		//public String mStreamInfo;
		public Channel mChannel;
		
		public boolean mFullScreen = true;

		public ChannelStream(Node node, Channel parent)
		{
			mChannel = parent;

			try
			{

				//<Stream p="0" q="1" fp="1" h="1" s="4" f="nokia40mtv150.sdp"/>
				mProtocol = StreamingProtocol.values()[Integer.parseInt(node.getAttributes().getNamedItem("type").getNodeValue())];
				
				Node temp = node.getAttributes().getNamedItem("embedd");
				if(temp != null)
					mEmbedd = (Integer.parseInt(temp.getNodeValue().trim()) != 0); 
					
				
				temp = node.getAttributes().getNamedItem("player");
				if(temp != null)
					mPlayer = temp.getNodeValue().trim();
				
				temp = node.getAttributes().getNamedItem("link");
				if(temp != null)
					mLink = temp.getNodeValue().trim();
				
				temp = node.getAttributes().getNamedItem("url");
				if(temp != null)
					mUrl = temp.getNodeValue().trim();
				
				temp = node.getAttributes().getNamedItem("file");
				if(temp != null)
					mFileName = temp.getNodeValue().trim();
				
				temp = node.getAttributes().getNamedItem("extra");
				if(temp != null)
					mExtra = temp.getNodeValue().trim();
				
				temp = node.getAttributes().getNamedItem("exclude");
				if(temp != null)
					mExclude = temp.getNodeValue().trim();
				
				temp = node.getAttributes().getNamedItem("fullscreen");
				if(temp != null)
					mFullScreen = (Integer.parseInt(temp.getNodeValue().trim()) != 0); 
				
			}
			catch(Exception ex)
			{
				Log.d("Dream TV", ex.toString());
			}
		}
		
		public boolean isYouTubeVideo()
		{
//			if(!Utility.isStringNullOrEmpty(mStreamInfo) &&
//					(mStreamInfo.contains("ytpl:") ||
//					mStreamInfo.contains("ytv:")))
//				return true;
			
			return false;
		}
		
		public String getStream()
		{
			return UriGenerator.Generate(this);
		}
	}

	public enum StreamingProtocol
	{
		Rtsp,
		Rtmp
		
	}

}


