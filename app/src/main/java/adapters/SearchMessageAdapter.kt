package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.seeaspark.SearchChatMessagesActivity
import kotlinx.android.synthetic.main.item_search_message.view.*
import models.MessagesModel
import utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class SearchMessageAdapter(mConetxt: Context, mMessagesArray: List<MessagesModel>)
    : RecyclerView.Adapter<SearchMessageAdapter.ViewHolder>() {

    var mMessagesArray = ArrayList<MessagesModel>()
    var mContext: Context? = null
    var mUtils: Utils? = null
    internal var date_format = SimpleDateFormat("dd MMM hh:mm a", Locale.US)

    init {
        this.mMessagesArray = mMessagesArray as ArrayList<MessagesModel>
        this.mContext = mConetxt
        mUtils = Utils(mConetxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_message, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mMessagesArray[position].sender_id
                        .equals(mUtils!!.getString("user_id", "")))
            holder.txtMessageOwnerName.text = mUtils!!.getString("user_name", "")
        else
            holder.txtMessageOwnerName.text = (mContext as SearchChatMessagesActivity).opponentUserName

        holder.txtMessageSearch.text = mMessagesArray[position].message
        holder.txtTimeSearch.text = date_format.format(mMessagesArray[position].message_time.toLong())

        holder.llSearchMessageItem.setOnClickListener {
            (mContext as SearchChatMessagesActivity)
                    .displayFullMessage(mMessagesArray[position].message,position)
        }
    }

    override fun getItemCount() = mMessagesArray.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llSearchMessageItem = itemView.llSearchMessageItem!!
        val txtMessageOwnerName = itemView.txtMessageOwnerName!!
        val txtMessageSearch = itemView.txtMessageSearch!!
        val txtTimeSearch = itemView.txtTimeSearch!!
    }
}