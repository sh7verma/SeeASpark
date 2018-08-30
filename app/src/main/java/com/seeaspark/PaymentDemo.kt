package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*
import kotlinx.android.synthetic.main.activity_payment_demo.*
import utils.GooglePay


class PaymentDemo : BaseActivity() {

    private var mPaymentsClient: PaymentsClient? = null
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 42

    override fun getContentView() = R.layout.activity_payment_demo

    override fun initUI() {
    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateStuff() {
        mPaymentsClient = Wallet.getPaymentsClient(
                this,
                Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .build())

        possiblyShowGooglePayButton()
    }

    override fun initListener() {
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun possiblyShowGooglePayButton() {
        val isReadyToPayJson = GooglePay.getIsReadyToPayRequest()
        if (!isReadyToPayJson.isPresent) {
            return
        }
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString()) ?: return
        val task = mPaymentsClient!!.isReadyToPay(request)
        task.addOnCompleteListener(object : OnCompleteListener<Boolean> {
            override fun onComplete(p0: Task<Boolean>) {
                try {
                    val result = task.getResult(ApiException::class.java)
                    if (result) {
                        // show Google as a payment option
                        mGooglePayButton!!.setOnClickListener(
                                { view -> requestPayment(view) })
                        mGooglePayButton!!.visibility = View.VISIBLE
                    }
                } catch (exception: ApiException) {
                    // handle developer errors
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestPayment(view: View) {
        val paymentDataRequestJson = GooglePay.getPaymentDataRequest()
        if (!paymentDataRequestJson.isPresent) {
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString())
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient!!.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
        // value passed in AutoResolveHelper
            LOAD_PAYMENT_DATA_REQUEST_CODE -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val paymentData = PaymentData.getFromIntent(data)
                    val json = paymentData!!.toJson()
                }
                Activity.RESULT_CANCELED -> {
                }
                AutoResolveHelper.RESULT_ERROR -> {
                    val status = AutoResolveHelper.getStatusFromIntent(data)
                }
            }
        }
    }

}