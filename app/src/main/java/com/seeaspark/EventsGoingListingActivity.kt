package com.seeaspark

import adapters.EventGoingAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_events_going_listing.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.PostModel

class EventsGoingListingActivity : BaseActivity() {

    private var mLayoutManager: LinearLayoutManager? = null
    private var mEventsGoingAdapter: EventGoingAdapter? = null
    private var mEventsUsersArray = ArrayList<PostModel.ResponseBean.GoingUserBean>()

    override fun getContentView() = R.layout.activity_events_going_listing

    override fun initUI() {
        txtTitleCustom.text = getString(R.string.going)
        mLayoutManager = LinearLayoutManager(mContext)
        rvEventsGoing.layoutManager = mLayoutManager
    }

    override fun displayDayMode() {
        llMainEventsListing.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    override fun displayNightMode() {
        llMainEventsListing.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBackCustom.setImageResource(R.mipmap.ic_back_black)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {

        mEventsUsersArray.addAll(intent.getParcelableArrayListExtra("goingList"))

        mEventsGoingAdapter = EventGoingAdapter(mEventsUsersArray, mContext!!)
        rvEventsGoing.adapter = mEventsGoingAdapter
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackCustom -> {
                moveBack()
            }
        }
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}