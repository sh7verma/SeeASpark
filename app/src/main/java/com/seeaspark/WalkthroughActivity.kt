package com.seeaspark

import adapters.NewFragmentPagerAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.support.v4.view.ViewPager
import android.view.View
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_walkthrough.*

class WalkthroughActivity : BaseActivity() {

    var delay: Long = 0
    var mAdapterWalk: NewFragmentPagerAdapter? = null
    var isFinishEnable = false

    override fun getContentView() = R.layout.activity_walkthrough

    override fun initUI() {

    }

    override fun onCreateStuff() {

        if (intent.hasExtra("settings"))
            isFinishEnable = true

        mAdapterWalk = NewFragmentPagerAdapter(supportFragmentManager, mContext!!)
        vpWalk.adapter = mAdapterWalk
        cpIndicatorWalk.setViewPager(vpWalk)
        cpIndicatorWalk.fillColor = Color.BLACK
        delay = 12000
        mHandler.postDelayed(runnable, delay)

        cpIndicatorWalk.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    delay = 12000
                    mHandler.removeCallbacks(runnable)
                    mHandler.postDelayed(runnable, delay)
                } else {
                    delay = 3000
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
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
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
        if (isFinishEnable) {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        } else
            super.onBackPressed()
    }
}