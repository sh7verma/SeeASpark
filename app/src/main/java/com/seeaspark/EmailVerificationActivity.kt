package com.seeaspark

import android.view.View
import kotlinx.android.synthetic.main.activity_email_verification.*


class EmailVerificationActivity : BaseActivity() {

    override fun initUI() {

    }

    override fun onCreateStuff() {
    }

    override fun initListener() {
        txtDoneEmail.setOnClickListener(this)
        txtLoginEmail.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_email_verification

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtDoneEmail -> {

            }
            txtLoginEmail -> {

            }
        }
    }
}