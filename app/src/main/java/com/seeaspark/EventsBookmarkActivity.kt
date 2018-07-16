package com.seeaspark


import adapters.EventsAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_events_bookmark.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.BaseSuccessModel
import models.PostModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class EventsBookmarkActivity : BaseActivity() {

    private var mLayoutManager: LinearLayoutManager? = null
    private var mEventsAdapter: EventsAdapter? = null
    private var mEventsArray = ArrayList<PostModel.ResponseBean>()
    private var mBookmarkEvents: EventsBookmarkActivity? = null
    private val mOffset: Int = 1
    private var mCurrentVisible: Int = 1

    override fun getContentView() = R.layout.activity_events_bookmark

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {
        mBookmarkEvents = this
        txtTitleCustom.text = getString(R.string.Bookmarks)

        imgOption1Custom.setImageResource(R.mipmap.ic_search)
        imgOption1Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)

        rvEventsBookmark.layoutManager = mLayoutManager

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
        llMainEventsBookmark.setBackgroundColor(whiteColor)
        txtNoEventsBookmark.setTextColor(blackColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBackCustom.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        imgOption1Custom.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        llMainEventsBookmark.setBackgroundColor(blackColor)
        txtNoEventsBookmark.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {
        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(llCustomToolbar)
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().getBookmarkPosts(mUtils!!.getString("access_token", ""),
                mOffset, Constants.EVENT)
        call.enqueue(object : Callback<PostModel> {

            override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    populateData(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(rvEventsBookmark, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(llCustomToolbar, t!!.localizedMessage)
            }
        })
    }

    private fun populateData(response: MutableList<PostModel.ResponseBean>) {
        mEventsArray.addAll(response)
        if (mOffset == 1) {
            if (mEventsArray.size == 0)
                txtNoEventsBookmark.visibility = View.VISIBLE
            else {
                mEventsAdapter = EventsAdapter(mContext!!, mEventsArray, null, mBookmarkEvents!!)
                rvEventsBookmark.adapter = mEventsAdapter
            }
        } else {
            mEventsAdapter!!.notifyDataSetChanged()
        }
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
                intent.putExtra("bookmark", "yes")
                intent.putExtra("path", "events")
                startActivity(intent)
            }
        }
    }

    fun moveToEventDetail(id: Int) {
        if (connectedToInternet()) {
            val intent = Intent(mContext, EventsDetailActivity::class.java)
            intent.putExtra("eventId", id)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            showInternetAlert(rvEventsBookmark)
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    fun updateLikeStatus(likedStatus: Int, postId: Int, likeCount: Int) {
        db!!.updateLikeStatus(postId, likedStatus, likeCount)
        sendLikeBroadcast(postId, likedStatus)
        hitLikeAPI(postId)
    }

    fun updateBookmarkStatus(bookmarked: Int, postId: Int, postData: PostModel.ResponseBean) {
        removeEvent(postData)
        db!!.updateBookmarkStatus(postId, bookmarked)
        sendBookMarkBroadcast(postId, bookmarked)
        hitBookmarkAPI(bookmarked, postId)
    }

    private fun removeEvent(position: PostModel.ResponseBean) {
        mEventsArray.remove(position)
        mEventsAdapter!!.notifyDataSetChanged()
        if (mEventsArray.size == 0) {
            txtNoEventsBookmark.visibility = View.VISIBLE
        }
    }

    private fun hitLikeAPI(postId: Int) {
        val call = RetrofitClient.getInstance().postActivity(mUtils!!.getString("access_token", ""),
                postId, Constants.LIKE)
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                if (response!!.body().response == null) {
                    /// change db status to previous
                    setPreviousDBStatus(postId)
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        showToast(mContext!!, response.body().error!!.message!!)
                        removeElement(postId)
                    } else
                        showAlert(rvEventsBookmark, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                /// change db status to previous
                setPreviousDBStatus(postId)
            }
        })
    }

    private fun hitBookmarkAPI(bookmarked: Int, postId: Int) {
        val call = RetrofitClient.getInstance().markBookmark(mUtils!!.getString("access_token", ""),
                postId)
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                if (response.body().response != null) {

                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        showToast(mContext!!, response.body().error!!.message!!)
                        removeElement(postId)
                    } else
                        showAlert(rvEventsBookmark, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                showAlert(rvEventsBookmark, t!!.localizedMessage)
            }
        })

    }

    private fun setPreviousDBStatus(postId: Int) {

    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                IntentFilter(Constants.POST_BROADCAST))
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onPause() {
        mCurrentVisible = 0
        super.onPause()
    }

    override fun onResume() {
        mCurrentVisible = 1
        super.onResume()
    }

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            try {
                if (mCurrentVisible == 0) {/// only work user performed operations from other activities
                    if (intent.getIntExtra("status", 0) == Constants.LIKED) {
                        Log.e("Liked = ", "Yes")
                        for ((index, eventData) in mEventsArray.withIndex()) {
                            if (eventData.id == intent.getIntExtra("postId", 0)
                                    && eventData.liked == Constants.UNLIKED) {
                                eventData.liked = Constants.LIKED
                                eventData.like++
                                mEventsArray[index] = eventData
                                break
                            }
                        }
                        mEventsAdapter!!.notifyDataSetChanged()
                    } else if (intent.getIntExtra("status", 0) == Constants.UNLIKED) {
                        Log.e("Liked = ", "No")
                        for ((index, eventData) in mEventsArray.withIndex()) {
                            if (eventData.id == intent.getIntExtra("postId", 0)
                                    && eventData.liked == Constants.LIKED) {
                                eventData.liked = Constants.UNLIKED
                                eventData.like--
                                mEventsArray[index] = eventData
                                break
                            }
                        }
                        mEventsAdapter!!.notifyDataSetChanged()
                    } else if (intent.getIntExtra("status", 0) == Constants.BOOKMARK) {
                        Log.e("Bookmark = ", "Yes")
                        for ((index, eventData) in mEventsArray.withIndex()) {
                            if (eventData.id == intent.getIntExtra("postId", 0)) {
                                if (intent.getIntExtra("bookmarkStatus", 0) == 0) {
                                    removeEvent(eventData)
                                } else {
                                    eventData.bookmarked = intent.getIntExtra("bookmarkStatus", 0)
                                    mEventsArray[index] = eventData
                                }
                                break
                            }
                        }
                        mEventsAdapter!!.notifyDataSetChanged()
                    } else if (intent.getIntExtra("status", 0) == Constants.COMMENT) {
                        Log.e("comment = ", "Yes")
                        for ((index, eventData) in mEventsArray.withIndex()) {
                            if (eventData.id == intent.getIntExtra("postId", 0)) {
                                eventData.comment = intent.getIntExtra("commentCount", 0)
                                mEventsArray[index] = eventData
                                break
                            }
                        }
                        mEventsAdapter!!.notifyDataSetChanged()
                    } else if (intent.getIntExtra("status", 0) == Constants.DELETE) {
                        Log.e("Delete = ", "Yes")
                        removeElement(intent.getIntExtra("postId", 0))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun sendLikeBroadcast(postId: Int, likedStatus: Int) {
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", likedStatus)
        broadCastIntent.putExtra("postId", postId)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    private fun sendBookMarkBroadcast(postId: Int, bookmarked: Int) {
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", Constants.BOOKMARK)
        broadCastIntent.putExtra("bookmarkStatus", bookmarked)
        broadCastIntent.putExtra("postId", postId)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    private fun removeElement(postId: Int) {
        for ((index, postData) in mEventsArray.withIndex()) {
            if (postData.id == postId) {
                db!!.deletePostById(postId)
                mEventsArray.remove(postData)
                mEventsAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }


}