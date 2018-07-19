package services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class ReceiverFunctions {

    private var mAlarmManagerDay: AlarmManager? = null
    private var mPendingIntentDay: PendingIntent? = null

    private var mAlarmManagerNight: AlarmManager? = null
    private var mPendingIntentNight: PendingIntent? = null

    fun startAutoDayMode(mContext: Context) {
        val intent = Intent(mContext, DayBroadcastReceiver::class.java)
        mPendingIntentDay = PendingIntent.getBroadcast(
                mContext, 43, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val interval = 1000 * 60 * 60 * 24
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        mAlarmManagerDay = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManagerDay!!.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                interval.toLong(), mPendingIntentDay)
    }

    fun startAutoNightMode(mContext: Context) {
        val intent = Intent(mContext, NightBroadCastReceiver::class.java)
        mPendingIntentNight = PendingIntent.getBroadcast(
                mContext, 44, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val interval = 1000 * 60 * 60 * 24
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 18)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        mAlarmManagerNight = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManagerNight!!.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                interval.toLong(), mPendingIntentNight)
    }

    fun turnOffAutoNightMode() {
        if (mAlarmManagerNight != null) {
            mAlarmManagerNight!!.cancel(mPendingIntentNight)
        }
    }

    fun turnOffAutoDayMode() {
        if (mAlarmManagerDay != null) {
            mAlarmManagerDay!!.cancel(mPendingIntentDay)
        }
    }

}