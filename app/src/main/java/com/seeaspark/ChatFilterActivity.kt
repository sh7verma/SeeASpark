package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_chat_filter.*
import utils.Constants

/**
 * Created by dev on 26/7/18.
 */
class ChatFilterActivity : BaseActivity() {

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent))
        return R.layout.activity_chat_filter
    }

    override fun initUI() {
        var mentee = intent.getStringExtra("mentee")
        var mentor = intent.getStringExtra("mentor")

        if (mentee.equals("0")) {
            imgMentee.visibility = View.INVISIBLE
        } else {
            imgMentee.visibility = View.VISIBLE
        }

        if (mentor.equals("0")) {
            imgMentor.visibility = View.INVISIBLE
        } else {
            imgMentor.visibility = View.VISIBLE
        }
    }

    override fun displayDayMode() {
        llOuterFilter.setBackgroundResource(R.drawable.white_short_profile_background)
        if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_BOTH)) {
            txtBoth.setTextColor(blackColor)
            txtMentor.setTextColor(lightGrey)
            txtMentee.setTextColor(lightGrey)
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTOR)) {
            txtBoth.setTextColor(lightGrey)
            txtMentor.setTextColor(blackColor)
            txtMentee.setTextColor(lightGrey)
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)) {
            txtBoth.setTextColor(lightGrey)
            txtMentor.setTextColor(lightGrey)
            txtMentee.setTextColor(blackColor)
        }
    }

    override fun displayNightMode() {
        llOuterFilter.setBackgroundResource(R.drawable.dark_short_profile_background)
        if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_BOTH)) {
            txtBoth.setTextColor(whiteColor)
            txtMentor.setTextColor(transperent)
            txtMentee.setTextColor(transperent)
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTOR)) {
            txtBoth.setTextColor(transperent)
            txtMentor.setTextColor(whiteColor)
            txtMentee.setTextColor(transperent)
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)) {
            txtBoth.setTextColor(transperent)
            txtMentor.setTextColor(transperent)
            txtMentee.setTextColor(whiteColor)
        }
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        llBoth.setOnClickListener(this)
        llMentor.setOnClickListener(this)
        llMentee.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            llBoth -> {
                mUtils!!.setString("filter_type", Constants.FILTER_BOTH)
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            llMentor -> {
                mUtils!!.setString("filter_type", Constants.FILTER_MENTOR)
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
                overridePendingTransition(0, 0)
            }
            llMentee -> {
                mUtils!!.setString("filter_type", Constants.FILTER_MENTEE)
                val intent = Intent()
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