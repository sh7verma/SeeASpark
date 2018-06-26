package com.seeaspark

import adapters.ProfessionListingAdapter
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_profession_listing.*
import models.ProfessionModel
import models.SignupModel


class SelectProfessionActivity : BaseActivity() {

    var mProfession: Int = 0
    var mProfessionName: String? = null
    private var mAdapterProfession: ProfessionListingAdapter? = null
    private var mProfessionListing: SelectProfessionActivity? = null
    private var mProfessionArray = ArrayList<ProfessionModel>()
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

    override fun onBackPressed() {
        moveBack()
    }

    fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}