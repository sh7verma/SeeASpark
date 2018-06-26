package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_select_skill.*
import kotlinx.android.synthetic.main.layout_skills.view.*
import models.SignupModel
import models.SkillsModel
import utils.Constants

class SelectSkillActivity : BaseActivity() {

    private var mSkillsSelectedArray = ArrayList<String>()
    private var mOwnSkillsArray = ArrayList<SkillsModel>()
    var mSkillsArrayText = ArrayList<String>()

    var mSkillsArray = ArrayList<SkillsModel>()
    private val ADD_SKILLS: Int = 1
    private var userData: SignupModel? = null


    override fun getContentView() = R.layout.activity_select_skill


    override fun initUI() {
    }

    override fun onCreateStuff() {

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        /// Creating only text array to avoid duplicacy
        for (skillsValue in userData!!.skills) {
            mSkillsArrayText.add(skillsValue.name)
        }

        /// adding Skills from server and first element
        var skillPLusModel = SkillsModel()
        skillPLusModel.isFirstElement = true
        mSkillsArray.add(skillPLusModel)
        mSkillsArray.addAll(userData!!.skills)

        mSkillsSelectedArray.addAll(intent.getStringArrayListExtra("selectedSkills"))

        for (skillValue: SkillsModel in mSkillsArray) {
            flSkillsSelect.addView(inflateView(skillValue))
        }
    }

    override fun initListener() {
        txtNextSkillsSelect.setOnClickListener(this)
        imgBackSkillsSelect.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            txtNextSkillsSelect -> {
                if (mSkillsSelectedArray.size == 0)
                    showAlert(txtNextSkillsSelect, getString(R.string.error_skills))
                else {
                    val intent = Intent()
                    intent.putStringArrayListExtra("selectedSkills", mSkillsSelectedArray)
                    setResult(Activity.RESULT_OK, intent)
                    moveBack()
                }
            }
            imgBackSkillsSelect -> {
                moveBack()
            }
        }
    }

    private fun inflateView(skillValue: SkillsModel): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.layout_skills, null, false)

        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Constants.dpToPx(52))
        interestChip.llMainSkills.layoutParams = innerParms

        if (!skillValue.isFirstElement) {
            interestChip.imgSkillAdd.visibility = View.GONE
            interestChip.txtSkillChip.visibility = View.VISIBLE
        } else {
            interestChip.imgSkillAdd.visibility = View.VISIBLE
            interestChip.txtSkillChip.visibility = View.GONE
        }

        if (mSkillsSelectedArray.contains(skillValue.name)) {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        } else {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        }

        interestChip.txtSkillChip.text = skillValue.name

        interestChip.imgSkillAdd.setOnClickListener {
            val intent = Intent(this, AddSkillsActivity::class.java)
            intent.putParcelableArrayListExtra("skillsArray", mOwnSkillsArray)
            intent.putParcelableArrayListExtra("allSkillsArray", mSkillsArray)
            startActivityForResult(intent, ADD_SKILLS)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }

        interestChip.txtSkillChip.setOnClickListener {
            if (mSkillsSelectedArray.contains(skillValue.name)) {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
                mSkillsSelectedArray.remove(skillValue.name)
            } else {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
                mSkillsSelectedArray.add(skillValue.name)
            }
            Log.e("Add/Remove = ", skillValue.name)
        }

        return interestChip
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_SKILLS -> {
                    flSkillsSelect.removeAllViews()
                    mOwnSkillsArray.clear()
                    mOwnSkillsArray.addAll(data!!.getParcelableArrayListExtra<Parcelable>("skillsArray") as ArrayList<out SkillsModel>)

                    for (skillValue: SkillsModel in mOwnSkillsArray) {
                        if (!mSkillsArrayText.contains(skillValue.name)) {
                            mSkillsArray.add(1, skillValue)
                            mSkillsArrayText.add(skillValue.name)
                        }

                        if (!mSkillsSelectedArray.contains(skillValue.name))
                            mSkillsSelectedArray.add(skillValue.name)
                    }

                    for (skillValue: SkillsModel in mSkillsArray) {
                        flSkillsSelect.addView(inflateView(skillValue))
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }


}