package fragments

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.gson.Gson
import com.seeaspark.*
import com.squareup.picasso.Picasso
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.SwipeDirection
import database.Database
import kotlinx.android.synthetic.main.fragment_home_card_swipe.*
import kotlinx.android.synthetic.main.item_swipe_card.view.*
import models.*
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.ConnectivityReceiver
import utils.Constants
import utils.MainApplication
import utils.Utils
import java.util.*

class HomeCardSwipeFragment : Fragment(), View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private val PREFERENCES: Int = 2
    private val VIEWPROFILE: Int = 4

    var mLandingInstance: LandingActivity? = null
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
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private var mDb: Database? = null

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

        if (mLandingInstance!!.mLatitude != 0.0 && mLandingInstance!!.mLatitude != 0.0) {
            mLandingInstance!!.mArrayCards.clear()
            mLandingInstance!!.mArrayCards.addAll(mLandingInstance!!.mArrayTempCards)
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
        imgPreferHome.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        txtTitleHome.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
        imgProfileHome.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        rlCardBase.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))

        txtOutOfCards.setTextColor(mLandingInstance!!.blackColor)
        txtOutOfCardsHint.setTextColor(mLandingInstance!!.blackColor)
        txtGetCards.setTextColor(mLandingInstance!!.blackColor)
        txtGetCards.setBackgroundResource(R.drawable.black_border_solid_white_round_corner)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llHomeToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        imgPreferHome.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        txtTitleHome.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        imgProfileHome.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        rlCardBase.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))

        txtOutOfCards.setTextColor(mLandingInstance!!.whiteColor)
        txtOutOfCardsHint.setTextColor(mLandingInstance!!.whiteColor)
        txtGetCards.setTextColor(mLandingInstance!!.whiteColor)
        txtGetCards.setBackgroundResource(R.drawable.white_border_solid_trnsperent)
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

        Picasso.with(mContext).load(mLandingInstance!!.userData!!.response.avatar.avtar_url)
                .into(imgCenterAvatar)

        csvUsers.setCardEventListener(object : CardStackView.CardEventListener {
            override fun onCardDragging(percentX: Float, percentY: Float) {

            }

            override fun onCardSwiped(direction: SwipeDirection?) {
                if (mLandingInstance!!.connectedToInternet()) {

                    Log.e("Test = ", mCurrentPosition.toString())

                    if (direction == SwipeDirection.Left)
                        swipeRightLeft(0, mLandingInstance!!.mArrayCards[mCurrentPosition])
                    else
                        swipeRightLeft(1, mLandingInstance!!.mArrayCards[mCurrentPosition])

                    mCurrentPosition++

                    mLandingInstance!!.mArrayTempCards.removeAt(0)

                    if (mLandingInstance!!.mArrayTempCards.size == 0)
                        checkVisibility()

                    if (mLandingInstance!!.mArrayTempCards.size - CARDAPICOUNT == 5) { ///Paging
                        mOffset++
                        hitAPI(false)
                    }
                } else {
                    csvUsers.reverse()
                    mLandingInstance?.showInternetAlert(csvUsers)
                }
            }

            override fun onCardReversed() {

            }

            override fun onCardMovedToOrigin() {

            }

            override fun onCardClicked(index: Int) {
                openProfile(mLandingInstance!!.mArrayCards[index],
                        csvUsers.topView.contentContainer.imgAvatarCard)
            }

        })
    }

    private fun checkVisibility() {
        if (mLandingInstance!!.mArrayTempCards.isEmpty()) {
            llOutOfCards.visibility = View.VISIBLE
        } else {
            llOutOfCards.visibility = View.GONE
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
                if (response.body().response != null) {
                    populateData(response.body())
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        mLandingInstance!!.moveToSplash()
                    } else {
                        mLandingInstance!!.showAlert(llMainHomeFrag, response.body().error!!.message!!)
                    }
                }
                if (visibleLoader)
                    mLandingInstance!!.dismissLoader()
            }

            override fun onFailure(call: Call<CardModel>?, t: Throwable?) {
                if (visibleLoader)
                    mLandingInstance!!.dismissLoader()
                mLandingInstance!!.showAlert(llMainHomeFrag, t!!.localizedMessage)
            }
        })
    }

    private fun populateData(response: CardModel) {
        if (llMainHomeFrag != null) {
            if (mOffset == 1) {
                mLandingInstance!!.mArrayCards.clear()
                mLandingInstance!!.mArrayTempCards.clear()

                mLandingInstance!!.mArrayCards.addAll(response.response)
                mLandingInstance!!.mArrayTempCards.addAll(response.response)

                mAdapterCards = HomeCardSwipeAdapter(mContext!!, 0, mLandingInstance!!.mArrayCards)
                csvUsers.setAdapter(mAdapterCards)

//                addCardWithAnimation(response.response)
            } else {
                if (response.response.isNotEmpty()) {
                    mLandingInstance!!.mArrayCards.addAll(response.response)
                    mLandingInstance!!.mArrayTempCards.addAll(response.response)
                    csvUsers.setPaginationReserved()
                    mAdapterCards!!.notifyDataSetChanged()
                }
            }
            checkVisibility()
        }
    }

    private fun addCardWithAnimation(response: MutableList<CardsDisplayModel>) {
        var count = 0
        mRunnable = Runnable {
            if (count < response.size) {
                mLandingInstance!!.mArrayCards.add(response[count])
                mLandingInstance!!.mArrayTempCards.add(response[count])
                mAdapterCards!!.notifyDataSetChanged()
                count++
                mHandler!!.postDelayed(mRunnable, 300)
            } else {
                mHandler!!.removeCallbacks(mRunnable)
            }
        }
        mHandler!!.postDelayed(mRunnable, 0)
    }

    private fun displayCards() {
        mAdapterCards = HomeCardSwipeAdapter(mContext!!, 0, mLandingInstance!!.mArrayCards)
        csvUsers.setAdapter(mAdapterCards)
        checkVisibility()
    }

    private fun initListener() {
        imgPreferHome.setOnClickListener(this)
        imgProfileHome.setOnClickListener(this)
        txtGetCards.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val intent: Intent?
        when (view) {
            txtGetCards -> {
                if (mLandingInstance!!.connectedToInternet()) {
                    mOffset = 1
                    mCurrentPosition = 0
                    hitAPI(true)
                } else {
                    mLandingInstance!!.showInternetAlert(llMainHomeFrag)
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
        if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTOR) {
            if (mLandingInstance!!.mArrayCards.size == 0) {
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
                        mLandingInstance!!.showToast(mContext!!, response.body().error!!.message!!)
                        mLandingInstance!!.moveToSplash()
                    }
                }
            }

            override fun onFailure(call: Call<SwipeCardModel>?, t: Throwable?) {
                mLandingInstance!!.showAlert(llHomeToolbar, t!!.localizedMessage)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PREFERENCES -> {
                    mCurrentPosition = 0
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

        Picasso.with(mContext).load(mLandingInstance!!.userData!!.response.avatar.avtar_url)
                .into(imgCenterAvatar)
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
        if (llMainHomeFrag != null) {
            if (isConnected) {
                // Todo set Adapter here
            }
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
            mUtils!!.setString("user_type", mLandingInstance!!.userData!!.response.user_type.toString())
            mLandingInstance!!.checkUserType()
            populateData()
            if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTEE)
                txtTitleHome.text = getString(R.string.mentors)
            else
                txtTitleHome.text = getString(R.string.mentees)
        }
    }

    fun resetData() {
        mOffset = 1
        mCurrentPosition = 0
        mAdapterCards = HomeCardSwipeAdapter(mContext!!, 0, mLandingInstance!!.mArrayCards)
        csvUsers.setAdapter(mAdapterCards)
        checkVisibility()
    }

    /// Hunny Code
    internal fun createNewChat(othetUser: CardsDisplayModel, response: SignupModel.ResponseBean) {
        val mParticpantIDSList = ArrayList<String>()
        mParticpantIDSList.add(othetUser!!.id.toString() + "_" + othetUser!!.user_type)
        mParticpantIDSList.add(mLandingInstance!!.userData!!.response.id.toString() + "_" + mLandingInstance!!.userData!!.response.user_type)
        Collections.sort(mParticpantIDSList)
        var mParticpantIDS = "" + mParticpantIDSList
        var participants = mParticpantIDS.substring(1, mParticpantIDS.length - 1)
        val mChat = ChatsModel()
        mChat.chat_dialog_id = participants
        mChat.last_message = Constants.DEFAULT_MESSAGE_REGEX

        val lastTime = HashMap<String, Long>()
        lastTime.put(mLandingInstance!!.userData!!.response.id.toString(), 0L)
        lastTime.put(othetUser.id.toString(), 0L)
        mChat.last_message_time = lastTime

        mChat.last_message_sender_id = mLandingInstance!!.userData!!.response.id.toString()
        mChat.last_message_id = "0"
        mChat.last_message_type = "0"
        mChat.participant_ids = participants
        val unread = HashMap<String, Int>()
        unread.put(mLandingInstance!!.userData!!.response.id.toString(), 0)
        unread.put(othetUser.id.toString(), 0)
        mChat.unread_count = unread

        val name = HashMap<String, String>()
        name.put(mLandingInstance!!.userData!!.response.id.toString(), mLandingInstance!!.userData!!.response.full_name)
        name.put(othetUser.id.toString(), othetUser.full_name)
        mChat.name = name

        val pic = HashMap<String, String>()
        pic.put(mLandingInstance!!.userData!!.response.id.toString(), mLandingInstance!!.userData!!.response.avatar.avtar_url)
        pic.put(othetUser.id.toString(), othetUser.avatar.avtar_url)
        mChat.profile_pic = pic

// val time = ServerValue.TIMESTAMP
        val utcTime = Constants.getUtcTime(Calendar.getInstance().timeInMillis)
        val deleteTime = HashMap<String, Long>()
        deleteTime.put(mLandingInstance!!.userData!!.response.id.toString(), utcTime)
        deleteTime.put(othetUser.id.toString(), utcTime)
        mChat.delete_dialog_time = deleteTime

        val block = HashMap<String, String>()
        block.put(mLandingInstance!!.userData!!.response.id.toString(), "0")
        block.put(othetUser.id.toString(), "0")
        mChat.block_status = block

        val userType = HashMap<String, String>()
        userType.put(mLandingInstance!!.userData!!.response.id.toString(), mLandingInstance!!.userData!!.response.user_type.toString())
        userType.put(othetUser.id.toString(), othetUser.user_type.toString())
        mChat.user_type = userType

        val mFirebaseConfig = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS)
        mFirebaseConfig.child(participants).setValue(mChat).addOnSuccessListener {
            mChat.opponent_user_id = othetUser.id.toString()
            mDb!!.addNewChat(mChat, mLandingInstance!!.userData!!.response.id.toString(), othetUser.id.toString())
            var mFirebaseConfigChat: DatabaseReference
            mFirebaseConfigChat = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS)
            mFirebaseConfigChat.child(participants).child("delete_dialog_time").child(mLandingInstance!!.userData!!.response.id.toString())
                    .setValue(ServerValue.TIMESTAMP)
            mFirebaseConfigChat.child(participants).child("delete_dialog_time").child(othetUser.id.toString())
                    .setValue(ServerValue.TIMESTAMP)

            var mFirebaseConfigUser: DatabaseReference
            mFirebaseConfigUser = FirebaseDatabase.getInstance().getReference().child(Constants.USERS)
            mFirebaseConfigUser.child("id_" + othetUser.id).child("chat_dialog_ids").child(participants)
                    .setValue(participants)
            mFirebaseConfigUser.child("id_" + mLandingInstance!!.userData!!.response.id.toString()).child("chat_dialog_ids").child(participants)
                    .setValue(participants)

            val intent = Intent(mContext!!, HandshakeActivity::class.java)
            intent.putExtra("otherProfileData", response)
            startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }.addOnFailureListener {

        }
    }
    ///
}