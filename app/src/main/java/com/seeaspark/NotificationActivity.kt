package com.seeaspark

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.CompoundButton
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

class NotificationActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener {

    override fun getContentView() = R.layout.activity_notifications

    var mMessages: Int = 0
    var mPosts: Int = 0
    var mQuotes: Int = 0
    var mNotes: Int = 0
    private var userData: SignupModel? = null

    override fun initUI() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        txtTitleCustom.text = getString(R.string.notifications)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        if (userData!!.response.user_type == Constants.MENTEE) {
            llCommunity.visibility = View.GONE
            viewCommunity.visibility = View.GONE
        }
    }

    override fun onCreateStuff() {

        if (connectedToInternet()) {
            hitNotificationSettings()
        } else {
            showInternetAlert(llCustomToolbar)
        }

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
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
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
}