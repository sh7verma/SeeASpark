package com.seeaspark

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_splash.*
import pl.droidsonroids.gif.GifDrawable
import java.io.ByteArrayOutputStream
import java.security.MessageDigest


@Suppress("DEPRECATION")
class SplashActivity : BaseActivity() {

    private lateinit var lowerTranslateAnimation: TranslateAnimation
    private lateinit var upperAlphaAnimation: AlphaAnimation
    private val MOVEBACK: Int = 0

    override fun getContentView() = R.layout.activity_splash

    override fun initUI() {
       gifSplash.setImageResource(R.drawable.logo_animation_white)
        val existingOriginalDrawable = gifSplash.drawable as GifDrawable?
        existingOriginalDrawable!!.addAnimationListener {
            if (existingOriginalDrawable.canPause()) {
                existingOriginalDrawable.pause()
                existingOriginalDrawable.setVisible(false, false)
                rlSplash.isDrawingCacheEnabled = true
                val bitmap = Bitmap.createBitmap(rlSplash.drawingCache)
                rlSplash.isDrawingCacheEnabled = false

                val drawable = BitmapDrawable(bitmap) as Drawable
                window.setBackgroundDrawable(drawable)

                if (mUtils!!.getString("profileReview", "").equals("yes")) {
                    presentActivity(ReviewActivity(), bitmap)
                } else {
                    if (!TextUtils.isEmpty(mUtils!!.getString("access_token", ""))
                            && mUtils!!.getInt("profile_status", 0) == 2) {
                        /// email verified.
                        presentActivity(LandingActivity(), bitmap)
                    } else {
                        /// not verified or new user
                        presentActivity(WalkthroughActivity(), bitmap)
                    }
                }
            }
        }
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
        } catch (e: Exception) {

        }
    }

    override fun initListener() {
    }

    override fun getContext() = this

    override fun onClick(p0: View?) {
    }

    @Suppress("DEPRECATION")
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
                rlSplash.isDrawingCacheEnabled = true
                val bitmap = Bitmap.createBitmap(rlSplash.drawingCache)
                rlSplash.isDrawingCacheEnabled = false

                val drawable = BitmapDrawable(bitmap) as Drawable
                window.setBackgroundDrawable(drawable)

                if (mUtils!!.getString("profileReview", "").equals("yes")) {
                    presentActivity(ReviewActivity(), bitmap)
                } else {
                    if (!TextUtils.isEmpty(mUtils!!.getString("access_token", ""))
                            && mUtils!!.getInt("profile_status", 0) == 2) {
                        /// email verified.
                        presentActivity(LandingActivity(), bitmap)
                    } else {
                        /// not verified or new user
                        presentActivity(WalkthroughActivity(), bitmap)
                    }
                }
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
    }

    fun presentActivity(activity: AppCompatActivity, bitmap: Bitmap) {
        Handler().postDelayed({
            val bStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream)
            val byteArray = bStream.toByteArray()

            val intentSecond = Intent(this@SplashActivity, activity::class.java)
            intentSecond.putExtra("image", byteArray);
            startActivity(intentSecond)
            finish()
            overridePendingTransition(0, 0)
        }, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == MOVEBACK) {
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}