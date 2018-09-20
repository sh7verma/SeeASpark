package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_availability.view.*
import models.AvailabilityModel

class AvailabilityAdapter(mAvailabilityArray: ArrayList<AvailabilityModel>, mContext: Context,
                          isEditable: Boolean)
    : RecyclerView.Adapter<AvailabilityAdapter.ViewHolder>() {

    var mAvailabilityArray = ArrayList<AvailabilityModel>()
    var mContext: Context? = null
    var isEditable = false

    init {
        this.mAvailabilityArray = mAvailabilityArray
        this.mContext = mContext
        this.isEditable = isEditable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_availability, parent,
                false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtDayAvailability.text = mAvailabilityArray[position].dayValue

        if (mAvailabilityArray[position].isSelected)
            holder.rlAvailability.setBackgroundResource(R.drawable.green_circle)
        else
            holder.rlAvailability.setBackgroundResource(R.drawable.red_circle)

        holder.rlAvailability.setOnClickListener {
            if (isEditable) {
                mAvailabilityArray[position].isSelected = !mAvailabilityArray[position]
                        .isSelected
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return mAvailabilityArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rlAvailability = itemView.rlAvailability!!
        val txtDayAvailability = itemView.txtDayAvailability!!
    }

}