package com.seeaspark

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.content.IntentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import customviews.FlowLayout
import customviews.ScrollViewX
import kotlinx.android.synthetic.main.activity_view_profile.*
import kotlinx.android.synthetic.main.activity_view_profile.view.*
import kotlinx.android.synthetic.main.add_skills.view.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import models.SignupModel
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
    override fun onCreateStuff() {
        populateData()
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
                showToast(mContext!!, getString(R.string.work_in_progress))
            }
        }
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

        if (userData!!.response.user_type == Constants.MENTEE)
            txtChangeUserType.text = getString(R.string.become_mentor)
        else
            txtChangeUserType.text = getString(R.string.become_mentee)

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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun inflateView(skillValue: String): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms
        interestChip.txtAddSkillChip.background = ContextCompat.getDrawable(this, R.drawable.selected_skills)
        interestChip.txtAddSkillChip.text = skillValue
        interestChip.txtAddSkillChip.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        return interestChip
    }

    @Throws(ParseException::class)
    private fun calculateAge(birthdate: String): Int {
        val birthFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val birthDate = birthFormat.parse(birthdate)
        val cal = Calendar.getInstance()
        val today = cal.time
        val diff = today.time - birthDate.time
        val diffSeconds = diff / 1000 % 60
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000) % 24
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


    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }
}