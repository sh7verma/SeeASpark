package com.seeaspark

import adapters.LanguageAdapter
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_select_language.*
import models.LanguageListingModel
import models.LanguageModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.util.*

class SelectLanguageActivity : BaseActivity() {


    var mAdapterLangugae: LanguageAdapter? = null
    private var userData: SignupModel? = null
    var mLanguageArray = ArrayList<LanguageModel>()
    var tempArray = ArrayList<LanguageModel>()
    var mSelectedLanguageArray = ArrayList<LanguageModel>()

    override fun getContentView() = R.layout.activity_select_language

    override fun initUI() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        mSelectedLanguageArray = intent.getParcelableArrayListExtra("selectedLanguages")
        mLanguageArray.addAll(userData!!.languages)
        tempArray.addAll(userData!!.languages)

        rvSelectLanguage.layoutManager = LinearLayoutManager(this)

        edLanguageSelect.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searString = s.toString().toLowerCase()

                if (searString.trim().isEmpty()) {
                    tempArray.clear()
                    tempArray.addAll(mLanguageArray)
                } else {
                    tempArray.clear()
                    for (language in mLanguageArray) {
                        if ((language.name).toLowerCase().contains(searString)) {
                            tempArray.add(language);
                        }
                    }
                }
                populateData()
            }
        })

        populateData()
    }

    private fun populateData() {

        for (selectedLanguage in mSelectedLanguageArray) {
            for (allLanguage in tempArray) {
                if (selectedLanguage.id == allLanguage.id) {
                    allLanguage.isSelected = true
                    break
                }
            }
        }
        mAdapterLangugae = LanguageAdapter(this, tempArray)
        rvSelectLanguage.adapter = mAdapterLangugae
    }

    override fun displayDayMode() {

    }

    override fun displayNightMode() {

    }

    override fun onCreateStuff() {
        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(txtNextSelectLanguage)
    }

    private fun hitAPI() {
        val call = RetrofitClient.getInstance().getLanguages(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<LanguageListingModel> {

            override fun onResponse(call: Call<LanguageListingModel>?, response: Response<LanguageListingModel>) {
                if (response.body().response != null) {
                    mLanguageArray.clear()
                    tempArray.clear()
                    mLanguageArray.addAll(response.body().response)
                    tempArray.addAll(response.body().response)

                    populateData()
                    upDateData(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(txtNextSelectLanguage, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<LanguageListingModel>?, t: Throwable?) {
                showAlert(txtNextSelectLanguage, t!!.localizedMessage)
            }
        })
    }

    private fun upDateData(response: MutableList<LanguageModel>) {
        userData!!.languages.clear()
        userData!!.languages.addAll(response)
        mUtils!!.setString("userDataLocal", mGson.toJson(userData))
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