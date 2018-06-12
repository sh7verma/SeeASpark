package com.seeaspark

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.DisplayMetrics
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.Window
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_profile_review.*
import models.SignupModel
import utils.Utils

class ProfileReviewDialog : Activity() {

    internal var mScreenwidth: Int = 0
    internal var mScreenheight: Int = 0
    var mUtils: Utils? = null
    var userProfileData: SignupModel.ResponseBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wmlp = this.window.attributes

        wmlp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        this.setFinishOnTouchOutside(false)
        setContentView(R.layout.dialog_profile_review)
        getDefaults()
        window.setLayout(mScreenwidth, (mScreenheight * 0.6).toInt())

        mUtils = Utils(this)
        userProfileData = intent.getParcelableExtra("userProfileData")

        txtNameDialog.text = "HI ${userProfileData!!.full_name}!"

        Picasso.with(this).load(userProfileData!!.avatar).into(imgAvatarProfileReview)

        txtReady.setOnClickListener {
            var intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra("avatar", userProfileData!!.avatar)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }

        txtLogoutReview.setOnClickListener {
            alertLogoutDialog()
        }
    }

    internal fun getDefaults() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mScreenwidth = dm.widthPixels
        mScreenheight = dm.heightPixels
    }

    internal fun alertLogoutDialog() {
        val alertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.myDialog));
        alertDialog.setTitle("LOG OUT")
        alertDialog.setMessage("Are you sure you want to Log out?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            moveToSplash()
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    fun moveToSplash() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        mUtils!!.clear_shf()
        val inSplash = Intent(this, AfterWalkThroughActivity::class.java)
        inSplash.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        inSplash.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(inSplash)
    }
}