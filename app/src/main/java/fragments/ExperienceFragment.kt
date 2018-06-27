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

        rsbYears.setNotifyWhileDragging(true)
        rsbYears.selectedMaxValue = 1
        rsbYears.setOnRangeSeekBarChangeListener(object : RangeSeekBar.OnRangeSeekBarChangeListener<Int> {
            override fun onRangeSeekBarValuesChanged(bar: RangeSeekBar<Int>?, minValue: Int?, maxValue: Int?) {
                txtYearCount.text = "$maxValue"
                mCreateProfileInstance!!.mExpeirenceYears="$maxValue"
            }
        })

        rsbMonths.setNotifyWhileDragging(true)
        rsbMonths.selectedMaxValue = 1
        rsbMonths.setOnRangeSeekBarChangeListener(object : RangeSeekBar.OnRangeSeekBarChangeListener<Int> {
            override fun onRangeSeekBarValuesChanged(bar: RangeSeekBar<Int>?, minValue: Int?, maxValue: Int?) {
                txtMonthCount.text = "$maxValue"
                mCreateProfileInstance!!.mExpeirenceMonth="$maxValue"
            }
        })
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