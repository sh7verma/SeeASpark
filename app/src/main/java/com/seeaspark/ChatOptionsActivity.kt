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
    }

    override fun displayDayMode() {
        llOuterChatOptions.setBackgroundResource(R.drawable.white_short_profile_background)
        txtShareProfile.setTextColor(blackColor)
        txtFavouriteMessage.setTextColor(blackColor)
        txtClearChat.setTextColor(blackColor)
        txtUnmatch.setTextColor(blackColor)
        txtBlock.setTextColor(blackColor)
        txtReport.setTextColor(blackColor)
    }

    override fun displayNightMode() {
        llOuterChatOptions.setBackgroundResource(R.drawable.dark_short_profile_background)
        txtShareProfile.setTextColor(whiteColor)
        txtFavouriteMessage.setTextColor(whiteColor)
        txtClearChat.setTextColor(whiteColor)
        txtUnmatch.setTextColor(whiteColor)
        txtBlock.setTextColor(whiteColor)
        txtReport.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        txtShareProfile.setOnClickListener(this)
        txtFavouriteMessage.setOnClickListener(this)
        txtClearChat.setOnClickListener(this)
        txtUnmatch.setOnClickListener(this)
        txtBlock.setOnClickListener(this)
        txtReport.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtShareProfile -> {
                val intent = Intent()
                intent.putExtra("type", "share_profile")
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
                val intent = Intent()
                intent.putExtra("type", "clear_chat")
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            txtUnmatch -> {
                val intent = Intent()
                intent.putExtra("type", "unmatch")
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            txtBlock -> {
                val intent = Intent()
                intent.putExtra("type", "block")
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            txtReport -> {
                val intent = Intent()
                intent.putExtra("type", "report")
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

}