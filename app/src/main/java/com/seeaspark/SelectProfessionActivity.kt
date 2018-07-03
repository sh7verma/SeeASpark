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
import models.ProfessionModel
import models.SignupModel
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

    override fun onCreateStuff() {

        mProfession = intent.getIntExtra("professionId", 0)
        mProfessionName = intent.getStringExtra("professionName")

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        mProfessionListing = this

        mProfessionArray.addAll(userData!!.professions)

        rvProfessionListing.layoutManager = LinearLayoutManager(this)
        mAdapterProfession = ProfessionListingAdapter(this, mProfessionArray, mProfessionListing!!)
        rvProfessionListing.adapter = mAdapterProfession


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