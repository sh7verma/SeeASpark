package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cocosw.bottomsheet.BottomSheet
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_gender.*
import utils.Constants


class GenderFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_gender, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun onCreateStuff() {
        txtSelectGender.text = mCreateProfileInstance!!.mGenderText
    }

    private fun initListener() {
        imgBackGender.setOnClickListener(this)
        txtSelectGender.setOnClickListener(this)
        txtNextGender.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            imgBackGender -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
            txtSelectGender -> {
                optionGender()
            }
            txtNextGender -> {
                if (TextUtils.isEmpty(txtSelectGender.text.toString()))
                    mCreateProfileInstance!!.showAlertActivity(txtNextGender, getString(R.string.error_gender))
                else {
                    mCreateProfileInstance!!.moveToNext()
                }
            }
        }
    }

    private fun optionGender() {
        BottomSheet.Builder(activity, R.style.BottomSheet_Dialog)
                .title(getString(R.string.select_gender))
                .sheet(R.menu.menu_gender).listener { dialog, which ->
            when (which) {
                R.id.item_male -> {
                    txtSelectGender.setText(R.string.male)
                    mCreateProfileInstance!!.mGenderText = txtSelectGender.text.toString()
                    mCreateProfileInstance!!.mGender = Constants.MALE
                }
                R.id.item_female -> {
                    txtSelectGender.setText(R.string.female)
                    mCreateProfileInstance!!.mGenderText = txtSelectGender.text.toString()
                    mCreateProfileInstance!!.mGender = Constants.FEMALE
                }
                R.id.item_other -> {
                    txtSelectGender.setText(R.string.other)
                    mCreateProfileInstance!!.mGenderText = txtSelectGender.text.toString()
                    mCreateProfileInstance!!.mGender = Constants.OTHER
                }
            }
        }.show()
    }


}