package com.seeaspark

import adapters.BookmarkCommunityAdapter
import adapters.CommunityAdapter
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_community_bookmark.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_community.*
import models.CommunityModel

class CommunityBookmarkActivity : BaseActivity() {
    private var mLayoutManager: LinearLayoutManager? = null
    private var mCommunityAdapter: CommunityAdapter? = null
    private var mCommunityArray = ArrayList<CommunityModel>()

    var mCommunityBookmark: CommunityBookmarkActivity? = null

    override fun getContentView() = R.layout.activity_community_bookmark

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {

        mCommunityBookmark = this

        txtTitleCustom.text = getString(R.string.Bookmarks)

        imgOption1Custom.setImageResource(R.mipmap.ic_search)
        imgOption1Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)

        rvCommunityBookmark.layoutManager = mLayoutManager

        if (mUtils!!.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_black)
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        imgBackCustom.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        imgOption1Custom.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        rvCommunityBookmark.setBackgroundColor(ContextCompat.getColor(this, R.color.background))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBackCustom.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        imgOption1Custom.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        rvCommunityBookmark.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
    }

    override fun onCreateStuff() {
        mCommunityAdapter = CommunityAdapter(mCommunityArray, mContext!!, mCommunityBookmark!!, null)
        rvCommunityBookmark.adapter = mCommunityAdapter
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgOption1Custom.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackCustom -> {
                moveBack()
            }
            imgOption1Custom -> {
                intent = Intent(mContext, SearchEventCommunityActivity::class.java)
                intent.putExtra("path", "community")
                startActivity(intent)
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

    fun moveToCommunityDetail() {
        if (connectedToInternet()) {
            val intent = Intent(mContext, CommunityDetailActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            showInternetAlert(rvCommunityListing)
    }
}