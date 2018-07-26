package fragments

import adapters.MyNotesAdapter
import adapters.ReceivedNotesAdapter
import android.app.Activity
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import com.seeaspark.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_notes.*
import models.BaseSuccessModel
import models.NotesListingModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.Utils

class NotesFragment : Fragment(), View.OnClickListener {

    var mLandingInstance: LandingActivity? = null
    var itemView: View? = null
    var mContext: Context? = null
    var mUtils: Utils? = null

    private val NOTES = 1
    private val SEARCH: Int = 2

    private var intialViewPosition: Int = 0
    private var swipeleft: TranslateAnimation? = null
    private var swiperight: TranslateAnimation? = null

    private var isMyNotes = true

    private var mMyNotesOffset = 1
    var isLoadingMyNotes: Boolean = false

    private var mReceivedNotesOffset = 1
    var isLoadingReceivedNotes: Boolean = false

    private var mLayoutManager: LinearLayoutManager? = null
    private var mReceivedLayoutManager: LinearLayoutManager? = null

    private var mMyNotesArray = ArrayList<NotesListingModel.ResponseBean>()
    private var mReceivedArray = ArrayList<NotesListingModel.ResponseBean>()

    private var mMyNotesAdapter: MyNotesAdapter? = null
    private var mReceivedNotesAdapter: ReceivedNotesAdapter? = null

