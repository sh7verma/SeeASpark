package fragments

import adapters.HomeCardsAdapter
import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cocosw.bottomsheet.BottomSheet
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.seeaspark.*
import com.seeaspark.BuildConfig.APPLICATION_ID
import kotlinx.android.synthetic.main.activity_verify_id.*
import kotlinx.android.synthetic.main.fragment_home.*
import models.CardModel
import models.CardsDisplayModel
import models.BaseSuccessModel
import models.SwipeCardModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.Constants.moveToSplash
import utils.GpsStatusDetector
import utils.Utils


class HomeFragment : Fragment(), View.OnClickListener {

    private val PREFERENCES: Int = 2

    var mLandingInstance: LandingActivity? = null
    var itemView: View? = null
    var mContext: Context? = null
    private var mAdapterCards: HomeCardsAdapter? = null
    private var mHomeFragment: HomeFragment? = null
    private var mArrayCards = ArrayList<CardsDisplayModel>()
    private var mUtils: Utils? = null
    private var mOffset = 1

    private var mLayoutManager: LinearLayoutManager? = null
    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0
    private var visibleThreshold: Int = 3
    var isLoading: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_home, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mContext = activity
        mLandingInstance = activity as LandingActivity
        mHomeFragment = this
        displayLightModeUI()
        initListener()
        onCreateStuff()
        super.onActivityCreated(savedInstanceState)
    }

    private fun displayLightModeUI() {
        llHomeToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white_color))
        rlCardBase.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))
    }

    private fun onCreateStuff() {

        mUtils = Utils(mContext)
        if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTEE)
            txtTitleHome.text = getString(R.string.mentors)
        else
            txtTitleHome.text = getString(R.string.mentees)

        mLayoutManager = LinearLayoutManager(mContext)

        rvCards.layoutManager = mLayoutManager

        rvCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = mLayoutManager!!.getItemCount();
                lastVisibleItem = mLayoutManager!!.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mLandingInstance!!.connectedToInternet()) {
                        mOffset++
                        val cardsDisplayModel = CardsDisplayModel()
                        cardsDisplayModel.post_type = Constants.PROGRESS
                        mArrayCards.add(cardsDisplayModel)
                        mAdapterCards!!.notifyItemInserted(mArrayCards.size - 1)
                        hitAPI(false)
                        isLoading = true
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

    }

    fun hitAPI(visibleLoader: Boolean) {
        if (visibleLoader)
            mLandingInstance!!.showLoader()
        val call = RetrofitClient.getInstance().getCards(mUtils!!.getString("access_token", ""),
                mLandingInstance!!.mLatitude.toString(), mLandingInstance!!.mLongitude.toString(), mOffset.toString())
        call.enqueue(object : Callback<CardModel> {

            override fun onResponse(call: Call<CardModel>?, response: Response<CardModel>) {
                if (visibleLoader)
                    mLandingInstance!!.dismissLoader()
                if (response.body().response != null) {
                    if (mOffset == 1)
                        populateData(response.body())
                    else {
                        mArrayCards.removeAt(mArrayCards.size - 1)
                        mAdapterCards!!.notifyItemRemoved(mArrayCards.size)
                        mArrayCards.addAll(response.body().response)
                        mAdapterCards!!.notifyDataSetChanged()
                        isLoading = false
                    }
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(llCancelDone, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<CardModel>?, t: Throwable?) {
                if (visibleLoader)
                    mLandingInstance!!.dismissLoader()
            }
        })
    }

    private fun populateData(response: CardModel) {

        mArrayCards.clear()

        for (cardsValue in response.response) {
            mArrayCards.add(cardsValue)
        }

        for (postValue in response.posts) {
            mArrayCards.add(postValue)
        }

        if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTEE) {
            val cardsDisplayModel = CardsDisplayModel()
            cardsDisplayModel.post_type = 3
            mArrayCards.add(cardsDisplayModel)
        }
        mAdapterCards = HomeCardsAdapter(mArrayCards, mContext!!, mLandingInstance!!.mWidth, mHomeFragment)
        rvCards.adapter = mAdapterCards
    }

    private fun initListener() {
        imgPreferHome.setOnClickListener(this)
        imgProfileHome.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            imgProfileHome -> {
                showAccountOptions()
            }
            imgPreferHome -> {
                val intent = Intent(mContext!!, PreferencesActivity::class.java)
                intent.putExtra("showPrefilled", "yes")
                startActivityForResult(intent, PREFERENCES)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
            }
        }
    }

    private fun showAccountOptions() {
        BottomSheet.Builder(activity, R.style.BottomSheet_Dialog)
                .title("Select Option")
                .sheet(R.menu.menu_account).listener { dialog, which ->
                    when (which) {
                        R.id.item_logout -> {
                            mLandingInstance!!.alertLogoutDialog()
                        }
                        R.id.item_delete_account -> {
                            alertDeleteAccountDialog()
                        }
                    }
                }.show()

    }

    fun openShortProfile(cardsDisplayModel: CardsDisplayModel) {
        val intent = Intent(mContext, ShortProfileDialog::class.java)
        intent.putExtra("otherProfileData", cardsDisplayModel)
        startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_up, 0)
    }

    fun swipeRightLeft(swiped: Int, id: Int) {
        if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTOR) {
            if (mArrayCards.size == 0) {
                rvCards.visibility = View.GONE
                llOutOfCards.visibility = View.VISIBLE
            }
        } else {
            /* var count = 0
             for (cardsData in mArrayCards) {
                 if (cardsData.post_type == 0) {
                     count++
                 }
             }
             /// adding out of cards data in list
             if (count == 0) {
                 val cardsDisplayModel = CardsDisplayModel()
                 cardsDisplayModel.post_type = 3
                 mArrayCards.add(cardsDisplayModel)
                 mAdapterCards!!.notifyDataSetChanged()
             }*/
        }
        hitSwipeAPI(swiped, id)
    }

    private fun hitSwipeAPI(swiped: Int, othetUserId: Int) {
        var call = RetrofitClient.getInstance().swipeCards(mUtils!!.getString("access_token", ""),
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
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(llCancelDone, response.body().error!!.message!!)
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
                    mOffset = 1
                    hitAPI(false)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun alertDeleteAccountDialog() {
        val alertDialog = AlertDialog.Builder(mContext!!)
        alertDialog.setTitle(getString(R.string.delete_account))
        alertDialog.setMessage(getString(R.string.delete_account_msg))
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->

            if (mLandingInstance!!.connectedToInternet()) {
                hitDeleteAccountAPI()
            } else {
                mLandingInstance!!.showInternetAlert(rvCards)
            }
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    private fun hitDeleteAccountAPI() {
        mLandingInstance!!.showLoader()
        val call = RetrofitClient.getInstance().deleteAccount(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                mLandingInstance!!.dismissLoader()
                if (response.body().response != null) {
                    mLandingInstance!!.moveToSplash()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(llCancelDone, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable) {
                mLandingInstance!!.dismissLoader()
                mLandingInstance!!.showAlert(rvCards, t.localizedMessage)
            }
        })
    }
}