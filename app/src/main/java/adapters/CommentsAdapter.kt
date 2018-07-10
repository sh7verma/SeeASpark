package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CommentsActivity
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_comments.view.*
import models.CommentModel
import utils.Constants
import utils.Utils

class CommentsAdapter(mConetxt: Context, mCommentArray: ArrayList<CommentModel.ResponseBean>, mComments: CommentsActivity) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    var mCommentArray = ArrayList<CommentModel.ResponseBean>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    var mComments: CommentsActivity? = null

    init {
        this.mCommentArray = mCommentArray
        this.mContext = mConetxt
        mUtils = Utils(mContext)
        this.mComments = mComments
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_comments, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.with(mContext).load(mCommentArray[position].avatar).into(holder.imgComments)
        holder.txtNameComments.text = mCommentArray[position].full_name
        holder.txtMessageComments.text = mCommentArray[position].description
        holder.txtTimeComments.text = Constants.displayDateTime(mCommentArray[position].date_time)
    }

    override fun getItemCount() = mCommentArray.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llClickComments = itemView.llClickComments!!
        val imgComments = itemView.imgComments!!
        val txtNameComments = itemView.txtNameComments!!
        val txtMessageComments = itemView.txtMessageComments!!
        val txtTimeComments = itemView.txtTimeComments!!

        init {
            if (mUtils!!.getInt("nightMode", 0) == 1)
                displayNightMode()
            else
                displayDayMode()
        }

        private fun displayNightMode() {
            llClickComments.setBackgroundResource(mComments!!.blackRipple)
            txtNameComments.setTextColor(mComments!!.whiteColor)
            txtMessageComments.setTextColor(mComments!!.whiteColor)
        }

        private fun displayDayMode() {
            llClickComments.setBackgroundResource(mComments!!.whiteRipple)
            txtNameComments.setTextColor(mComments!!.blackColor)
            txtMessageComments.setTextColor(ContextCompat.getColor(mContext!!, R.color.greyTextColor))
        }
    }
}