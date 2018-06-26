package fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.AddSkillsActivity
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import customviews.FlowLayout
import kotlinx.android.synthetic.main.fragment_skills.*
import kotlinx.android.synthetic.main.layout_skills.view.*
import models.SkillsModel
import utils.Constants

class SkillsFragment : Fragment(), View.OnClickListener {

    private var mCreateProfileInstance: CreateProfileActivity? = null
    private var itemView: View? = null

    private var mSkillsSelectedArray = ArrayList<String>()
    private var mOwnSkillsArray = ArrayList<SkillsModel>()
    private val ADD_SKILLS: Int = 1
    private var mContext: Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_skills, container, false)
        mCreateProfileInstance = activity as CreateProfileActivity
        mContext = activity

        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun onCreateStuff() {
        for (skillValue: SkillsModel in mCreateProfileInstance!!.mSkillsArray) {
            flSkills.addView(inflateView(skillValue))
        }
    }

    private fun initListener() {
        txtNextSkills.setOnClickListener(this)
        imgBackSkills.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            txtNextSkills -> {
                if (mSkillsSelectedArray.size == 0)
                    mCreateProfileInstance!!.showAlertActivity(txtNextSkills, getString(R.string.error_skills))
                else {
                    mCreateProfileInstance!!.mSkillsServerArray.clear()
                    mCreateProfileInstance!!.mSkillsServerArray.addAll(mSkillsSelectedArray)
                    mCreateProfileInstance!!.moveToNext()
                    Constants.showKeyboard(activity!!, txtNextSkills)
                }
            }
            imgBackSkills -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
        }
    }

    private fun inflateView(skillValue: SkillsModel): View {
        val interestChip = LayoutInflater.from(activity).inflate(R.layout.layout_skills, null, false)

        val innerParms = FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Constants.dpToPx(52))
        interestChip.llMainSkills.layoutParams = innerParms

        if (!skillValue.isFirstElement) {
            interestChip.imgSkillAdd.visibility = View.GONE
            interestChip.txtSkillChip.visibility = View.VISIBLE
        } else {
            interestChip.imgSkillAdd.visibility = View.VISIBLE
            interestChip.txtSkillChip.visibility = View.GONE
        }

        if (mSkillsSelectedArray.contains(skillValue.name)) {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
        } else {
            interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
            interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
        }

        interestChip.txtSkillChip.text = skillValue.name

        interestChip.imgSkillAdd.setOnClickListener {
            val intent = Intent(activity, AddSkillsActivity::class.java)
            intent.putParcelableArrayListExtra("skillsArray", mOwnSkillsArray)
            intent.putParcelableArrayListExtra("allSkillsArray", mCreateProfileInstance!!.mSkillsArray)
            startActivityForResult(intent, ADD_SKILLS)
            activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        interestChip.txtSkillChip.setOnClickListener {
            if (mSkillsSelectedArray.contains(skillValue.name)) {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.default_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.black_color))
                mSkillsSelectedArray.remove(skillValue.name)
            } else {
                interestChip.txtSkillChip.setBackgroundResource(R.drawable.selected_skills)
                interestChip.txtSkillChip.setTextColor(ContextCompat.getColor(mContext!!, R.color.white_color))
                mSkillsSelectedArray.add(skillValue.name)
            }
            Log.e("Add/Remove = ", skillValue.name)
        }

        return interestChip
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_SKILLS -> {
                    flSkills.removeAllViews()
                    mOwnSkillsArray.clear()
                    mOwnSkillsArray.addAll(data!!.getParcelableArrayListExtra<Parcelable>("skillsArray") as ArrayList<out SkillsModel>)

                    for (skillValue: SkillsModel in mOwnSkillsArray) {
                        if (!mCreateProfileInstance!!.mSkillsArrayText.contains(skillValue.name)) {
                            mCreateProfileInstance!!.mSkillsArray.add(1, skillValue)
                            mCreateProfileInstance!!.mSkillsArrayText.add(skillValue.name)
                        }

                        if (!mSkillsSelectedArray.contains(skillValue.name))
                            mSkillsSelectedArray.add(skillValue.name)
                    }

                    for (skillValue: SkillsModel in mCreateProfileInstance!!.mSkillsArray) {
                        flSkills.addView(inflateView(skillValue))
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}