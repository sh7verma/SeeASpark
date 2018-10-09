package com.seeaspark

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : BaseActivity() {

    private lateinit var lowerTranslateAnimation: TranslateAnimation
    private lateinit var upperAlphaAnimation: AlphaAnimation
    private val MOVEBACK: Int = 0

    override fun getContentView() = R.layout.activity_splash

    override fun initUI() {
        animateSplash()
    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    @SuppressLint("PackageManagerGetSignatures")
    override fun onCreateStuff() {
        Fabric.with(this, Crashlytics())
        try {
            val info = packageManager.getPackageInfo(
                    "com.seeaspark", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        }  catch (e: Exception) {

        }
    }

    override fun initListener() {
    }

    override fun getContext() = this

    override fun onClick(p0: View?) {
    }

    private fun animateSplash() {
        lowerTranslateAnimation = TranslateAnimation(0f, 0f, mHeight.toFloat(), 0f)
        lowerTranslateAnimation.duration = 800
        lowerTranslateAnimation.fillAfter = true

        upperAlphaAnimation = AlphaAnimation(0f, 1f)
        upperAlphaAnimation.duration = 800
        upperAlphaAnimation.startOffset = 800
        upperAlphaAnimation.fillAfter = true

        imgLowerPart.startAnimation(lowerTranslateAnimation)
        imgUpperPart.startAnimation(upperAlphaAnimation)

        upperAlphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                if (mUtils!!.getString("profileReview", "").equals("yes")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        presentActivity(ReviewActivity(), false)
                    else {
                        startActivity(Intent(this@SplashActivity, ReviewActivity::class.java))
                        finish()
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                } else {
                    if (!TextUtils.isEmpty(mUtils!!.getString("access_token", "")) &&
                            mUtils!!.getInt("profile_status", 0) == 2) {
                        /// email verified.
                        startActivity(Intent(this@SplashActivity, LandingActivity::class.java))
                        finish()
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    } else {
                        /// not verified or new user
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            presentActivity(WalkthroughActivity(), false)
                        else {
                            startActivity(Intent(this@SplashActivity, WalkthroughActivity::class.java))
                            finish()
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                        }
                    }
                }
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
    }

    fun presentActivity(activity: AppCompatActivity, finish: Boolean) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imgUpperPart,
                "transition")
        val revealX = (imgUpperPart.x + imgUpperPart.width / 2).toInt()
        val revealY = (imgUpperPart.y + imgUpperPart.height / 2).toInt()

        val intent = Intent(mContext, activity::class.java)
        intent.putExtra("EXTRA_CIRCULAR_REVEAL_X", revealX)
        intent.putExtra("EXTRA_CIRCULAR_REVEAL_Y", revealY)
        if (!finish)
            ActivityCompat.startActivityForResult(this, intent, MOVEBACK, options.toBundle())
        else {
            ActivityCompat.startActivity(this, intent, options.toBundle())
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == MOVEBACK) {
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}