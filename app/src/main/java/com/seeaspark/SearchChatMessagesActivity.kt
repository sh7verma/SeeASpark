package com.seeaspark

import adapters.SearchMessageAdapter
import android.content.Intent
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.activity_search_events.*
import models.MessagesModel
import utils.Constants

class SearchChatMessagesActivity : BaseActivity() {

    private var mLayoutManager: LinearLayoutManager? = null

    var opponentUserName = Constants.EMPTY
    var dialogId = Constants.EMPTY
    var opponentUserId = Constants.EMPTY
    var opponentUserPic = Constants.EMPTY
    var deleteTime = 0L

    lateinit var mSearchAdapter: SearchMessageAdapter
    var mMessagesArray = mutableListOf<MessagesModel>()
    val tempArray = ArrayList<MessagesModel>()

    override fun getContentView() = R.layout.activity_search_events

    override fun initUI() {
        mLayoutManager = LinearLayoutManager(mContext)
        rvSearchEventCommunity.layoutManager = mLayoutManager
        imgBackSearch.setImageResource(R.mipmap.ic_back_org)
    }

    override fun displayDayMode() {
        imgBackSearch.setBackgroundResource(whiteRipple)
        imgCancelSearch.setBackgroundResource(whiteRipple)
        edSearchEventCommunity.setBackgroundColor(whiteColor)
        imgBackSearch.setImageResource(R.mipmap.ic_back_org)
        llMainSearchEvents.setBackgroundColor(whiteColor)
        edSearchEventCommunity.setTextColor(blackColor)
        txtNoResultFound.setTextColor(blackColor)
    }

    override fun displayNightMode() {
        imgBackSearch.setBackgroundResource(blackRipple)
        imgCancelSearch.setBackgroundResource(blackRipple)
        edSearchEventCommunity.setBackgroundColor(blackColor)
        imgBackSearch.setImageResource(R.mipmap.ic_back_org)
        llMainSearchEvents.setBackgroundColor(blackColor)
        edSearchEventCommunity.setTextColor(whiteColor)
        txtNoResultFound.setTextColor(whiteColor)
    }

    override fun onCreateStuff() {
        opponentUserName = intent.getStringExtra("opponentName")
        dialogId = intent.getStringExtra("dialogId")
        opponentUserId = intent.getStringExtra("opponentUserId")
        opponentUserPic = intent.getStringExtra("opponentUserPic")
        deleteTime = intent.getLongExtra("deleteTime", 0)

        mMessagesArray = db!!.getAllMessagesArray(dialogId, mUtils!!.getString("user_id", ""),
                Constants.TYPE_TEXT, deleteTime, opponentUserId)

        Log.e("Messages Size = ", mMessagesArray.size.toString())

        edSearchEventCommunity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tempArray.clear()
                if (char.toString().isNotEmpty()) {
                    imgCancelSearch.visibility = View.VISIBLE
                    for (messageModel in mMessagesArray) {
                        if (messageModel.message.contains(char.toString())) {
                            tempArray.add(messageModel)
                        }
                    }
                    if (tempArray.isEmpty()) {
                        txtNoResultFound.visibility = View.VISIBLE
                        rvSearchEventCommunity.visibility = View.GONE
                    } else {
                        mSearchAdapter = SearchMessageAdapter(mContext!!, tempArray)
                        rvSearchEventCommunity.adapter = mSearchAdapter
                        txtNoResultFound.visibility = View.GONE
                        rvSearchEventCommunity.visibility = View.VISIBLE
                    }
                } else {
                    imgCancelSearch.visibility = View.INVISIBLE
                    txtNoResultFound.visibility = View.GONE
                    rvSearchEventCommunity.visibility = View.GONE
                }
            }
        })
    }

    override fun initListener() {
        imgBackSearch.setOnClickListener(this)
        imgCancelSearch.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackSearch -> {
                moveBack()
            }
            imgCancelSearch -> {
                rvSearchEventCommunity.adapter = null
                edSearchEventCommunity.setText(Constants.EMPTY)
            }
        }
    }

    private fun moveBack() {
        Constants.closeKeyboard(mContext!!, imgBackSearch)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    fun displayFullMessage(message: String?, position: Int) {
        val intent = Intent(mContext, FullViewMessageActivity::class.java)
        intent.putExtra("message", message)
        if (tempArray[position].sender_id
                        .equals(mUtils!!.getString("user_id", ""))) {
            intent.putExtra("name", mUtils!!.getString("user_name", ""))
            intent.putExtra("pic", mUtils!!.getString("user_pic", ""))
        } else {
            intent.putExtra("name", opponentUserName)
            intent.putExtra("pic", opponentUserPic)
        }
        mContext!!.startActivity(intent)
        overridePendingTransition(0, 0)
    }
}