package com.seeaspark

import android.view.View
import kotlinx.android.synthetic.main.activity_tell_a_friend.*
import models.SignupModel

class TellAFriendActivity : BaseActivity() {

    private var userData: SignupModel? = null

    override fun getContentView() = R.layout.activity_tell_a_friend

    override fun initUI() {
    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        txtNameFriend.text = userData!!.response.full_name
    }

    override fun initListener() {
        imgBackTell.setOnClickListener(this)
        llFacebook.setOnClickListener(this)
        llwhatsapp.setOnClickListener(this)
        llEmail.setOnClickListener(this)
        llMessages.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackTell -> {
                moveBack()
            }
            llFacebook -> {

            }
            llwhatsapp -> {

            }
            llEmail -> {

            }
            llMessages -> {

            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}