package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.like.LikeButton
import com.like.OnLikeListener
import com.seeaspark.EventsBookmarkActivity
import com.seeaspark.R
import com.seeaspark.SearchEventCommunityActivity
import com.squareup.picasso.Picasso
import fragments.EventsFragment
import kotlinx.android.synthetic.main.item_events.view.*
import models.PostModel
import utils.Connection_Detector
import utils.Constants
import utils.Utils


class EventsAdapter(mContext: Context?, mEventsArray: ArrayList<PostModel.ResponseBean>, mEventFragment: EventsFragment?)
    : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    var mEventsArray = ArrayList<PostModel.ResponseBean>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    private var mEventFragment: EventsFragment? = null
    private var mSearchInstance: SearchEventCommunityActivity? = null
    private var mBookmarkInstance: EventsBookmarkActivity? = null
    private var mWidth = 0

    init {
        this.mEventsArray = mEventsArray
        this.mContext = mContext
        mUtils = Utils(mContext)
        mWidth = mUtils!!.getInt("width", 0)
        mWidth -= (mWidth / 9)
        this.mEventFragment = mEventFragment
    }

    constructor(mContext: Context?, mEventsArray: ArrayList<PostModel.ResponseBean>, mEventFragment: EventsFragment?, mSearchInstance: SearchEventCommunityActivity)
            : this(mContext, mEventsArray, mEventFragment) {
        this.mSearchInstance = mSearchInstance
    }

    constructor(mContext: Context?, mEventsArray: ArrayList<PostModel.ResponseBean>, mEventFragment: EventsFragment?, mBookmarkInstance: EventsBookmarkActivity)
            : this(mContext, mEventsArray, mEventFragment) {
        this.mBookmarkInstance = mBookmarkInstance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_events, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Picasso.with(mContext).load(mEventsArray[position].images[0].thumbnail_url)
                .resize(mWidth, mContext!!.resources.getDimension(R.dimen._140sdp).toInt())
                .centerCrop().into(holder.imgEventsListing)

        holder.txtTitleEventsListing.text = mEventsArray[position].title
        holder.txtDescEventsListing.text = mEventsArray[position].description
        holder.txtLocationEventsListing.text = mEventsArray[position].address
        holder.txtTimeEventListing.text = Constants.displayDateTime(mEventsArray[position].date_time)

        holder.imgLikeEventsListing.isLiked = mEventsArray[position].liked == 1

        if (mEventsArray[position].bookmarked == 1)
            holder.imgBookmarkEventsListing.setImageResource(R.mipmap.ic_bookmark_white)
        else
            holder.imgBookmarkEventsListing.setImageResource(R.mipmap.ic_bookmark_border)

        holder.txtLikeCountEventsListing.text = "${mEventsArray[position].like} LIKE(S)"

        holder.txtCommentCountEventsListing.text = "${mEventsArray[position].comment} COMMENT(S)"

        holder.imgBookmarkEventsListing.setOnClickListener {
            if ((Connection_Detector(mContext).isConnectingToInternet)) {
                if (mEventsArray[position].bookmarked == 1) {
                    mEventsArray[position].bookmarked = 0
                    when {
                        mEventFragment != null -> mEventFragment!!.updateBookmarkStatus(mEventsArray[position].bookmarked,
                                mEventsArray[position].id)
                        mSearchInstance != null -> mSearchInstance!!.updateBookmarkStatus(mEventsArray[position].bookmarked,
                                mEventsArray[position].id)
                        mBookmarkInstance != null -> mBookmarkInstance!!.updateBookmarkStatus(mEventsArray[position].bookmarked,
                                mEventsArray[position].id, mEventsArray[position])
                    }
                } else {
                    mEventsArray[position].bookmarked = 1
                    when {
                        mEventFragment != null -> mEventFragment!!.updateBookmarkStatus(mEventsArray[position].bookmarked,
                                mEventsArray[position].id)
                        mSearchInstance != null -> mSearchInstance!!.updateBookmarkStatus(mEventsArray[position].bookmarked,
                                mEventsArray[position].id)
                    }
                }
                notifyDataSetChanged()
            }
        }

        holder.imgLikeEventsListing.setOnLikeListener(object : OnLikeListener {
            override fun liked(p0: LikeButton?) {
                if ((Connection_Detector(mContext).isConnectingToInternet)) {
                    mEventsArray[position].liked = 1
                    mEventsArray[position].like++
                    notifyDataSetChanged()
                    when {
                        mEventFragment != null -> mEventFragment!!.updateLikeStatus(mEventsArray[position].liked,
                                mEventsArray[position].id, mEventsArray[position].like)
                        mSearchInstance != null -> mSearchInstance!!.updateLikeStatus(mEventsArray[position].liked,
                                mEventsArray[position].id, mEventsArray[position].like)
                        mBookmarkInstance != null -> mBookmarkInstance!!.updateLikeStatus(mEventsArray[position].liked,
                                mEventsArray[position].id, mEventsArray[position].like)
                    }
                } else {
                    holder.imgLikeEventsListing.isLiked = false
                }
            }

            override fun unLiked(p0: LikeButton?) {
                if ((Connection_Detector(mContext).isConnectingToInternet)) {
                    mEventsArray[position].liked = 0
                    mEventsArray[position].like--
                    notifyDataSetChanged()
                    when {
                        mEventFragment != null -> mEventFragment!!.updateLikeStatus(mEventsArray[position].liked,
                                mEventsArray[position].id, mEventsArray[position].like)
                        mSearchInstance != null -> mSearchInstance!!.updateLikeStatus(mEventsArray[position].liked,
                                mEventsArray[position].id, mEventsArray[position].like)
                        mBookmarkInstance != null -> mBookmarkInstance!!.updateLikeStatus(mEventsArray[position].liked,
                                mEventsArray[position].id, mEventsArray[position].like)
                    }
                } else {
                    holder.imgLikeEventsListing.isLiked = true
                }
            }
        })

        holder.cvEventsListing.setOnClickListener {
            when {
                mEventFragment != null -> mEventFragment!!.moveToEventDetail(mEventsArray[position].id)
                mSearchInstance != null -> mSearchInstance!!.moveToEventDetail(mEventsArray[position].id)
                mBookmarkInstance != null -> mBookmarkInstance!!.moveToEventDetail(mEventsArray[position].id)
            }
        }
    }

    override fun getItemCount(): Int {
        return mEventsArray.size
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
        var imgLikeEventsListing = itemView.imgLikeEventsListing!!

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