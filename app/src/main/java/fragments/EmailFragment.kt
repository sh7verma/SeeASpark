package fragments

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_email.*
import utils.Constants


class EmailFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_email, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun initListener() {
        txtNextEmail.setOnClickListener(this)
        imgBackName.setOnClickListener(this)
    }

    private fun onCreateStuff() {
        if (mCreateProfileInstance!!.userData!!.response.user_type == Constants.MENTOR)
            txtUserType.text = getString(R.string.mentor)
        else
            txtUserType.text = getString(R.string.mentee)
        val typeface = Typeface.createFromAsset(activity!!.assets, "fonts/medium.otf")
        edEmailProfile.setTypeface(typeface)
    }

    override fun onClick(view: View?) {
        when (view) {
            txtNextEmail -> {
                if (edEmailProfile.getText().toString().trim({ it <= ' ' }).isEmpty())
                    mCreateProfileInstance!!.showAlertActivity(txtNextEmail, resources.getString(R.string.enter_email))
                else if (!validateEmail(edEmailProfile.getText()))
                    mCreateProfileInstance!!.showAlertActivity(txtNextEmail, resources.getString(R.string.enter_valid_email))
                else if (edEmailProfile.getText().toString().trim().startsWith("."))
                    mCreateProfileInstance!!.showAlertActivity(txtNextEmail, resources.getString(R.string.enter_valid_email))
                else {
                    mCreateProfileInstance!!.verifyEmail(edEmailProfile.text.toString().trim())
                }
            }
            imgBackName -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
        }
    }

    internal fun validateEmail(text: CharSequence): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val matcher = pattern.matcher(text)
        return matcher.matches()
    }

}