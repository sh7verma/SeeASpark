package com.seeaspark

import android.app.Activity
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

class ShortProfileDialog : Activity() {
    private var mScreenwidth: Int = 0
    private var mScreenheight: Int = 0
    var mUtils: Utils? = null
    var mOtherProfileData: CardsDisplayModel? = null

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wmlp = this.window.attributes

        wmlp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        this.setFinishOnTouchOutside(true)
        setContentView(R.layout.dialog_short_profile)
        getDefaults()
        window.setLayout(mScreenwidth, (mScreenheight))

        mUtils = Utils(this)

        txtViewFullProfile.setOnClickListener {
            /// navigate to Full Profile
            Toast.makeText(this, R.string.work_in_progress, Toast.LENGTH_LONG).show()
        }

        mOtherProfileData = intent.getParcelableExtra("otherProfileData")

        /// display Data
        populateData()

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun populateData() {

        Picasso.with(this).load(mOtherProfileData!!.avatar).into(imgShortProfile)

        txtNameShortProfile.text = mOtherProfileData!!.full_name

        var mGender = ""
        if (mOtherProfileData!!.gender == "1")
            mGender = getString(R.string.male)
        else if (mOtherProfileData!!.gender == "2")
            mGender = getString(R.string.female)
        else if (mOtherProfileData!!.gender == "1")
            mGender = getString(R.string.other)


        txtGenderShortProfile.text = "$mGender . ${calculateAge(mOtherProfileData!!.age)} Years"
        txtProfessionShortProfile.text = mOtherProfileData!!.profession.name
        txtExperienceShortProfile.text = "${mOtherProfileData!!.experience_year}.${mOtherProfileData!!.experience_month} Years of Experience"
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

    private fun getDefaults() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mScreenwidth = dm.widthPixels
        mScreenheight = dm.heightPixels
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun inflateView(skillValue: String): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms
        interestChip.txtAddSkillChip.background=ContextCompat.getDrawable(this,R.drawable.selected_skills)
        interestChip.txtAddSkillChip.text = skillValue
        interestChip.txtAddSkillChip.setTextColor(ContextCompat.getColor(this,R.color.white_color))
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
        overridePendingTransition(0,R.anim.slidedown_out)
    }

}