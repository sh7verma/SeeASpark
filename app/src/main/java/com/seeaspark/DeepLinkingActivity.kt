package com.seeaspark

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.R.attr.data
import android.content.Intent
import android.net.Uri
import android.util.Log


class DeepLinkingActivity : BaseActivity() {
    override fun getContentView() = R.layout.activity_deeplinking

    override fun initUI() {
    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    override fun onCreateStuff() {
        val data = intent.dataString
        if (!TextUtils.isEmpty(data)) {
            val uri = Uri.parse(data)
            val args = uri.queryParameterNames
            Log.e("Arguments = ", args.toString())
            val id = uri.getQueryParameter("id")
            val filename = uri.getQueryParameter("filename")
            Log.e("Id = ", id)
            Log.e("filename = ", filename)

            if (mUtils!!.getString("access_token", "").isNotEmpty()) {
                val intent = Intent(mContext!!, NotesActivity::class.java)
                intent.putExtra("noteId", id)
                intent.putExtra("noteFileName", filename)
                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)
            } else {
                val intent = Intent(mContext!!, SplashActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }

    override fun initListener() {
    }

    override fun getContext() = this

    override fun onClick(p0: View?) {
    }
}