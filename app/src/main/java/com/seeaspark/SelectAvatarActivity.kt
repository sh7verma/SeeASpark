package com.seeaspark

import adapters.SelectAvatarAdapter
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_select_avatar.*
import models.AvatarModel
import models.SignupModel
import utils.Constants

class SelectAvatarActivity : BaseActivity() {


    var mAvatarAdapter: SelectAvatarAdapter? = null
    var mAvatarArray = ArrayList<AvatarModel>()
    private var mSelectAvatar: SelectAvatarActivity? = null
    var mAvatarURL = Constants.EMPTY
    var mAvatarName = Constants.EMPTY
    private var userData: SignupModel? = null
    private var mGender: Int = 0

    override fun getContentView() = R.layout.activity_select_avatar

    override fun initUI() {

    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }
    override fun onCreateStuff() {

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        mSelectAvatar = this
        mGender = intent.getIntExtra("gender", 0)
        if (mGender != 3) {
            for (avatar: AvatarModel in userData!!.avatars) {
                if (avatar.gender == mGender)
                    mAvatarArray.add(avatar)
            }
        } else
            mAvatarArray.addAll(userData!!.avatars)

        mAvatarURL = intent.getStringExtra("avatarURL")

        rvAvatarSelect.layoutManager = GridLayoutManager(this, 3)
        mAvatarAdapter = SelectAvatarAdapter(mAvatarArray, this, mSelectAvatar!!)
        rvAvatarSelect.adapter = mAvatarAdapter
    }

    override fun initListener() {
        imgBackAvatarSelect.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackAvatarSelect -> {
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
            }
        }
    }

    fun moveToNext() {
        val intent = Intent()
        intent.putExtra("avatarURL", mAvatarURL)
        intent.putExtra("avatarName", mAvatarName)
        setResult(Activity.RESULT_OK, intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}