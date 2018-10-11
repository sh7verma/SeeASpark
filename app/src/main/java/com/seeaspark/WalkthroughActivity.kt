package com.seeaspark

import adapters.NewFragmentPagerAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.support.v4.view.ViewPager
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_walkthrough.*


class WalkthroughActivity : BaseActivity() {

    var delay: Long = 0
    var mAdapterWalk: NewFragmentPagerAdapter? = null
    var isFinishEnable = false

    override fun getContentView() = R.layout.activity_walkthrough

    override fun initUI() {
        if (intent.hasExtra("image"))
            displaySplashAnimation()
    }

    private fun displaySplashAnimation() {
        val byteArray = intent.getByteArrayExtra("image")
        val bmp: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        imgAnimationOverlay.setImageBitmap(bmp)

        val scaleAnimation = ScaleAnimation(1f, 3f, 1f, 3f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.duration = 300

        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = 300

        val animatorSet = AnimationSet(true)
        animatorSet.addAnimation(scaleAnimation)
        animatorSet.addAnimation(alphaAnimation)
        imgAnimationOverlay.startAnimation(animatorSet)

        animatorSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                imgAnimationOverlay.alpha = 0f
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
    }


    override fun onCreateStuff() {

        if (intent.hasExtra("settings"))
            isFinishEnable = true

        mAdapterWalk = NewFragmentPagerAdapter(supportFragmentManager, mContext!!)
        vpWalk.adapter = mAdapterWalk
        cpIndicatorWalk.setViewPager(vpWalk)
        cpIndicatorWalk.fillColor = Color.BLACK
        delay = 16000
        mHandler.postDelayed(runnable, delay)

        cpIndicatorWalk.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    delay = 16000
                    mHandler.removeCallbacks(runnable)
                    mHandler.postDelayed(runnable, delay)
                } else {
                    delay = 4000
                    mHandler.removeCallbacks(runnable)
                    mHandler.postDelayed(runnable, delay)
                }
            }
        })
    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    override fun initListener() {
        txtGotIt.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            txtGotIt -> {
                if (isFinishEnable) {
                    moveBack()
                } else {
                    mUtils!!.setString("device_token", FirebaseInstanceId.getInstance().token)
                    val intent = Intent(mContext, AfterWalkThroughActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                }
            }
        }
    }

    override fun getContext() = this

    override fun onPause() {
        mHandler.removeCallbacks(runnable)
        super.onPause()
    }

    private val mHandler = Handler()
    internal var runnable: Runnable = object : Runnable {
        override fun run() {

            if (vpWalk.currentItem < 5) {
                vpWalk.currentItem = vpWalk.currentItem + 1
            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(0, 0)
    }

}