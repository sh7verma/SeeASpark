package fragments

import adapters.HomeCardsAdapter
import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.seeaspark.LandingActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_home.*
import models.CardModel
import utils.Connection_Detector
import utils.Constants

class HomeFragment : Fragment(), View.OnClickListener {

    var mLandingInstance: LandingActivity? = null
    var itemView: View? = null
    var mContext: Context? = null
    var mAdapterCards: HomeCardsAdapter? = null
    private var mArrayCards= ArrayList<CardModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_home, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mContext = activity
        mLandingInstance = activity as LandingActivity
        onCreateStuff()
        displayLightModeUI()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun displayLightModeUI() {
        llHomeToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white_color))
        llCardBase.setBackgroundColor(ContextCompat.getColor(activity, R.color.background))
    }

    private fun onCreateStuff() {
        if (mLandingInstance!!.userData!!.response.user_type == Constants.MENTEE)
            txtTitleHome.text = getString(R.string.mentors)
        else
            txtTitleHome.text = getString(R.string.mentees)

        rvCards.layoutManager = LinearLayoutManager(mContext)

        if (mLandingInstance!!.connectedToInternet()) {
            hitAPI()
        } else {
            mLandingInstance!!.showInternetAlert(llHomeToolbar)
        }
    }

    private fun hitAPI() {
        mAdapterCards = HomeCardsAdapter(mArrayCards, mContext!!)
        rvCards.adapter = mAdapterCards
    }

    private fun initListener() {
        imgPreferHome.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            imgPreferHome -> {

            }
        }
    }


}