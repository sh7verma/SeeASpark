package fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.seeaspark.CreateProfileActivity
import com.seeaspark.R
import kotlinx.android.synthetic.main.fragment_age.*
import utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AgeFragment : Fragment(), View.OnClickListener {

    var mCreateProfileInstance: CreateProfileActivity? = null
    var itemView: View? = null

    var mShowStartDate = SimpleDateFormat("dd MMM, yyyy", Locale.US)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemView = inflater.inflate(R.layout.fragment_age, container, false)
        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCreateProfileInstance = activity as CreateProfileActivity
        onCreateStuff()
        initListener()
        super.onActivityCreated(savedInstanceState)
    }

    private fun onCreateStuff() {
        txtSelectAge.text = mCreateProfileInstance!!.mAge

    }

    private fun initListener() {
        txtNextAge.setOnClickListener(this)
        imgBackAge.setOnClickListener(this)
        txtSelectAge.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            txtNextAge -> {
                if (TextUtils.isEmpty(txtSelectAge.text.toString()))
                    mCreateProfileInstance!!.showAlertActivity(txtNextAge, getString(R.string.error_age))
                else {
                    mCreateProfileInstance!!.moveToNext()
                }
            }
            imgBackAge -> {
                mCreateProfileInstance!!.moveToPrevious()
            }
            txtSelectAge -> {
                setBday()
            }
        }
    }

    fun setBday() {
        Constants.closeKeyboard(activity, txtSelectAge)
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.add(Calendar.YEAR, -18)
        val datePickerDOB = DatePickerDialog(activity,
                R.style.DatePickerTheme,
                dobPickerListener,
                mCreateProfileInstance!!.calDOB!!.get(Calendar.YEAR),
                mCreateProfileInstance!!.calDOB!!.get(Calendar.MONTH),
                mCreateProfileInstance!!.calDOB!!.get(Calendar.DAY_OF_MONTH))

        datePickerDOB.setCancelable(false)
        datePickerDOB.datePicker.maxDate = calendar.timeInMillis
        datePickerDOB.setTitle("")
        datePickerDOB.show()
    }

    private val dobPickerListener = object : DatePickerDialog.OnDateSetListener {

        override fun onDateSet(view: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
            mCreateProfileInstance!!.calDOB!!.set(Calendar.YEAR, selectedYear)
            mCreateProfileInstance!!.calDOB!!.set(Calendar.MONTH, selectedMonth)
            mCreateProfileInstance!!.calDOB!!.set(Calendar.DATE, selectedDay)
            try {
                calculate_age(mShowStartDate.format(mCreateProfileInstance!!.calDOB!!.time))
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(ParseException::class)
    fun calculate_age(birthdate: String): Int {
        val birth_format = SimpleDateFormat("dd MMMM, yyyy", Locale.US)
        val birth_date = birth_format.parse(birthdate)
        val cal = Calendar.getInstance()
        val today = cal.time
        val diff = today.time - birth_date.time
        val diffSeconds = diff / 1000 % 60
        val diffMinutes = diff / (60 * 1000) % 60
        val diffHours = diff / (60 * 60 * 1000) % 24
        val diffDays = diff / (24 * 60 * 60 * 1000)
        val diffyears = (diffDays / 365).toInt()
        txtSelectAge.text = "$diffyears years"
        mCreateProfileInstance!!.mAge = txtSelectAge.text.toString()
        return diffyears
    }
}