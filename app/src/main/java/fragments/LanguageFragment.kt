package fragments

import adapters.LanguageAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_language.*
import java.util.*

class LanguageFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var mAdapterLangugae: LanguageAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_language, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun onCreateStuff() {
        rvLanguage.layoutManager = LinearLayoutManager(activity)
        mAdapterLangugae = LanguageAdapter(activity!!, mCreateProfileInstance!!.mLanguageArray)
        rvLanguage.adapter = mAdapterLangugae
    }

    private fun initListener() {
        imgBackLanguage.setOnClickListener(this)
        txtNextLanguage.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            imgBackLanguage -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
            txtNextLanguage -> {
                for (languageValue in mCreateProfileInstance!!.mLanguageArray) {
                    if (languageValue.isSelected)
                        mCreateProfileInstance!!.mSelectedLanguageArray.add(languageValue.id)
                }

                if (mCreateProfileInstance!!.mSelectedLanguageArray.size == 0)
                    mCreateProfileInstance!!.showAlertActivity(txtNextLanguage, getString(R.string.error_Language))
                else
                    mCreateProfileInstance!!.moveToNext()
            }
        }
    }

}