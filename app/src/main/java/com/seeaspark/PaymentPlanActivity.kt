package com.seeaspark

import android.content.Intent
import android.util.Log
import android.view.View
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_payment_demo.*
import utils.BillingManager
import java.util.ArrayList

class PaymentPlanActivity : BaseActivity(), BillingManager.BillingUpdatesListener {


    private var mBillingManager: BillingManager? = null
    var skuDetailsList = ArrayList<SkuDetails>()

    override fun getContentView() = R.layout.activity_payment_demo

    override fun initUI() {

    }

    override fun displayDayMode() {

    }

    override fun displayNightMode() {

    }

    override fun onCreateStuff() {
        mBillingManager = BillingManager(this, this, ArrayList<String>())

        mGooglePayButton.setOnClickListener {
            mBillingManager!!.initiatePurchaseFlow(skuDetailsList[0].sku)
        }
    }

    override fun initListener() {

    }

    override fun getContext() = this

    override fun onClick(view: View?) {
    }

    override fun onBillingClientSetupFinished() {
    }

    override fun onConsumeFinished(token: String?, result: Int) {
    }

    override fun onPurchasesUpdated(purchases: MutableList<Purchase>) {
        Log.e("Purchase = ", purchases.size.toString())
    }

    override fun productsList(skuDetailsListLocal: ArrayList<SkuDetails>) {
        skuDetailsList.clear()
        skuDetailsList.addAll(skuDetailsListLocal)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("TEst", "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
    }
}