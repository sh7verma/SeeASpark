package com.seeaspark

import android.app.Activity
import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.activity_select_experience.*

class SelectExperienceActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_select_experience

    var mYears: Int = -1
    var mMonths: Int = -1

    override fun initUI() {

    }

    override fun onCreateStuff() {

        mYears = intent.getIntExtra("Years", 0)

        mMonths = intent.getIntExtra("Months", 0)

        rsbYearsSelect.isNotifyWhileDragging = true
        rsbYearsSelect.selectedMaxValue = mYears
        txtYearCountSelect.text = "$mYears"
        rsbYearsSelect.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            txtYearCountSelect.text = "$maxValue"
            mYears = maxValue as Int
        }

        rsbMonthsSelect.isNotifyWhileDragging = true
        rsbMonthsSelect.selectedMaxValue = mMonths
        txtMonthCountSelect.text = "$mMonths"
        rsbMonthsSelect.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            txtMonthCountSelect.text = "$maxValue"
            mMonths = maxValue as Int
        }
    }

    override fun initListener() {
        imgBackExperienceSelect.setOnClickListener(this)
        txtDoneSelectExperience.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {

            imgBackExperienceSelect -> {
                moveBack()
            }
            txtDoneSelectExperience -> {
                val intent = Intent()
                intent.putExtra("Years", mYears)
                intent.putExtra("Months", mMonths)
                setResult(Activity.RESULT_OK, intent)
                moveBack()
            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

}