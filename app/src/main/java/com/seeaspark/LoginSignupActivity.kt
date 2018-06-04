package com.seeaspark

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.linkedin.platform.APIHelper
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.ApiListener
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONException
import utils.Constants
import java.util.*


class LoginSignupActivity : BaseActivity() {

    private var swipeleft: TranslateAnimation? = null
    private var swiperight: TranslateAnimation? = null
    private var modeEnabledSignup = true
    var intialViewPosition: Int = 0
    private var callbackManager: CallbackManager? = null

    override fun initUI() {
        intialViewPosition = ((mWidth * 0.5 - mWidth / 12).toInt())
        val viewParms = LinearLayout.LayoutParams(Constants.dpToPx(24), Constants.dpToPx(2))
        viewParms.leftMargin = intialViewPosition
        viewLine.layoutParams = viewParms

        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")

        edEmail.typeface = typeface
        edPassword.typeface = typeface
    }

    override fun onCreateStuff() {

        callbackManager = CallbackManager.Factory.create();

        swipeleft = TranslateAnimation(-(intialViewPosition).toFloat(), 0f, 0f, 0f)
        swipeleft!!.duration = 300
        swipeleft!!.fillAfter = true

        swiperight = TranslateAnimation(0f, -(intialViewPosition).toFloat(), 0f, 0f)
        swiperight!!.duration = 300
        swiperight!!.fillAfter = true

        setRegister()

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { jsonObject, response ->
                    // Getting FB User Data
                    Log.e("First Name  ", jsonObject.getString("first_name"));
                    LoginManager.getInstance().logOut()

                    mUtils!!.setBoolean("addEmailFragment", true)
                    hitSignupAPI()
                }

                val parameters = Bundle()
                parameters.putString("fields", "id,first_name,last_name,email,gender")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
                Log.e("Result - ", exception.localizedMessage);
            }
        })

        edPassword.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                verifyDetails()
            }
            true
        })

    }

    override fun initListener() {
        imgBack.setOnClickListener(this)
        imgLinkedin.setOnClickListener(this)
        txtSignin.setOnClickListener(this)
        txtSignup.setOnClickListener(this)
        txtDone.setOnClickListener(this)
        txtForgotPassword.setOnClickListener(this)
    }

    override fun getContentView() = R.layout.activity_signup

    override fun getContext() = this

    override fun onClick(view: View) {
        when (view) {
            imgBack -> {
                moveBack()
            }
            txtDone -> {
                verifyDetails()
            }
            imgLinkedin -> {
                linkedinLogin(imgLinkedin)
            }
            txtSignin -> {
                if (modeEnabledSignup) {
                    viewLine.startAnimation(swiperight)
                    setLogin()
                    modeEnabledSignup = false;
                }
            }
            txtSignup -> {
                if (!modeEnabledSignup) {
                    viewLine.startAnimation(swipeleft)
                    setRegister()
                    modeEnabledSignup = true;
                }
            }
            txtForgotPassword -> {
                startActivity(Intent(this, ForgotPasswordActivity::class.java))
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }
        }
    }

    private fun setLogin() {
        edEmail.isFocusable = true
        edEmail.setText(Constants.EMPTY)
        edPassword.setText(Constants.EMPTY)
        txtForgotPassword.visibility = View.VISIBLE
        txtSignin.alpha = 1.0f
        txtSignup.alpha = 0.4f
    }

    private fun setRegister() {
        edEmail.setText(Constants.EMPTY)
        edPassword.setText(Constants.EMPTY)
        txtForgotPassword.visibility = View.INVISIBLE
        txtSignin.alpha = 0.4f
        txtSignup.alpha = 1.0f
    }

    override fun onBackPressed() {
        moveBack();
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slidedown_in, R.anim.slidedown_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun linkedinLogin(view: View) {
        LISessionManager.getInstance(applicationContext).clearSession()
        LISessionManager.getInstance(applicationContext).init(this, buildScope(), object : AuthListener {
            override fun onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                Log.d("landing", "Linkedin onAuthSuccess ")
                getprofiledata()
            }

            override fun onAuthError(error: LIAuthError) {
                // Handle authentication errors
                Log.d("landing", "Linkedin onAuthError " + error.toString())
            }
        }, true)
    }

    fun buildScope(): Scope {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    fun getprofiledata() {
        var url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,public-profile-url,picture-url,email-address,picture-urls::(original))";
        var apiHelper = APIHelper.getInstance(getApplicationContext())

        apiHelper.getRequest(this, url, object : ApiListener {
            override fun onApiSuccess(apiResponse: ApiResponse?) {
                var jsonObject = apiResponse!!.responseDataAsJson
                try {
                    val emailAddress = jsonObject.getString("emailAddress");
                    Log.e("Email Address = ", emailAddress)
                    Log.e("Response", "Linkedin apiResponse JSON " + apiResponse.getResponseDataAsString());
                    mUtils!!.setBoolean("addEmailFragment", true)
                    hitSignupAPI()
                } catch (exception: JSONException) {

                }
            }

            override fun onApiError(LIApiError: LIApiError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun verifyDetails() {
        if (edEmail.getText().toString().trim({ it <= ' ' }).isEmpty())
            showAlert(txtDone, resources.getString(R.string.enter_email))
        else if (!validateEmail(edEmail.getText()))
            showAlert(txtDone, resources.getString(R.string.enter_valid_email))
        else if (edPassword.getText().toString().trim({ it <= ' ' }).isEmpty())
            showAlert(txtDone, resources.getString(R.string.enter_password))
        else if (edPassword.getText().toString().trim({ it <= ' ' }).length < 8)
            showAlert(txtDone, resources.getString(R.string.error_password))
        else {
            if (connectedToInternet()) {
                Constants.closeKeyboard(this, txtDone)
                if (modeEnabledSignup) {
                    mUtils!!.setBoolean("addEmailFragment", false)
                    hitSignupAPI()
                } else
                    hitLoginAPI()
            } else
                showInternetAlert(txtDone)
        }
    }

    private fun hitLoginAPI() {
        mUtils!!.setString("access_token", "123")
        var intent = Intent(mContext, LandingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun hitSignupAPI() {
        var intent = Intent(mContext, CreateProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    internal fun validateEmail(text: CharSequence): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val matcher = pattern.matcher(text)
        return matcher.matches()
    }

}