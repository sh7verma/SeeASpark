package utils

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.seeaspark.AfterWalkThroughActivity
import database.Database
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by applify on 5/25/2018.
 */
class ConstantsKotlin {
    var EMPTY = ""

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }


    fun getLocalTime(time: Long): Long {
        var localTime: Long = 0
        var dateValue = ""
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val calUtc = Calendar.getInstance()

            val localFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            localFormat.timeZone = TimeZone.getDefault()
            val calLocal = Calendar.getInstance()

            calUtc.timeInMillis = time
            dateValue = utcFormat.format(calUtc.time)
            val value = utcFormat.parse(dateValue)

            dateValue = localFormat.format(value)
            val localvalue = localFormat.parse(dateValue)
            localTime = localvalue.time

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return localTime
    }


    fun getUtcTime(time: Long): Long {
        var utcTime: Long = 0
        var dateValue = ""
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val calUtc = Calendar.getInstance()

            val localFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            localFormat.timeZone = TimeZone.getDefault()
            val calLocal = Calendar.getInstance()

            calLocal.timeInMillis = time
            dateValue = localFormat.format(calLocal.time)
            val value = localFormat.parse(dateValue)

            dateValue = utcFormat.format(value)
            calUtc.time = utcFormat.parse(dateValue)
            utcTime = calUtc.timeInMillis
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return utcTime
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
                inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    fun getImageOrientation(imagePath: String): Int {
        var rotate = 0
        try {
            val imageFile = File(imagePath)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return rotate
    }

    fun getRealPathFromURI(context: Context, contentURI: Uri): String {
        val result: String
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun closeKeyboard(mContext: Context, view: View) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(mContext: Context, view: View) {
        val inputMethodManager = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
                view.applicationWindowToken,
                InputMethodManager.SHOW_FORCED, 0)
    }

    fun moveToSplash(mContext: Context, utils: Utils) {
        val notificationManager = mContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        utils.clear_shf()
        val db = Database(mContext)
        db.deleteAllTables()
        val inSplash = Intent(mContext, AfterWalkThroughActivity::class.java)
        inSplash.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        inSplash.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mContext.startActivity(inSplash)
        System.exit(2)
    }

    @Throws(ParseException::class)
    fun convertDateTime(endDate: String): String {
        try {
            val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            return displayFormat.format(parseFormat.parse(endDate))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""

    }

    @Throws(ParseException::class)
    fun convertDate(endDate: String): String {
        try {
            val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            return displayFormat.format(parseFormat.parse(endDate))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""

    }

    @Throws(ParseException::class)
    fun convertSelectdDate(endDate: String): String {
        try {
            val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
            val parseFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            return displayFormat.format(parseFormat.parse(endDate))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    @Throws(ParseException::class)
    fun convertReviewsDate(endDate: String): String {
        try {
            val displayFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
            val parseFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            return displayFormat.format(parseFormat.parse(endDate))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    @Throws(ParseException::class)
    fun displayDateTime(endDate: String): String {

        val utc_format = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
        val date_gmt = SimpleDateFormat("Z", Locale.US)
        val gmt_text = date_gmt.format(calendar.time)

        val utc_date = utc_format.parse(endDate)
        val utc_create = Calendar.getInstance()
        utc_create.time = utc_date

        var hh = 0
        var mm = 0
        if (gmt_text.trim { it <= ' ' }.length == 3) {

        } else {
            hh = Integer.parseInt(gmt_text.substring(1, 3))
            mm = Integer.parseInt(gmt_text.substring(3, 5))

            if (gmt_text.substring(0, 1) == "+") {
                utc_create.add(Calendar.HOUR_OF_DAY, hh)
                utc_create.add(Calendar.MINUTE, mm)
            } else if (gmt_text.substring(0, 1) == "-") {
                utc_create.add(Calendar.HOUR_OF_DAY, -hh)
                utc_create.add(Calendar.MINUTE, -mm)
            }
        }

        val dateFormat = SimpleDateFormat("dd MMM hh:mm aa")
        return dateFormat.format(utc_create.time)
    }


}