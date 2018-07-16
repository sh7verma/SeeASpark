package services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import kotlin.math.log

class DayBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
//        Toast.makeText(context, "Night Mode Start",
//                Toast.LENGTH_LONG).show();
        Log.e("Day Mode Start","Yes")
    }
}