package com.seeaspark

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
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
import com.google.firebase.database.FirebaseDatabase
import com.linkedin.platform.APIHelper
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.ApiListener
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import kotlinx.android.synthetic.main.activity_signup.*
import models.ProfileModel
import models.SignupModel
import network.RetrofitClient
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.Constants
import java.util.*


class LoginSignupActivity : BaseActivity() {

    private val VERIFY: Int = 1

    private var swipeleft: TranslateAnimation? = null
    private var swiperight: TranslateAnimation? = null
    private var modeEnabledSignup = true
    var intialViewPosition: Int = 0
    private var callbackManager: CallbackManager? = null

    var mFacebookId = Constants.EMPTY
    var mLinkedinId = Constants.EMPTY
    var mEmail = Constants.EMPTY
    var mPassword = Constants.EMPTY
    var mName = Constants.EMPTY
    var mAccountType: Int = 0
    var mEmailVerified: Int = 0
    var mUserType: Int = 0


    override fun getContentView() = R.layout.activity_signup

    override fun initUI() {
        intialViewPosition = ((mWidth * 0.5 - mWidth / 12).toInt())
        val viewParms = LinearLayout.LayoutParams(Constants.dpToPx(24), Constants.dpToPx(2))
        viewParms.leftMargin = intialViewPosition
        viewLine.layoutParams = viewParms

        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")

        edEmail.typeface = typeface
        edPassword.typeface = typeface
    }

    override fun displayDayMode() {

    }

    override fun displayNightMode() {

    }

