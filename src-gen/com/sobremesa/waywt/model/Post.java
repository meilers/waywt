package com.sobremesa.waywt.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.sobremesa.waywt.database.tables.PostTable;

/**
 * Generated model class for usage in your application, defined by classifiers in ecore diagram
 * 		
 * Generated Class. Do not modify!
 * 
 * @author MDSDACP Team - goetzfred@fh-bingen.de 
 * @date 2014.01.25	 
 */
public class Post {

	private Long id;
	private int ups;
	private int downs;
	private java.lang.String author;
	private long created;
	private java.lang.String permalink;
	private java.lang.String title;
	private int is_male;
	private int is_teen;
	private int post_type;

	private final ContentValues values = new ContentValues();

	public Post() {
	}

	public Post(final Cursor cursor) {
		setId(cursor.getLong(cursor.getColumnIndex(PostTable.ID)));
		setUps(cursor.getInt(cursor.getColumnIndex(PostTable.UPS)));
		setDowns(cursor.getInt(cursor.getColumnIndex(PostTable.DOWNS)));
		setAuthor(cursor.getString(cursor.getColumnIndex(PostTable.AUTHOR)));
		setCreated(cursor.getLong(cursor.getColumnIndex(PostTable.CREATED)));
		setPermalink(cursor.getString(cursor
				.getColumnIndex(PostTable.PERMALINK)));
		setTitle(cursor.getString(cursor.getColumnIndex(PostTable.TITLE)));
		setIs_male(cursor.getInt(cursor.getColumnIndex(PostTable.IS_MALE)));
		setIs_teen(cursor.getInt(cursor.getColumnIndex(PostTable.IS_TEEN)));
		setPost_type(cursor.getInt(cursor.getColumnIndex(PostTable.POST_TYPE)));

	}

	/**
	 * Set id
	 *
	 * @param id from type java.lang.Long
	 */
	public void setId(final Long id) {
		this.id = id;
		this.values.put(PostTable.ID, id);
	}

	/**
	 * Get id
	 *
	 * @return id from type java.lang.Long				
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Set ups and set content value
	 *
	 * @param ups from type int
	 */
	public void setUps(final int ups) {
		this.ups = ups;
		this.values.put(PostTable.UPS, ups);
	}

	/**
	 * Get ups
	 *
	 * @return ups from type int				
	 */
	public int getUps() {
		return this.ups;
	}

	/**
	 * Set downs and set content value
	 *
	 * @param downs from type int
	 */
	public void setDowns(final int downs) {
		this.downs = downs;
		this.values.put(PostTable.DOWNS, downs);
	}

	/**
	 * Get downs
	 *
	 * @return downs from type int				
	 */
	public int getDowns() {
		return this.downs;
	}

	/**
	 * Set author and set content value
	 *
	 * @param author from type java.lang.String
	 */
	public void setAuthor(final java.lang.String author) {
		this.author = author;
		this.values.put(PostTable.AUTHOR, author);
	}

	/**
	 * Get author
	 *
	 * @return author from type java.lang.String				
	 */
	public java.lang.String getAuthor() {
		return this.author;
	}

	/**
	 * Set created and set content value
	 *
	 * @param created from type long
	 */
	public void setCreated(final long created) {
		this.created = created;
		this.values.put(PostTable.CREATED, created);
	}

	/**
	 * Get created
	 *
	 * @return created from type long				
	 */
	public long getCreated() {
		return this.created;
	}

	/**
	 * Set permalink and set content value
	 *
	 * @param permalink from type java.lang.String
	 */
	public void setPermalink(final java.lang.String permalink) {
		this.permalink = permalink;
		this.values.put(PostTable.PERMALINK, permalink);
	}

	/**
	 * Get permalink
	 *
	 * @return permalink from type java.lang.String				
	 */
	public java.lang.String getPermalink() {
		return this.permalink;
	}

	/**
	 * Set title and set content value
	 *
	 * @param title from type java.lang.String
	 */
	public void setTitle(final java.lang.String title) {
		this.title = title;
		this.values.put(PostTable.TITLE, title);
	}

	/**
	 * Get title
	 *
	 * @return title from type java.lang.String				
	 */
	public java.lang.String getTitle() {
		return this.title;
	}

	/**
	 * Set is_male and set content value
	 *
	 * @param is_male from type int
	 */
	public void setIs_male(final int is_male) {
		this.is_male = is_male;
		this.values.put(PostTable.IS_MALE, is_male);
	}

	/**
	 * Get is_male
	 *
	 * @return is_male from type int				
	 */
	public int getIs_male() {
		return this.is_male;
	}

	/**
	 * Set is_teen and set content value
	 *
	 * @param is_teen from type int
	 */
	public void setIs_teen(final int is_teen) {
		this.is_teen = is_teen;
		this.values.put(PostTable.IS_TEEN, is_teen);
	}

	/**
	 * Get is_teen
	 *
	 * @return is_teen from type int				
	 */
	public int getIs_teen() {
		return this.is_teen;
	}

	/**
	 * Set post_type and set content value
	 *
	 * @param post_type from type int
	 */
	public void setPost_type(final int post_type) {
		this.post_type = post_type;
		this.values.put(PostTable.POST_TYPE, post_type);
	}

	/**
	 * Get post_type
	 *
	 * @return post_type from type int				
	 */
	public int getPost_type() {
		return this.post_type;
	}

	/**
	 * Get ContentValues
	 *
	 * @return id from type android.content.ContentValues with the values of this object				
	 */
	public ContentValues getContentValues() {
		return this.values;
	}
}
