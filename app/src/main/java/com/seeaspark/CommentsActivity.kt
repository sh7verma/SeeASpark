package com.seeaspark

import adapters.CommentsAdapter
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_event.*
import models.BaseSuccessModel
import models.CommentModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.util.*
import java.text.SimpleDateFormat


class CommentsActivity : BaseActivity() {

    private var postId = 0
    private var mOffset = 1

    private var mCommentsArray = ArrayList<CommentModel.ResponseBean>()
    var mCommentIdArray = ArrayList<Int>()
    private var mCommentAdapter: CommentsAdapter? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var mCommentInstance: CommentsActivity? = null
    private var userData: SignupModel? = null
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private var mCommentCount: Int = 0

    override fun getContentView() = R.layout.activity_comments

    override fun initUI() {

        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")

        edComments.typeface = typeface
        edComments.isFocusableInTouchMode = true
        edComments.requestFocus()

        txtTitleCustom.text = getString(R.string.comments)
        mLayoutManager = LinearLayoutManager(mContext)
        mLayoutManager!!.stackFromEnd = true
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
        txtCommentsCount.setBackgroundColor(whiteColor)
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
        txtCommentsCount.setBackgroundColor(blackColor)
    }

    override fun onCreateStuff() {
        mCommentInstance = this
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
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

                if (response.body().response != null) {
                    if (mOffset == 1)
                        dismissLoader()
                    populateData(response.body().response)
                    mCommentCount = response.body().comment_count
                    updateCommentCountByBroadcast()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        showToast(mContext!!, response.body().error!!.message!!)
                        sendDeleteBroadCast()
                        finish()
                    } else
                        showAlert(rvEventsListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<CommentModel>?, t: Throwable?) {
                if (mOffset == 1)
                    dismissLoader()
            }
        })
    }

    private fun sendDeleteBroadCast() {
        db!!.deletePostById(postId)
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", Constants.DELETE)
        broadCastIntent.putExtra("postId", postId)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    private fun populateData(response: List<CommentModel.ResponseBean>) {
        if (mOffset == 1) {
            for (commentData in response) {
                mCommentIdArray.add(commentData.id)
            }
            mCommentsArray.addAll(response)
            mCommentsArray.reverse()
            mCommentAdapter = CommentsAdapter(mContext!!, mCommentsArray, mCommentInstance!!)
            rvComments.adapter = mCommentAdapter

            /// fetching comments in background
            mHandler = Handler()
            getRealTimeComments()
        } else {
            srlComments.isRefreshing = false
            for (commnentData in response) {
                if (!mCommentIdArray.contains(commnentData.id)) {
                    mCommentIdArray.add(commnentData.id)
                    mCommentsArray.add(0, commnentData)
                }
            }
            updateCommentCountByBroadcast()
            mCommentAdapter!!.notifyDataSetChanged()
        }

    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgSendComments.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgSendComments -> {
                if (connectedToInternet()) {
                    addOwnComment(edComments.text.toString().trim())
                    hitCommentAPI(edComments.text.toString().trim())
                    edComments.setText(Constants.EMPTY)
                } else {
                    showInternetAlert(imgSendComments)
                }
            }
            imgBackCustom -> {
                moveBack()
            }
        }
    }

    private fun addOwnComment(commentText: String) {
        val commentModel = CommentModel.ResponseBean()
        commentModel.id = 0
        commentModel.full_name = userData!!.response.full_name
        commentModel.avatar = userData!!.response.avatar
        commentModel.user_id = userData!!.response.id
        commentModel.description = commentText
        commentModel.date_time = getDate()
        mCommentsArray.add(commentModel)
        mCommentAdapter!!.notifyDataSetChanged()
        rvComments.smoothScrollToPosition(mCommentAdapter!!.itemCount - 1)
        mCommentCount++
        updateCommentCountByBroadcast()
    }

    private fun hitCommentAPI(commentText: String) {
        val call = RetrofitClient.getInstance().postComments(mUtils!!.getString("access_token", ""),
                postId, commentText)
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                if (response.body().response != null) {

                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        showToast(mContext!!, response.body().error!!.message!!)
                        sendDeleteBroadCast()
                        finish()
                    } else
                        showAlert(rvEventsListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {

            }
        })
    }

    private fun getRealTimeComments() {
        mRunnable = Runnable {
            hitBackgroundCommentsAPI()
        }
        mHandler!!.postDelayed(mRunnable, 3000)
    }

    private fun hitBackgroundCommentsAPI() {
        val call = RetrofitClient.getInstance().getLatestCommnets(mUtils!!.getString("access_token", ""),
                postId, mCommentsArray[mCommentsArray.size - 1].id)
        call.enqueue(object : Callback<CommentModel> {

            override fun onResponse(call: Call<CommentModel>?, response: Response<CommentModel>) {
                if (response.body().response != null) {
                    for (commentData in response.body().response) {
                        if (!mCommentIdArray.contains(commentData.id)) {
                            mCommentCount++
                            mCommentIdArray.add(commentData.id)
                            mCommentsArray.add(commentData)
                        }
                    }
                    updateCommentCountByBroadcast()
                    mCommentAdapter!!.notifyDataSetChanged()
                    getRealTimeComments()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        showToast(mContext!!, response.body().error!!.message!!)
                        sendDeleteBroadCast()
                        finish()
                    } else
                        showAlert(rvEventsListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<CommentModel>?, t: Throwable?) {

            }
        })
    }

    private fun updateCommentCountByBroadcast() {
        db!!.updateCommentCount(postId, mCommentCount)
        txtCommentsCount.text = "$mCommentCount COMMENTS"
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", Constants.COMMENT)
        broadCastIntent.putExtra("commentCount", mCommentCount)
        broadCastIntent.putExtra("postId", postId)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        mHandler!!.removeCallbacks(mRunnable)
        Constants.closeKeyboard(mContext!!, imgBackCustom)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm aa", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return formatter.format(calendar.time)
    }


}