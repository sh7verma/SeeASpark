package com.seeaspark

import android.view.View
import kotlinx.android.synthetic.main.activity_preferences.*


class PreferencesActivity : BaseActivity() {

    override fun initUI() {

    }

    override fun onCreateStuff() {
    }

    override fun initListener() {
        imgForwardPrefer.setOnClickListener(this)
        txtGenderPrefer.setOnClickListener(this)

    }

    override fun getContentView() = R.layout.activity_preferences

    override fun getContext() = this

    override fun onClick(view: View?) {
        when(view){

        }
    }
}