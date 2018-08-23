package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.NotesListingModel
import models.NotesModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import utils.MainApplication

class NotesActivity : BaseActivity() {

    var isBold = false
    var isItalic = false
    var isUnderLine = false
    var isDoneEnabled = false
    var isStriked = false
    var mNotesData: NotesListingModel.ResponseBean? = null
    var isEdit = false
    var noteId = Constants.EMPTY!!
    var noteFileName = Constants.EMPTY!!

    override fun getContentView() = R.layout.activity_notes

    override fun initUI() {
        txtTitleCustom.text = getString(R.string.write_note)
        imgBackCustom.setImageResource(R.mipmap.ic_back_org)

        txtOptionCustom.visibility = View.VISIBLE
        txtOptionCustom.text = getString(R.string.done)
        txtOptionCustom.setTextColor(ContextCompat.getColor(mContext!!, R.color.colorPrimary))

        mEditor.setEditorFontSize(16)
        mEditor.setPlaceholder("Write here...")
        mEditor.setPadding(10, 10, 10, 10)
        mEditor.setEditorWidth(mWidth)
        mEditor.focusEditor()
        mEditor.setTextColor(Color.BLACK)

        if (intent.hasExtra("notesData")) {/// getting data from previous activity
            mNotesData = intent.getParcelableExtra("notesData")
            if (mNotesData!!.user_id.toString() == mUtils!!.getString("user_id", "")) {/// own Note
                setEditorData()
                isEdit = true
                isDoneEnabled = true
            } else
                setNonEditableMode()
        }

        if (intent.hasExtra("noteId")) {/// getting data from deep Linking
            noteId = intent.getStringExtra("noteId")
            noteFileName = intent.getStringExtra("noteFileName")

            if (connectedToInternet())
                hitFetchNotesDetailAPI()
            else
                showInternetAlert(llUnderline)
        }

        mEditor.setOnDecorationChangeListener { text, types ->
            unSelectItalic()
            unSelectBold()
            unSelectStrikeThrough()
            unSelectUnderLine()
            for (type in types!!) {
                Log.e("Type = ", type.name)
                if (type.name == "BOLD") {
                    selectBold()
                }
                if (type.name == "ITALIC") {
                    setItalic()
                }
                if (type.name == "UNDERLINE") {
                    selectUnderLine()
                }
                if (type.name == "STRIKETHROUGH") {
                    selectStrikeThrough()
                }

                when {
                    type.name == "BLACK" -> {
                        selectBlack()
                    }
                    type.name == "RED" -> {
                        selectRed()
                    }
                    type.name == "GREEN" -> {
                        selectGreen()
                    }
                    type.name == "BLUE" -> {
                        selectBlue()
                    }
                }

            }
        }

        mEditor.setOnTextChangeListener({
            if (it.isEmpty()) {
                isDoneEnabled = false
                mEditor.removeFormat()
                resetEditior()
            } else {
                isDoneEnabled = true
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {


    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        // no op
    }

    override fun onCreateStuff() {
        // no op
    }

    override fun initListener() {
        llBold.setOnClickListener(this)
        llItalic.setOnClickListener(this)
        llUnderline.setOnClickListener(this)
        llStrike.setOnClickListener(this)
        rlBlack.setOnClickListener(this)
        rlRed.setOnClickListener(this)
        rlGreen.setOnClickListener(this)
        rlBlue.setOnClickListener(this)
        imgBackCustom.setOnClickListener(this)
        txtOptionCustom.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View) {
        when (view) {
            imgBackCustom -> {
                Constants.closeKeyboard(mContext!!, imgBackCustom)
                moveBack()
            }
            txtOptionCustom -> {
                if (connectedToInternet()) {
                    if (isDoneEnabled) {
                        if (isEdit)
                            hitEditAPI()
                        else
                            hitAPI()
                    } else
                        showAlert(txtOptionCustom, getString(R.string.error_note))
                } else
                    showInternetAlert(txtOptionCustom)
            }
            llBold -> {
                if (!isBold) {
                    selectBold()
                } else {
                    unSelectBold()
                }
                mEditor.setBold()
            }
            llStrike -> {
                if (!isStriked) {
                    selectStrikeThrough()
                } else {
                    unSelectStrikeThrough()
                }
                mEditor.setStrikeThrough()
            }
            llItalic -> {
                if (!isItalic) {
                    setItalic()
                } else {
                    unSelectItalic()
                }
                mEditor.setItalic()
            }
            llUnderline -> {
                if (!isUnderLine) {
                    selectUnderLine()
                } else {
                    unSelectUnderLine()
                }
                mEditor.setUnderline()
            }
            rlBlack -> {
                selectBlack()
                persistState()
            }
            rlRed -> {
                selectRed()
                persistState()
            }
            rlGreen -> {
                selectGreen()
                persistState()
            }
            rlBlue -> {
                selectBlue()
                persistState()
            }
        }
    }

    private fun persistState() {
        if (!isDoneEnabled) {
            if (isBold)
                mEditor.setBold()
            if (isItalic)
                mEditor.setItalic()
            if (isUnderLine)
                mEditor.setUnderline()
            if (isStriked)
                mEditor.setStrikeThrough()
        }
    }

    @Suppress("DEPRECATION")
    private fun moveBack() {
        if (MainApplication.isLandingAvailable) {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        } else {
            val intent = Intent(mContext, LandingActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
        }
    }

    @Suppress("DEPRECATION")
    private fun hitAPI() {
        showLoader()
        var serverText = Html.fromHtml(Html.fromHtml(mEditor.html).toString()).toString()
        Log.e("Title", "serverText")
        Log.e("Desc", mEditor.html)

        if (serverText.length > 100)
            serverText = serverText.substring(0, 100)

        val call = RetrofitClient.getInstance().addNotes(mUtils!!.getString("access_token", ""),
                serverText, mEditor.html)
        call.enqueue(object : Callback<NotesModel> {

            override fun onResponse(call: Call<NotesModel>?, response: Response<NotesModel>) {
                if (response.body().response != null) {
                    db!!.addNotes(response.body().response)
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    moveBack()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(llCustomToolbar, response.body().error!!.message!!)
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<NotesModel>?, t: Throwable?) {
                dismissLoader()
            }
        })
    }

    @Suppress("DEPRECATION")
    private fun hitEditAPI() {
        showLoader()
        var serverText = Html.fromHtml(Html.fromHtml(mEditor.html).toString()).toString()
        Log.e("Title", "serverText")
        Log.e("Desc", mEditor.html)

        if (serverText.length > 100)
            serverText = serverText.substring(0, 100)

        val call = RetrofitClient.getInstance().updateNotes(mUtils!!.getString("access_token", ""),
                mNotesData!!.id.toString(),
                serverText, mEditor.html)
        call.enqueue(object : Callback<NotesModel> {

            override fun onResponse(call: Call<NotesModel>?, response: Response<NotesModel>) {
                if (response.body().response != null) {
                    db!!.addNotes(response.body().response)
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    moveBack()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(llCustomToolbar, response.body().error!!.message!!)
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<NotesModel>?, t: Throwable?) {
                dismissLoader()
            }
        })
    }

    private fun setEditorData() {
        mEditor.html = mNotesData!!.description
        mEditor.focusEditor()
    }

    private fun hitFetchNotesDetailAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().fetchDetailNotes(mUtils!!.getString("access_token", ""),
                noteId, noteFileName)
        call.enqueue(object : Callback<NotesModel> {
            override fun onResponse(call: Call<NotesModel>?, response: Response<NotesModel>?) {
                if (response!!.body().response != null) {
                    mNotesData = response.body().response
                    if (mNotesData!!.user_id.toString() == mUtils!!.getString("user_id", "")) {
                        setEditorData()
                        mNotesData!!.note_type = Constants.MYNOTES
                        isEdit = true
                        isDoneEnabled = true
                    } else {
                        setNonEditableMode()
                    }
                    db!!.addNotes(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(llCustomToolbar, response.body().error!!.message!!)
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<NotesModel>?, t: Throwable?) {
                showAlert(llUnderline, t!!.localizedMessage)
                dismissLoader()
            }
        })
    }

    private fun setNonEditableMode() {
        txtTitleCustom.text = getString(R.string.note)
        mNotesData!!.note_type = Constants.RECEIVEDNOTES
        Constants.closeKeyboard(mContext!!, llCustomToolbar)
        mEditor.html = mNotesData!!.description
        mEditor.setInputEnabled(false)
        txtOptionCustom.visibility = View.INVISIBLE
        llOptions.visibility = View.GONE
    }

    private fun resetEditior() {
        if (isBold) {
            mEditor.setBold()
            unSelectBold()
            isBold = false
        }
        if (isItalic) {
            mEditor.setItalic()
            unSelectItalic()
            isItalic = false
        }
        if (isUnderLine) {
            mEditor.setUnderline()
            unSelectUnderLine()
            isUnderLine = false
        }
        selectBlack()
    }

    private fun unSelectUnderLine() {
        isUnderLine = false
        imgUnderline.setImageResource(R.mipmap.ic_u)
    }

    private fun selectUnderLine() {
        imgUnderline.setImageResource(R.mipmap.ic_sel_u)
        isUnderLine = true
    }

    private fun unSelectItalic() {
        isItalic = false
        imgItalic.setImageResource(R.mipmap.ic_i)
    }

    private fun setItalic() {
        imgItalic.setImageResource(R.mipmap.ic_sel_i)
        isItalic = true
    }

    private fun unSelectBold() {
        isBold = false
        imgBold.setImageResource(R.mipmap.ic_b)
    }

    private fun selectBold() {
        imgBold.setImageResource(R.mipmap.ic_sel_b)
        isBold = true
    }

    private fun unSelectStrikeThrough() {
        isStriked = false
        imgStrike.setImageResource(R.mipmap.ic_strike)
    }

    private fun selectStrikeThrough() {
        imgStrike.setImageResource(R.mipmap.ic_strike_s)
        isStriked = true
    }

    private fun selectBlue() {
        imgSelectedBlack.visibility = View.GONE
        imgSelectedRed.visibility = View.GONE
        imgSelectedGreen.visibility = View.GONE
        imgSelectedBlue.visibility = View.VISIBLE
        mEditor.setTextColor(ContextCompat.getColor(mContext!!, R.color.blue_color))
    }

    private fun selectGreen() {
        imgSelectedBlack.visibility = View.GONE
        imgSelectedRed.visibility = View.GONE
        imgSelectedGreen.visibility = View.VISIBLE
        imgSelectedBlue.visibility = View.GONE
        mEditor.setTextColor(ContextCompat.getColor(mContext!!, R.color.green_color))
    }

    private fun selectRed() {
        imgSelectedBlack.visibility = View.GONE
        imgSelectedRed.visibility = View.VISIBLE
        imgSelectedGreen.visibility = View.GONE
        imgSelectedBlue.visibility = View.GONE
        mEditor.setTextColor(ContextCompat.getColor(mContext!!, R.color.red_color))
    }

    private fun selectBlack() {
        imgSelectedBlack.visibility = View.VISIBLE
        imgSelectedRed.visibility = View.GONE
        imgSelectedGreen.visibility = View.GONE
        imgSelectedBlue.visibility = View.GONE
        mEditor.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
    }


}