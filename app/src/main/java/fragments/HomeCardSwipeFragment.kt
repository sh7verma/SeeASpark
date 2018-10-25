package fragments

import adapters.HomeBoostPlansAdapter
import adapters.HomeCardSwipeAdapter
import android.app.Activity
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.ConfettiSource
import com.github.jinatonic.confetti.ConfettoGenerator
import com.github.jinatonic.confetti.confetto.CircleConfetto
import com.github.jinatonic.confetti.confetto.Confetto
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.gson.Gson
import com.seeaspark.*
import com.squareup.picasso.Picasso
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.SwipeDirection
import database.Database
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_home_card_swipe.*
import kotlinx.android.synthetic.main.item_swipe_card.view.*
import models.*
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.BillingManager
import utils.Constants
import utils.Utils
import java.util.*

class HomeCardSwipeFragment : Fragment(), View.OnClickListener,
        BillingManager.BillingUpdatesListener, ConfettoGenerator {

    private val PREFERENCES: Int = 2
    private val VIEWPROFILE: Int = 4

    lateinit var mLandingInstance: LandingActivity
    var itemView: View? = null
    var mContext: Context? = null
    private var mAdapterCards: HomeCardSwipeAdapter? = null
    private var mHomeFragment: HomeCardSwipeFragment? = null

    private var mUtils: Utils? = null

    private var width = 0
    private var height = 0
    private var mOffset = 1
    private var mCurrentPosition = 0
    private val CARDAPICOUNT = 3
    private lateinit var mHandler: Handler
    private var mDb: Database? = null

    private lateinit var mBillingManager: BillingManager
    private lateinit var mAdapterBoost: HomeBoostPlansAdapter
    private var mPlansArray = ArrayList<PlansModel.Response>()
    var isBuyEnable = true
    private var skuDetailsList = ArrayList<SkuDetails>()
    private var isPlanBought = false
    private var purchasePlanPosition = -1
    private var confettiManager: ConfettiManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_home_card_swipe, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mContext = activity
        mLandingInstance = activity as LandingActivity
        mHomeFragment = this
        mUtils = Utils(mContext)
        mDb = Database(mContext)

        if (mUtils!!.getInt("nightMode", 0) != 1)
            displayDayMode()
        else
            displayNightMode()

        if (mLandingInstance.mLatitude != 0.0 && mLandingInstance.mLatitude != 0.0) {
            mLandingInstance.mArrayCards.clear()
            mLandingInstance.mArrayCards.addAll(mLandingInstance.mArrayTempCards)
            displayCards()
        }
        mHandler = Handler()
        initListener()
        onCreateStuff()

        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayDayMode() {
        llHomeToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white_color))
        rlInnerOutCards.setBackgroundResource(R.drawable.background_out_of_card)
        imgPreferHome.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        txtTitleHome.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
        imgProfileHome.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        rlCardBase.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))

        txtOutOfCards.setTextColor(mLandingInstance.blackColor)
        txtOutOfCardsHint.setTextColor(mLandingInstance.blackColor)
        txtGetCards.setTextColor(mLandingInstance.blackColor)
        txtGetCards.setBackgroundResource(R.drawable.black_border_solid_white_round_corner)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llHomeToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        rlInnerOutCards.setBackgroundResource(R.drawable.dark_background_out_of_cards)
        imgPreferHome.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        txtTitleHome.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        imgProfileHome.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        rlCardBase.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))

        txtOutOfCards.setTextColor(mLandingInstance.whiteColor)
        txtOutOfCardsHint.setTextColor(mLandingInstance.whiteColor)
        txtGetCards.setTextColor(mLandingInstance.whiteColor)
        txtGetCards.setBackgroundResource(R.drawable.white_border_solid_trnsperent)
    }

    private fun onCreateStuff() {
        mUtils = Utils(mContext)
        if (mLandingInstance.userData!!.response.user_type == Constants.MENTEE) {
            if (mLandingInstance.connectedToInternet())
                hitPlansApi()
            txtTitleHome.text = getString(R.string.mentors)
        } else
            txtTitleHome.text = getString(R.string.mentees)
        val drawable = ContextCompat.getDrawable(mContext!!, R.mipmap.ic_ava_ob)

        width = drawable!!.intrinsicWidth
        height = drawable.intrinsicHeight

        Picasso.with(mContext).load(mLandingInstance.userData!!.response.avatar.avtar_url)
                .resize(width, height).into(imgProfileHome)

        Picasso.with(mContext).load(mLandingInstance.userData!!.response.avatar.avtar_url)
                .into(imgCenterAvatar)

        csvUsers.setCardEventListener(object : CardStackView.CardEventListener {
            override fun onCardDragging(percentX: Float, percentY: Float) {

            }

            override fun onCardSwiped(direction: SwipeDirection?) {
                if (mLandingInstance.connectedToInternet()) {

                    ///Paging
                    when {
                        mLandingInstance.mArrayCards[mCurrentPosition].post_type == Constants.CARD -> {
                            if (direction == SwipeDirection.Left)
                                swipeRightLeft(0, mLandingInstance.mArrayCards[mCurrentPosition])
                            else
                                swipeRightLeft(1, mLandingInstance.mArrayCards[mCurrentPosition])
                            // decrementing card count which provides the functionality to display buy Plans.
                            mLandingInstance.cardLeftCount--
                        }
                        mLandingInstance.mArrayCards[mCurrentPosition].post_type == Constants.COMMUNITY ->
                            if (direction == SwipeDirection.Right)
                                moveToCommunityDetail(mLandingInstance.mArrayCards[mCurrentPosition].id)
                        mLandingInstance.mArrayCards[mCurrentPosition].post_type == Constants.EVENT ->
                            if (direction == SwipeDirection.Right)
                                moveToEventDetail(mLandingInstance.mArrayCards[mCurrentPosition].id)
                    }

                    mCurrentPosition++
                    mLandingInstance.mArrayTempCards.removeAt(0)

                    // Evalauating which layout to display either buy plan or out of cards.
                    if (mLandingInstance.mArrayTempCards.size == 0)
                        checkVisibility()

                    if (mLandingInstance.mArrayTempCards.size - CARDAPICOUNT == 5) {
                        ///Paging (Fetching more cards )
                        mOffset++
                        hitAPI(false)
                    }

                    if (mCurrentPosition < mLandingInstance.mArrayCards.size) {
                        checkOverlayVisibility()
                    }
                } else {
                    csvUsers.reverse()
                    mLandingInstance.showInternetAlert(csvUsers)
                }
            }

            override fun onCardReversed() {

            }

            override fun onCardMovedToOrigin() {

            }

            override fun onCardClicked(index: Int) {
                if (mLandingInstance.mArrayCards[index].post_type == Constants.CARD)
                    openProfile(mLandingInstance.mArrayCards[index],
                            csvUsers.topView.contentContainer.imgAvatarCard)
            }
        })

        rvUnlimitedPlans.layoutManager = GridLayoutManager(activity, 2)
        mAdapterBoost = HomeBoostPlansAdapter(mPlansArray, mHomeFragment!!, null)
        rvUnlimitedPlans.adapter = mAdapterBoost
    }

    private fun checkVisibility() {
        if (mHomeFragment != null) {
            if (mLandingInstance.mArrayTempCards.isEmpty()) {
                if (mLandingInstance.userData!!.response.user_type == Constants.MENTEE) {
                    if (isBuyEnable && mLandingInstance.cardLeftCount == 0) {
                        llOutOfCards.visibility = View.GONE
                        llHomePlans.visibility = View.VISIBLE
                    } else {
                        llHomePlans.visibility = View.GONE
                        llOutOfCards.visibility = View.VISIBLE
                    }
                } else
                    llOutOfCards.visibility = View.VISIBLE
                if (confettiManager != null)
                    confettiManager!!.terminate()
            } else {
                Handler().postDelayed({
                    generateConfetti()
                }, 100)

                llOutOfCards.visibility = View.GONE
                llHomePlans.visibility = View.GONE
            }
        }
    }

    private fun generateConfetti() {
        /*  confettiManager = getConfettiManager().setNumInitialCount(0)
                  .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                  .setEmissionRate(25f).animate()*/
    }

    fun hitAPI(visibleLoader: Boolean) {
        if (visibleLoader)
            mLandingInstance.showLoader()

        val call = RetrofitClient.getInstance().getCards(mUtils!!.getString("access_token", ""),
                mLandingInstance.mLatitude.toString(), mLandingInstance.mLongitude.toString(),
                mOffset.toString())

        call.enqueue(object : Callback<CardModel> {

            override fun onResponse(call: Call<CardModel>?, response: Response<CardModel>) {
                if (response.body().response != null) {
                    mLandingInstance.cardLeftCount = response.body().card_left
                    populateData(response.body())
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        mLandingInstance.moveToSplash()
                    } else {
                        mLandingInstance.showAlert(llMainHomeFrag, response.body().error!!.message!!)
                    }
                }
                if (visibleLoader)
                    mLandingInstance.dismissLoader()
            }

            override fun onFailure(call: Call<CardModel>?, t: Throwable?) {
                if (visibleLoader)
                    mLandingInstance.dismissLoader()
                mLandingInstance.showAlert(llMainHomeFrag, t!!.localizedMessage)
            }
        })
    }

    private fun populateData(response: CardModel) {
        if (llMainHomeFrag != null) {
            if (mOffset == 1) {
                mLandingInstance.mArrayCards.clear()
                mLandingInstance.mArrayTempCards.clear()

                mLandingInstance.mArrayCards.addAll(response.response)
                mLandingInstance.mArrayTempCards.addAll(response.response)

                for (postValue in response.posts) {
                    mLandingInstance.mArrayCards.add(postValue)
                    mLandingInstance.mArrayTempCards.add(postValue)

                    val postData = createPostData(postValue)
                    mLandingInstance.db!!.addPosts(postData)
                    for (imagesData in postData.images) {
                        if (postValue.post_type == Constants.EVENT)
                            mLandingInstance.db!!.addPostImages(imagesData, postData.id.toString(), Constants.EVENT)
                        else
                            mLandingInstance.db!!.addPostImages(imagesData, postData.id.toString(), Constants.COMMUNITY)
                    }
                    for (goingUserData in postData.going_list) {
                        mLandingInstance.db!!.addPostGoingUsers(goingUserData, postData.id.toString())
                    }
                }

                mAdapterCards = HomeCardSwipeAdapter(mContext!!, 0,
                        mLandingInstance.mArrayCards)
                csvUsers.setAdapter(mAdapterCards)

                if (mLandingInstance.mArrayCards.isNotEmpty())
                    checkOverlayVisibility()

            } else {
                if (response.response.isNotEmpty()) {
                    mLandingInstance.mArrayCards.addAll(response.response)
                    mLandingInstance.mArrayTempCards.addAll(response.response)
                    csvUsers.setPaginationReserved()
                    mAdapterCards!!.notifyDataSetChanged()
                }
            }
            checkVisibility()
        }
    }

    private fun checkOverlayVisibility() {
        if (mLandingInstance.mArrayCards[mCurrentPosition].post_type == Constants.CARD) {
            csvUsers.setRightOverlay(R.layout.layout_right_overlay)
            csvUsers.setLeftOverlay(R.layout.layout_left_overlay)
        } else {
            csvUsers.setRightOverlay(0)
            csvUsers.setLeftOverlay(0)
        }
    }

    private fun displayCards() {
        mAdapterCards = HomeCardSwipeAdapter(mContext!!, 0, mLandingInstance.mArrayCards)
        csvUsers.setAdapter(mAdapterCards)
        if (mLandingInstance.mArrayCards.isNotEmpty())
            checkOverlayVisibility()
        checkVisibility()
    }

    private fun initListener() {
        imgPreferHome.setOnClickListener(this)
        imgProfileHome.setOnClickListener(this)
        txtGetCards.setOnClickListener(this)
        txtGetCardsPlans.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent: Intent?
        when (view) {
            txtGetCardsPlans -> {
                if (mLandingInstance.connectedToInternet()) {
                    mOffset = 1
                    mCurrentPosition = 0
                    hitAPI(true)
                } else {
                    mLandingInstance.showInternetAlert(llMainHomeFrag)
                }
            }
            txtGetCards -> {
                if (mLandingInstance.connectedToInternet()) {
                    mOffset = 1
                    mCurrentPosition = 0
                    hitAPI(true)
                } else {
                    mLandingInstance.showInternetAlert(llMainHomeFrag)
                }
            }
            imgProfileHome -> {
                intent = Intent(mContext!!, ViewProfileActivity::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                            imgProfileHome, getString(R.string.transition_image))
                    activity.startActivityForResult(intent, VIEWPROFILE, option.toBundle())
                } else {
                    startActivityForResult(intent, VIEWPROFILE)
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                }
            }
            imgPreferHome -> {
                intent = Intent(mContext!!, PreferencesActivity::class.java)
                intent.putExtra("showPrefilled", "yes")
                startActivityForResult(intent, PREFERENCES)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
            }
        }
    }

    fun openProfile(cardsDisplayModel: CardsDisplayModel, imgAvatarCard: ImageView) {
        val intent = Intent(mContext, OtherProfileActivity::class.java)
        intent.putExtra("otherProfileData", cardsDisplayModel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val option = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    imgAvatarCard, getString(R.string.transition_image))
            activity.startActivity(intent, option.toBundle())
        } else {
            startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
    }

    fun swipeRightLeft(swiped: Int, otheeUserModel: CardsDisplayModel) {
        if (mLandingInstance.userData!!.response.user_type == Constants.MENTOR) {
            if (mLandingInstance.mArrayCards.size == 0) {
                llOutOfCards.visibility = View.VISIBLE
            }
        }
        hitSwipeAPI(swiped, otheeUserModel)
    }

    private fun hitSwipeAPI(swiped: Int, othetUserModel: CardsDisplayModel) {
        val call = RetrofitClient.getInstance().swipeCards(mUtils!!.getString("access_token", ""),
                swiped, othetUserModel.id.toString())
        call.enqueue(object : Callback<SwipeCardModel> {
            override fun onResponse(call: Call<SwipeCardModel>?, response: Response<SwipeCardModel>) {
                if (response.body().response != null) {
                    if (response.body().is_handshake == 1) {
                        /// match case
                        createNewChat(othetUserModel, response.body().response)
                    }
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        mLandingInstance.showToast(mContext!!, response.body().error!!.message!!)
                        mLandingInstance.moveToSplash()
                    }
                }
            }

            override fun onFailure(call: Call<SwipeCardModel>?, t: Throwable?) {
                mLandingInstance.showAlert(llHomeToolbar, t!!.localizedMessage)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PREFERENCES -> {
                    mCurrentPosition = 0
                    mOffset = 1
                    isBuyEnable = true
                    hitPlansApi()
                    hitAPI(false)
                }
                VIEWPROFILE -> {
                    populateData()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun populateData() {
        val mGson = Gson()
        mLandingInstance.userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        Picasso.with(mContext).load(mLandingInstance.userData!!.response.avatar.avtar_url)
                .resize(width, height).into(imgProfileHome)

        Picasso.with(mContext).load(mLandingInstance.userData!!.response.avatar.avtar_url)
                .into(imgCenterAvatar)
    }

    private fun hitPlansApi() {
        if (llHomePlans.visibility == View.VISIBLE)
            pbPlansLoader.visibility = View.VISIBLE
        RetrofitClient.getInstance().getPlans(mLandingInstance.mUtils!!.getString("access_token", ""),
                "Unlimitdd").enqueue(object : Callback<PlansModel> {
            override fun onResponse(call: Call<PlansModel>?, response: Response<PlansModel>) {
                if (mHomeFragment != null) {
                    if (llHomePlans.visibility == View.VISIBLE)
                        pbPlansLoader.visibility = View.GONE
                    if (response.body().error != null) {
                        if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                            Toast.makeText(activity, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                            mLandingInstance.moveToSplash()
                        } else
                            mLandingInstance.showAlert(llHomePlans, response.body().error!!.message!!)
                    } else {
                        mPlansArray.clear()
                        val planIdsArray = ArrayList<String>()
                        for (planData in response.body().response) {
                            planIdsArray.add(planData.plan_id)
                            mPlansArray.add(planData)
                            if (planData.is_expired == 0)
                                isBuyEnable = false
                        }
                        mBillingManager = BillingManager(activity, this@HomeCardSwipeFragment,
                                planIdsArray)
                    }
                }
            }

            override fun onFailure(call: Call<PlansModel>?, t: Throwable?) {
                if (mHomeFragment != null) {
                    if (llHomePlans.visibility == View.VISIBLE)
                        pbPlansLoader.visibility = View.GONE
                }
            }
        })
    }

    fun buyPlan(position: Int) {
        if (mLandingInstance.connectedToInternet()) {
            if (isBuyEnable) {
                isPlanBought = true
                purchasePlanPosition = position
                mBillingManager.initiatePurchaseFlow(skuDetailsList[position].sku)
            } else {
                mLandingInstance.showToast(activity, getString(R.string.plan_already_bought))
            }
        } else {
            mLandingInstance.showInternetAlert(llHomePlans)
        }
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(nightModeReceiver,
                IntentFilter(Constants.NIGHT_MODE))
        LocalBroadcastManager.getInstance(activity).registerReceiver(switchUserTypeReceiver,
                IntentFilter(Constants.SWITCH_USER_TYPE))
        super.onStart()
    }

    override fun onDestroy() {
        Log.e("Destroy = ", "destroy")
        if (confettiManager != null)
            confettiManager!!.terminate()
        mHomeFragment = null
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(nightModeReceiver)
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(switchUserTypeReceiver)
        super.onDestroy()
    }

    var nightModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context, intent: Intent) {
            mOffset = 1
            mCurrentPosition = 0
            if (intent.getIntExtra("status", 0) == Constants.DAY) {
                resetData()
                displayDayMode()
            } else {
                resetData()
                displayNightMode()
            }
        }
    }

    var switchUserTypeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mOffset = 1
            mCurrentPosition = 0
            hitAPI(false)
            mUtils!!.setString("user_type", mLandingInstance.userData!!.response.user_type.toString())
            mLandingInstance.checkUserType()
            populateData()
            if (mLandingInstance.userData!!.response.user_type == Constants.MENTEE)
                txtTitleHome.text = getString(R.string.mentors)
            else
                txtTitleHome.text = getString(R.string.mentees)
        }
    }

    fun resetData() {
        mOffset = 1
        mCurrentPosition = 0
        mAdapterCards = HomeCardSwipeAdapter(mContext!!, 0, mLandingInstance.mArrayCards)
        csvUsers.setAdapter(mAdapterCards)
        checkVisibility()
    }

    override fun onBillingClientSetupFinished() {
    }

    override fun onConsumeFinished(token: String?, result: Int) {
    }

    override fun onPurchasesUpdated(purchases: MutableList<Purchase>) {
        if (mLandingInstance.connectedToInternet()) {
            if (mHomeFragment != null && purchases.isNotEmpty()) {
                isPlanBought = true
                hitAddPlanSuccessApi(purchases[purchases.size - 1].sku,
                        Constants.SUCCESS,
                        purchases[purchases.size - 1].purchaseToken,
                        purchases[purchases.size - 1].originalJson,
                        mLandingInstance.paymentPlatform,
                        purchases[purchases.size - 1].purchaseTime.toString())
            }
        } else
            mLandingInstance.showInternetAlert(llHomePlans)
    }

    override fun onPurchaseFailure() {
        if (mLandingInstance.connectedToInternet()) {
            if (mHomeFragment != null && isPlanBought) {
                hitAddPlanSuccessApi(mPlansArray[purchasePlanPosition].plan_id,
                        Constants.FAILURE,
                        Constants.EMPTY,
                        Constants.EMPTY,
                        mLandingInstance.paymentPlatform,
                        Constants.EMPTY)
            }
        } else
            mLandingInstance.showInternetAlert(llHomePlans)
    }

    override fun productsList(skuDetailsListLocal: ArrayList<SkuDetails>) {
        if (mHomeFragment != null) {
            skuDetailsList.clear()
            skuDetailsList.addAll(skuDetailsListLocal)
            if (skuDetailsListLocal.size == mPlansArray.size) {
                mAdapterBoost.notifyDataSetChanged()
            } else {
                mLandingInstance.showAlert(llHomePlans, getString(R.string.plans_error))
            }
        }
    }

    private fun hitAddPlanSuccessApi(planId: String, status: String, purchaseToken: String,
                                     originalJson: String, paymentPlatform: String,
                                     purchaseTime: String) {
        if (isPlanBought) {
            mLandingInstance.showLoader()
            RetrofitClient.getInstance().addPlanSubscription(mUtils!!.getString("access_token", ""),
                    status, planId, originalJson, paymentPlatform, purchaseTime, purchaseToken)
                    .enqueue(object : Callback<PaymentAdditionModel> {
                        override fun onFailure(call: Call<PaymentAdditionModel>?, t: Throwable?) {
                            mLandingInstance.dismissLoader()
                            mLandingInstance.showAlert(llHomePlans, t!!.localizedMessage)
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
                                    mLandingInstance.showAlert(llHomePlans, response.body().error!!.message!!)
                            } else {
                                if (status == Constants.SUCCESS) {
                                    mBillingManager.consumeProduct(purchaseToken)
                                    isPlanBought = false
                                    isBuyEnable = false
                                    llHomePlans.visibility = View.GONE
                                    if (mLandingInstance.connectedToInternet()) {
                                        mOffset = 1
                                        mCurrentPosition = 0
                                        hitAPI(true)
                                    } else {
                                        mLandingInstance.showInternetAlert(llMainHomeFrag)
                                    }
                                }
                            }
                        }
                    })
        }
    }

    fun moveToCommunityDetail(postId: Int) {
        if (mLandingInstance.connectedToInternet()) {
            val intent = Intent(mContext, CommunityDetailActivity::class.java)
            intent.putExtra("communityId", postId)
            startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            mLandingInstance.showInternetAlert(rvEventsListing)
    }

    fun moveToEventDetail(postId: Int) {
        if (mLandingInstance.connectedToInternet()) {
            val intent = Intent(mContext, EventsDetailActivity::class.java)
            intent.putExtra("eventId", postId)
            startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            mLandingInstance.showInternetAlert(rvEventsListing)
    }

    private fun createPostData(postValue: CardsDisplayModel): PostModel.ResponseBean {
        val postData = PostModel.ResponseBean()
        postData.id = postValue.id
        postData.post_type = postValue.post_type
        postData.title = postValue.title
        postData.description = postValue.description
        postData.profession_id = postValue.profession_id
        postData.address = postValue.address
        postData.latitude = postValue.latitude
        postData.longitude = postValue.longitude
        postData.date_time = postValue.date_time
        postData.url = postValue.url
        postData.is_featured = postValue.is_featured
        postData.like = postValue.like
        postData.comment = postValue.comment
        postData.going = postValue.going
        postData.interested = postValue.interested
        postData.liked = postValue.liked
        postData.is_going = postValue.is_going
        postData.bookmarked = postValue.bookmarked
        postData.going_list = postValue.going_list
        postData.images = postValue.images
        postData.shareable_link = postValue.shareable_link
        return postData
    }

    /// Hunny Code
    internal fun createNewChat(othetUser: CardsDisplayModel, response: SignupModel.ResponseBean) {
        val mParticpantIDSList = ArrayList<String>()
        mParticpantIDSList.add(othetUser.id.toString() + "_" + othetUser.user_type)
        mParticpantIDSList.add(mLandingInstance.userData!!.response.id.toString() + "_" + mLandingInstance.userData!!.response.user_type)
        Collections.sort(mParticpantIDSList)
        var mParticpantIDS = mParticpantIDSList.toString()
        var participants = mParticpantIDS.substring(1, mParticpantIDS.length - 1)
        participants = participants.replace(" ", "")
        val mChat = ChatsModel()
        mChat.chat_dialog_id = participants
        mChat.last_message = Constants.DEFAULT_MESSAGE_REGEX


        mChat.last_message_sender_id = mLandingInstance.userData!!.response.id.toString()
        mChat.last_message_id = "0"
        mChat.participant_ids = participants
        val unread = HashMap<String, Int>()
        unread.put(mLandingInstance.userData!!.response.id.toString(), 0)
        unread.put(othetUser.id.toString(), 0)
        mChat.unread_count = unread

        val name = HashMap<String, String>()
        name.put(mLandingInstance.userData!!.response.id.toString(), mLandingInstance.userData!!.response.full_name)
        name.put(othetUser.id.toString(), othetUser.full_name)
        mChat.name = name

        val lastMessageType = HashMap<String, String>()
        lastMessageType.put(mLandingInstance.userData!!.response.id.toString(), "")
        lastMessageType.put(othetUser.id.toString(), "")
        mChat.last_message_type = lastMessageType

        val lastMessageData = HashMap<String, String>()
        lastMessageData.put(mLandingInstance.userData!!.response.id.toString(), Constants.DEFAULT_MESSAGE_REGEX)
        lastMessageData.put(othetUser.id.toString(), Constants.DEFAULT_MESSAGE_REGEX)
        mChat.last_message_data = lastMessageData

        val pic = HashMap<String, String>()
        pic.put(mLandingInstance.userData!!.response.id.toString(), mLandingInstance.userData!!.response.avatar.avtar_url)
        pic.put(othetUser.id.toString(), othetUser.avatar.avtar_url)
        mChat.profile_pic = pic

// val time = ServerValue.TIMESTAMP
        val utcTime = Constants.getUtcTime(Calendar.getInstance().timeInMillis)
        val deleteTime = HashMap<String, Long>()
        deleteTime.put(mLandingInstance.userData!!.response.id.toString(), utcTime)
        deleteTime.put(othetUser.id.toString(), utcTime)
        mChat.delete_dialog_time = deleteTime

        val lastTime = HashMap<String, Long>()
        lastTime.put(mLandingInstance.userData!!.response.id.toString(), utcTime)
        lastTime.put(othetUser.id.toString(), utcTime)
        mChat.last_message_time = lastTime

        val block = HashMap<String, String>()
        block.put(mLandingInstance.userData!!.response.id.toString(), "0")
        block.put(othetUser.id.toString(), "0")
        mChat.block_status = block

        val rating = HashMap<String, String>()
        rating.put(mLandingInstance.userData!!.response.id.toString(), "0")
        rating.put(othetUser.id.toString(), "0")
        mChat.rating = rating

        val message_rating_count = HashMap<String, Int>()
        message_rating_count.put(mLandingInstance.userData!!.response.id.toString(), 0)
        message_rating_count.put(othetUser.id.toString(), 0)
        mChat.message_rating_count = message_rating_count

        val userType = HashMap<String, String>()
        userType.put(mLandingInstance.userData!!.response.id.toString(), mLandingInstance.userData!!.response.user_type.toString())
        userType.put(othetUser.id.toString(), othetUser.user_type.toString())
        mChat.user_type = userType

        val mFirebaseConfig = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS)
        mFirebaseConfig.child(participants).setValue(mChat).addOnSuccessListener {
            mChat.opponent_user_id = othetUser.id.toString()
            mDb!!.addNewChat(mChat, mLandingInstance.userData!!.response.id.toString(), othetUser.id.toString())
            val mFirebaseConfigChat: DatabaseReference
            mFirebaseConfigChat = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS)
            mFirebaseConfigChat.child(participants).child("delete_dialog_time").child(mLandingInstance.userData!!.response.id.toString())
                    .setValue(ServerValue.TIMESTAMP)
            mFirebaseConfigChat.child(participants).child("delete_dialog_time").child(othetUser.id.toString())
                    .setValue(ServerValue.TIMESTAMP)

            val mFirebaseConfigUser: DatabaseReference
            mFirebaseConfigUser = FirebaseDatabase.getInstance().getReference().child(Constants.USERS)
            mFirebaseConfigUser.child("id_" + othetUser.id).child("chat_dialog_ids").child(participants)
                    .setValue(participants)
            mFirebaseConfigUser.child("id_" + mLandingInstance.userData!!.response.id.toString())
                    .child("chat_dialog_ids").child(participants).setValue(participants)

            try {
                val intent = Intent(mContext!!, HandshakeActivity::class.java)
                intent.putExtra("otherProfileData", response)
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            } catch (e: Exception) {
                Log.e("e", e.localizedMessage);
            }
        }.addOnFailureListener {

        }
    }

    private fun getConfettiManager(): ConfettiManager {
        val confettiSource = ConfettiSource(llEmitter.width / 2, llEmitter.height / 2)
        return ConfettiManager(mContext!!, this, confettiSource, llEmitter)
                .setVelocityX(0f, 70.toFloat())
                .setVelocityY(0f, 70.toFloat())
    }

    override fun generateConfetto(random: Random?): Confetto {
        return CircleConfetto(ContextCompat.getColor(activity, R.color.colorPrimary),
                8f)
    }
}