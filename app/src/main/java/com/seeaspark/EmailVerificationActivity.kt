package com.seeaspark

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_email_verification.*
import models.BaseSuccessModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants


class EmailVerificationActivity : BaseActivity() {

    override fun initUI() {

    }

    override fun displayDayMode() {
       /// no operation
    }

    override fun displayNightMode() {
        /// no operation
    }

    override fun onCreateStuff() {
    }

    override fun initListener() {
        txtDoneEmail.setOnClickListener(this)
        txtLoginEmail.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_email_verification

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtDoneEmail -> {
                if (connectedToInternet())
                    hitAPI()
                else
                    showInternetAlert(txtDoneEmail)
            }
            txtLoginEmail -> {

                if (intent!!.hasExtra("path")) {
                    var intent = Intent(mContext, LoginSignupActivity::class.java)
                    intent.putExtra("setLogin", true)
                    intent.putExtra("userType", intent.getIntExtra("userType", 0))
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                } else {
                    var intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().resendEmail(intent.getStringExtra("access_token"))
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    showToast(mContext!!, response.body().response.message)
                } else {
                    showAlert(txtDoneEmail, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtDoneEmail, t!!.localizedMessage)
            }
        })
    }

    internal var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mUtils!!.setBoolean("addEmailFragment", false)
            mUtils!!.setInt(Constants.EMAIL_VERIFY, 1)

            val inStarted = Intent(mContext, CreateProfileActivity::class.java)
            inStarted.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            inStarted.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(inStarted)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
    }

    override  fun onResume() {
        super.onResume()
        Log.e("onResume", "onResume")
        mUtils!!.setInt("inside_verify", 1)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                IntentFilter(Constants.EMAIL_VERIFY))
    }

    override fun onStop() {
        mUtils!!.setInt("inside_verify", 0)
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }
}