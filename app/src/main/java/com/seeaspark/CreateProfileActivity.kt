package com.seeaspark

import adapters.CreateProfileAdapter
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import fragments.*
import kotlinx.android.synthetic.main.activity_create_profile.*
import kotlinx.android.synthetic.main.activity_signup.*
import models.*
import network.RetrofitClient
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.util.*

class CreateProfileActivity : BaseActivity() {

    var mFragmentArray = ArrayList<Fragment>()
    var mCurrentPosition = 0;
    var mSkillsArray = ArrayList<SkillsModel>()
    var mSkillsArrayText = ArrayList<String>()
    var mProfessionArray = ArrayList<ProfessionModel>()
    var mLanguageArray = ArrayList<LanguageModel>()
    var mSelectedLanguageArray = ArrayList<Int>()

    var mAvatarId = Constants.EMPTY
    var mName = Constants.EMPTY
    var mAge = Constants.EMPTY
    var mGender: Int? = 0
    var mGenderText = Constants.EMPTY
    var mProfession: Int? = -1
    var mExpeirenceYears = Constants.EMPTY
    var mExpeirenceMonth = Constants.EMPTY
    var mSkillsServerArray = ArrayList<String>()
    var mBio = Constants.EMPTY
    var mDescription = Constants.EMPTY

    var calDOB: Calendar? = null
    var userData: SignupModel? = null

    private var mProfileAdapter: CreateProfileAdapter? = null

    override fun initUI() {
        vpProfile.setPagingEnabled(false)
        vpProfile.offscreenPageLimit = mFragmentArray.size
    }

    override fun onCreateStuff() {

        /// getting profile Data
        if (intent.hasExtra("userData"))
            userData = intent.getParcelableExtra("userData")
        else
            userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java);

        mName = userData!!.response.full_name
        mUtils!!.setString("access_token", userData!!.response.access_token)

        /// adding languages
        mLanguageArray.addAll(userData!!.languages)

        /// adding professions
        mProfessionArray.addAll(userData!!.professions)

        /// adding Skills from server and first element
        for (skillvalue in userData!!.skills) {
            mSkillsArrayText.add(skillvalue.name)
        }
        var skillPLusModel = SkillsModel()
        skillPLusModel.isFirstElement = true
        mSkillsArray.add(skillPLusModel)
        mSkillsArray.addAll(userData!!.skills)

        /// intialize calendar object for fragment
        calDOB = Calendar.getInstance(TimeZone.getDefault())
        calDOB!!.add(Calendar.YEAR, -14)

        /// Adding fragments in array
        addFragments()
        Constants.showKeyboard(mContext, vpProfile)
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

        mFragmentArray.add(NameFragment())
        mFragmentArray.add(AgeFragment())
        mFragmentArray.add(GenderFragment())
        mFragmentArray.add(AvatarFragment())
        mFragmentArray.add(LanguageFragment())
        mFragmentArray.add(ProfessionFragment())
        mFragmentArray.add(ExperienceFragment())
        mFragmentArray.add(SkillsFragment())
        mFragmentArray.add(BioFragment())
        mFragmentArray.add(DescribeProfessionFragment())
    }

    fun moveToNext() {
        mCurrentPosition++;
        vpProfile.currentItem = mCurrentPosition
    }

    fun moveToPrevious() {
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

    fun verifyEmail(email: String) {
        showLoader()
        var call = RetrofitClient.getInstance().verifyEmail(email, userData!!.response.access_token)
        call.enqueue(object : Callback<ResendModel> {
            override fun onResponse(call: retrofit2.Call<ResendModel>?, response: Response<ResendModel>?) {
                dismissLoader()
                if (response!!.body().response != null) {
                    var intent = Intent(mContext, EmailVerificationActivity::class.java)
                    intent.putExtra("access_token", userData!!.response.access_token)
                    intent.putExtra("path", "profile")
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                } else {
                    showAlert(vpProfile, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: retrofit2.Call<ResendModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(vpProfile, t!!.localizedMessage)
            }
        })
    }

    override fun onPause() {
        Constants.closeKeyboard(mContext!!, vpProfile)
        super.onPause()
    }

}