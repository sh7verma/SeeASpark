package com.seeaspark

import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_review.*
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class ReviewActivity : BaseActivity(), ViewTreeObserver.OnGlobalLayoutListener {
    private var userData: SignupModel? = null
    private var isSwitched = false
    private var mQuotesArrayList = ArrayList<String>()
    private var mQuotesAuthorArrayList = ArrayList<String>()
    private var count = 1
    private var countAuthor = 1
    private var reviewActivity: ReviewActivity? = null

    private var revealX: Int = 0
    private var revealY: Int = 0
    private var isAnimationCompleted = false

    override fun getContentView() = R.layout.activity_review

    override fun initUI() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra("EXTRA_CIRCULAR_REVEAL_X") &&
                intent.hasExtra("EXTRA_CIRCULAR_REVEAL_Y")) {
            llMainReview.visibility = View.INVISIBLE

            revealX = intent.getIntExtra("EXTRA_CIRCULAR_REVEAL_X", 0)
            revealY = intent.getIntExtra("EXTRA_CIRCULAR_REVEAL_Y", 0)

            val viewTreeObserver = llMainReview.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener {
                    if (!isAnimationCompleted) {
                        revealActivity(revealX, revealY)
                        llMainReview.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                }
            }
        }
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
                if (reviewActivity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    displayQuotesWithAnimationNougat(it)
                else
                    displayQuotesWithAnimation(it)
            }
        }

        txtQuoteAuthor.setText(mQuotesAuthorArrayList[0])
        txtQuoteAuthor.show()
        txtQuoteAuthor.setValueUpdateListener {
            if (reviewActivity != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    displayQuotesWithAnimationNougatAuthor(it)
                else
                    displayQuotesWithAnimation(it)
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

    private fun revealActivity(x: Int, y: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(llMainReview.getWidth(), llMainReview.getHeight()) * 1.1).toFloat()
            val circularReveal = ViewAnimationUtils.createCircularReveal(llMainReview, x, y, 0f, finalRadius)
            circularReveal.duration = 400
            circularReveal.interpolator = AccelerateInterpolator()

            llMainReview.visibility = View.VISIBLE
            circularReveal.start()
            isAnimationCompleted = true
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

    private fun displayQuotesWithAnimationNougat(isVisible: Boolean) {
        Log.e("Count  = ", count.toString())
        if (isVisible) {
            /// turn to hide
            Handler().postDelayed({
                if (count < 9) {
                    txtQuote.hide()
                }
            }, 3000)
        } else {
            /// turn to visible
            Handler().postDelayed({
                if (count < 10) {
                    txtQuote.setText(mQuotesArrayList[count])
                    txtQuote.show()
                }
            }, 100)
            count++
        }
    }

    private fun displayQuotesWithAnimationNougatAuthor(isVisible: Boolean) {
        Log.e("Count  = ", countAuthor.toString())
        if (isVisible) {
            /// turn to hide
            Handler().postDelayed({
                if (countAuthor < 9) {
                    txtQuoteAuthor.hide()
                }
            }, 3000)
        } else {
            /// turn to visible
            Handler().postDelayed({
                if (countAuthor < 10) {
                    txtQuoteAuthor.setText(mQuotesAuthorArrayList[countAuthor])
                    txtQuoteAuthor.show()
                }
            }, 100)
            countAuthor++
        }
    }

    private fun displayQuotesWithAnimation(isVisible: Boolean) {
        Log.e("Count  = ", count.toString())
        if (count < 10) {
            if (isVisible) {
                /// turn to hide
                Handler().postDelayed({
                    txtQuote.hide()
                    txtQuoteAuthor.hide()
                }, 3000)
            } else {
                /// turn to visible
                txtQuote.setText(mQuotesArrayList[count])
                txtQuote.show()
                txtQuoteAuthor.setText(mQuotesAuthorArrayList[count])
                txtQuoteAuthor.show()
                count++
            }
        }
    }

    private fun updateProfileDatabase(response: SignupModel.ResponseBean?) {
        userData!!.response = response
        mUtils!!.setString("userDataLocal", mGson.toJson(userData))
        val broadCastIntent = Intent(Constants.PROFILE_UPDATE)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isAnimationCompleted) {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
            overridePendingTransition(0, 0)
        }
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
    override fun onGlobalLayout() {

    }
}