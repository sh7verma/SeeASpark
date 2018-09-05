package com.seeaspark

import adapters.FavouriteAdapter
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_favourite_message.*
import models.ChatsModel
import models.MessagesModel
import models.ProfileModel
import services.DownloadFileService
import services.FavouriteMessageApi
import services.UploadFileService
import utils.Constants
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by dev on 31/7/18.
 */
class FavouriteMessageActivity : BaseActivity(), DownloadFileService.FileDownloadFavouriteInterface, UploadFileService.FileUploadFavouriteInterface {

    internal var usersQuery: Query? = null
    internal var mFirebaseConfigProfile = FirebaseDatabase.getInstance().getReference().child(Constants.USERS)

    internal var chat_date_format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    internal var today_date_format = SimpleDateFormat("hh:mm aa", Locale.US)
    internal var only_date_format = SimpleDateFormat("dd MMMM", Locale.US)
    internal var mTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    internal var show_dateheader_format = SimpleDateFormat("dd MMM yyyy", Locale.US)

    var mMessagesMap: HashMap<String, MessagesModel>? = null
    var mMessageIds: ArrayList<String> = ArrayList()
    private var mFavouriteMessageActivity: FavouriteMessageActivity? = null
    private var mFavouriteAdapter: FavouriteAdapter? = null
    internal var mOpponentUser: ProfileModel? = null
    internal var mCurrentUser: ProfileModel? = null
    internal var mPrivateChat: ChatsModel? = null
    var mParticpantIDS = ""
    internal var mOpponentUserId = ""

    override fun getContentView() = R.layout.activity_favourite_message

    override fun initUI() {
        mMessagesMap = HashMap()
        mMessageIds = ArrayList<String>()
        mFavouriteMessageActivity = this
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llOuterFavourite.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        imgBack.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llOuterFavourite.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBack.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        mCurrentUser = db!!.getProfile(mUtils!!.getString("user_id", ""))
        mCurrentUser!!.chat_dialog_ids = db!!.getDialogs(mCurrentUser!!.user_id)
        mParticpantIDS = intent.getStringExtra("participantIDs")
        mOpponentUserId = intent.getStringExtra("opponentUserId")
        mOpponentUser = db!!.getProfile(mOpponentUserId)
        mPrivateChat = db!!.getChatDialog("" + mParticpantIDS, mUtils!!.getString("user_id", ""), mOpponentUserId)
        setHeaderData()
        val deletetime = mPrivateChat!!.delete_dialog_time.get(mUtils!!.getString("user_id", ""))
        mMessagesMap = db!!.getAllFavouriteMessages(
                mPrivateChat!!.chat_dialog_id,
                mCurrentUser!!.user_id,
                deletetime!!,
                mOpponentUserId,
                "1"
        )
        makeHeaders()

        if (mMessagesMap!!.size == 0) {
            txtNoFavourite.visibility = View.VISIBLE
            lvFavouriteList.visibility = View.GONE
        } else {
            txtNoFavourite.visibility = View.GONE
            lvFavouriteList.visibility = View.VISIBLE
        }

        mFavouriteAdapter = FavouriteAdapter(this, mFavouriteMessageActivity, mWidth, mCurrentUser!!.user_id,
                mOpponentUserId, mPrivateChat!!.participant_ids, mPrivateChat)
        lvFavouriteList.adapter = mFavouriteAdapter

        setOnlineFlag(mOpponentUser!!)
        listenUser()
        DownloadFileService.setFavouriteDownloadingListener(this)
        UploadFileService.setFavouriteUploadingListener(this)
    }

