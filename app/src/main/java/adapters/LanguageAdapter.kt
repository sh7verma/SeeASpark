package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.seeaspark.SelectLanguageActivity
import kotlinx.android.synthetic.main.item_profession.view.*
import models.LanguageModel


class LanguageAdapter(mConetxt: Context, mLanguageArray: ArrayList<LanguageModel>) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    var mLanguageArray = ArrayList<LanguageModel>()
    var mContext: Context? = null
    var mSelectLanguage:SelectLanguageActivity?=null
    init {
        this.mLanguageArray = mLanguageArray
        this.mContext = mConetxt
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_profession, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtProfessionData.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        holder.txtProfessionData.text = mLanguageArray[position].name

        if (mLanguageArray[position].isSelected)
            holder.imgSelectedProfession.visibility = View.VISIBLE
        else
            holder.imgSelectedProfession.visibility = View.INVISIBLE

        holder.txtProfessionData.setOnClickListener {
            if (mLanguageArray[position].isSelected) {
                holder.imgSelectedProfession.visibility = View.INVISIBLE
                mLanguageArray[position].isSelected = false
            } else {
                holder.imgSelectedProfession.visibility = View.VISIBLE
                mLanguageArray[position].isSelected = true
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = mLanguageArray.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtProfessionData = itemView.txtProfessionData
        val imgSelectedProfession = itemView.imgSelectedProfession
    }
}