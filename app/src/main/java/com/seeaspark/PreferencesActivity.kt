package com.seeaspark

import adapters.PreferProfessionAdapter
import android.app.Activity
import android.content.Intent
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.cocosw.bottomsheet.BottomSheet
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.android.synthetic.main.activity_preferences.view.*
import kotlinx.android.synthetic.main.add_skills.view.*
import models.ProfessionModel

class PreferencesActivity : BaseActivity() {

    var mSelectedSkillsArray = ArrayList<String>()
    private val SKILLS: Int = 1

    var mAdapterProfession: PreferProfessionAdapter? = null
    var mProfessionArray = ArrayList<ProfessionModel>()

    override fun initUI() {
        var bottomParms = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight / 6)
        llProfessionText.layoutParams = bottomParms
        initPersistentBottomsheetProfession()
        rvProfessionPrefer.layoutManager = LinearLayoutManager(this)
    }

    override fun onCreateStuff() {

        rsbDistance.setNotifyWhileDragging(true)
        rsbDistance.setOnRangeSeekBarChangeListener { bar, minValue, maxValue -> txtDistanceCount.text = "$maxValue Miles" }
        rsbDistance.selectedMaxValue = 20

        rsbExperience.setNotifyWhileDragging(true)
        rsbExperience.setOnRangeSeekBarChangeListener { bar, minValue, maxValue -> txtExperienceCount.text = "$maxValue Years" }
        rsbExperience.selectedMaxValue = 8



        mAdapterProfession = PreferProfessionAdapter(mContext!!, mProfessionArray)
        rvProfessionPrefer.adapter = mAdapterProfession
    }

    override fun initListener() {
        imgForwardPrefer.setOnClickListener(this)
        llGenderPrefer.setOnClickListener(this)
        llProfessionListing.setOnClickListener(this)
        llSkillSelection.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_preferences

    override fun getContext() = this

    override fun onClick(view: View?) {
        var intent: Intent? = null
        when (view) {
            llSkillSelection -> {
                intent = Intent(mContext, SkillSelectionActivity::class.java)
                intent.putStringArrayListExtra("selectedSkills", mSelectedSkillsArray)
                startActivityForResult(intent, SKILLS)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
            imgForwardPrefer -> {
                intent = Intent(mContext, DisclamierDialog::class.java)
                startActivity(intent)
            }
            llGenderPrefer -> {
                optionGender()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SKILLS -> {
                    var count = 0
                    flSkillsPrefer.removeAllViews()
                    mSelectedSkillsArray.clear()
                    mSelectedSkillsArray.addAll(data!!.getStringArrayListExtra("selectedSkills"))
                    for (skills: String in mSelectedSkillsArray) {
                        if (count == 3) {
                            flSkillsPrefer.addView(inflateView("+ ${mSelectedSkillsArray.size - count} More"))
                            break
                        } else {
                            flSkillsPrefer.addView(inflateView(skills))
                        }
                        count++
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun inflateView(skillValue: String): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms
        interestChip.txtAddSkillChip.text = skillValue
        return interestChip
    }


    fun initPersistentBottomsheetProfession() {
        // bottom view of accepted job

        var behaviorJobDetail = BottomSheetBehavior.from<View>(coordinator.llProfessionListing)
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
                .sheet(R.menu.menu_gender).listener { dialog, which ->
            when (which) {
                R.id.item_male -> {
                    txtGenderPrefer.setText(R.string.male)
                }
                R.id.item_female -> {
                    txtGenderPrefer.setText(R.string.female)
                }
                R.id.item_other -> {
                    txtGenderPrefer.setText(R.string.other)
                }
            }
        }.show()
    }


}