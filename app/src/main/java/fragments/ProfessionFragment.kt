package fragments

import adapters.ProfessionAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_profession.*
import models.ProfessionListingModel
import models.ProfessionModel
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.math.MathContext

class ProfessionFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null
    var mAdapterProfession: ProfessionAdapter? = null
    var mProfessionFragment: ProfessionFragment? = null
    var tempArray = ArrayList<ProfessionModel>()
    var isTypeable = false
    var makeVisisble = false

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

        tempArray.clear()
        tempArray.addAll(mCreateProfileInstance!!.mProfessionArray)
        populateData()

        edProfession.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searString = s.toString().toLowerCase()
                if (searString.trim().isEmpty()) {
                    llNoResultFoundProfessions.visibility = View.GONE
                    tempArray.clear()
                    tempArray.addAll(mCreateProfileInstance!!.mProfessionArray)
                    populateData()
                } else {
                    if (isTypeable)
                        hitAPI(searString)
                    else
                        isTypeable = true
                }
            }
        })
    }

    private fun populateData() {
        mAdapterProfession = ProfessionAdapter(activity!!, tempArray, mCreateProfileInstance!!, mProfessionFragment)
        rvProfession.adapter = mAdapterProfession
    }

    private fun hitAPI(searString: String) {
        val call = RetrofitClient.getInstance().searchProfessions(mCreateProfileInstance!!.mUtils!!.getString("access_token", ""), searString)
        call.enqueue(object : Callback<ProfessionListingModel> {
            override fun onResponse(call: Call<ProfessionListingModel>?, response: Response<ProfessionListingModel>) {
                if (response.body().response != null) {
                    if (response.body().response.size > 0) {
                        llNoResultFoundProfessions.visibility = View.GONE
                        tempArray.clear()
                        tempArray.addAll(response.body().response)
                    } else {
                        tempArray.clear()
                        llNoResultFoundProfessions.visibility = View.VISIBLE
                    }
                    populateData()
                }
            }

            override fun onFailure(call: Call<ProfessionListingModel>?, t: Throwable?) {

            }

        })
    }

    private fun localSearch(searString: String) {
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

    private fun initListener() {
        imgBackProfession.setOnClickListener(this)
        txtNextProfession.setOnClickListener(this)
        txtAddProfession.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            txtAddProfession -> {
                alertOccupationDialog()
                Constants.closeKeyboard(activity, imgBackProfession)
            }
            imgBackProfession -> {
                makeVisisble = false
                Constants.closeKeyboard(activity, imgBackProfession)
                mCreateProfileInstance!!.moveToPrevious()
            }
            txtNextProfession -> {
                if (mCreateProfileInstance!!.mProfessionName.isEmpty())
                    mCreateProfileInstance!!.showAlertActivity(txtNextProfession, getString(R.string.error_profession))
                else {
                    if (mCreateProfileInstance!!.mProfessionName == edProfession.text.toString().trim()) {
                        alertOccupationDialog()
                    } else {
                        alertDifferentOccupationDialog()
                    }
                }
            }
        }
    }

    fun setSearchText(mProfessionName: String?) {
        isTypeable = false
        edProfession.setText(mProfessionName)
        edProfession.setSelection(edProfession.text.trim().length)
        edProfession.isFocusableInTouchMode = true
        edProfession.requestFocus()
        Constants.closeKeyboard(activity, edProfession)
    }

    private fun alertOccupationDialog() {
        val alertDialog = AlertDialog.Builder(mCreateProfileInstance!!.mContext!!)
        alertDialog.setMessage("Are you sure you want to continue with occupation \"${edProfession.text.trim()}\"?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            makeVisisble = true
            mCreateProfileInstance!!.mProfessionName = edProfession.text.toString().trim()
            mCreateProfileInstance!!.moveToNext()

        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    private fun alertDifferentOccupationDialog() {
        val alertDialog = AlertDialog.Builder(mCreateProfileInstance!!.mContext!!)
        alertDialog.setMessage("Your selected occupation is \"${mCreateProfileInstance!!.mProfessionName}\". Do you want to continue?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            isTypeable = false
            makeVisisble = true
            edProfession.setText(mCreateProfileInstance!!.mProfessionName)
            edProfession.setSelection(edProfession.text.toString().length)
            mCreateProfileInstance!!.moveToNext()
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }


    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            if (makeVisisble) {
                llNoResultFoundProfessions.visibility = View.GONE
                var displayLocalData = false
                for (occupation in mCreateProfileInstance!!.mProfessionArray) {
                    if (occupation.name == mCreateProfileInstance!!.mProfessionName) {
                        displayLocalData = true
                        break
                    } else {
                        displayLocalData = false
                    }
                }
                if (displayLocalData) {
                    tempArray.clear()
                    tempArray.addAll(mCreateProfileInstance!!.mProfessionArray)
                    populateData()
                } else {
                    hitAPI(mCreateProfileInstance!!.mProfessionName)
                }
            }
        }
    }

}