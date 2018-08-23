package com.seeaspark

import adapters.ShareNotesAdapter
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_my_notes.*
import models.NotesListingModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

/**
 * Created by dev on 9/8/18.
 */
class MyNotesActivity : BaseActivity() {

    private val NOTES = 18
    private val SEARCH: Int = 19

    private var mMyNotesOffset = 1
    var isLoadingMyNotes: Boolean = false

    private var mLayoutManager: LinearLayoutManager? = null

    private var mMyNotesArray = ArrayList<NotesListingModel.ResponseBean>()

    private var mShareNotesAdapter: ShareNotesAdapter? = null

    private var mMYNotesActivity: MyNotesActivity? = null

    override fun getContentView() = R.layout.activity_my_notes

    override fun initUI() {
        mMYNotesActivity = this
        mLayoutManager = LinearLayoutManager(this)
        rvMyNotes.layoutManager = mLayoutManager
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llOuterNotes.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        txtSendNotes.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        txtNoMyNotes.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llOuterNotes.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        txtSendNotes.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        txtNoMyNotes.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        if (connectedToInternet()) {
            if (db!!.getNotesByType(Constants.MYNOTES).size > 0) {
                hitMyNotesAPI(false)
                populateData()
            } else
                hitMyNotesAPI(true)

        } else
            showInternetAlert(rvMyNotes)
    }

    override fun initListener() {
        imgBack.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                moveBack()
            }
            imgSearch -> {
                intent = Intent(mContext, SearchNotesActivity::class.java)
                intent.putExtra("path", "notes")
                intent.putExtra("noteType", Constants.MYNOTES)
                startActivityForResult(intent, SEARCH)
            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun hitMyNotesAPI(isVisible: Boolean) {
        if (isVisible)
            pbMyNotes.visibility = View.VISIBLE

        val call = RetrofitClient.getInstance().getNotes(mUtils!!.getString("access_token", ""),
                Constants.MYNOTES, mMyNotesOffset.toString())
        call.enqueue(object : Callback<NotesListingModel> {
            override fun onResponse(call: Call<NotesListingModel>?, response: Response<NotesListingModel>) {
                if (isVisible)
                    pbMyNotes.visibility = View.GONE

                if (response.body().response != null) {
                    addToLocalDatabase(response.body().response)
                    populateData()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(llOuterNotes, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<NotesListingModel>?, t: Throwable?) {
                if (isVisible)
                    pbMyNotes.visibility = View.GONE
            }
        })
    }

    private fun addToLocalDatabase(response: List<NotesListingModel.ResponseBean>) {
        for (notesData in response) {
            db!!.addNotes(notesData)
        }
    }


    private fun populateData() {
        mMyNotesArray.clear()
        mMyNotesArray.addAll(db!!.getNotesByType(Constants.MYNOTES))

        if (mShareNotesAdapter == null) {
            mShareNotesAdapter = ShareNotesAdapter(mMyNotesArray, mContext!!, mMYNotesActivity)
            rvMyNotes.adapter = mShareNotesAdapter
        } else
            mShareNotesAdapter!!.notifyDataSetChanged()

        if (mMyNotesArray.size == 0) {
            txtNoMyNotes.visibility = View.VISIBLE
            rvMyNotes.visibility = View.GONE
        } else {
            txtNoMyNotes.visibility = View.GONE
            rvMyNotes.visibility = View.VISIBLE
        }

    }

    fun moveToDetail(notesData: NotesListingModel.ResponseBean) {
//        val intent = Intent(mContext, NotesActivity::class.java)
//        intent.putExtra("notesData", notesData)
//        startActivityForResult(intent, NOTES)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

}