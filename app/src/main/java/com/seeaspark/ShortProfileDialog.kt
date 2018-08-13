package com.seeaspark

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import com.squareup.picasso.Picasso
import customviews.FlowLayout
import kotlinx.android.synthetic.main.add_skills.view.*
import kotlinx.android.synthetic.main.dialog_short_profile.*
import models.CardsDisplayModel
import utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ShortProfileDialog : BaseActivity() {

    var mOtherProfileData: CardsDisplayModel? = null


    override fun getContentView(): Int {
        this.window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.light_white_transparent));
        return R.layout.dialog_short_profile
    }

    override fun initUI() {

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llMainShortProfileDialog.background = ContextCompat.getDrawable(this, R.drawable.white_short_profile_background)
        txtNameShortProfile.setTextColor(blackColor)
        txtProfessionShortProfile.setTextColor(blackColor)
        txtViewFullProfile.setTextColor(blackColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llMainShortProfileDialog.background = ContextCompat.getDrawable(this, R.drawable.dark_short_profile_background)
        txtNameShortProfile.setTextColor(whiteColor)
        txtProfessionShortProfile.setTextColor(whiteColor)
        txtViewFullProfile.setTextColor(whiteColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreateStuff() {
        mOtherProfileData = intent.getParcelableExtra("otherProfileData")
        populateData()
    }

    override fun initListener() {
        llOuter.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (llOuter) {
            llOuter -> {
                finish()
                overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun populateData() {

        Picasso.with(this).load(mOtherProfileData!!.avatar.avtar_url).into(imgShortProfile)

        txtNameShortProfile.text = mOtherProfileData!!.full_name

        var mGender = ""
        if (mOtherProfileData!!.gender == "1")
            mGender = getString(R.string.male)
        else if (mOtherProfileData!!.gender == "2")
            mGender = getString(R.string.female)
        else if (mOtherProfileData!!.gender == "3")
            mGender = getString(R.string.other)


        txtGenderShortProfile.text = "$mGender · ${calculateAge(mOtherProfileData!!.age)} Years"
        txtProfessionShortProfile.text = mOtherProfileData!!.profession.name

        if (mOtherProfileData!!.experience_month != 0)
            txtExperienceShortProfile.text = "${mOtherProfileData!!.experience_year}.${mOtherProfileData!!.experience_month} Years of Experience"
        else
            txtExperienceShortProfile.text = "${mOtherProfileData!!.experience_year} Years of Experience"

        txtBioShortProfile.text = mOtherProfileData!!.bio

        for ((index, skills) in mOtherProfileData!!.skills.withIndex()) {
            if (index == 3) {
                flShortProfile.addView(inflateView("+ ${mOtherProfileData!!.skills.size - index} More"))
                break
            } else {
                flShortProfile.addView(inflateView(skills))
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
    fun calculateAge(birthdate: String): Int {
        val birth_format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val birth_date = birth_format.parse(birthdate)
        val cal = Calendar.getInstance()
        val today = cal.time
        val diff = today.time - birth_date.time
        val diffSeconds = diff / 1000 % 60
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000) % 24
        val diffDays = diff / (24 * 60 * 60 * 1000)
        val diffyears = (diffDays / 365).toInt()
        return diffyears
    }


    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out)
    }

}