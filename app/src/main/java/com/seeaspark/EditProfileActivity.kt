package com.seeaspark

import adapters.AvailabilityAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.cocosw.bottomsheet.BottomSheet
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.add_skills.view.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_select_availability.*
import models.AvailabilityModel
import models.LanguageModel
import models.SignupModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditProfileActivity : BaseActivity() {

    private val AVATAR: Int = 0
    private val PROFESSION: Int = 1
    private val EXPERIENCE: Int = 2
    private val LANGUAGES: Int = 3
    private val SKILL: Int = 4

    private var userData: SignupModel? = null
    private var mGender: Int = 0
    private var mShowStartDate = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private var calDOB: Calendar? = null
    var mDOB = SimpleDateFormat("dd-MM-yyyy", Locale.US)

    private var mAvatarName = Constants.EMPTY
    private var mAvatarURL = Constants.EMPTY
    private var mAvatarParentId = 0
    private var mProfessionId: Int = 0
    private var mYears: Int = 0
    private var mMonths: Int = 0
    private var mSelectedLanguagesArray = ArrayList<LanguageModel>()
    private var mSelectedSkillsArray = ArrayList<String>()
    private val availabilityArray = ArrayList<AvailabilityModel>()

    override fun getContentView() = R.layout.activity_edit_profile

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun initUI() {

        txtOptionCustom.visibility = View.VISIBLE

        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        val typefaceBold = Typeface.createFromAsset(assets, "fonts/bold.otf")

        edDescriptionEditProfile.typeface = typefaceBold
        edBioEditProfile.typeface = typefaceBold
        edNameProfile.typeface = typefaceBold

        val cd = ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
        llCustomToolbar.background = cd
        cd.alpha = 0

        svEditProfile.setOnTouchListener { v, event ->
            svEditProfile.clearFocus()
            false
        }

        svEditProfile.setOnScrollViewListener { v, l, t, oldl, oldt ->
            cd.alpha = getAlphaforActionBar(v.scrollY)
        }

        edBioEditProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty())
                    txtCountBioCharacter.text = "${320 - p0.length} Characters Left"
                else
                    txtCountBioCharacter.text = "320 Characters Left"
            }
        })

        edDescriptionEditProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty())
                    txtCountDescriptionCharacter.text = "${1024 - p0.length} Characters Left"
                else
                    txtCountDescriptionCharacter.text = "1024 Characters Left"
            }
        })

        rvAvailability.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)

    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llMainEditProfile.background = ContextCompat.getDrawable(this, R.drawable.white_short_profile_background)
        txtAgeEditProfile.setBackgroundResource(whiteRipple)
        txtGenderEditProfile.setBackgroundResource(whiteRipple)
        txtProfessionEditProfile.setBackgroundResource(whiteRipple)
        txtExperienceEditProfile.setBackgroundResource(whiteRipple)
        llLanguageEditProfile.setBackgroundResource(whiteRipple)
        llSkillEditProfile.setBackgroundResource(whiteRipple)

        edNameProfile.setBackgroundColor(whiteColor)
        edBioEditProfile.setBackgroundColor(whiteColor)
        edDescriptionEditProfile.setBackgroundColor(whiteColor)

        edNameProfile.setTextColor(darkGrey)
        txtAgeEditProfile.setTextColor(darkGrey)
        txtGenderEditProfile.setTextColor(darkGrey)
        txtProfessionEditProfile.setTextColor(darkGrey)
        txtExperienceEditProfile.setTextColor(darkGrey)
        edBioEditProfile.setTextColor(darkGrey)
        edDescriptionEditProfile.setTextColor(darkGrey)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llMainEditProfile.background = ContextCompat.getDrawable(this, R.drawable.dark_short_profile_background)
        txtAgeEditProfile.setBackgroundResource(blackRipple)
        txtGenderEditProfile.setBackgroundResource(blackRipple)
        txtProfessionEditProfile.setBackgroundResource(blackRipple)
        txtExperienceEditProfile.setBackgroundResource(blackRipple)
        llLanguageEditProfile.setBackgroundResource(blackRipple)
        llSkillEditProfile.setBackgroundResource(blackRipple)

        edNameProfile.setBackgroundColor(blackColor)
        edBioEditProfile.setBackgroundColor(blackColor)
        edDescriptionEditProfile.setBackgroundColor(blackColor)

        edNameProfile.setTextColor(whiteColor)
        txtAgeEditProfile.setTextColor(whiteColor)
        txtGenderEditProfile.setTextColor(whiteColor)
        txtProfessionEditProfile.setTextColor(whiteColor)
        txtExperienceEditProfile.setTextColor(whiteColor)
        edBioEditProfile.setTextColor(whiteColor)
        edDescriptionEditProfile.setTextColor(whiteColor)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreateStuff() {

        /// intialize calendar
        calDOB = Calendar.getInstance(TimeZone.getDefault())

        txtTitleCustom.text = getString(R.string.edit_profile)
        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        val dateParts = userData!!.response.age.split("-")
        calDOB!!.set(Calendar.YEAR, dateParts[2].toInt())
        calDOB!!.set(Calendar.MONTH, dateParts[1].toInt() - 1)
        calDOB!!.set(Calendar.DATE, dateParts[0].toInt())

        getAvailabilityData()
        populateData()
    }


    override fun initListener() {
        imgBackCustom.setOnClickListener(this)
        imgEditProfile.setOnClickListener(this)
        txtAgeEditProfile.setOnClickListener(this)
        txtGenderEditProfile.setOnClickListener(this)
        txtProfessionEditProfile.setOnClickListener(this)
        txtExperienceEditProfile.setOnClickListener(this)
        llLanguageEditProfile.setOnClickListener(this)
        llSkillEditProfile.setOnClickListener(this)
        txtAgeEditProfile.setOnClickListener(this)
        txtSaveEditProfile.setOnClickListener(this)
        txtOptionCustom.setOnClickListener(this)
        llAvailability.setOnClickListener(this)
    }

    override fun getContext() = this


    override fun onClick(view: View) {
        var intent: Intent? = null
        when (view) {
            llAvailability -> {
                selectAvailabilityDialog()
            }
            imgBackCustom -> {
                moveBack()
            }
            txtAgeEditProfile -> {
                setBirthday()
            }
            txtGenderEditProfile -> {
                optionGender()
            }
            imgEditProfile -> {
                intent = Intent(mContext, SelectAvatarActivity::class.java)
                intent.putExtra("avatarURL", mAvatarURL)
                intent.putExtra("avatarParentId", mAvatarParentId)
                intent.putExtra("gender", mGender)
                startActivityForResult(intent, AVATAR)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            txtProfessionEditProfile -> {
                intent = Intent(mContext, SelectProfessionActivity::class.java)
                intent.putExtra("professionId", mProfessionId)
                intent.putExtra("professionName", txtProfessionEditProfile.text.toString())
                startActivityForResult(intent, PROFESSION)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            txtExperienceEditProfile -> {
                intent = Intent(mContext, SelectExperienceActivity::class.java)
                intent.putExtra("Years", mYears)
                intent.putExtra("Months", mMonths)
                startActivityForResult(intent, EXPERIENCE)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            llSkillEditProfile -> {
                intent = Intent(mContext, SelectSkillActivity::class.java)
                intent.putStringArrayListExtra("selectedSkills", mSelectedSkillsArray)
                startActivityForResult(intent, SKILL)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            llLanguageEditProfile -> {
                intent = Intent(mContext, SelectLanguageActivity::class.java)
                intent.putParcelableArrayListExtra("selectedLanguages", mSelectedLanguagesArray)
                startActivityForResult(intent, LANGUAGES)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            txtOptionCustom -> {
                if (connectedToInternet())
                    verifyDetails()
                else
                    showInternetAlert(txtSaveEditProfile)
            }
        }
    }

    private fun selectAvailabilityDialog() {
        val tempAvailabilityArray = ArrayList<AvailabilityModel>()
        tempAvailabilityArray.addAll(availabilityArray)

        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_availability)
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.rvAvailabilitySelection.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        dialog.rvAvailabilitySelection.adapter = AvailabilityAdapter(tempAvailabilityArray,
                this, true)

        dialog.txtDoneAvailability.setOnClickListener {
            availabilityArray.clear()
            availabilityArray.addAll(tempAvailabilityArray)
            rvAvailability.adapter = AvailabilityAdapter(availabilityArray,
                    this, false)
            dialog.dismiss()
        }

        dialog.txtClearAvailability.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun verifyDetails() {
        when {
            mAvatarURL.isEmpty() -> showAlert(txtSaveEditProfile, getString(R.string.error_avatar))
            edNameProfile.text.trim().toString().length < 2 -> showAlert(txtSaveEditProfile, getString(R.string.error_name))
            edBioEditProfile.text.toString().trim().isEmpty() -> showAlert(txtSaveEditProfile, getString(R.string.error_bio))
            edDescriptionEditProfile.text.toString().trim().isEmpty() -> showAlert(txtSaveEditProfile, getString(R.string.error_description))
            else -> hitAPI()
        }
    }

    private fun hitAPI() {

        showLoader()

        val tempLanguagesArray = ArrayList<String>()
        for (languageValue in mSelectedLanguagesArray) {
            tempLanguagesArray.add(languageValue.id.toString())
        }

        val stringBuilder = StringBuilder()
        for (availabilityValue in availabilityArray) {
            if (availabilityValue.isSelected)
                stringBuilder.append(availabilityValue.dayId).append(",")
        }


        var tempAvailability = Constants.EMPTY
        if (stringBuilder.toString().isNotBlank())
            tempAvailability = stringBuilder.toString()
                    .substring(0, stringBuilder.toString().length - 1).trim()

        val tempLanguages = tempLanguagesArray.toString()
                .substring(1, tempLanguagesArray.toString().length - 1).trim()

        val tempSkills = mSelectedSkillsArray.toString()
                .substring(1, mSelectedSkillsArray.toString().length - 1).trim()

        val call = RetrofitClient.getInstance().editProfile(mUtils!!.getString("access_token", ""),
                mAvatarName, edNameProfile.text.toString().trim(),
                mDOB.format(calDOB!!.time),
                mGender.toString(),
                tempLanguages,
                txtProfessionEditProfile.text.toString(),
                "$mYears,$mMonths",
                tempSkills,
                edBioEditProfile.text.toString().trim(),
                edDescriptionEditProfile.text.toString(),
                tempAvailability)

        call.enqueue(object : Callback<SignupModel> {

            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {

                mUtils!!.setString("user_name", response.body().response.full_name)
                mUtils!!.setString("user_pic", response.body().response.avatar.avtar_url)
                userData!!.response = response.body().response
                mUtils!!.setString("userDataLocal", mGson.toJson(userData))

                val mFirebaseConfigChats = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS)
                val ids = db!!.getDialogs(userData!!.response.id.toString())
                for (dialogId in ids.keys) {
                    mFirebaseConfigChats.child(dialogId).child("name").child(userData!!.response.id.toString()).setValue(userData!!.response.full_name)
                    mFirebaseConfigChats.child(dialogId).child("profile_pic").child(userData!!.response.id.toString()).setValue(userData!!.response.avatar.avtar_url)
                }

                val values = HashMap<String, Any>()
                values.put("online_status", Constants.ONLINE_LONG)
                values.put("access_token", userData!!.response.access_token)
                values.put("user_name", userData!!.response.full_name)
                values.put("user_pic", userData!!.response.avatar.avtar_url)

                val mFirebaseConfigProfile = FirebaseDatabase.getInstance().reference.child(Constants.USERS)
                mFirebaseConfigProfile.child("id_" + userData!!.response.id.toString()).updateChildren(values).addOnCompleteListener {
                    dismissLoader()
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    moveBack()
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtSaveEditProfile, t!!.localizedMessage)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun populateData() {
        mAvatarURL = userData!!.response.avatar.avtar_url
        mAvatarParentId = userData!!.response.avatar.parent_id
        Picasso.with(this).load(mAvatarURL).placeholder(R.drawable.placeholder_image).into(imgEditProfile)

        edNameProfile.setText(userData!!.response.full_name)
        edNameProfile.setSelection(edNameProfile.text.toString().trim().length)

        txtAgeEditProfile.text = "${calculateAge(userData!!.response.age)} Years"

        mGender = userData!!.response.gender.toInt()

        when {
            userData!!.response.gender == "1" -> txtGenderEditProfile.text = getString(R.string.male)
            userData!!.response.gender == "2" -> txtGenderEditProfile.text = getString(R.string.female)
            userData!!.response.gender == "3" -> txtGenderEditProfile.text = getString(R.string.other)
        }

        mProfessionId = userData!!.response.profession.id
        txtProfessionEditProfile.text = userData!!.response.profession.name

        mYears = userData!!.response.experience_year
        mMonths = userData!!.response.experience_month

        if (userData!!.response.experience_month != 0)
            txtExperienceEditProfile.text = "${userData!!.response.experience_year}.${userData!!.response.experience_month} Yrs"
        else
            txtExperienceEditProfile.text = "${userData!!.response.experience_year} Yrs"

        edBioEditProfile.setText(userData!!.response.bio)
        edBioEditProfile.setSelection(edBioEditProfile.text.toString().trim().length)

        edDescriptionEditProfile.setText(userData!!.response.pro_description)
        edDescriptionEditProfile.setSelection(edDescriptionEditProfile.text.toString().trim().length)

        rvAvailability.adapter = AvailabilityAdapter(availabilityArray,
                this, false)

        mSelectedLanguagesArray.addAll(userData!!.response.languages)
        displayLanguageChips()

        mSelectedSkillsArray.addAll(userData!!.response.skills)
        displaySkillsChips()
    }

    private fun getAlphaforActionBar(scrollY: Int): Int {
        val minDist = 0
        val maxDist = 300
        when {
            scrollY > maxDist -> {

                return 255
            }
            scrollY < minDist -> {

                return 0
            }
            else -> {
                var alpha = 0
                alpha = (255.0 / maxDist * scrollY).toInt()
                return alpha
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun inflateView(skillValue: String, showBlackBackground: Boolean): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms

        if (mUtils!!.getInt("nightMode", 0) == 1) {
            interestChip.txtAddSkillChip.background = ContextCompat.getDrawable(this, R.drawable.skill_card_outer)
            interestChip.txtAddSkillChip.setTextColor(ContextCompat.getColor(this, R.color.white_color))
        } else {
            if (showBlackBackground) {
                interestChip.txtAddSkillChip.background = ContextCompat.getDrawable(this, R.drawable.skill_card_outer)
                interestChip.txtAddSkillChip.setTextColor(ContextCompat.getColor(this, R.color.black_color))
            } else {
                interestChip.txtAddSkillChip.background = ContextCompat.getDrawable(this, R.drawable.skill_card_outer)
                interestChip.txtAddSkillChip.setTextColor(ContextCompat.getColor(this, R.color.black_color))
            }
        }
        interestChip.txtAddSkillChip.text = skillValue
        return interestChip
    }

    private val dobPickerListener = DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
        calDOB!!.set(Calendar.YEAR, selectedYear)
        calDOB!!.set(Calendar.MONTH, selectedMonth)
        calDOB!!.set(Calendar.DATE, selectedDay)
        try {
            calculateAge(mShowStartDate.format(calDOB!!.time))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun getAvailabilityData() {
        var selectedAvailabilityArray = arrayOf<String>()
        if (userData!!.response.availability.isNotBlank())
            selectedAvailabilityArray = userData!!.response.availability.split(",").toTypedArray()

        val array = arrayListOf("S", "M", "T", "W", "T", "F", "S")
        for (i in 1..7) {
            val availabilityModel = AvailabilityModel()
            availabilityModel.dayId = i
            availabilityModel.dayValue = array[i - 1]
            availabilityModel.isSelected = selectedAvailabilityArray.contains(i.toString())
            availabilityArray.add(availabilityModel)
        }
    }

    fun setBirthday() {
        Constants.closeKeyboard(this, txtAgeEditProfile)
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.add(Calendar.YEAR, -16)
        val datePickerDOB = DatePickerDialog(this,
                R.style.DatePickerTheme,
                dobPickerListener,
                calDOB!!.get(Calendar.YEAR),
                calDOB!!.get(Calendar.MONTH),
                calDOB!!.get(Calendar.DAY_OF_MONTH))

        datePickerDOB.setCancelable(false)
        datePickerDOB.datePicker.maxDate = calendar.timeInMillis
        datePickerDOB.setTitle("")
        datePickerDOB.show()
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
        txtAgeEditProfile.text = "$diffyears Years"
        return diffyears
    }

    private fun optionGender() {
        BottomSheet.Builder(this, R.style.BottomSheet_Dialog)
                .title(getString(R.string.select_gender))
                .sheet(R.menu.menu_gender).listener { dialog, which ->
                    when (which) {
                        R.id.item_male -> {
                            txtGenderEditProfile.text = getString(R.string.male)
                            if (mGender != 1) {
                                mGender = 1
                                mAvatarURL = Constants.EMPTY
                                mAvatarParentId = 0
                                showToast(mContext!!, getString(R.string.gender_changed))
                                Picasso.with(this).load(R.drawable.placeholder_image).placeholder(R.drawable.placeholder_image).into(imgEditProfile)
                            }
                        }
                        R.id.item_female -> {
                            txtGenderEditProfile.text = getString(R.string.female)
                            if (mGender != 2) {
                                mGender = 2
                                mAvatarURL = Constants.EMPTY
                                mAvatarParentId = 0
                                showToast(mContext!!, getString(R.string.gender_changed))
                                Picasso.with(this).load(R.drawable.placeholder_image).placeholder(R.drawable.placeholder_image).into(imgEditProfile)
                            }
                        }
                        R.id.item_other -> {
                            txtGenderEditProfile.text = getString(R.string.other)
                            if (mGender != 3) {
                                mGender = 3
                                mAvatarURL = Constants.EMPTY
                                mAvatarParentId = 0
                                showToast(mContext!!, getString(R.string.gender_changed))
                                Picasso.with(this).load(R.drawable.placeholder_image).placeholder(R.drawable.placeholder_image).into(imgEditProfile)
                            }
                        }
                    }
                }.show()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AVATAR -> {
                    mAvatarURL = data!!.getStringExtra("avatarURL")
                    mAvatarName = data.getStringExtra("avatarName")
                    mAvatarParentId = data.getIntExtra("avatarParentId", 0)
                    Picasso.with(this).load(mAvatarURL).placeholder(R.drawable.placeholder_image).into(imgEditProfile)
                }
                PROFESSION -> {
                    txtProfessionEditProfile.text = data!!.getStringExtra("professionName")
                    mProfessionId = data.getIntExtra("professionId", 0)
                }
                EXPERIENCE -> {
                    mYears = data!!.getIntExtra("Years", 0)
                    mMonths = data.getIntExtra("Months", 0)

                    if (mMonths != 0)
                        txtExperienceEditProfile.text = "$mYears.$mMonths Yrs"
                    else
                        txtExperienceEditProfile.text = "$mYears Yrs"
                }
                SKILL -> {
                    flSkillEditProfile.removeAllViews()
                    mSelectedSkillsArray.clear()
                    mSelectedSkillsArray.addAll(data!!.getStringArrayListExtra("selectedSkills"))
                    displaySkillsChips()
                }
                LANGUAGES -> {
                    flLanguagesEditProfile.removeAllViews()
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
        for ((index, languages) in mSelectedLanguagesArray.withIndex()) {
            if (index == 1) {
                flLanguagesEditProfile.addView(inflateView("+ ${mSelectedLanguagesArray.size - index}", false))
                break
            } else {
                flLanguagesEditProfile.addView(inflateView(languages.name, true))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun displaySkillsChips() {
        for ((index, skills) in mSelectedSkillsArray.withIndex()) {
            if (index == 1) {
                flSkillEditProfile.addView(inflateView("+ ${mSelectedSkillsArray.size - index}", false))
                break
            } else {
                flSkillEditProfile.addView(inflateView(skills, true))
            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        Constants.closeKeyboard(mContext!!, imgBackCustom)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun onDestroy() {
        Constants.OWNSKILLS_ARRAY.clear()
        super.onDestroy()
    }

}