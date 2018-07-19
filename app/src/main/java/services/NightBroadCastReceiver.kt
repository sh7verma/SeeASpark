package services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import utils.Constants
import utils.Utils

class NightBroadCastReceiver : BroadcastReceiver() {

    var broadcaster: LocalBroadcastManager? = null
    var mReceiverFunctions: ReceiverFunctions? = null

    override fun onReceive(context: Context?, p1: Intent?) {
        Log.e("Night Mode Start", "Yes")

        val mUtils = Utils(context)
        mReceiverFunctions = ReceiverFunctions()

        if (mUtils.getInt("nightMode", 0) == 0 && mUtils.getInt("autoNightMode", 0) == 1) {

            mReceiverFunctions!!.turnOffAutoNightMode()
            mReceiverFunctions!!.startAutoDayMode(context!!)

            broadcaster = LocalBroadcastManager.getInstance(context)

            mUtils.setInt("nightMode", 1)
            val broadCastIntent = Intent(Constants.NIGHT_MODE)
            broadCastIntent.putExtra("status", Constants.NIGHT)
            broadcaster!!.sendBroadcast(broadCastIntent)
        }

    }
}