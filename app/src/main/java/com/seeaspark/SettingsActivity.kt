package com.seeaspark


import android.view.View
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_settings

    override fun initUI() {

    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
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
        when (view) {
            llTellFriend -> {

            }
            llRateUS -> {

            }
            llBlockedUser -> {

            }
            llPrivacyPolicy -> {

            }
            llTerms -> {

            }
            llNotifications -> {

            }
            llChangePassword -> {

            }
            llLogout -> {

            }

        }
    }
}