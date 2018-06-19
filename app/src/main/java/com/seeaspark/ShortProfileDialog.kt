package com.seeaspark

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import customviews.FlowLayout
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.android.synthetic.main.add_skills.view.*
import kotlinx.android.synthetic.main.dialog_short_profile.*
import utils.Utils

class ShortProfileDialog : Activity() {
    private var mScreenwidth: Int = 0
    private var mScreenheight: Int = 0
    var mUtils: Utils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wmlp = this.window.attributes

        wmlp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        this.setFinishOnTouchOutside(true)
        setContentView(R.layout.dialog_short_profile)
        getDefaults()
        window.setLayout(mScreenwidth, (mScreenheight * 0.8).toInt())

        mUtils = Utils(this)

        txtViewFullProfile.setOnClickListener {
            /// navigate to Full Profile
            Toast.makeText(this, R.string.work_in_progress, Toast.LENGTH_LONG).show()
        }
        flShortProfile.addView(inflateView("More"))
        flShortProfile.addView(inflateView("+3 More"))
    }

    private fun getDefaults() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        mScreenwidth = dm.widthPixels
        mScreenheight = dm.heightPixels
    }

    private fun inflateView(skillValue: String): View {
        val interestChip = LayoutInflater.from(this).inflate(R.layout.add_skills, null, false)
        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        interestChip.llMainAddSkills.layoutParams = innerParms
        interestChip.txtAddSkillChip.text = skillValue
        return interestChip
    }


}