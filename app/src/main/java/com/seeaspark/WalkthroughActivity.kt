package com.seeaspark

import adapters.WalkthroughAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_walkthrough.*

class WalkthroughActivity : BaseActivity() {

    var mAdapterWalk: WalkthroughAdapter? = null
    private val walkArray = intArrayOf(R.mipmap.walk1, R.mipmap.walk1, R.mipmap.walk1, R.mipmap.walk1, R.mipmap.walk1)

    override fun initUI() {
    }

    override fun onCreateStuff() {

        mAdapterWalk = WalkthroughAdapter(walkArray, ArrayList<String>(), mContext!!)
        vpWalk.adapter = mAdapterWalk
        cpIndicatorWalk.setViewPager(vpWalk)
        cpIndicatorWalk.fillColor =  Color.BLACK

    }

    override fun initListener() {
        txtGotIt.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            txtGotIt -> {
                var intent = Intent(mContext, CreateProfileActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        }
    }

    override fun getContentView() = R.layout.activity_walkthrough

    override fun getContext() = this


    override fun onResume() {
        mHandler.postDelayed(runnable, 8000)
        super.onResume()
    }

    override fun onPause() {
        mHandler.removeCallbacks(runnable)
        super.onPause()
    }

    private val mHandler = Handler()
    internal var runnable: Runnable = object : Runnable {
        override fun run() {
            if (vpWalk.currentItem == vpWalk.adapter!!.getCount() - 1) {
                vpWalk.currentItem = 0
            } else {
                vpWalk.currentItem = vpWalk.currentItem + 1
            }
            mHandler.postDelayed(this, 8000)
        }
    }

}