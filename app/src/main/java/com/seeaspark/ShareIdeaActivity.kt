package com.seeaspark

import android.graphics.Typeface
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_share_idea.*

class ShareIdeaActivity : BaseActivity() {
    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent))
        return R.layout.activity_share_idea
    }

    override fun initUI() {
        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        edContent.typeface = typeface
        edSubject.typeface = typeface
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llMainIdea.setBackgroundResource(whiteRipple)
        txtEnterIdea.setTextColor(blackColor)
        edSubject.setTextColor(blackColor)
        edContent.setTextColor(blackColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llMainIdea.setBackgroundResource(R.drawable.share_idea_night_mode)
        txtEnterIdea.setTextColor(whiteColor)
        edSubject.setTextColor(whiteColor)
        edContent.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        txtSend.setOnClickListener(this)
        txtCancel.setOnClickListener(this)
    }

    override fun getContext() = this
    override fun onClick(view: View?) {
        when (view) {
            txtCancel -> {
                finish()
            }
            txtSend -> {
                if (edSubject.text.toString().trim().isEmpty())
                    showAlert(txtSend, getString(R.string.error_subject))
                else if (edContent.text.toString().trim().isEmpty())
                    showAlert(txtSend, getString(R.string.error_content))
                else {
                    if (connectedToInternet())
                        hitAPI()
                    else
                        showInternetAlert(txtSend)
                }
            }
        }
    }

    private fun hitAPI() {

    }
}