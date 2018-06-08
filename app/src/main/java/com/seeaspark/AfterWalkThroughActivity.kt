package com.seeaspark

import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.activity_afterwalkthrough.*


class AfterWalkThroughActivity : BaseActivity() {

    override fun initUI() {

    }

    override fun onCreateStuff() {


    }

    override fun initListener() {
        txtMentee.setOnClickListener(this)
        txtMentor.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_afterwalkthrough

    override fun getContext() = this

    override fun onClick(view: View) {

        when (view) {
            txtMentee -> {
                startActivity(Intent(this, LoginSignupActivity::class.java).putExtra("userType", 1))
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }
            txtMentor -> {
                startActivity(Intent(this, LoginSignupActivity::class.java).putExtra("userType", 0))
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }
        }
    }

}