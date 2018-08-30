package com.seeaspark

import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_broadcast.*

class BroadcastActivity : BaseActivity() {
    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent));
        return R.layout.activity_broadcast
    }

    override fun initUI() {

    }

    override fun displayDayMode() {

    }

    override fun displayNightMode() {

    }

    override fun onCreateStuff() {
        txtTitle.text = intent.getStringExtra("broadcastTitle")
        txtMessage.text = intent.getStringExtra("broadcastMessage")
    }

    override fun initListener() {
        txtOK.setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }
    }

    override fun getContext() = this
    override fun onClick(p0: View?) {

    }
}