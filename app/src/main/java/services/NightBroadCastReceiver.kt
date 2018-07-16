package services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NightBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
//        Toast.makeText(context, "Night Mode Start",
//                Toast.LENGTH_LONG).show();
        Log.e("Night Mode Start","Yes")
    }
}