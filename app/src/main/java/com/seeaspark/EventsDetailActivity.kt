package com.seeaspark

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_events_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_interested.*


class EventsDetailActivity : BaseActivity() {
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

        llBottomEvents.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        llLikesEvents.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtLikeCountEvents.setTextColor(ContextCompat.getColor(this, R.color.white_color))

        llCommentsEvents.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtCommentCountEvents.setTextColor(ContextCompat.getColor(this, R.color.white_color))

    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgOption1Custom.setOnClickListener(this)
        imgOption2Custom.setOnClickListener(this)
        llLikesEvents.setOnClickListener(this)
        llLikesEvents.setOnClickListener(this)
        txtEventLink.setOnClickListener(this)
        llCommentsEvents.setOnClickListener(this)
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

            }
            txtInterestedEvents -> {
                showInterestedOptions()
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
        val maxDist = 300
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

        txtInterestedDialog.setOnClickListener {
            dialog.dismiss()
        }

        txtGoingDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}