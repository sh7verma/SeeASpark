package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Window
import kotlinx.android.synthetic.main.dialog_profile_review.*

class ProfileReviewDialog : Activity() {

    internal var mScreenwidth: Int = 0
    internal var mScreenheight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wmlp = this.window.attributes

        wmlp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        this.setFinishOnTouchOutside(false)
        setContentView(R.layout.dialog_profile_review)
        getDefaults()
        window.setLayout(mScreenwidth, (mScreenheight * 0.6).toInt())

        txtReady.setOnClickListener {
            var intent = Intent(this, QuestionnariesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }
    }

    internal fun getDefaults() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mScreenwidth = dm.widthPixels
        mScreenheight = dm.heightPixels
    }
}