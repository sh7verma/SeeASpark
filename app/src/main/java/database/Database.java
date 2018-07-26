package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;

import models.NotesListingModel;
import models.NotesModel;
import models.PostModel;
import utils.Utils;


public class Database extends SQLiteOpenHelper {

    private Utils utils;
    private static final int dbversion = 1;
    private static final String DATABASE = "see_a_spark_local";

    /// Events Table
    static final String POSTS_TABLE = "posts_table";
    static final String POST_ID = "post_id";
    static final String POST_TYPE = "post_type";
    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String PROFESSION_ID = "profession_id";
    static final String ADDRESS = "address";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";
    static final String DATE_TIME = "date_time";
    static final String URL = "url";
    static final String LIKE_COUNT = "like_count";
    static final String COMMENT_COUNT = "comment_count";
    static final String GOING = "going";
    static final String INTRESTED = "interested";
    static final String IS_BOOKMARKED = "is_bookmarked";
    static final String LIKED = "liked";
    static final String IS_GOING = "is_going";

    /// Images Table
    static final String IMAGES_TABLE = "images_table";
    static final String IMAGE_ID = "image_id";
    static final String IMAGE_URL = "image_url";
    static final String THUMBNAIL_URL = "thumnamil_url";

    /// Going Table
    static final String GOING_TABLE = "going_table";
    static final String FULL_NAME = "full_name";
    static final String AVATAR = "avatar";
    static final String USER_ID = "user_id";

    /// NOtes Table
    static final String NOTES_TABLE = "notes_table";
    static final String NOTES_ID = "notes_id";
    static final String NOTES_NAME = "notes_name";
    static final String NOTES_TITLE = "notes_title";
    static final String NOTES_DESC = "notes_desc";
    static final String NOTES_URL = "notes_url";
    static final String NOTES_TYPE = "notes_type";
    static final String NOTES_CREATED = "notes_created";
    static final String NOTES_UPDATED = "notes_updated";
    static final String NOTES_USER_ID = "note_user_id";
    static final String NOTES_USER_NAME = "note_user_name";

