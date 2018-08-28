package com.seeaspark


import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import database.Database
import helper.FirebaseListeners
import services.DayBroadcastReceiver
import services.ListenerService
import services.NightBroadCastReceiver
import services.ReceiverFunctions
import utils.Connection_Detector
import utils.Constants
import utils.CustomLoadingDialog
import utils.Utils
import java.util.*


abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {

    var mContext: Context? = null
    var mErrorInternet = "";
    var mErrorAPI = "";
    var mWidth: Int = 0
    var mHeight: Int = 0
    var mPlatformStatus: Int = 2
    var mUtils: Utils? = null;
    var db: Database? = null
    var mGson = Gson()
    var broadcaster: LocalBroadcastManager? = null
    var blackColor = 0
    var whiteColor = 0
    var darkGrey = 0
    var blackRipple = 0
    var whiteRipple = 0
    var lightGrey = 0
    var transperent = 0

    var mReceiverFunction: ReceiverFunctions? = null
    var currentCalendar: Calendar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        mContext = getContext()
        broadcaster = LocalBroadcastManager.getInstance(baseContext)
        mUtils = Utils(this)
        db = Database(mContext!!)
        currentCalendar = Calendar.getInstance()
        currentCalendar!!.timeInMillis = System.currentTimeMillis()

        mReceiverFunction = ReceiverFunctions()
        getDefaults()

        blackColor = ContextCompat.getColor(this, R.color.black_color)
        whiteColor = ContextCompat.getColor(this, R.color.white_color)
        darkGrey = ContextCompat.getColor(this, R.color.darkGreyText)
        lightGrey = ContextCompat.getColor(this, R.color.light_grey)
        transperent = ContextCompat.getColor(this, R.color.transperent)

        blackRipple = R.drawable.black_ripple
        whiteRipple = R.drawable.white_ripple
        initUI()

        if (mUtils!!.getInt("switchNightMode", 0) == 1) {
            mUtils!!.setInt("nightMode", 1)
            displayNightMode()
        } else {
            if (mUtils!!.getInt("autoNightMode", 0) == 1) {
                if (currentCalendar!!.get(Calendar.HOUR_OF_DAY) in 6..17) {
                    mUtils!!.setInt("nightMode", 0)
                    displayDayMode()
                } else {
                    mUtils!!.setInt("nightMode", 1)
                    displayNightMode()
                }
            }
        }

        onCreateStuff()
        initListener()
        mErrorInternet = getString(R.string.internet)
        mErrorAPI = getString(R.string.error_api)

    }

    abstract fun getContentView(): Int /// Initalize Activity Layout
    abstract fun initUI() /// Alter UI here
    abstract fun displayDayMode()
    abstract fun displayNightMode()
    abstract fun onCreateStuff() /// Initalize Variables here
    abstract fun initListener() /// Initalize Click Listener Here
    abstract fun getContext(): Context /// Initalize Activity Context

    fun connectedToInternet() = (Connection_Detector(mContext).isConnectingToInternet)

    fun showLoader() {
        CustomLoadingDialog.getLoader().showLoader(mContext)
    }

    fun dismissLoader() {
        CustomLoadingDialog.getLoader().dismissLoader()
    }

    fun showAlert(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showInternetAlert(view: View) {
        Snackbar.make(view, R.string.internet, Snackbar.LENGTH_SHORT).show()
    }


    fun getDefaults() {
        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)
        mWidth = display.widthPixels
        mHeight = display.heightPixels
        mUtils!!.setInt("width", mWidth)
        mUtils!!.setInt("height", mHeight)
    }

    fun moveToSplash() {
        FirebaseListeners.getListenerClass(this).RemoveAllListeners()
        FirebaseListeners.getListenerClass(this).clearApplicationData(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancelAll()
        }
        stopService(Intent(applicationContext, ListenerService::class.java))

        val notificationManager = mContext!!
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        mUtils!!.clear_shf()
        db!!.deleteAllTables()
        val inSplash = Intent(mContext, AfterWalkThroughActivity::class.java)
        inSplash.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        inSplash.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mContext!!.startActivity(inSplash)
//        System.exit(2)
    }

    fun alertLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("LOG OUT")
        alertDialog.setMessage("Are you sure you want to Log out?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            moveToSplash()
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    var nightModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getIntExtra("status", 0) == Constants.DAY) {
                displayDayMode()
            } else {
                displayNightMode()
            }
        }
    }

    override fun onResume() {
        currentCalendar = Calendar.getInstance()
        currentCalendar!!.timeInMillis = System.currentTimeMillis()
        super.onResume()
    }

    override fun onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(nightModeReceiver,
                IntentFilter(Constants.NIGHT_MODE))
        super.onStart()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(nightModeReceiver)
        super.onDestroy()
    }


}