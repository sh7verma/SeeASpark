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
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_questionaires.*
import kotlinx.android.synthetic.main.activity_view_profile.*
import models.ProfileModel
import models.QuestionAnswerModel
import models.QuestionListingModel
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
    var isSwitchAccount = false
    var mNewUserType = 0

    override fun getContentView() = R.layout.activity_questionaires

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
        mUtils!!.setString("profileReview", "no")

        if (intent.hasExtra("newUserType")) {
            mNewUserType = intent.getIntExtra("newUserType", 0)
            imgBackQuestion.visibility = View.VISIBLE
            isSwitchAccount = true
            hitFetchSwitchQuestionAPI(mNewUserType)
        } else {
            if (mUtils!!.getInt("switchMode", 0) == 1) {
                imgBackQuestion.visibility = View.VISIBLE
                isSwitchAccount = true
                mNewUserType = Constants.MENTOR
                hitFetchSwitchQuestionAPI(mNewUserType)
            } else {
                if (userData!!.response.profile_status != 2) {
                    mArrayQuestions.addAll(userData!!.answers)
                    populateData()
                } else {
                    imgBackQuestion.visibility = View.VISIBLE
                    isSwitchAccount = true
                    mNewUserType = Constants.MENTOR
                    hitFetchSwitchQuestionAPI(mNewUserType)
                }
            }
        }
    }

    private fun populateData() {

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


    override fun getContext() = this

    override fun onClick(view: View?) {
        when (view) {
            imgBackQuestion -> {

            }
            txtDoneQuestion -> {
                if (connectedToInternet()) {
                    if (mAnswerCount == mArrayQuestions.size) {
                        if (isSwitchAccount)
                            hitSwitchAccountAPI()
                        else
                            hitAPI()
                    }
                } else
                    showInternetAlert(txtDoneQuestion)
            }
        }
    }

    private fun hitSwitchAccountAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().postSwitchAnswers(userData!!.response.access_token, mNewUserType, mServerQuestion.toString())
        call.enqueue(object : Callback<SignupModel> {

            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                if (response.body().response != null) {
                    if (mUtils!!.getInt("switchMode", 0) == 1) {
                        moveToPreferences(response.body().response)
                    } else {
                        dismissLoader()
                        updateProfileDatabase(response.body().response)
                        finish()
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
                    }
                } else {
                    dismissLoader()
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
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

    private fun updateProfileDatabase(response: SignupModel.ResponseBean?) {
        userData!!.response = response
        mUtils!!.setString("userDataLocal", mGson.toJson(userData))
        val broadCastIntent = Intent(Constants.PROFILE_UPDATE)
        broadCastIntent.putExtra("updateHomeScreen", true)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    private fun hitFetchSwitchQuestionAPI(userType: Int) {
        showLoader()
        val call = RetrofitClient.getInstance().getSwitchQuestions(userData!!.response.access_token,
                userType)
        call.enqueue(object : Callback<QuestionListingModel> {
            override fun onResponse(call: Call<QuestionListingModel>?, response: Response<QuestionListingModel>) {
                if (response.body().response != null) {
                    mArrayQuestions.clear()
                    mArrayQuestions.addAll(response.body().response)
                    populateData()
                } else {
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(txtDoneQuestion, response.body().error!!.message!!)
                }
                dismissLoader()
            }

            override fun onFailure(call: Call<QuestionListingModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtChangeUserType, t!!.localizedMessage)
            }
        })
    }

    private fun hitAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().postAnswers(userData!!.response.access_token, mServerQuestion.toString())
        call.enqueue(object : Callback<SignupModel> {

            override fun onResponse(call: Call<SignupModel>?, response: Response<SignupModel>) {
                if (response.body().response != null) {
                    moveToPreferences(response.body().response)
                } else {
                    dismissLoader()
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
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

    private fun moveToPreferences(response: SignupModel.ResponseBean) {
        mUtils!!.setString("profileReview", "")
        userData!!.response = response
        mUtils!!.setString("access_token", userData!!.response.access_token)
        mUtils!!.setInt("profile_status", userData!!.response.profile_status)
        mUtils!!.setString("user_name", userData!!.response.full_name)
        mUtils!!.setString("user_pic", userData!!.response.avatar.avtar_url)
        mUtils!!.setString("user_id", response.id.toString())

        setDataOnFirebase()
    }

    internal fun setDataOnFirebase() {
        val model = ProfileModel()
        model.access_token = userData!!.response.access_token
        model.user_id = userData!!.response.id.toString()
        model.online_status = Constants.ONLINE_LONG
        model.user_name = userData!!.response.full_name
        model.user_pic = userData!!.response.avatar.avtar_url
        val mFirebaseConfigProfile = FirebaseDatabase.getInstance().getReference().child(Constants.USERS)
        mFirebaseConfigProfile.child("id_" + model.user_id).setValue(model).addOnSuccessListener {
            try {
                dismissLoader()
                db!!.addProfile(model)
                val intent = Intent(mContext!!, PreferencesActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.addOnFailureListener {

        }
    }

    internal var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            for ((index, questionData) in mArrayQuestions.withIndex()) {
                if (questionData.id == intent.getIntExtra("questionId", 0)) {
                    questionData.setuserAnswers(intent.getStringArrayListExtra("answer"))
                    mArrayQuestions[index] = questionData
                    mIndicatorAdapter!!.notifyDataSetChanged()
                    break
                }
            }

            mAnswerCount = 0
            mServerQuestion = JSONArray()
            for (questionData in mArrayQuestions) {
                if (questionData.getuserAnswers().isNotEmpty()) {
                    val obj = JSONObject()
                    val stringBuilder = StringBuilder()
                    for (answers in questionData.getuserAnswers()) {
                        stringBuilder.append(answers).append("54#45")
                    }
                    val answers = stringBuilder.toString()
                    obj.put("answers", answers.substring(0, answers.length - 5))
                    obj.put("question_id", questionData.id)
                    mServerQuestion.put(obj)
                    Log.e("Question =", "${questionData.id}")
                    Log.e("Answers = ", "${questionData.getuserAnswers()}")
                    mAnswerCount++
                }
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