package com.seeaspark

import android.support.v7.app.AlertDialog
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_review.*

class ReviewActivity : BaseActivity() {

    override fun initUI() {

    }

    override fun onCreateStuff() {
        Picasso.with(mContext).load(intent.getStringExtra("avatar")).into(imgAvatarReview)
    }

    override fun initListener() {
        txtLogoutReviewScreen.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_review

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtLogoutReviewScreen -> {
                alertLogoutDialog()
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