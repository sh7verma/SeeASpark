package utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

class ConnectionDetector(context: Context) : BroadcastReceiver() {

    private var _context: Context? = null

    init {
        this._context=context
    }
    /**
     * Checking for all possible internet providers
     */
    fun isConnectingToInternet(): Boolean {

        val connectivity = _context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info.indices)
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }

        }
        return false
    }

    override fun onReceive(arg0: Context, arg1: Intent) {
        _context = arg0
        val check = isConnectingToInternet()
        Log.d("check", "is " + check)
    }
}