    private var mNotesFragment: NotesFragment? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_notes, container, false)
        return itemView
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mLandingInstance = activity as LandingActivity
        mContext = activity
        mUtils = Utils(activity)
        mNotesFragment = this
        initUI()
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun initUI() {

        if (mUtils!!.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()

        txtTitleCustom.text = getString(R.string.notes)
        txtTitleCustom.setPadding(Constants.dpToPx(16), 0, 0, 0)

        imgBackCustom.visibility = View.GONE

        imgOption2Custom.setImageResource(R.mipmap.ic_search)
        imgOption2Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)
        mReceivedLayoutManager = LinearLayoutManager(mContext)

        rvMyNotes.layoutManager = mLayoutManager
        rvReceivedNotes.layoutManager = mReceivedLayoutManager

        intialViewPosition = ((mLandingInstance!!.mWidth * 0.5) - (mLandingInstance!!.mWidth / 24)).toInt()

        swipeleft = TranslateAnimation(0f, intialViewPosition.toFloat(), 0f, 0f)
        swipeleft!!.duration = 300
        swipeleft!!.fillAfter = true

        swiperight = TranslateAnimation(intialViewPosition.toFloat(), 0f, 0f, 0f)
        swiperight!!.duration = 300
        swiperight!!.fillAfter = true

        setMyNotes()

    }

    private fun onCreateStuff() {
        if (mLandingInstance!!.connectedToInternet()) {
            if (mLandingInstance!!.db!!.getNotesByType(Constants.MYNOTES).size == 0 &&
                    mLandingInstance!!.db!!.getNotesByType(Constants.RECEIVEDNOTES).size == 0) {
                llNotesInner.visibility = View.GONE
                txtNoAllNotes.visibility = View.VISIBLE
            }

            if (mLandingInstance!!.db!!.getNotesByType(Constants.MYNOTES).size > 0) {
                hitMyNotesAPI(false)
                populateData()
            } else
                hitMyNotesAPI(true)

            if (mLandingInstance!!.connectedToInternet()) {
                if (mLandingInstance!!.db!!.getNotesByType(Constants.RECEIVEDNOTES).size > 0) {
                    hitReceivedNotesAPI(false)
                    populateReceivedData()
                } else
                    hitReceivedNotesAPI(true)
            } else
                mLandingInstance!!.showInternetAlert(rlMyNotes)


        } else
            mLandingInstance!!.showInternetAlert(rlMyNotes)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayDayMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.white_color))
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.black_color))
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.white_ripple)
        llMainNotes.setBackgroundColor(mLandingInstance!!.whiteColor)
        llNotesInner.setBackgroundResource(R.drawable.white_short_profile_background)
        viewLineNotes.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        pbReceivedNotes.setIndicatorColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        pbMyNotes.setIndicatorColor(ContextCompat.getColor(activity, R.color.colorPrimary))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayNightMode() {
        llCustomToolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        txtTitleCustom.setTextColor(ContextCompat.getColor(activity, R.color.white_color))
        imgOption2Custom.background = ContextCompat.getDrawable(activity, R.drawable.black_ripple)
        llMainNotes.setBackgroundColor(mLandingInstance!!.blackColor)
        llNotesInner.setBackgroundResource(R.drawable.gradient_upper_round_corner)
        viewLineNotes.setBackgroundColor(ContextCompat.getColor(activity, R.color.black_color))
        pbReceivedNotes.setIndicatorColor(ContextCompat.getColor(activity, R.color.white_color))
        pbMyNotes.setIndicatorColor(ContextCompat.getColor(activity, R.color.white_color))
    }

    private fun initListener() {
        txtNotesHint.setOnClickListener(this)
        txtMyNotes.setOnClickListener(this)
        imgOption2Custom.setOnClickListener(this)
        txtReceived.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        var intent: Intent? = null
        when (view) {
            imgOption2Custom -> {
                intent = Intent(mContext, SearchActivity::class.java)
                intent.putExtra("path", "notes")
                if (isMyNotes)
                    intent.putExtra("noteType", Constants.MYNOTES)
                else
                    intent.putExtra("noteType", Constants.RECEIVEDNOTES)
                startActivityForResult(intent, SEARCH)
            }
            txtNotesHint -> {
                intent = Intent(activity, NotesActivity::class.java)
                startActivityForResult(intent, NOTES)
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            txtMyNotes -> {
                if (!isMyNotes) {
                    viewLineNotes.startAnimation(swiperight)
                    isMyNotes = true
                    setMyNotes()
                    if (mLandingInstance!!.connectedToInternet()) {
                        if (mLandingInstance!!.db!!.getNotesByType(Constants.MYNOTES).size > 0) {
                            hitMyNotesAPI(false)
                            populateData()
                        } else
                            hitMyNotesAPI(true)
                    } else
                        mLandingInstance!!.showInternetAlert(rlMyNotes)
                }
            }
            txtReceived -> {
                if (isMyNotes) {
                    viewLineNotes.startAnimation(swipeleft)
                    isMyNotes = false
                    setReceived()
                    if (mLandingInstance!!.connectedToInternet()) {
                        if (mLandingInstance!!.db!!.getNotesByType(Constants.RECEIVEDNOTES).size > 0) {
                            hitReceivedNotesAPI(false)
                            populateReceivedData()
                        } else
                            hitReceivedNotesAPI(true)
                    } else
                        mLandingInstance!!.showInternetAlert(rlMyNotes)
                }
            }
        }
    }

    private fun hitMyNotesAPI(isVisible: Boolean) {
        if (isVisible)
            pbMyNotes.visibility = View.VISIBLE

        val call = RetrofitClient.getInstance().getNotes(mLandingInstance!!.mUtils!!.getString("access_token", ""),
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
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(llMainNotes, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<NotesListingModel>?, t: Throwable?) {
                if (isVisible)
                    pbMyNotes.visibility = View.GONE
            }
        })
    }

    private fun hitReceivedNotesAPI(isVisible: Boolean) {
        if (isVisible)
            pbReceivedNotes.visibility = View.VISIBLE

        val call = RetrofitClient.getInstance().getNotes(mLandingInstance!!.mUtils!!.getString("access_token", ""),
                Constants.RECEIVEDNOTES, mReceivedNotesOffset.toString())
        call.enqueue(object : Callback<NotesListingModel> {
            override fun onResponse(call: Call<NotesListingModel>?, response: Response<NotesListingModel>) {
                if (isVisible)
                    pbReceivedNotes.visibility = View.GONE

                if (response.body().response != null) {
                    addToLocalDatabase(response.body().response)
                    populateReceivedData()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(llMainNotes, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<NotesListingModel>?, t: Throwable?) {
                if (isVisible)
                    pbReceivedNotes.visibility = View.GONE
            }
        })
    }

    private fun addToLocalDatabase(response: List<NotesListingModel.ResponseBean>) {
        for (notesData in response) {
            mLandingInstance!!.db!!.addNotes(notesData)
        }
    }


    private fun populateData() {
        mMyNotesArray.clear()
        mMyNotesArray.addAll(mLandingInstance!!.db!!.getNotesByType(Constants.MYNOTES))

        if (mMyNotesAdapter == null) {
            mMyNotesAdapter = MyNotesAdapter(mMyNotesArray, mContext!!, mNotesFragment)
            rvMyNotes.adapter = mMyNotesAdapter
        } else
            mMyNotesAdapter!!.notifyDataSetChanged()

        if (rlMyNotes.visibility == View.VISIBLE) {
            if (mMyNotesArray.size == 0) {
                txtNoMyNotes.visibility = View.VISIBLE
                rvMyNotes.visibility = View.GONE
            } else {
                txtNoMyNotes.visibility = View.GONE
                rvMyNotes.visibility = View.VISIBLE
            }
        }

        if (mMyNotesArray.size == 0 && mReceivedArray.size == 0) {
            txtNoAllNotes.visibility = View.VISIBLE
            llNotesInner.visibility = View.GONE
        } else {
            txtNoAllNotes.visibility = View.GONE
            llNotesInner.visibility = View.VISIBLE
        }
    }

    private fun populateReceivedData() {
        mReceivedArray.clear()
        mReceivedArray.addAll(mLandingInstance!!.db!!.getNotesByType(Constants.RECEIVEDNOTES))

        if (mReceivedNotesAdapter == null) {
            mReceivedNotesAdapter = ReceivedNotesAdapter(mReceivedArray, mContext!!, mNotesFragment)
            rvReceivedNotes.adapter = mReceivedNotesAdapter
        } else
            mReceivedNotesAdapter!!.notifyDataSetChanged()

        if (rlReceivedNotes.visibility == View.VISIBLE) {
            if (mReceivedArray.size == 0) {
                txtNoReceivedNotes.visibility = View.VISIBLE
                rvReceivedNotes.visibility = View.GONE
            } else {
                txtNoReceivedNotes.visibility = View.GONE
                rvReceivedNotes.visibility = View.VISIBLE
            }
        }

        if (mMyNotesArray.size == 0 && mReceivedArray.size == 0) {
            txtNoAllNotes.visibility = View.VISIBLE
            llNotesInner.visibility = View.GONE
        } else {
            txtNoAllNotes.visibility = View.GONE
            llNotesInner.visibility = View.VISIBLE
        }

    }


    private fun setMyNotes() {
        rlMyNotes.visibility = View.VISIBLE
        rlReceivedNotes.visibility = View.GONE
        txtReceived.alpha = 0.4f
        txtMyNotes.alpha = 1f
    }

    private fun setReceived() {
        rlMyNotes.visibility = View.GONE
        rlReceivedNotes.visibility = View.VISIBLE
        txtReceived.alpha = 1f
        txtMyNotes.alpha = 0.4f
    }

    fun moveToDetail(notesData: NotesListingModel.ResponseBean) {
        val intent = Intent(mContext, NotesActivity::class.java)
        intent.putExtra("notesData", notesData)
        startActivityForResult(intent, NOTES)
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == NOTES || requestCode == SEARCH) {
                populateData()
                populateReceivedData()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun deleteNote(noteId: Int) {
        val alertDialog = AlertDialog.Builder(mContext!!)
        alertDialog.setTitle("DELETE NOTE")
        alertDialog.setMessage("Are you sure you want to delete this note?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            if (mLandingInstance!!.connectedToInternet())
                hitDeleteAPI(noteId)
            else
                mLandingInstance!!.showToast(mContext!!, getString(R.string.internet))
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    private fun hitDeleteAPI(noteId: Int) {
        mLandingInstance!!.showLoader()
        val call = RetrofitClient.getInstance().deleteNote(mLandingInstance!!.mUtils!!.getString("access_token", ""),
                noteId.toString())
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                mLandingInstance!!.dismissLoader()
                if (response!!.body().response != null) {
                    mLandingInstance!!.db!!.deleteNotes(noteId)
                    populateData()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(llMainNotes, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                mLandingInstance!!.dismissLoader()
                mLandingInstance!!.showToast(mContext!!, t!!.localizedMessage)
            }
        })
    }

    fun moveToShare(responseBean: NotesListingModel.ResponseBean) {
        val intent = Intent(mContext!!, ShareActivity::class.java)
        intent.putExtra("notesData", responseBean)
        startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }

    fun deleteReceivedNote(noteId: Int, name: String) {
        val alertDialog = AlertDialog.Builder(mContext!!)
        alertDialog.setTitle("DELETE NOTE")
        alertDialog.setMessage("Are you sure you want to delete this note?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            if (mLandingInstance!!.connectedToInternet())
                hitReceivedDeleteAPI(noteId, name)
            else
                mLandingInstance!!.showToast(mContext!!, getString(R.string.internet))
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    private fun hitReceivedDeleteAPI(noteId: Int, name: String) {
        mLandingInstance!!.showLoader()
        val call = RetrofitClient.getInstance().deleteReceivedNote(mLandingInstance!!.mUtils!!.getString("access_token", ""),
                noteId.toString(), name)
        call.enqueue(object : Callback<BaseSuccessModel> {
            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>?) {
                mLandingInstance!!.dismissLoader()
                if (response!!.body().response != null) {
                    mLandingInstance!!.db!!.deleteNotes(noteId)
                    populateReceivedData()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        mLandingInstance!!.moveToSplash()
                    } else
                        mLandingInstance!!.showAlert(llMainNotes, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                mLandingInstance!!.dismissLoader()
                mLandingInstance!!.showToast(mContext!!, t!!.localizedMessage)
            }
        })
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
            if (intent.getIntExtra("status", 0) == Constants.DAY)
                displayDayMode()
            else
                displayNightMode()

            if (isMyNotes) {
                isLoadingMyNotes = false
                mMyNotesOffset = 1
                populateData()
            } else {
                isLoadingReceivedNotes = false
                mReceivedNotesOffset = 1
                populateReceivedData()
            }

        }
    }

}