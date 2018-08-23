package adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.AddSkillsActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.item_skill_selection.view.*
import models.SkillsModel

class SkillSuggestionAdapter(mSkillsArray: ArrayList<SkillsModel>, mContext: Context,
                             mAddSkillsActivity: AddSkillsActivity?) : RecyclerView.Adapter<SkillSuggestionAdapter.ViewHolder>() {

    var mSkillsArray = ArrayList<SkillsModel>()
    var mContext: Context? = null
    var mAddSkillsActivity: AddSkillsActivity? = null

    init {
        this.mSkillsArray = mSkillsArray
        this.mContext = mContext
        this.mAddSkillsActivity = mAddSkillsActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vhItem: ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_skill_selection, parent, false)
        vhItem = ViewHolder(v)
        return vhItem
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtSkillContent.text = mSkillsArray[position].name
        holder.txtSkillContent.setOnClickListener {
            mAddSkillsActivity!!.selectSkill(mSkillsArray[position].name)
        }
    }

    override fun getItemCount(): Int {
        return mSkillsArray.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtSkillContent = itemView.txtSkillContent
    }
}