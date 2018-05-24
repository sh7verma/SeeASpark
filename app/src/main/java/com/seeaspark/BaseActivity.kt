package com.seeaspark


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import utils.Connection_Detector
import utils.CustomLoadingDialog


abstract class BaseActivity : AppCompatActivity() {

    var mContext: Context? = null
    var mErrorInternet = "";
    var mErrorAPI = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        mContext = getContext()
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

    protected fun showInternetAlert(view: View) {
        Snackbar.make(view, R.string.internet, Snackbar.LENGTH_SHORT).show()
    }


}