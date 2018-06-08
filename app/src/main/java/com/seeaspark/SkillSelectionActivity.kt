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
import models.SkillsModel
import utils.Constants


class SkillSelectionActivity : BaseActivity() {

    private var mSkillsSelectedArray = ArrayList<String>()
    private var mSkillsArray = ArrayList<SkillsModel>()

    override fun initUI() {

    }

    override fun onCreateStuff() {

        for (skillValue in Constants.tempSkills) {
            flSkillsSelection.addView(inflateView(skillValue))
        }
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
            imgBackSelection -> {
                moveBack()
            }
        }
    }

    private fun inflateView(skillValue: String): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.layout_skills, null, false)

        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Constants.dpToPx(52))
        interestChip.llMainSkills.layoutParams = innerParms

        interestChip.imgSkillAdd.visibility = View.GONE

        if (mSkillsSelectedArray.contains(skillValue)) {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        } else {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        }

        interestChip.txtSkillChip.text = skillValue

        interestChip.txtSkillChip.setOnClickListener {
            if (mSkillsSelectedArray.contains(skillValue)) {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
                mSkillsSelectedArray.remove(skillValue)
            } else {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
                mSkillsSelectedArray.add(skillValue)
            }
            Log.e("Add/Remove = ", skillValue)
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