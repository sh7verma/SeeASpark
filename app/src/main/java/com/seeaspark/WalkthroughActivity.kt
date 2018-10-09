package com.seeaspark

import adapters.NewFragmentPagerAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_walkthrough.*


class WalkthroughActivity : BaseActivity(), ViewTreeObserver.OnGlobalLayoutListener {

    var delay: Long = 0
    var mAdapterWalk: NewFragmentPagerAdapter? = null
    var isFinishEnable = false

    private var revealX: Int = 0
    private var revealY: Int = 0
    private var isAnimationCompleted = false

    override fun getContentView() = R.layout.activity_walkthrough

    override fun initUI() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra("EXTRA_CIRCULAR_REVEAL_X") &&
                intent.hasExtra("EXTRA_CIRCULAR_REVEAL_Y")) {
            llMainWalkthrough.visibility = View.INVISIBLE

            revealX = intent.getIntExtra("EXTRA_CIRCULAR_REVEAL_X", 0)
            revealY = intent.getIntExtra("EXTRA_CIRCULAR_REVEAL_Y", 0)

            val viewTreeObserver = llMainWalkthrough.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener {
                    if (!isAnimationCompleted) {
                        revealActivity(revealX, revealY)
                        llMainWalkthrough.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            }
        }
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

    private fun revealActivity(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(llMainWalkthrough.getWidth(), llMainWalkthrough.getHeight()) * 1.1).toFloat()
            val circularReveal = ViewAnimationUtils.createCircularReveal(llMainWalkthrough, x, y, 0f, finalRadius)
            circularReveal.duration = 400
            circularReveal.interpolator = AccelerateInterpolator()

            llMainWalkthrough.setVisibility(View.VISIBLE)
            circularReveal.start()
            isAnimationCompleted = true
        }
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

    override fun onGlobalLayout() {

    }

    private fun moveBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
        overridePendingTransition(0, 0)
    }

}