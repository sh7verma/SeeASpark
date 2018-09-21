package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.PaymentsHistoryActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_payments.view.*
import models.PaymentAdditionModel
import models.PaymentsHistoryModel
import utils.Utils

class PaymentsHistoryAdapter(private var mPaymentsArray: List<PaymentAdditionModel.Response>,
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