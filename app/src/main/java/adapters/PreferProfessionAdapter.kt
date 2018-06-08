package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_profession.view.*
import models.ProfessionModel

class PreferProfessionAdapter(mConetxt: Context, mProfessionArray: ArrayList<ProfessionModel>) : RecyclerView.Adapter<PreferProfessionAdapter.ViewHolder>() {

    var mProfessionArray = ArrayList<ProfessionModel>()
    var mContext: Context? = null

    init {
        this.mProfessionArray = mProfessionArray
        this.mContext = mConetxt
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_profession, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtProfessionData.text = mProfessionArray[position].name

        if (mProfessionArray[position].isSelected)
            holder.imgSelectedProfession.visibility = View.VISIBLE
        else
            holder.imgSelectedProfession.visibility = View.INVISIBLE

        holder.txtProfessionData.setOnClickListener {
            if (mProfessionArray[position].isSelected) {
                holder.imgSelectedProfession.visibility = View.INVISIBLE
                mProfessionArray[position].isSelected = false
            } else {
                holder.imgSelectedProfession.visibility = View.VISIBLE
                mProfessionArray[position].isSelected = true
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = mProfessionArray.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProfessionData = itemView.txtProfessionData
        val imgSelectedProfession = itemView.imgSelectedProfession
    }
}