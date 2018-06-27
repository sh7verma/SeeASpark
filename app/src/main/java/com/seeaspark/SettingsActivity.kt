package com.seeaspark


import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class SettingsActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_settings

    override fun initUI() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        txtTitleCustom.text = getString(R.string.settings)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        scNightMode.setOnCheckedChangeListener { p0, p1 ->
            showToast(this, getString(R.string.work_in_progress))
        }

    }

    override fun onCreateStuff() {

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
        llLogout.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        var intent: Intent? = null
        when (view) {
            imgBackCustom -> {
                moveBack()
            }
            llTellFriend -> {
                intent = Intent(mContext!!, TellAFriendActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            llRateUS -> {

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

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}