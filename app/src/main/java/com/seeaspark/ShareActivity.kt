package com.seeaspark

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.activity_signup.*
import models.NotesListingModel
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
    private var mSharedURL = Constants.EMAIL
    private var mPath = 0

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent))
        return R.layout.activity_share
    }

    override fun initUI() {
    }

    override fun displayDayMode() {
        llInnerShare.setBackgroundResource(R.drawable.white_short_profile_background)
        txtShare.setTextColor(blackColor)
    }

    override fun displayNightMode() {
        llInnerShare.setBackgroundResource(R.drawable.dark_short_profile_background)
        txtShare.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {

        mCallbackManager = CallbackManager.Factory.create()
        mShareDialog = ShareDialog(this)

        mPath = intent.getIntExtra("path", 0)
        when (mPath) {
            1 -> txtShare.text = getString(R.string.share_profile)
            2 -> txtShare.text = getString(R.string.share_note)
            3 -> txtShare.text = getString(R.string.share_event)
            4 -> txtShare.text = getString(R.string.share_community)
        }

        if (intent.hasExtra("notesData")) {
            mNotesData = intent.getParcelableExtra("notesData")
            mSharedURL = mSharedURL
            if (connectedToInternet())
                hitAPI()
            else
                showInternetAlert(llShareOptions)
        }

        if (intent.hasExtra("postUrl")) {
            mSharedURL = intent.getStringExtra("postUrl")
            llShareOptions.visibility = View.VISIBLE
            pbShare.visibility = View.GONE
        }
    }

    override fun initListener() {
        llWasteShare.setOnClickListener(this)
        imgWhatsapp.setOnClickListener(this)
        imgFb.setOnClickListener(this)
        imgSMS.setOnClickListener(this)
        imgEmail.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            llWasteShare -> {
                finish()
                overridePendingTransition(0, 0)
            }
            imgWhatsapp -> {
                if (appInstalledOrNot("com.whatsapp")) {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Start Exploring the New Age of Education. Download and Scout on See A Spark $mSharedURL")
                    sendIntent.type = "text/plain"
                    sendIntent.`package` = "com.whatsapp"
                    startActivity(sendIntent)
                } else
                    showToast(mContext!!, "Please install Whatsapp")
            }
            imgFb -> {
                val linkContent = ShareLinkContent.Builder()
                        .setQuote("Start Exploring the New Age of Education. Download and Scout on See A Spark")
                        .setContentUrl(Uri.parse(mSharedURL))
                        .build()
                mShareDialog!!.show(linkContent)
            }
            imgSMS -> {
                val smsUri = Uri.parse("smsto:" + "")
                val smsIntent = Intent(Intent.ACTION_SENDTO, smsUri)
                smsIntent.putExtra("sms_body", "Start Exploring the New Age of Education. Download and Scout on See A Spark $mSharedURL")
                startActivity(smsIntent)
            }
            imgEmail -> {
                try {
                    val email = Intent(Intent.ACTION_SEND)
                    email.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                    email.putExtra(Intent.EXTRA_TEXT, "Start Exploring the New Age of Education. Download and Scout on See A Spark $mSharedURL")
                    email.type = "message/rfc822"
                    startActivity(email)
                } catch (e: Exception) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
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
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
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
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }

        return false
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

}