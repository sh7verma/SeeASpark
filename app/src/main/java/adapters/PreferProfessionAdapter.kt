package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.PreferencesActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_profession.view.*
import models.ProfessionModel

class PreferProfessionAdapter(mConetxt: Context,
                              mProfessionArray: ArrayList<ProfessionModel>,
                              mPreferActivity: PreferencesActivity?)
    : RecyclerView.Adapter<PreferProfessionAdapter.ViewHolder>() {

    var mProfessionArray = ArrayList<ProfessionModel>()
    var mContext: Context? = null
    var mPreferActivity: PreferencesActivity? = null

    init {
        this.mProfessionArray = mProfessionArray
        this.mContext = mConetxt
        this.mPreferActivity = mPreferActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_profession, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtProfessionData.text = mProfessionArray[position].name

        if (mPreferActivity!!.mSelectedProfessionsArray.contains(mProfessionArray[position].id.toString()))
            holder.imgSelectedProfession.visibility = View.VISIBLE
        else {
            if (mProfessionArray[position].isSelected)
                holder.imgSelectedProfession.visibility = View.VISIBLE
            else
                holder.imgSelectedProfession.visibility = View.INVISIBLE
        }

        holder.txtProfessionData.setOnClickListener {
            if (holder.adapterPosition == 0) {
                mPreferActivity!!.clearProfessionPreferences()
            } else {
                if (mPreferActivity!!.mSelectedProfessionsArray.contains(mProfessionArray[position].id.toString())) {
                    holder.imgSelectedProfession.visibility = View.INVISIBLE
                    mPreferActivity!!.mSelectedProfessionsArray.remove(mProfessionArray[position].id.toString())

                    if (mPreferActivity!!.mSelectedProfessionsArray.size == 0)
                        mPreferActivity!!.clearProfessionPreferences()

                } else {
                    holder.imgSelectedProfession.visibility = View.VISIBLE
                    mPreferActivity!!.mSelectedProfessionsArray.add(mProfessionArray[position].id.toString())
                    mPreferActivity!!.unSelectedNoPreferences()
                }
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