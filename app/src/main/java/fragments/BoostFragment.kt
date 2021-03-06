package fragments

import adapters.BoostPlansAdapter
import android.app.Fragment
import android.content.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.LandingActivity
import com.seeaspark.PaymentsHistoryActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_boost.*
import utils.Constants
import utils.Utils
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import models.PaymentAdditionModel
import models.PlansModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.BillingManager
import utils.GridSpacingItemDecoration
import java.lang.Long
import java.util.*


class BoostFragment : Fragment(), View.OnClickListener, BillingManager.BillingUpdatesListener {

    lateinit var mLandingInstance: LandingActivity
    lateinit var itemView: View
    lateinit var mUtils: Utils
    private lateinit var mAdapterBoost: BoostPlansAdapter
    var mPlansArray = ArrayList<PlansModel.Response>()
    private var boostFragment: BoostFragment? = null
    private lateinit var mBillingManager: BillingManager
    private var skuDetailsList = java.util.ArrayList<SkuDetails>()
    private var isPlanBought = false
    private var purchasePlanPosition = -1
    var isBuyEnable = true

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_boost, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        boostFragment = this
        mLandingInstance = activity as LandingActivity
        mUtils = mLandingInstance.mUtils!!
        initUI()
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun initUI() {
        if (mUtils.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()

        rvBoostPlans.layoutManager = GridLayoutManager(activity, 2)

        val drawable = txtMyPayments.background as GradientDrawable
        drawable.cornerRadius = Constants.dpToPx(32).toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayDayMode() {
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
    }

    private fun onCreateStuff() {
        mAdapterBoost = BoostPlansAdapter(mPlansArray, boostFragment)
        rvBoostPlans.adapter = mAdapterBoost

        if (mLandingInstance.connectedToInternet())
            hitPlansApi()
        else
            mLandingInstance.showInternetAlert(llPlans)
    }

    private fun initListener() {
        txtMyPayments.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            txtMyPayments -> {
                if (mLandingInstance.connectedToInternet()) {
                    val paymentIntent = Intent(activity, PaymentsHistoryActivity::class.java)
                    startActivity(paymentIntent)
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                }
            }
        }
    }

    private fun hitPlansApi() {
        pbPlans.visibility = View.VISIBLE
        RetrofitClient.getInstance().getPlans(mLandingInstance.mUtils!!.getString("access_token", ""),
                "Boost").enqueue(object : Callback<PlansModel> {
            override fun onResponse(call: Call<PlansModel>?, response: Response<PlansModel>) {
                if (response.body().error != null) {
                    pbPlans.visibility = View.GONE
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(activity, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        mLandingInstance.moveToSplash()
                    } else
                        mLandingInstance.showAlert(llPlans, response.body().error!!.message!!)
                } else {
                    mPlansArray.clear()
                    val planIdsArray = ArrayList<String>()
                    for (planData in response.body().response) {
                        planIdsArray.add(planData.plan_id)
                        mPlansArray.add(planData)

                        if (planData.is_expired == 0)
                            isBuyEnable = false
                    }
                    mBillingManager = BillingManager(activity, this@BoostFragment,
                            planIdsArray)
                }
            }

            override fun onFailure(call: Call<PlansModel>?, t: Throwable?) {
                pbPlans.visibility = View.GONE
            }
        })
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(nightModeReceiver,
                IntentFilter(Constants.NIGHT_MODE))
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(nightModeReceiver)
        super.onDestroy()
    }

    fun buyPlan(position: Int) {
        if (mLandingInstance.connectedToInternet()) {
            if (isBuyEnable) {
                purchasePlanPosition = position
                isPlanBought = true
                mBillingManager.initiatePurchaseFlow(skuDetailsList[position].sku)
            } else {
                mLandingInstance.showToast(activity, getString(R.string.plan_already_bought))
            }
        } else {
            mLandingInstance.showInternetAlert(llPlans)
        }
    }

    private var nightModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getIntExtra("status", 0) == Constants.DAY) {
                displayDayMode()
            } else {
                displayNightMode()
            }
        }
    }

    override fun onBillingClientSetupFinished() {
    }

    override fun onConsumeFinished(token: String?, result: Int) {
    }

    override fun onPurchasesUpdated(purchases: MutableList<Purchase>) {
        if (mLandingInstance.connectedToInternet()) {
            if (boostFragment != null && purchases.isNotEmpty()) {
                val date = Date(purchases[purchases.size - 1].purchaseTime)
                Log.e("Date = ", date.toString())
                hitAddPlanSuccessApi(purchases[purchases.size - 1].sku,
                        Constants.SUCCESS,
                        purchases[purchases.size - 1].purchaseToken,
                        purchases[purchases.size - 1].originalJson,
                        mLandingInstance.paymentPlatform,
                        purchases[purchases.size - 1].purchaseTime.toString())
            }
        } else
            mLandingInstance.showInternetAlert(llPlans)
    }

    override fun onPurchaseFailure() {
        if (mLandingInstance.connectedToInternet()) {
            if (boostFragment != null && isPlanBought) {
                hitAddPlanSuccessApi(mPlansArray[purchasePlanPosition].plan_id,
                        Constants.FAILURE,
                        Constants.EMPTY,
                        Constants.EMPTY,
                        mLandingInstance.paymentPlatform,
                        Constants.EMPTY)
            }
        } else
            mLandingInstance.showInternetAlert(llPlans)
    }

    private fun hitAddPlanSuccessApi(planId: String, status: String, purchaseToken: String,
                                     originalJson: String, paymentPlatform: String,
                                     purchaseTime: String) {
        if (isPlanBought) {
            mLandingInstance.showLoader()
            RetrofitClient.getInstance().addPlanSubscription(mUtils.getString("access_token", ""),
                    status, planId, originalJson, paymentPlatform, purchaseTime, purchaseToken)
                    .enqueue(object : Callback<PaymentAdditionModel> {
                        override fun onFailure(call: Call<PaymentAdditionModel>?, t: Throwable?) {
                            mLandingInstance.dismissLoader()
                            mLandingInstance.showAlert(llPlans, t!!.localizedMessage)
                            isPlanBought = false
                        }

                        override fun onResponse(call: Call<PaymentAdditionModel>?,
                                                response: Response<PaymentAdditionModel>) {
                            mLandingInstance.dismissLoader()
                            if (response.body().error != null) {
                                isPlanBought = false
                                if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                                    Toast.makeText(activity, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                                    mLandingInstance.moveToSplash()
                                } else
                                    mLandingInstance.showAlert(llPlans, response.body().error!!.message!!)
                            } else {
                                if (status == Constants.SUCCESS) {
                                    mBillingManager.consumeProduct(purchaseToken)
                                    isPlanBought = false
                                    isBuyEnable = false
                                    mPlansArray.set(purchasePlanPosition, response.body().response)
                                    mAdapterBoost.notifyDataSetChanged()
                                }
                            }
                        }
                    })
        }
    }

    override fun productsList(skuDetailsListLocal: java.util.ArrayList<SkuDetails>) {
        if (boostFragment != null) {
            pbPlans.visibility = View.GONE
            skuDetailsList.clear()
            skuDetailsList.addAll(skuDetailsListLocal)
            mAdapterBoost.notifyDataSetChanged()
        }
    }
}