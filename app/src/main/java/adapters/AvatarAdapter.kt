package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_avatar.view.*


class AvatarAdapter(mAvatarArray: ArrayList<String>, mContext: Context, mCreateProfileInstance: CreateProfileActivity) : RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {

    var mAvatarArray = ArrayList<String>()
    var mContext: Context? = null
    var mCreateProfileInstance: CreateProfileActivity? = null

    init {
        this.mAvatarArray = mAvatarArray
        this.mContext = mContext
        this.mCreateProfileInstance = mCreateProfileInstance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.with(mContext).load(R.mipmap.ic_avatar_1).into(holder.imgAvatar)
        holder.imgAvatar.setOnClickListener {
            mCreateProfileInstance!!.moveToNext()
        }
    }

    override fun getItemCount(): Int {
        return 15
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar = itemView.imgAvatar
    }

}