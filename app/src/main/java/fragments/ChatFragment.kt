package fragments

import adapters.ChatsAdapter
import android.app.Activity
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.*
import database.Database
import helper.FirebaseListeners
import kotlinx.android.synthetic.main.fragment_chats.*
import models.ChatsModel
import models.ProfileModel
import utils.Constants
import utils.Utils
import java.util.*

/**
 * Created by dev on 23/7/18.
 */
class ChatFragment : Fragment(), View.OnClickListener, FirebaseListeners.ChatDialogsListenerInterface {

    var itemView: View? = null
    var mWidth: Int = 0
    var mHeight: Int = 0
    var mUtils: Utils? = null
    var mDb: Database? = null
    private var mChatsAdapter: ChatsAdapter? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var mChatFragment: ChatFragment? = null
    internal val FILTER = 11

    var mChats: LinkedHashMap<String, ChatsModel>? = null
    var mKeys: ArrayList<String> = ArrayList()
    internal var mCurrentUser: ProfileModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_chats, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        getDefaults()
        initUi()
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    fun initUi(){
        mUtils = Utils(activity)
        if (mUtils!!.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()
    }

    fun getDefaults() {
        mChatFragment = this
        mUtils = Utils(activity)
        mDb = Database(activity)
        val display = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(display)
        mWidth = display.widthPixels
        mHeight = display.heightPixels
        mCurrentUser = mDb!!.getProfile(mUtils!!.getString("user_id", ""))
    }

    private fun initListener() {
        imgFilter.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBackSearch.setOnClickListener(this)
        imgCancel.setOnClickListener(this)
    }

    private fun onCreateStuff() {
        FirebaseListeners.setChatDialogListener(this)

        val typeface = Typeface.createFromAsset(activity.assets, "fonts/medium.otf")
        edSearch.typeface = typeface

        mLayoutManager = LinearLayoutManager(activity)
        rvChats.layoutManager = mLayoutManager

        if (mCurrentUser != null) {
            mCurrentUser!!.chat_dialog_ids = mDb!!.getDialogs(mCurrentUser!!.user_id)
            mChats = mDb!!.getAllChats(mCurrentUser!!.user_id, mUtils!!.getString("filter_type", Constants.FILTER_BOTH))
            mKeys = ArrayList()
            for (key in mChats!!.keys) {
                mKeys.add(key)
            }
            mChatsAdapter = ChatsAdapter(activity!!, mChatFragment!!, mWidth!!, mChats!!, mKeys!!,
                    mCurrentUser!!.user_id, false)
            rvChats.adapter = mChatsAdapter
        }

        if (mChats != null && mChats!!.size > 0) {
            llNoHandshake.setVisibility(View.GONE)
        } else {
            llNoHandshake.setVisibility(View.VISIBLE)
            if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_BOTH)) {
                txtNoChat.text = getString(R.string.chat_available)
            }else if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)){
                txtNoChat.text = getString(R.string.no_mentee)
            }else{
                txtNoChat.text = getString(R.string.no_mentor)
            }
        }

        edSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searString = s.toString().toLowerCase()
                if (mCurrentUser != null) {
                    if (mChats!!.size > 0) {
                        if (searString.trim().length == 0) {
                            mChatsAdapter = ChatsAdapter(activity!!, mChatFragment!!, mWidth!!, mChats!!, mKeys!!,
                                    mCurrentUser!!.user_id, false)
                            rvChats.adapter = mChatsAdapter
                            llNoHandshake.setVisibility(View.GONE)
                        } else {
                            val localChat = LinkedHashMap<String, ChatsModel>()
                            val localKeys = ArrayList<String>()
                            for (i in 0 until mChats!!.size) {
                                val ch = mChats!![mKeys[i]]
//                                var otherUserId = ""
//                                for (userUniqueKey in ch!!.user_type.keys) {
//                                    if (!userUniqueKey.equals(mUtils!!.setString("user_id", ""))) {
//                                        otherUserId = userUniqueKey
//                                        break
//                                    }
//                                }
                                if (("" + ch!!.name.get(ch.opponent_user_id)).toLowerCase().contains(searString.trim())) {
                                    localChat.put(mKeys[i], ch)
                                    localKeys.add(mKeys[i])
                                }
                            }
                            mChatsAdapter = ChatsAdapter(activity!!, mChatFragment!!, mWidth!!, localChat!!, localKeys!!,
                                    mCurrentUser!!.user_id, true)
                            rvChats.adapter = mChatsAdapter
                            if (localChat != null && localChat!!.size > 0) {
                                llNoHandshake.setVisibility(View.GONE)
                            } else {
                                llNoHandshake.setVisibility(View.VISIBLE)
                                txtNoChat.text = getString(R.string.no_result_found)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            imgFilter -> {
                var mentee = "0"
                var mentor = "0"
                var localChat = mDb!!.getAllChats(mCurrentUser!!.user_id, mUtils!!.getString("filter_type", Constants.FILTER_BOTH))
                for (key in localChat!!.keys) {
                    if (localChat!![key]!!.unread_count.get(mCurrentUser!!.user_id) != 0 && localChat!![key]!!.user_type.get(mCurrentUser!!.user_id)!!.equals(Constants.MENTEE)) {
                        mentee = "1"
                    }
                    if (localChat!![key]!!.unread_count.get(mCurrentUser!!.user_id) != 0 && localChat!![key]!!.user_type.get(mCurrentUser!!.user_id)!!.equals(Constants.MENTOR)) {
                        mentor = "1"
                    }
                }
                var intent = Intent(activity, ChatFilterActivity::class.java)
                intent.putExtra("mentee", mentee)
                intent.putExtra("mentor", mentor)
                startActivityForResult(intent, FILTER)
            }
            imgSearch -> {
                llToolbarChats.visibility = View.GONE
                llToolbarSearch.visibility = View.VISIBLE
            }
            imgBackSearch -> {
                Constants.closeKeyboard(activity, imgBackSearch)
                llToolbarSearch.visibility = View.GONE
                edSearch.setText("")
                llToolbarChats.visibility = View.VISIBLE
            }
            imgCancel -> {
                edSearch.setText("")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayDayMode() {
        llOuterChats.background = ContextCompat.getDrawable(activity, R.color.white_color)
        imgFilter.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        imgSearch.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        imgBackSearch.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        imgCancel.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        txtTitleHandshakes.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
        edSearch.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llOuterChats.background = ContextCompat.getDrawable(activity, R.color.black_color)
        imgFilter.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        imgSearch.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        imgBackSearch.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        imgCancel.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        txtTitleHandshakes.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        edSearch.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(nightModeReceiver,
                IntentFilter(Constants.NIGHT_MODE))
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(nightModeReceiver)
        super.onDestroy()
    }

    var nightModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getIntExtra("status", 0) == Constants.DAY) {
                displayDayMode()
            } else {
                displayNightMode()
            }
        }
    }

    fun checkSearch(): Boolean {
        if (llToolbarSearch.visibility == View.VISIBLE) {
            llToolbarSearch.visibility = View.GONE
            edSearch.setText("")
            llToolbarChats.visibility = View.VISIBLE
            return false
        } else {
            return true
        }
    }

    fun openConversationActivity(chat: ChatsModel) {
        val intent = Intent(activity, ConversationActivity::class.java)
        intent.putExtra("participantIDs", chat.participant_ids)
        startActivity(intent)
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        Constants.closeKeyboard(activity, imgBackSearch)
        llToolbarSearch.visibility = View.GONE
        edSearch.setText("")
        llToolbarChats.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FILTER -> {
                    mChats = mDb!!.getAllChats(mCurrentUser!!.user_id, mUtils!!.getString("filter_type", Constants.FILTER_BOTH))
                    mKeys = ArrayList()
                    for (key in mChats!!.keys) {
                        mKeys.add(key)
                    }
                    mChatsAdapter = ChatsAdapter(activity!!, mChatFragment!!, mWidth!!, mChats!!, mKeys!!,
                            mCurrentUser!!.user_id, false)
                    rvChats.adapter = mChatsAdapter
                    if (mChats != null && mChats!!.size > 0) {
                        llNoHandshake.setVisibility(View.GONE)
                    } else {
                        llNoHandshake.setVisibility(View.VISIBLE)
                        if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_BOTH)) {
                            txtNoChat.text = getString(R.string.chat_available)
                        }else if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)){
                            txtNoChat.text = getString(R.string.no_mentee)
                        }else{
                            txtNoChat.text = getString(R.string.no_mentor)
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDialogAdd(mChat: ChatsModel?) {
//        var otherUserType = ""
//        for (userUniqueKey in mChat!!.user_type.keys) {
//            if (!userUniqueKey.equals(mUtils!!.setString("user_id", ""))) {
//                otherUserType = mChat!!.user_type.get(userUniqueKey)!!
//                break
//            }
//        }
        var status = 0
        if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH) == Constants.FILTER_MENTOR) {
            if (mChat!!.opponent_user_id == Constants.FILTER_MENTOR) {
                status = 1
            }
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH) == Constants.FILTER_MENTEE) {
            if (mChat!!.opponent_user_id == Constants.FILTER_MENTEE) {
                status = 1
            }
        } else {
            status = 1
        }
        if (status == 1) {
            if (mKeys.contains(mChat!!.chat_dialog_id)) { // already
                mChats!!.put(mChat.chat_dialog_id, mChat)
            } else {
                mKeys.add(0, mChat.chat_dialog_id)
                val newmap = mChats!!.clone() as LinkedHashMap<String, ChatsModel>
                mChats!!.clear()
                mChats!!.put(mChat.chat_dialog_id, mChat)
                mChats!!.putAll(newmap)
                if (mChats != null && mChats!!.size > 0) {
                    llNoHandshake.setVisibility(View.GONE)
                } else {
                    llNoHandshake.setVisibility(View.VISIBLE)
                    if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_BOTH)) {
                        txtNoChat.text = getString(R.string.chat_available)
                    }else if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)){
                        txtNoChat.text = getString(R.string.no_mentee)
                    }else{
                        txtNoChat.text = getString(R.string.no_mentor)
                    }
                }
            }
            mChatsAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onDialogChanged(mChat: ChatsModel?, value: Int) {
//        var otherUserType = ""
//        for (userUniqueKey in mChat!!.user_type.keys) {
//            if (!userUniqueKey.equals(mUtils!!.setString("user_id", ""))) {
//                otherUserType = mChat!!.user_type.get(userUniqueKey)!!
//                break
//            }
//        }
        var status = 0
        if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH) == Constants.FILTER_MENTOR) {
            if (mChat!!.opponent_user_id == Constants.FILTER_MENTOR) {
                status = 1
            }
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH) == Constants.FILTER_MENTEE) {
            if (mChat!!.opponent_user_id == Constants.FILTER_MENTEE) {
                status = 1
            }
        } else {
            status = 1
        }
        if (status == 1) {
            if (value == 0) {
                if (mChat!!.user_type.containsKey(mCurrentUser!!.user_id)) {
                    if (!mKeys.contains(mChat.chat_dialog_id)) {
                        mKeys.add(0, mChat.chat_dialog_id)
                        val newmap = mChats!!.clone() as LinkedHashMap<String, ChatsModel>
                        mChats!!.clear()
                        mChats!!.put(mChat.chat_dialog_id, mChat)
                        mChats!!.putAll(newmap)
                    } else {
                        val msgTime = mChats!![mChat!!.chat_dialog_id]!!.last_message_time.get(mUtils!!.getString("user_id",""))
                        if (mChat.last_message_time.get(mUtils!!.getString("user_id",""))!! > msgTime!!) {
                            mChats!!.remove(mChat.chat_dialog_id)
                            val newmap = mChats!!.clone() as LinkedHashMap<String, ChatsModel>
                            mChats!!.clear()
                            mChats!!.put(mChat.chat_dialog_id, mChat)
                            mChats!!.putAll(newmap)
                            mKeys.remove(mChat.chat_dialog_id)
                            mKeys.add(0, mChat.chat_dialog_id)
                        } else {
                            mChats!!.put(mChat.chat_dialog_id, mChat)
                        }
                    }
                } else {
                    mChats!!.remove(mChat.chat_dialog_id)
                    mKeys!!.remove(mChat.chat_dialog_id)
                }
            } else {
                mChats!!.remove(mChat!!.chat_dialog_id)
                mKeys!!.remove(mChat!!.chat_dialog_id)
            }

            mChatsAdapter!!.notifyDataSetChanged()
            if (mChats != null && mChats!!.size > 0) {
                llNoHandshake.setVisibility(View.GONE)
            } else {
                llNoHandshake.setVisibility(View.VISIBLE)
                if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_BOTH)) {
                    txtNoChat.text = getString(R.string.chat_available)
                }else if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)){
                    txtNoChat.text = getString(R.string.no_mentee)
                }else{
                    txtNoChat.text = getString(R.string.no_mentor)
                }
            }
        }
    }

    override fun onDialogRemoved(dialogId: String?) {
        val ch = mChats!![dialogId]
//        var otherUserType = ""
//        for (userUniqueKey in ch!!.user_type.keys) {
//            if (!userUniqueKey.equals(mUtils!!.setString("user_id", ""))) {
//                otherUserType = ch!!.user_type.get(userUniqueKey)!!
//                break
//            }
//        }
        var status = 0
        if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH) == Constants.FILTER_MENTOR) {
            if (ch!!.opponent_user_id == Constants.FILTER_MENTOR) {
                status = 1
            }
        } else if (mUtils!!.getString("filter_type", Constants.FILTER_BOTH) == Constants.FILTER_MENTEE) {
            if (ch!!.opponent_user_id == Constants.FILTER_MENTEE) {
                status = 1
            }
        } else {
            status = 1
        }
        if (status == 1) {
            mChats!!.remove(dialogId)
            mKeys!!.remove(dialogId)
            mChatsAdapter!!.notifyDataSetChanged()
            if (mChats != null && mChats!!.size > 0) {
                llNoHandshake.setVisibility(View.GONE)
            } else {
                llNoHandshake.setVisibility(View.VISIBLE)
                if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_BOTH)) {
                    txtNoChat.text = getString(R.string.chat_available)
                }else if(mUtils!!.getString("filter_type", Constants.FILTER_BOTH).equals(Constants.FILTER_MENTEE)){
                    txtNoChat.text = getString(R.string.no_mentee)
                }else{
                    txtNoChat.text = getString(R.string.no_mentor)
                }
            }
        }
    }

}