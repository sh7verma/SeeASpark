package com.seeaspark

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_review.*
import models.SignupModel
import utils.Constants

class ReviewActivity : BaseActivity() {

    private var userData: SignupModel? = null

    override fun initUI() {

    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        mUtils!!.setString("profileReview", "yes")
        Picasso.with(mContext).load(userData!!.response.avatar).into(imgAvatarReview)
    }

    override fun initListener() {
        txtLogoutReviewScreen.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_review

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtLogoutReviewScreen -> {
                if (connectedToInternet())
                    alertLogoutDialog()
                else
                    showInternetAlert(txtLogoutReviewScreen)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume", "onResume")
        mUtils!!.setInt("inside_reviewFull", 1)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                IntentFilter(Constants.REVIEW))
    }

    override fun onStop() {
        mUtils!!.setInt("inside_reviewFull", 0)
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val inStarted = Intent(mContext, QuestionnariesActivity::class.java)
            inStarted.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            inStarted.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(inStarted)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
    }

}