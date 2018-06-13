package fragments

import adapters.ProfessionAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_age.*
import kotlinx.android.synthetic.main.fragment_profession.*

class ProfessionFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var mAdapterProfession: ProfessionAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_profession, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun onCreateStuff() {
        rvProfession.layoutManager = LinearLayoutManager(activity)
        mAdapterProfession = ProfessionAdapter(activity!!, mCreateProfileInstance!!.mProfessionArray, mCreateProfileInstance!!)
        rvProfession.adapter = mAdapterProfession
    }

    private fun initListener() {
        imgBackProfession.setOnClickListener(this)
        txtNextProfession.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            imgBackProfession -> {
                mCreateProfileInstance!!.moveToPrevious()
            }

            txtNextProfession -> {
                if (mCreateProfileInstance!!.mProfession == -1)
                    mCreateProfileInstance!!.showAlertActivity(txtNextProfession, getString(R.string.error_profession))
                else
                    mCreateProfileInstance!!.moveToNext()
            }
        }
    }
}