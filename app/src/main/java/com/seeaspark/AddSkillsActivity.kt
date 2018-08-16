package com.seeaspark

import adapters.SkillSuggestionAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_add_skills.*
import kotlinx.android.synthetic.main.add_skills.view.*
import models.SearchSkillModel
import models.SkillsModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class AddSkillsActivity : BaseActivity() {

    private var mSkillsArray = ArrayList<SkillsModel>()
    private var mAllSkillsArray = ArrayList<SkillsModel>()
    private var mTempArray = ArrayList<String>()
    private var mAddSkillsActivity: AddSkillsActivity? = null
    private var mCallServer: Boolean = true

    var call: Call<SearchSkillModel>? = null

    override fun initUI() {
        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        edSkill.typeface = typeface

        rvSkillSuggestions.layoutManager = LinearLayoutManager(this)
    }

    override fun displayDayMode() {
        /// no operation
    }

    override fun displayNightMode() {
        /// no operation
    }

    override fun onCreateStuff() {

        mAddSkillsActivity = this

        mSkillsArray.addAll(intent.getParcelableArrayListExtra("skillsArray"))
        mAllSkillsArray.addAll(intent.getParcelableArrayListExtra("allSkillsArray"))

        for (skillValue in mAllSkillsArray) {
            mTempArray.add(skillValue.name)
        }

        for (skillValue: SkillsModel in mSkillsArray) {
            flAddSkills.addView(inflateView(skillValue))
        }

        edSkill.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (char.toString().isNotEmpty()) {
                    if (mCallServer)
                        hitAPI(char.toString())
                    else
                        mCallServer = true
                } else {
                    cvSkillSuggestion.visibility = View.INVISIBLE
                }
            }
        })

    }

    private fun hitAPI(searchText: String) {
        call = RetrofitClient.getInstance().searchSkills(mUtils!!.getString("access_token", ""), searchText)
        call!!.enqueue(object : Callback<SearchSkillModel> {
            override fun onResponse(call: Call<SearchSkillModel>?, response: Response<SearchSkillModel>) {
                if (response.body().response.size > 0)
                    cvSkillSuggestion.visibility = View.VISIBLE
                else
                    cvSkillSuggestion.visibility = View.INVISIBLE

                rvSkillSuggestions.adapter = SkillSuggestionAdapter(response.body().response as ArrayList<SkillsModel>,
                        mContext!!, mAddSkillsActivity)
            }

            override fun onFailure(call: Call<SearchSkillModel>?, t: Throwable?) {
                showAlert(imgPlusSkill, t!!.localizedMessage)
            }
        })
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
                        showAlert(imgPlusSkill, getString(R.string.skill_error))
                    else
                        addSkills()
                } else
                    showAlert(imgPlusSkill, getString(R.string.enter_skill_error))
            }
        }
    }

    private fun addSkills() {
        flAddSkills.removeAllViews()

        mTempArray.add(edSkill.text.toString().trim())

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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
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

    fun selectSkill(name: String?) {
        mCallServer = false
        cvSkillSuggestion.visibility = View.INVISIBLE
        rvSkillSuggestions.adapter = null
        edSkill.setText(name)
        edSkill.setSelection(edSkill.text.toString().length)
    }
}