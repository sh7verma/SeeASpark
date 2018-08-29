package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.Toast
import com.ipaulpro.afilechooser.utils.FileUtils
import com.soundcloud.android.crop.Crop
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_attachment.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext

/**
 * Created by dev on 7/8/18.
 */
class AttachmentActivity : BaseActivity() {

    internal var select_path = ""
    internal var crop_path = ""
    internal var saved_path = ""
    internal var fname = ""
    internal var pic: Bitmap? = null
    internal var max_color = 0
    internal var name = ""
    internal var mPic = ""

    override fun getContentView() = R.layout.activity_attachment

    override fun initUI() {
        select_path = intent.getStringExtra("select_path")
        name = intent.getStringExtra("name")
        mPic = intent.extras!!.getString("pic")
        txtName.text = name
        Picasso.with(this).load(mPic).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)

        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        val mat = Matrix()
        try {
            val angle = getImageOrientation(select_path)
            mat.postRotate(angle.toFloat())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        pic = BitmapFactory.decodeFile(select_path)

        if (pic == null) {
            Toast.makeText(this, resources.getString(R.string.invalid_value), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (pic!!.getHeight() > getMaxTextureSize() || pic!!.getWidth() > getMaxTextureSize()) {
            pic = BitmapFactory.decodeFile(select_path, options)
        } else {
            pic = Bitmap.createBitmap(pic, 0, 0, pic!!.getWidth(),
                    pic!!.getHeight(), mat, true)
        }

        imgSelectedImage.setImageBitmap(pic)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
//        llOuterAttachment.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
//        txtSendPhoto.setTextColor(ContextCompat.getColor(this, R.color.black_color))
//        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
//        llOuterAttachment.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
//        txtSendPhoto.setTextColor(ContextCompat.getColor(this, R.color.white_color))
//        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        imgBack.setOnClickListener(this)
        imgCrop.setOnClickListener(this)
        imgSend.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                moveBack()
            }
            imgCrop -> {
                val f = File(select_path)
                val rr = Uri.fromFile(File(f.absolutePath))
                beginCrop(rr)
            }
            imgSend -> {
                sendMessage("")
            }
        }
    }

    private fun beginCrop(source: Uri) {
        val destination = Uri.fromFile(File(cacheDir, "cropped"))
        Crop.of(source, destination).asSquare().start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Crop.REQUEST_CROP -> {
                    Log.e("crop photo", "is " + data)
                    handleCrop(resultCode, data!!)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    override fun onActivityResult(arg0: Int, arg1: Int, arg2: Intent?) {
//        // TODO Auto-generated method stub
//        if (arg1 == RESULT_OK) {
//            if (arg0!! == Crop.REQUEST_CROP) {
//                handleCrop(arg1, arg2)
//            }
//        }
//        super.onActivityResult(arg0, arg1, arg2)
//    }

    private fun handleCrop(resultCode: Int, result: Intent) {
        try {
            if (resultCode == RESULT_OK) {
                val ur = Crop.getOutput(result)
                val picturePath = FileUtils.getPath(this, ur)
                crop_path = picturePath
                var picCrop = BitmapFactory.decodeFile(picturePath)

                val mat = Matrix()
                try {
                    val angle = getImageOrientation(picturePath)
                    mat.postRotate(angle.toFloat())
                } catch (e: Exception) {
                }

                picCrop = Bitmap.createBitmap(picCrop, 0, 0,
                        picCrop.width, picCrop.height, mat, true)

                imgSelectedImage.setImageBitmap(picCrop)

            } else if (resultCode == Crop.RESULT_ERROR) {
                Toast.makeText(this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                        .show()
            }
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            Toast.makeText(this, resources.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
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

    fun getMaxTextureSize(): Int {
        // Safe minimum default size
        val IMAGE_MAX_BITMAP_DIMENSION = 2048

        // Get EGL Display
        val egl = EGLContext.getEGL() as EGL10
        val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)

        // Initialise
        val version = IntArray(2)
        egl.eglInitialize(display, version)

        // Query total number of configurations
        val totalConfigurations = IntArray(1)
        egl.eglGetConfigs(display, null, 0, totalConfigurations)

        // Query actual list configurations
        val configurationsList = arrayOfNulls<EGLConfig>(totalConfigurations[0])
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0],
                totalConfigurations)

        val textureSize = IntArray(1)
        var maximumTextureSize = 0

        // Iterate through all the configurations to located the maximum texture
        // size
        for (i in 0 until totalConfigurations[0]) {
            // Only need to check for width since opengl textures are always
            // squared
            egl.eglGetConfigAttrib(display, configurationsList[i],
                    EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize)

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0])
                maximumTextureSize = textureSize[0]
        }

        // Release
        egl.eglTerminate(display)

        // Return largest texture size found, or default
        return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION)
    }

    internal fun save_image(flag: Int) {
        if (flag == 2 && !TextUtils.isEmpty(crop_path)) {
            val mat = Matrix()
            try {
                val angle = getImageOrientation(crop_path)
                mat.postRotate(angle.toFloat())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            pic = BitmapFactory.decodeFile(crop_path)
            pic = Bitmap.createBitmap(pic, 0, 0, pic!!.getWidth(),
                    pic!!.getHeight(), mat, true)

        }
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/SeeASpark/Image/Sent")
        if (!myDir.exists())
            myDir.mkdirs()
        val cal = Calendar.getInstance()
        fname = "IMG" + cal.timeInMillis + ".jpg"
        val file = File(myDir, fname)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            pic!!.compress(Bitmap.CompressFormat.JPEG, 70, out)
            out.flush()
            out.close()

            saved_path = file.absolutePath
            refreshGallery(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun refreshGallery(file: File) {
        val mediaScanIntent = Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = Uri.fromFile(file)
        sendBroadcast(mediaScanIntent)
    }

    internal fun sendMessage(message: String) {
        save_image(2)
        val intent = Intent()
        intent.putExtra("selected_image", saved_path)
        intent.putExtra("selected_name", fname)
        setResult(RESULT_OK, intent)
        finish()
    }

}