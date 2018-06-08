package fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import com.seeaspark.VerifyIdActivity
import kotlinx.android.synthetic.main.fragment_bio.*
import kotlinx.android.synthetic.main.fragment_description.*
import kotlinx.android.synthetic.main.fragment_name.*
import kotlinx.android.synthetic.main.fragment_profession.*
import utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class DescribeProfessionFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var mDOB = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    var itemView: View? = null
    var makeVisisble = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_description, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initListener() {
        imgBackDescription.setOnClickListener(this)
        txtNextDescription.setOnClickListener(this)
    }

    private fun onCreateStuff() {

        edDescriptionProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length > 0)
                    txtCountCharacterDescription.text = "${1024 - p0!!.length} Characters Left"
                else
                    txtCountCharacterDescription.text = "1024 Characters Left"
            }
        })
    }

    override fun onClick(view: View) {
        when (view) {
            imgBackDescription -> {
                makeVisisble = true
                mCreateProfileInstance!!.moveToPrevious()
            }
            txtNextDescription -> {
                if (TextUtils.isEmpty(edDescriptionProfile.text.toString().trim()))
                    mCreateProfileInstance!!.showAlertActivity(txtNextDescription, "Please specify Profession")
                else {
                    makeVisisble = true
                    Constants.closeKeyboard(activity!!, txtNextDescription)
                    moveToVerifyID();
                }
            }
        }
    }

    private fun moveToVerifyID() {
        mCreateProfileInstance!!.mDescription = edDescriptionProfile.text.toString().trim()
        val intent = Intent(activity, VerifyIdActivity::class.java)
        intent.putExtra("userData", mCreateProfileInstance!!.userData)
        intent.putExtra("avatarId", mCreateProfileInstance!!.mAvatarId)
        intent.putExtra("name", mCreateProfileInstance!!.mName)
        intent.putExtra("dob", mDOB.format(mCreateProfileInstance!!.calDOB!!.time))
        intent.putExtra("gender", mCreateProfileInstance!!.mGender.toString())
        intent.putExtra("profession", mCreateProfileInstance!!.mProfession.toString())
        intent.putIntegerArrayListExtra("languages", mCreateProfileInstance!!.mSelectedLanguageArray)
        intent.putExtra("experience", "${mCreateProfileInstance!!.mExpeirenceYears},${mCreateProfileInstance!!.mExpeirenceMonth}")
        intent.putStringArrayListExtra("skills", mCreateProfileInstance!!.mSkillsServerArray)
        intent.putExtra("bio", mCreateProfileInstance!!.mBio)
        intent.putExtra("description", mCreateProfileInstance!!.mDescription)
        startActivity(intent)
        activity!!.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            if (makeVisisble) {
                edDescriptionProfile.isFocusable = true
                Constants.showKeyboard(activity!!, edDescriptionProfile)
            }
        }
    }

}