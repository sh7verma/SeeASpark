package com.seeaspark

import adapters.FullViewPagerAdapter
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_full_view.*

@Suppress("DEPRECATION")
/**
 * Created by dev on 31/7/18.
 */
class FullViewActivity : BaseActivity() {

    internal var display = ""
    internal var name = ""
    internal var mPic = ""
    var paths = ArrayList<String>()
    private var mFullViewPagerAdapter: FullViewPagerAdapter? = null

    override fun getContentView() = R.layout.activity_full_view

    override fun initUI() {

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llOuterFullView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        imgBack.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtHeading.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        imgBack.setImageResource(R.mipmap.ic_back_b)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llOuterFullView.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBack.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtHeading.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        imgBack.setImageResource(R.mipmap.ic_back_w)
    }

    override fun onCreateStuff() {
        paths = intent.extras!!.getStringArrayList("paths")
        display = intent.extras!!.getString("display")
        name = intent.getStringExtra("name")
        mPic = intent.extras!!.getString("pic")

        if (!paths.contains(display)) {
            Toast.makeText(this, resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show()
        }

        viewPager.offscreenPageLimit = 2

        mFullViewPagerAdapter = FullViewPagerAdapter(applicationContext, paths.size, paths, mWidth, name, mPic)
        viewPager.adapter = mFullViewPagerAdapter
        viewPager.currentItem = paths.indexOf(display)

//        val title = "" + (viewPager.currentItem + 1) + "/" + paths.size
        txtHeading!!.setText(paths.size.toString()+" "+getString(R.string.media))

        viewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(arg0: Int) {
                // TODO Auto-generated method stub
//                val title = "" + (viewPager.currentItem + 1) + "/" + paths.size
//
//                supportActionBar!!.setTitle(title)
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
                // TODO Auto-generated method stub

            }

            override fun onPageScrollStateChanged(arg0: Int) {
                // TODO Auto-generated method stub

            }
        })
    }

    override fun initListener() {
        imgBack.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                moveBack()
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

    override fun onDestroy() {
        unbindDrawables(findViewById(R.id.llOuterFullView))
        System.gc()
        super.onDestroy()
    }

    private fun unbindDrawables(view: View) {
        if (view.background != null) {
            view.background.callback = null
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                unbindDrawables(view.getChildAt(i))
            }
            view.removeAllViews()
        }
    }
}