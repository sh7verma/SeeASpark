package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.disclamier_dialog.*
import utils.Utils


class DisclamierDialog : Activity() {

    internal var mScreenwidth: Int = 0
    internal var mScreenheight: Int = 0
    var mUtils: Utils? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wmlp = this.window.attributes

        wmlp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        this.setFinishOnTouchOutside(false)
        setContentView(R.layout.disclamier_dialog)
        getDefaults()
        window.setLayout(mScreenwidth, (mScreenheight * 0.5).toInt())

        mUtils = Utils(this)

        llMainDialogDisclamier.setOnClickListener {
            // no operation
        }

        txtSkip.setOnClickListener {
            mUtils!!.setString("access_token", "123")
            var intent = Intent(this, LandingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }

        txtSettings.setOnClickListener {
            Toast.makeText(this, "work in progress", Toast.LENGTH_LONG).show()
        }
    }

    internal fun getDefaults() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mScreenwidth = dm.widthPixels
        mScreenheight = dm.heightPixels
    }
}