package com.seeaspark

import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.BaseSuccessModel
import models.NotificationModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.MainApplication

class NotificationActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener {

    override fun getContentView() = R.layout.activity_notifications

    var mMessages: Int = 0
    var mPosts: Int = 0
    var mQuotes: Int = 0
    var mNotes: Int = 0
    private var userData: SignupModel? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        txtTitleCustom.text = getString(R.string.notifications)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        if (userData!!.response.user_type == Constants.MENTEE) {
            llCommunity.visibility = View.GONE
            viewCommunity.visibility = View.GONE
        }

        displayDayMode()
    }

    override fun onCreateStuff() {
        if (connectedToInternet()) {
            hitNotificationSettings()
        } else {
            showInternetAlert(llCustomToolbar)
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {

        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llMainNotifications.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))

        llMessage.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtMessages.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llCommunity.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtCommunityPosts.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llInspirational.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtInspirational.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llNotes.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtNotes.setTextColor(ContextCompat.getColor(this, R.color.black_color))

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {

        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llMainNotifications.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))

        llMessage.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtMessages.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llCommunity.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtCommunityPosts.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llInspirational.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtInspirational.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llNotes.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtNotes.setTextColor(ContextCompat.getColor(this, R.color.white_color))

    }


    private fun hitNotificationSettings() {
        showLoader()
        val call = RetrofitClient.getInstance().notificationSettings(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<NotificationModel> {
            override fun onResponse(call: Call<NotificationModel>?, response: Response<NotificationModel>?) {
                if (response!!.body().response != null) {
                    setStatuses(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(imgBackCustom, response.body().error!!.message!!)
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<NotificationModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(imgBackCustom, t!!.localizedMessage)
            }
        })

    }

    private fun setStatuses(response: NotificationModel.ResponseBean) {

        mMessages = response.messages_notification
        mPosts = response.posts_notification
        mQuotes = response.qoutes_notification
        mNotes = response.notes_notification

        scMessageReceived.isChecked = response.messages_notification == 1
        scCommunity.isChecked = response.posts_notification == 1
        scInspirational.isChecked = response.qoutes_notification == 1
        scNotes.isChecked = response.notes_notification == 1
    }

    override fun initListener() {
        scMessageReceived.setOnCheckedChangeListener(this)
        scCommunity.setOnCheckedChangeListener(this)
        scInspirational.setOnCheckedChangeListener(this)
        scNotes.setOnCheckedChangeListener(this)
        imgBackCustom.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackCustom -> {
                if (connectedToInternet())
                    hitAPI()
                else
                    showInternetAlert(imgBackCustom)
            }
        }
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().updateNotifications(mUtils!!.getString("access_token", ""), mMessages, mPosts, mQuotes, mNotes)
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                dismissLoader()
                if (response!!.body().response != null) {
                    showToast(mContext!!, response.body().response.message)
                    moveBack()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(imgBackCustom, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(imgBackCustom, t!!.localizedMessage)
            }
        })
    }

    private fun moveBack() {
        if (MainApplication.isLandingAvailable) {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        } else {
            val intent = Intent(mContext, LandingActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onCheckedChanged(view: CompoundButton?, status: Boolean) {
        when (view) {
            scMessageReceived -> {
                mMessages = if (status)
                    1
                else
                    0
            }
            scCommunity -> {
                mPosts = if (status)
                    1
                else
                    0
            }
            scInspirational -> {
                mQuotes = if (status)
                    1
                else
                    0
            }
            scNotes -> {
                mNotes = if (status)
                    1
                else
                    0
            }
        }
    }

    override fun onBackPressed() {
        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(imgBackCustom)
    }
}