package com.seeaspark

import adapters.CommunityAdapter
import adapters.EventsAdapter
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_search_events.*
import models.CommunityModel
import models.PostModel
import utils.Constants

class SearchEventCommunityActivity : BaseActivity() {

    private var mCommunityAdapter: CommunityAdapter? = null
    private var mEventsAdapter: EventsAdapter? = null
    private var mCommunityArray = ArrayList<CommunityModel>()
    private var mEventArray = ArrayList<PostModel.ResponseBean>()
    private var mSearchInstance: SearchEventCommunityActivity? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var isCommunity = false

    override fun getContentView() = R.layout.activity_search_events

    override fun initUI() {
        mLayoutManager = LinearLayoutManager(mContext)
        rvSearchEventCommunity.layoutManager = mLayoutManager
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
        mSearchInstance = this

        if (intent.getStringExtra("path") == "community")
            isCommunity = true

        if (isCommunity) {
            mCommunityAdapter = CommunityAdapter(mCommunityArray, mContext!!, mSearchInstance!!, null)
            rvSearchEventCommunity.adapter = mCommunityAdapter
        } else {
            mEventsAdapter = EventsAdapter(mContext!!, mEventArray, null, mSearchInstance!!)
            rvSearchEventCommunity.adapter = mEventsAdapter
        }
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
        Constants.closeKeyboard(mContext!!, imgBackSearch)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    fun moveToCommunityDetail() {
        if (connectedToInternet()) {
            val intent = Intent(mContext, CommunityDetailActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            showInternetAlert(rvSearchEventCommunity)
    }

    fun moveToEventDetail() {
        if (connectedToInternet()) {
            val intent = Intent(mContext, EventsDetailActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            showInternetAlert(rvSearchEventCommunity)
    }
}