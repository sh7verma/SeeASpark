package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.View
import com.faradaj.blurbehind.BlurBehind
import kotlinx.android.synthetic.main.activity_chat_filter.*
import utils.Constants

/**
 * Created by dev on 26/7/18.
 */
class ChatFilterActivity : BaseActivity() {

    var status = 0;

    override fun getContentView(): Int {
//        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent))
        return R.layout.activity_chat_filter
    }

    override fun initUI() {

        BlurBehind.getInstance()
                .withAlpha(80)
                .withFilterColor(ContextCompat.getColor(this, R.color.light_white_transparent))
                .setBackground(this)

        val mentee = intent.getStringExtra("mentee")
        val mentor = intent.getStringExtra("mentor")

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
        if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTOR)) {
            txtBoth.setTextColor(viewLineColor)
            txtMentor.setTextColor(blackColor)
            txtMentee.setTextColor(viewLineColor)
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)) {
            txtBoth.setTextColor(viewLineColor)
            txtMentor.setTextColor(viewLineColor)
            txtMentee.setTextColor(blackColor)
        } else {
            txtBoth.setTextColor(blackColor)
            txtMentor.setTextColor(viewLineColor)
            txtMentee.setTextColor(viewLineColor)
        }
    }

    override fun displayNightMode() {
        status = 1
        llOuterFilter.setBackgroundResource(R.drawable.share_night_background)
        if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTOR)) {
            txtBoth.setTextColor(viewLineColor)
            txtMentor.setTextColor(whiteColor)
            txtMentee.setTextColor(viewLineColor)
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)) {
            txtBoth.setTextColor(viewLineColor)
            txtMentor.setTextColor(viewLineColor)
            txtMentee.setTextColor(whiteColor)
        } else {
            txtBoth.setTextColor(whiteColor)
            txtMentor.setTextColor(viewLineColor)
            txtMentee.setTextColor(viewLineColor)
        }
    }

    override fun onCreateStuff() {
        if (status == 0) {
            if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTOR)) {
                txtBoth.setTextColor(viewLineColor)
                txtMentor.setTextColor(blackColor)
                txtMentee.setTextColor(viewLineColor)
            } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)) {
                txtBoth.setTextColor(viewLineColor)
                txtMentor.setTextColor(viewLineColor)
                txtMentee.setTextColor(blackColor)
            } else {
                txtBoth.setTextColor(blackColor)
                txtMentor.setTextColor(viewLineColor)
                txtMentee.setTextColor(viewLineColor)
            }
        }
    }

    override fun initListener() {
        llBoth.setOnClickListener(this)
        llMentor.setOnClickListener(this)
        llMentee.setOnClickListener(this)
        llMainFilter.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            llMainFilter -> {
                finish()
                overridePendingTransition(0, 0)
            }
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