package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import kotlinx.android.synthetic.main.indicator_item.view.*
import models.QuestionAnswerModel

class IndicatorAdapter(mQuestionsArray: ArrayList<QuestionAnswerModel>, mContext: Context, selectedPos: Int) :
        RecyclerView.Adapter<IndicatorAdapter.MyViewHolder>() {

    private val mContext: Context = mContext
    private val mQuestionsArray: ArrayList<QuestionAnswerModel> = mQuestionsArray
    private var selectedPos = selectedPos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.indicator_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (position == selectedPos) {
            holder.imgIndicator.setImageResource(R.mipmap.ic_dot_b)
        } else {
            if (mQuestionsArray[position].getuserAnswers().isNotEmpty()) {
                holder.imgIndicator.setImageResource(R.mipmap.ic_dot_g)
            } else {
                holder.imgIndicator.setImageResource(R.mipmap.ic_dot_grey)
            }
        }
    }

    override fun getItemCount(): Int {
        return mQuestionsArray.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIndicator = itemView.imgIndicator
    }

    fun setSelectedPos(pos: Int) {
        selectedPos = pos
        notifyDataSetChanged()
    }
}