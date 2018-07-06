package com.seeaspark

import adapters.LanguageAdapter
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_select_language.*
import kotlinx.android.synthetic.main.activity_signup.*
import models.LanguageModel
import models.SignupModel
import java.util.ArrayList

class SelectLanguageActivity : BaseActivity() {


    var mAdapterLangugae: LanguageAdapter? = null
    private var userData: SignupModel? = null
    var mLanguageArray = ArrayList<LanguageModel>()
    var mSelectedLanguageArray = ArrayList<LanguageModel>()

    override fun getContentView() = R.layout.activity_select_language

    override fun initUI() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        mSelectedLanguageArray = intent.getParcelableArrayListExtra("selectedLanguages")
        mLanguageArray.addAll(userData!!.languages)

        for (selectedLanguage in mSelectedLanguageArray) {
            for (allLanguage in userData!!.languages) {
                if (selectedLanguage.id == allLanguage.id) {
                    allLanguage.isSelected = true
                    break
                }
            }
        }

        rvSelectLanguage.layoutManager = LinearLayoutManager(this)
        mAdapterLangugae = LanguageAdapter(this, mLanguageArray)
        rvSelectLanguage.adapter = mAdapterLangugae
    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    override fun onCreateStuff() {

    }

    override fun initListener() {
        imgBackSelectLanguage.setOnClickListener(this)
        txtNextSelectLanguage.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {

            imgBackSelectLanguage -> {
                moveBack()
            }

            txtNextSelectLanguage -> {
                mSelectedLanguageArray.clear()
                for (languageValue in mLanguageArray) {
                    if (languageValue.isSelected)
                        mSelectedLanguageArray.add(languageValue)
                }

                if (mSelectedLanguageArray.size == 0)
                    showAlert(txtNextSelectLanguage, getString(R.string.error_Language))
                else {
                    val intent = Intent()
                    intent.putParcelableArrayListExtra("selectedLanguages", mSelectedLanguageArray)
                    setResult(Activity.RESULT_OK, intent)
                    moveBack()
                }
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

}