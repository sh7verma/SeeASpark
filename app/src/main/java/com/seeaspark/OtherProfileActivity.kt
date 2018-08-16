package com.seeaspark

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Picasso
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_other_profile.*
import kotlinx.android.synthetic.main.add_skills.view.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.CardsDisplayModel
import models.OtherProfileModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OtherProfileActivity : BaseActivity() {

    private var userData: CardsDisplayModel? = null
    private var mOtherUserId = Constants.EMPTY
    var width: Int = 0

    override fun getContentView() = R.layout.activity_other_profile

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {

        txtTitleCustom.alpha = 0f

        val drawable = ContextCompat.getDrawable(mContext!!, R.mipmap.ic_avatar_1)
        width = drawable!!.intrinsicWidth

        val cd = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
        llCustomToolbar.background = cd
        cd.alpha = 0

        svViewProfile.setOnScrollViewListener { v, _, _, _, _ ->
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
        if (intent.hasExtra("otherProfileData")) {
            userData = intent.getParcelableExtra("otherProfileData")
            mOtherUserId = userData!!.id.toString()
            populateData()
        } else {
            mOtherUserId = intent.getStringExtra("otherUserId")
            hideViews()
        }

        if (connectedToInternet())
            hitProfileAPI()
        else
            showInternetAlert(llMainViewProfile)
    }

    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgShareProfile.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackCustom -> {
                moveBack()
            }
            imgShareProfile -> {
                intent = Intent(mContext!!, ShareActivity::class.java)
                intent.putExtra("path", 1)
                intent.putExtra("postUrl", userData!!.url)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
    }

    private fun hitProfileAPI() {
        val call = RetrofitClient.getInstance().getOtherProfile(mUtils!!.getString("access_token", ""),
                mOtherUserId)
        call.enqueue(object : Callback<OtherProfileModel> {

            override fun onResponse(call: Call<OtherProfileModel>?, response: Response<OtherProfileModel>) {
                if (response.body().response != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        userData = response.body().response
                        displayViews()
                        populateData()
                    }
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(llMainViewProfile, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<OtherProfileModel>?, t: Throwable?) {
                showAlert(llMainViewProfile, t!!.localizedMessage)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun populateData() {
        svViewProfile.visibility = View.VISIBLE
        txtTitleCustom.text = userData!!.full_name

        Picasso.with(this).load(userData!!.avatar.avtar_url).resize(width, width)
                .placeholder(R.drawable.placeholder_image).into(imgViewProfile)

        txtNameViewProfile.text = userData!!.full_name

        var mGender = ""
        when {
            userData!!.gender == "1" -> mGender = getString(R.string.male)
            userData!!.gender == "2" -> mGender = getString(R.string.female)
            userData!!.gender == "3" -> mGender = getString(R.string.other)
        }

        txtGenderViewProfile.text = "$mGender · ${calculateAge(userData!!.age)} Years"
        txtProfessionViewProfile.text = userData!!.profession.name

        if (userData!!.experience_month != 0)
            txtExperienceViewProfile.text = "${userData!!.experience_year}.${userData!!.experience_month} Year(s) of Experience"
        else
            txtExperienceViewProfile.text = "${userData!!.experience_year} Year(s) of Experience"

        txtBioViewProfile.text = userData!!.bio
        txtDescriptionViewProfile.text = userData!!.pro_description

        flLanguagesViewProfile.removeAllViews()
        for (languages in userData!!.languages) {
            flLanguagesViewProfile.addView(inflateView(languages.name))
        }

        flSkillsViewProfile.removeAllViews()
        for (skills in userData!!.skills) {
            flSkillsViewProfile.addView(inflateView(skills))
        }
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
                return (255.0 / maxDist * scrollY).toInt()
            }
        }
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
        return (diffDays / 365).toInt()
    }

    private fun hideViews() {
        llUpperProfile.visibility = View.INVISIBLE
        svViewProfile.visibility = View.INVISIBLE
        aviProfile.visibility = View.VISIBLE
    }

    private fun displayViews() {
        llUpperProfile.visibility = View.VISIBLE
        svViewProfile.visibility = View.VISIBLE
        aviProfile.visibility = View.INVISIBLE
    }

    private fun moveBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition()
        } else {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
    }

}