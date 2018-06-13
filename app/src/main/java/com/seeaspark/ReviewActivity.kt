package com.seeaspark

import android.support.v7.app.AlertDialog
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_review.*
import models.SignupModel

class ReviewActivity : BaseActivity() {

    private var userData: SignupModel? = null

    override fun initUI() {

    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        mUtils!!.setString("profileReview", "yes")
        Picasso.with(mContext).load(userData!!.response.avatar).into(imgAvatarReview)
    }

    override fun initListener() {
        txtLogoutReviewScreen.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_review

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtLogoutReviewScreen -> {
                if (connectedToInternet())
                    alertLogoutDialog()
                else
                    showInternetAlert(txtLogout)
            }
        }
    }

    internal fun alertLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("LOG OUT")
        alertDialog.setMessage("Are you sure you want to Log out?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            moveToSplash()
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }


}