package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_add_skills.*
import kotlinx.android.synthetic.main.add_skills.view.*
import models.SkillsModel
import utils.Constants

class AddSkillsActivity : BaseActivity() {

    private var mSkillsArray = ArrayList<SkillsModel>()
    private var mAllSkillsArray = ArrayList<SkillsModel>()
    private var mTempArray = ArrayList<String>()

    override fun initUI() {
        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        edSkill.setTypeface(typeface)
    }

    override fun onCreateStuff() {
        mSkillsArray.addAll(intent.getParcelableArrayListExtra("skillsArray"))
        mAllSkillsArray.addAll(intent.getParcelableArrayListExtra("allSkillsArray"))

        for (skillValue in mAllSkillsArray) {
            mTempArray.add(skillValue.name)
        }

        for (skillValue: SkillsModel in mSkillsArray) {
            flAddSkills.addView(inflateView(skillValue))
        }
    }

    override fun initListener() {
        imgCrossSkills.setOnClickListener(this)
        txtDoneSkills.setOnClickListener(this)
        imgPlusSkill.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_add_skills

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgCrossSkills -> {
                moveBack()
            }
            txtDoneSkills -> {
                Constants.closeKeyboard(this, txtDoneSkills)
                val intent = Intent()
                intent.putParcelableArrayListExtra("skillsArray", mSkillsArray)
                setResult(Activity.RESULT_OK, intent)
                moveBack()
            }
            imgPlusSkill -> {
                if (!TextUtils.isEmpty(edSkill.text.toString().trim())) {
                    if (mTempArray.contains(edSkill.text.toString().trim()))
                        showAlert(imgPlusSkill, "This Skill already exist. Please choose another one.")
                    else
                        addSkills()
                } else
                    showAlert(imgPlusSkill, "Please enter a skill")
            }
        }
    }

    private fun addSkills() {
        flAddSkills.removeAllViews()

        mTempArray.add(edSkill.text.toString().trim())
        Constants.tempSkills.add(edSkill.text.toString().trim())

        val newSkillModel = SkillsModel()
        newSkillModel.name = edSkill.text.toString().trim()
        newSkillModel.isSelected = true
        newSkillModel.isFirstElement = false
        mSkillsArray.add(0, newSkillModel)

        for (skillValue: SkillsModel in mSkillsArray) {
            flAddSkills.addView(inflateView(skillValue))
        }
        edSkill.setText(Constants.EMPTY)
    }

    private fun moveBack() {
        Constants.closeKeyboard(mContext, imgPlusSkill)
        finish()
        overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out)
    }

    private fun inflateView(skillValue: SkillsModel): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms
        if (skillValue.isFirstElement) {
            interestChip.txtAddSkillChip.visibility = View.GONE
        }
        interestChip.txtAddSkillChip.text = skillValue.name
        return interestChip
    }
}