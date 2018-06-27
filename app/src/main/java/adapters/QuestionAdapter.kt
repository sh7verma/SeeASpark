package adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.QuestionnariesActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_questions.view.*
import models.QuestionAnswerModel


class QuestionAdapter(mQuestionsArray: ArrayList<QuestionAnswerModel>, mContext: Context, mQuestionarieInstance: QuestionnariesActivity?) : PagerAdapter() {
    var mQuestionsArray = ArrayList<QuestionAnswerModel>()
    var mContext: Context? = null
    var mQuestionarieInstance: QuestionnariesActivity? = null

    init {
        this.mQuestionsArray = mQuestionsArray
        this.mContext = mContext
        this.mQuestionarieInstance = mQuestionarieInstance
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object` as View
    }

    override fun getCount() = mQuestionsArray.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container?.context).inflate(R.layout.item_questions, container, false)

        view.txtQuestionName.text = mQuestionsArray[position].question
        view.rvAnswers.layoutManager = LinearLayoutManager(mContext)
        view.rvAnswers.adapter = AnswerAdapter(mQuestionsArray[position], mContext!!,mQuestionarieInstance)

        if (!TextUtils.isEmpty(mQuestionsArray[position].answers)) {
            if (mQuestionarieInstance!!.mAnswerCount < mQuestionsArray.size)
                mQuestionarieInstance!!.mAnswerCount++
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        // Remove the view from view group specified position
        parent.removeView(`object` as View)
    }

}