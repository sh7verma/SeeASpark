package com.seeaspark

import adapters.TipsAdapter
import android.Manifest
import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import fragments.CommunityFragment
import fragments.EventsFragment
import fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_landing.*
import kotlinx.android.synthetic.main.activity_verify_id.*
import models.BaseSuccessModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.GpsStatusDetector

class LandingActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks,
        LocationListener, GpsStatusDetector.GpsStatusDetectorCallBack, GoogleApiClient.OnConnectionFailedListener {

    private val LOCATION_CHECK: Int = 1

    var userData: SignupModel? = null

    private var currentPosition = 0
    private var mGoogleApiClient: GoogleApiClient? = null
    var mLatitude = 0.0
    var mLongitude = 0.0

    private var mLocationRequest: LocationRequest? = null
    private var mGpsStatusDetector: GpsStatusDetector? = null

    private var width = 0
    private var height = 0

    val homeFragment = HomeFragment()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {
        if (ContextCompat.checkSelfPermission(mContext!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_CHECK)
        } else {
            buildGoogleApiClient()
            mGpsStatusDetector = GpsStatusDetector(this)
            mGpsStatusDetector!!.checkGpsStatus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llBottomNavigation.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        llHome.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        llNotes.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        llChat.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        llEvents.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        llCommunity.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        homeFragment.resetData()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llBottomNavigation.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        llHome.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        llNotes.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        llChat.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        llEvents.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        llCommunity.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        homeFragment.resetData()
    }

    override fun onCreateStuff() {

        if (intent.hasExtra("matchData")) {
            val matchData: String = intent.getStringExtra("matchData")
            intent = Intent(this, HandshakeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            intent.putExtra("matchData", matchData)
            startActivity(intent)
        }

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        var drawable = ContextCompat.getDrawable(mContext!!, R.mipmap.ic_ava_ob)

        width = drawable!!.intrinsicWidth
        height = drawable.intrinsicHeight

        Picasso.with(mContext).load(userData!!.response.avatar)
                .resize(width, height).into(imgProfileTip)

        if (userData!!.response.user_type == Constants.MENTEE) {
            imgCommunity.setImageResource(R.mipmap.ic_boost)
            imgCommunityTips.setImageResource(R.mipmap.ic_boost_s)
        }
        /// adding home fragment
        addHomeFragment(homeFragment)

        if (mUtils!!.getString("tipsVisible", "") == "0")
            displayTipsData()
        else
            imgHome.setImageResource(R.mipmap.ic_home_s)
    }

    override fun initListener() {
        imgNextTips.setOnClickListener(this)
        llHome.setOnClickListener(this)
        llNotes.setOnClickListener(this)
        llChat.setOnClickListener(this)
        llEvents.setOnClickListener(this)
        llCommunity.setOnClickListener(this)
        rlMainTips.setOnClickListener(this)
        cpIndicatorTips.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_landing

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            llHome -> {
                imgHome.setImageResource(R.mipmap.ic_home_s)
                imgEvents.setImageResource(R.mipmap.ic_events)
                imgCommunity.setImageResource(R.mipmap.ic_community)
                replaceFragment(homeFragment)
            }
            llNotes -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llChat -> {
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
            llEvents -> {
                imgHome.setImageResource(R.mipmap.ic_home)
                imgEvents.setImageResource(R.mipmap.ic_events_s)
                imgCommunity.setImageResource(R.mipmap.ic_community)
                replaceFragment(EventsFragment())
            }
            llCommunity -> {
                imgHome.setImageResource(R.mipmap.ic_home)
                imgEvents.setImageResource(R.mipmap.ic_events)
                imgCommunity.setImageResource(R.mipmap.ic_community_s)
                replaceFragment(CommunityFragment())
            }
            imgNextTips -> {
                if (currentPosition < 5) {
                    currentPosition++
                    vpTips.currentItem = currentPosition
                } else {
                    if (connectedToInternet()) {
                        mUtils!!.setString("tipsVisible", "1")
                        hitTipsAPI()
                        rlMainTips.visibility = View.GONE
                        imgHome.setImageResource(R.mipmap.ic_home_s)
                    } else
                        showInternetAlert(llCommunity)
                }

                when (currentPosition) {
                    1 -> {
                        imgProfileTip.visibility = View.INVISIBLE
                        imgHomeTips.visibility = View.VISIBLE
                    }
                    2 -> {
                        imgHomeTips.visibility = View.INVISIBLE
                        imgNotesTips.visibility = View.VISIBLE
                    }
                    3 -> {
                        imgNotesTips.visibility = View.INVISIBLE
                        imgChatTips.visibility = View.VISIBLE
                    }
                    4 -> {
                        imgChatTips.visibility = View.INVISIBLE
                        imgEventsTips.visibility = View.VISIBLE
                    }
                    5 -> {
                        imgEventsTips.visibility = View.INVISIBLE
                        imgCommunityTips.visibility = View.VISIBLE
                        imgNextTips.setImageResource(R.mipmap.ic_ob_done)
                    }
                }
            }
        }
    }

    private fun replaceFragment(eventsFragment: Fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.llFragment, eventsFragment, null)
                .addToBackStack(null).commit()
    }

    private fun hitTipsAPI() {
        val call = RetrofitClient.getInstance().skipTip(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                if (response.body() != null) {

                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(llCancelDone, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable) {
                showAlert(llCancelDone, t.localizedMessage)
            }
        })
    }

    private fun addHomeFragment(fragment: HomeFragment) {
        fragmentManager.beginTransaction().replace(R.id.llFragment, fragment, null).commit()
    }

    private fun displayTipsData() {

        rlMainTips.visibility = View.VISIBLE

        val iconArray = intArrayOf(R.mipmap.ic_ava_ob,
                R.mipmap.ic_home_big,
                R.mipmap.ic_notes_big,
                R.mipmap.ic_chat_big,
                R.mipmap.ic_events_big,
                R.mipmap.ic_community_big)

        val titleArray = arrayListOf<String>(getString(R.string.Profile), getString(R.string.home), getString(R.string.notes),
                getString(R.string.handshakes), getString(R.string.events), getString(R.string.community))

        val descArray = arrayListOf<String>(getString(R.string.tips_profile),
                getString(R.string.tips_home),
                getString(R.string.tips_notes),
                getString(R.string.tips_chat),
                getString(R.string.tips_events),
                getString(R.string.tips_community))

        val iconArrayMentee = intArrayOf(R.mipmap.ic_ava_ob,
                R.mipmap.ic_home_big,
                R.mipmap.ic_notes_big,
                R.mipmap.ic_chat_big,
                R.mipmap.ic_events_big,
                R.mipmap.ic_boost_big)

        val titleArrayMentee = arrayListOf<String>(getString(R.string.Profile), getString(R.string.home), getString(R.string.notes),
                getString(R.string.handshakes), getString(R.string.events), getString(R.string.boost))

        val descArrayMentee = arrayListOf<String>(getString(R.string.tips_profile),
                getString(R.string.tips_home),
                getString(R.string.tips_notes),
                getString(R.string.tips_chat),
                getString(R.string.tips_events),
                getString(R.string.tips_boost))

        if (userData!!.response.user_type == Constants.MENTOR)
            vpTips.adapter = TipsAdapter(iconArray, titleArray, descArray, mContext!!, userData!!.response.avatar)
        else
            vpTips.adapter = TipsAdapter(iconArrayMentee, titleArrayMentee, descArrayMentee, mContext!!, userData!!.response.avatar)

        cpIndicatorTips.setViewPager(vpTips)
        cpIndicatorTips.radius = 10f
        cpIndicatorTips.fillColor = Color.WHITE
        cpIndicatorTips.pageColor = ContextCompat.getColor(this, R.color.pagerColorCode)
    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(mContext!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(mContext!!,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient!!.isConnected)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    @Suppress("DEPRECATION")
    override fun onLocationChanged(location: Location?) {
        //stop location updates
        if (location != null) {
            mLatitude = location.latitude
            mLongitude = location.longitude
            if (connectedToInternet()) {
                if (intent.hasExtra("matchData"))
                    homeFragment.hitAPI(false)
                else
                    homeFragment.hitAPI(true)
            } else {
                showInternetAlert(llCommunity)
            }
        } else {
            showAlert(llCommunity, getString(R.string.unable_location))
        }

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
        }
    }

    override fun onGpsSettingStatus(enabled: Boolean) {
        if (enabled)
            buildGoogleApiClient()

    }

    override fun onGpsAlertCanceledByUser() {

    }

    override fun onConnectionFailed(result: ConnectionResult) {
        showAlert(llCommunity, result.errorMessage!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (ContextCompat.checkSelfPermission(mContext!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                mGpsStatusDetector!!.checkOnActivityResult(requestCode, resultCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_CHECK -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(mContext!!,
                                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleApiClient == null) {
                        buildGoogleApiClient()
                        mGpsStatusDetector = GpsStatusDetector(this)
                        mGpsStatusDetector!!.checkGpsStatus()

                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}