package services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.seeaspark.BaseActivity
import utils.Constants
import utils.Utils
import kotlin.math.log

class DayBroadcastReceiver : BroadcastReceiver() {

    var broadcaster: LocalBroadcastManager? = null
    var mReceiverFunctions: ReceiverFunctions? = null
    override fun onReceive(context: Context?, p1: Intent?) {
        Log.e("Day Mode Start", "Yes")
        val mUtils = Utils(context)
        mReceiverFunctions = ReceiverFunctions()
        if (mUtils.getInt("nightMode", 0) == 1&&mUtils.getInt("autoNightMode", 0) == 1) {
            mReceiverFunctions!!.turnOffAutoDayMode()
            mReceiverFunctions!!.startAutoNightMode(context!!)

            broadcaster = LocalBroadcastManager.getInstance(context!!)
            mUtils.setInt("nightMode", 0)
            val broadCastIntent = Intent(Constants.NIGHT_MODE)
            broadCastIntent.putExtra("status", Constants.DAY)
            broadcaster!!.sendBroadcast(broadCastIntent)
        }
    }
}