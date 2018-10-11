package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_answer_detail.view.*

class AnswerDetailAdapter(mAnswersArray: Array<String>,
                          mContext: Context)
    : RecyclerView.Adapter<AnswerDetailAdapter.ViewHolder>() {

    var mAnswersArray = arrayOf<String>()
    var mContext: Context? = null

    init {
        this.mAnswersArray = mAnswersArray
        this.mContext = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_answer_detail,
                parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtAnswer.text = mAnswersArray[position]
    }

    override fun getItemCount(): Int {
        return mAnswersArray.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtAnswer = itemView.txtAnswer
    }
}