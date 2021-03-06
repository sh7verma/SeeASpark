package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_skill_selection.*
import kotlinx.android.synthetic.main.layout_skills.view.*
import models.ServerSkillsModel
import models.SignupModel
import models.SkillsModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants


class SkillSelectionActivity : BaseActivity() {


    private var mSkillsSelectedArray = ArrayList<String>()
    private var mSelectedSkillsNameArray = ArrayList<String>()
    private var userData: SignupModel? = null

    override fun getContentView() = R.layout.activity_skill_selection

    override fun initUI() {

    }

    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        mSkillsSelectedArray.clear()
        mSkillsSelectedArray.addAll(intent.getStringArrayListExtra("selectedSkills"))

        mSelectedSkillsNameArray.clear()
        mSelectedSkillsNameArray.addAll(intent.getStringArrayListExtra("selectedSkillsName"))

        for (skillValue in userData!!.skills) {
            flSkillsSelection.addView(inflateView(skillValue))
        }
        if (connectedToInternet())
            hitAPI()
        else
            showInternetAlert(txtClearLanguage)
    }

    override fun displayDayMode() {
    }

    override fun displayNightMode() {
    }

    override fun initListener() {
        txtClearLanguage.setOnClickListener(this)
        imgBackSelection.setOnClickListener(this)
        txtDonePreferLanguage.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtDonePreferLanguage -> {
                val intent = Intent()
                intent.putStringArrayListExtra("selectedSkills", mSkillsSelectedArray);
                intent.putStringArrayListExtra("selectedSkillsName", mSelectedSkillsNameArray);
                setResult(Activity.RESULT_OK, intent)
                moveBack()
            }
            txtClearLanguage->{
                mSkillsSelectedArray.clear()
                mSelectedSkillsNameArray.clear()
                flSkillsSelection.removeAllViews()
                for (skillValue in userData!!.skills) {
                    flSkillsSelection.addView(inflateView(skillValue))
                }
            }
            imgBackSelection -> {
                moveBack()
            }
        }
    }

    private fun hitAPI() {
        val call = RetrofitClient.getInstance().getUserSkills(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<ServerSkillsModel> {
            override fun onResponse(call: Call<ServerSkillsModel>?, response: Response<ServerSkillsModel>?) {
                userData!!.skills.clear()
                userData!!.skills.addAll(response!!.body().response)
                flSkillsSelection.removeAllViews()
                for (skillValue in userData!!.skills) {
                    flSkillsSelection.addView(inflateView(skillValue))
                }
            }
            override fun onFailure(call: Call<ServerSkillsModel>?, t: Throwable?) {
                showAlert(txtClearLanguage, t!!.localizedMessage)
            }
        })
    }

    private fun inflateView(skillValue: SkillsModel): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.layout_skills, null, false)

        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Constants.dpToPx(52))
        interestChip.llMainSkills.layoutParams = innerParms

        interestChip.llSkillAdd.visibility = View.GONE

        if (mSkillsSelectedArray.contains(skillValue.id.toString())) {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        } else {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        }

        interestChip.txtSkillChip.text = skillValue.name

        interestChip.txtSkillChip.setOnClickListener {
            if (mSkillsSelectedArray.contains(skillValue.id.toString())) {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
                mSkillsSelectedArray.remove(skillValue.id.toString())
                mSelectedSkillsNameArray.remove(skillValue.name)
            } else {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
                mSkillsSelectedArray.add(skillValue.id.toString())
                mSelectedSkillsNameArray.add(skillValue.name)
            }
            Log.e("Add/Remove = ", skillValue.name)
        }

        return interestChip
    }

    override fun onBackPressed() {
        moveBack()
    }

    fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

}