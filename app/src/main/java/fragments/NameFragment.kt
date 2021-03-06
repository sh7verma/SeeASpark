package fragments

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_name.*
import utils.Constants


class NameFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var makeVisisble = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_name, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initListener() {
        txtNextName.setOnClickListener(this)
        imgBackName.setOnClickListener(this)
    }

    private fun onCreateStuff() {

        if (mCreateProfileInstance!!.userData!!.response.user_type == Constants.MENTOR)
            txtUserType.text = getString(R.string.mentor)
        else
            txtUserType.text = getString(R.string.mentee)

        val typeface = Typeface.createFromAsset(activity!!.assets, "fonts/medium.otf")
        edNameProfile.typeface = typeface
        edNameProfile.setText(mCreateProfileInstance!!.mName)
        edNameProfile.setSelection(edNameProfile.text.toString().length)
    }

    override fun onClick(view: View?) {
        when (view) {
            txtNextName -> {
                if (edNameProfile.getText().trim().toString().length < 2) {
                    mCreateProfileInstance!!.showAlertActivity(txtNextName, getString(R.string.error_name))
                } else {
                    makeVisisble = true
                    Constants.closeKeyboard(activity!!, txtNextName)
                    mCreateProfileInstance!!.mName = edNameProfile.text.toString()
                    mCreateProfileInstance!!.moveToNext()
                }
            }
            imgBackName -> {
                Constants.closeKeyboard(activity!!, txtNextName)
                mCreateProfileInstance!!.moveToPrevious()
            }
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            if (makeVisisble) {
                edNameProfile.requestFocus()
                Constants.showKeyboard(activity!!, txtNextName)
            }
        }
    }


}