    internal fun makeHeaders() {
        mMessageIds = ArrayList()
        for (key in mMessagesMap!!.keys) {
            mMessageIds.add(key)
        }
        if (mMessagesMap!!.size <= 1) {
            mMessagesMap!!.clear()
            mMessageIds.clear()
        }
//        for (i in 0 until mMessagesMap!!.size) {
//            val message = mMessagesMap!![mMessageIds[i]]
//            if (!message!!.is_header) {
//                if (!message.sender_id.equals(mCurrentUser!!.user_id) && (message.message_type.equals(Constants.TYPE_IMAGE) ||
//                        message.message_type.equals(Constants.TYPE_AUDIO) || message.message_type.equals(Constants.TYPE_VIDEO)/* ||
//                        message.message_type.equals(Constants.TYPE_DOCUMENT)*/)) {
//                    if (mMessageIds.contains(message.message_id)) {
//                        if (TextUtils.isEmpty(message.attachment_path) && message.attachment_status.equals(Constants.FILE_EREROR) && !TextUtils.isEmpty(message.attachment_url)) {
//                            if (connectedToInternet()) {
//                                val intent = Intent(this, DownloadFileService::class.java)
//                                intent.putExtra("message_id", "" + message.message_id)
//                                startService(intent)
//                            }
//                        }
//                    } else {
//                        if (connectedToInternet()) {
//                            if (!TextUtils.isEmpty(message.attachment_url)) {
//                                val intent = Intent(this, DownloadFileService::class.java)
//                                intent.putExtra("message_id", "" + message.message_id)
//                                startService(intent)
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun initListener() {
        imgBack.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                moveBack()
            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    fun setHeaderData() {
        txtName.setText(mPrivateChat!!.name.get(mOpponentUserId))
        Picasso.with(this).load(mPrivateChat!!.profile_pic.get(mOpponentUserId)).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
    }

    internal fun listenUser() {
        usersQuery = mFirebaseConfigProfile.orderByKey().equalTo("id_" + mOpponentUserId)
        usersQuery!!.addChildEventListener(mOpponentUserListener)
    }

    internal var mOpponentUserListener: ChildEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: com.google.firebase.database.DataSnapshot, s: String?) {
            val user = ProfileModel.parseProfile(dataSnapshot)
            db!!.addProfile(user)
            mOpponentUser = db!!.getProfile(user.user_id)
            txtName.setText(mOpponentUser!!.user_name)
            if (!mOpponentUser!!.user_pic.equals(user.user_pic)) {
                Picasso.with(this@FavouriteMessageActivity).load(mOpponentUser!!.user_pic).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
            }
            setOnlineFlag(user)
        }

        override fun onChildChanged(dataSnapshot: com.google.firebase.database.DataSnapshot, s: String?) {
            val user = ProfileModel.parseProfile(dataSnapshot)
            db!!.addProfile(user)
            mOpponentUser = db!!.getProfile(user.user_id)
            txtName.setText(mOpponentUser!!.user_name)
            if (!mOpponentUser!!.user_pic.equals(user.user_pic)) {
                Picasso.with(this@FavouriteMessageActivity).load(mOpponentUser!!.user_pic).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
            }
            setOnlineFlag(user)
        }

        override fun onChildRemoved(dataSnapshot: com.google.firebase.database.DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: com.google.firebase.database.DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    internal fun setOnlineFlag(mUser: ProfileModel) {
        val online_format = SimpleDateFormat("HH:mm", Locale.US)
        if (mUser.online_status == Constants.ONLINE_LONG) {
            txtActive.setText("" + Constants.ONLINE)
        } else {
            var last_seen_status = ""
            try {
                val current = Calendar.getInstance()
                val current_time = current.time
                val today = chat_date_format.format(current_time)
                val time = Constants.getLocalTime(mUser.online_status)
                val cl = Calendar.getInstance()
                cl.timeInMillis = time
                val dd = mTime.format(cl.time)
                val utc_date = mTime.parse(dd)
                val utc_create = Calendar.getInstance()
                utc_create.time = utc_date

                if (today.equals(chat_date_format.format(utc_create.time), ignoreCase = true)) {
                    //today
                    last_seen_status = resources.getString(R.string.Last_seen_today_at) + " " +
                            online_format.format(utc_create.time)
                } else {
                    current.add(Calendar.DATE, -1)
                    val yesterday = current.time
                    if (chat_date_format.format(yesterday).equals(chat_date_format.format(utc_create.time), ignoreCase = true)) {
                        //yesterday
                        last_seen_status = resources.getString(R.string.Last_seen_yesterday_at) + " " +
                                online_format.format(utc_create.time)
                    } else {
                        last_seen_status = resources.getString(R.string.Last_seen) + " " +
                                only_date_format.format(utc_create.time) + " " + resources.getString(R.string.at) + " " + online_format.format(utc_create.time)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            txtActive.setText("Offline")
//            txtActive.setText(last_seen_status)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (usersQuery != null) {
            usersQuery!!.removeEventListener(mOpponentUserListener)
        }
    }

    var listGalleryPermissionsNeeded = ArrayList<String>()
    internal var galleryPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val GALLERY_PERMISSION_REQUEST_CODE = 18
    fun checkGalleryPermissions(): Boolean {
        var result: Int
        listGalleryPermissionsNeeded = ArrayList<String>()
        for (p in galleryPermissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listGalleryPermissionsNeeded.add(p)
            }
        }
        if (!listGalleryPermissionsNeeded.isEmpty()) {
            return false
        }
        return true
    }

    fun requestGalleryPermission(text: Int) {
        if (hasPermissions(this, listGalleryPermissionsNeeded)) {
            mUtils!!.setBoolean(Constants.GALLERY_PERMISSION, false)
            showSnackbar(text,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this, listGalleryPermissionsNeeded.toTypedArray(), GALLERY_PERMISSION_REQUEST_CODE)
            })
        } else {
            if (!mUtils!!.getBoolean(Constants.GALLERY_PERMISSION, false))
                ActivityCompat.requestPermissions(this, listGalleryPermissionsNeeded.toTypedArray(), GALLERY_PERMISSION_REQUEST_CODE)
            if (mUtils!!.getBoolean(Constants.GALLERY_PERMISSION, false)) {
                showSnackbar(text,
                        android.R.string.ok, View.OnClickListener {
                    // Request permission
                    mUtils!!.setBoolean(Constants.GALLERY_PERMISSION, false)
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.data = Uri.parse("package:" + getPackageName())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(intent)
                })
            }
            mUtils!!.setBoolean(Constants.GALLERY_PERMISSION, true)
        }
    }

    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int, listener: View.OnClickListener) {
        Snackbar.make(findViewById(android.R.id.content), getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE).setActionTextColor(ContextCompat.getColor(this, R.color.red_color)).setAction(getString(actionStringId), listener).show()
    }

    fun hasPermissions(context: Context?, vararg permissions: ArrayList<String>): Boolean {
        for ((index, permission) in permissions.withIndex()) {
            if (ActivityCompat.checkSelfPermission(context!!, permission.toString()) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onStartUploading(message_id: String?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_UPLOADING
            mFavouriteAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onSuccessUploading(message_id: String?, attachment_data: String?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_SUCCESS
            mMessagesMap!![message_id]!!.attachment_url = attachment_data
            mMessagesMap!![message_id]!!.message_status = Constants.STATUS_MESSAGE_SENT
            mFavouriteAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onErrorUploading(message_id: String?, exception: java.lang.Exception?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_EREROR
            mFavouriteAdapter!!.notifyDataSetChanged()
            Toast.makeText(this, "" + exception, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onProgressUpdate(message_id: String?, progress: Int) {
        if (mMessageIds!!.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_progress = "" + progress
            mFavouriteAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onDownloadProgressUpdate(message_id: String?, progress: Int) {
        if (mMessageIds!!.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_progress = "" + progress
            mFavouriteAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onStartDownloading(message_id: String?) {
        if (mMessagesMap != null && mMessagesMap!!.containsKey(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_UPLOADING
            mFavouriteAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onSuccessDownloading(message_id: String?, path: String?, thumbPath: String?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_SUCCESS
            mMessagesMap!![message_id]!!.attachment_progress = "100"
            mMessagesMap!![message_id]!!.attachment_path = path
            mMessagesMap!![message_id]!!.custom_data = thumbPath
            mFavouriteAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onErrorDownloading(message_id: String?, exception: java.lang.Exception?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_EREROR
            mFavouriteAdapter!!.notifyDataSetChanged()
        }
    }

    fun removeFavouriteMessage(msgId: String) {
        db!!.changFavouriteStatus(msgId, "0")
        FavouriteMessageApi(this, msgId)
        val deletetime = mPrivateChat!!.delete_dialog_time.get(mUtils!!.getString("user_id", ""))
        mMessagesMap = db!!.getAllFavouriteMessages(
                mPrivateChat!!.chat_dialog_id,
                mCurrentUser!!.user_id,
                deletetime!!,
                mOpponentUserId,
                "1"
        )
        makeHeaders()
        mFavouriteAdapter!!.notifyDataSetChanged()

        if (mMessagesMap!!.size == 0) {
            txtNoFavourite.visibility = View.VISIBLE
            lvFavouriteList.visibility = View.GONE
        } else {
            txtNoFavourite.visibility = View.GONE
            lvFavouriteList.visibility = View.VISIBLE
        }

        if (mChangeFavouriteInterface != null) {
            mChangeFavouriteInterface!!.onChange(msgId)
        }
    }

    interface ChangeFavouriteInterface {
        fun onChange(message_id: String)
    }

    companion object {
        var mChangeFavouriteInterface: ChangeFavouriteInterface? = null
        fun setUploadingListener(listsner: ChangeFavouriteInterface) {
            mChangeFavouriteInterface = listsner
        }
    }

}