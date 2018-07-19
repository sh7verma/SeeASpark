package com.seeaspark

import adapters.ProfessionAdapter
import adapters.ProfessionListingAdapter
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_profession_listing.*
import models.LanguageModel
import models.ProfessionListingModel
import models.ProfessionModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants


class SelectProfessionActivity : BaseActivity() {

    var mProfession: Int = 0
    var mProfessionName: String? = null
    private var mAdapterProfession: ProfessionListingAdapter? = null
    private var mProfessionListing: SelectProfessionActivity? = null
    private var mProfessionArray = ArrayList<ProfessionModel>()
    private var tempArray = ArrayList<ProfessionModel>()
    private var userData: SignupModel? = null

    override fun getContentView() = R.layout.activity_profession_listing

    override fun initUI() {

    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }


    override fun onCreateStuff() {

        mProfessionListing = this
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        mProfessionArray.addAll(userData!!.professions)
        populateData()

        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(txtNextProfessionListing)

        mProfession = intent.getIntExtra("professionId", 0)
        mProfessionName = intent.getStringExtra("professionName")



        edProfessionSelect.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searString = s.toString().toLowerCase()

                if (searString.trim().isEmpty()) {
                    tempArray.clear()
                    tempArray.addAll(mProfessionArray)
                    mAdapterProfession = ProfessionListingAdapter(mContext!!, tempArray, mProfessionListing!!)
                    rvProfessionListing.adapter = mAdapterProfession
                } else {
                    tempArray.clear()
                    for (profession in mProfessionArray) {
                        if ((profession.name).toLowerCase().contains(searString)) {
                            tempArray.add(profession);
                        }
                    }
                    mAdapterProfession = ProfessionListingAdapter(mContext!!, tempArray, mProfessionListing!!)
                    rvProfessionListing.adapter = mAdapterProfession
                }
            }
        })
    }

    private fun populateData() {
        rvProfessionListing.layoutManager = LinearLayoutManager(this)
        mAdapterProfession = ProfessionListingAdapter(this, mProfessionArray, mProfessionListing!!)
        rvProfessionListing.adapter = mAdapterProfession
    }

    private fun upDateData(response: MutableList<ProfessionModel>) {
        userData!!.professions.clear()
        userData!!.professions.addAll(response)
        mUtils!!.setString("userDataLocal", mGson.toJson(userData))
    }

    private fun hitAPI() {

        val call = RetrofitClient.getInstance().getProfessions(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<ProfessionListingModel> {

            override fun onResponse(call: Call<ProfessionListingModel>?, response: Response<ProfessionListingModel>) {
                if (response.body().response != null) {
                    mProfessionArray.clear()
                    mProfessionArray.addAll(response.body().response)
                    populateData()
                    upDateData(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(txtNextProfessionListing, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<ProfessionListingModel>?, t: Throwable?) {
                showAlert(txtNextProfessionListing, t!!.localizedMessage)
            }

        })
    }

    override fun initListener() {
        imgBackProfessionListing.setOnClickListener(this)
        txtNextProfessionListing.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackProfessionListing -> {
                moveBack()
            }
            txtNextProfessionListing -> {
                val intent = Intent()
                intent.putExtra("professionId", mProfession)
                intent.putExtra("professionName", mProfessionName)
                setResult(Activity.RESULT_OK, intent)
                moveBack()
            }
        }
    }

    fun setSearchText(mProfessionName: String?) {
        edProfessionSelect.setText(mProfessionName)
        edProfessionSelect.setSelection(edProfessionSelect.text.trim().length)
        Constants.closeKeyboard(this, edProfessionSelect)
    }

    override fun onBackPressed() {
        moveBack()
    }

    fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}