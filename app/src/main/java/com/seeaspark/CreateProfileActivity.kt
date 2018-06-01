package com.seeaspark

import adapters.CreateProfileAdapter
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import fragments.*
import kotlinx.android.synthetic.main.activity_create_profile.*
import models.SkillsModel
import utils.Constants
import java.util.*

class CreateProfileActivity : BaseActivity() {

    var mFragmentArray = ArrayList<Fragment>()
    var mCurrentPosition = 0;
    var mSkillsArray = ArrayList<SkillsModel>()

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
        calDOB = Calendar.getInstance(TimeZone.getDefault())
        calDOB!!.add(Calendar.YEAR, -14)
        mSkillsArray.add(SkillsModel("+", false, true))
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
        mFragmentArray.add(AvatarFragment())
        mFragmentArray.add(NameFragment())
        mFragmentArray.add(AgeFragment())
        mFragmentArray.add(GenderFragment())
        mFragmentArray.add(ProfessionFragment())
        mFragmentArray.add(ExperienceFragment())
        mFragmentArray.add(SkillsFragment())
        mFragmentArray.add(BioFragment())
        mFragmentArray.add(DescribeProfessionFragment())
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