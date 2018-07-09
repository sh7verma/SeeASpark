package fragments

import adapters.EventsAdapter
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_event.*
import models.PostModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.Utils

class EventsFragment : Fragment(), View.OnClickListener {

    var itemView: View? = null
    var mUtils: Utils? = null
    var mContext: Context? = null
    var mLandingInstance: LandingActivity? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var mEventsAdapter: EventsAdapter? = null
    private var mEventsArray = ArrayList<PostModel.ResponseBean>()
    private var mEventFragment: EventsFragment? = null

    private val mOffset: Int = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_event, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mContext = activity
        mEventFragment = this
        mLandingInstance = activity as LandingActivity
        initUI()
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun initUI() {

        txtTitleCustom.text = getString(R.string.Events)

        imgBackCustom.setImageResource(R.mipmap.ic_bookmark)

        imgOption1Custom.setImageResource(R.mipmap.ic_search)
        imgOption1Custom.visibility = View.VISIBLE

        imgOption2Custom.setImageResource(R.mipmap.ic_add_top_bar)
        imgOption2Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)

        rvEventsListing.layoutManager = mLayoutManager

        mUtils = Utils(activity)
        if (mUtils!!.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayDayMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white_color))
        imgBackCustom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
        imgOption1Custom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        rvEventsListing.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        imgBackCustom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        imgOption1Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        rvEventsListing.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
    }

    private fun onCreateStuff() {
        if (mLandingInstance!!.connectedToInternet())
            hitAPI()
        else
            mLandingInstance!!.showInternetAlert(rvEventsListing)
    }

    private fun hitAPI() {
        mLandingInstance!!.showLoader()
        val call = RetrofitClient.getInstance().getPosts(mLandingInstance!!.mUtils!!.getString("access_token", "")
                , Constants.EVENT.toString(), mOffset)
        call.enqueue(object : Callback<PostModel> {

            override fun onResponse(call: Call<PostModel>?, response: Response<PostModel>) {

                if (response.body().response != null) {
                    if (mOffset == 1)
                        addToLocalDatabase(response.body().response)

                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(rvEventsListing, response.body().error!!.message!!)
                }
                mLandingInstance!!.dismissLoader()
            }

            override fun onFailure(call: Call<PostModel>?, t: Throwable?) {
                mLandingInstance!!.dismissLoader()
                mLandingInstance!!.showAlert(rvEventsListing, t!!.localizedMessage)
            }
        })
    }

    private fun addToLocalDatabase(response: List<PostModel.ResponseBean>) {
        for (responseData in response) {
            mLandingInstance!!.db!!.addPosts(responseData)
            for (imagesData in responseData.images) {
                mLandingInstance!!.db!!.addPostImages(imagesData, responseData.id.toString())
            }
            for(goingUserData in responseData.going_list) {
                mLandingInstance!!.db!!.addPostGoingUsers(goingUserData, responseData.id.toString())
            }
        }
        populateData()
    }

    private fun populateData() {
        mEventsArray.addAll(mLandingInstance!!.db!!.getPostsByType(Constants.EVENT))
        mEventsAdapter = EventsAdapter(mContext, mEventsArray, mEventFragment)
        rvEventsListing.adapter = mEventsAdapter
    }

    private fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgOption1Custom.setOnClickListener(this)
        imgOption2Custom.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        var intent: Intent? = null
        when (view) {
            imgBackCustom -> {
                intent = Intent(mContext, EventsBookmarkActivity::class.java)
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
            }
            imgOption1Custom -> {
                intent = Intent(mContext, SearchEventCommunityActivity::class.java)
                intent.putExtra("path", "events")
                startActivity(intent)
            }
            imgOption2Custom -> {
                intent = Intent(mContext, ShareIdeaActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun moveToEventDetail(responseBean: PostModel.ResponseBean) {
        if (mLandingInstance!!.connectedToInternet()) {
            val intent = Intent(mContext, EventsDetailActivity::class.java)
            intent.putExtra("eventData",responseBean)
            startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            mLandingInstance!!.showInternetAlert(rvEventsListing)
    }
}