package com.seeaspark

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Window
import kotlinx.android.synthetic.main.dialog_boost.*
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
        setContentView(R.layout.dialog_boost)
        getDefaults()
        window.setLayout(mScreenwidth, (mScreenheight))

        mUtils = Utils(this)

        txtBuyNow.setOnClickListener {
            finish()
            overridePendingTransition(0, R.anim.slidedown_out)
        }
        txtNoThanks.setOnClickListener {
            finish()
            overridePendingTransition(0, R.anim.slidedown_out)
        }

        llOuterBoost.setOnClickListener {
            finish()
            overridePendingTransition(0, R.anim.slidedown_out)
        }
    }

    private fun getDefaults() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mScreenwidth = dm.widthPixels
        mScreenheight = dm.heightPixels
    }

}