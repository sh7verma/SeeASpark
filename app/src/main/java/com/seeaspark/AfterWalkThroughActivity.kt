package com.seeaspark

import kotlinx.android.synthetic.main.activity_main.*

class AfterWalkThroughActivity : BaseActivity() {

    override fun initUI() {
        txtHello.setText("Rajat Arora")
    }

    override fun onCreateStuff() {
    }

    override fun initListener() {
    }

    override fun getContentView() = R.layout.activity_main

    override fun getContext() = this

}