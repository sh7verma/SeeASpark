package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import com.seeaspark.AnimationDemo
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import com.squareup.picasso.Picasso
import fragments.AvatarFragment
import kotlinx.android.synthetic.main.item_avatar.view.*
import models.AvatarModel


class AvatarAdapter(mAvatarArray: ArrayList<AvatarModel>, mContext: Context, mCreateProfileInstance: CreateProfileActivity?, mAvatarFragment: AvatarFragment?) : RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {

    var mAvatarArray = ArrayList<AvatarModel>()
    var mContext: Context? = null
    var mCreateProfileInstance: CreateProfileActivity? = null
    var width: Int = 0
    var height: Int = 0
    private var mAvatarFragment: AvatarFragment? = null
    var layoutParms: RelativeLayout.LayoutParams? = null

    init {
        this.mAvatarArray = mAvatarArray
        this.mContext = mContext
        this.mCreateProfileInstance = mCreateProfileInstance
        this.mAvatarFragment = mAvatarFragment

        val drawable = ContextCompat.getDrawable(mContext, R.mipmap.ic_avatar_1)
        width = drawable!!.intrinsicWidth
        height = drawable.intrinsicHeight

        Log.e("Width / height = ", "$width , $height")
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

        if (mAvatarArray[position].name == mCreateProfileInstance!!.mAvatarId) {
            holder.llSelected.visibility = View.VISIBLE
            holder.imgTick.visibility = View.VISIBLE
        } else {
            holder.llSelected.visibility = View.INVISIBLE
            holder.imgTick.visibility = View.INVISIBLE
        }

        if (mAvatarArray[position].getIsSelected()) {
            backToNormal(holder.imgAvatar)
            mAvatarArray[position].setIsSelected(false)
        }

        Picasso.with(mContext).load(mAvatarArray[position].avtar_url).resize(width, width).placeholder(R.drawable.placeholder_image).into(holder.imgAvatar)

        Picasso.with(mContext).load(R.drawable.avatar_selected).resize(width, width).into(holder.llSelected)

        holder.imgAvatar.setOnClickListener {
            val location = IntArray(2)
            holder.imgAvatar.getLocationOnScreen(location);
            mAvatarFragment!!.showAnimation(location[0], location[1], mAvatarArray[position].skins, position, holder.imgAvatar)

            if (!mAvatarArray[position].getIsSelected()) {
                mAvatarArray[position].setIsSelected(true)
            }
        }
    }

    private fun backToNormal(imgAvatar: ImageView) {
        val animationZoomOut = AnimationUtils.loadAnimation(mContext, R.anim.zoom_to_normal)
        imgAvatar.startAnimation(animationZoomOut)
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