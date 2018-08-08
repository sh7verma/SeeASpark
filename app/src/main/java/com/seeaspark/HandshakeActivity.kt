package com.seeaspark

import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_handshake.*
import kotlinx.android.synthetic.main.dialog_short_profile.*
import models.SignupModel
import pl.droidsonroids.gif.GifDrawable

class HandshakeActivity : BaseActivity() {

    var mOtherProfileData: SignupModel.ResponseBean? = null
    var userProfileData: SignupModel? = null

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent ));
        return R.layout.activity_handshake
    }

    override fun initUI() {

    }

    override fun displayDayMode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayNightMode() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreateStuff() {

      /*  mOtherProfileData = if (intent.hasExtra("otherProfileData"))
            intent.getParcelableExtra("otherProfileData")
        else
            Gson().fromJson(intent.getStringExtra("matchData"), SignupModel.ResponseBean::class.java);


        userProfileData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        populateData()
*/
        val animation1 = AlphaAnimation(0f, 1f)
        animation1.duration = 1500
        animation1.fillAfter = true

        val existingOriginalDrawable = gifHandshake.drawable as GifDrawable?
        existingOriginalDrawable!!.addAnimationListener {
            if (existingOriginalDrawable.canPause()) {
                existingOriginalDrawable.pause()
                llDataHandshake.visibility = View.VISIBLE
                llDataHandshake.startAnimation(animation1)
            }
        }
    }

    private fun populateData() {
        Picasso.with(this).load(userProfileData!!.response.avatar.avtar_url).into(imgUserMatch1)
        Picasso.with(this).load(mOtherProfileData!!.avatar.avtar_url).into(imgUserMatch2)
    }

    override fun initListener() {
        txtStartChat.setOnClickListener(this)
        txtExplore.setOnClickListener(this)
    }



    override fun getContext() = this

    override fun onClick(view: View) {
        when (view) {
            txtExplore -> {
                moveBack()
            }
            txtStartChat -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
                moveBack()
            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(0, 0)
    }
}