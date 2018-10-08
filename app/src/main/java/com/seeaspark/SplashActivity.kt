package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_handshake.*
import kotlinx.android.synthetic.main.activity_splash.*
import pl.droidsonroids.gif.GifDrawable
import utils.Utils
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_splash)

        val mUtils = Utils(this)

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

        gifSplash.setImageResource(R.drawable.splash_caspark)

        val existingOriginalDrawable = gifSplash.drawable as GifDrawable?
        existingOriginalDrawable!!.addAnimationListener {
            if (existingOriginalDrawable.canPause()) {
                existingOriginalDrawable.pause()
                if (mUtils.getString("profileReview", "").equals("yes")) {
                    startActivity(Intent(this@SplashActivity, ReviewActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                } else {
                    if (!TextUtils.isEmpty(mUtils.getString("access_token", "")) &&
                            mUtils.getInt("profile_status", 0) == 2) {
                        /// email verified.
                        startActivity(Intent(this@SplashActivity, LandingActivity::class.java))
                        finish()
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    } else {
                        /// not verified or new user
                        startActivity(Intent(this@SplashActivity, WalkthroughActivity::class.java))
                        finish()
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                }
            }
        }

        /*Handler().postDelayed({
            if (mUtils.getString("profileReview", "").equals("yes")) {
                startActivity(Intent(this@SplashActivity, ReviewActivity::class.java))
                finish()
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            } else {
                if (!TextUtils.isEmpty(mUtils.getString("access_token", "")) &&
                        mUtils.getInt("profile_status", 0) == 2) {
                    /// email verified.
                    startActivity(Intent(this@SplashActivity, LandingActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                } else {
                    /// not verified or new user
                    startActivity(Intent(this@SplashActivity, WalkthroughActivity::class.java))
                    finish()
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                }
            }
        }, 2000)*/
    }
}