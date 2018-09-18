package adapters

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import com.squareup.picasso.Picasso
import database.Database
import fragments.ChatFragment
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.item_chats.view.*
import models.ChatsModel
import utils.Constants
import utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class ChatsAdapter(mContext: Context, mChatFragment: ChatFragment, width: Int, chats: LinkedHashMap<String, ChatsModel>,
                   keys: ArrayList<String>, userId: String, status: Boolean) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    var mContext: Context? = null
    var mUtils: Utils? = null
    var mDb: Database? = null
    private var mChatFragment: ChatFragment? = null

    var mScreenWidth: Int = 0
    var mChats: LinkedHashMap<String, ChatsModel>? = null
    var mKeys: ArrayList<String> = ArrayList()
    var mUserID = ""
    internal var date_format = SimpleDateFormat("dd MMM hh:mm a", Locale.US)
    internal var today_format = SimpleDateFormat("hh:mm a", Locale.US)
    internal var only_date_format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    internal var state = false

    init {
        this.mContext = mContext
        mUtils = Utils(mContext)
        mDb = Database(mContext)
        this.mChatFragment = mChatFragment
        mScreenWidth = width
        mChats = chats
        mKeys = keys
        mUserID = userId
        state = status
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chats, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        var mPrivateChat: ChatsModel? = null
        if (state) {
            mPrivateChat = mChats!![mKeys[position]]
        } else {
            mPrivateChat = mChatFragment!!.mChats!!.get(mChatFragment!!.mKeys.get(position))
        }
        val otherUserId = mPrivateChat!!.opponent_user_id
//        for (userUniqueKey in mPrivateChat!!.user_type.keys) {
//            if (!userUniqueKey.equals(mUserID)) {
//                otherUserId = userUniqueKey
//                break
//            }
//        }

        Picasso.with(mContext).load(mPrivateChat.profile_pic.get(otherUserId)).placeholder(R.drawable.placeholder_image).into(holder!!.imgProfileAvatar)
        holder.txtName.text = mPrivateChat.name.get(otherUserId)

        if (mPrivateChat.user_type.get(otherUserId).equals("" + Constants.MENTEE)) {
            holder.txtCategory.text = mContext!!.getString(R.string.mentee)
            holder.txtCategory.setTextColor(ContextCompat.getColor(mContext!!, R.color.green_color))
        } else {
            holder.txtCategory.text = mContext!!.getString(R.string.mentor)
            holder.txtCategory.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))
        }

        if (mPrivateChat.unread_count.get(mUserID) != 0) {
            holder.txtUnreadCount.visibility = View.VISIBLE
            holder.txtUnreadCount.text = "" + mPrivateChat.unread_count.get(mUserID)
        } else {
            holder.txtUnreadCount.visibility = View.INVISIBLE
        }

        if (!mPrivateChat.last_message.equals(Constants.DEFAULT_MESSAGE_REGEX)) {
            try {
                val cal = Calendar.getInstance()
                cal.timeInMillis = Constants.getLocalTime(mPrivateChat.last_message_time.get(mUserID)!!)
                val today = Calendar.getInstance()
                var time = mDb!!.getMessageTime(mPrivateChat.chat_dialog_id)
                if (time > 0) {
                    val calLocal = Calendar.getInstance()
//                    val values = mUtils!!.getString("offset", "0.0").split(".\\").toTypedArray()
                    calLocal.timeInMillis = time /*+ values[0].toLong()*/
                    if (only_date_format.format(today.time) == only_date_format.format(cal.time)) {
                        // today
                        holder.txtTime.text = today_format.format(calLocal.time)
                    } else {

                        holder.txtTime.text = date_format.format(calLocal.time)
                    }
                } else {
                    if (only_date_format.format(today.time) == only_date_format.format(cal.time)) {
                        // today
                        holder.txtTime.text = today_format.format(cal.time)
                    } else {

                        holder.txtTime.text = date_format.format(cal.time)
                    }
                }
            } catch (e: Exception) {
                holder.txtTime.text = ""
            }
        } else {
            holder.txtTime.text = ""
        }

        holder.llChatOuter.setOnClickListener {
            mChatFragment!!.openConversationActivity(mPrivateChat)
        }
    }

    override fun getItemCount(): Int {
        if (state) {
            return mKeys.size
        } else {
            return mChatFragment!!.mKeys.size
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llChatOuter = itemView.llChatOuter!!
        val imgProfileAvatar = itemView.imgProfileAvatar!!
        val txtCategory = itemView.txtCategory!!
        val txtName = itemView.txtName!!
        val txtTime = itemView.txtTime!!
        val txtUnreadCount = itemView.txtUnreadCount!!

        init {
            if (mUtils!!.getInt("nightMode", 0) == 1)
                displayNightMode()
            else
                displayDayMode()
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        private fun displayNightMode() {
            llChatOuter.background = ContextCompat.getDrawable(mContext!!, R.drawable.black_ripple)
            txtName.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
            txtUnreadCount.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        private fun displayDayMode() {
            llChatOuter.background = ContextCompat.getDrawable(mContext!!, R.drawable.white_ripple)
            txtName.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            txtUnreadCount.setTextColor(ContextCompat.getColor(mContext!!, R.color.red_color))
        }
    }

}