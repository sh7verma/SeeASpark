package services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import utils.Constants
import utils.Utils
import java.util.*

class TimeChangedReceiver : BroadcastReceiver() {
    var broadcaster: LocalBroadcastManager? = null
    var mReceiverFunctions: ReceiverFunctions? = null

    override fun onReceive(context: Context?, p1: Intent?) {
        Log.e("Time Changed = ", "Yes")

        val mUtils = Utils(context)
        if (mUtils.getInt("autoNightMode", 0) == 1) {
            broadcaster = LocalBroadcastManager.getInstance(context!!)

            val currentCalendar = Calendar.getInstance()
            currentCalendar.timeInMillis = System.currentTimeMillis()

            mReceiverFunctions = ReceiverFunctions()
            mReceiverFunctions!!.turnOffAutoNightMode()
            mReceiverFunctions!!.turnOffAutoDayMode()

            if (currentCalendar.get(Calendar.HOUR_OF_DAY) in 6..17) {
                Log.e("Set Day Mode = ", "Yes")
                if (mUtils.getInt("nightMode", 0) == 1) {

                    mReceiverFunctions!!.startAutoDayMode(context)

                    mUtils.setInt("nightMode", 0)
                    val broadCastIntent = Intent(Constants.NIGHT_MODE)
                    broadCastIntent.putExtra("status", Constants.DAY)
                    broadcaster!!.sendBroadcast(broadCastIntent)
                }
            } else {
                Log.e("Set Night Mode = ", "Yes")
                if (mUtils.getInt("nightMode", 0) == 0) {

                    mReceiverFunctions!!.startAutoNightMode(context)

                    mUtils.setInt("nightMode", 1)
                    val broadCastIntent = Intent(Constants.NIGHT_MODE)
                    broadCastIntent.putExtra("status", Constants.NIGHT)
                    broadcaster!!.sendBroadcast(broadCastIntent)
                }
            }
        }
    }
}