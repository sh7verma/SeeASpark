package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.seeaspark.R
import com.seeaspark.SelectAvatarActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_avatar.view.*
import models.AvatarModel

class SelectAvatarAdapter(mAvatarArray: ArrayList<AvatarModel>, mContext: Context, mSelectAvatar: SelectAvatarActivity) : RecyclerView.Adapter<SelectAvatarAdapter.ViewHolder>() {

    var mAvatarArray = ArrayList<AvatarModel>()
    var mContext: Context? = null
    var mSelectAvatar: SelectAvatarActivity? = null
    var width: Int = 0
    var height: Int = 0
    var layoutParms: RelativeLayout.LayoutParams? = null

    init {
        this.mAvatarArray = mAvatarArray
        this.mContext = mContext
        this.mSelectAvatar = mSelectAvatar
        val drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_avatar_1)
        width = drawable!!.intrinsicWidth
        height = drawable.intrinsicHeight

        layoutParms = RelativeLayout.LayoutParams(width, width)
        layoutParms!!.addRule(RelativeLayout.CENTER_IN_PARENT)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.llSelected.layoutParams = layoutParms
        holder.imgAvatar.layoutParams = layoutParms

        if (mAvatarArray[position].avtar_url == mSelectAvatar!!.mAvatarURL) {
            holder.llSelected.visibility = View.VISIBLE
            holder.imgTick.visibility = View.VISIBLE
        } else {
            holder.llSelected.visibility = View.INVISIBLE
            holder.imgTick.visibility = View.INVISIBLE
        }

        Picasso.with(mContext).load(mAvatarArray[position].avtar_url)
                .placeholder(R.drawable.placeholder_image)
                .resize(width, width).into(holder.imgAvatar)

        Picasso.with(mContext).load(R.drawable.avatar_selected).resize(width, width).into(holder.llSelected)

        holder.imgAvatar.setOnClickListener {
            mSelectAvatar!!.mAvatarURL = mAvatarArray[position].avtar_url
            mSelectAvatar!!.mAvatarName = mAvatarArray[position].name
            mSelectAvatar!!.mAvatarParentId = 0
            mSelectAvatar!!.moveToNext()
            notifyDataSetChanged()
        }

        holder.imgAvatar.setOnLongClickListener {
            val location = IntArray(2)
            holder.imgAvatar.getLocationOnScreen(location);
            mSelectAvatar!!.showAnimation(location[0], location[1], mAvatarArray[position].skins, position, holder.imgAvatar)

            if (!mAvatarArray[position].getIsSelected()) {
                mAvatarArray[position].setIsSelected(true)
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return mAvatarArray.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar = itemView.imgAvatar!!
        val llSelected = itemView.llSelected!!
        val imgTick = itemView.imgTick!!
    }

}