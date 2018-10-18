package com.seeaspark

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_review.*
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class ReviewActivity : BaseActivity() {
    private var userData: SignupModel? = null
    private var isSwitched = false
    private var mQuotesArrayList = ArrayList<String>()
    private var mQuotesAuthorArrayList = ArrayList<String>()
    private var count = 1
    private var countAuthor = 1
    private var reviewActivity: ReviewActivity? = null

    private var displayTime: Long = 8500

    override fun getContentView() = R.layout.activity_review

    override fun initUI() {
        if (intent.hasExtra("image"))
            displaySplashAnimation()
    }

    private fun displaySplashAnimation() {
        val byteArray = intent.getByteArrayExtra("image")
        val bmp: Bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        imgAnimationOverlay.setImageBitmap(bmp)

        val scaleAnimation = ScaleAnimation(1f, 3f, 1f, 3f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        scaleAnimation.duration = 300

        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = 300

        val animatorSet = AnimationSet(true)
        animatorSet.addAnimation(scaleAnimation)
        animatorSet.addAnimation(alphaAnimation)
        imgAnimationOverlay.startAnimation(animatorSet)

        animatorSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                imgAnimationOverlay.alpha = 0f
            }

            override fun onAnimationStart(p0: Animation?) {
            }
        })
    }


    override fun displayDayMode() {

    }

    override fun displayNightMode() {

    }

    override fun onCreateStuff() {

        if (intent.hasExtra("isSwitched")) {
            isSwitched = true
            imgBackReview.visibility = View.VISIBLE
            imgLogoutReview.visibility = View.INVISIBLE
        }

        loadQuotesData()

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        if (!isSwitched && mUtils!!.getString("profileReview", "").isEmpty())
            mUtils!!.setString("profileReview", "yes")

        Picasso.with(mContext).load(userData!!.response.avatar.avtar_url).into(imgAvatarReview)

        if (connectedToInternet()) {
            if (userData!!.response.switch_status == 0)
                hitSubmitAPI()
        } else {
            showInternetAlert(imgBackReview)
        }

        if (mUtils!!.getInt("document_verified", 0) == 2) {
            txtUnderReview.text = getString(R.string.in_review_message)
        }

        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        val typefaceBold = Typeface.createFromAsset(assets, "fonts/bold.otf")

        txtQuote.typeface = typeface
        txtQuoteAuthor.typeface = typefaceBold

        txtQuote.setText(mQuotesArrayList[0])
        txtQuote.show()
        txtQuote.setValueUpdateListener {
            if (reviewActivity != null) {
//                if (reviewActivity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                displayQuotes(it)
//                else
//                    displayQuotesWithAnimation(it)
            }
        }

        txtQuoteAuthor.setText(mQuotesAuthorArrayList[0])
        txtQuoteAuthor.show()
        txtQuoteAuthor.setValueUpdateListener {
            if (reviewActivity != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                displayAuthor(it)
//                else
//                    displayQuotesWithAnimation(it)
            }
        }
    }

    override fun initListener() {
        imgLogoutReview.setOnClickListener(this)
        imgBackReview.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgLogoutReview -> {
                if (connectedToInternet())
                    alertLogoutDialog()
                else
                    showInternetAlert(imgLogoutReview)
            }
            imgBackReview -> {
                if (isSwitched) {
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
                }
            }
        }
    }

    private fun hitSubmitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().submitProfile(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<SignupModel> {

            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                if (response.body().response != null) {
                    dismissLoader()
                    updateProfileDatabase(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(imgBackReview, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(imgBackReview, t!!.localizedMessage)
            }
        })
    }

    private fun loadQuotesData() {
        mQuotesArrayList.add(getString(R.string.quote_1))
        mQuotesArrayList.add(getString(R.string.quote_2))
        mQuotesArrayList.add(getString(R.string.quote_3))
        mQuotesArrayList.add(getString(R.string.quote_4))
        mQuotesArrayList.add(getString(R.string.quote_5))
        mQuotesArrayList.add(getString(R.string.quote_6))
        mQuotesArrayList.add(getString(R.string.quote_7))
        mQuotesArrayList.add(getString(R.string.quote_8))
        mQuotesArrayList.add(getString(R.string.quote_9))
        mQuotesArrayList.add(getString(R.string.quote_10))

        mQuotesAuthorArrayList.add(getString(R.string.quote_owner1))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner2))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner3))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner4))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner5))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner6))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner7))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner8))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner9))
        mQuotesAuthorArrayList.add(getString(R.string.quote_owner10))
    }

    private fun displayQuotes(isVisible: Boolean) {

        if (isVisible) {
            /// turn to hide
            Handler().postDelayed({
                txtQuote.hide()
            }, displayTime)
        } else {
            /// turn to visible
            Handler().postDelayed({
                if (count > 9)
                    count = 0
                txtQuote.setText(mQuotesArrayList[count])
                txtQuote.show()
            }, 100)
            count++
        }
    }

    private fun displayAuthor(isVisible: Boolean) {
        if (isVisible) {
            /// turn to hide
            Handler().postDelayed({
                txtQuoteAuthor.hide()
            }, displayTime)
        } else {
            /// turn to visible
            Handler().postDelayed({
                if (countAuthor > 9)
                    countAuthor = 0
                txtQuoteAuthor.setText(mQuotesAuthorArrayList[countAuthor])
                txtQuoteAuthor.show()
            }, 100)
            countAuthor++
        }
    }

    private fun updateProfileDatabase(response: SignupModel.ResponseBean?) {
        userData!!.response = response
        mUtils!!.setString("userDataLocal", mGson.toJson(userData))
        val broadCastIntent = Intent(Constants.PROFILE_UPDATE)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    override fun onBackPressed() {
        if (isSwitched) {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
        super.onBackPressed()
    }

    override fun onResume() {
        Log.e("onResume", "onResume")
        mUtils!!.setInt("inside_reviewFull", 1)
        if (!isSwitched) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }
        super.onResume()
    }

    override fun onStart() {
        reviewActivity = this
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                IntentFilter(Constants.REVIEW))
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverUnverified,
                IntentFilter(Constants.UNVERIFIED))
        super.onStart()
    }

    override fun onStop() {
        mUtils!!.setInt("inside_reviewFull", 0)
        reviewActivity = null
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverUnverified)
        super.onStop()
    }


    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, data: Intent) {
            val intent: Intent?
            if (data.hasExtra("type")) {
                txtUnderReview.text = data.getStringExtra("displayMessage")
            } else {
                if (isSwitched) {
                    intent = Intent(mContext!!, QuestionnariesActivity::class.java)
                    intent.putExtra("newUserType", Constants.MENTOR)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                } else {
                    intent = Intent(mContext, QuestionnariesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                }
            }
        }
    }

    var receiverUnverified: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            alertUnVerifiedDialog()
        }
    }

    private fun alertUnVerifiedDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("DISAPPROVED")
        alertDialog.setMessage("Admin rejected your application.")
        alertDialog.setPositiveButton("Ok") { dialog, which ->
            if (isSwitched) {
                val broadCastIntent = Intent(Constants.PROFILE_UPDATE)
                broadCastIntent.putExtra("updateAPI", true)
                broadcaster!!.sendBroadcast(broadCastIntent)
                finish()
            } else
                moveToSplash()
        }
        alertDialog.show()
    }


}