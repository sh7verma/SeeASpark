package com.seeaspark

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Window
import utils.Utils

class BoostDialog : Activity() {

    private var mScreenwidth: Int = 0
    private var mScreenheight: Int = 0
    var mUtils: Utils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wmlp = this.window.attributes

        wmlp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        this.setFinishOnTouchOutside(true)
        setContentView(R.layout.dialog_short_profile)
        getDefaults()
        window.setLayout(mScreenwidth, (mScreenheight * 0.75).toInt())

        mUtils = Utils(this)
    }

    private fun getDefaults() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mScreenwidth = dm.widthPixels
        mScreenheight = dm.heightPixels
    }

}