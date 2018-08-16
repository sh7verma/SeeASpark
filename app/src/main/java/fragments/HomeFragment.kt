package fragments

import adapters.HomeCardsAdapter
import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.google.gson.Gson
import com.seeaspark.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_home.*
import models.*
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.ConnectivityReceiver
import utils.Constants
import utils.MainApplication
import utils.Utils


class HomeFragment : Fragment(), View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private val PREFERENCES: Int = 2
    private val VIEWPROFILE: Int = 4

    var mLandingInstance: LandingActivity? = null
    var itemView: View? = null
    var mContext: Context? = null
    private var mAdapterCards: HomeCardsAdapter? = null
    private var mHomeFragment: HomeFragment? = null

    private var mUtils: Utils? = null
    private var mOffset = 1

    private var mLayoutManager: LinearLayoutManager? = null
    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0
    private var visibleThreshold: Int = 3
    var isLoading: Boolean = false

    private var width = 0
    private var height = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_home, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mContext = activity
        mLandingInstance = activity as LandingActivity
        mHomeFragment = this
        mUtils = Utils(mContext)

        if (mUtils!!.getInt("nightMode", 0) != 1) displayDayMode() else displayNightMode()

        if (mLandingInstance!!.mLatitude != 0.0 && mLandingInstance!!.mLatitude != 0.0)
            displayCards()

        initListener()
        onCreateStuff()


        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayDayMode() {
        llHomeToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white_color))
        imgPreferHome.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        txtTitleHome.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
        imgProfileHome.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        rlCardBase.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llHomeToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        imgPreferHome.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        txtTitleHome.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        imgProfileHome.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        rlCardBase.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
    }

    private fun onCreateStuff() {
        mUtils = Utils(mContext)
        if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTEE)
            txtTitleHome.text = getString(R.string.mentors)
        else
            txtTitleHome.text = getString(R.string.mentees)
        val drawable = ContextCompat.getDrawable(mContext!!, R.mipmap.ic_ava_ob)

        width = drawable!!.intrinsicWidth
        height = drawable.intrinsicHeight

        Picasso.with(mContext).load(mLandingInstance!!.userData!!.response.avatar.avtar_url)
                .resize(width, height).into(imgProfileHome)

        mLayoutManager = LinearLayoutManager(mContext)

        rvCards.layoutManager = mLayoutManager!!

        rvCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleThreshold = mLayoutManager!!.childCount
                totalItemCount = mLayoutManager!!.itemCount
                lastVisibleItem = mLayoutManager!!.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mLandingInstance!!.mArrayCards.size > 5) {
                        if (mLandingInstance!!.connectedToInternet()) {
                            mOffset++
                            val cardsDisplayModel = CardsDisplayModel()
                            cardsDisplayModel.post_type = Constants.PROGRESS
                            mLandingInstance!!.mArrayCards.add(cardsDisplayModel)
                            rvCards.post(Runnable { mAdapterCards!!.notifyItemInserted(mLandingInstance!!.mArrayCards.size - 1) })
                            hitAPI(false)
                            isLoading = true
                        }
                    }
                }
            }

        })

        srlCards.setColorSchemeResources(R.color.colorPrimary)
        srlCards.setOnRefreshListener {
            isLoading = false
            mOffset = 1
            hitAPI(false)
        }

    }

    fun hitAPI(visibleLoader: Boolean) {
        if (visibleLoader)
            mLandingInstance!!.showLoader()
        val call = RetrofitClient.getInstance().getCards(mUtils!!.getString("access_token", ""),
                mLandingInstance!!.mLatitude.toString(),
                mLandingInstance!!.mLongitude.toString(),
                mOffset.toString())
        call.enqueue(object : Callback<CardModel> {

            override fun onResponse(call: Call<CardModel>?, response: Response<CardModel>) {
                if (visibleLoader)
                    mLandingInstance!!.dismissLoader()
                if (response.body().response != null) {
                    if (mOffset == 1)
                        populateData(response.body())
                    else {
                        mLandingInstance!!.mArrayCards.removeAt(mLandingInstance!!.mArrayCards.size - 1)
                        mAdapterCards!!.notifyItemRemoved(mLandingInstance!!.mArrayCards.size)

                        if (response.body().response.size > 0) {
                            mLandingInstance!!.mArrayCards.addAll(response.body().response)
                            mAdapterCards!!.notifyDataSetChanged()
                            isLoading = false
                        } else {
                            isLoading = true
                        }
                    }
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(rvCards, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<CardModel>?, t: Throwable?) {
                if (visibleLoader)
                    mLandingInstance!!.dismissLoader()
                mLandingInstance!!.showAlert(rvCards, t!!.localizedMessage)
            }
        })
    }

    private fun populateData(response: CardModel) {
        if (llMainHomeFrag != null) {
            if (srlCards != null && srlCards.isRefreshing)
                srlCards.isRefreshing = false

            mLandingInstance!!.mArrayCards.clear()

            for (cardsValue in response.response) {
                mLandingInstance!!.mArrayCards.add(cardsValue)
            }

            for (postValue in response.posts) {
                mLandingInstance!!.mArrayCards.add(postValue)

                val postData = createPostData(postValue)

                mLandingInstance!!.db!!.addPosts(postData)

                for (imagesData in postData.images) {
                    if (postValue.post_type == Constants.EVENT)
                        mLandingInstance!!.db!!.addPostImages(imagesData, postData.id.toString(), Constants.EVENT)
                    else
                        mLandingInstance!!.db!!.addPostImages(imagesData, postData.id.toString(), Constants.COMMUNITY)
                }
                for (goingUserData in postData.going_list) {
                    mLandingInstance!!.db!!.addPostGoingUsers(goingUserData, postData.id.toString())
                }
            }

            if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTEE) {
                val cardsDisplayModel = CardsDisplayModel()
                cardsDisplayModel.post_type = 3
                cardsDisplayModel.time_left = response.time_left
                mLandingInstance!!.mArrayCards.add(cardsDisplayModel)
            }
            if (mLandingInstance!!.mArrayCards.size == 0) {
                llOutOfCards.visibility = View.VISIBLE
            } else {
                llOutOfCards.visibility = View.GONE
                displayCards()
            }
        }
    }

    private fun displayCards() {
        mAdapterCards = HomeCardsAdapter(mLandingInstance!!.mArrayCards, mContext!!, mLandingInstance!!.mWidth, mHomeFragment)
        rvCards.adapter = mAdapterCards
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

    private fun initListener() {
        imgPreferHome.setOnClickListener(this)
        imgProfileHome.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        var intent: Intent? = null
        when (view) {
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

    fun swipeRightLeft(swiped: Int, id: Int) {
        if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTOR) {
            if (mLandingInstance!!.mArrayCards.size == 0) {
                rvCards.visibility = View.GONE
                llOutOfCards.visibility = View.VISIBLE
            }
        }
        isLoading = false
        hitSwipeAPI(swiped, id)
    }

    private fun hitSwipeAPI(swiped: Int, othetUserId: Int) {
        val call = RetrofitClient.getInstance().swipeCards(mUtils!!.getString("access_token", ""),
                swiped, othetUserId.toString())
        call.enqueue(object : Callback<SwipeCardModel> {
            override fun onResponse(call: Call<SwipeCardModel>?, response: Response<SwipeCardModel>) {
                if (response.body().response != null) {
                    if (response.body().is_handshake == 1) {
                        /// match case
                        val intent = Intent(mContext!!, HandshakeActivity::class.java)
                        intent.putExtra("otherProfileData", response.body().response)
                        startActivity(intent)
                        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        mLandingInstance!!.showToast(mContext!!, response.body().error!!.message!!)
                        mLandingInstance!!.moveToSplash()
                    } else if (response.body().error!!.code == Constants.DELETE_ACCOUNT) {
                        /// no operation
                    } else
                        mLandingInstance!!.showAlert(rvCards, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SwipeCardModel>?, t: Throwable?) {
                mLandingInstance!!.showAlert(llHomeToolbar, t!!.localizedMessage)
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PREFERENCES -> {
                    isLoading = false
                    mOffset = 1
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
        mLandingInstance!!.userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        Picasso.with(mContext).load(mLandingInstance!!.userData!!.response.avatar.avtar_url)
                .resize(width, height).into(imgProfileHome)
    }

    fun boostPlan() {
        val intent = Intent(mContext, BoostDialog::class.java)
        startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_up, 0)
    }


    override fun onResume() {
        // register connection status listener
        MainApplication.getInstance().setConnectivityListener(this)
        super.onResume()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (rvCards != null) {
            if (isConnected) {
                if (mLandingInstance!!.mArrayCards.size == 0) {
                    mOffset = 1
                    isLoading = false
                    hitAPI(false)
                } else {
                    mAdapterCards = HomeCardsAdapter(mLandingInstance!!.mArrayCards, mContext!!, mLandingInstance!!.mWidth, mHomeFragment)
                    rvCards.adapter = mAdapterCards
                }
            } else {
                mAdapterCards = HomeCardsAdapter(mLandingInstance!!.mArrayCards, mContext!!, mLandingInstance!!.mWidth, mHomeFragment)
                rvCards.adapter = mAdapterCards
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun resetData() {
        if (mAdapterCards != null) {
            isLoading = false
            mOffset = 1
            mAdapterCards = HomeCardsAdapter(mLandingInstance!!.mArrayCards, mContext!!, mLandingInstance!!.mWidth, mHomeFragment)
            rvCards.adapter = mAdapterCards
        }
    }

    fun moveToCommunityDetail(postId: Int, imgCommunityListing: ImageView) {
        if (mLandingInstance!!.connectedToInternet()) {
            val intent = Intent(mContext, CommunityDetailActivity::class.java)
            intent.putExtra("communityId", postId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        imgCommunityListing, getString(R.string.transition_image))
                activity.startActivity(intent, option.toBundle())
            } else {
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        } else
            mLandingInstance!!.showInternetAlert(rvEventsListing)
    }

    fun moveToEventDetail(postId: Int, imgEventCard: ImageView) {
        if (mLandingInstance!!.connectedToInternet()) {
            val intent = Intent(mContext, EventsDetailActivity::class.java)
            intent.putExtra("eventId", postId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        imgEventCard, getString(R.string.transition_image))
                activity.startActivity(intent, option.toBundle())
            } else {
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        } else
            mLandingInstance!!.showInternetAlert(rvEventsListing)
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(nightModeReceiver,
                IntentFilter(Constants.NIGHT_MODE))
        LocalBroadcastManager.getInstance(activity).registerReceiver(switchUserTypeReceiver,
                IntentFilter(Constants.SWITCH_USER_TYPE))
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(nightModeReceiver)
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(switchUserTypeReceiver)
        super.onDestroy()
    }

    var nightModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context, intent: Intent) {
            isLoading = false
            mOffset = 1
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
            hitAPI(false)
            mLandingInstance!!.checkUserType()
            if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTEE)
                txtTitleHome.text = getString(R.string.mentors)
            else
                txtTitleHome.text = getString(R.string.mentees)
        }
    }
}