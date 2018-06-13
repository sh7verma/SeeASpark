package com.seeaspark

import adapters.PreferProfessionAdapter
import android.app.Activity
import android.content.Intent
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.cocosw.bottomsheet.BottomSheet
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.android.synthetic.main.activity_preferences.view.*
import kotlinx.android.synthetic.main.add_skills.view.*
import models.ProfessionModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class PreferencesActivity : BaseActivity() {

    var mSelectedSkillsArray = ArrayList<String>()
    var mSelectedSkillsNameArray = ArrayList<String>()
    private val SKILLS: Int = 1

    var mAdapterProfession: PreferProfessionAdapter? = null
    var mProfessionArray = ArrayList<ProfessionModel>()
    private var userData: SignupModel? = null
    private var mGenderValue: Int? = 0

    override fun initUI() {
        val bottomParms = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight / 6)
        llProfessionText.layoutParams = bottomParms
        initPersistentBottomsheetProfession()
        rvProfessionPrefer.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateStuff() {

        rsbDistance.isNotifyWhileDragging = true
        rsbDistance.setOnRangeSeekBarChangeListener { bar, minValue, maxValue -> txtDistanceCount.text = "$maxValue Miles" }
        rsbDistance.selectedMaxValue = 15

        rsbExperience.isNotifyWhileDragging = true
        rsbExperience.setOnRangeSeekBarChangeListener { bar, minValue, maxValue -> txtExperienceCount.text = "$maxValue Years" }
        rsbExperience.selectedMaxValue = 1

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java);

        if (userData!!.response.gender == "1") {
            mGenderValue = 2
            txtGenderPrefer.text = getString(R.string.female)
        } else if (userData!!.response.gender == "2") {
            mGenderValue = 1
            txtGenderPrefer.text = getString(R.string.male)
        } else {
            mGenderValue = 3
            txtGenderPrefer.text = getString(R.string.other)
        }

        extractProfessionValue()
        extractLanguageValue()

        mAdapterProfession = PreferProfessionAdapter(mContext!!, mProfessionArray)
        rvProfessionPrefer.adapter = mAdapterProfession
    }

    private fun extractLanguageValue() {
        /// no operation
    }

    private fun extractProfessionValue() {
        mProfessionArray.addAll(userData!!.professions)
    }

    override fun initListener() {
        imgForwardPrefer.setOnClickListener(this)
        llGenderPrefer.setOnClickListener(this)
        llProfessionListing.setOnClickListener(this)
        llSkillSelection.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_preferences

    override fun getContext() = this

    override fun onClick(view: View?) {
        var intent: Intent? = null
        when (view) {
            llSkillSelection -> {
                intent = Intent(mContext, SkillSelectionActivity::class.java)
                intent.putStringArrayListExtra("selectedSkills", mSelectedSkillsArray)
                intent.putStringArrayListExtra("selectedSkillsName", mSelectedSkillsNameArray)
                startActivityForResult(intent, SKILLS)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            imgForwardPrefer -> {
                if (connectedToInternet()) {
                    hitAPI()
                } else
                    showInternetAlert(imgForwardPrefer)


            }
            llGenderPrefer -> {
                optionGender()
            }
        }
    }

    private fun hitAPI() {
        showLoader()
        var tempProfessions: String = Constants.EMPTY

        val tempProfessionsArray = ArrayList<String>()
        for (selectedProfession in mProfessionArray) {
            if (selectedProfession.isSelected)
                tempProfessionsArray.add(selectedProfession.id.toString())
        }

        if (tempProfessionsArray.size > 0)
            tempProfessions = tempProfessionsArray.toString()
                    .substring(1, tempProfessionsArray.toString().length - 1).trim()

        Log.e("Selected Professions = ", tempProfessions)

        var tempSkills = Constants.EMPTY
        if (mSelectedSkillsArray.size > 0)
            tempSkills = mSelectedSkillsArray.toString()
                    .substring(1, mSelectedSkillsArray.toString().length - 1).trim()


        val call = RetrofitClient.getInstance().updatePreferences(userData!!.response.access_token,
                rsbDistance.selectedMaxValue as Int,
                rsbExperience.selectedMaxValue.toString(),
                mGenderValue!!,
                tempSkills,
                tempProfessions,
                Constants.EMPTY)

        call.enqueue(object : Callback<SignupModel> {
            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    userData!!.response = response.body().response
                    mUtils!!.setString("userDataLocal", mGson.toJson(userData))
                    mUtils!!.setString("access_token", userData!!.response.access_token)
                    mUtils!!.setInt("profile_status", userData!!.response.profile_status)
                    intent = Intent(mContext, DisclamierDialog::class.java)
                    startActivity(intent)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(imgForwardPrefer, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(imgForwardPrefer, t!!.localizedMessage)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SKILLS -> {
                    var count = 0
                    flSkillsPrefer.removeAllViews()
                    mSelectedSkillsArray.clear()
                    mSelectedSkillsArray.addAll(data!!.getStringArrayListExtra("selectedSkills"))

                    mSelectedSkillsNameArray.clear()
                    mSelectedSkillsNameArray.addAll(data!!.getStringArrayListExtra("selectedSkillsName"))
                    for (skills: String in mSelectedSkillsNameArray) {
                        if (count == 3) {
                            flSkillsPrefer.addView(inflateView("+ ${mSelectedSkillsArray.size - count} More"))
                            break
                        } else {
                            flSkillsPrefer.addView(inflateView(skills))
                        }
                        count++
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun inflateView(skillValue: String): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms
        interestChip.txtAddSkillChip.text = skillValue
        return interestChip
    }


    fun initPersistentBottomsheetProfession() {
        // bottom view of accepted job

        var behaviorJobDetail = BottomSheetBehavior.from<View>(coordinator.llProfessionListing)
        behaviorJobDetail.peekHeight = mHeight / 6

        behaviorJobDetail.state = BottomSheetBehavior.STATE_COLLAPSED


        imgProfessionUpward.setOnClickListener(View.OnClickListener {
            if (behaviorJobDetail.state == BottomSheetBehavior.STATE_COLLAPSED) {
                behaviorJobDetail.state = BottomSheetBehavior.STATE_EXPANDED
                imgProfessionUpward.rotation = 180F
            } else {
                behaviorJobDetail.state = BottomSheetBehavior.STATE_COLLAPSED
                imgProfessionUpward.rotation = 0F
            }
        })

        if (behaviorJobDetail != null)
            behaviorJobDetail.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    //showing the different states
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {

                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {

                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {

                        }
                        BottomSheetBehavior.STATE_SETTLING -> {
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // React to dragging events

                }
            })

    }

    private fun optionGender() {
        BottomSheet.Builder(this, R.style.BottomSheet_Dialog)
                .title(getString(R.string.select_gender))
                .sheet(R.menu.menu_gender).listener { dialog, which ->
                    when (which) {
                        R.id.item_male -> {
                            txtGenderPrefer.setText(R.string.male)
                            mGenderValue = 1
                        }
                        R.id.item_female -> {
                            txtGenderPrefer.setText(R.string.female)
                            mGenderValue = 2
                        }
                        R.id.item_other -> {
                            txtGenderPrefer.setText(R.string.other)
                            mGenderValue = 3
                        }
                    }
                }.show()
    }


}