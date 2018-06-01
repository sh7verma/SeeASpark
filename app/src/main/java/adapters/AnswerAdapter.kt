package adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_answers.view.*


class AnswerAdapter(mAnswersArray: ArrayList<String>, mContext: Context) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {

    var mAnswersArray = ArrayList<String>()
    var mContext: Context? = null

    init {
        this.mAnswersArray = mAnswersArray
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_answers, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.txtAnswerQuestion.setOnClickListener {
            if (holder.txtAnswerQuestion.currentTextColor == ContextCompat.getColor(mContext!!, R.color.black_color)) {
                holder.txtAnswerQuestion.setBackgroundResource(R.drawable.answer_background)
                holder.txtAnswerQuestion.setTextColor(ContextCompat.getColor(mContext!!, R.color.hint_color_light))
            } else {
                holder.txtAnswerQuestion.setBackgroundResource(R.drawable.answer_selected)
                holder.txtAnswerQuestion.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
            }
        }
        if (position == 0)
            holder.txtAnswerQuestion.text = "Evaluation"
        else if (position == 1)
            holder.txtAnswerQuestion.text = "Analysis"
        else if (position == 2)
            holder.txtAnswerQuestion.text = "Segmentation"
        else if (position == 3)
            holder.txtAnswerQuestion.text = "Good Planer"
    }

    override fun getItemCount(): Int {
        return 4
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtAnswerQuestion = itemView.txtAnswerQuestion
    }
}