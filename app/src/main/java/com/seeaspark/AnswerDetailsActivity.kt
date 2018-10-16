package com.seeaspark

import adapters.AnswerDetailAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.faradaj.blurbehind.BlurBehind
import kotlinx.android.synthetic.main.activity_answer_detail.*

class AnswerDetailsActivity : BaseActivity() {

    var mAnswersArray = arrayOf<String>()

    lateinit var answerData: String

    override fun getContentView() = R.layout.activity_answer_detail

    override fun initUI() {
        BlurBehind.getInstance().withAlpha(100)
                .withFilterColor(ContextCompat.getColor(this, R.color.transperent))
                .setBackground(this)
        rvAnswersDetail.layoutManager = LinearLayoutManager(mContext)
    }

    override fun displayDayMode() {

    }

    override fun displayNightMode() {

    }

    override fun onCreateStuff() {
        answerData = intent.getStringExtra("answerData")
        mAnswersArray = answerData.split("54#45").toTypedArray()
        rvAnswersDetail.adapter = AnswerDetailAdapter(mAnswersArray, mContext!!)
    }

    override fun initListener() {
        imgCancel.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun getContext() = this

    override fun onClick(view: View?) {

    }
}