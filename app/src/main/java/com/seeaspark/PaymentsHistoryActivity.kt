package com.seeaspark

import adapters.PaymentsHistoryAdapter
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_payments_history.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_boost.*
import models.PaymentsHistoryModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.BillingManager
import utils.Constants

class PaymentsHistoryActivity : BaseActivity() {

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mPaymentAdapter: PaymentsHistoryAdapter

    override fun getContentView() = R.layout.activity_payments_history

    override fun initUI() {
        txtTitleCustom.text = getString(R.string.payment_history)
        mLayoutManager = LinearLayoutManager(mContext)
        rvPaymentsHistory.layoutManager = mLayoutManager
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        imgBackCustom.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        imgOption1Custom.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        rvPaymentsHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.background))
        txtNoPayments.setTextColor(blackColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBackCustom.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        imgOption1Custom.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        rvPaymentsHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        txtNoPayments.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {
        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(rvPaymentsHistory)
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackCustom -> {
                moveBack()
            }
        }
    }

    private fun hitAPI() {
        showLoader()
        RetrofitClient.getInstance().getPaymentHistory(mUtils!!.getString("access_token", ""))
                .enqueue(object : Callback<PaymentsHistoryModel> {
                    override fun onFailure(call: Call<PaymentsHistoryModel>?, t: Throwable?) {
                        dismissLoader()
                    }

                    override fun onResponse(call: Call<PaymentsHistoryModel>?, response: Response<PaymentsHistoryModel>) {
                        dismissLoader()
                        if (response.body().error != null) {
                            if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                                Toast.makeText(mContext, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                                moveToSplash()
                            } else
                                showAlert(llPlans, response.body().error!!.message!!)
                        } else {
                            if (response.body().response.isNotEmpty()) {
                                mPaymentAdapter = PaymentsHistoryAdapter(response.body().response, mContext!!)
                                rvPaymentsHistory.adapter = mPaymentAdapter
                            } else {
                                txtNoPayments.visibility = View.VISIBLE
                            }
                        }
                    }
                })
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}