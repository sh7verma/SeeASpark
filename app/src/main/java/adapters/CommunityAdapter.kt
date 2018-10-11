package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.like.LikeButton
import com.like.OnLikeListener
import com.seeaspark.CommunityBookmarkActivity
import com.seeaspark.R
import com.seeaspark.SearchActivity
import com.squareup.picasso.Picasso
import fragments.CommunityFragment
import kotlinx.android.synthetic.main.item_community.view.*
import kotlinx.android.synthetic.main.item_progress.view.*
import models.PostModel
import utils.Connection_Detector
import utils.Constants
import utils.Utils

class CommunityAdapter(mCommunityArray: ArrayList<PostModel.ResponseBean>,
                       mContext: Context,
                       mCommunityFragment: CommunityFragment?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mCommunityArray = ArrayList<PostModel.ResponseBean>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    private var mCommunityFragment: CommunityFragment? = null
    private var mSearchInstance: SearchActivity? = null
    private var mBookmarkInstance: CommunityBookmarkActivity? = null
    private var mWidth = 0

    init {
        this.mCommunityArray = mCommunityArray
        this.mContext = mContext
        mUtils = Utils(mContext)
        mWidth = mUtils!!.getInt("width", 0)
        mWidth -= (mWidth / 9)
        this.mCommunityFragment = mCommunityFragment
    }

    constructor(mCommunityArray: ArrayList<PostModel.ResponseBean>,
                mContext: Context,
                mSearchInstance: SearchActivity,
                mCommunityFragment: CommunityFragment?)
            : this(mCommunityArray, mContext, mCommunityFragment) {
        this.mSearchInstance = mSearchInstance
    }

    constructor(mCommunityArray: ArrayList<PostModel.ResponseBean>, mContext: Context, mBookmarkInstance: CommunityBookmarkActivity, mCommunityFragment: CommunityFragment?)
            : this(mCommunityArray, mContext, mCommunityFragment) {
        this.mBookmarkInstance = mBookmarkInstance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            Constants.COMMUNITY -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_community, parent, false)
                PostViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
                LoadMoreViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (mCommunityArray[position].post_type) {
            Constants.COMMUNITY -> {
                (holder as CommunityAdapter.PostViewHolder)

                if (mCommunityArray[position].images.isNotEmpty())
                    Picasso.with(mContext).load(mCommunityArray[position].images[0].thumbnail_url)
                            .resize(mWidth, mContext!!.resources.getDimension(R.dimen._160sdp).toInt())
                            .centerCrop().into(holder.imgCommunityListing)

                holder.txtCommunityTitle.text = mCommunityArray[position].title
                holder.txtCommunityDesc.text = mCommunityArray[position].description
                holder.txtCenterOption.text = mCommunityArray[position].profession_id

                if (mCommunityArray[position].date_time.isNotEmpty())
                    holder.txtDateCommunity.text = Constants.displayDateTime(mCommunityArray[position].date_time)

                holder.imgLikeCommunityListing.isLiked = mCommunityArray[position].liked == 1

                if (mCommunityArray[position].bookmarked == 1)
                    holder.imgCommunityListingBookmark.setImageResource(R.mipmap.ic_bookmark_white)
                else
                    holder.imgCommunityListingBookmark.setImageResource(R.mipmap.ic_bookmark_border)

                holder.txtLikeCountCommunityListing.text = "${mCommunityArray[position].like} LIKE(S)"

                holder.txtCommentCountCommunityListing.text = "${mCommunityArray[position].comment} COMMENT(S)"


                holder.imgCommunityListingBookmark.setOnClickListener {
                    if ((Connection_Detector(mContext).isConnectingToInternet)) {
                        if (mCommunityArray[position].bookmarked == 1) {
                            mCommunityArray[position].bookmarked = 0
                            when {
                                mCommunityFragment != null -> mCommunityFragment!!.updateBookmarkStatus(mCommunityArray[position].bookmarked,
                                        mCommunityArray[position].id)
                                mSearchInstance != null -> mSearchInstance!!.updateBookmarkStatus(mCommunityArray[position].bookmarked,
                                        mCommunityArray[position].id)
                                mBookmarkInstance != null -> mBookmarkInstance!!.updateBookmarkStatus(mCommunityArray[position].bookmarked,
                                        mCommunityArray[position].id, mCommunityArray[position])
                            }
                        } else {
                            mCommunityArray[position].bookmarked = 1
                            when {
                                mCommunityFragment != null -> mCommunityFragment!!.updateBookmarkStatus(mCommunityArray[position].bookmarked,
                                        mCommunityArray[position].id)
                                mSearchInstance != null -> mSearchInstance!!.updateBookmarkStatus(mCommunityArray[position].bookmarked,
                                        mCommunityArray[position].id)
                            }
                        }
                        notifyDataSetChanged()
                    }
                }

                holder.imgLikeCommunityListing.setOnLikeListener(object : OnLikeListener {
                    override fun liked(p0: LikeButton?) {
                        if ((Connection_Detector(mContext).isConnectingToInternet)) {
                            mCommunityArray[position].liked = 1
                            mCommunityArray[position].like++
                            notifyDataSetChanged()
                            when {
                                mCommunityFragment != null -> mCommunityFragment!!.updateLikeStatus(mCommunityArray[position].liked,
                                        mCommunityArray[position].id, mCommunityArray[position].like)
                                mSearchInstance != null -> mSearchInstance!!.updateLikeStatus(mCommunityArray[position].liked,
                                        mCommunityArray[position].id, mCommunityArray[position].like)
                                mBookmarkInstance != null -> mBookmarkInstance!!.updateLikeStatus(mCommunityArray[position].liked,
                                        mCommunityArray[position].id, mCommunityArray[position].like)
                            }
                        } else {
                            holder.imgLikeCommunityListing.isLiked = false
                        }
                    }

                    override fun unLiked(p0: LikeButton?) {
                        if ((Connection_Detector(mContext).isConnectingToInternet)) {
                            mCommunityArray[position].liked = 0
                            mCommunityArray[position].like--
                            notifyDataSetChanged()
                            when {
                                mCommunityFragment != null -> mCommunityFragment!!.updateLikeStatus(mCommunityArray[position].liked,
                                        mCommunityArray[position].id, mCommunityArray[position].like)
                                mSearchInstance != null -> mSearchInstance!!.updateLikeStatus(mCommunityArray[position].liked,
                                        mCommunityArray[position].id, mCommunityArray[position].like)
                                mBookmarkInstance != null -> mBookmarkInstance!!.updateLikeStatus(mCommunityArray[position].liked,
                                        mCommunityArray[position].id, mCommunityArray[position].like)
                            }
                        } else {
                            holder.imgLikeCommunityListing.isLiked = true
                        }
                    }
                })

                holder.cvCommunityListing.setOnClickListener {
                    when {
                        mCommunityFragment != null -> mCommunityFragment!!.moveToCommunityDetail(mCommunityArray[position].id, holder.imgCommunityListing)
                        mSearchInstance != null -> mSearchInstance!!.moveToCommunityDetail(mCommunityArray[position].id, holder.imgCommunityListing)
                        mBookmarkInstance != null -> mBookmarkInstance!!.moveToCommunityDetail(mCommunityArray[position].id, holder.imgCommunityListing)
                    }
                }
            }
            else -> {
                (holder as CommunityAdapter.LoadMoreViewHolder)
            }
        }
    }

    override fun getItemCount(): Int {
        return mCommunityArray.size
    }

    override fun getItemViewType(position: Int): Int {
        when (mCommunityArray[position].post_type) {
            1 -> return Constants.COMMUNITY
            else -> Constants.PROGRESS
        }
        return 0
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar = itemView.progressBar!!
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCommunityListing = itemView.cvCommunityListing!!
        val rlCommunityListing = itemView.rlCommunityListing!!
        val imgCommunityListingBookmark = itemView.imgCommunityListingBookmark!!
        val imgCommunityListing = itemView.imgCommunityListing!!
        val txtCommunityTitle = itemView.txtCommunityTitle!!
        val txtDateCommunity = itemView.txtDateCommunity!!
        val txtCommunityDesc = itemView.txtCommunityDesc!!
        val txtCenterOption = itemView.txtCenterOption!!
        val imgLikeCommunityListing = itemView.imgLikeCommunityListing!!
        var txtLikeCountCommunityListing = itemView.txtLikeCountCommunityListing!!
        var txtCommentCountCommunityListing = itemView.txtCommentCountCommunityListing!!
        var llCommunityLikesComments = itemView.llCommunityLikesComments!!

        init {
            llCommunityLikesComments.visibility = View.VISIBLE
            imgCommunityListingBookmark.visibility = View.VISIBLE
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