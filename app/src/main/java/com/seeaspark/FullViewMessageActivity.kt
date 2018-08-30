package com.seeaspark

import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_view_message.*

class FullViewMessageActivity : BaseActivity() {

    internal var mName = ""
    internal var mPic = ""
    internal var mMessage = ""

    override fun getContentView() = R.layout.activity_full_view_message

    override fun initUI() {

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llOuterFullMessage.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtMessage.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llOuterFullMessage.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtMessage.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        mName = intent.extras!!.getString("name")
        mPic = intent.extras!!.getString("pic")
        mMessage = intent.extras!!.getString("message")

//        txtName.setText(mName)
        txtMessage.setText(mMessage)
        Picasso.with(this).load(mPic).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
    }

    override fun initListener() {
        imgBack.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                moveBack()
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