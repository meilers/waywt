package com.sobremesa.waywt.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;

import com.sobremesa.waywt.util.StringUtils;
import com.sobremesa.waywt.util.UserUtil;
import com.sobremesa.waywt.util.Util;
import com.sobremesa.waywt.R;
import com.sobremesa.waywt.activities.CameraActivity;
import com.sobremesa.waywt.activities.MainActivity;
import com.sobremesa.waywt.application.WaywtApplication;
import com.sobremesa.waywt.common.Constants;  
import com.sobremesa.waywt.common.RedditIsFunHttpClientFactory;
import com.sobremesa.waywt.contentprovider.Provider;
import com.sobremesa.waywt.database.tables.CommentTable;
import com.sobremesa.waywt.enums.SortByType;
import com.sobremesa.waywt.listeners.CommentsListener;
import com.sobremesa.waywt.listeners.MyPostsListener;
import com.sobremesa.waywt.managers.FontManager;
import com.sobremesa.waywt.model.MyPost;
import com.sobremesa.waywt.model.ThingInfo;
import com.sobremesa.waywt.service.PostService;
import com.sobremesa.waywt.settings.RedditSettings;
import com.sobremesa.waywt.tasks.DownloadMyPostsTask;
import com.viewpagerindicator.TitlePageIndicator;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class MyPostsFragment extends Fragment implements MyPostsListener {

	public static final String TAG = WaywtFragment.class.getCanonicalName();

	public static class Extras {
		public static String SUBREDDIT = "subreddit";
		public static String PERMALINKS = "permalinks";
	}
	
	

	private final Pattern COMMENT_PATH_PATTERN = Pattern.compile(Constants.COMMENT_PATH_PATTERN_STRING);
	private final Pattern COMMENT_CONTEXT_PATTERN = Pattern.compile("context=(\\d+)");

	private ViewPager mPager;
	public MyPostPagerAdapter mPagerAdapter;
	private TitlePageIndicator mindicator;

	private String mSubreddit = UserUtil.getSubreddit();
	private List<String> mThreadIds;
	private ThingInfo mOPComment = null;
	
	private final HttpClient mRedditClient = WaywtApplication.getRedditClient();
	private final RedditSettings mRedditSettings = WaywtApplication.getRedditSettings();

	private MenuItem mRefreshMenuItem;
	private MenuItem mLoadingMenuItem;
	
	
	private DownloadMyPostsTask getNewDownloadMyPostsTask() {
		return new DownloadMyPostsTask(this, mSubreddit, mThreadIds, mRedditSettings, mRedditClient);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ArrayList<String> permalinks = getArguments().getStringArrayList(Extras.PERMALINKS);
		mThreadIds = new ArrayList<String>();
		mSubreddit = getArguments().getString(Extras.SUBREDDIT);

		for( String s : permalinks )
		{
			String commentPath = null;
			String commentQuery;
			String jumpToCommentId = null;
			Uri data = Uri.parse(s);
			if (data != null) {
				// Comment path: a URL pointing to a thread or a comment in a
				// thread.
				commentPath = data.getPath();
				commentQuery = data.getQuery();
			} else {
				if (Constants.LOGGING)
					Log.e(TAG, "Quitting because no subreddit and thread id data was passed into the Intent.");
				getActivity().finish();
			}
			
			if (commentPath != null) {
				if (Constants.LOGGING)
					Log.d(TAG, "comment path: " + commentPath);
				
				if (Util.isRedditShortenedUri(data)) {
					// http://redd.it/abc12
					mThreadIds.add(commentPath.substring(1));
				} else {
					// http://www.reddit.com/...
					Matcher m = COMMENT_PATH_PATTERN.matcher(commentPath);
					if (m.matches()) {
						mSubreddit = m.group(1);
						mThreadIds.add(m.group(2));
						jumpToCommentId = m.group(3);
					}
				}
			} else {
				if (Constants.LOGGING)
					Log.e(TAG, "Quitting because of bad comment path.");
				getActivity().finish();
			}
			
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_waywt, null, false);

		mPager = (ViewPager) view.findViewById(R.id.pager);
		mPagerAdapter = new MyPostPagerAdapter(getChildFragmentManager(), mSubreddit);
		mPager.setAdapter(mPagerAdapter);

		mindicator = (TitlePageIndicator) view.findViewById(R.id.page_indicator);
		mindicator.setViewPager(mPager);
		mindicator.setTypeface(FontManager.INSTANCE.getAppFont());

		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		fetchComments();
	}
	

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		


	}

	@Override
	public void onPause() {
		super.onPause();
		CookieSyncManager.getInstance().stopSync();
		mRedditSettings.saveRedditPreferences(getActivity());
	}

	@Override
	public void onDestroy() {
		mPagerAdapter = null;

		super.onDestroy();
	}

	@Override
	public void resetUI() {
		// findViewById(R.id.loading_light).setVisibility(View.GONE);
		// findViewById(R.id.loading_dark).setVisibility(View.GONE);

		if( mPagerAdapter != null )
			mPagerAdapter.mIsLoading = false;
	}

	@Override
	public void enableLoadingScreen() {
		// findViewById(R.id.loading_light).setVisibility(View.VISIBLE);
		// findViewById(R.id.loading_dark).setVisibility(View.GONE);

		if (mPagerAdapter != null)
			mPagerAdapter.mIsLoading = true;
		getActivity().getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_START);
	}


	
	
	private void fetchComments()
	{
		if (mRefreshMenuItem != null && mLoadingMenuItem != null) {
			mLoadingMenuItem.setVisible(true);
			mRefreshMenuItem.setVisible(false);
		}
		
		getNewDownloadMyPostsTask().execute(Constants.DEFAULT_COMMENT_DOWNLOAD_LIMIT);
	}
	
	
	private int getReplyCount( ThingInfo ci )
	{
		int total = 0;
		
		if( ci.getReplies() != null && ci.getReplies().getData() != null && ci.getReplies().getData().getChildren() != null )
		{
			for( int i=0; i< ci.getReplies().getData().getChildren().length; ++i)
				total += getReplyCount( ci.getReplies().getData().getChildren()[i].getData() );
				
			return ci.getReplies().getData().getChildren().length + total;
			
		}
		else
			return total;
	}
	

	public static class MyPostPagerAdapter extends FragmentStatePagerAdapter {
		public boolean mIsLoading = false;

		private ArrayList<MyPost> mMyPosts = new ArrayList<MyPost>();

		private String mSubreddit = "";

		public MyPostPagerAdapter(FragmentManager fragmentManager, String subreddit) {
			super(fragmentManager);

			mSubreddit = subreddit;
		}

		public void addMyPosts(List<MyPost> myPosts) {
			
			mMyPosts.clear();
			mMyPosts.addAll(myPosts);

			this.notifyDataSetChanged();
		}

		@Override
		public Fragment getItem(int position) {

			CommentFragment fragment = new CommentFragment();
			Bundle args = new Bundle();
			args.putString(CommentFragment.Extras.SUBREDDIT, mSubreddit);
			args.putString(CommentFragment.Extras.THREAD_ID, mMyPosts.get(position).getThreadId());
			args.putParcelable(CommentFragment.Extras.COMMENT, mMyPosts.get(position).getComment());
			fragment.setArguments(args);

			return fragment;

		}

		@Override
		public int getCount() {
			return mMyPosts.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (mMyPosts.get(position).getComment().getAuthor() != null)
				return mMyPosts.get(position).getComment().getAuthor().toUpperCase();

			return null;
		}

	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    // TODO Add your menu entries here
		
		inflater.inflate(R.menu.waywt, menu);
		
		mRefreshMenuItem = menu.findItem(R.id.refresh_menu_id);
		mLoadingMenuItem = menu.findItem(R.id.loading_menu_id);
		mLoadingMenuItem.setActionView(R.layout.actionbar_indeterminate_progress);
	}
	

	@Override
	public void onPrepareOptionsMenu(final Menu menu) {

		
//		// Login/Logout
//		if (mRedditSettings.isLoggedIn()) {
//			menu.findItem(R.id.login_menu_id).setVisible(false);
//			menu.findItem(R.id.logout_menu_id).setVisible(true);
//			menu.findItem(R.id.logout_menu_id).setTitle(String.format(getResources().getString(R.string.logout), mRedditSettings.getUsername()));
//		} else {
//			menu.findItem(R.id.login_menu_id).setVisible(true);
//			menu.findItem(R.id.logout_menu_id).setVisible(false);
//		}
//		
//		String sortByTxt = "RANDOM";
//
//		switch (UserUtil.getSortBy()) {
//		case 0:
//			sortByTxt = "RANDOM";
//			break;
//
//		case 1:
//			sortByTxt = "VOTES";
//			break;
//
//		case 2:
//			sortByTxt = "COMMENTS";
//			break;
//		}
//
//		menu.findItem(R.id.sort_by_menu_id).setTitle(String.format(getResources().getString(R.string.sort_by), sortByTxt));
//		
//		
//		String subredditTxt = "MFA";
//
//		if( UserUtil.getIsMale() )
//			subredditTxt = "MFA";
//		else
//			subredditTxt = "FFA";
//		
//		menu.findItem(R.id.subreddit_menu_id).setTitle(String.format(getResources().getString(R.string.subreddit), subredditTxt));

		
//		List<Integer> optionIds = new ArrayList<Integer>();
//		optionIds.add(R.id.login_menu_id);
//		optionIds.add(R.id.logout_menu_id);
//		optionIds.add(R.id.sort_by_menu_id);
//		
//		for (Integer id : optionIds) {
//			final MenuItem item = menu.findItem(id);
//
//			if (item != null) {
//				View actionView = item.getActionView();
//
//				if (actionView == null) {
//					Log.d("ACTIONBAR", "creating action view");
//					actionView = this.getLayoutInflater().inflate(R.layout.action_menu_button_layout, null, false);
//					((TextView) actionView.findViewById(R.id.action_menu_button_text)).setText(item.getTitle());
//					((TextView) actionView.findViewById(R.id.action_menu_button_text)).setTypeface(FontManager.INSTANCE.getAppFont());
////					actionView.setBackgroundResource(Common.);
//					actionView.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							menu.performIdentifierAction(item.getItemId(), 0);
//
//						}
//					});
//					item.setActionView(actionView);
//				} else if (actionView instanceof TextView) {
//					((TextView) actionView).setTypeface(FontManager.INSTANCE.getAppFont());
//				}
//			}
//		}
			
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		
			case R.id.camera_menu_id:
				if (!mRedditSettings.isLoggedIn())
				{
					getActivity().showDialog(Constants.DIALOG_LOGIN);  
					break;
				}
				
				if( mOPComment != null )
				{
					Intent intent = new Intent(getActivity(), CameraActivity.class);
					intent.putExtra(CameraActivity.Extras.OP_COMMENT, (Parcelable)mOPComment);
					startActivity(intent);					
				}

				break;
				
			case R.id.refresh_menu_id:
				fetchComments();
				break;
			
//		case R.id.login_menu_id:
//			showDialog(Constants.DIALOG_LOGIN);
//			break;
//		case R.id.logout_menu_id:
//			Common.doLogout(mRedditSettings, mRedditClient, getApplicationContext());
//			Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show();
//
//			mRedditSettings.saveRedditPreferences(this);
//			break;
//
//		case R.id.sort_by_menu_id:
//			showSortByDialog();
//			break;
//			
//		case R.id.subreddit_menu_id:
//			showSubredditDialog();
//			break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void updateMyPosts(List<MyPost> myPosts) {
		if (getView() != null) {


			mPagerAdapter.addMyPosts(myPosts);

			ViewFlipper vf = (ViewFlipper) getView().findViewById(R.id.vf);
			vf.setDisplayedChild(1);
			
			// UPDATE NAV BAR
//			MainActivity act = (MainActivity)getActivity();
//			
//			if( act != null )
//				act.updateCurrentNavItemDescription(comments.size()+" POSTS");
			
			
			Log.d("yoo", "yooo");
			
			// UPDATE MENU ITEMS
			if (mRefreshMenuItem != null && mLoadingMenuItem != null) {
				mRefreshMenuItem.setVisible(true);
				mLoadingMenuItem.setVisible(false);
			}
		}
	}
	


}
