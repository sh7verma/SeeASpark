package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_skill_selection.*
import kotlinx.android.synthetic.main.layout_skills.view.*
import models.SkillsModel
import utils.Constants


class SkillSelectionActivity : BaseActivity() {

    private var mSkillsSelectedArray = ArrayList<String>()
    private var mSkillsArray = ArrayList<SkillsModel>()

    override fun initUI() {

    }

    override fun onCreateStuff() {
        addValue()
        for (skillValue: SkillsModel in mSkillsArray) {
            flSkillsSelection.addView(inflateView(skillValue))
        }
    }

    private fun addValue() {
        mSkillsArray.add(SkillsModel("Lorem", false, false))
        mSkillsArray.add(SkillsModel("Ipsum", false, false))
        mSkillsArray.add(SkillsModel("Dollar", false, false))
        mSkillsArray.add(SkillsModel("Sit", false, false))
        mSkillsArray.add(SkillsModel("Amet", false, false))
        mSkillsArray.add(SkillsModel("Enim", false, false))
        mSkillsArray.add(SkillsModel("Aliqua UT", false, false))
    }

    override fun initListener() {
        txtDoneSelection.setOnClickListener(this)
        imgBackSelection.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_skill_selection

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtDoneSelection -> {
                var intent = Intent()
                intent.putStringArrayListExtra("selectedSkills", mSkillsSelectedArray);
                setResult(Activity.RESULT_OK, intent)
                moveBack()
            }
            imgBack -> {
                moveBack()
            }
        }
    }

    private fun inflateView(skillValue: SkillsModel): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.layout_skills, null, false)

        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Constants.dpToPx(52))
        interestChip.llMainSkills.layoutParams = innerParms

        interestChip.imgSkillAdd.visibility = View.GONE

        if (mSkillsSelectedArray.contains(skillValue.skill)) {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        } else {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        }

        interestChip.txtSkillChip.text = skillValue.skill

        interestChip.txtSkillChip.setOnClickListener {
            if (mSkillsSelectedArray.contains(skillValue.skill)) {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
                mSkillsSelectedArray.remove(skillValue.skill)
            } else {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
                mSkillsSelectedArray.add(skillValue.skill)
            }
            Log.e("Add/Remove = ", skillValue.skill)
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