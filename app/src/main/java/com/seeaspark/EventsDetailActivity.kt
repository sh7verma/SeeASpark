package com.seeaspark

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_events_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_interested.*
import kotlinx.android.synthetic.main.fragment_event.*
import models.BaseSuccessModel
import models.PostModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants


class EventsDetailActivity : BaseActivity() {

    var mEventData: PostModel.ResponseBean? = null
    private var isGoing = 0
    private var isIntreseted = 0
    private var isLiked = 0

    private var userData: SignupModel? = null

    override fun getContentView() = R.layout.activity_events_details

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

        svViewEvent.setOnScrollViewListener { v, l, t, oldl, oldt ->
            cd.alpha = getAlphaforActionBar(v.scrollY)
        }

        imgLikeEvent.setOnLikeListener(object : OnLikeListener {
            override fun liked(p0: LikeButton?) {
                if (connectedToInternet()) {
                    isLiked = 1
                    mEventData!!.liked = 1
                    hitLikeAPI(Constants.LIKE)
                    sendLikeBroadcast(Constants.LIKED)
                    mEventData!!.like++
                    displayLikeData()
                    db!!.updateLikeEventStatus(mEventData!!.id, Constants.LIKED, mEventData!!.like)
                } else {
                    imgLikeEvent.isLiked = false
                }
            }

            override fun unLiked(p0: LikeButton?) {
                if (connectedToInternet()) {
                    isLiked = 0
                    mEventData!!.liked = 0
                    hitLikeAPI(Constants.LIKE)
                    sendLikeBroadcast(Constants.UNLIKED)
                    mEventData!!.like--
                    displayLikeData()
                    db!!.updateLikeEventStatus(mEventData!!.id, Constants.UNLIKED, mEventData!!.like)
                } else {
                    imgLikeEvent.isLiked = true
                }
            }
        })
    }

    private fun sendLikeBroadcast(status: Int) {
        val broadCastIntent = Intent(Constants.EVENT_BROADCAST)
        broadCastIntent.putExtra("status", status)
        broadCastIntent.putExtra("postId", mEventData!!.id)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {

        llMainEvents.background = ContextCompat.getDrawable(this, R.drawable.white_short_profile_background)

        txtTitleEvents.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtLocationEvents.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtTimeEvent.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtGoingCount.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtEventLink.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtDescEvents.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llGoingEvents.setBackgroundResource(whiteRipple)

        llBottomEvents.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        llLikesEvents.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtLikeCountEvents.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        llCommentsEvents.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtCommentCountEvents.setTextColor(ContextCompat.getColor(this, R.color.black_color))

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {

        llMainEvents.background = ContextCompat.getDrawable(this, R.drawable.dark_short_profile_background)

        txtTitleEvents.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtLocationEvents.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtTimeEvent.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtGoingCount.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtEventLink.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtDescEvents.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llGoingEvents.setBackgroundResource(blackRipple)

        llBottomEvents.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        llLikesEvents.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtLikeCountEvents.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llCommentsEvents.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtCommentCountEvents.setTextColor(ContextCompat.getColor(this, R.color.white_color))

    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        mEventData = db!!.getPostDataById(intent.getIntExtra("eventId", 0), Constants.EVENT)
        populateData()
    }

    private fun populateData() {

        Picasso.with(mContext).load(mEventData!!.images[0].image_url).centerCrop().resize(mWidth, resources.getDimension(R.dimen._240sdp).toInt()).into(imgEventDetail)

        txtTitleCustom.text = mEventData!!.title
        txtTitleEvents.text = mEventData!!.title
        txtLocationEvents.text = mEventData!!.address
        txtTimeEvent.text = Constants.displayDateTime(mEventData!!.date_time)
        txtDescEvents.text = mEventData!!.description
        txtEventLink.text = mEventData!!.url

        if (mEventData!!.liked == 1) isLiked = 1

        if (mEventData!!.bookmarked == 1)
            imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_white)
        else
            imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_border)

        displayLikeData()

        displayCommentData()

        displayGoingData()

        if (mEventData!!.is_going == 1) {
            isGoing = 1
            txtInterestedEvents.setTextColor(blackColor)
            txtInterestedEvents.text = getString(R.string.going)
            txtInterestedEvents.setBackgroundResource(R.drawable.selected_interesetd)
        } else {
            if (mEventData!!.interested == 1) {
                isIntreseted = 1
                txtInterestedEvents.setTextColor(blackColor)
                txtInterestedEvents.setBackgroundResource(R.drawable.selected_interesetd)
            }
        }
    }

    private fun displayCommentData() {
        if (mEventData!!.comment == 1)
            txtCommentCountEvents.text = "${mEventData!!.comment} COMMENT(S)"
        else
            txtCommentCountEvents.text = "${mEventData!!.comment} COMMENT(S)"
    }

    private fun displayLikeData() {
        if (mEventData!!.liked == 1) {
            imgLikeEvent.isLiked = true
            txtLikeCountEvents.text = "${mEventData!!.like} LIKE(S)"
        } else {
            imgLikeEvent.isLiked = false
            txtLikeCountEvents.text = "${mEventData!!.like} LIKE(S)"
        }
    }

    private fun displayGoingData() {
        if (mEventData!!.going_list.size <= 3) {
            txtGoingCount.text = "${mEventData!!.going_list.size} GOING"

            when {
                mEventData!!.going_list.size == 1 -> {
                    Picasso.with(mContext).load(mEventData!!.going_list[0].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents1)
                    imgPeopleEvents2.setImageResource(0)
                    imgPeopleEvents3.setImageResource(0)
                }
                mEventData!!.going_list.size == 2 -> {

                    Picasso.with(mContext).load(mEventData!!.going_list[0].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents1)

                    Picasso.with(mContext).load(mEventData!!.going_list[1].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents2)
                    imgPeopleEvents3.setImageResource(0)
                }
                mEventData!!.going_list.size == 3 -> {

                    Picasso.with(mContext).load(mEventData!!.going_list[0].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents1)

                    Picasso.with(mContext).load(mEventData!!.going_list[1].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents2)

                    Picasso.with(mContext).load(mEventData!!.going_list[2].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents3)
                }
            }
        } else {
            txtGoingCount.text = "${mEventData!!.going_list.size - 3} GOING"

            Picasso.with(mContext).load(mEventData!!.going_list[0].avatar)
                    .placeholder(R.drawable.placeholder_image).into(imgPeopleEvents1)

            Picasso.with(mContext).load(mEventData!!.going_list[1].avatar)
                    .placeholder(R.drawable.placeholder_image).into(imgPeopleEvents2)

            Picasso.with(mContext).load(mEventData!!.going_list[2].avatar)
                    .placeholder(R.drawable.placeholder_image).into(imgPeopleEvents3)
        }
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgOption1Custom.setOnClickListener(this)
        imgOption2Custom.setOnClickListener(this)
        llCommentsEvents.setOnClickListener(this)
        txtEventLink.setOnClickListener(this)
        txtInterestedEvents.setOnClickListener(this)
        llGoingEvents.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        var intent: Intent? = null
        when (view) {
            imgBackCustom -> {
                moveBack()
            }
            imgOption1Custom -> {

            }
            imgOption2Custom -> {
                if (connectedToInternet()) {
                    if (mEventData!!.bookmarked == 1) {
                        mEventData!!.bookmarked = 0
                        imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_border)
                    } else {
                        mEventData!!.bookmarked = 1
                        imgOption2Custom.setImageResource(R.mipmap.ic_bookmark_white)
                    }
                    db!!.updateBookmarkEventStatus(mEventData!!.id, mEventData!!.bookmarked)
                    sendBookMarkBroadcast(mEventData!!.bookmarked)
                    hitBookmarkAPI(mEventData!!.id)
                } else
                    showInternetAlert(llGoingEvents)
            }
            llCommentsEvents -> {
                intent = Intent(mContext!!, CommentsActivity::class.java)
                intent.putExtra("postId", mEventData!!.id)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            txtEventLink -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mEventData!!.url))
                startActivity(browserIntent)
            }
            txtInterestedEvents -> {
                if (connectedToInternet())
                    showInterestedOptions()
                else
                    showInternetAlert(txtInterestedEvents)
            }
            llGoingEvents -> {
                intent = Intent(mContext, EventsGoingListingActivity::class.java)
                intent.putParcelableArrayListExtra("goingList", mEventData!!.going_list as ArrayList)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        }
    }

    private fun sendBookMarkBroadcast(bookmarkStatus: Int) {
        val broadCastIntent = Intent(Constants.EVENT_BROADCAST)
        broadCastIntent.putExtra("status", Constants.BOOKMARK)
        broadCastIntent.putExtra("bookmarkStatus", bookmarkStatus)
        broadCastIntent.putExtra("postId", mEventData!!.id)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
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

    private fun showInterestedOptions() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_interested)


        var dialogParms = CoordinatorLayout.LayoutParams(mWidth - (mWidth / 16), mHeight / 6)
        dialogParms.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        val bottomSheet = dialog.window.findViewById(android.support.design.R.id.design_bottom_sheet) as FrameLayout
        bottomSheet.setBackgroundResource(R.drawable.white_short_profile_background)
        bottomSheet.layoutParams = dialogParms

        val txtInterestedDialog = dialog.txtInterestedDialog
        val txtGoingDialog = dialog.txtGoingDialog

        if (isGoing == 1)
            txtGoingDialog.text = getString(R.string.not_going)

        if (isIntreseted == 1)
            txtInterestedDialog.text = getString(R.string.not_interested)

        txtInterestedDialog.setOnClickListener {
            if (connectedToInternet())
                hitActivityAPI(Constants.INTERESTED)
            else
                showInternetAlert(llGoingEvents)
            dialog.dismiss()
        }

        txtGoingDialog.setOnClickListener {
            if (connectedToInternet())
                hitActivityAPI(Constants.GOING)
            else
                showInternetAlert(llGoingEvents)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun hitActivityAPI(status: Int) {
        showLoader()
        val call = RetrofitClient.getInstance().eventActivity(mUtils!!.getString("access_token", ""),
                mEventData!!.id, status)
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                if (status == Constants.GOING) {
                    if (isGoing == 0) {
                        isGoing = 1
                        isIntreseted = 0
                        db!!.updateGoingStatusEvents(mEventData!!.id, isGoing, isIntreseted)/// updating going and intresetd status
                        addOwnToGoingList()/// adding data from local database
                        mEventData!!.going++
                        displayGoingData()
                        txtInterestedEvents.setTextColor(blackColor)
                        txtInterestedEvents.text = getString(R.string.going)
                        txtInterestedEvents.setBackgroundResource(R.drawable.selected_interesetd)
                    } else {
                        isGoing = 0
                        isIntreseted = 0
                        db!!.updateGoingStatusEvents(mEventData!!.id, isGoing, isIntreseted)/// updating going and intresetd status
                        removeGoingList()/// removing data to local database
                        mEventData!!.going--
                        displayGoingData()
                        txtInterestedEvents.setTextColor(ContextCompat.getColor(this@EventsDetailActivity, R.color.greyTextColor))
                        txtInterestedEvents.text = getString(R.string.interested)
                        txtInterestedEvents.setBackgroundResource(R.drawable.default_interested)
                    }
                } else {
                    if (isIntreseted == 0) {
                        isIntreseted = 1
                        txtInterestedEvents.setTextColor(blackColor)
                        txtInterestedEvents.text = getString(R.string.interested)
                        txtInterestedEvents.setBackgroundResource(R.drawable.selected_interesetd)
                    } else {
                        isIntreseted = 0
                        txtInterestedEvents.setTextColor(ContextCompat.getColor(this@EventsDetailActivity, R.color.greyTextColor))
                        txtInterestedEvents.text = getString(R.string.interested)
                        txtInterestedEvents.setBackgroundResource(R.drawable.default_interested)
                    }
                    if (isGoing == 1) {
                        isGoing = 0
                        removeGoingList()
                        mEventData!!.going--
                        displayGoingData()
                    }
                    db!!.updateGoingStatusEvents(mEventData!!.id, isGoing, isIntreseted)/// updating going and intresetd status
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(llGoingEvents, t!!.localizedMessage)
            }
        })
    }

    private fun hitLikeAPI(status: Int) {
        val call = RetrofitClient.getInstance().eventActivity(mUtils!!.getString("access_token", ""),
                mEventData!!.id, status)
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                if (response!!.body().response == null) {
                    /// change db status to previous
                    setPreviousDBStatus(status)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                /// change db status to previous
                setPreviousDBStatus(status)
            }
        })
    }

    private fun hitBookmarkAPI(postId: Int) {
        val call = RetrofitClient.getInstance().eventBookmark(mUtils!!.getString("access_token", ""),
                postId)
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                if (response.body().response != null) {

                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(rvEventsListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                showAlert(rvEventsListing, t!!.localizedMessage)
            }
        })
    }

    private fun setPreviousDBStatus(status: Int) {
        if (status == 1) {
            /// Change to Unlike
        } else {
            /// Change to Like
        }
    }

    private fun addOwnToGoingList() {
        val goingUserData = PostModel.ResponseBean.GoingUserBean()
        goingUserData.avatar = userData!!.response.avatar
        goingUserData.full_name = userData!!.response.full_name
        goingUserData.id = userData!!.response.id
        mEventData!!.going_list!!.add(0, goingUserData)
        db!!.addPostGoingUsers(goingUserData, mEventData!!.id.toString())
    }

    private fun removeGoingList() {
        for (goingData in mEventData!!.going_list) {
            if (goingData.id == userData!!.response.id) {
                mEventData!!.going_list.remove(goingData)
                break
            }
        }
        db!!.removeGoingUser(userData!!.response.id)
    }
}