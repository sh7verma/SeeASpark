package com.seeaspark


import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_home.*
import models.BaseSuccessModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import android.R.attr.versionName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi


class SettingsActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_settings
    private var userData: SignupModel? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        txtTitleCustom.text = getString(R.string.settings)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        scNightMode.isChecked = mUtils!!.getInt("nightMode", 0) == 1

        scNightMode.setOnCheckedChangeListener { p0, status ->
            if (status) {
                mUtils!!.setInt("nightMode", 1)
                val broadCastIntent = Intent(Constants.NIGHT_MODE)
                broadCastIntent.putExtra("status", Constants.NIGHT)
                broadcaster!!.sendBroadcast(broadCastIntent)
            } else {
                mUtils!!.setInt("nightMode", 0)
                val broadCastIntent = Intent(Constants.NIGHT_MODE)
                broadCastIntent.putExtra("status", Constants.DAY)
                broadcaster!!.sendBroadcast(broadCastIntent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {

        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llMainSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))

        llTellFriend.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtTellFriend.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llRateUS.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtRateUs.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llBlockedUser.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtBlockedUsers.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llPrivacyPolicy.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtPrivacyPolicy.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llTerms.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtTerms.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llNotifications.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtNotification.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llNightMode.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtNightMode.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llAutoNightMode.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtAutoNightMode.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llChangePassword.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtChangePassword.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llLogout.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtLogout.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llDeactivate.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtDeactivate.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llDeleteAccount.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtDeleteAccount.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        txtVersion.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {

        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llMainSettings.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))

        llTellFriend.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtTellFriend.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llRateUS.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtRateUs.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llBlockedUser.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtBlockedUsers.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llPrivacyPolicy.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtPrivacyPolicy.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llTerms.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtTerms.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llNotifications.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtNotification.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llNightMode.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtNightMode.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llAutoNightMode.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtAutoNightMode.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llChangePassword.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtChangePassword.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llLogout.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtLogout.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llDeactivate.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtDeactivate.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llDeleteAccount.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtDeleteAccount.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        txtVersion.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        /// hiding change password in case of social logins
        if (userData!!.response.account_type != Constants.EMAIL_LOGIN) {
            llChangePassword.visibility = View.GONE
            viewChangePassword.visibility = View.GONE
        }

        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            txtVersion.text = "App Version ${pInfo.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        llTellFriend.setOnClickListener(this)
        llRateUS.setOnClickListener(this)
        llBlockedUser.setOnClickListener(this)
        llPrivacyPolicy.setOnClickListener(this)
        llTerms.setOnClickListener(this)
        llNotifications.setOnClickListener(this)
        llChangePassword.setOnClickListener(this)
        llDeactivate.setOnClickListener(this)
        llDeleteAccount.setOnClickListener(this)
        llLogout.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        var intent: Intent? = null
        when (view) {

            llDeactivate -> {
                alertDeactivateAccountDialog()
            }

            llDeleteAccount -> {
                alertDeleteAccountDialog()
            }

            imgBackCustom -> {
                moveBack()
            }
            llTellFriend -> {
                intent = Intent(mContext!!, TellAFriendActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            llRateUS -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llBlockedUser -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llPrivacyPolicy -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llTerms -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llChangePassword -> {
                intent = Intent(mContext!!, ChangePasswordActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            llNotifications -> {
                intent = Intent(mContext!!, NotificationActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            llLogout -> {
                if (connectedToInternet())
                    alertLogoutDialog()
                else
                    showInternetAlert(llTellFriend)
            }
        }
    }

    private fun alertDeleteAccountDialog() {
        val alertDialog = AlertDialog.Builder(mContext!!)
        alertDialog.setTitle(getString(R.string.delete_account))
        alertDialog.setMessage(getString(R.string.delete_account_msg))
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->

            if (connectedToInternet()) {
                hitDeleteAccountAPI()
            } else {
                showInternetAlert(rvCards)
            }
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }


    private fun alertDeactivateAccountDialog() {
        val alertDialog = AlertDialog.Builder(mContext!!)
        alertDialog.setTitle(getString(R.string.deactivate_account))
        alertDialog.setMessage(getString(R.string.deactivate_account_msg))
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->

            if (connectedToInternet()) {
                hitDeactivateAccountAPI()
            } else {
                showInternetAlert(rvCards)
            }
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }


    private fun hitDeleteAccountAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().deleteAccount(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    moveToSplash()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(rvCards, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable) {
                dismissLoader()
                showAlert(rvCards, t.localizedMessage)
            }
        })
    }

    private fun hitDeactivateAccountAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().deactivateAccount(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    moveToSplash()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(rvCards, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable) {
                dismissLoader()
                showAlert(rvCards, t.localizedMessage)
            }
        })
    }


    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}