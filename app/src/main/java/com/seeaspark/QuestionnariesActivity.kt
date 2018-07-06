package com.seeaspark

import adapters.IndicatorAdapter
import adapters.QuestionAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_questionaires.*
import models.QuestionAnswerModel
import models.SignupModel
import network.RetrofitClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants

class QuestionnariesActivity : BaseActivity() {


    private var mAdapterQuestions: QuestionAdapter? = null
    private var mIndicatorAdapter: IndicatorAdapter? = null

    private var mArrayQuestions = ArrayList<QuestionAnswerModel>()
    private var userData: SignupModel? = null
    var mAnswerCount = 0
    var mQuestionarieInstance: QuestionnariesActivity? = null
    var mServerQuestion = JSONArray()
    var selectedPos = 0

    override fun initUI() {

    }

    override fun displayDayMode() {

    }

    override fun displayNightMode() {
    }

    override fun onCreateStuff() {

        mQuestionarieInstance = this

        userData = mGson.fromJson(mUtils!!.getString("userDataLocal", ""), SignupModel::class.java)
        mArrayQuestions.clear()
        mArrayQuestions.addAll(userData!!.answers)

        mAdapterQuestions = QuestionAdapter(mArrayQuestions, mContext!!, mQuestionarieInstance)
        vpQuestion.adapter = mAdapterQuestions
        vpQuestion.offscreenPageLimit = mArrayQuestions.size

        rvIndicators.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mIndicatorAdapter = IndicatorAdapter(mArrayQuestions, mContext!!, selectedPos)
        rvIndicators.adapter = mIndicatorAdapter

        vpQuestion.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mIndicatorAdapter!!.setSelectedPos(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


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
                if (connectedToInternet()) {
                    if (mAnswerCount == mArrayQuestions.size) {
                        hitAPI()
                    }
                } else
                    showInternetAlert(txtDoneQuestion)
            }
        }
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().postAnswers(userData!!.response.access_token, mServerQuestion.toString())
        call.enqueue(object : Callback<SignupModel> {

            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                dismissLoader()
                if (response.body().response != null) {
                    mUtils!!.setString("profileReview", "")
                    userData!!.response = response.body().response
                    mUtils!!.setString("access_token", userData!!.response.access_token)
                    mUtils!!.setInt("profile_status", userData!!.response.profile_status)
                    val intent = Intent(mContext!!, PreferencesActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        moveToSplash()
                    } else
                        showAlert(txtDoneQuestion, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtDoneQuestion, t!!.localizedMessage)
            }
        })
    }

    internal var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            for ((index, questionData) in mArrayQuestions.withIndex()) {
                if (questionData.id == intent.getIntExtra("questionId", 0)) {
                    questionData.answers = intent.getStringExtra("answer")
                    mArrayQuestions.set(index, questionData)
                    mIndicatorAdapter!!.notifyDataSetChanged()
                    break
                }
            }

            mAnswerCount = 0
            for ((index, questionData) in mArrayQuestions.withIndex()) {
                if (!TextUtils.isEmpty(questionData.answers)) {
                    val obj = JSONObject()
                    obj.put("answers", questionData.answers)
                    obj.put("question_id", questionData.id)
                    mServerQuestion.put(obj)
                    mAnswerCount++
                }
                Log.e("Question = Answer = ", "${questionData.id},${questionData.answers}")
            }

            if (mAnswerCount.equals(mArrayQuestions.size)) {
                txtDoneQuestion.visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                IntentFilter(Constants.QUESTIONS))

    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

}