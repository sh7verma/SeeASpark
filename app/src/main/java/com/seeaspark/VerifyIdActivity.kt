package com.seeaspark

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.cameraview.CameraView
import com.ipaulpro.afilechooser.utils.FileUtils
import com.soundcloud.android.crop.Crop
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_verify_id.*
import kotlinx.android.synthetic.main.tool_bar.*
import models.SignupModel
import network.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.Constants.INVALID_ACCESS_TOKEN
import java.io.*
import java.util.*


@Suppress("DEPRECATION")
class VerifyIdActivity : BaseActivity() {

    internal val GALLERY_INTENT = 2
    private val MULTIPLE_PERMISSIONS = 5
    internal var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private var mOption: Int = 0
    private var progDailog: ProgressDialog? = null
    var serverFile: File? = null

    private val TAG = "MainActivity"
    private var isFlashON: Boolean = false
    private var mBackgroundHandler: Handler? = null
    var userData: SignupModel? = null

    override fun initUI() {
        setSupportActionBar(toolBar)
        supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_back_org)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("")

        txtTitle.text = getString(R.string.verify_your_id)

    }

    override fun onCreateStuff() {

        userData = intent.getParcelableExtra("userData")

        if (userData!!.response.user_type == Constants.MENTEE) {
            txtOption.visibility = View.VISIBLE
            txtOption.text = getString(R.string.skip)
            txtOption.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        }

        if (mCameraView != null) {
            mCameraView.addCallback(mCallback)
        }
    }

    override fun initListener() {
        imgClick.setOnClickListener(this)
        imgFlash.setOnClickListener(this)
        imgGallery.setOnClickListener(this)
        llCancelDone.setOnClickListener(this)
        txtCancel.setOnClickListener(this)
        txtDoneVerify.setOnClickListener(this)
        txtOption.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_verify_id

    override fun getContext() = this

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home ->
                moveBack()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onClick(view: View?) {
        when (view) {
            imgClick -> {
                mOption = 1
                if (checkPermissions()) {
                    if (mCameraView != null) {
                        progDailog = ProgressDialog.show(this, "Please wait ...", "Capturing...", true)
                        mCameraView.takePicture()
                    }
                } else {
                    Toast.makeText(mContext, "Please enable camera permisssion in settings", Toast.LENGTH_LONG).show()
                }
            }
            imgFlash -> {
                if (mCameraView != null) {
                    if (isFlashON) {
                        mCameraView.flash = CameraView.FLASH_OFF
                        imgFlash.setImageResource(R.mipmap.ic_flash_off)
                        isFlashON = false
                    } else {
                        mCameraView.flash = CameraView.FLASH_ON
                        imgFlash.setImageResource(R.mipmap.ic_flash)
                        isFlashON = true
                    }
                }
            }
            imgGallery -> {
                mOption = 2
                if (checkPermissions()) {
                    showGallery()
                } else {
                    Toast.makeText(mContext, "Please enable Gallery permisssion in settings", Toast.LENGTH_LONG).show()
                }
            }
            txtCancel -> {
                llCancelDone.visibility = View.INVISIBLE
                imgDisplay.visibility = View.INVISIBLE
            }
            txtDoneVerify -> {
                if (connectedToInternet()) {
                    hitAPI()
                } else
                    showInternetAlert(txtDoneVerify)
            }
            txtOption -> {
                if (connectedToInternet())
                    hitAPI()
                else
                    showInternetAlert(llCancelDone)
            }
        }
    }

    private fun hitAPI() {

        showLoader()
        val tempLangauges = intent.getIntegerArrayListExtra("languages").toString()
                .substring(1, intent.getIntegerArrayListExtra("languages").toString().length - 1).trim()

        val tempSkills = intent.getStringArrayListExtra("skills").toString()
                .substring(1, intent.getStringArrayListExtra("skills").toString().length - 1).trim()

        var call: Call<SignupModel>? = null

        if (serverFile != null)
            call = RetrofitClient.getInstance().createProfile(
                    createPartFromString(userData!!.response.access_token),
                    createPartFromString(intent.getStringExtra("avatarId")),
                    createPartFromString(userData!!.response.user_type.toString()),
                    createPartFromString(intent.getStringExtra("name")),
                    createPartFromString(intent.getStringExtra("dob")),
                    createPartFromString(intent.getStringExtra("gender")),
                    createPartFromString(tempLangauges),
                    createPartFromString(intent.getStringExtra("profession")),
                    createPartFromString(intent.getStringExtra("experience")),
                    createPartFromString(tempSkills),
                    createPartFromString(intent.getStringExtra("bio")),
                    createPartFromString(intent.getStringExtra("description")),
                    prepareFilePart(serverFile!!))
        else
            call = RetrofitClient.getInstance().createProfile(
                    createPartFromString(userData!!.response.access_token),
                    createPartFromString(intent.getStringExtra("avatarId")),
                    createPartFromString(userData!!.response.user_type.toString()),
                    createPartFromString(intent.getStringExtra("name")),
                    createPartFromString(intent.getStringExtra("dob")),
                    createPartFromString(intent.getStringExtra("gender")),
                    createPartFromString(tempLangauges),
                    createPartFromString(intent.getStringExtra("profession")),
                    createPartFromString(intent.getStringExtra("experience")),
                    createPartFromString(tempSkills),
                    createPartFromString(intent.getStringExtra("bio")),
                    createPartFromString(intent.getStringExtra("description")),
                    prepareFilePartEmpty())

        call.enqueue(object : Callback<SignupModel> {
            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                dismissLoader()

                if (response.body().response != null) {
                    userData!!.response = response.body().response
                    mUtils!!.setString("userDataLocal", mGson.toJson(userData))
                    val intent = Intent(mContext, ProfileReviewDialog::class.java)
                    intent.putExtra("userProfileData", response.body().response)
                    startActivity(intent)

                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(llCancelDone, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_INTENT -> {
                    val selectedImage = data!!.getData()
                    beginCrop(selectedImage)
                }
                Crop.REQUEST_CROP -> {
                    Log.e("crop photo", "is " + data)
                    handleCrop(resultCode, data)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleCrop(resultCode: Int, data: Intent?) {
        val ur = Crop.getOutput(data)
        val picturePath = FileUtils.getPath(this, ur)
        llCancelDone.visibility = View.VISIBLE
        imgDisplay.visibility = View.VISIBLE
        var path = getRealPathFromURI(getImageUri(this, getBitmap(picturePath)))
        serverFile = File(path)
        Picasso.with(mContext).load(File(path)).fit().into(imgDisplay)
    }

    private fun beginCrop(source: Uri) {
        val destination = Uri.fromFile(File(cacheDir, "cropped"))
        Crop.of(source, destination).asSquare().start(this)
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        Constants.showKeyboard(this, imgClick)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded = ArrayList<String>()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (grantResults.size == 2) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        // permissions granted.
                        if (mOption == 2)
                            showGallery()
                    }
                } else if (grantResults.size == 1) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permissions granted.
                        if (mOption == 2)
                            showGallery()

                    }
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    internal fun showGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, GALLERY_INTENT)
    }

    override fun onResume() {
        super.onResume()
        mOption = 1
        if (checkPermissions()) {
            mCameraView.start()
        }
    }

    override fun onPause() {
        mCameraView.stop()
        super.onPause()
    }


    private val mCallback = object : CameraView.Callback() {

        override fun onCameraOpened(cameraView: CameraView) {
            Log.d(TAG, "onCameraOpened")
        }

        override fun onCameraClosed(cameraView: CameraView) {
            Log.d(TAG, "onCameraClosed")
        }

        override fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
            Log.d(TAG, "onPictureTaken " + data.size)
            Toast.makeText(cameraView.context, R.string.picture_taken, Toast.LENGTH_SHORT)
                    .show()
            var file: File? = null
            getBackgroundHandler().post(Runnable {
                file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg")
                serverFile = file
                var os: OutputStream? = null
                try {
                    os = FileOutputStream(file)
                    os!!.write(data)
                    os.close()
                    loadImage(file)
                } catch (e: IOException) {
                    Log.w(TAG, "Cannot write to " + file, e)
                } finally {
                    if (os != null) {
                        try {
                            os.close()
                        } catch (e: IOException) {
                            // Ignore
                            Log.e("Exce = ", e.localizedMessage)
                        }
                    }
                }
            })

        }
    }

    private fun loadImage(absolutePath: File?) {
        runOnUiThread(Runnable {
            Log.e("Path = ", absolutePath!!.absolutePath)
            Log.e("Path New = ", FileUtils.getPath(this, Uri.fromFile(absolutePath)))
            llCancelDone.visibility = View.VISIBLE
            imgDisplay.visibility = View.VISIBLE

            val rotation = getImageOrientation(absolutePath!!.absolutePath)
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())

            val options1 = BitmapFactory.Options()
            options1.inSampleSize = 1
            val bm = BitmapFactory.decodeFile(absolutePath!!.absolutePath, options1)
            val aa1 = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
            val uri = getImageUri(mContext!!, aa1)

            Picasso.with(mContext).load(uri).fit().into(imgDisplay)
            progDailog!!.dismiss()


            /*  Picasso.with(mContext).load("file://" + FileUtils.getPath(this, Uri.fromFile(absolutePath)))
                      .fit().centerCrop()
                      .into(imgDisplay, object : Callback {
                          override fun onSuccess() {

                          }

                          override fun onError() {
                              Log.e("Error = ", "Yes")
                          }

                      })*/
        })
    }


    private fun getBackgroundHandler(): Handler {
        if (mBackgroundHandler == null) {
            val thread = HandlerThread("background")
            thread.start()
            mBackgroundHandler = Handler(thread.looper)
        }
        return mBackgroundHandler!!
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler!!.getLooper().quitSafely()
            } else {
                mBackgroundHandler!!.getLooper().quit()
            }
            mBackgroundHandler = null
        }
    }

    fun getBitmap(path: String): Bitmap {
        val rotation = getImageOrientation(path)
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())

        val options = BitmapFactory.Options()
        options.inSampleSize = 1
        val sourceBitmap = BitmapFactory.decodeFile(path, options)

        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.width, sourceBitmap.height, matrix,
                true)
    }


    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }


    fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
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

    private fun createPartFromString(data: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), data)
    }

    private fun prepareFilePart(mFile: File): MultipartBody.Part {
        val requestFile = RequestBody.create(MediaType.parse("image/*"), mFile)
        return MultipartBody.Part.createFormData("document", mFile.name, requestFile)
    }

    private fun prepareFilePartEmpty(): MultipartBody.Part {
        val requestFile = RequestBody.create(MediaType.parse("image/*"), "")
        return MultipartBody.Part.createFormData("document", "", requestFile)
    }

}

