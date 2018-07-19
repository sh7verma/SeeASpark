package com.seeaspark

import adapters.CommunityAdapter
import adapters.EventsAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_search_events.*
import models.BaseSuccessModel
import models.PostModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class SearchEventCommunityActivity : BaseActivity() {

    private var mCommunityAdapter: CommunityAdapter? = null
    private var mEventsAdapter: EventsAdapter? = null
    private var mCommunityArray = ArrayList<PostModel.ResponseBean>()
    private var mEventsArray = ArrayList<PostModel.ResponseBean>()
    private var mSearchInstance: SearchEventCommunityActivity? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var isCommunity = false
    private var isBookmark = false
    private var mCurrentVisible: Int = 1
    private var mOffset: Int = 1

    override fun getContentView() = R.layout.activity_search_events

    override fun initUI() {
        mLayoutManager = LinearLayoutManager(mContext)
        rvSearchEventCommunity.layoutManager = mLayoutManager
    }

    override fun displayDayMode() {
        imgBackSearch.setBackgroundResource(whiteRipple)
        imgCancelSearch.setBackgroundResource(whiteRipple)
        edSearchEventCommunity.setBackgroundColor(whiteColor)
        imgBackSearch.setImageResource(R.mipmap.ic_back_black)

        llMainSearchEvents.setBackgroundColor(whiteColor)
        edSearchEventCommunity.setTextColor(blackColor)
        txtNoResultFound.setTextColor(blackColor)
    }

    override fun displayNightMode() {
        imgBackSearch.setBackgroundResource(blackRipple)
        imgCancelSearch.setBackgroundResource(blackRipple)
        edSearchEventCommunity.setBackgroundColor(blackColor)
        imgBackSearch.setImageResource(R.mipmap.ic_back_org)
        llMainSearchEvents.setBackgroundColor(blackColor)
        edSearchEventCommunity.setTextColor(whiteColor)
        txtNoResultFound.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {
        mSearchInstance = this

        if (intent.hasExtra("bookmark"))
            isBookmark = true

        if (intent.getStringExtra("path") == "community")
            isCommunity = true

        imgCancelSearch.visibility = View.INVISIBLE

        edSearchEventCommunity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (char.toString().isNotEmpty())
                    imgCancelSearch.visibility = View.VISIBLE
                else
                    imgCancelSearch.visibility = View.INVISIBLE
            }
        })

        edSearchEventCommunity.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (connectedToInternet()) {
                    Constants.closeKeyboard(mContext, llMainSearchEvents)
                    hitAPI(edSearchEventCommunity.text.toString())
                } else
                    showInternetAlert(llMainSearchEvents)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun hitAPI(searchText: String) {
        if (isBookmark) {
            /// search from bookmark
            if (isCommunity) {
                /// community section
                val call = RetrofitClient.getInstance().searchBookmarkPost(mUtils!!.getString("access_token", ""),
                        Constants.COMMUNITY, searchText, mOffset)
                call.enqueue(object : Callback<PostModel> {
                    override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {
                        if (response.body().response != null) {
                            populateCommunityData(response.body().response)
                        } else {
                            if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                                moveToSplash()
                            } else
                                showAlert(rvSearchEventCommunity, response.body().error!!.message!!)
                        }
                    }

                    override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                        showAlert(llMainSearchEvents, t!!.localizedMessage)
                    }
                })
            } else {
                /// Event section
                val call = RetrofitClient.getInstance().searchBookmarkPost(mUtils!!.getString("access_token", ""),
                        Constants.EVENT, searchText, mOffset)
                call.enqueue(object : Callback<PostModel> {
                    override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {
                        if (response.body().response != null) {
                            populateEventData(response.body().response)
                        } else {
                            if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                                moveToSplash()
                            } else
                                showAlert(rvSearchEventCommunity, response.body().error!!.message!!)
                        }
                    }

                    override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                        showAlert(llMainSearchEvents, t!!.localizedMessage)
                    }
                })
            }
        } else {
            if (isCommunity) {
                /// community section
                val call = RetrofitClient.getInstance().searchPost(mUtils!!.getString("access_token", ""),
                        Constants.COMMUNITY, searchText, mOffset)
                call.enqueue(object : Callback<PostModel> {
                    override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {
                        if (response.body().response != null) {
                            populateCommunityData(response.body().response)
                        } else {
                            if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                                moveToSplash()
                            } else
                                showAlert(rvSearchEventCommunity, response.body().error!!.message!!)
                        }
                    }

                    override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                        showAlert(llMainSearchEvents, t!!.localizedMessage)
                    }
                })
            } else {
                /// Event section
                val call = RetrofitClient.getInstance().searchPost(mUtils!!.getString("access_token", ""),
                        Constants.EVENT, searchText, mOffset)
                call.enqueue(object : Callback<PostModel> {
                    override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {
                        if (response.body().response != null) {
                            populateEventData(response.body().response)
                        } else {
                            if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                                moveToSplash()
                            } else
                                showAlert(rvSearchEventCommunity, response.body().error!!.message!!)
                        }
                    }

                    override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                        showAlert(llMainSearchEvents, t!!.localizedMessage)
                    }
                })
            }
        }
    }

    private fun populateCommunityData(response: List<PostModel.ResponseBean>) {
        mCommunityArray.clear()
        mCommunityArray.addAll(response)
        if (mOffset == 1) {
            if (mCommunityArray.size == 0)
                txtNoResultFound.visibility = View.VISIBLE
            else
                txtNoResultFound.visibility = View.GONE
            mCommunityAdapter = CommunityAdapter(mCommunityArray, mContext!!, mSearchInstance!!, null)
            rvSearchEventCommunity.adapter = mCommunityAdapter
        } else {
            mCommunityAdapter!!.notifyDataSetChanged()
        }
    }

    private fun populateEventData(response: MutableList<PostModel.ResponseBean>) {
        mEventsArray.clear()
        mEventsArray.addAll(response)
        if (mOffset == 1) {
            if (mEventsArray.size == 0)
                txtNoResultFound.visibility = View.VISIBLE
            else
                txtNoResultFound.visibility = View.GONE
            mEventsAdapter = EventsAdapter(mContext!!, mEventsArray, null, mSearchInstance!!)
            rvSearchEventCommunity.adapter = mEventsAdapter
        } else {
            mEventsAdapter!!.notifyDataSetChanged()
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
                if (isCommunity) {
                    mCommunityArray.clear()
                    if (mCommunityAdapter != null) {
                        mCommunityAdapter!!.notifyDataSetChanged()
                    }
                } else {
                    mEventsArray.clear()
                    if (mEventsAdapter != null) {
                        mEventsAdapter!!.notifyDataSetChanged()
                    }
                }
                edSearchEventCommunity.setText(Constants.EMPTY)
            }
        }
    }

    private fun moveBack() {
        Constants.closeKeyboard(mContext!!, imgBackSearch)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    fun moveToCommunityDetail(communityId: Int) {
        if (connectedToInternet()) {
            val intent = Intent(mContext, CommunityDetailActivity::class.java)
            intent.putExtra("communityId", communityId)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            showInternetAlert(rvSearchEventCommunity)
    }

    fun moveToEventDetail(eventId: Int) {
        if (connectedToInternet()) {
            val intent = Intent(mContext, EventsDetailActivity::class.java)
            intent.putExtra("eventId", eventId)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            showInternetAlert(rvSearchEventCommunity)
    }

    fun updateLikeStatus(likedStatus: Int, postId: Int, likeCount: Int) {
        db!!.updateLikeStatus(postId, likedStatus, likeCount)
        sendEventLikeBroadcast(postId, likedStatus)
        hitLikeAPI(postId)
    }

    fun updateBookmarkStatus(bookmarked: Int, postId: Int) {
        db!!.updateBookmarkStatus(postId, bookmarked)
        sendEventBookMarkBroadcast(postId, bookmarked)
        hitBookmarkAPI(bookmarked, postId)
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
                        if (isCommunity)
                            removeCommunityElement(postId)
                        else
                            removeEventElement(postId)
                    } else
                        showAlert(llMainSearchEvents, response.body().error!!.message!!)
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
                        if (isCommunity)
                            removeCommunityElement(postId)
                        else
                            removeEventElement(postId)
                    } else
                        showAlert(llMainSearchEvents, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                showAlert(rvSearchEventCommunity, t!!.localizedMessage)
            }
        })
    }

    private fun setPreviousDBStatus(postId: Int) {

    }

    private fun sendEventLikeBroadcast(postId: Int, likedStatus: Int) {
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", likedStatus)
        broadCastIntent.putExtra("postId", postId)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    private fun sendEventBookMarkBroadcast(postId: Int, bookmarked: Int) {
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", Constants.BOOKMARK)
        broadCastIntent.putExtra("bookmarkStatus", bookmarked)
        broadCastIntent.putExtra("postId", postId)
        broadcaster!!.sendBroadcast(broadCastIntent)
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
                        if (isCommunity) {
                            for ((index, communityData) in mCommunityArray.withIndex()) {
                                if (communityData.id == intent.getIntExtra("postId", 0)
                                        && communityData.liked == Constants.UNLIKED) {
                                    communityData.liked = Constants.LIKED
                                    communityData.like++
                                    mCommunityArray[index] = communityData
                                    break
                                }
                            }
                            mCommunityAdapter!!.notifyDataSetChanged()
                        } else {
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
                        }
                    } else if (intent.getIntExtra("status", 0) == Constants.UNLIKED) {
                        Log.e("Liked = ", "No")
                        if (isCommunity) {
                            for ((index, communityData) in mCommunityArray.withIndex()) {
                                if (communityData.id == intent.getIntExtra("postId", 0)
                                        && communityData.liked == Constants.LIKED) {
                                    communityData.liked = Constants.UNLIKED
                                    communityData.like--
                                    mCommunityArray[index] = communityData
                                    break
                                }
                            }
                            mCommunityAdapter!!.notifyDataSetChanged()
                        } else {
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
                        }
                    } else if (intent.getIntExtra("status", 0) == Constants.BOOKMARK) {
                        Log.e("Bookmark = ", "Yes")
                        if (isCommunity) {
                            for ((index, communityData) in mCommunityArray.withIndex()) {
                                if (communityData.id == intent.getIntExtra("postId", 0)) {
                                    communityData.bookmarked = intent.getIntExtra("bookmarkStatus", 0)
                                    mCommunityArray[index] = communityData
                                    break
                                }
                            }
                            mCommunityAdapter!!.notifyDataSetChanged()
                        } else {
                            for ((index, eventData) in mEventsArray.withIndex()) {
                                if (eventData.id == intent.getIntExtra("postId", 0)) {
                                    eventData.bookmarked = intent.getIntExtra("bookmarkStatus", 0)
                                    mEventsArray[index] = eventData
                                    break
                                }
                            }
                            mEventsAdapter!!.notifyDataSetChanged()
                        }
                    } else if (intent.getIntExtra("status", 0) == Constants.COMMENT) {
                        Log.e("comment = ", "Yes")
                        if (isCommunity) {
                            for ((index, communityData) in mCommunityArray.withIndex()) {
                                if (communityData.id == intent.getIntExtra("postId", 0)) {
                                    communityData.comment = intent.getIntExtra("commentCount", 0)
                                    mCommunityArray[index] = communityData
                                    break
                                }
                            }
                            mCommunityAdapter!!.notifyDataSetChanged()
                        } else {
                            for ((index, eventData) in mEventsArray.withIndex()) {
                                if (eventData.id == intent.getIntExtra("postId", 0)) {
                                    eventData.comment = intent.getIntExtra("commentCount", 0)
                                    mEventsArray[index] = eventData
                                    break
                                }
                            }
                            mEventsAdapter!!.notifyDataSetChanged()
                        }
                    } else if (intent.getIntExtra("status", 0) == Constants.DELETE) {
                        Log.e("comment = ", "Yes")
                        if (isCommunity) {
                            removeCommunityElement(intent.getIntExtra("postId", 0))
                        } else {
                            removeEventElement(intent.getIntExtra("postId", 0))
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun removeEventElement(postId: Int) {
        for ((index, postData) in mEventsArray.withIndex()) {
            if (postData.id == postId) {
                db!!.deletePostById(postId)
                mEventsArray.remove(postData)
                mEventsAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    private fun removeCommunityElement(postId: Int) {
        for ((index, postData) in mCommunityArray.withIndex()) {
            if (postData.id == postId) {
                db!!.deletePostById(postId)
                mCommunityArray.remove(postData)
                mCommunityAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }


}