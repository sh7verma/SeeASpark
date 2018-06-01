package adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_questions.view.*
import models.QuestionModel


class QuestionAdapter(mQuestionsArray: ArrayList<QuestionModel>, mContext: Context) : PagerAdapter() {
    var mQuestionsArray = ArrayList<QuestionModel>()
    var mContext: Context? = null

    init {
        this.mQuestionsArray = mQuestionsArray
        this.mContext = mContext
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object` as View
    }

    override fun getCount() = 5

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container?.context).inflate(R.layout.item_questions, container, false)

        view.rvAnswers.layoutManager = LinearLayoutManager(mContext)
        view.rvAnswers.adapter = AnswerAdapter(ArrayList(), mContext!!)

        container.addView(view)
        return view
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        // Remove the view from view group specified position
        parent.removeView(`object` as View)
    }

}