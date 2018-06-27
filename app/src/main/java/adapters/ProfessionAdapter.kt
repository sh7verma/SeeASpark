package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_profession.view.*
import models.ProfessionModel


class ProfessionAdapter(mConetxt: Context, mProfessionArray: ArrayList<ProfessionModel>, mCreateProfileInstance: CreateProfileActivity) : RecyclerView.Adapter<ProfessionAdapter.ViewHolder>() {

    var mProfessionArray = ArrayList<ProfessionModel>()
    var mContext: Context? = null
    var mCreateProfileInstance: CreateProfileActivity? = null

    init {
        this.mProfessionArray = mProfessionArray
        this.mContext = mConetxt
        this.mCreateProfileInstance = mCreateProfileInstance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_profession, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtProfessionData.text = mProfessionArray[position].name
        holder.txtProfessionData.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))

        if (mProfessionArray[position].id
                == mCreateProfileInstance!!.mProfession)
            holder.imgSelectedProfession.visibility = View.VISIBLE
        else
            holder.imgSelectedProfession.visibility = View.INVISIBLE

        holder.txtProfessionData.setOnClickListener {
            mCreateProfileInstance!!.mProfession = mProfessionArray[position].id
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = mProfessionArray.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProfessionData = itemView.txtProfessionData
        val imgSelectedProfession = itemView.imgSelectedProfession
    }
}