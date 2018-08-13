package com.seeaspark

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.BaseSuccessModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants


class ChangePasswordActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_change_password

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {
        txtTitleCustom.text = getString(R.string.change_password)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))

        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")

        edCurrentPassword.typeface = typeface
        edNewPassword.typeface = typeface
        edConfirmPassword.typeface = typeface


        displayDayMode()
        displayNightMode()

    }

    override fun displayNightMode() {
        llMainPassword.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
    }

    override fun displayDayMode() {
        llMainPassword.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        txtChangePassword.setOnClickListener(this)
        imgBackCustom.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtChangePassword -> {
                verifyDetails()
            }
            imgBackCustom -> {
                moveBack()
            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        Constants.closeKeyboard(this, txtChangePassword)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun verifyDetails() {
        if (edCurrentPassword.text.toString().trim({ it <= ' ' }).isEmpty())
            showAlert(txtChangePassword, resources.getString(R.string.error_current_password))
        else if (edCurrentPassword.text.toString().trim { it <= ' ' }.length < 8) {
            showAlert(txtChangePassword, getString(R.string.error_password))
        } else if (edNewPassword.text.toString().trim({ it <= ' ' }).isEmpty())
            showAlert(txtChangePassword, resources.getString(R.string.error_new_password))
        else if (edNewPassword.text.toString().trim { it <= ' ' }.length < 8) {
            showAlert(txtChangePassword, getString(R.string.error_password))
        } else if (edConfirmPassword.text.toString().trim({ it <= ' ' }).isEmpty())
            showAlert(txtChangePassword, resources.getString(R.string.error_confrim_password))
        else if (edConfirmPassword.text.toString().trim({ it <= ' ' }).length < 8) {
            showAlert(txtChangePassword, getString(R.string.error_password))
        } else if (edNewPassword.text.toString().trim { it <= ' ' } != edConfirmPassword.text.toString().trim({ it <= ' ' })) {
            showAlert(txtChangePassword, getString(R.string.password_mismatch))
        } else {
            if (connectedToInternet()) {
                hitAPI()
            } else
                showInternetAlert(txtChangePassword)
        }
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().changePassword(mUtils!!.getString("access_token", ""),
                edCurrentPassword.text.toString().trim(),
                edNewPassword.text.toString().trim())

        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                dismissLoader()
                if (response!!.body().response != null) {
                    Toast.makeText(this@ChangePasswordActivity, response.body().response.message, Toast.LENGTH_SHORT).show()
                    moveBack()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(txtChangePassword, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtChangePassword, t!!.localizedMessage)
            }

        })
    }

}