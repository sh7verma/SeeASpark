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
import models.BaseSuccessModel
import models.PostModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants


class EventsDetailActivity : BaseActivity() {

    var mEventData: PostModel.ResponseBean? = null
    var isGoing = 0
    var isIntreseted = 0

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

        imgLikeEvents.setOnLikeListener(object : OnLikeListener {
            override fun liked(p0: LikeButton?) {
                mEventData!!.like++
                displayLikeData()
            }

            override fun unLiked(p0: LikeButton?) {
                mEventData!!.like--
                displayLikeData()
            }
        })
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
        mEventData = intent.getParcelableExtra("eventData")
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
            txtCommentCountEvents.text = "${mEventData!!.comment} COMMENT"
        else
            txtCommentCountEvents.text = "${mEventData!!.comment} COMMENTS"
    }

    private fun displayLikeData() {
        if (mEventData!!.like == 1)
            txtLikeCountEvents.text = "${mEventData!!.like} LIKE"
        else
            txtLikeCountEvents.text = "${mEventData!!.like} LIKES"
    }

    private fun displayGoingData() {
        if (mEventData!!.going <= 3) {
            txtGoingCount.text = "${mEventData!!.going} GOING"

            when {
                mEventData!!.going == 1 -> {
                    Picasso.with(mContext).load(mEventData!!.going_list[0].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents1)
                    imgPeopleEvents2.setImageResource(0)
                    imgPeopleEvents3.setImageResource(0)
                }
                mEventData!!.going == 2 -> {

                    Picasso.with(mContext).load(mEventData!!.going_list[0].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents1)

                    Picasso.with(mContext).load(mEventData!!.going_list[1].avatar)
                            .placeholder(R.drawable.placeholder_image)
                            .into(imgPeopleEvents2)
                    imgPeopleEvents3.setImageResource(0)
                }
                mEventData!!.going == 3 -> {

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
            txtGoingCount.text = "${mEventData!!.going - 3} GOING"

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

            }
            llLikesEvents -> {

            }
            llCommentsEvents -> {

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
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
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
                        mEventData!!.going++
                        displayGoingData()
                        txtInterestedEvents.setTextColor(blackColor)
                        txtInterestedEvents.text = getString(R.string.going)
                        txtInterestedEvents.setBackgroundResource(R.drawable.selected_interesetd)
                    } else {
                        isGoing = 0
                        isIntreseted = 0
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
                        mEventData!!.going--
                        displayGoingData()
                    }
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(llGoingEvents, t!!.localizedMessage)
            }
        })
    }

}