package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_chat_filter.*
import kotlinx.android.synthetic.main.activity_chat_options.*

/**
 * Created by dev on 26/7/18.
 */
class ChatOptionsActivity : BaseActivity() {

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent))
        return R.layout.activity_chat_options
    }

    override fun initUI() {
        var blockStatus = intent.getStringExtra("block_status")
        if (blockStatus.equals("0")) {
            txtBlock.text = getString(R.string.block)
        } else {
            txtBlock.text = getString(R.string.unblock)
        }
    }

    override fun displayDayMode() {
        llinnerChatOptions.setBackgroundResource(R.drawable.white_short_profile_background)
        txtShareProfile.setTextColor(blackColor)
        txtRating.setTextColor(blackColor)
        txtFavouriteMessage.setTextColor(blackColor)
        txtClearChat.setTextColor(blackColor)
        txtUnmatch.setTextColor(blackColor)
        txtBlock.setTextColor(blackColor)
        txtReport.setTextColor(blackColor)
    }

    override fun displayNightMode() {
        llinnerChatOptions.setBackgroundResource(R.drawable.share_night_background)
        txtShareProfile.setTextColor(whiteColor)
        txtRating.setTextColor(whiteColor)
        txtFavouriteMessage.setTextColor(whiteColor)
        txtClearChat.setTextColor(whiteColor)
        txtUnmatch.setTextColor(whiteColor)
        txtBlock.setTextColor(whiteColor)
        txtReport.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        llOuterChatOptions.setOnClickListener(this)
        txtShareProfile.setOnClickListener(this)
        txtRating.setOnClickListener(this)
        txtFavouriteMessage.setOnClickListener(this)
        txtClearChat.setOnClickListener(this)
        txtUnmatch.setOnClickListener(this)
        txtBlock.setOnClickListener(this)
        txtReport.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            llOuterChatOptions -> {
                finish()
                overridePendingTransition(0, 0)
            }
            txtShareProfile -> {
                val intent = Intent()
                intent.putExtra("type", "share_profile")
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            txtRating -> {
                val intent = Intent()
                intent.putExtra("type", "rating")
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            txtFavouriteMessage -> {
                val intent = Intent()
                intent.putExtra("type", "favourite_message")
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            txtClearChat -> {
                if (connectedToInternet()) {
                    val intent = Intent()
                    intent.putExtra("type", "clear_chat")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    showInternetAlert(txtShareProfile)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            txtUnmatch -> {
                if (connectedToInternet()) {
                    val intent = Intent()
                    intent.putExtra("type", "unmatch")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    showInternetAlert(txtShareProfile)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            txtBlock -> {
                if (connectedToInternet()) {
                    val intent = Intent()
                    intent.putExtra("type", "block")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    showInternetAlert(txtShareProfile)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
            txtReport -> {
                if (connectedToInternet()) {
                    val intent = Intent()
                    intent.putExtra("type", "report")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    showInternetAlert(txtShareProfile)
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

}