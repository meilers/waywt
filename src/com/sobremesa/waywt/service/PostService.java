package com.sobremesa.waywt.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import retrofit.http.EncodedPath;
import retrofit.http.GET;
import retrofit.http.Query;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.sobremesa.waywt.contentprovider.Provider;
import com.sobremesa.waywt.database.tables.PostTable;
import com.sobremesa.waywt.enums.PostType;
import com.sobremesa.waywt.service.BaseService;
import com.sobremesa.waywt.service.RemoteObject;
import com.sobremesa.waywt.service.clients.PostServiceClient;
import com.sobremesa.waywt.service.synchronizer.PostPreprocessor;
import com.sobremesa.waywt.service.synchronizer.PostSynchronizer;
import com.sobremesa.waywt.service.synchronizer.RemotePreProcessor;
import com.sobremesa.waywt.service.synchronizer.Synchronizer;
import com.sobremesa.waywt.util.UserUtil;

import de.greenrobot.event.EventBus;

public class PostService extends BaseService {

	public static final class Extras
	{
		public static final String IS_MALE = "is_male";
		public static final String IS_TEEN = "is_teen";
	}
	
	private boolean mIsMale = true;
	private boolean mIsTeen = false;
	
	public class RemoteResponse {
		public String kind;
		public RemoteData data;
	}

	public class RemoteData extends RemoteObject {
		public String modhash;
		public String after;
		public List<RemoteRedditPost> children;
		
		@Override
		public String getIdentifier() {
			return "";
		}
	}
	
	public class RemoteRedditPost extends RemoteObject {
		public String kind;
		public PostType postType;
		public RemoteRedditPostData data;
		
		@Override
		public String getIdentifier() {
			return data.permalink;
		}
		
		
		@Override
	    public boolean equals(Object object)
	    {
	        boolean sameSame = false;

	        if (object != null && object instanceof RemoteRedditPost)
	        {
	            sameSame = this.data.permalink == ((RemoteRedditPost) object).data.permalink;
	        }

	        return sameSame;
	    }
	}

	public class RemoteRedditPostData extends RemoteObject {

		public String domain;
		public String subreddit;
		public String permalink; // unique
		
		public String author;
		public String created;
		public String title;
		public int ups;
		public int downs;
		
		public String author_flair_text;
		
		@Override
		public String getIdentifier() {
			return permalink; 
		}
	}
	
	// Interfaces
	
	public interface PostClient {
		RemoteResponse getPosts(@EncodedPath("path") String path, @Query("t")String time, @Query("after")String after);  
	}
	
	public interface MFAPostClient extends PostClient {
		@GET("/r/malefashionadvice/{path}.json")
		RemoteResponse getPosts(@EncodedPath("path") String path, @Query("t")String time, @Query("after")String after);  
	}
	
	public interface FFAPostClient extends PostClient {
		@GET("/r/femalefashionadvice/{path}.json")
		RemoteResponse getPosts(@EncodedPath("path") String path, @Query("t")String time, @Query("after")String after);  
	}
	
	public interface TeenMFAPostClient extends PostClient {
		@GET("/r/TeenMFA/{path}.json")
		RemoteResponse getPosts(@EncodedPath("path") String path, @Query("t")String time, @Query("after")String after);  
	}
	
	public interface TeenFFAPostClient extends PostClient {
		@GET("/r/TeenFFA/{path}.json")
		RemoteResponse getPosts(@EncodedPath("path") String path, @Query("t")String time, @Query("after")String after);  
	}
	
	

	public PostService() {
		super("RedditPostService");
	} 

