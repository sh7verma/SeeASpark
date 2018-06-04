package com.seeaspark

import adapters.QuestionAdapter
import android.content.Intent
import android.graphics.Color
import android.view.View
import kotlinx.android.synthetic.main.activity_questionaires.*
import models.QuestionModel

class QuestionnariesActivity : BaseActivity() {

    var mAdapterQuestions: QuestionAdapter? = null
    var mArrayQuestions = ArrayList<QuestionModel>()

    override fun initUI() {

    }

    override fun onCreateStuff() {
        mAdapterQuestions = QuestionAdapter(mArrayQuestions, mContext!!)
        vpQuestion.adapter = mAdapterQuestions
        vpQuestion.offscreenPageLimit = 4
        cpIndicator.setViewPager(vpQuestion)
        cpIndicator.fillColor = Color.BLACK
    }

    override fun initListener() {
        imgBackQuestion.setOnClickListener(this)
        txtDoneQuestion.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_questionaires

    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackQuestion -> {

            }
            txtDoneQuestion -> {
                var intent = Intent(mContext, PreferencesActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        }
    }
}