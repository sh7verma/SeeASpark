package fragments

import adapters.CommunityAdapter
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
import kotlinx.android.synthetic.main.fragment_community.*
import models.CommunityModel
import utils.Utils

class CommunityFragment : Fragment(), View.OnClickListener {

    var itemView: View? = null
    var mUtils: Utils? = null
    var mContext: Context? = null
    var mLandingInstance: LandingActivity? = null

    private var mLayoutManager: LinearLayoutManager? = null
    private var mCommunityAdapter: CommunityAdapter? = null
    private var mCommunityArray = ArrayList<CommunityModel>()
    private var mCommunityFragment: CommunityFragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_community, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mContext = activity
        mCommunityFragment = this
        mLandingInstance = activity as LandingActivity
        initUI()
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun initUI() {

        txtTitleCustom.text = getString(R.string.community)

        imgBackCustom.setImageResource(R.mipmap.ic_bookmark)

        imgOption1Custom.setImageResource(R.mipmap.ic_search)
        imgOption1Custom.visibility = View.VISIBLE

        imgOption2Custom.setImageResource(R.mipmap.ic_add_top_bar)
        imgOption2Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)

        rvCommunityListing.layoutManager = mLayoutManager

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
        rvCommunityListing.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        imgBackCustom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        imgOption1Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        rvCommunityListing.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
    }

    private fun onCreateStuff() {
        mCommunityAdapter = CommunityAdapter(mCommunityArray, mContext!!, mCommunityFragment)
        rvCommunityListing.adapter = mCommunityAdapter
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
                intent = Intent(mContext, CommunityBookmarkActivity::class.java)
                startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
            }
            imgOption1Custom -> {
                intent = Intent(mContext, SearchEventCommunityActivity::class.java)
                intent.putExtra("path", "community")
                startActivity(intent)
            }
            imgOption2Custom -> {
                intent = Intent(mContext, ShareIdeaActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun moveToEventDetail() {
        if (mLandingInstance!!.connectedToInternet()) {
            val intent = Intent(mContext, CommunityDetailActivity::class.java)
            startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            mLandingInstance!!.showInternetAlert(rvCommunityListing)
    }
}