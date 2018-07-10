package com.seeaspark

import adapters.CommentsAdapter
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.CommentModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CommentsActivity : BaseActivity() {

    private var postId = 0
    private var mOffset = 1

    var mCommentsArray = ArrayList<CommentModel.ResponseBean>()
    var mCommentAdapter: CommentsAdapter? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var mCommentInstance: CommentsActivity? = null

    override fun getContentView() = R.layout.activity_comments

    override fun initUI() {
        txtTitleCustom.text = getString(R.string.comments)
        mLayoutManager = LinearLayoutManager(mContext)
        mLayoutManager!!.reverseLayout = true;
        rvComments.layoutManager = mLayoutManager
        srlComments.setColorSchemeResources(R.color.colorPrimary)
        srlComments.setOnRefreshListener {
            mOffset++
            hitAPI()
        }

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_black)
        llCustomToolbar.setBackgroundColor(whiteColor)
        imgBackCustom.setBackgroundResource(whiteRipple)
        txtTitleCustom.setTextColor(blackColor)
        imgOption1Custom.setBackgroundResource(whiteRipple)
        llMainComments.setBackgroundColor(whiteColor)
        edComments.setBackgroundColor(whiteColor)
        edComments.setTextColor(blackColor)
        imgSendComments.setBackgroundResource(whiteRipple)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        llCustomToolbar.setBackgroundColor(blackColor)
        imgBackCustom.setBackgroundResource(blackRipple)
        txtTitleCustom.setTextColor(whiteColor)
        imgOption1Custom.setBackgroundResource(blackRipple)
        llMainComments.setBackgroundColor(blackColor)
        edComments.setBackgroundColor(blackColor)
        edComments.setTextColor(whiteColor)
        imgSendComments.setBackgroundResource(blackRipple)
    }

    override fun onCreateStuff() {
        mCommentInstance = this
        postId = intent.getIntExtra("postId", 0)
        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(llMainComments)
    }

    private fun hitAPI() {
        if (mOffset == 1)
            showLoader()
        val call = RetrofitClient.getInstance().getComments(mUtils!!.getString("access_token", "")
                , postId, mOffset)
        call.enqueue(object : Callback<CommentModel> {
            override fun onResponse(call: Call<CommentModel>?, response: Response<CommentModel>) {
                if (mOffset == 1)
                    dismissLoader()
                populateData(response.body().response)
            }

            override fun onFailure(call: Call<CommentModel>?, t: Throwable?) {
                if (mOffset == 1)
                    dismissLoader()
            }
        })
    }

    private fun populateData(response: List<CommentModel.ResponseBean>) {
        if (mOffset == 1) {
            mCommentsArray.addAll(response)
            mCommentsArray.reverse()
            mCommentAdapter = CommentsAdapter(mContext!!, mCommentsArray, mCommentInstance!!)
            rvComments.adapter = mCommentAdapter
        } else {
            srlComments.isRefreshing = false

            for (commnentData in response) {
                mCommentsArray.add(0, commnentData)
            }
            mCommentAdapter!!.notifyDataSetChanged()
        }
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackCustom -> {
                finish()
                moveBack()
            }
        }
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}