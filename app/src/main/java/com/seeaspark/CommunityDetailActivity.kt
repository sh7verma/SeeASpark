package com.seeaspark

import adapters.FullImageAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.activity_community_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.BaseSuccessModel
import models.PostDetailModel
import models.PostModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.util.ArrayList

class CommunityDetailActivity : BaseActivity() {

    var mCommunityData: PostModel.ResponseBean? = null
    private var isLiked = 0

    private var mPostId: String = Constants.EMPTY
    private var userData: SignupModel? = null

    override fun getContentView() = R.layout.activity_community_detail

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {
        txtTitleCustom.alpha = 0f

        imgOption1Custom.visibility = View.VISIBLE
        imgOption2Custom.visibility = View.VISIBLE

        imgOption1Custom.setImageResource(R.mipmap.ic_share_white)
        imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_border)

        val cd = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
        llCustomToolbar.background = cd
        cd.alpha = 0

        svViewCommunity.setOnScrollViewListener { v, l, t, oldl, oldt ->
            cd.alpha = getAlphaforActionBar(v.scrollY)
        }

        imgLikeCommunity.setOnLikeListener(object : OnLikeListener {
            override fun liked(p0: LikeButton?) {
                if (connectedToInternet()) {
                    isLiked = 1
                    mCommunityData!!.liked = 1
                    hitLikeAPI(Constants.LIKE)
                    sendLikeBroadcast(Constants.LIKED)
                    mCommunityData!!.like++
                    displayLikeData()
                    db!!.updateLikeStatus(mCommunityData!!.id, Constants.LIKED, mCommunityData!!.like)
                } else {
                    imgLikeCommunity.isLiked = false
                }
            }

            override fun unLiked(p0: LikeButton?) {
                if (connectedToInternet()) {
                    isLiked = 0
                    mCommunityData!!.liked = 0
                    hitLikeAPI(Constants.LIKE)
                    sendLikeBroadcast(Constants.UNLIKED)
                    mCommunityData!!.like--
                    displayLikeData()
                    db!!.updateLikeStatus(mCommunityData!!.id, Constants.UNLIKED, mCommunityData!!.like)
                } else {
                    imgLikeCommunity.isLiked = true
                }
            }
        })
    }

    private fun sendLikeBroadcast(status: Int) {
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", status)
        broadCastIntent.putExtra("postId", mCommunityData!!.id)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {

        llMainCommunity.background = ContextCompat.getDrawable(this, R.drawable.white_short_profile_background)

        txtTitleCommunity.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtTimeCommunity.setTextColor(darkGrey)
        txtDescCommunity.setTextColor(darkGrey)

        llBottomCommunity.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        llLikesCommunity.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtLikeCountCommunity.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llCommentsCommunity.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtCommentCountCommunity.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llMainCommunityParent.setBackgroundColor(whiteColor)

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {

        llMainCommunity.background = ContextCompat.getDrawable(this, R.drawable.dark_short_profile_background)

        txtTitleCommunity.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtTimeCommunity.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtDescCommunity.setTextColor(darkGrey)

        llBottomCommunity.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        llLikesCommunity.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtLikeCountCommunity.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llCommentsCommunity.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtCommentCountCommunity.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llMainCommunityParent.setBackgroundColor(blackColor)

    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        if (intent.hasExtra("postId")) {
            mPostId = intent.getStringExtra("postId")
            if (connectedToInternet())
                hitDetailAPI()
            else
                showInternetAlert(llCustomToolbar)
        } else {
            mCommunityData = db!!.getPostDataById(intent.getIntExtra("communityId", 0), Constants.COMMUNITY)
            populateData()
        }
    }

    private fun hitDetailAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().getPostDetail(mUtils!!.getString("access_token", ""),
                mPostId.toInt())
        call.enqueue(object : Callback<PostDetailModel> {
            override fun onResponse(call: Call<PostDetailModel>?, response: Response<PostDetailModel>?) {
                if (response!!.body().response != null) {
                    mCommunityData = response.body().response
                    addToLocalDatabase(response.body().response)
                    populateData()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN)
                        moveToSplash()
                    else {
                        showToast(mContext!!, response.body().error!!.message!!)
                        finish()
                    }
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<PostDetailModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(llCustomToolbar, t!!.localizedMessage)
            }
        })
    }

    private fun addToLocalDatabase(response: PostModel.ResponseBean) {
        db!!.addPosts(response)
        for (imagesData in response.images) {
            db!!.addPostImages(imagesData, response.id.toString(), Constants.COMMUNITY)
        }
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgOption1Custom.setOnClickListener(this)
        imgOption2Custom.setOnClickListener(this)
        llCommentsCommunity.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        var intent: Intent? = null
        when (view) {
            imgBackCustom -> {
                moveBack()
            }
            imgOption1Custom -> {
                intent = Intent(mContext!!, ShareActivity::class.java)
                intent.putExtra("postUrl", mCommunityData!!.shareable_link)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            imgOption2Custom -> {
                if (connectedToInternet()) {
                    if (mCommunityData!!.bookmarked == 1) {
                        mCommunityData!!.bookmarked = 0
                        imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_border)
                    } else {
                        mCommunityData!!.bookmarked = 1
                        imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_white)
                    }
                    db!!.updateBookmarkStatus(mCommunityData!!.id, mCommunityData!!.bookmarked)
                    sendBookMarkBroadcast(mCommunityData!!.bookmarked)
                    hitBookmarkAPI(mCommunityData!!.id)
                } else
                    showInternetAlert(llMainCommunity)
            }
            llCommentsCommunity -> {
                intent = Intent(mContext!!, CommentsActivity::class.java)
                intent.putExtra("postId", mCommunityData!!.id)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        }
    }

    private fun populateData() {
        svViewCommunity.visibility = View.VISIBLE
        vpCommunityDetail.adapter = FullImageAdapter(mContext!!, mCommunityData!!.images as ArrayList<PostModel.ResponseBean.ImagesBean>, 1)
        cpIndicatorCommunity.setViewPager(vpCommunityDetail)
        cpIndicatorCommunity.fillColor = Color.BLACK

        txtTitleCustom.text = mCommunityData!!.title
        txtTitleCommunity.text = mCommunityData!!.title

        if (mCommunityData!!.date_time.isNotEmpty())
            txtTimeCommunity.text = Constants.displayDateTime(mCommunityData!!.date_time)

        txtDescCommunity.text = mCommunityData!!.description

        if (mCommunityData!!.liked == 1) isLiked = 1

        if (mCommunityData!!.bookmarked == 1)
            imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_white)
        else
            imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_border)

        displayLikeData()
        displayCommentData()
    }

    private fun displayCommentData() {
        if (mCommunityData!!.comment == 1)
            txtCommentCountCommunity.text = "${mCommunityData!!.comment} COMMENT(S)"
        else
            txtCommentCountCommunity.text = "${mCommunityData!!.comment} COMMENT(S)"
    }

    private fun displayLikeData() {
        if (mCommunityData!!.liked == 1) {
            imgLikeCommunity.isLiked = true
            txtLikeCountCommunity.text = "${mCommunityData!!.like} LIKE(S)"
        } else {
            imgLikeCommunity.isLiked = false
            txtLikeCountCommunity.text = "${mCommunityData!!.like} LIKE(S)"
        }
    }

    private fun sendBookMarkBroadcast(bookmarkStatus: Int) {
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", Constants.BOOKMARK)
        broadCastIntent.putExtra("bookmarkStatus", bookmarkStatus)
        broadCastIntent.putExtra("postId", mCommunityData!!.id)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    private fun getAlphaforActionBar(scrollY: Int): Int {
        val minDist = 0
        val maxDist = 600
        when {
            scrollY > maxDist -> {
                txtTitleCustom.alpha = 1.0f
                return 255
            }
            scrollY < minDist -> {
                txtTitleCustom.alpha = 0f
                return 0
            }
            else -> {
                txtTitleCustom.alpha = 0f
                var alpha = 0
                alpha = (255.0 / maxDist * scrollY).toInt()
                return alpha
            }
        }
    }

    private fun hitBookmarkAPI(postId: Int) {
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
                        sendDeleteBroadCast()
                        finish()
                    } else
                        showAlert(llMainCommunity, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                showAlert(llMainCommunity, t!!.localizedMessage)
            }
        })
    }

    private fun hitLikeAPI(status: Int) {
        val call = RetrofitClient.getInstance().postActivity(mUtils!!.getString("access_token", ""),
                mCommunityData!!.id, status)
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                if (response!!.body().response == null) {
                    /// change db status to previous
                    setPreviousDBStatus(status)

                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else if (response.body().error!!.code == Constants.POST_DELETED) {
                        showToast(mContext!!, response.body().error!!.message!!)
                        sendDeleteBroadCast()
                        finish()
                    } else
                        showAlert(llMainCommunity, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                /// change db status to previous
                setPreviousDBStatus(status)
            }
        })
    }

    private fun sendDeleteBroadCast() {
        db!!.deletePostById(mCommunityData!!.id)
        val broadCastIntent = Intent(Constants.POST_BROADCAST)
        broadCastIntent.putExtra("status", Constants.DELETE)
        broadCastIntent.putExtra("postId", mCommunityData!!.id)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    private fun setPreviousDBStatus(status: Int) {
        if (status == 1) {
            /// Change to Unlike
        } else {
            /// Change to Like
        }
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

    private var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (intent.getIntExtra("status", 0) == Constants.COMMENT) {
                    txtCommentCountCommunity.text = "${intent.getIntExtra("commentCount", 0)} COMMENT(S)"
                } else if (intent.getIntExtra("status", 0) == Constants.DELETE) {
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
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