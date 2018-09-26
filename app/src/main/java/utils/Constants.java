package utils;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.seeaspark.AfterWalkThroughActivity;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import database.Database;
import models.SkillsModel;
import network.RetrofitClient;


public class Constants {

    public static String EMPTY = "";
    public static String EMAIL = "";
    public static int EMAIL_LOGIN = 0;
    public static int FACEBOOK_LOGIN = 1;
    public static int LIKENDIN_LOGIN = 2;
    public static int EMAIL_VERIFIED = 1;
    public static int EMAIL_NOTVERIFIED = 0;
    public static int MENTEE = 1;
    public static int MENTOR = 0;
    public static int PROCEED_AS_OTHER_UNDER_REVIEW = 2002;
    public static int PROCEED_NORMAL = 200;
    public static int PROCEED_AS_OTHER = 2001;
    public static int PROFILE_UNDER_REVIEW = 3001;
    public static int PROFILE_IN_REVIEW = 2005;
    public static final String EMAIL_VERIFY = "email_verify";
    public static final String REVIEW = "review";
    public static final String UNVERIFIED = "unverified";
    public static final String QUESTIONS = "questions";
    public static final String PROFILE_UPDATE = "profile_update";
    public static final int INVALID_ACCESS_TOKEN = 301;
    public static final int POST_DELETED = 1111;
    public static final int DELETE_ACCOUNT = 6001;
    public static final int COMMUNITY = 1;
    public static final int EVENT = 2;
    public static final int OUT_OF_CARD = 3;
    public static final int PROGRESS = 4;
    public static final int CARD = 0;
    public static final int DISTANCE = 15;
    public static final int EXPERIENCE = 3;
    public static ArrayList<SkillsModel> OWNSKILLS_ARRAY = new ArrayList<>();
    public static final String NIGHT_MODE = "NightMode";
    public static final String SWITCH_USER_TYPE = "switch_user_type";
    public static final Integer DAY = 1;
    public static final Integer NIGHT = 2;
    public static final int INTERESTED = 2;
    public static final int GOING = 1;
    public static final int LIKED = 1;
    public static final int LIKE = 3;
    public static final int UNLIKED = 0;
    public static final String POST_BROADCAST = "event_like";
    public static final String UNMATCH = "unmatch";
    public static final Integer BOOKMARK = 2;
    public static final Integer COMMENT = 3;
    public static final Integer DELETE = 4;
    public static final Integer CHANGE_POSITION = 5;

    public static final String AUTODAYMODE = "autoDay";
    public static final String MYNOTES = "1";
    public static final String RECEIVEDNOTES = "2";
    public static final int MALE=1;
    public static final int FEMALE=2;
    public static final int OTHER=3;

    /// Hunny Constants
    public final static String KEY_SELECTED_MEDIA = "SELECTED_PHOTOS";
    public final static String KEY_SELECTED_DOCS = "SELECTED_DOCS";

    public final static String EXTRA_PICKER_TYPE = "EXTRA_PICKER_TYPE";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String EXTRA_FILE_TYPE = "EXTRA_FILE_TYPE";
    public final static String EXTRA_BUCKET_ID = "EXTRA_BUCKET_ID";
    public final static String ALL_PHOTOS_BUCKET_ID = "ALL_PHOTOS_BUCKET_ID";
    public final static int DOC_PICKER = 0x12;
    public final static String PERMISSIONS_FILE_PICKER =
            Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public enum FILE_TYPE {
        PDF, WORD, EXCEL, PPT, TXT, UNKNOWN
    }

    public final static String PDF = "PDF";
    public final static String PPT = "PPT";
    public final static String DOC = "DOC";
    public final static String XLS = "XLS";
    public final static String TXT = "TXT";
    public static final int REQUEST_CODE_DOC = 234;
    public final static int DEFAULT_MAX_COUNT = -1;
    public final static int FILE_TYPE_MEDIA = 1;
    public final static int FILE_TYPE_DOCUMENT = 2;
    public final static int MEDIA_PICKER = 0x11;

    public final static String DEFAULT_MESSAGE_REGEX = "<*&^(SeeASpark)^&*>";
    public final static String USERS = "Users";
    public final static String CHATS = "Chats";
    public final static String MESSAGES = "Messages";
    public final static String READ_STATUS = "ReadStatus";
    public final static String NOTIFICATIONS = "Notifications";
    public final static Long ONLINE_LONG = 123L;
    public final static String ONLINE = "Online";
    public final static int TEXT_LENGTH = 2000;
    public final static int SHOW_TEXT_LENGTH = 500;
    public final static String POUND = "£";

    public final static String FILTER_MENTEE = "1";
    public final static String FILTER_MENTOR = "0";
    public final static String FILTER_BOTH = "2";

    public static final String SHARE_URL = RetrofitClient.BASE_URL + "share_user?id=";
    public final static int LOAD_MORE_VALUE = 20;

    public final static String AUDIO_PERMISSION = "audio_permission";
    public final static String CAMRA_PERMISSION = "camera_permission";
    public final static String GALLERY_PERMISSION = "gallery_permission";

    public final static String TYPE_TEXT = "1";
    public final static String TYPE_IMAGE = "2";
    public final static String TYPE_VIDEO = "3";
    public final static String TYPE_DOCUMENT = "4";
    public final static String TYPE_NOTES = "5";
    public final static String TYPE_AUDIO = "6";

