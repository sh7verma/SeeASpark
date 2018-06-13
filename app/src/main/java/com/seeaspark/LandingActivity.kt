package com.seeaspark

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.view.View
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : BaseActivity() {


    override fun initUI() {

    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        txtLogout.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_landing

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtLogout -> {
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