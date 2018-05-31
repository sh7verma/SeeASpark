package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import utils.Utils
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var mUtils = Utils(this)

        try {
            val info = packageManager.getPackageInfo(
                    "com.seeaspark",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }


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