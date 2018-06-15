package com.seeaspark


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import utils.Connection_Detector
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
    var mGson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        mContext = getContext()
        mUtils = Utils(this)
        getDefaults()
        initUI()
        onCreateStuff()
        initListener()
        mErrorInternet = getString(R.string.internet)
        mErrorAPI = getString(R.string.error_api)

    }

    abstract fun initUI() /// Alter UI here
    abstract fun onCreateStuff() /// Initalize Variables here
    abstract fun initListener() /// Initalize Click Listener Here
    abstract fun getContentView(): Int /// Initalize Activity Layout
    abstract fun getContext(): Context /// Initalize Activity Context

    fun connectedToInternet() = (Connection_Detector(mContext).isConnectingToInternet())

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

    protected fun moveToSplash() {
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
}