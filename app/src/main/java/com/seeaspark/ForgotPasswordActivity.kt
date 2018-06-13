package com.seeaspark

import android.graphics.Typeface
import android.util.Patterns
import android.view.View
import kotlinx.android.synthetic.main.activity_forgot_password.*
import models.ForgotPasswordModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class ForgotPasswordActivity : BaseActivity() {

    override fun initUI() {
        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        edEmailForgot.setTypeface(typeface)
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        imgBackForgot.setOnClickListener(this)
        txtReset.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_forgot_password

    override fun getContext() = this

    override fun onClick(view: View) {
        when (view) {
            txtReset -> {
                verifyDetails()
            }
            imgBackForgot -> {
                moveBack()
            }
        }
    }

    private fun verifyDetails() {
        if (edEmailForgot.getText().toString().trim({ it <= ' ' }).isEmpty()) {
            showAlert(txtReset, resources.getString(R.string.enter_email))
        } else if (!validateEmail(edEmailForgot.getText())) {
            showAlert(txtReset, resources.getString(R.string.enter_valid_email))
        } else {
            if (connectedToInternet()) {
                Constants.closeKeyboard(this, txtReset)
                hitAPI()
            } else {
                showInternetAlert(txtReset)
            }
        }
    }

    private fun moveBack() {
        Constants.closeKeyboard(mContext, imgBackForgot)
        finish()
        overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out)
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().forgotPassword(edEmailForgot.text.toString().trim())
        call.enqueue(object : Callback<ForgotPasswordModel> {

            override fun onResponse(call: Call<ForgotPasswordModel>?, response: Response<ForgotPasswordModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    showToast(mContext!!, response.body().response.message!!)
                    finish()
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
                } else {
                    showAlert(txtReset, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<ForgotPasswordModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtReset, t!!.localizedMessage)
            }
        })
    }

    internal fun validateEmail(text: CharSequence): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val matcher = pattern.matcher(text)
        return matcher.matches()
    }

}