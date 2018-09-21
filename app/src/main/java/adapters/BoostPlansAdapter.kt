package adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import fragments.BoostFragment
import kotlinx.android.synthetic.main.item_boost_plans.view.*
import models.PlansModel
import utils.Constants
import utils.Utils
import java.lang.StringBuilder

class BoostPlansAdapter(private var mPlansArray: ArrayList<PlansModel.Response>,
                        private var boostFragment: BoostFragment)
    : RecyclerView.Adapter<BoostPlansAdapter.ViewHolder>() {

    var mUtils: Utils = Utils(boostFragment.activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_boost_plans, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtBoostPlanCard.text = mPlansArray[position].duration
        holder.txtPlanType.text = mPlansArray[position].time_period
        holder.txtBoostPlanCost.text = StringBuilder().append(Constants.POUND)
                .append(mPlansArray[position].amount)

        holder.txtBuyNow.setOnClickListener {
            boostFragment.buyPlan(mPlansArray[position],position)
        }
    }

    override fun getItemCount(): Int {
        return mPlansArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtBoostPlanCard = itemView.txtBoostPlanCard!!
        val txtPlanType = itemView.txtPlanType!!
        val txtBoostPlanCost = itemView.txtBoostPlanCost!!
        val txtBuyNow = itemView.txtBuyNow!!
    }
}