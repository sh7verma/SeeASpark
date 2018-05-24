package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import utils.Utils


class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var mUtils = Utils(this)

        Handler().postDelayed({
            if (!TextUtils.isEmpty(mUtils.getString("access_token", "")) && mUtils.getBoolean("email_verified", false)) {
                /// email verified.
                startActivity( Intent(this@SplashActivity, LandingActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            } else {
                /// not verified or new user
                startActivity(Intent(this@SplashActivity, AfterWalkThroughActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }
        }, 2000)
    }

}