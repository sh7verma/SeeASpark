package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.EventsBookmarkActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_events.view.*
import models.PostModel
import utils.Utils

class BookmarkEventsAdapter(mContext: Context?, mEventsArray: ArrayList<PostModel.ResponseBean>, mbookmarkEvents: EventsBookmarkActivity?)
    : RecyclerView.Adapter<BookmarkEventsAdapter.ViewHolder>() {

    /* Not in use Currently
   * Created for future purpose onli if there will be any changes */

    var mEventsArray = ArrayList<PostModel.ResponseBean>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    var mbookmarkEvents: EventsBookmarkActivity? = null

    init {
        this.mEventsArray = mEventsArray
        this.mContext = mContext
        mUtils = Utils(mContext)
        this.mbookmarkEvents = mbookmarkEvents
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_events, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cvEventsListing.setOnClickListener {
            mbookmarkEvents!!.moveToEventDetail(mEventsArray[position].id, holder.imgEventsListing)
        }

    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvEventsListing = itemView.cvEventsListing!!
        var imgEventsListing = itemView.imgEventsListing!!
        var imgBookmarkEventsListing = itemView.imgBookmarkEventsListing!!
        var txtTitleEventsListing = itemView.txtTitleEventsListing!!
        var txtDescEventsListing = itemView.txtDescEventsListing!!
        var txtLocationEventsListing = itemView.txtLocationEventsListing!!
        var txtTimeEventListing = itemView.txtTimeEventListing!!
        var txtLikeCountEventsListing = itemView.txtLikeCountEventsListing!!
        var txtCommentCountEventsListing = itemView.txtCommentCountEventsListing!!

        init {
            if (mUtils!!.getInt("nightMode", 0) == 1)
                displayNightMode()
        }

        private fun displayNightMode() {
            cvEventsListing.setCardBackgroundColor(ContextCompat.getColor(mContext!!, R.color.cardview_dark_background))
            txtTitleEventsListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtDescEventsListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtLocationEventsListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtTimeEventListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtLikeCountEventsListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtCommentCountEventsListing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        }
    }

}