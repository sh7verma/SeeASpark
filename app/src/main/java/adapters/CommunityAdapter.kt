package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import fragments.CommunityFragment
import kotlinx.android.synthetic.main.item_community.view.*
import kotlinx.android.synthetic.main.item_events.view.*
import models.CommunityModel
import utils.Utils

class CommunityAdapter(mCommunityArray: ArrayList<CommunityModel>, mContext: Context, mCommunityFragment: CommunityFragment?)
    : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {

    var mCommunityArray = ArrayList<CommunityModel>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    var mCommunityFragment: CommunityFragment? = null

    init {
        this.mCommunityArray = mCommunityArray
        this.mContext = mContext
        mUtils = Utils(mContext)
        this.mCommunityFragment = mCommunityFragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_community, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cvCommunityListing.setOnClickListener {
            mCommunityFragment!!.moveToEventDetail()
        }
    }

    override fun getItemCount(): Int {
        return 10
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCommunityListing = itemView.cvCommunityListing!!
        val imgCommunityListingBookmark = itemView.imgCommunityListingBookmark!!
        val imgCommunity = itemView.imgCommunityHome!!
        val txtCommunityTitle = itemView.txtCommunityTitle!!
        val txtDateCommunity = itemView.txtDateCommunity!!
        val txtCommunityDesc = itemView.txtCommunityDesc!!
        val txtCenterOption = itemView.txtCenterOption!!
        var txtLikeCountCommunityListing = itemView.txtLikeCountCommunityListing!!
        var txtCommentCountCommunityListing = itemView.txtCommentCountCommunityListing!!
        var llCommunityLikesComments = itemView.llCommunityLikesComments!!


        init {
            llCommunityLikesComments.visibility = View.VISIBLE
            imgCommunityListingBookmark.visibility=View.VISIBLE
            if (mUtils!!.getInt("nightMode", 0) == 1)
                displayNightMode()
            else
                displayDayMode()
        }

        private fun displayDayMode() {
            cvCommunityListing.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtCommunityTitle.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            txtCommunityDesc.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            txtLikeCountCommunityListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            txtCommentCountCommunityListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        }

        private fun displayNightMode() {
            cvCommunityListing.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.cardview_dark_background))
            txtCommunityTitle.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtCommunityDesc.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtLikeCountCommunityListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtCommentCountCommunityListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        }

    }

}