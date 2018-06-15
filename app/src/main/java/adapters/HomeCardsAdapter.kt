package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import models.CardModel

class HomeCardsAdapter(mCardsArray: ArrayList<CardModel>, mContext: Context)
    : RecyclerView.Adapter<HomeCardsAdapter.ViewHolder>() {


    var mCardsArray = ArrayList<CardModel>()
    var mContext: Context? = null

    init {
        this.mCardsArray = mCardsArray
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cards, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: HomeCardsAdapter.ViewHolder?, position: Int) {

    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}