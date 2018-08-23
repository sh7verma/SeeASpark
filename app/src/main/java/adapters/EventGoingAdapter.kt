package adapters

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_going_user.view.*
import models.EventUserModel
import models.PostModel
import utils.Utils

class EventGoingAdapter(mEventUserArray: ArrayList<PostModel.ResponseBean.GoingUserBean>, mContext: Context) : RecyclerView.Adapter<EventGoingAdapter.ViewHolder>() {

    var mEventUserArray = ArrayList<PostModel.ResponseBean.GoingUserBean>()
    var mContext: Context? = null
    var mUtils: Utils? = null

    init {
        this.mEventUserArray = mEventUserArray
        this.mContext = mContext
        mUtils = Utils(mContext)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_going_user, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtNameEventGoing.text = mEventUserArray[position].full_name
        Picasso.with(mContext).load(mEventUserArray[position].avatar).into(holder.imgEventGoingListing)
    }

    override fun getItemCount(): Int {
        return mEventUserArray.size
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llClickEventGoing = itemView.llClickEventGoing!!
        val imgEventGoingListing = itemView.imgEventGoingListing!!
        val txtNameEventGoing = itemView.txtNameEventGoing!!

        init {
            if (mUtils!!.getInt("nightMode", 0) == 1)
                displayNightMode()
            else
                displayDayMode()
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        private fun displayNightMode() {
            llClickEventGoing.background = ContextCompat.getDrawable(mContext!!, R.drawable.black_ripple)
            txtNameEventGoing.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        private fun displayDayMode() {
            llClickEventGoing.background = ContextCompat.getDrawable(mContext!!, R.drawable.white_ripple)
            txtNameEventGoing.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        }
    }
}