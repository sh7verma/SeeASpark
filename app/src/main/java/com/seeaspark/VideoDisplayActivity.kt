package com.seeaspark

import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.View
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_video_display.*

/**
 * Created by dev on 31/7/18.
 */
class VideoDisplayActivity : BaseActivity() {

    internal var path = ""

    override fun getContentView() = R.layout.activity_video_display

    override fun initUI() {
        path = intent.extras!!.getString("path")
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llOuterVideoDisplay.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llOuterVideoDisplay.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
    }

    override fun onCreateStuff() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(MediaController(this))
        handler.sendEmptyMessage(1)

        ViewCompat.setTransitionName(videoView, "VideoView")
    }

    override fun initListener() {
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    internal var handler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {

            val pos = msg.what
            if (pos == 1) {

                videoView.setVideoPath(path)
                videoView.requestFocus()
                videoView.start()

                Log.d("Before Video Finish", "i m in before video finish")
                videoView.setOnCompletionListener(MediaPlayer.OnCompletionListener { onBackPressed() })
            }
        }
    }
}