package adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.QuestionnariesActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_answers.view.*
import models.QuestionAnswerModel
import models.SelectedQuestionAnswerModel
import utils.Constants


class AnswerAdapter(mAnswersArray: QuestionAnswerModel, mContext: Context, mQuestionarieInstance: QuestionnariesActivity?)
    : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {

    var mAnswersArray = ArrayList<String>()
    var mAnswersModel: QuestionAnswerModel? = null
    var mContext: Context? = null
    var mQuestionarieInstance: QuestionnariesActivity? = null

    private var broadcaster: LocalBroadcastManager? = null

    init {
        this.mAnswersArray = mAnswersArray.options.split(",") as ArrayList<String>
        this.mContext = mContext
        mAnswersModel = mAnswersArray
        this.mQuestionarieInstance = mQuestionarieInstance
        broadcaster = LocalBroadcastManager.getInstance(mContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_answers, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtAnswerQuestion.setOnClickListener {
            holder.txtAnswerQuestion.setBackgroundResource(R.drawable.answer_selected)
            holder.txtAnswerQuestion.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            mAnswersModel!!.answers = mAnswersArray[position]
            val questionIntent = Intent(Constants.QUESTIONS)
            questionIntent.putExtra("questionId", mAnswersModel!!.id)
            questionIntent.putExtra("answer", mAnswersModel!!.answers)
            broadcaster!!.sendBroadcast(questionIntent)
            notifyDataSetChanged()
        }

        if (mAnswersModel!!.answers.equals(mAnswersArray[position])) {
            holder.txtAnswerQuestion.setBackgroundResource(R.drawable.answer_selected)
            holder.txtAnswerQuestion.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        } else {
            holder.txtAnswerQuestion.setBackgroundResource(R.drawable.new_answer_background)
            holder.txtAnswerQuestion.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        }

        holder.txtAnswerQuestion.text = mAnswersArray[position]
    }

    override fun getItemCount(): Int {
        return mAnswersArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtAnswerQuestion = itemView.txtAnswerQuestion
    }
}