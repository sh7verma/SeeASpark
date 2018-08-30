package com.seeaspark

import adapters.PreferProfessionAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.cocosw.bottomsheet.BottomSheet
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.android.synthetic.main.activity_preferences.view.*
import kotlinx.android.synthetic.main.add_skills.view.*
import models.LanguageModel
import models.ProfessionListingModel
import models.ProfessionModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class PreferencesActivity : BaseActivity() {

    private var mSelectedSkillsArray = ArrayList<String>()
    private var mSelectedSkillsNameArray = ArrayList<String>()
    private val SKILLS: Int = 1
    private val LANGUAGES: Int = 2

    var mAdapterProfession: PreferProfessionAdapter? = null
    private var mProfessionArray = ArrayList<ProfessionModel>()
    var mSelectedProfessionsArray = ArrayList<String>()
    private var userData: SignupModel? = null
    private var mGenderValue = 0
    private var mMaxDistanceValue = 15
    private var mMaxExperienceValue = 3
    private var moveToHome = false
    private var mPreferActivity: PreferencesActivity? = null
    private var mSelectedLanguagesArray = ArrayList<LanguageModel>()

    override fun getContentView() = R.layout.activity_preferences

    override fun initUI() {
        mPreferActivity = this
        val bottomParms = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight / 6)
        llProfessionText.layoutParams = bottomParms
        initPersistentBottomsheetProfession()
        rvProfessionPrefer.layoutManager = LinearLayoutManager(this)
        cbNoExperience.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                llDisableExperience.visibility = View.VISIBLE
                mMaxExperienceValue = -1
            } else {
                llDisableExperience.visibility = View.GONE
                mMaxExperienceValue = 3
            }
            rsbExperience.selectedMaxValue = 3
            txtExperienceCount.text = "3 Year(s)"
        }

        cbNoDistance.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                llDisableDistance.visibility = View.VISIBLE
                mMaxDistanceValue = 102
                rsbDistance.selectedMaxValue = 100
                txtDistanceCount.text = "100+ Mile(s)"
            } else {
                llDisableDistance.visibility = View.GONE
                mMaxDistanceValue = 101
            }
        }
    }

    override fun displayDayMode() {
        llMainPreferences.setBackgroundColor(whiteColor)
        txtTitlePrefer.setTextColor(blackColor)
        imgForwardPrefer.setBackgroundResource(whiteRipple)

        txtDistanceHint.setTextColor(blackColor)
        txtDistanceCount.setTextColor(blackColor)

        txtExperienceHint.setTextColor(blackColor)
        txtExperienceCount.setTextColor(blackColor)

        llGenderPrefer.setBackgroundResource(whiteRipple)
        txtGenderPrefer.setBackgroundResource(whiteRipple)
        txtGenderHint.setTextColor(blackColor)
        txtGenderPrefer.setTextColor(blackColor)

        llLanguageSelection.setBackgroundResource(whiteRipple)
        txtLanguageHint.setTextColor(blackColor)

        llSkillSelection.setBackgroundResource(whiteRipple)
        txtSkillHint.setTextColor(blackColor)

        cbNoDistance.setTextColor(blackColor)
        cbNoExperience.setTextColor(blackColor)
    }

    override fun displayNightMode() {
        llMainPreferences.setBackgroundColor(blackColor)
        txtTitlePrefer.setTextColor(whiteColor)
        imgForwardPrefer.setBackgroundResource(blackRipple)

        txtDistanceHint.setTextColor(whiteColor)
        txtDistanceCount.setTextColor(whiteColor)

        txtExperienceHint.setTextColor(whiteColor)
        txtExperienceCount.setTextColor(whiteColor)

        llGenderPrefer.setBackgroundResource(blackRipple)
        txtGenderPrefer.setBackgroundResource(blackRipple)
        txtGenderHint.setTextColor(whiteColor)
        txtGenderPrefer.setTextColor(whiteColor)

        llLanguageSelection.setBackgroundResource(blackRipple)
        txtLanguageHint.setTextColor(whiteColor)

        llSkillSelection.setBackgroundResource(blackRipple)
        txtSkillHint.setTextColor(whiteColor)

        cbNoDistance.setTextColor(whiteColor)
        cbNoExperience.setTextColor(whiteColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreateStuff() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)

        rsbDistance.isNotifyWhileDragging = true
        rsbDistance.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            mMaxDistanceValue = maxValue.toInt()
            txtDistanceCount.text = "$maxValue Mile(s)"
            if (maxValue == 100) {
                mMaxDistanceValue = maxValue.toInt() + 1
                txtDistanceCount.text = "$maxValue+ Mile(s)"
            }
        }

        rsbExperience.isNotifyWhileDragging = true
        rsbExperience.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            mMaxExperienceValue = maxValue.toInt()
            txtExperienceCount.text = "$maxValue Year(s)"
        }

        if (intent.hasExtra("showPrefilled")) {
            /// navigating from home section
            moveToHome = true

            mMaxDistanceValue = userData!!.response.preferences.distance

            if (userData!!.response.preferences.distance == 102) {
                cbNoDistance.isChecked = true
                rsbDistance.selectedMaxValue = 100
            } else if (userData!!.response.preferences.distance == 101) {
                txtDistanceCount.text = "100+ Mile(s)"
                rsbDistance.selectedMaxValue = 100
            } else {
                txtDistanceCount.text = "${userData!!.response.preferences.distance} Mile(s)"
                rsbDistance.selectedMaxValue = userData!!.response.preferences.distance
            }

            if (userData!!.response.preferences.experience_year == -1) {
                mMaxExperienceValue = -1
                cbNoExperience.isChecked = true
                llDisableExperience.visibility = View.VISIBLE
                rsbExperience.selectedMaxValue = 3
                txtExperienceCount.text = "3 Year(s)"
            } else {
                cbNoExperience.isChecked = false
                llDisableExperience.visibility = View.GONE
                mMaxExperienceValue = userData!!.response.preferences.experience_year
                rsbExperience.selectedMaxValue = userData!!.response.preferences.experience_year
                txtExperienceCount.text = "${userData!!.response.preferences.experience_year} Year(s)"
            }

            when {
                userData!!.response.preferences.gender == 1 -> {
                    mGenderValue = 1
                    txtGenderPrefer.text = getString(R.string.male)
                }
                userData!!.response.preferences.gender == 2 -> {
                    mGenderValue = 2
                    txtGenderPrefer.text = getString(R.string.female)
                }
                else -> {
                    mGenderValue = 3
                    txtGenderPrefer.text = getString(R.string.don_t_mind)
                }
            }
        } else {
            rsbDistance.selectedMaxValue = Constants.DISTANCE
            txtDistanceCount.text = "${Constants.DISTANCE} Mile(s)"

            rsbExperience.selectedMaxValue = Constants.EXPERIENCE
            txtExperienceCount.text = "${Constants.EXPERIENCE} Year(s)"

            if (userData!!.response.gender == "1") {
                mGenderValue = 2
                txtGenderPrefer.text = getString(R.string.female)
            } else if (userData!!.response.gender == "2") {
                mGenderValue = 1
                txtGenderPrefer.text = getString(R.string.male)
            } else {
                mGenderValue = 3
                txtGenderPrefer.text = getString(R.string.don_t_mind)
            }
        }

        extractSkills()
        extractSelectedProfessions()
        extractSelectedLanguages()
        extractProfessionValue()

        if (connectedToInternet())
            hitProfessionAPI()
        else
            showInternetAlert(llGenderPrefer)
    }

    private fun extractSelectedProfessions() {
        for (profession in userData!!.response.preferences.professions) {
            mSelectedProfessionsArray.add(profession.id.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun extractSelectedLanguages() {
        mSelectedLanguagesArray.addAll(userData!!.response.preferences.languages)
        displayLanguageChips()
    }

    private fun extractSkills() {
        for (skillValue in userData!!.response.preferences.skills) {
            mSelectedSkillsNameArray.add(skillValue.name)
            mSelectedSkillsArray.add(skillValue.id.toString())
        }
        displaySkillsChips()
    }

    private fun extractProfessionValue() {
        addNoProfessionData()
        mProfessionArray.addAll(userData!!.professions)
        populateData()
    }

    private fun addNoProfessionData() {
        val professionData = ProfessionModel()
        professionData.name = "No Preferences"
        if (mSelectedProfessionsArray.isEmpty())
            professionData.isSelected = true
        professionData.id = 0
        mProfessionArray.add(professionData)
    }

    private fun populateData() {
        mAdapterProfession = PreferProfessionAdapter(mContext!!, mProfessionArray, mPreferActivity)
        rvProfessionPrefer.adapter = mAdapterProfession
    }

    override fun initListener() {
        imgForwardPrefer.setOnClickListener(this)
        llGenderPrefer.setOnClickListener(this)
        llProfessionListing.setOnClickListener(this)
        llSkillSelection.setOnClickListener(this)
        llLanguageSelection.setOnClickListener(this)
        llDisableExperience.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        var intent: Intent? = null
        when (view) {
            llLanguageSelection -> {
                intent = Intent(mContext, SelectLanguageActivity::class.java)
                intent.putExtra("prefer", true)
                intent.putParcelableArrayListExtra("selectedLanguages", mSelectedLanguagesArray)
                startActivityForResult(intent, LANGUAGES)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
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

        for (selectedProfession in mProfessionArray) {
            if (selectedProfession.isSelected)
                mSelectedProfessionsArray.add(selectedProfession.id.toString())
        }

        if (mSelectedProfessionsArray.size > 0)
            tempProfessions = mSelectedProfessionsArray.toString()
                    .substring(1, mSelectedProfessionsArray.toString().length - 1).trim()

        Log.e("Selected Professions = ", tempProfessions)

        var tempSkills = Constants.EMPTY
        if (mSelectedSkillsArray.size > 0)
            tempSkills = mSelectedSkillsArray.toString()
                    .substring(1, mSelectedSkillsArray.toString().length - 1).trim()

        var tempLanguages = Constants.EMPTY
        val tempLanguagesArray = ArrayList<Int>()
        for (languageData in mSelectedLanguagesArray) {
            tempLanguagesArray.add(languageData.id)
        }

        if (tempLanguagesArray.size > 0)
            tempLanguages = tempLanguagesArray.toString()
                    .substring(1, tempLanguagesArray.toString().length - 1).trim()


        val call = RetrofitClient.getInstance().updatePreferences(userData!!.response.access_token,
                mMaxDistanceValue,
                mMaxExperienceValue.toString(),
                mGenderValue,
                tempSkills,
                tempProfessions,
                tempLanguages)

        call.enqueue(object : Callback<SignupModel> {
            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    userData!!.response = response.body().response
                    mUtils!!.setString("userDataLocal", mGson.toJson(userData))
                    mUtils!!.setString("access_token", userData!!.response.access_token)
                    mUtils!!.setInt("profile_status", userData!!.response.profile_status)

                    if (moveToHome) {
                        /// navigate back to home
                        intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                    } else {
                        /// navigate back to disclamier
                        intent = Intent(mContext, DisclamierDialog::class.java)
                        startActivity(intent)
                    }
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SKILLS -> {
                    flSkillsPrefer.removeAllViews()
                    mSelectedSkillsArray.clear()
                    mSelectedSkillsArray.addAll(data!!.getStringArrayListExtra("selectedSkills"))

                    mSelectedSkillsNameArray.clear()
                    mSelectedSkillsNameArray.addAll(data!!.getStringArrayListExtra("selectedSkillsName"))

                    displaySkillsChips()
                }
                LANGUAGES -> {
                    flLanguagePrefer.removeAllViews()
                    mSelectedLanguagesArray.clear()
                    mSelectedLanguagesArray.addAll(data!!.getParcelableArrayListExtra("selectedLanguages"))
                    displayLanguageChips()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displayLanguageChips() {
        if (mSelectedLanguagesArray.size > 0) {
            flLanguagePrefer.visibility = View.VISIBLE
            txtNoLanguages.visibility = View.GONE
            for ((index, languages) in mSelectedLanguagesArray.withIndex()) {
                if (index == 3) {
                    flLanguagePrefer.addView(inflateView("+ ${mSelectedLanguagesArray.size - index} More"))
                    break
                } else {
                    flLanguagePrefer.addView(inflateView(languages.name))
                }
            }
        } else {
            flLanguagePrefer.visibility = View.GONE
            txtNoLanguages.visibility = View.VISIBLE
        }
    }

    private fun displaySkillsChips() {
        if (mSelectedSkillsNameArray.size > 0) {
            flSkillsPrefer.visibility = View.VISIBLE
            txtNoSkills.visibility = View.GONE
            var count = 0
            for (skills: String in mSelectedSkillsNameArray) {
                if (count == 3) {
                    flSkillsPrefer.addView(inflateView("+ ${mSelectedSkillsArray.size - count} More"))
                    break
                } else {
                    flSkillsPrefer.addView(inflateView(skills))
                }
                count++
            }
        } else {
            flSkillsPrefer.visibility = View.GONE
            txtNoSkills.visibility = View.VISIBLE
        }
    }

    private fun inflateView(skillValue: String): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms
        if (mUtils!!.getInt("nightMode", 0) == 1)
            interestChip.txtAddSkillChip.setTextColor(whiteColor)
        else
            interestChip.txtAddSkillChip.setTextColor(blackColor)
        interestChip.txtAddSkillChip.text = skillValue
        return interestChip
    }

    private fun initPersistentBottomsheetProfession() {

        val behaviorJobDetail = BottomSheetBehavior.from<View>(coordinator.llProfessionListing)
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
                .sheet(R.menu.menu_preferences_gender).listener { dialog, which ->
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
                            txtGenderPrefer.setText(R.string.don_t_mind)
                            mGenderValue = 3
                        }
                    }
                }.show()
    }

    private fun hitProfessionAPI() {

        val call = RetrofitClient.getInstance().getProfessions(mUtils!!.getString("access_token", ""))
        call.enqueue(object : Callback<ProfessionListingModel> {

            override fun onResponse(call: Call<ProfessionListingModel>?, response: Response<ProfessionListingModel>) {
                if (response.body().response != null) {
                    mProfessionArray.clear()
                    addNoProfessionData()
                    mProfessionArray.addAll(response.body().response)
                    populateData()
                    upDateData(response.body().response)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(imgForwardPrefer, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<ProfessionListingModel>?, t: Throwable?) {
                showAlert(imgForwardPrefer, t!!.localizedMessage)
            }
        })
    }

    private fun upDateData(response: MutableList<ProfessionModel>) {
        userData!!.professions.clear()
        userData!!.professions.addAll(response)
        mUtils!!.setString("userDataLocal", mGson.toJson(userData))
    }

    override fun onBackPressed() {
        if (moveToHome) {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else
            super.onBackPressed()
    }

    fun clearProfessionPreferences() {
        mProfessionArray[0].isSelected = true
        mSelectedProfessionsArray.clear()
    }

    fun unSelectedNoPreferences() {
        mProfessionArray[0].isSelected = false
    }
}