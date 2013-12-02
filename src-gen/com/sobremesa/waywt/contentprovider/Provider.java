package com.sobremesa.waywt.contentprovider;

import com.sobremesa.waywt.database.Database;

import com.sobremesa.waywt.database.tables.*;

import android.provider.BaseColumns;
import android.text.TextUtils;
import android.content.ContentUris;
import android.database.sqlite.SQLiteQueryBuilder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Content provider implementation
 * The authority of the content provider is: content://com.sobremesa.waywt.provider.Model
 * 
 * More information about content providers:
 * @see <a href="http://developer.android.com/reference/android/content/ContentProvider.html">Reference</a>
 * @see <a href="http://developer.android.com/guide/topics/providers/content-providers.html">Tutorial</a>
 * @see <a href="http://developer.android.com/guide/topics/testing/contentprovider_testing.html">Content Provider Testing</a>
 *
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2013.12.01
 */
public class Provider extends ContentProvider {
	private static final String TAG = "com.sobremesa.waywt.contentprovider.Provider";

	public static final String AUTHORITY = "com.sobremesa.waywt.provider.Model";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	public static final Uri POST_CONTENT_URI = Uri.withAppendedPath(
			Provider.AUTHORITY_URI, PostContent.CONTENT_PATH);

	public static final Uri IMAGE_CONTENT_URI = Uri.withAppendedPath(
			Provider.AUTHORITY_URI, ImageContent.CONTENT_PATH);

	public static final Uri COMMENT_CONTENT_URI = Uri.withAppendedPath(
			Provider.AUTHORITY_URI, CommentContent.CONTENT_PATH);

	public static final Uri REPLY_CONTENT_URI = Uri.withAppendedPath(
			Provider.AUTHORITY_URI, ReplyContent.CONTENT_PATH);

	private static final UriMatcher URI_MATCHER;

	private Database db;

