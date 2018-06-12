package utils;

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

import database.Db;
import kotlin.collections.AbstractList;
import kotlin.jvm.JvmStatic;


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
    public static final String EMAIL_VERIFY = "email_verify";
    public static final String QUESTIONS = "questions";

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
        Db db = new Db(mContext);
        db.deleteAllTables();
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

    public static String convertSelectdDate(String endDate)
            throws ParseException {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
            SimpleDateFormat parseFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
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

        SimpleDateFormat utc_format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
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

        DateFormat dateFormat = new SimpleDateFormat("dd MMM hh:mm aa");
        return dateFormat.format(utc_create.getTime());
    }


}
