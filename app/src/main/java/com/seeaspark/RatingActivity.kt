package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_rating.*
import kotlinx.android.synthetic.main.activity_report.*
import models.BaseSuccessModel
import models.RatingModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

/**
 * Created by dev on 29/8/18.
 */
class RatingActivity : BaseActivity() {

    var mOpponentUserId = ""
    var status = ""
    var chatDialogId = ""
    var userName = ""
    internal var mFirebaseConfigChats = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS)

    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent))
        return R.layout.activity_rating
    }

    override fun initUI() {
        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        edComment.typeface = typeface
    }

    override fun displayDayMode() {

    }

    override fun displayNightMode() {

    }

    override fun onCreateStuff() {
        mOpponentUserId = intent.getStringExtra("user_id")
        status = intent.getStringExtra("status")
        chatDialogId = intent.getStringExtra("chatDialogId")
        userName = intent.getStringExtra("user_name")

        txtRate.text = getString(R.string.rate) + " " + userName + "!"

        if (status.equals("1")) {
            getRatingAPI()
            txtDone.visibility = View.GONE
        }else{
            txtDone.visibility = View.VISIBLE
        }
    }

    override fun initListener() {
        txtDone.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtDone -> {
                if (ratingBar.getRating() <= 0) {
                    val toast = Toast.makeText(this, getString(R.string.please_rate), Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else if (edComment.text.toString().trim().isEmpty()) {
                    val toast = Toast.makeText(this, getString(R.string.error_desc), Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                } else {
                    if (connectedToInternet())
                        hitAPI()
                    else
                        showInternetAlert(imgDoneIdea)
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, 0)
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().rateUser(mUtils!!.getString("access_token", ""),
                mOpponentUserId, ratingBar.getRating().toString(), edComment.text.toString().trim())
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                if (response.body().response != null) {
                    mFirebaseConfigChats.child(chatDialogId).child("rating").child(mUtils!!.getString("user_id", "")).setValue("1").addOnSuccessListener {
                        dismissLoader()
                        showToast(mContext!!, response.body().response.message)
                        finish()
                    }
                } else {
                    dismissLoader()
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(llMainIdea, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                dismissLoader()
            }

        })
    }

    private fun getRatingAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().getRating(mUtils!!.getString("access_token", ""), mOpponentUserId)
        call.enqueue(object : Callback<RatingModel> {
            override fun onResponse(call: Call<RatingModel>?, response: Response<RatingModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    edComment.setText(response.body().response.comment)
                    ratingBar.rating = response.body().response.rating.toFloat()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(llMainIdea, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<RatingModel>?, t: Throwable?) {
                dismissLoader()
            }

        })
    }

}