	private static final int POST_DIR = 0;
	private static final int POST_ID = 1;
	private static final int IMAGE_DIR = 2;
	private static final int IMAGE_ID = 3;
	private static final int COMMENT_DIR = 4;
	private static final int COMMENT_ID = 5;
	private static final int REPLY_DIR = 6;
	private static final int REPLY_ID = 7;

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(AUTHORITY, PostContent.CONTENT_PATH, POST_DIR);
		URI_MATCHER.addURI(AUTHORITY, PostContent.CONTENT_PATH + "/#", POST_ID);
		URI_MATCHER.addURI(AUTHORITY, ImageContent.CONTENT_PATH, IMAGE_DIR);
		URI_MATCHER.addURI(AUTHORITY, ImageContent.CONTENT_PATH + "/#",
				IMAGE_ID);
		URI_MATCHER.addURI(AUTHORITY, CommentContent.CONTENT_PATH, COMMENT_DIR);
		URI_MATCHER.addURI(AUTHORITY, CommentContent.CONTENT_PATH + "/#",
				COMMENT_ID);
		URI_MATCHER.addURI(AUTHORITY, ReplyContent.CONTENT_PATH, REPLY_DIR);
		URI_MATCHER.addURI(AUTHORITY, ReplyContent.CONTENT_PATH + "/#",
				REPLY_ID);
	}

	/**
	 * Provides the content information of the PostTable.
	 * 
	 * CONTENT_PATH: post (String)
	 * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.post (String)
	 * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.post (String)
	 * ALL_COLUMNS: Provides the same information as PostTable.ALL_COLUMNS (String[])
	 */
	public static final class PostContent implements BaseColumns {
		/**
		 * Specifies the content path of the PostTable for the required uri
		 * Exact URI: content://com.sobremesa.waywt.provider.Model/post
		 */
		public static final String CONTENT_PATH = "post";

		/**
		 * Specifies the type for the folder and the single item of the PostTable  
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.post";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.post";

		/**
		 * Contains all columns of the PostTable
		 */
		public static final String[] ALL_COLUMNS = PostTable.ALL_COLUMNS;
	}

	/**
	 * Provides the content information of the ImageTable.
	 * 
	 * CONTENT_PATH: image (String)
	 * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.image (String)
	 * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.image (String)
	 * ALL_COLUMNS: Provides the same information as ImageTable.ALL_COLUMNS (String[])
	 */
	public static final class ImageContent implements BaseColumns {
		/**
		 * Specifies the content path of the ImageTable for the required uri
		 * Exact URI: content://com.sobremesa.waywt.provider.Model/image
		 */
		public static final String CONTENT_PATH = "image";

		/**
		 * Specifies the type for the folder and the single item of the ImageTable  
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.image";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.image";

		/**
		 * Contains all columns of the ImageTable
		 */
		public static final String[] ALL_COLUMNS = ImageTable.ALL_COLUMNS;
	}

	/**
	 * Provides the content information of the CommentTable.
	 * 
	 * CONTENT_PATH: comment (String)
	 * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.comment (String)
	 * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.comment (String)
	 * ALL_COLUMNS: Provides the same information as CommentTable.ALL_COLUMNS (String[])
	 */
	public static final class CommentContent implements BaseColumns {
		/**
		 * Specifies the content path of the CommentTable for the required uri
		 * Exact URI: content://com.sobremesa.waywt.provider.Model/comment
		 */
		public static final String CONTENT_PATH = "comment";

		/**
		 * Specifies the type for the folder and the single item of the CommentTable  
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.comment";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.comment";

		/**
		 * Contains all columns of the CommentTable
		 */
		public static final String[] ALL_COLUMNS = CommentTable.ALL_COLUMNS;
	}

	/**
	 * Provides the content information of the ReplyTable.
	 * 
	 * CONTENT_PATH: reply (String)
	 * CONTENT_TYPE: vnd.android.cursor.dir/vnd.mdsdacp.reply (String)
	 * CONTENT_ITEM_TYPE: vnd.android.cursor.item/vnd.mdsdacp.reply (String)
	 * ALL_COLUMNS: Provides the same information as ReplyTable.ALL_COLUMNS (String[])
	 */
	public static final class ReplyContent implements BaseColumns {
		/**
		 * Specifies the content path of the ReplyTable for the required uri
		 * Exact URI: content://com.sobremesa.waywt.provider.Model/reply
		 */
		public static final String CONTENT_PATH = "reply";

		/**
		 * Specifies the type for the folder and the single item of the ReplyTable  
		 */
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mdsdacp.reply";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mdsdacp.reply";

		/**
		 * Contains all columns of the ReplyTable
		 */
		public static final String[] ALL_COLUMNS = ReplyTable.ALL_COLUMNS;
	}

	/**
	 * Instantiate the database, when the content provider is created
	 */
	@Override
	public final boolean onCreate() {
		db = new Database(getContext());
		return true;
	}

	/**
	 * Providing information whether uri returns an item or an directory.
	 * 
	 * @param uri from type Uri
	 * 
	 * @return content_type from type Content.CONTENT_TYPE or Content.CONTENT_ITEM_TYPE
	 *
	 */
	@Override
	public final String getType(final Uri uri) {
		switch (URI_MATCHER.match(uri)) {
			case POST_DIR :
				return PostContent.CONTENT_TYPE;
			case POST_ID :
				return PostContent.CONTENT_ITEM_TYPE;
			case IMAGE_DIR :
				return ImageContent.CONTENT_TYPE;
			case IMAGE_ID :
				return ImageContent.CONTENT_ITEM_TYPE;
			case COMMENT_DIR :
				return CommentContent.CONTENT_TYPE;
			case COMMENT_ID :
				return CommentContent.CONTENT_ITEM_TYPE;
			case REPLY_DIR :
				return ReplyContent.CONTENT_TYPE;
			case REPLY_ID :
				return ReplyContent.CONTENT_ITEM_TYPE;
			default :
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	/**
	 * Insert given values to given uri. Uri has to be from type directory (see switch-cases).
	 * Returns uri of inserted element.
	 *
	 * @param uri from type Uri
	 * @param values from type ContentValues
	 *
	 * @return uri of inserted element from type Uri
	 */
	@Override
	public final Uri insert(final Uri uri, final ContentValues values) {
		final SQLiteDatabase dbConnection = db.getWritableDatabase();

		try {
			dbConnection.beginTransaction();

			switch (URI_MATCHER.match(uri)) {
				case POST_DIR :
				case POST_ID :
					final long postid = dbConnection.insertOrThrow(
							PostTable.TABLE_NAME, null, values);
					final Uri newPost = ContentUris.withAppendedId(
							POST_CONTENT_URI, postid);
					getContext().getContentResolver().notifyChange(newPost,
							null);
					dbConnection.setTransactionSuccessful();
					return newPost;
				case IMAGE_DIR :
				case IMAGE_ID :
					final long imageid = dbConnection.insertOrThrow(
							ImageTable.TABLE_NAME, null, values);
					final Uri newImage = ContentUris.withAppendedId(
							IMAGE_CONTENT_URI, imageid);
					getContext().getContentResolver().notifyChange(newImage,
							null);
					dbConnection.setTransactionSuccessful();
					return newImage;
				case COMMENT_DIR :
				case COMMENT_ID :
					final long commentid = dbConnection.insertOrThrow(
							CommentTable.TABLE_NAME, null, values);
					final Uri newComment = ContentUris.withAppendedId(
							COMMENT_CONTENT_URI, commentid);
					getContext().getContentResolver().notifyChange(newComment,
							null);
					dbConnection.setTransactionSuccessful();
					return newComment;
				case REPLY_DIR :
				case REPLY_ID :
					final long replyid = dbConnection.insertOrThrow(
							ReplyTable.TABLE_NAME, null, values);
					final Uri newReply = ContentUris.withAppendedId(
							REPLY_CONTENT_URI, replyid);
					getContext().getContentResolver().notifyChange(newReply,
							null);
					dbConnection.setTransactionSuccessful();
					return newReply;
				default :
					throw new IllegalArgumentException("Unsupported URI:" + uri);
			}
		} catch (Exception e) {
			Log.e(TAG, "Insert Exception", e);
		} finally {
			dbConnection.endTransaction();
		}

		return null;
	}

	/**
	 * Updates given values of given uri, returning number of affected rows.
	 *
	 * @param uri from type Uri
	 * @param values from type ContentValues
	 * @param selection from type String
	 * @param selectionArgs from type String[]
	 *
	 * @return number of affected rows from type int
	 */
	@Override
	public final int update(final Uri uri, final ContentValues values,
			final String selection, final String[] selectionArgs) {

		final SQLiteDatabase dbConnection = db.getWritableDatabase();
		int updateCount = 0;

		try {
			dbConnection.beginTransaction();

			switch (URI_MATCHER.match(uri)) {

				case POST_DIR :
					updateCount = dbConnection.update(PostTable.TABLE_NAME,
							values, selection, selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				case POST_ID :
					final Long postId = ContentUris.parseId(uri);
					updateCount = dbConnection.update(
							PostTable.TABLE_NAME,
							values,
							PostTable.ID
									+ "="
									+ postId
									+ (TextUtils.isEmpty(selection)
											? ""
											: " AND (" + selection + ")"),
							selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;

				case IMAGE_DIR :
					updateCount = dbConnection.update(ImageTable.TABLE_NAME,
							values, selection, selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				case IMAGE_ID :
					final Long imageId = ContentUris.parseId(uri);
					updateCount = dbConnection.update(
							ImageTable.TABLE_NAME,
							values,
							ImageTable.ID
									+ "="
									+ imageId
									+ (TextUtils.isEmpty(selection)
											? ""
											: " AND (" + selection + ")"),
							selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;

				case COMMENT_DIR :
					updateCount = dbConnection.update(CommentTable.TABLE_NAME,
							values, selection, selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				case COMMENT_ID :
					final Long commentId = ContentUris.parseId(uri);
					updateCount = dbConnection.update(CommentTable.TABLE_NAME,
							values, CommentTable.ID
									+ "="
									+ commentId
									+ (TextUtils.isEmpty(selection)
											? ""
											: " AND (" + selection + ")"),
							selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;

				case REPLY_DIR :
					updateCount = dbConnection.update(ReplyTable.TABLE_NAME,
							values, selection, selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				case REPLY_ID :
					final Long replyId = ContentUris.parseId(uri);
					updateCount = dbConnection.update(
							ReplyTable.TABLE_NAME,
							values,
							ReplyTable.ID
									+ "="
									+ replyId
									+ (TextUtils.isEmpty(selection)
											? ""
											: " AND (" + selection + ")"),
							selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				default :
					throw new IllegalArgumentException("Unsupported URI:" + uri);
			}
		} finally {
			dbConnection.endTransaction();
		}

		if (updateCount > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return updateCount;

	}

	/**
	 * Deletes given elements by their uri (items or directories) and returns number of deleted rows.
	 *
	 * @param uri from type Uri
	 * @param selection from type String
	 * @param selectionArgs from type String[]
	 *
	 * @return number of deleted rows from type int
	 */
	@Override
	public final int delete(final Uri uri, final String selection,
			final String[] selectionArgs) {

		final SQLiteDatabase dbConnection = db.getWritableDatabase();
		int deleteCount = 0;

		try {
			dbConnection.beginTransaction();

			switch (URI_MATCHER.match(uri)) {
				case POST_DIR :
					deleteCount = dbConnection.delete(PostTable.TABLE_NAME,
							selection, selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				case POST_ID :
					deleteCount = dbConnection.delete(PostTable.TABLE_NAME,
							PostTable.WHERE_ID_EQUALS, new String[]{uri
									.getPathSegments().get(1)});
					dbConnection.setTransactionSuccessful();
					break;
				case IMAGE_DIR :
					deleteCount = dbConnection.delete(ImageTable.TABLE_NAME,
							selection, selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				case IMAGE_ID :
					deleteCount = dbConnection.delete(ImageTable.TABLE_NAME,
							ImageTable.WHERE_ID_EQUALS, new String[]{uri
									.getPathSegments().get(1)});
					dbConnection.setTransactionSuccessful();
					break;
				case COMMENT_DIR :
					deleteCount = dbConnection.delete(CommentTable.TABLE_NAME,
							selection, selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				case COMMENT_ID :
					deleteCount = dbConnection.delete(CommentTable.TABLE_NAME,
							CommentTable.WHERE_ID_EQUALS, new String[]{uri
									.getPathSegments().get(1)});
					dbConnection.setTransactionSuccessful();
					break;
				case REPLY_DIR :
					deleteCount = dbConnection.delete(ReplyTable.TABLE_NAME,
							selection, selectionArgs);
					dbConnection.setTransactionSuccessful();
					break;
				case REPLY_ID :
					deleteCount = dbConnection.delete(ReplyTable.TABLE_NAME,
							ReplyTable.WHERE_ID_EQUALS, new String[]{uri
									.getPathSegments().get(1)});
					dbConnection.setTransactionSuccessful();
					break;

				default :
					throw new IllegalArgumentException("Unsupported URI:" + uri);
			}
		} finally {
			dbConnection.endTransaction();
		}

		if (deleteCount > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return deleteCount;

	}

	/**
	 * Executes a query on a given uri and returns a Cursor with results.
	 *
	 * @param uri from type Uri
	 * @param projection from type String[]
	 * @param selection from type String 
	 * @param selectionArgs from type String[]
	 * @param sortOrder from type String
	 *
	 * @return cursor with results from type Cursor
	 */
	@Override
	public final Cursor query(final Uri uri, final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) {

		final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		final SQLiteDatabase dbConnection = db.getReadableDatabase();

		switch (URI_MATCHER.match(uri)) {
			case POST_ID :
				queryBuilder.appendWhere(PostTable.ID + "="
						+ uri.getPathSegments().get(1));
			case POST_DIR :
				queryBuilder.setTables(PostTable.TABLE_NAME);
				break;
			case IMAGE_ID :
				queryBuilder.appendWhere(ImageTable.ID + "="
						+ uri.getPathSegments().get(1));
			case IMAGE_DIR :
				queryBuilder.setTables(ImageTable.TABLE_NAME);
				break;
			case COMMENT_ID :
				queryBuilder.appendWhere(CommentTable.ID + "="
						+ uri.getPathSegments().get(1));
			case COMMENT_DIR :
				queryBuilder.setTables(CommentTable.TABLE_NAME);
				break;
			case REPLY_ID :
				queryBuilder.appendWhere(ReplyTable.ID + "="
						+ uri.getPathSegments().get(1));
			case REPLY_DIR :
				queryBuilder.setTables(ReplyTable.TABLE_NAME);
				break;
			default :
				throw new IllegalArgumentException("Unsupported URI:" + uri);
		}

		Cursor cursor = queryBuilder.query(dbConnection, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;

	}

}
