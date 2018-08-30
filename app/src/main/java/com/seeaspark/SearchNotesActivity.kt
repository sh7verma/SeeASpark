package com.seeaspark

import adapters.ShareNotesAdapter
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_search_events.*
import models.NotesListingModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

/**
 * Created by dev on 9/8/18.
 */
class SearchNotesActivity : BaseActivity() {

    private var mNotesAdapter: ShareNotesAdapter? = null
    private var mNotesArray = ArrayList<NotesListingModel.ResponseBean>()
    private var mSearchInstance: SearchNotesActivity? = null
    private var isNotes = false
    private var mLayoutManager: LinearLayoutManager? = null
    private var mOffset: Int = 1
    private var mNoteType = Constants.EMPTY

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
        mSearchInstance = this

        if (intent.getStringExtra("path") == "notes") {
            isNotes = true
            mNoteType = intent.getStringExtra("noteType")
            rvSearchEventCommunity.setPadding(resources.getDimension(R.dimen._16sdp).toInt(), 0, resources.getDimension(R.dimen._16sdp).toInt(), 0)
        }

        imgCancelSearch.visibility = View.INVISIBLE

        edSearchEventCommunity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (char.toString().isNotEmpty())
                    imgCancelSearch.visibility = View.VISIBLE
                else
                    imgCancelSearch.visibility = View.INVISIBLE
            }
        })

        edSearchEventCommunity.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (connectedToInternet()) {
                    Constants.closeKeyboard(mContext, llMainSearchEvents)
                    hitAPI(edSearchEventCommunity.text.toString())
                } else
                    showInternetAlert(llMainSearchEvents)
                return@OnEditorActionListener true
            }
            false
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

        if (isNotes) {
            val intent = Intent()
            setResult(RESULT_OK, intent)
        }
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun hitAPI(searchText: String) {
        if (isNotes) {

            if (mNoteType == Constants.MYNOTES) {
                showLoader()
                val call = RetrofitClient.getInstance().searchNotes(mUtils!!.getString("access_token", ""),
                        Constants.MYNOTES, searchText, mOffset.toString())
                call.enqueue(object : Callback<NotesListingModel> {
                    override fun onResponse(call: Call<NotesListingModel>?, response: Response<NotesListingModel>) {
                        if (response.body().response != null) {
                            dismissLoader()
                            mNotesArray.clear()
                            mNotesArray.addAll(response.body().response)
                            populateNotesData()
                        } else {
                            dismissLoader()
                            if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                                moveToSplash()
                            } else
                                showAlert(rvSearchEventCommunity, response.body().error!!.message!!)
                        }
                    }

                    override fun onFailure(call: Call<NotesListingModel>?, t: Throwable?) {
                        dismissLoader()
                        showAlert(llMainSearchEvents, t!!.localizedMessage)
                    }
                })
            }
        }
    }

    private fun populateNotesData() {
        if (mOffset == 1) {
            if (mNotesArray.size == 0)
                txtNoResultFound.visibility = View.VISIBLE
            else
                txtNoResultFound.visibility = View.GONE
            mNotesAdapter = ShareNotesAdapter(mNotesArray, mContext!!, null, mSearchInstance!!)
            rvSearchEventCommunity.adapter = mNotesAdapter
        } else
            mNotesAdapter!!.notifyDataSetChanged()

    }

}