package com.seeaspark

import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import kotlinx.android.synthetic.main.activity_handshake.*
import pl.droidsonroids.gif.GifDrawable

class HandshakeActivity : BaseActivity() {

    override fun initUI() {

    }

    override fun onCreateStuff() {

        val animation1 = AlphaAnimation(0f, 1f)
        animation1.duration = 1500
        animation1.fillAfter = true

        val existingOriginalDrawable = gifHandshake.drawable as GifDrawable?
        existingOriginalDrawable!!.addAnimationListener {
            if (existingOriginalDrawable.canPause()) {
                existingOriginalDrawable.pause()
                llDataHandshake.visibility = View.VISIBLE
                llDataHandshake.startAnimation(animation1)
            }
        }
    }

    override fun initListener() {
        txtStartChat.setOnClickListener(this)
        txtExplore.setOnClickListener(this)
    }

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.transperent));
        return R.layout.activity_handshake
    }

    override fun getContext() = this

    override fun onClick(view: View) {
        when (view) {
            txtExplore -> {
                moveBack()
            }
            txtStartChat -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
                moveBack()
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