	public PostService(Context c) {
		super("RedditPostService", c);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SYNC)) 
		{
			mIsMale = intent.getBooleanExtra(Extras.IS_MALE, true);
			mIsTeen = intent.getBooleanExtra(Extras.IS_TEEN, false);
			
			List<RemoteRedditPost> totalPosts = new ArrayList<RemoteRedditPost>();
			
			PostClient client = PostServiceClient.getInstance().getClient(getContext(), PostClient.class); 
			
			if( mIsMale )
			{
				if( !mIsTeen )
					client = PostServiceClient.getInstance().getClient(getContext(), MFAPostClient.class); 
				else
					client = PostServiceClient.getInstance().getClient(getContext(), TeenMFAPostClient.class); 
				
			}
			else
			{
				if( !mIsTeen )
					client = PostServiceClient.getInstance().getClient(getContext(), FFAPostClient.class); 
				else
					client = PostServiceClient.getInstance().getClient(getContext(), TeenFFAPostClient.class); 
			}
				
			RemoteResponse response; 
			RemoteData remoteData;
			
			List<RemoteRedditPost> posts;
			
			Iterator<RemoteRedditPost> iter;
			
			
			// new 
			String after = "";
			int i = 0;
			
			while( after != null && i < 1)
			{
				try{
					response = client.getPosts("hot", "", after);  
				}
				catch(Exception e)
				{
					return;
				}
				
				if( response == null )
					return; 
				
				remoteData = response.data;
				
				posts = remoteData.children;   
				
				iter = posts.iterator();
				while (iter.hasNext()) {
					RemoteRedditPost post = iter.next();
					
					PostType postType = getPostType(mIsMale, mIsTeen, post);
					post.postType = postType;
					
					if ( postType == PostType.INVALID || totalPosts.contains(post) )
				        iter.remove(); 
				}
				
				totalPosts.addAll(posts);
				after = response.data.after;
				
				++i;
			}
			
			
			// today
			after = "";
			i = 0;
			
			while( after != null && i < 2)
			{
				try
				{
					response = client.getPosts("top", "today", after);  
				}
				catch(Exception e)
				{
					return;
				}
				
				if( response == null )
					return; 
				
				remoteData = response.data;
				
				posts = remoteData.children;  
				
				iter = posts.iterator();
				while (iter.hasNext()) {
					RemoteRedditPost post = iter.next();
					
					PostType postType = getPostType(mIsMale, mIsTeen, post);
					post.postType = postType;
					
					if ( postType == PostType.INVALID || totalPosts.contains(post) )
				        iter.remove(); 
				}
				
				totalPosts.addAll(posts);
				after = response.data.after;
				
				++i;
			}
			
			// week 
			after = "";
			i = 0;
			
			while( after != null && i < 3)
			{
				try
				{
					response = client.getPosts("top", "week", after);  
				}
				catch(Exception e)
				{
					return;
				}
				
				if( response == null )
					return; 
				
				remoteData = response.data;
				
				posts = remoteData.children;  
				
				iter = posts.iterator();
				while (iter.hasNext()) {
					RemoteRedditPost post = iter.next();
					
					PostType postType = getPostType(mIsMale, mIsTeen, post);
					post.postType = postType;
					
					if ( postType == PostType.INVALID || totalPosts.contains(post) )
				        iter.remove(); 
				}
				
				totalPosts.addAll(posts);
				after = response.data.after;
				
				++i;
			}
			
			
			// month
			after = "";
			i = 0;
			
			while( after != null && i < 5)
			{
				
				try
				{
					response = client.getPosts("top", "month", after);  
				}
				catch(Exception e)
				{
					return;
				}
				
				if( response == null )
					return; 
				
				remoteData = response.data;
				
				posts = remoteData.children;  
				
				iter = posts.iterator();
				while (iter.hasNext()) {
					RemoteRedditPost post = iter.next();
					
					PostType postType = getPostType(mIsMale, mIsTeen, post);
					post.postType = postType;
					
					if ( postType == PostType.INVALID || totalPosts.contains(post) )
				        iter.remove(); 
				}
				
				totalPosts.addAll(posts);
				after = response.data.after;
				
				++i;
			}
			
			if (totalPosts != null && totalPosts.size() > 0) { 
				// synchronize!
				Cursor localRecCursor = getContext().getContentResolver().query(Provider.POST_CONTENT_URI, PostTable.ALL_COLUMNS, PostTable.IS_MALE + "=? AND " + PostTable.IS_TEEN + "=?" , new String[] { mIsMale ? "1":"0", mIsTeen ? "1":"0" }, null);
				localRecCursor.moveToFirst();
				
				PostSynchronizer sync = new PostSynchronizer(getContext());
				sync.setIsMale(mIsMale);
				sync.setIsTeen(mIsTeen);
				synchronizeRemoteRecords(totalPosts, localRecCursor, localRecCursor.getColumnIndex(PostTable.PERMALINK), sync, new PostPreprocessor());
				
				//
			} else {
//				EventBus.getDefault().post(new RecordingServiceEvent(false, "There where no representatives for this zip code"));
			}
			
		}
	}
	
	private PostType getPostType( boolean isMale, boolean isTeen, RemoteRedditPost post )
	{
		if( isMale )
		{
			if( !isTeen )
			{
				if( !post.data.domain.equals("self.malefashionadvice") || post.data.title.toLowerCase().contains("announcement") || post.data.title.toLowerCase().contains("phone") || post.data.title.toLowerCase().contains("interest") || post.data.title.toLowerCase().contains("top") || (!post.data.title.toLowerCase().contains("waywt") && !post.data.title.toLowerCase().contains("outfit feedback") && !post.data.title.toLowerCase().contains("recent purchases")) )
					return PostType.INVALID;
				
				if( post.data.title.toLowerCase().contains("waywt") )
					return PostType.WAYWT;
				else if( post.data.title.toLowerCase().contains("outfit feedback") )
					return PostType.OUTFIT_FEEDBACK;
				else if( post.data.title.toLowerCase().contains("recent purchases") )
					return PostType.RECENT_PURCHASES;
				else
					return PostType.INVALID;
			}
			else
			{
				if( !post.data.domain.equals("self.TeenMFA") || post.data.title.toLowerCase().contains("announcement") || post.data.title.toLowerCase().contains("interest")|| post.data.title.toLowerCase().contains("top")|| (!post.data.title.toLowerCase().contains("waywt") && !post.data.title.toLowerCase().contains("recent purchases")) )
					return PostType.INVALID;
				
				if( post.data.title.toLowerCase().contains("waywt") )
					return PostType.WAYWT;
				else if( post.data.title.toLowerCase().contains("outfit feedback") )
					return PostType.OUTFIT_FEEDBACK;
				else if( post.data.title.toLowerCase().contains("recent purchases") )
					return PostType.RECENT_PURCHASES;
				else
					return PostType.INVALID;
			}
		}
		else
		{
			if( !isTeen )
			{
				if( !post.data.domain.equals("self.femalefashionadvice") || post.data.title.toLowerCase().contains("announcement") || post.data.title.toLowerCase().contains("interest")|| post.data.title.toLowerCase().contains("top") ||  (!post.data.title.toLowerCase().contains("waywt") && !post.data.title.toLowerCase().contains("outfit feedback") && !post.data.title.toLowerCase().contains("theme") && !post.data.title.toLowerCase().contains("recent purchases")) )
					return PostType.INVALID;
				
				if( post.data.title.toLowerCase().contains("waywt") )
					return PostType.WAYWT;
				else if( post.data.title.toLowerCase().contains("outfit feedback") )
					return PostType.OUTFIT_FEEDBACK;
				else if( post.data.title.toLowerCase().contains("recent purchases") )
					return PostType.RECENT_PURCHASES;
				else
					return PostType.INVALID;
			}	
			else
			{
				if( !post.data.domain.equals("self.TeenFFA") || post.data.title.toLowerCase().contains("announcement") || post.data.title.toLowerCase().contains("interest") || post.data.title.toLowerCase().contains("top") ||  (!post.data.title.toLowerCase().contains("waywt") && !post.data.title.toLowerCase().contains("outfit feedback") && !post.data.title.toLowerCase().contains("recent purchases")) )
					return PostType.INVALID;
				
				if( post.data.title.toLowerCase().contains("waywt") )
					return PostType.WAYWT;
				else if( post.data.title.toLowerCase().contains("outfit feedback") )
					return PostType.OUTFIT_FEEDBACK;
				else if( post.data.title.toLowerCase().contains("recent purchases") )
					return PostType.RECENT_PURCHASES;
				else
					return PostType.INVALID;
			}
		}
	}


	public void synchronizeRemoteRecords(List<RemoteRedditPost> remoteReps, Cursor localReps, int remoteIdentifierColumn, Synchronizer<RemoteRedditPost> synchronizer, RemotePreProcessor<RemoteRedditPost> preProcessor) {
		preProcessor.preProcessRemoteRecords(remoteReps);
		synchronizer.synchronize(getContext(), remoteReps, localReps, remoteIdentifierColumn);
	}

}
