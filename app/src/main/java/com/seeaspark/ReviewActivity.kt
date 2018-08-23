package com.seeaspark

import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_review.*
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class ReviewActivity : BaseActivity() {
    private var userData: SignupModel? = null
    private var isSwitched = false

    override fun getContentView() = R.layout.activity_review

    override fun initUI() {
        if (intent.hasExtra("isSwitched")) {
            isSwitched = true
            imgBackReview.visibility = View.VISIBLE
            txtLogoutReviewScreen.visibility = View.INVISIBLE
        }
    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        if (!isSwitched && mUtils!!.getString("profileReview", "").isEmpty())
            mUtils!!.setString("profileReview", "yes")

        Picasso.with(mContext).load(userData!!.response.avatar.avtar_url).into(imgAvatarReview)

        if (connectedToInternet()) {
            if (userData!!.response.switch_status == 0)
                hitSubmitAPI()
        } else {
            showInternetAlert(imgBackReview)
        }
    }

    override fun initListener() {
        txtLogoutReviewScreen.setOnClickListener(this)
        imgBackReview.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtLogoutReviewScreen -> {
                if (connectedToInternet())
                    alertLogoutDialog()
                else
                    showInternetAlert(txtLogoutReviewScreen)
            }
            imgBackReview -> {
                if (isSwitched) {
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
                }
            }
        }
    }

    private fun hitSubmitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().submitProfile(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<SignupModel> {

            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                if (response.body().response != null) {
                    dismissLoader()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        updateProfileDatabase(response.body().response)
                    }
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(imgBackReview, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(imgBackReview, t!!.localizedMessage)
            }
        })
    }

    private fun updateProfileDatabase(response: SignupModel.ResponseBean?) {
        userData!!.response = response
        mUtils!!.setString("userDataLocal", mGson.toJson(userData))
        val broadCastIntent = Intent(Constants.PROFILE_UPDATE)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    override fun onBackPressed() {
        if (isSwitched) {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        super.onBackPressed()
    }

    override fun onResume() {
        Log.e("onResume", "onResume")
        mUtils!!.setInt("inside_reviewFull", 1)
        if (!isSwitched) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }
        super.onResume()
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                IntentFilter(Constants.REVIEW))
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverUnverified,
                IntentFilter(Constants.UNVERIFIED))
        super.onStart()
    }

    override fun onStop() {
        mUtils!!.setInt("inside_reviewFull", 0)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverUnverified)
        super.onStop()
    }


    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var intent: Intent? = null
            if (isSwitched) {
                intent = Intent(mContext!!, QuestionnariesActivity::class.java)
                intent.putExtra("newUserType", Constants.MENTOR)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            } else {
                intent = Intent(mContext, QuestionnariesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        }
    }

    var receiverUnverified: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            alertUnVerifiedDialog()
        }
    }

    private fun alertUnVerifiedDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("DISAPPROVED")
        alertDialog.setMessage("Admin rejected your application.")
        alertDialog.setPositiveButton("Ok") { dialog, which ->
            if (isSwitched) {
                val broadCastIntent = Intent(Constants.PROFILE_UPDATE)
                broadCastIntent.putExtra("updateAPI", true)
                broadcaster!!.sendBroadcast(broadCastIntent)
                finish()
            } else
                moveToSplash()
        }
        alertDialog.show()
    }
}