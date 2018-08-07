package com.seeaspark

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_questionaires.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_view_profile.*
import kotlinx.android.synthetic.main.add_skills.view.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.ProfileModel
import models.QuestionAnswerModel
import models.QuestionListingModel
import models.SignupModel
import network.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ViewProfileActivity : BaseActivity() {

    private val EDITPROFILE: Int = 1

    private var userData: SignupModel? = null

    override fun getContentView() = R.layout.activity_view_profile

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {

        txtTitleCustom.alpha = 0f

        imgOption1Custom.visibility = View.VISIBLE
        imgOption2Custom.visibility = View.VISIBLE

        imgOption1Custom.setImageResource(R.mipmap.ic_pencil)
        imgOption2Custom.setImageResource(R.mipmap.ic_settings)

        val cd = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
        llCustomToolbar.background = cd
        cd.alpha = 0

        svViewProfile.setOnScrollViewListener { v, l, t, oldl, oldt ->
            cd.alpha = getAlphaforActionBar(v.scrollY)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llMainViewProfile.background = ContextCompat.getDrawable(this, R.drawable.white_short_profile_background)
        txtRatingViewProfile.setTextColor(blackColor)
        txtNameViewProfile.setTextColor(blackColor)
        txtProfessionViewProfile.setTextColor(blackColor)
        populateData()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        populateData()
        llMainViewProfile.background = ContextCompat.getDrawable(this, R.drawable.dark_short_profile_background)
        txtRatingViewProfile.setTextColor(whiteColor)
        txtNameViewProfile.setTextColor(whiteColor)
        txtProfessionViewProfile.setTextColor(whiteColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreateStuff() {
        populateData()
        if (connectedToInternet())
            hitProfileAPI()
        else
            showInternetAlert(llMainViewProfile)
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgOption1Custom.setOnClickListener(this)
        imgOption2Custom.setOnClickListener(this)
        txtChangeUserType.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackCustom -> {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                moveBack()
            }
            imgOption1Custom -> {
                val intent = Intent(mContext, EditProfileActivity::class.java)
                startActivityForResult(intent, EDITPROFILE)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            imgOption2Custom -> {
                val intent = Intent(mContext, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            txtChangeUserType -> {
                if (userData!!.response.can_switch == 1) {
                    if (connectedToInternet())
                        switchUserTypeDialog()
                    else
                        showInternetAlert(txtChangeUserType)
                } else {
                    if (connectedToInternet())
                        changeUserTypeDialog()
                    else
                        showInternetAlert(txtChangeUserType)
                }
            }
        }
    }

    private fun switchUserTypeDialog() {
        var userType = 0
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("SWITCH ACCOUNT")
        if (userData!!.response.user_type == Constants.MENTOR) {
            alertDialog.setMessage(getString(R.string.switch_to_mentee_msg))
            userType = Constants.MENTEE
        } else {
            alertDialog.setMessage(getString(R.string.switch_to_mentor_msg))
            userType = Constants.MENTOR
        }
        alertDialog.setPositiveButton(getString(R.string.confirm)) { dialog, which ->
            if (connectedToInternet())
                hitSwitchAccountAPI(userType)
            else
                showToast(mContext!!, mErrorInternet)
        }
        alertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    private fun hitProfileAPI() {
        val call = RetrofitClient.getInstance().getProfile(mUtils!!.getString("access_token", ""),
                userData!!.response.id.toString())
        call.enqueue(object : Callback<ProfileModel> {

            override fun onResponse(call: Call<ProfileModel>?, response: Response<ProfileModel>) {
                if (response.body().response != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        updateProfileDatabase(response.body().response)
                        populateData()
                    }
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        showAlert(llMainViewProfile, response.body().error!!.message!!)
                        moveToSplash()
                    } else
                        showAlert(llMainViewProfile, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<ProfileModel>?, t: Throwable?) {
                showAlert(llMainViewProfile, t!!.localizedMessage)
            }
        })
    }

    private fun hitSwitchAccountAPI(userType: Int) {
        showLoader()
        val call = RetrofitClient.getInstance().switchUser(mUtils!!.getString("access_token", ""),
                userType)
        call.enqueue(object : Callback<SignupModel> {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    updateProfileDatabase(response.body().response)
                    populateData()
                    sendSwitchUserTypeBroadCast()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        showAlert(txtDoneQuestion, response.body().error!!.message!!)
                        moveToSplash()
                    } else
                        showAlert(txtDoneQuestion, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
            }
        })
    }

    private fun updateProfileDatabase(response: SignupModel.ResponseBean?) {
        userData!!.response = response
        mUtils!!.setString("userDataLocal", mGson.toJson(userData))
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun populateData() {
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        txtTitleCustom.text = userData!!.response.full_name

        Picasso.with(this).load(userData!!.response.avatar).placeholder(R.drawable.placeholder_image).into(imgViewProfile)

        txtNameViewProfile.text = userData!!.response.full_name

        var mGender = ""
        when {
            userData!!.response.gender == "1" -> mGender = getString(R.string.male)
            userData!!.response.gender == "2" -> mGender = getString(R.string.female)
            userData!!.response.gender == "3" -> mGender = getString(R.string.other)
        }

        txtGenderViewProfile.text = "$mGender · ${calculateAge(userData!!.response.age)} Years"
        txtProfessionViewProfile.text = userData!!.response.profession.name

        if (userData!!.response.experience_month != 0)
            txtExperienceViewProfile.text = "${userData!!.response.experience_year}.${userData!!.response.experience_month} Year(s) of Experience"
        else
            txtExperienceViewProfile.text = "${userData!!.response.experience_year} Year(s) of Experience"

        txtBioViewProfile.text = userData!!.response.bio
        txtDescriptionViewProfile.text = userData!!.response.pro_description

        flLanguagesViewProfile.removeAllViews()
        for (languages in userData!!.response.languages) {
            flLanguagesViewProfile.addView(inflateView(languages.name))
        }

        flSkillsViewProfile.removeAllViews()
        for (skills in userData!!.response.skills) {
            flSkillsViewProfile.addView(inflateView(skills))
        }

        if (userData!!.response.can_switch == 1) {
            if (userData!!.response.user_type == Constants.MENTEE)
                txtChangeUserType.text = getString(R.string.switch_to_mentor)
            else
                txtChangeUserType.text = getString(R.string.switch_to_mentee)
        } else {
            if (userData!!.response.user_type == Constants.MENTEE)
                txtChangeUserType.text = getString(R.string.become_mentor)
            else
                txtChangeUserType.text = getString(R.string.become_mentee)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EDITPROFILE -> {
                    populateData()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendSwitchUserTypeBroadCast() {
        val broadCastIntent = Intent(Constants.SWITCH_USER_TYPE)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun inflateView(skillValue: String): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms

        if (mUtils!!.getInt("nightMode", 0) == 1) {
            interestChip.txtAddSkillChip.background = ContextCompat.getDrawable(this, R.drawable.white_default)
            interestChip.txtAddSkillChip.setTextColor(ContextCompat.getColor(this, R.color.black_color))
        } else {
            interestChip.txtAddSkillChip.background = ContextCompat.getDrawable(this, R.drawable.selected_skills)
            interestChip.txtAddSkillChip.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        }

        interestChip.txtAddSkillChip.text = skillValue

        return interestChip
    }

    @Throws(ParseException::class)
    private fun calculateAge(birthdate: String): Int {
        val birthFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val birthDate = birthFormat.parse(birthdate)
        val cal = Calendar.getInstance()
        val today = cal.time
        val diff = today.time - birthDate.time
        val diffDays = diff / (24 * 60 * 60 * 1000)
        val diffyears = (diffDays / 365).toInt()
        return diffyears
    }

    private fun getAlphaforActionBar(scrollY: Int): Int {
        val minDist = 0
        val maxDist = 300
        when {
            scrollY > maxDist -> {
                txtTitleCustom.alpha = 1.0f
                return 255
            }
            scrollY < minDist -> {
                txtTitleCustom.alpha = 0f
                return 0
            }
            else -> {
                txtTitleCustom.alpha = 0f
                var alpha = 0
                alpha = (255.0 / maxDist * scrollY).toInt()
                return alpha
            }
        }
    }

    private fun changeUserTypeDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("SWITCH ACCOUNT")
        if (userData!!.response.user_type == Constants.MENTOR)
            alertDialog.setMessage(getString(R.string.want_to_mentee))
        else
            alertDialog.setMessage(getString(R.string.want_to_mentor))
        alertDialog.setPositiveButton(getString(R.string.confirm)) { dialog, which ->
            if (userData!!.response.user_type == Constants.MENTOR) {
                /// user is mentor
                /// turning user to mentee
                if (connectedToInternet())
                    moveToQuestionarrie(Constants.MENTEE)
                else
                    showToast(mContext!!, mErrorInternet)
            } else {
                /// user is mentee ...
                /// turning user to mentor
                if (userData!!.response.mentor_profile_status == 0) {
                    /// move to Document verification
                    moveToVerifyId()
                } else {
                    if (userData!!.response.mentor_profile_status == 1 && userData!!.response.mentor_verified == 0) {
                        /// profile Under Review
                        moveToProfileReview()
                    } else if (userData!!.response.mentor_profile_status == 1 && userData!!.response.mentor_verified == 1) {
                        /// Move to Questionnaire
                        moveToQuestionarrie(Constants.MENTOR)
                    }
                }
            }
        }
        alertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    private fun moveToProfileReview() {
        val intent = Intent(mContext!!, ReviewActivity::class.java)
        intent.putExtra("isSwitched", true)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    private fun moveToVerifyId() {
        val intent = Intent(mContext!!, VerifyIdActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    private fun moveToQuestionarrie(userType: Int) {
        val intent = Intent(mContext!!, QuestionnariesActivity::class.java)
        intent.putExtra("newUserType", userType)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        moveBack()
    }

    private fun moveBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition()
        } else {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                IntentFilter(Constants.PROFILE_UPDATE))
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverChangeStatus,
                IntentFilter(Constants.REVIEW))
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverChangeStatus)
        super.onDestroy()
    }

    internal var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context, intent: Intent) {
            populateData()
            if (intent.hasExtra("updateHomeScreen"))
                sendSwitchUserTypeBroadCast()
            if (intent.hasExtra("updateAPI"))
                hitProfileAPI()
        }
    }

    var receiverChangeStatus: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (connectedToInternet())
                hitProfileAPI()
            else
                showInternetAlert(llMainViewProfile)
        }
    }
}