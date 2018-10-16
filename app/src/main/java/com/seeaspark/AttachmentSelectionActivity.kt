package com.seeaspark

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.faradaj.blurbehind.BlurBehind
import kotlinx.android.synthetic.main.activity_attachment_selection.*
import utils.Constants
import java.util.ArrayList

/**
 * Created by dev on 26/7/18.
 */
class AttachmentSelectionActivity : BaseActivity() {

    override fun getContentView(): Int {
        return R.layout.activity_attachment_selection
    }

    override fun initUI() {
        BlurBehind.getInstance()
                .withAlpha(100)
                .withFilterColor(ContextCompat.getColor(this, R.color.light_white_transparent))
                .setBackground(this)
    }

    override fun displayDayMode() {
        llMainAttachment.setBackgroundResource(R.drawable.white_short_profile_background)
        txtShareChat.setTextColor(blackColor)
        txtCamera.setTextColor(blackColor)
        txtGallery.setTextColor(blackColor)
        txtNotes.setTextColor(blackColor)
        txtDocument.setTextColor(blackColor)
    }

    override fun displayNightMode() {
        llMainAttachment.setBackgroundResource(R.drawable.dark_short_profile_background)
        txtShareChat.setTextColor(whiteColor)
        txtCamera.setTextColor(whiteColor)
        txtGallery.setTextColor(whiteColor)
        txtNotes.setTextColor(whiteColor)
        txtDocument.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        llCamera.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        llNotes.setOnClickListener(this)
        llDocument.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            llCamera -> {
                if (checkCameraPermissions()) {
                    val intent = Intent()
                    intent.putExtra("type", "camera")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    requestCameraPermission()
                }
            }
            llGallery -> {
                if (checkGalleryPermissions()) {
                    val intent = Intent()
                    intent.putExtra("type", "gallery")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    requestGalleryPermission()
                }
            }
            llNotes -> {
                val intent = Intent()
                intent.putExtra("type", "notes")
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            llDocument -> {
                if (checkGalleryPermissions()) {
                    val intent = Intent()
                    intent.putExtra("type", "document")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    requestGalleryPermission()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

    //// Camera Permission ////

    var listCameraPermissionsNeeded = ArrayList<String>()
    internal var cameraPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private val CAMERA_PERMISSION_REQUEST_CODE = 16
    private fun checkCameraPermissions(): Boolean {
        var result: Int
        listCameraPermissionsNeeded = ArrayList<String>()
        for (p in cameraPermissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listCameraPermissionsNeeded.add(p)
            }
        }
        if (!listCameraPermissionsNeeded.isEmpty()) {
            return false
        }
        return true
    }

    fun requestCameraPermission() {
        if (hasPermissions(this, listCameraPermissionsNeeded)) {
            mUtils!!.setBoolean(Constants.CAMRA_PERMISSION, false)
            showSnackbar(R.string.camera_permission,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this, listCameraPermissionsNeeded.toTypedArray(), CAMERA_PERMISSION_REQUEST_CODE)
            })
        } else {
            if (!mUtils!!.getBoolean(Constants.CAMRA_PERMISSION, false))
                ActivityCompat.requestPermissions(this, listCameraPermissionsNeeded.toTypedArray(), CAMERA_PERMISSION_REQUEST_CODE)
            if (mUtils!!.getBoolean(Constants.CAMRA_PERMISSION, false)) {
                showSnackbar(R.string.camera_permission,
                        android.R.string.ok, View.OnClickListener {
                    // Request permission
                    mUtils!!.setBoolean(Constants.CAMRA_PERMISSION, false)
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.data = Uri.parse("package:" + getPackageName())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(intent)
                })
            }
            mUtils!!.setBoolean(Constants.CAMRA_PERMISSION, true)
        }
    }

    ///////////////////////////

    /// Gallery Permission ///

    var listGalleryPermissionsNeeded = ArrayList<String>()
    internal var galleryPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val GALLERY_PERMISSION_REQUEST_CODE = 17
    private fun checkGalleryPermissions(): Boolean {
        var result: Int
        listGalleryPermissionsNeeded = ArrayList<String>()
        for (p in galleryPermissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listGalleryPermissionsNeeded.add(p)
            }
        }
        if (!listGalleryPermissionsNeeded.isEmpty()) {
            return false
        }
        return true
    }

    fun requestGalleryPermission() {
        if (hasPermissions(this, listGalleryPermissionsNeeded)) {
            mUtils!!.setBoolean(Constants.GALLERY_PERMISSION, false)
            showSnackbar(R.string.gallery_permission,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this, listGalleryPermissionsNeeded.toTypedArray(), GALLERY_PERMISSION_REQUEST_CODE)
            })
        } else {
            if (!mUtils!!.getBoolean(Constants.GALLERY_PERMISSION, false))
                ActivityCompat.requestPermissions(this, listGalleryPermissionsNeeded.toTypedArray(), GALLERY_PERMISSION_REQUEST_CODE)
            if (mUtils!!.getBoolean(Constants.GALLERY_PERMISSION, false)) {
                showSnackbar(R.string.gallery_permission,
                        android.R.string.ok, View.OnClickListener {
                    // Request permission
                    mUtils!!.setBoolean(Constants.GALLERY_PERMISSION, false)
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.data = Uri.parse("package:" + getPackageName())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(intent)
                })
            }
            mUtils!!.setBoolean(Constants.GALLERY_PERMISSION, true)
        }
    }

    //////////////////////////

    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int, listener: View.OnClickListener) {
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE).setActionTextColor(ContextCompat.getColor(this, R.color.red_color)).setAction(getString(actionStringId), listener).show()
    }

    fun hasPermissions(context: Context?, vararg permissions: ArrayList<String>): Boolean {
        for ((index, permission) in permissions.withIndex()) {
            if (ActivityCompat.checkSelfPermission(context!!, permission.toString()) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size == 2) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    }
                } else if (grantResults.size == 1) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                return
            }
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size == 2) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    }
                } else if (grantResults.size == 1) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}