    public final static String FILE_SUCCESS = "2";
    public final static String FILE_UPLOADING = "1";
    public final static String FILE_EREROR = "0";

    public final static String SERVICE_RUNNING = "service_running";

    public final static int STATUS_MESSAGE_PENDING = 0;
    public final static int STATUS_MESSAGE_SENT = 1;
    public final static int STATUS_MESSAGE_DELIVERED = 2;
    public final static int STATUS_MESSAGE_SEEN = 3;

    public static final String FIRBASE_STORAGE_DIRECTRY = "chat_uploads/";

    // public static final String fireBase_Database_Url= "https://seeaspark-96ec8.firebaseio.com/"; // report@applify.co //pass: w3lc0m3@!@#
    public static final String FIREBASE_SERVER_kEY = "AIzaSyC2znGBwGZQUfFQCmVa5fIIRZZEjlomNLc";
    public static final String FIREBASE_SENDER_ID = "367374373614";
    public static final String FIREBASE_STORAGE_URL = "gs://seeaspark-96ec8.appspot.com";

    public static boolean contains(String[] types, String path) {
        for (String string : types) {
            if (path.toLowerCase().endsWith(string)) return true;
        }
        return false;
    }

    ////

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public static long getLocalTime(long time) {
        long localTime = 0;
        String dateValue = "";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Calendar calUtc = Calendar.getInstance();

            SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            localFormat.setTimeZone(TimeZone.getDefault());
            Calendar calLocal = Calendar.getInstance();

            calUtc.setTimeInMillis(time);
            dateValue = utcFormat.format(calUtc.getTime());
            Date value = utcFormat.parse(dateValue);

            dateValue = localFormat.format(value);
            Date localvalue = localFormat.parse(dateValue);
            localTime = localvalue.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localTime;
    }


    public static long getUtcTime(long time) {
        long utcTime = 0;
        String dateValue = "";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Calendar calUtc = Calendar.getInstance();

            SimpleDateFormat localFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            localFormat.setTimeZone(TimeZone.getDefault());
            Calendar calLocal = Calendar.getInstance();

            calLocal.setTimeInMillis(time);
            dateValue = localFormat.format(calLocal.getTime());
            Date value = localFormat.parse(dateValue);

            dateValue = utcFormat.format(value);
            calUtc.setTime(utcFormat.parse(dateValue));
            utcTime = calUtc.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return utcTime;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void closeKeyboard(Context mContext, View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context mContext, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
      /*  inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);*/

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    public static void moveToSplash(Context mContext, Utils utils) {
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        utils.clear_shf();
        Database database = new Database(mContext);
        database.deleteAllTables();
        Intent inSplash = new Intent(mContext, AfterWalkThroughActivity.class);
        inSplash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        inSplash.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(inSplash);
        System.exit(2);
    }

    public static String convertDateTime(String endDate)
            throws ParseException {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            return displayFormat.format(parseFormat.parse(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static String convertDate(String endDate)
            throws ParseException {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            return displayFormat.format(parseFormat.parse(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static String convertSelectedDate(String endDate)
            throws ParseException {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.US);
            SimpleDateFormat parseFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
            return displayFormat.format(parseFormat.parse(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertReviewsDate(String endDate)
            throws ParseException {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
            SimpleDateFormat parseFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            return displayFormat.format(parseFormat.parse(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String displayDateTime(String endDate)
            throws ParseException {

        SimpleDateFormat utc_format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        DateFormat date_gmt = new SimpleDateFormat("Z", Locale.US);
        String gmt_text = date_gmt.format(calendar.getTime());

        Date utc_date = utc_format.parse(endDate);
        Calendar utc_create = Calendar.getInstance();
        utc_create.setTime(utc_date);

        int hh = 0, mm = 0;
        if (gmt_text.trim().length() == 3) {

        } else {
            hh = Integer.parseInt(gmt_text.substring(1, 3));
            mm = Integer.parseInt(gmt_text.substring(3, 5));

            if (gmt_text.substring(0, 1).equals("+")) {
                utc_create.add(Calendar.HOUR_OF_DAY, hh);
                utc_create.add(Calendar.MINUTE, mm);
            } else if (gmt_text.substring(0, 1).equals("-")) {
                utc_create.add(Calendar.HOUR_OF_DAY, -hh);
                utc_create.add(Calendar.MINUTE, -mm);
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa");
        return dateFormat.format(utc_create.getTime());
    }


    public static String displayDateTimeNotes(String endDate)
            throws ParseException {

        SimpleDateFormat utc_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        DateFormat date_gmt = new SimpleDateFormat("Z", Locale.US);
        String gmt_text = date_gmt.format(calendar.getTime());

        Date utc_date = utc_format.parse(endDate);
        Calendar utc_create = Calendar.getInstance();
        utc_create.setTime(utc_date);

        int hh = 0, mm = 0;
        if (gmt_text.trim().length() == 3) {

        } else {
            hh = Integer.parseInt(gmt_text.substring(1, 3));
            mm = Integer.parseInt(gmt_text.substring(3, 5));

            if (gmt_text.substring(0, 1).equals("+")) {
                utc_create.add(Calendar.HOUR_OF_DAY, hh);
                utc_create.add(Calendar.MINUTE, mm);
            } else if (gmt_text.substring(0, 1).equals("-")) {
                utc_create.add(Calendar.HOUR_OF_DAY, -hh);
                utc_create.add(Calendar.MINUTE, -mm);
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm aa");
        return dateFormat.format(utc_create.getTime());
    }


}
