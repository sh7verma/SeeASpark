package com.seeaspark

import adapters.FullImageAdapter
import android.content.Intent
import android.os.Build
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.activity_fullviewimage.*
import models.PostModel
import utils.Constants

class FullViewImageActivity : BaseActivity() {

    var mImagesArrayList = ArrayList<PostModel.ResponseBean.ImagesBean>()

    override fun getContentView() = R.layout.activity_fullviewimage

    override fun initUI() {

    }

    override fun displayDayMode() {

    }

    override fun displayNightMode() {
    }

    override fun onCreateStuff() {
        mImagesArrayList.addAll(intent.getParcelableArrayListExtra("images"))
        vpImages.adapter = FullImageAdapter(mContext!!, mImagesArrayList, 2)
        vpImages.currentItem = intent.getIntExtra("imagePosition", 0)

        vpImages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                val broadCastIntent = Intent(Constants.POST_BROADCAST)
                broadCastIntent.putExtra("status", Constants.CHANGE_POSITION)
                broadCastIntent.putExtra("position", position)
                broadcaster!!.sendBroadcast(broadCastIntent)
            }
        })

    }

    override fun initListener() {
        txtDone.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtDone -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    supportFinishAfterTransition()
                } else {
                    finish()
                }
            }
        }
    }
}