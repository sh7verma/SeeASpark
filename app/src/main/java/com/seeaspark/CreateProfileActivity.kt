package com.seeaspark

import adapters.CreateProfileAdapter
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import fragments.*
import kotlinx.android.synthetic.main.activity_create_profile.*
import models.LanguageModel
import models.ProfessionModel
import models.SkillsModel
import utils.Constants
import java.util.*

class CreateProfileActivity : BaseActivity() {

    var mFragmentArray = ArrayList<Fragment>()
    var mCurrentPosition = 0;
    var mSkillsArray = ArrayList<SkillsModel>()
    var mProfessionArray = ArrayList<ProfessionModel>()
    var mLanguageArray = ArrayList<LanguageModel>()

    var mName = Constants.EMPTY
    var mAge = Constants.EMPTY
    var mDob = Constants.EMPTY
    var mGender = Constants.EMPTY
    var mProfession = Constants.EMPTY
    var mProfessionId = Constants.EMPTY
    var mExpeirenceYears = Constants.EMPTY
    var mExpeirenceMonth = Constants.EMPTY
    var mSkillsServerArray = ArrayList<String>()
    var mBio = Constants.EMPTY
    var mDescription = Constants.EMPTY
    var calDOB: Calendar? = null

    private var mProfileAdapter: CreateProfileAdapter? = null

    override fun initUI() {
        vpProfile.setPagingEnabled(false)
        vpProfile.offscreenPageLimit = mFragmentArray.size
    }

    override fun onCreateStuff() {
        /// intialize calendar object for fragment
        calDOB = Calendar.getInstance(TimeZone.getDefault())
        calDOB!!.add(Calendar.YEAR, -14)

        /// intialize Skills array for fragment
        mSkillsArray.add(SkillsModel("+", false, true))

        /// adding demo professions
        addProfession()

        /// adding demo languages
        addLanguages()

        /// Adding fragments in array
        addFragments()

        mProfileAdapter = CreateProfileAdapter(supportFragmentManager, mFragmentArray)
        vpProfile.adapter = mProfileAdapter

        vpProfile.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
                mCurrentPosition = position
            }

        })
    }

    override fun initListener() {

    }

    override fun getContentView() = R.layout.activity_create_profile

    override fun getContext() = this

    override fun onClick(view: View?) {

    }

    fun addFragments() {

        if (mUtils!!.getBoolean("addEmailFragment", true))
            mFragmentArray.add(EmailFragment())

        mFragmentArray.add(AvatarFragment())
        mFragmentArray.add(NameFragment())
        mFragmentArray.add(AgeFragment())
        mFragmentArray.add(GenderFragment())
        mFragmentArray.add(ProfessionFragment())
        mFragmentArray.add(LanguageFragment())
        mFragmentArray.add(ExperienceFragment())
        mFragmentArray.add(SkillsFragment())
        mFragmentArray.add(BioFragment())
        mFragmentArray.add(DescribeProfessionFragment())
    }

    private fun addProfession() {
        mProfessionArray.add(ProfessionModel("Actor", false))
        mProfessionArray.add(ProfessionModel("Architecture", false))
        mProfessionArray.add(ProfessionModel("Accountant", false))
        mProfessionArray.add(ProfessionModel("Consultant", false))
        mProfessionArray.add(ProfessionModel("Designer", false))
        mProfessionArray.add(ProfessionModel("Dentist", false))
        mProfessionArray.add(ProfessionModel("Engineer", false))
    }

    private fun addLanguages() {
        mLanguageArray.add(LanguageModel("Albanian", false))
        mLanguageArray.add(LanguageModel("Arabic", false))
        mLanguageArray.add(LanguageModel("Italian", false))
        mLanguageArray.add(LanguageModel("Korean", false))
        mLanguageArray.add(LanguageModel("Spanish", false))
        mLanguageArray.add(LanguageModel("English UK", false))
        mLanguageArray.add(LanguageModel("English US", false))
    }

    fun moveToNext() {
        Constants.closeKeyboard(mContext, vpProfile)
        mCurrentPosition++;
        vpProfile.currentItem = mCurrentPosition
    }

    fun moveToPrevious() {
        Constants.closeKeyboard(mContext, vpProfile)
        if (mCurrentPosition > 0) {
            mCurrentPosition--;
            vpProfile.currentItem = mCurrentPosition
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        moveToPrevious()
    }

    fun showAlertActivity(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

}