    public Database(Context context) {
        super(context, DATABASE, null, dbversion);
        utils = new Utils(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        String postsQuery = "create table if not exists " + POSTS_TABLE
                + " (" + POST_ID + " TEXT ,"
                + POST_TYPE + " TEXT ,"
                + TITLE + " TEXT ,"
                + DESCRIPTION + " TEXT ,"
                + PROFESSION_ID + " TEXT ,"
                + ADDRESS + " TEXT ,"
                + LATITUDE + " TEXT ,"
                + LONGITUDE + " TEXT ,"
                + DATE_TIME + " TEXT ,"
                + URL + " TEXT ,"
                + LIKE_COUNT + " TEXT ,"
                + COMMENT_COUNT + " TEXT ,"
                + GOING + " TEXT ,"
                + INTRESTED + " TEXT ,"
                + IS_BOOKMARKED + " TEXT ,"
                + LIKED + " TEXT ,"
                + IS_GOING + " TEXT )";
        db.execSQL(postsQuery);

        String imagesQuery = "create table if not exists " + IMAGES_TABLE
                + " (" + IMAGE_ID + " TEXT ,"
                + IMAGE_URL + " TEXT ,"
                + THUMBNAIL_URL + " TEXT ,"
                + POST_ID + " TEXT ,"
                + POST_TYPE + " TEXT )";
        db.execSQL(imagesQuery);

        String goingQuery = "create table if not exists " + GOING_TABLE
                + " (" + FULL_NAME + " TEXT ,"
                + AVATAR + " TEXT ,"
                + USER_ID + " TEXT ,"
                + POST_ID + " TEXT )";
        db.execSQL(goingQuery);

        String notesQuery = "create table if not exists " + NOTES_TABLE
                + " (" + NOTES_ID + " TEXT ,"
                + NOTES_NAME + " TEXT ,"
                + NOTES_TITLE + " TEXT ,"
                + NOTES_DESC + " TEXT ,"
                + NOTES_URL + " TEXT ,"
                + NOTES_TYPE + " TEXT ,"
                + NOTES_CREATED + " TEXT ,"
                + NOTES_UPDATED + " TEXT ,"
                + NOTES_USER_ID + " TEXT ,"
                + NOTES_USER_NAME + " TEXT )";
        db.execSQL(notesQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addNotes(NotesListingModel.ResponseBean notesData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor data = null;
        try {
            ContentValues values = new ContentValues();
            values.put(NOTES_NAME, notesData.getName());
            values.put(NOTES_TITLE, notesData.getTitle());
            values.put(NOTES_DESC, notesData.getDescription());
            values.put(NOTES_URL, notesData.getUrl());
            values.put(NOTES_TYPE, notesData.getNote_type());
            values.put(NOTES_CREATED, notesData.getCreated_at());
            values.put(NOTES_UPDATED, notesData.getUpdated_at());
            values.put(NOTES_USER_ID, notesData.getUser_id());
            values.put(NOTES_USER_NAME, notesData.getFull_name());

            data = getReadableDatabase().rawQuery("Select * from " + NOTES_TABLE + " where "
                    + NOTES_ID + " = '" + notesData.getId() + "'", null);
            if (data.getCount() > 0) {
                db.update(NOTES_TABLE, values, NOTES_ID + " = '" + notesData.getId() + "'", null);
            } else {
                values.put(NOTES_ID, notesData.getId());
                db.insertOrThrow(NOTES_TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (data != null)
                data.close();
        }
    }

    public ArrayList<NotesListingModel.ResponseBean> getNotesByType(String noteType) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cur = null;
        ArrayList<NotesListingModel.ResponseBean> mArrayListNotes = new ArrayList<>();
        try {
            String qry = "select * from " + NOTES_TABLE + " where " + NOTES_TYPE + " = '" + noteType + "' order by " + NOTES_UPDATED + " DESC";
            cur = db.rawQuery(qry, null);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                NotesListingModel.ResponseBean notesModel = new NotesListingModel.ResponseBean();
                notesModel.setId(Integer.parseInt(cur.getString(0)));
                notesModel.setName(cur.getString(1));
                notesModel.setTitle(cur.getString(2));
                notesModel.setDescription(cur.getString(3));
                notesModel.setUrl(cur.getString(4));
                notesModel.setNote_type(cur.getString(5));
                notesModel.setCreated_at(cur.getString(6));
                notesModel.setUpdated_at(cur.getString(7));
                notesModel.setUser_id(Integer.parseInt(cur.getString(8)));
                notesModel.setFull_name(cur.getString(9));

                mArrayListNotes.add(notesModel);
                cur.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (cur != null)
                cur.close();
        }
        return mArrayListNotes;
    }

    public void deleteNotes(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String unShortlist = "Delete from " + NOTES_TABLE + " where " + NOTES_ID + " = '" + noteId + "'";
            db.execSQL(unShortlist);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }

    public void addPosts(PostModel.ResponseBean postData) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor data = null;
        try {
            ContentValues values = new ContentValues();
            values.put(POST_TYPE, postData.getPost_type());
            values.put(TITLE, postData.getTitle());
            values.put(DESCRIPTION, postData.getDescription());
            values.put(PROFESSION_ID, postData.getProfession_id());
            values.put(ADDRESS, postData.getAddress());
            values.put(LATITUDE, postData.getLatitude());
            values.put(LONGITUDE, postData.getLongitude());
            values.put(DATE_TIME, postData.getDate_time());
            values.put(URL, postData.getUrl());
            values.put(LIKE_COUNT, postData.getLike());
            values.put(COMMENT_COUNT, postData.getComment());
            values.put(GOING, postData.getGoing());
            values.put(INTRESTED, postData.getInterested());
            values.put(IS_BOOKMARKED, postData.getBookmarked());
            values.put(LIKED, postData.getLiked());
            values.put(IS_GOING, postData.getIs_going());

            data = getReadableDatabase().rawQuery("Select * from " + POSTS_TABLE + " where "
                    + POST_ID + " = '" + postData.getId() + "'", null);
            if (data.getCount() > 0) {
                db.update(POSTS_TABLE, values, POST_ID + " = '" + postData.getId() + "'", null);
            } else {
                values.put(POST_ID, postData.getId());
                db.insertOrThrow(POSTS_TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (data != null)
                data.close();
        }
    }

    public ArrayList<PostModel.ResponseBean> getPostsByType(int postType) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cur = null;
        ArrayList<PostModel.ResponseBean> mArrayListPosts = new ArrayList<>();
        try {
            String qry = "select * from " + POSTS_TABLE + " where " + POST_TYPE + " = '" + postType + "'";
            cur = db.rawQuery(qry, null);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                PostModel.ResponseBean postModel = new PostModel.ResponseBean();
                postModel.setId(Integer.parseInt(cur.getString(0)));
                postModel.setPost_type(Integer.parseInt(cur.getString(1)));
                postModel.setTitle(cur.getString(2));
                postModel.setDescription(cur.getString(3));
                postModel.setProfession_id(cur.getString(4));
                postModel.setAddress(cur.getString(5));
                postModel.setLatitude(cur.getString(6));
                postModel.setLongitude(cur.getString(7));
                postModel.setDate_time(cur.getString(8));
                postModel.setUrl(cur.getString(9));
                postModel.setLike(Integer.parseInt(cur.getString(10)));
                postModel.setComment(Integer.parseInt(cur.getString(11)));
                postModel.setGoing(Integer.parseInt(cur.getString(12)));
                postModel.setInterested(Integer.parseInt(cur.getString(13)));
                postModel.setBookmarked(Integer.parseInt(cur.getString(14)));
                postModel.setLiked(Integer.parseInt(cur.getString(15)));
                postModel.setIs_going(Integer.parseInt(cur.getString(16)));
                postModel.setGoing_list(getPostGoingUsers(cur.getString(0)));
                postModel.setImages(getImageByPostId(cur.getString(0)));

                mArrayListPosts.add(postModel);
                cur.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (cur != null)
                cur.close();
        }
        return mArrayListPosts;
    }

    public void addPostImages(PostModel.ResponseBean.ImagesBean imagesData, String postId, int postType) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor data = null;
        try {
            ContentValues values = new ContentValues();
            values.put(IMAGE_URL, imagesData.getImage_url());
            values.put(THUMBNAIL_URL, imagesData.getThumbnail_url());
            values.put(POST_ID, postId);
            values.put(POST_TYPE, postType);

            data = getReadableDatabase().rawQuery("Select * from " + IMAGES_TABLE + " where "
                    + IMAGE_ID + " = '" + imagesData.getId() + "'", null);
            if (data.getCount() > 0) {
                db.update(IMAGES_TABLE, values, IMAGE_ID + " = '" + imagesData.getId() + "'", null);
            } else {
                values.put(IMAGE_ID, imagesData.getId());
                db.insertOrThrow(IMAGES_TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (data != null)
                data.close();
        }
    }

    public ArrayList<PostModel.ResponseBean.ImagesBean> getImageByPostId(String postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cur = null;
        ArrayList<PostModel.ResponseBean.ImagesBean> mArrayListImages = new ArrayList<>();
        try {
            String qry = "select * from " + IMAGES_TABLE + " where " + POST_ID + " = '" + postId + "'";
            cur = db.rawQuery(qry, null);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                PostModel.ResponseBean.ImagesBean imagesModel = new PostModel.ResponseBean.ImagesBean();
                imagesModel.setId(Integer.parseInt(cur.getString(0)));
                imagesModel.setImage_url(cur.getString(1));
                imagesModel.setThumbnail_url(cur.getString(2));
                mArrayListImages.add(imagesModel);
                cur.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (cur != null)
                cur.close();
        }
        return mArrayListImages;
    }

    public void updateGoingStatusEvents(int postId, int goingStatus, int interested) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(IS_GOING, goingStatus);
            cv.put(INTRESTED, interested);
            db.update(POSTS_TABLE, cv, POST_ID + " = '" + postId + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }


    public void updateLikeStatus(int postId, int likeStatus, int likeCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(LIKED, likeStatus);
            cv.put(LIKE_COUNT, likeCount);
            db.update(POSTS_TABLE, cv, POST_ID + " = '" + postId + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }

    public void updateBookmarkStatus(int postId, int bookmarkStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(IS_BOOKMARKED, bookmarkStatus);
            db.update(POSTS_TABLE, cv, POST_ID + " = '" + postId + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }

    public void updateCommentCount(int postId, int commentCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COMMENT_COUNT, commentCount);
            db.update(POSTS_TABLE, cv, POST_ID + " = '" + postId + "'", null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }


    public void addPostGoingUsers(PostModel.ResponseBean.GoingUserBean goingUserData, String postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor data = null;
        try {
            ContentValues values = new ContentValues();
            values.put(AVATAR, goingUserData.getAvatar());
            values.put(FULL_NAME, goingUserData.getFull_name());
            values.put(POST_ID, postId);

            data = getReadableDatabase().rawQuery("Select * from " + GOING_TABLE + " where "
                    + USER_ID + " = '" + goingUserData.getId() + "'", null);
            if (data.getCount() > 0) {
                db.update(GOING_TABLE, values, USER_ID + " = '" + goingUserData.getId() + "'", null);
            } else {
                values.put(USER_ID, goingUserData.getId());
                db.insertOrThrow(GOING_TABLE, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (data != null)
                data.close();
        }
    }

    public ArrayList<PostModel.ResponseBean.GoingUserBean> getPostGoingUsers(String postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cur = null;
        ArrayList<PostModel.ResponseBean.GoingUserBean> mArrayListGoing = new ArrayList<>();
        try {
            String qry = "select * from " + GOING_TABLE + " where " + POST_ID + " = '" + postId + "'";
            cur = db.rawQuery(qry, null);
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                PostModel.ResponseBean.GoingUserBean goingModel = new PostModel.ResponseBean.GoingUserBean();
                goingModel.setFull_name(cur.getString(0));
                goingModel.setAvatar(cur.getString(1));
                goingModel.setId(Integer.parseInt(cur.getString(2)));
                mArrayListGoing.add(goingModel);
                cur.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (cur != null)
                cur.close();
        }
        return mArrayListGoing;
    }

    public PostModel.ResponseBean getPostDataById(int postId, int postType) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cur = null;
        PostModel.ResponseBean postModel = new PostModel.ResponseBean();
        try {
            String qry = "select * from " + POSTS_TABLE + " where " + POST_TYPE + " = '" + postType + "' and " + POST_ID + " = '" + postId + "'";
            cur = db.rawQuery(qry, null);
            cur.moveToFirst();
            postModel.setId(Integer.parseInt(cur.getString(0)));
            postModel.setPost_type(Integer.parseInt(cur.getString(1)));
            postModel.setTitle(cur.getString(2));
            postModel.setDescription(cur.getString(3));
            postModel.setProfession_id(cur.getString(4));
            postModel.setAddress(cur.getString(5));
            postModel.setLatitude(cur.getString(6));
            postModel.setLongitude(cur.getString(7));
            postModel.setDate_time(cur.getString(8));
            postModel.setUrl(cur.getString(9));
            postModel.setLike(Integer.parseInt(cur.getString(10)));
            postModel.setComment(Integer.parseInt(cur.getString(11)));
            postModel.setGoing(Integer.parseInt(cur.getString(12)));
            postModel.setInterested(Integer.parseInt(cur.getString(13)));
            postModel.setBookmarked(Integer.parseInt(cur.getString(14)));
            postModel.setLiked(Integer.parseInt(cur.getString(15)));
            postModel.setIs_going(Integer.parseInt(cur.getString(16)));
            postModel.setGoing_list(getPostGoingUsers(cur.getString(0)));
            postModel.setImages(getImageByPostId(cur.getString(0)));

        } catch (Exception e) {
            Log.e("Exception", "is " + e);
        } finally {
            db.endTransaction();
            if (cur != null)
                cur.close();
        }
        return postModel;
    }

    public void removeGoingUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String unShortlist = "Delete from " + GOING_TABLE + " where " + USER_ID + " = '" + userId + "'";
            db.execSQL(unShortlist);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteEventData(int postType) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String dropEvents = "Delete from  " + POSTS_TABLE + " where " + POST_TYPE + " = '" + postType + "'";
            db.execSQL(dropEvents);

            String dropImages = "Delete from  " + IMAGES_TABLE + " where " + POST_TYPE + " = '" + postType + "'";
            db.execSQL(dropImages);

            String dropGoingUsers = "Delete from  " + GOING_TABLE;
            db.execSQL(dropGoingUsers);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteCommunityData(int postType) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String dropEvents = "Delete from  " + POSTS_TABLE + " where " + POST_TYPE + " = '" + postType + "'";
            db.execSQL(dropEvents);

            String dropImages = "Delete from  " + IMAGES_TABLE + " where " + POST_TYPE + " = '" + postType + "'";
            db.execSQL(dropImages);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }


    public void deletePostById(int postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String dropEvents = "Delete from  " + POSTS_TABLE + " where " + POST_ID + " = '" + postId + "'";
            db.execSQL(dropEvents);

            String dropImages = "Delete from  " + IMAGES_TABLE + " where " + POST_ID + " = '" + postId + "'";
            db.execSQL(dropImages);

            String dropGoingUsers = "Delete from  " + GOING_TABLE + " where " + POST_ID + " = '" + postId + "'";
            db.execSQL(dropGoingUsers);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String dropEvents = "Delete from  " + POSTS_TABLE;
            db.execSQL(dropEvents);

            String dropImages = "Delete from  " + IMAGES_TABLE;
            db.execSQL(dropImages);

            String dropGoingUsers = "Delete from  " + GOING_TABLE;
            db.execSQL(dropGoingUsers);

            String dropNotes = "Delete from  " + NOTES_TABLE;
            db.execSQL(dropNotes);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Exception = ", e + "");
        } finally {
            db.endTransaction();
        }
    }
}
