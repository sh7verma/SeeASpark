package com.seeaspark

import adapters.FullImageAdapter
import android.view.View
import kotlinx.android.synthetic.main.activity_fullviewimage.*
import models.PostModel

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
    }

    override fun initListener() {
        txtDone.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtDone -> {
                finish()
            }
        }
    }
}