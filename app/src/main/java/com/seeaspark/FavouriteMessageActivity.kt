package com.seeaspark

import adapters.FavouriteAdapter
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_favourite_message.*

/**
 * Created by dev on 31/7/18.
 */
class FavouriteMessageActivity : BaseActivity() {

    private var mFavouriteAdapter: FavouriteAdapter? = null

    override fun getContentView() = R.layout.activity_favourite_message

    override fun initUI() {

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llOuterFavourite.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        imgBack.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llOuterFavourite.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBack.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        mFavouriteAdapter = FavouriteAdapter(this, mWidth)
        lvFavouriteList.adapter = mFavouriteAdapter
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