    override fun onCreateStuff() {

        mUserType = intent.getIntExtra("userType", 0)

        if (mUserType == 1)
            txtUserMode.text = getString(R.string.mentee)
        else
            txtUserMode.text = getString(R.string.mentor)


        callbackManager = CallbackManager.Factory.create();

        swipeleft = TranslateAnimation(-(intialViewPosition).toFloat(), 0f, 0f, 0f)
        swipeleft!!.duration = 300
        swipeleft!!.fillAfter = true

        swiperight = TranslateAnimation(0f, -(intialViewPosition).toFloat(), 0f, 0f)
        swiperight!!.duration = 300
        swiperight!!.fillAfter = true

        if (intent.hasExtra("path"))
            setLogin()
        else
            setRegister()

        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // App code
                val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                ) { jsonObject, response ->
                    // Getting FB User Data

                    if (jsonObject != null) {
                        Log.e("Fb data = ", jsonObject.toString())
                        if (!TextUtils.isEmpty(jsonObject.getString("email"))) {
                            mEmail = jsonObject.getString("email")
                            mEmailVerified = Constants.EMAIL_VERIFIED
                        } else {
                            mEmailVerified = Constants.EMAIL_NOTVERIFIED
                        }
                        mName = jsonObject.getString("first_name") + " " + jsonObject.getString("last_name")
                        mPassword = Constants.EMPTY
                        mFacebookId = jsonObject.getString("id")
                        mLinkedinId = Constants.EMPTY
                        mAccountType = Constants.FACEBOOK_LOGIN
                        LoginManager.getInstance().logOut()

                        if (connectedToInternet())
                            hitSignupAPI()
                        else
                            showInternetAlert(imgFacebook)
                    }
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
                    txtSigninOptions.text = getString(R.string.sign_in_with)
                    setLogin()
                    modeEnabledSignup = false;
                }
            }
            txtSignup -> {
                if (!modeEnabledSignup) {
                    txtSigninOptions.text = getString(R.string.sign_up_with)
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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                VERIFY -> {
                    modeEnabledSignup = false;
                    viewLine.startAnimation(swiperight)
                    setLogin()
                    edEmail.requestFocus()
                    edEmail.setText(Constants.EMPTY)
                    edPassword.setText(Constants.EMPTY)
                }
            }
        }
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
                getuserData()
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

    fun getuserData() {
        var url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,public-profile-url,picture-url,email-address,picture-urls::(original))";
        var apiHelper = APIHelper.getInstance(getApplicationContext())

        apiHelper.getRequest(this, url, object : ApiListener {
            override fun onApiSuccess(apiResponse: ApiResponse?) {
                var jsonObject = apiResponse!!.responseDataAsJson
                try {
                    if (connectedToInternet()) {
                        Log.e("Response", "Linkedin apiResponse JSON " + apiResponse.getResponseDataAsString());
                        mEmail = jsonObject.getString("emailAddress")
                        mLinkedinId = jsonObject.getString("id")
                        mName = jsonObject.getString("firstName") + " " + jsonObject.getString("lastName")
                        mAccountType = Constants.LIKENDIN_LOGIN
                        mPassword = Constants.EMPTY
                        mFacebookId = Constants.EMPTY
                        mEmailVerified = Constants.EMAIL_VERIFIED
                        hitSignupAPI()
                    } else
                        showInternetAlert(imgLinkedin)

                } catch (exception: JSONException) {

                }
            }

            override fun onApiError(LIApiError: LIApiError?) {
                showAlert(imgLinkedin, LIApiError!!.localizedMessage)
            }
        })
    }

    private fun verifyDetails() {
        if (edEmail.getText().toString().trim({ it <= ' ' }).isEmpty())
            showAlert(txtDone, resources.getString(R.string.enter_email))
        else if (!validateEmail(edEmail.getText().toString().trim()))
            showAlert(txtDone, resources.getString(R.string.enter_valid_email))
        else if (edEmail.getText().toString().trim().startsWith("."))
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

                    mEmail = edEmail.text.toString().trim()
                    mPassword = edPassword.text.toString().trim()
                    mFacebookId = Constants.EMPTY
                    mLinkedinId = Constants.EMPTY
                    mAccountType = Constants.EMAIL_LOGIN
                    mEmailVerified = Constants.EMAIL_NOTVERIFIED

                    hitSignupAPI()
                } else {
                    mEmail = edEmail.text.toString().trim()
                    mPassword = edPassword.text.toString().trim()
                    hitLoginAPI()
                }
            } else
                showInternetAlert(txtDone)
        }
    }


    private fun hitLoginAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().userLogin(mEmail,
                mPassword,
                mUtils!!.getString("device_token", ""),
                mPlatformStatus,
                mUserType)

        call.enqueue(object : Callback<SignupModel> {
            override fun onResponse(call: Call<SignupModel>, response: Response<SignupModel>) {
                dismissLoader()
                if (response.body().code == Constants.PROCEED_AS_OTHER
                        && response.body().response != null) {
                    /// user enter as different user Type as comapred to signup
                    // but didn't setuped profile yet

                    /// changing userType

                    val signupModel = response.body()

                    if (signupModel.response.user_type == Constants.MENTOR)
                        signupModel.response.user_type = Constants.MENTEE
                    else if (signupModel.response.user_type == Constants.MENTEE)
                        signupModel.response.user_type = Constants.MENTOR

                    signupModel.response.full_name = mName

                    /// add data to shared preference
                    addDataToSharedPreferences(signupModel)

                    alertContinueDialog(response.body().message, signupModel)

                } else if (response.body().code == Constants.PROFILE_UNDER_REVIEW
                        && response.body().response != null) {

                    /// add data to shared preference
                    addDataToSharedPreferences(response.body())

                    /// navigate to review screen
                    moveToReview(response.body())

                } else if (response.body().code == Constants.PROCEED_NORMAL
                        && response.body().response != null) {
                    if (response.body().response.email_verified == 0 &&
                            response.body().response.profile_status == 0) {

                        /// add data to shared preference
                        addDataToSharedPreferences(response.body())

                        /// navigate to email verification screen
                        moveToEmailVerification(response.body())

                    } else if (response.body().response.email_verified == 1 &&
                            response.body().response.document_verified == 0 &&
                            response.body().response.profile_status == 0) {

                        /// add data to shared preference
                        addDataToSharedPreferences(response.body())

                        /// navigate to create profile screen
                        moveToCreateProfile(response.body())

                    } else if (response.body().response.email_verified == 1 &&
                            response.body().response.document_verified == 1 &&
                            response.body().response.profile_status == 0) {

                        /// add data to shared preference
                        addDataToSharedPreferences(response.body())

                        /// navigate to create profile screen
                        moveToCreateProfile(response.body())

                    } else if (response.body().response.email_verified == 1 &&
                            response.body().response.document_verified == 1 &&
                            response.body().response.profile_status == 1) {

                        mUtils!!.setString("access_token", response.body().response.access_token)
                        mUtils!!.setString("user_id", response.body().response.id.toString())
                        mUtils!!.setInt("profile_status", response.body().response.profile_status)
                        mUtils!!.setString("user_type", response.body().response.user_type.toString())
                        mUtils!!.setString("user_name", response.body().response.full_name)
                        mUtils!!.setString("user_pic", response.body().response.avatar.avtar_url)
                        /// add data to shared preference
                        addDataToSharedPreferences(response.body())

                        /// navigate to questionarrie
                        moveToQuestionnaire()

                    } else if (response.body().response.email_verified == 1 &&
                            response.body().response.document_verified == 1 &&
                            response.body().response.profile_status == 2) {

                        mUtils!!.setString("access_token", response.body().response.access_token)
                        mUtils!!.setInt("profile_status", response.body().response.profile_status)
                        mUtils!!.setString("user_id", response.body().response.id.toString())
                        mUtils!!.setString("user_type", response.body().response.user_type.toString())
                        mUtils!!.setString("user_name", response.body().response.full_name)
                        mUtils!!.setString("user_pic", response.body().response.avatar.avtar_url)
                        /// add data to shared preference
                        addDataToSharedPreferences(response.body())

                        /// navigate to landing Screen
                        moveToLanding()

                    }
                } else if (response.body().response == null) {
                    if (response.body().error!!.code == Constants.PROCEED_AS_OTHER_UNDER_REVIEW)
                        alertProfileSubmittedDialog(response.body().error!!.message!!)
                    else
                        showAlert(txtDone, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtDone, t!!.getLocalizedMessage())
            }

        })
    }

    private fun hitSignupAPI() {
        showLoader()
        val call = RetrofitClient.getInstance().userSignup(mFacebookId,
                mEmail, mPassword, mLinkedinId, mAccountType,
                mUtils!!.getString("device_token", ""),
                mPlatformStatus, mEmailVerified, mUserType)

        call.enqueue(object : Callback<SignupModel> {
            override fun onResponse(call: Call<SignupModel>, response: Response<SignupModel>) {
                dismissLoader()

                if (response.body().response != null) {
                    if (response.body().response.user_type != mUserType) {
                        addDataToSharedPreferences(response.body())
                        mUtils!!.setInt("switchMode", 1)
                        switchAccounts(response.body())
                    } else {
                        mUtils!!.setInt("switchMode", 0)
                        if (response.body().code == Constants.PROCEED_AS_OTHER
                                && response.body().response != null) {
                            /// user enter as different user Type as comapred to signup
                            // but didn't setuped profile yet

                            val signupModel = response.body()

                            if (signupModel.response.user_type == Constants.MENTOR)
                                signupModel.response.user_type = Constants.MENTEE
                            else if (signupModel.response.user_type == Constants.MENTEE)
                                signupModel.response.user_type = Constants.MENTOR

                            signupModel.response.full_name = mName

                            /// add data to shared preference
                            addDataToSharedPreferences(signupModel)

                            alertContinueDialog(response.body().message, signupModel)

                        } else if (response.body().code == Constants.PROFILE_UNDER_REVIEW
                                && response.body().response != null) {
                            /// add data to shared preference
                            addDataToSharedPreferences(response.body())
                            /// navigate to review screen
                            moveToReview(response.body())
                        } else if (response.body().code == Constants.PROCEED_NORMAL
                                && response.body().response != null) {
                            if (response.body().response.email_verified == 0 &&
                                    response.body().response.profile_status == 0 &&
                                    (response.body().response.account_type == Constants.EMAIL_LOGIN)) {

                                addDataToSharedPreferences(response.body())
                                /// navigate to email verification screen
                                moveToEmailVerification(response.body())

                            } else if (response.body().response.email_verified == 0 &&
                                    response.body().response.profile_status == 0 &&
                                    response.body().response.document_verified == 0 &&
                                    (response.body().response.account_type == Constants.FACEBOOK_LOGIN ||
                                            response.body().response.account_type == Constants.LIKENDIN_LOGIN)) {

                                response.body().response.full_name = mName
                                addDataToSharedPreferences(response.body())

                                if (!TextUtils.isEmpty(response.body().response.email)) {
                                    mUtils!!.setBoolean("addEmailFragment", false)

                                    moveToEmailVerification(response.body())
                                } else {
                                    mUtils!!.setBoolean("addEmailFragment", true)
                                    /// navigate to create profile screen
                                    moveToCreateProfile(response.body())
                                }
                            } else if (response.body().response.email_verified == 1 &&
                                    response.body().response.document_verified == 0 &&
                                    response.body().response.profile_status == 0) {

                                mUtils!!.setBoolean("addEmailFragment", false)
                                response.body().response.full_name = mName
                                addDataToSharedPreferences(response.body())
                                /// navigate to create profile screen
                                moveToCreateProfile(response.body())

                            } else if (response.body().response.email_verified == 1 &&
                                    response.body().response.document_verified == 1 &&
                                    response.body().response.profile_status == 0) {

                                mUtils!!.setBoolean("addEmailFragment", false)
                                response.body().response.full_name = mName
                                addDataToSharedPreferences(response.body())
                                /// navigate to create profile screen
                                moveToCreateProfile(response.body())

                            } else if (response.body().response.email_verified == 1 &&
                                    response.body().response.document_verified == 1 &&
                                    response.body().response.profile_status == 1) {

                                mUtils!!.setString("access_token", response.body().response.access_token)
                                mUtils!!.setInt("profile_status", response.body().response.profile_status)
                                mUtils!!.setString("user_id", response.body().response.id.toString())
                                mUtils!!.setString("user_type", response.body().response.user_type.toString())
                                mUtils!!.setString("user_name", response.body().response.full_name)
                                mUtils!!.setString("user_pic", response.body().response.avatar.avtar_url)
                                addDataToSharedPreferences(response.body())
                                /// navigate to questionarrie
                                moveToQuestionnaire()

                            } else if (response.body().response.email_verified == 1 &&
                                    response.body().response.document_verified == 1 &&
                                    response.body().response.profile_status == 2) {

                                mUtils!!.setString("access_token", response.body().response.access_token)
                                mUtils!!.setInt("profile_status", response.body().response.profile_status)
                                mUtils!!.setString("user_id", response.body().response.id.toString())
                                mUtils!!.setString("user_type", response.body().response.user_type.toString())
                                mUtils!!.setString("user_name", response.body().response.full_name)
                                mUtils!!.setString("user_pic", response.body().response.avatar.avtar_url)
                                addDataToSharedPreferences(response.body())
                                /// navigate to landing Screen
                                moveToLanding()
                            }
                        }
                    }
                } else {
                    if (response.body().error!!.code == Constants.PROCEED_AS_OTHER_UNDER_REVIEW)
                        alertProfileSubmittedDialog(response.body().error!!.message!!)
                    else
                        showAlert(txtDone, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<SignupModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtDone, t!!.getLocalizedMessage())
            }
        })
    }

    private fun switchAccounts(response: SignupModel) {
        var docVerified = 0
        var profileStatus = 1
        if (mUserType == Constants.MENTOR) {
            docVerified = response.response.mentor_verified
            profileStatus = response.response.mentor_profile_status
        } else if (mUserType == Constants.MENTEE) {
            docVerified = response.response.document_verified
            profileStatus = response.response.mentee_profile_status
        }
        if (docVerified == 0 && profileStatus == 1) {
            /// Review Screen
            moveToReview(response)
        } else if (docVerified == 1 && profileStatus == 1) {
            /// Questionarrie Screen

            moveToQuestionnaire()
        } else if (docVerified == 0 && profileStatus == 1) {
            /// Landing Screen
            moveToLanding()
        }
    }

    private fun moveToReview(body: SignupModel?) {
        intent = Intent(mContext, ReviewActivity::class.java)
        intent.putExtra("avatar", body!!.response.avatar)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    private fun moveToEmailVerification(body: SignupModel?) {
        intent = Intent(mContext, EmailVerificationActivity::class.java)
        intent.putExtra("access_token", body!!.response.access_token)
        intent.putExtra("userType", mUserType)
        startActivityForResult(intent, VERIFY)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    private fun moveToCreateProfile(body: SignupModel?) {
        intent = Intent(mContext, CreateProfileActivity::class.java)
        intent.putExtra("userData", body)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    private fun moveToQuestionnaire() {
        intent = Intent(mContext, QuestionnariesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    private fun moveToLanding() {
        val values = HashMap<String, Any>()
        values.put("user_id", mUtils!!.getString("user_id", ""))
        values.put("online_status", Constants.ONLINE_LONG)
        values.put("access_token", mUtils!!.getString("access_token", ""))
        values.put("user_name", mUtils!!.getString("user_name", ""))
        values.put("user_pic", mUtils!!.getString("user_pic", ""))
        var mFirebaseConfigProfile = FirebaseDatabase.getInstance().getReference().child(Constants.USERS)
        mFirebaseConfigProfile.child("id_" + mUtils!!.getString("user_id", "")).updateChildren(values)

        val model = ProfileModel()
        model.access_token = mUtils!!.getString("access_token", "")
        model.user_id = mUtils!!.getString("user_id", "")
        model.online_status = Constants.ONLINE_LONG
        model.user_name = mUtils!!.getString("user_name", "")
        model.user_pic = mUtils!!.getString("user_pic", "")
        db!!.addProfile(model)

        intent = Intent(mContext, LandingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    internal fun validateEmail(text: CharSequence): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        val matcher = pattern.matcher(text)
        return matcher.matches()
    }

    private fun alertContinueDialog(message: String, signupModel: SignupModel) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.confrim, DialogInterface.OnClickListener { dialog, id ->
                    mUtils!!.setBoolean("addEmailFragment", false)
                    val intent = Intent(mContext, CreateProfileActivity::class.java)
                    intent.putExtra("userData", signupModel)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                })
                .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, id ->
                    edEmail.setText(Constants.EMPTY)
                    edPassword.setText(Constants.EMPTY)
                    dialog.cancel()

                })

        val alert = builder.create()
        alert.show()
    }

    private fun alertProfileSubmittedDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
                .setCancelable(false)
                .setNegativeButton(R.string.ok, DialogInterface.OnClickListener { dialog, id ->
                    edEmail.requestFocus()
                    edEmail.setText(Constants.EMPTY)
                    edPassword.setText(Constants.EMPTY)
                    dialog.cancel()
                })

        val alert = builder.create()
        alert.show()
    }

    fun addDataToSharedPreferences(signupModel: SignupModel) {
        mUtils!!.setString("tipsVisible", signupModel.response.tip)
        mUtils!!.setString("userDataLocal", mGson.toJson(signupModel))
    }

}