package fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.activity_preferences.*
import kotlinx.android.synthetic.main.fragment_experience.*
import org.florescu.android.rangeseekbar.RangeSeekBar


class ExperienceFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_experience, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun onCreateStuff() {

        rsbYears.isNotifyWhileDragging = true
        rsbYears.selectedMaxValue = 2
        rsbYears.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            txtYearCount.text = "$maxValue"
            mCreateProfileInstance!!.mExpeirenceYears = "$maxValue"
        }

        rsbMonths.isNotifyWhileDragging = true
        rsbMonths.selectedMaxValue = 0
        rsbMonths.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
            txtMonthCount.text = "$maxValue"
            mCreateProfileInstance!!.mExpeirenceMonth = "$maxValue"
        }
    }

    private fun initListener() {
        txtNextExperience.setOnClickListener(this)
        imgBackExperience.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            txtNextExperience -> {
                mCreateProfileInstance!!.moveToNext()
            }
            imgBackExperience -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
        }
    }


}