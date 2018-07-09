package com.seeaspark


import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import database.Database
import utils.Connection_Detector
import utils.Constants
import utils.CustomLoadingDialog
import utils.Utils


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
    var blackRipple = 0
    var whiteRipple = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        mContext = getContext()
        broadcaster = LocalBroadcastManager.getInstance(baseContext)
        mUtils = Utils(this)
        db=Database(mContext!!)
        getDefaults()

        blackColor = ContextCompat.getColor(this, R.color.black_color)
        whiteColor = ContextCompat.getColor(this, R.color.white_color)

        blackRipple = R.drawable.black_ripple
        whiteRipple = R.drawable.white_ripple
        initUI()
        if (mUtils!!.getInt("nightMode", 0) == 1)
            displayNightMode()
        else
            displayDayMode()

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


    protected fun getDefaults() {
        val display = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)
        mWidth = display.widthPixels
        mHeight = display.heightPixels
        mUtils!!.setInt("width", mWidth)
        mUtils!!.setInt("height", mHeight)
    }

    public fun moveToSplash() {
        val notificationManager = mContext!!
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        mUtils!!.clear_shf()
        val inSplash = Intent(mContext, AfterWalkThroughActivity::class.java)
        inSplash.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        inSplash.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mContext!!.startActivity(inSplash)
        System.exit(2)
    }

    public fun alertLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("LOG OUT")
        alertDialog.setMessage("Are you sure you want to Log out?")
        alertDialog.setPositiveButton("CONFIRM") { dialog, which ->
            moveToSplash()
        }
        alertDialog.setNegativeButton("CANCEL") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    internal var nightModeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getIntExtra("status", 0) == Constants.DAY) {
                displayDayMode()
            } else {
                displayNightMode()
            }
        }
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