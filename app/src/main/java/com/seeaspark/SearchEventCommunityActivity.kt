package com.seeaspark

import android.view.View
import kotlinx.android.synthetic.main.activity_search_events.*
import utils.Constants

class SearchEventCommunityActivity : BaseActivity() {
    override fun getContentView() = R.layout.activity_search_events

    override fun initUI() {
    }

    override fun displayDayMode() {
        imgBackSearch.setImageResource(R.mipmap.ic_back_black)
        llMainSearchEvents.setBackgroundColor(whiteColor)
        edSearchEventCommunity.setTextColor(blackColor)
    }

    override fun displayNightMode() {
        imgBackSearch.setImageResource(R.mipmap.ic_back_org)
        llMainSearchEvents.setBackgroundColor(blackColor)
        edSearchEventCommunity.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {



    }

    override fun initListener() {
        imgBackSearch.setOnClickListener(this)
        imgCancelSearch.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackSearch -> {
                moveBack()
            }
            imgCancelSearch -> {
                edSearchEventCommunity.setText(Constants.EMPTY)
            }
        }
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }
}