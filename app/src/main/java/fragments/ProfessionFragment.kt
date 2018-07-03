package fragments

import adapters.ProfessionAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_language.*
import kotlinx.android.synthetic.main.fragment_profession.*
import models.ProfessionModel
import utils.Constants

class ProfessionFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var mAdapterProfession: ProfessionAdapter? = null
    var mProfessionFragment: ProfessionFragment? = null
    var tempArray = ArrayList<ProfessionModel>()

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
        mProfessionFragment = this
        edProfession.setText(mCreateProfileInstance!!.mProfessionName)
        rvProfession.layoutManager = LinearLayoutManager(activity)

        mAdapterProfession = ProfessionAdapter(activity!!, mCreateProfileInstance!!.mProfessionArray, mCreateProfileInstance!!, mProfessionFragment)
        rvProfession.adapter = mAdapterProfession

        edProfession.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searString = s.toString().toLowerCase()

                if (searString.trim().isEmpty()) {
                    tempArray.clear()
                    tempArray.addAll(mCreateProfileInstance!!.mProfessionArray)
                    mAdapterProfession = ProfessionAdapter(activity!!, tempArray, mCreateProfileInstance!!, mProfessionFragment)
                    rvProfession.adapter = mAdapterProfession
                } else {
                    tempArray.clear()
                    for (profession in mCreateProfileInstance!!.mProfessionArray) {
                        if ((profession.name).toLowerCase().contains(searString)) {
                            tempArray.add(profession);
                        }
                    }
                    mAdapterProfession = ProfessionAdapter(activity!!, tempArray, mCreateProfileInstance!!, mProfessionFragment)
                    rvProfession.adapter = mAdapterProfession
                }
            }
        })
    }

    private fun initListener() {
        imgBackProfession.setOnClickListener(this)
        txtNextProfession.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            imgBackProfession -> {
                Constants.closeKeyboard(activity,imgBackProfession)
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

    fun setSearchText(mProfessionName: String?) {
        edProfession.setText(mProfessionName)
        edProfession.setSelection(edProfession.text.trim().length)
        Constants.closeKeyboard(activity, edProfession)
    }
}