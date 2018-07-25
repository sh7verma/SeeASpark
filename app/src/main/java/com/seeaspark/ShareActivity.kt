package com.seeaspark

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_share.*
import models.NotesListingModel
import com.facebook.share.widget.ShareDialog
import com.facebook.CallbackManager
import com.facebook.share.model.ShareLinkContent
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.NotesModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants


class ShareActivity : BaseActivity() {

    private var mCallbackManager: CallbackManager? = null
    private var mShareDialog: ShareDialog? = null

    private var mNotesData: NotesListingModel.ResponseBean? = null

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent));
        return R.layout.activity_share
    }

    override fun initUI() {
    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    override fun onCreateStuff() {
        mNotesData = intent.getParcelableExtra("notesData")

        mCallbackManager = CallbackManager.Factory.create();
        mShareDialog = ShareDialog(this);

        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(llShareOptions)

    }

    override fun initListener() {
        imgWhatsapp.setOnClickListener(this)
        imgFb.setOnClickListener(this)
        imgSMS.setOnClickListener(this)
        imgEmail.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgWhatsapp -> {

            }
            imgFb -> {
                val linkContent = ShareLinkContent.Builder()
                        .setQuote("See A Spark")
                        .setContentUrl(Uri.parse(mNotesData!!.url))
                        .build()
                mShareDialog!!.show(linkContent)
            }
            imgSMS -> {
                val smsUri = Uri.parse("smsto:" + "")
                val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
                smsIntent.putExtra("sms_body", mNotesData!!.url)
                startActivity(smsIntent)
            }
            imgEmail -> {

            }
        }
    }

    private fun hitAPI() {
        val call = RetrofitClient.getInstance().shareNote(mUtils!!.getString("access_token", ""),
                mNotesData!!.id.toString(), mNotesData!!.name)
        call.enqueue(object : Callback<NotesModel> {
            override fun onResponse(call: Call<NotesModel>?, response: Response<NotesModel>?) {
                if (response!!.body().response != null) {
                    llShareOptions.visibility = View.VISIBLE
                    pbShare.visibility = View.GONE
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else {
                        showToast(mContext!!, response.body().error!!.message!!)
                        finish()
                    }
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<NotesModel>?, t: Throwable?) {
                showAlert(llUnderline, t!!.localizedMessage)
                dismissLoader()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

}