package fragments

import adapters.MyNotesAdapter
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import com.seeaspark.*
import kotlinx.android.synthetic.main.activity_events_details.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_interested.*
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.android.synthetic.main.fragment_notes.*
import models.BaseSuccessModel
import models.NotesListingModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class NotesFragment : Fragment(), View.OnClickListener {

    var mLandingInstance: LandingActivity? = null
    var itemView: View? = null
    var mContext: Context? = null
    private val NOTES = 1
    private val SEARCH: Int = 2


    private var intialViewPosition: Int = 0
    private var swipeleft: TranslateAnimation? = null
    private var swiperight: TranslateAnimation? = null

    private var isMyNotes = true
    private val mMyNotesOffset = 1
    private var mLayoutManager: LinearLayoutManager? = null

    private var mMyNotesArray = ArrayList<NotesListingModel.ResponseBean>()
    private var mMyNotesAdapter: MyNotesAdapter? = null
    private var mNotesFragment: NotesFragment? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_notes, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mLandingInstance = activity as LandingActivity
        mContext = activity
        mNotesFragment = this
        initUI()
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initUI() {

        txtTitleCustom.text = getString(R.string.notes)
        txtTitleCustom.setPadding(Constants.dpToPx(16), 0, 0, 0)

        imgBackCustom.visibility = View.GONE

        imgOption2Custom.setImageResource(R.mipmap.ic_search)
        imgOption2Custom.visibility = View.VISIBLE

        mLayoutManager = LinearLayoutManager(mContext)

        rvMyNotes.layoutManager = mLayoutManager

        intialViewPosition = ((mLandingInstance!!.mWidth * 0.5).toInt())

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
            if (mLandingInstance!!.db!!.getNotesByType(Constants.MYNOTES).size > 0) {
                hitMyNotesAPI(false)
                populateData()
            } else {
                hitMyNotesAPI(true)
            }
        } else
            mLandingInstance!!.showInternetAlert(rvCommunityListing)
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
                    populateData()
                }
            }
            txtReceived -> {
                if (isMyNotes) {
                    viewLineNotes.startAnimation(swipeleft)
                    isMyNotes = false
                    setReceived()
                    txtNoNotes.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun hitMyNotesAPI(isVisible: Boolean) {
        if (isVisible)
            pbNotes.visibility = View.VISIBLE

        val call = RetrofitClient.getInstance().getNotes(mLandingInstance!!.mUtils!!.getString("access_token", ""),
                Constants.MYNOTES, mMyNotesOffset.toString())
        call.enqueue(object : Callback<NotesListingModel> {
            override fun onResponse(call: Call<NotesListingModel>?, response: Response<NotesListingModel>) {
                if (isVisible)
                    pbNotes.visibility = View.GONE

                if (response.body().response != null) {
                    mMyNotesArray.addAll(response.body().response)
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
                    pbNotes.visibility = View.GONE
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

        if (mMyNotesArray.size == 0) {
            txtNoNotes.visibility = View.VISIBLE
            rvMyNotes.visibility = View.GONE
        } else {
            txtNoNotes.visibility = View.GONE
            rvMyNotes.visibility = View.VISIBLE
        }
    }

    private fun setMyNotes() {
        txtReceived.alpha = 0.4f
        txtMyNotes.alpha = 1f
    }

    private fun setReceived() {
        rvMyNotes.visibility = View.GONE
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
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

}