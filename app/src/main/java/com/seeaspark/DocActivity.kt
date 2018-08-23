package com.seeaspark

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_doc.*

/**
 * Created by dev on 14/8/18.
 */
class DocActivity : BaseActivity() {

    internal var select_path = ""
    internal var name = ""
    internal var url = ""

    override fun getContentView() = R.layout.activity_doc

    override fun initUI() {
        select_path = intent.getStringExtra("select_path")
        name = intent.getStringExtra("name")
        txtName.text = name
        if(select_path.contains("http")){
            url = select_path
        }else{
            url = ""+select_path
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llOuterDoc.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        txtSendDoc.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llOuterDoc.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        txtSendDoc.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        wvDoc.getSettings().setJavaScriptEnabled(true)
        wvDoc.getSettings().setAllowFileAccess(true);
        wvDoc.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
//        wvDoc.setWebViewClient(MyBrowser())
        wvDoc.loadUrl("https://docs.cashfree.com/docs/android/guide/")
    }

    override fun initListener() {
        imgBack.setOnClickListener(this)
//        txtSend.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                moveBack()
            }
//            txtSend -> {
//                sendMessage("")
//            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    internal fun sendMessage(message: String) {
        val intent = Intent()
        intent.putExtra("caption", message)
        intent.putExtra("selected_name", select_path)
        setResult(RESULT_OK, intent)
        finish()
    }


    private inner class MyBrowser : WebViewClient() {

        override fun onLoadResource(view: WebView, url: String) {

            super.onLoadResource(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            super.onPageStarted(view, url, favicon)
            pbHeaderProgress.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            pbHeaderProgress.visibility = View.GONE
        }
    }

}