package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.squareup.picasso.Picasso
import customviews.CircleTransform
import kotlinx.android.synthetic.main.item_avatar.view.*
import models.SkillsModel


class AvatarAdapter(mSkillsArray: ArrayList<SkillsModel>, mContext: Context) : RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {

    var mSkillsArray = ArrayList<SkillsModel>()
    var mContext: Context? = null

    init {
        this.mSkillsArray = mSkillsArray
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.with(mContext).load(mSkillsArray[position].skill).transform(CircleTransform()).into(holder.imgAvatar)
    }

    override fun getItemCount(): Int {
        return mSkillsArray.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar = itemView.imgAvatar
    }

}