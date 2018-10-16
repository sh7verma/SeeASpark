package com.seeaspark

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.animation.AlphaAnimation
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_handshake.*
import models.SignupModel
import pl.droidsonroids.gif.GifDrawable
import utils.Constants
import java.util.*

class HandshakeActivity : BaseActivity() {

    var mOtherProfileData: SignupModel.ResponseBean? = null
    var userProfileData: SignupModel? = null

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent));
        return R.layout.activity_handshake
    }

    override fun initUI() {

    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    override fun onCreateStuff() {
        mOtherProfileData = if (intent.hasExtra("otherProfileData"))
            intent.getParcelableExtra("otherProfileData")
        else
            Gson().fromJson(intent.getStringExtra("matchData"),
                    SignupModel.ResponseBean::class.java);

        userProfileData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""),
                SignupModel::class.java)
        populateData()

        val animation1 = AlphaAnimation(0f, 1f)
        animation1.duration = 500
        animation1.fillAfter = true

        val existingOriginalDrawable = gifHandshake.drawable as GifDrawable?
        existingOriginalDrawable!!.addAnimationListener {
            if (existingOriginalDrawable.canPause()) {
                existingOriginalDrawable.pause()
                existingOriginalDrawable.setVisible(false, false)
                llHandshake.visibility = View.GONE
                llDataHandshake.visibility = View.VISIBLE
                llDataHandshake.startAnimation(animation1)
            }
        }
    }

    private fun populateData() {
        gifHandshake.setImageResource(R.drawable.v2)
        if ((userProfileData!!.response.gender.toInt() == Constants.MALE ||
                        userProfileData!!.response.gender.toInt() == Constants.OTHER) &&
                (mOtherProfileData!!.gender.toInt() == Constants.MALE ||
                        mOtherProfileData!!.gender.toInt() == Constants.OTHER)) {
            /// Male + Male
        } else if ((userProfileData!!.response.gender.toInt() == Constants.MALE ||
                        userProfileData!!.response.gender.toInt() == Constants.OTHER) &&
                mOtherProfileData!!.gender.toInt() == Constants.FEMALE) {
            /// Male + Female
        } else if (userProfileData!!.response.gender.toInt() == Constants.FEMALE &&
                (mOtherProfileData!!.gender.toInt() == Constants.MALE ||
                        mOtherProfileData!!.gender.toInt() == Constants.OTHER)) {
            /// Female + Male
        } else if (userProfileData!!.response.gender.toInt() == Constants.FEMALE &&
                mOtherProfileData!!.gender.toInt() == Constants.FEMALE) {
            /// Female + Female
        }
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
                val intent = Intent(this, ConversationActivity::class.java)
                val mParticpantIDSList = ArrayList<String>()

                val otherUserType: String
                if (userProfileData!!.response.user_type == Constants.MENTOR)
                    otherUserType = Constants.MENTEE.toString()
                else
                    otherUserType = Constants.MENTOR.toString()

                mParticpantIDSList.add(mOtherProfileData!!.id.toString() + "_" + otherUserType)
                mParticpantIDSList.add(userProfileData!!.response.id.toString() + "_" + userProfileData!!.response.user_type)
                mParticpantIDSList.sort()
                var mParticpantIDS = mParticpantIDSList.toString()
                mParticpantIDS = mParticpantIDS.replace(" ", "")
                val participants = mParticpantIDS.substring(1, mParticpantIDS.length - 1)
                intent.putExtra("participantIDs", participants)
                startActivity(intent)
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