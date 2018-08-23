package fragments

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
import kotlinx.android.synthetic.main.fragment_bio.*
import kotlinx.android.synthetic.main.fragment_name.*
import network.RetrofitClient
import utils.Constants

class BioFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var makeVisisble = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_bio, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()

        super.onActivityCreated(savedInstanceState)
    }

    private fun initListener() {
        imgBackBio.setOnClickListener(this)
        txtNextBio.setOnClickListener(this)
    }

    private fun onCreateStuff() {
        edBioProfile.isFocusableInTouchMode = true
        edBioProfile.requestFocus()
        edBioProfile.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty())
                    txtCountCharacter.text = "${320 - p0!!.length} Characters Left"
                else
                    txtCountCharacter.text = "320 Characters Left"
            }
        })
    }

    override fun onClick(view: View) {
        when (view) {
            imgBackBio -> {
                Constants.closeKeyboard(activity!!, imgBackBio)
                makeVisisble=true
                mCreateProfileInstance!!.moveToPrevious()
            }
            txtNextBio -> {
                if (TextUtils.isEmpty(edBioProfile.text.toString().trim()))
                    mCreateProfileInstance!!.showAlertActivity(txtNextBio, getString(R.string.error_bio))
                else {
                    makeVisisble=true
                    mCreateProfileInstance!!.moveToNext()
                    mCreateProfileInstance!!.mBio = edBioProfile.text.toString().trim()
                }
            }
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            if (makeVisisble) {
                edBioProfile.isFocusable = true
                Constants.showKeyboard(activity!!, edBioProfile)
            }
        }
    }

}
