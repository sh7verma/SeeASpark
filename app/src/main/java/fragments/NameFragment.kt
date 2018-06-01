package fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_name.*


class NameFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null

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

    }

    override fun onClick(view: View?) {
        when (view) {
            txtNextName -> {
                if (edNameProfile.getText().toString().length < 2)
                    mCreateProfileInstance!!.showAlertActivity(txtNextName, getString(R.string.error_name))
                else {
                    mCreateProfileInstance!!.mName = edNameProfile.text.toString()
                    mCreateProfileInstance!!.moveToNext()
                }
            }
            imgBackName -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
        }
    }

}