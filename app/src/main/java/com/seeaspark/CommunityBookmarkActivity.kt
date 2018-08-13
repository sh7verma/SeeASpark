package com.seeaspark

import adapters.CommunityAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_community_bookmark.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.BaseSuccessModel
import models.PostModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class CommunityBookmarkActivity : BaseActivity() {
    private var mLayoutManager: LinearLayoutManager? = null
    private var mCommunityAdapter: CommunityAdapter? = null
    private var mCommunityArray = ArrayList<PostModel.ResponseBean>()

    var mCommunityBookmark: CommunityBookmarkActivity? = null

    private val mOffset: Int = 1
    private var mCurrentVisible: Int = 1

    override fun getContentView() = R.layout.activity_community_bookmark

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {

        mCommunityBookmark = this

        txtTitleCustom.text = getString(R.string.Bookmarks)
        imgOption1Custom.setImageResource(R.mipmap.ic_search)
        imgOption1Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)
        rvCommunityBookmark.layoutManager = mLayoutManager

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        imgBackCustom.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        imgOption1Custom.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        rvCommunityBookmark.setBackgroundColor(ContextCompat.getColor(this, R.color.background))
        txtNoCommunityBookmark.setTextColor(blackColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBackCustom.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        imgOption1Custom.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        rvCommunityBookmark.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        txtNoCommunityBookmark.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {
        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(llCustomToolbar)
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
                intent = Intent(mContext, SearchActivity::class.java)
                intent.putExtra("path", "community")
                intent.putExtra("bookmark", "yes")
                startActivity(intent)
            }
        }
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().getBookmarkPosts(mUtils!!.getString("access_token", ""),
                mOffset, Constants.COMMUNITY)
        call.enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    populateData(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(rvCommunityBookmark, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(llCustomToolbar, t!!.localizedMessage)
            }
        })
    }

    private fun populateData(response: MutableList<PostModel.ResponseBean>) {
        mCommunityArray.addAll(response)
        if (mOffset == 1) {
            if (mCommunityArray.size == 0)
                txtNoCommunityBookmark.visibility = View.VISIBLE
            else {
                mCommunityAdapter = CommunityAdapter(mCommunityArray, mContext!!, mCommunityBookmark!!, null)
                rvCommunityBookmark.adapter = mCommunityAdapter
            }
        } else {
            mCommunityAdapter!!.notifyDataSetChanged()
        }
    }


    fun moveToCommunityDetail(communityId: Int, imgCommunityListing: ImageView) {
        if (connectedToInternet()) {
            val intent = Intent(mContext, CommunityDetailActivity::class.java)
            intent.putExtra("communityId", communityId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        imgCommunityListing, getString(R.string.transition_image))
                startActivity(intent, option.toBundle())
            } else {
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        } else
            showInternetAlert(rvCommunityBookmark)
    }

    fun updateLikeStatus(likedStatus: Int, postId: Int, likeCount: Int) {
        db!!.updateLikeStatus(postId, likedStatus, likeCount)
        sendLikeBroadcast(postId, likedStatus)
        hitLikeAPI(postId)
    }

    fun updateBookmarkStatus(bookmarked: Int, postId: Int, postData: PostModel.ResponseBean) {
        removeCommunity(postData)
        db!!.updateBookmarkStatus(postId, bookmarked)
        sendBookMarkBroadcast(postId, bookmarked)
        hitBookmarkAPI(bookmarked, postId)
    }

    private fun removeCommunity(position: PostModel.ResponseBean) {
        mCommunityArray.remove(position)
        mCommunityAdapter!!.notifyDataSetChanged()
        if (mCommunityArray.size == 0) {
            txtNoCommunityBookmark.visibility = View.VISIBLE
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
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        showToast(mContext!!, response.body().error!!.message!!)
                        removeElement(postId)
                    } else
                        showAlert(rvCommunityBookmark, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                /// change db status to previous
                setPreviousDBStatus(postId)
            }
        })
    }

    private fun removeElement(postId: Int) {
        for ((index, postData) in mCommunityArray.withIndex()) {
            if (postData.id == postId) {
                db!!.deletePostById(postData.id)
                mCommunityArray.remove(postData)
                mCommunityAdapter!!.notifyDataSetChanged()
                break
            }
        }
    }

    private fun hitBookmarkAPI(bookmarked: Int, postId: Int) {
        val call = RetrofitClient.getInstance().markBookmark(mUtils!!.getString("access_token", ""),
                postId)
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                if (response.body().response != null) {

                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        showToast(mContext!!, response.body().error!!.message!!)
                        removeElement(postId)
                    } else
                        showAlert(rvCommunityBookmark, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                showAlert(rvCommunityBookmark, t!!.localizedMessage)
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
                    } else if (intent.getIntExtra("status", 0) == Constants.UNLIKED) {
                        Log.e("Liked = ", "No")
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
                    } else if (intent.getIntExtra("status", 0) == Constants.BOOKMARK) {
                        Log.e("Bookmark = ", "Yes")
                        for ((index, communityData) in mCommunityArray.withIndex()) {
                            if (communityData.id == intent.getIntExtra("postId", 0)) {
                                if (intent.getIntExtra("bookmarkStatus", 0) == 0) {
                                    removeCommunity(communityData)
                                } else {
                                    communityData.bookmarked = intent.getIntExtra("bookmarkStatus", 0)
                                    mCommunityArray[index] = communityData
                                }
                                break
                            }
                        }
                        mCommunityAdapter!!.notifyDataSetChanged()
                    } else if (intent.getIntExtra("status", 0) == Constants.COMMENT) {
                        Log.e("comment = ", "Yes")
                        for ((index, communityData) in mCommunityArray.withIndex()) {
                            if (communityData.id == intent.getIntExtra("postId", 0)) {
                                communityData.comment = intent.getIntExtra("commentCount", 0)
                                mCommunityArray[index] = communityData
                                break
                            }
                        }
                        mCommunityAdapter!!.notifyDataSetChanged()
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

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

}