package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.PaymentsHistoryActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_payments.view.*
import models.PaymentAdditionModel
import models.PlansModel
import utils.Constants
import utils.Utils

class PaymentsHistoryAdapter(private var mPaymentsArray: List<PlansModel.Response>,
                             private var context: Context)
    : RecyclerView.Adapter<PaymentsHistoryAdapter.ViewHolder>() {

    var mUtils: Utils = Utils(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_payments, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtPaymentMode.text = mPaymentsArray[position].plan_type

        holder.txtPaymentCost.text = StringBuilder().append(Constants.POUND)
                .append(mPaymentsArray[position].amount)

        holder.txtPackCount.text = StringBuilder().append("(")
                .append(Constants.POUND)
                .append(mPaymentsArray[position].amount)
                .append(")")

        holder.txtPaymentStatus.text = mPaymentsArray[position].payment_status
        if (mPaymentsArray[position].payment_status.toLowerCase() == "success")
            holder.txtPaymentStatus.setTextColor(ContextCompat.getColor(context, R.color.green_color))
        else
            holder.txtPaymentStatus.setTextColor(ContextCompat.getColor(context, R.color.red_color))

        holder.txtPaymentDate.text = Constants.displayDateTime(mPaymentsArray[position]
                .purchase_date)

        holder.txtPaymentPlan.text = mPaymentsArray[position].plan_type.substring(0, 1)
                .toUpperCase()
    }

    override fun getItemCount(): Int {
        return mPaymentsArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPaymentPlan = itemView.txtPaymentPlan!!
        val txtPaymentMode = itemView.txtPaymentMode!!
        val txtPackCount = itemView.txtPackCount!!
        val txtPaymentStatus = itemView.txtPaymentStatus!!
        val txtPaymentDate = itemView.txtPaymentDate!!
        val txtPaymentCost = itemView.txtPaymentCost!!

        init {
            if (mUtils.getInt("nightMode", 0) == 1)
                displayNightMode()
            else
                displayDayMode()
        }

        private fun displayDayMode() {
            txtPaymentMode.setTextColor((context as PaymentsHistoryActivity).blackColor)
            txtPackCount.setTextColor((context as PaymentsHistoryActivity).blackColor)
        }

        private fun displayNightMode() {
            txtPaymentMode.setTextColor((context as PaymentsHistoryActivity).whiteColor)
            txtPackCount.setTextColor((context as PaymentsHistoryActivity).whiteColor)
        }
    }
}