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
import android.content.pm.PackageManager


class SettingsActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_settings
    private var userData: SignupModel? = null

    override fun initUI() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        txtTitleCustom.text = getString(R.string.settings)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        scNightMode.setOnCheckedChangeListener { p0, status ->

            if (status)
                mUtils!!.setInt("nightMode", 1)
            else
                mUtils!!.setInt("nightMode", 0)

        }
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