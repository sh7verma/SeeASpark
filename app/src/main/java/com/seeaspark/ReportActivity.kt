package com.seeaspark

import android.graphics.Typeface
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_report.*
import models.BaseSuccessModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

/**
 * Created by dev on 28/8/18.
 */
class ReportActivity : BaseActivity() {

    var mPostType = 3

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent))
        return R.layout.activity_report
    }

    override fun initUI() {
        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        edContent.typeface = typeface
        edSubject.typeface = typeface
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llMainIdea.setBackgroundResource(whiteRipple)
        txtEnterIdea.setTextColor(blackColor)
        edSubject.setTextColor(blackColor)
        edContent.setTextColor(blackColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llMainIdea.setBackgroundResource(R.drawable.share_idea_night_mode)
        txtEnterIdea.setTextColor(whiteColor)
        edSubject.setTextColor(whiteColor)
        edContent.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {
//        if (intent.getStringExtra("path") == "event")
//            mPostType = 2
    }

    override fun initListener() {
        imgCancelIdea.setOnClickListener(this)
        imgDoneIdea.setOnClickListener(this)
    }

    override fun getContext() = this
    override fun onClick(view: View?) {
        when (view) {
            imgCancelIdea -> {
                Constants.closeKeyboard(mContext!!, imgCancelIdea)
                finish()
            }
            imgDoneIdea -> {
                if (edSubject.text.toString().trim().isEmpty()) {
                    val toast = Toast.makeText(this, getString(R.string.error_subject), Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else if (edContent.text.toString().trim().isEmpty()) {
                    val toast = Toast.makeText(this, getString(R.string.error_content), Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else {
                    if (connectedToInternet())
                        hitAPI()
                    else
                        showInternetAlert(imgDoneIdea)
                }
            }
        }
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().shareAnIdea(mUtils!!.getString("access_token", ""),
                edSubject.text.toString().trim(), edContent.text.toString().trim(), mPostType)
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    showToast(mContext!!, response.body().response.message)
                    finish()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(llMainIdea, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                dismissLoader()
            }

        })

    }
}