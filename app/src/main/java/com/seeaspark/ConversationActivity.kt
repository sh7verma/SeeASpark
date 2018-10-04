package com.seeaspark

import adapters.ConversationAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.faradaj.blurbehind.BlurBehind
import com.google.firebase.database.*
import com.ipaulpro.afilechooser.utils.FileUtils
import com.squareup.picasso.Picasso
import filePicker.FilePickerBuilder
import helper.FirebaseListeners
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.media_options_dialog.*
import models.*
import network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import services.DeleteMessageApi
import services.DownloadFileService
import services.FavouriteMessageApi
import services.UploadFileService
import utils.Constants
import utils.MainApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ConversationActivity : BaseActivity(), FirebaseListeners.ChatDialogsListenerInterfaceForChat,
        FirebaseListeners.MessageListenerInterface, UploadFileService.FileUploadInterface,
        DownloadFileService.FileDownloadInterface, FavouriteMessageActivity.ChangeFavouriteInterface {

    private val ATTACHMENT = 12
    private val OPTIONS = 13
    private val CAMERAVIDEO = 14
    private val CAMERAIMAGE = 15
    private val GALLERY = 16
    private val GETIMAGE = 17
    private val GETVIDEO = 18
    private val GETDOC = 19
    private val NOTES = 20

    //// Audio message  /////////
    private var gestureRecordBtn: GestureDetector? = null
    internal var gestureListenerRecordBtn: View.OnTouchListener? = null
    private val stopRecord = Handler()
    internal var mediaRecorder: MediaRecorder? = null
    internal var showAudioTime = "00:00"
    internal var audioFile: File? = null
    internal var recordCancelAnim: Animation? = null
    internal var sDecelerator = DecelerateInterpolator()
    internal var mOvershooter = OvershootInterpolator(7f)
    internal var isRecording = false
    internal var audioLimitReached = false
    internal var cancelRecording = false
    internal var mp: MediaPlayer? = null
    internal var mpRecord: MediaPlayer? = null
    internal var x1 = 0f
    internal var x2 = 0f
    internal var mediaPlayer: MediaPlayer? = null
    internal var length = 0
    internal var startTime = 0
    internal var finalTime = 0

    ///////////////////////////

    private var mConversationActivity: ConversationActivity? = null
    private var mConversationAdapter: ConversationAdapter? = null

    private var camerapath = ""
    private var camerapathVideo = ""
    private var status = 0

    private var docPath = ""

    private var mOpponentUser: ProfileModel? = null
    private var mCurrentUser: ProfileModel? = null
    private var mPrivateChat: ChatsModel? = null
    var mParticpantIDS = ""
    private var mOpponentUserId = ""
    private var msgId = ""

    private var usersQuery: Query? = null
    private var mFirebaseConfigProfile = FirebaseDatabase.getInstance().reference.child(Constants.USERS)
    private var mFirebaseConfigChats = FirebaseDatabase.getInstance().reference.child(Constants.CHATS)
    private var mFirebaseConfigMessages = FirebaseDatabase.getInstance().reference.child(Constants.MESSAGES)
    private var mFirebaseConfigNotifications = FirebaseDatabase.getInstance().reference.child(Constants.NOTIFICATIONS)
    private var mFirebaseConfigReadStatus = FirebaseDatabase.getInstance().reference.child(Constants.READ_STATUS)

    private var chat_date_format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private var today_date_format = SimpleDateFormat("hh:mm aa", Locale.US)
    private var only_date_format = SimpleDateFormat("dd MMMM", Locale.US)
    private var mTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private var show_dateheader_format = SimpleDateFormat("dd MMM yyyy", Locale.US)

    internal var limit = 20
    private var setReadMessages: ArrayList<MessagesModel> = ArrayList()

    var mMessagesMap: HashMap<String, MessagesModel>? = null
    var mMessageIds: ArrayList<String> = ArrayList()

    var selectedPosition = 0
    var noResultsinPagination = false

    var clearTime = 0L

    override fun getContentView() = R.layout.activity_conversation

    override fun initUI() {
        mMessagesMap = HashMap()
        mMessageIds = ArrayList()
        mConversationActivity = this
        val typeface = Typeface.createFromAsset(assets, "fonts/medium.otf")
        edMessage.typeface = typeface

        val send_pams = RelativeLayout.LayoutParams((mWidth * 0.12).toInt(), (mWidth * 0.12).toInt())
        send_pams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        send_pams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        send_pams.setMargins(0, 0, mWidth / 48, mWidth / 48)
        sendRecordBtn.layoutParams = send_pams
        sendRecordBtn.isEnabled = true

        edMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().trim({ it <= ' ' }).isNotEmpty()) {
                    imgSendMessage.visibility = View.VISIBLE
                    sendRecordBtn.visibility = View.GONE
                } else {
                    imgSendMessage.visibility = View.GONE
                    sendRecordBtn.visibility = View.VISIBLE
                }
            }
        })

        //// Audio message  /////////
        recordCancelAnim = TranslateAnimation(0f, mWidth.toFloat(), 0f, 0f)
        recordCancelAnim!!.duration = 600
        recordCancelAnim!!.interpolator = LinearInterpolator()
        recordCancelAnim!!.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {
                // TODO Auto-generated method stub
                //                send_record_btn.setEnabled(false);
            }

            override fun onAnimationRepeat(animation: Animation) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationEnd(animation: Animation) {
                // TODO Auto-generated method stub
                sendRecordBtn.isEnabled = true
                llRecordLayout.visibility = View.GONE
                llTextMessageSend.visibility = View.VISIBLE
            }
        })

        gestureRecordBtn = GestureDetector(this,
                RecordBtnGesture())
        gestureListenerRecordBtn = View.OnTouchListener { v, event ->
            // TODO Auto-generated method stub
            if (gestureRecordBtn!!.onTouchEvent(event)) {
                return@OnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_UP) {
                if (mp != null) {
                    mp!!.stop()
                    mp!!.release()
                    mp = null
                }
                if (isRecording) {
                    timeRecordChrono.stop()
                    timeRecordChrono.text = ""
                    timeRecordChrono.animate().setInterpolator(mOvershooter)
                            .scaleX(1f).scaleY(1f)
                    stopRecord.removeCallbacks(stop_rec)
                    try {
                        if (mediaRecorder != null) {
                            mediaRecorder!!.stop()
                            mediaRecorder!!.release()
                            mediaRecorder = null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (mpRecord != null) {
                        mpRecord!!.stop()
                        mpRecord!!.release()
                        mpRecord = null
                    }
                    if (!audioLimitReached) {
                        val time = SystemClock.elapsedRealtime() - timeRecordChrono
                                .base
                        showAudioTime = String
                                .format("%02d:%02d",
                                        TimeUnit.MILLISECONDS
                                                .toMinutes(time),
                                        TimeUnit.MILLISECONDS
                                                .toSeconds(time) - TimeUnit.MINUTES
                                                .toSeconds(TimeUnit.MILLISECONDS
                                                        .toMinutes(time)))
                    }

                    x2 = event.x
                    val deltaX = x2 - x1
                    cancelRecording = Math.abs(deltaX) > 70

                    if (showAudioTime == "00:00" || showAudioTime == "00:01") {
                        cancelRecording = true
                    }
                    if (cancelRecording) {
                        if (audioFile!!.exists()) {
                            audioFile!!.delete()
                        }

                        if (mp != null) {
                            mp!!.stop()
                            mp!!.release()
                            mp = null
                        }
                        mp = MediaPlayer.create(this, R.raw.cancel_recording)
                        mp!!.start()

                        showAudioTime = "00:00"
                        txtSlideCancel.text = ""
                        slide_arrow.visibility = View.GONE
                        imgDeleteRecording.startAnimation(recordCancelAnim)
                        sendRecordIcon.setBackgroundResource(R.mipmap.ic_mic)
                        sendRecordBtn.animate().setInterpolator(mOvershooter)
                                .scaleX(1f).scaleY(1f)
                        imgSendMessage.visibility = View.GONE
                        edMessage.requestFocus()
                    } else {
                        llRecordLayout.visibility = View.GONE
                        llTextMessageSend.visibility = View.VISIBLE
                        sendRecordBtn.animate().setInterpolator(mOvershooter)
                                .scaleX(1f).scaleY(1f)
                        sendRecordIcon.setBackgroundResource(R.mipmap.ic_mic)
                        imgSendMessage.visibility = View.GONE
                        edMessage.requestFocus()
                        // send audio message
                        if (mPrivateChat != null) {
                            addAudioMessage(audioFile!!.absolutePath, showAudioTime)
                            val inSend = Intent(this, UploadFileService::class.java)
                            inSend.putExtra("attachment_path", "" + audioFile!!.absolutePath)
                            startService(inSend)
                        }
                    }
                } else {
                    timeRecordChrono.stop()
                    timeRecordChrono.text = ""
                    sendRecordBtn.animate().setInterpolator(mOvershooter)
                            .scaleX(1f).scaleY(1f)
                    stopRecord.removeCallbacks(stop_rec)
                    try {
                        if (mediaRecorder != null) {
                            mediaRecorder!!.stop()
                            mediaRecorder!!.release()
                            mediaRecorder = null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val messageText = edMessage.text
                            .toString().trim { it <= ' ' }
                    if (messageText.isNotEmpty()) {
                        // Send chat message
                    } else {
                        try {
                            if (mediaRecorder != null) {
                                mediaRecorder!!.stop()
                                mediaRecorder!!.release()
                                mediaRecorder = null
                            }
                            stopRecord.removeCallbacks(stop_rec)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
                return@OnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_CANCEL) {
                if (mp != null) {
                    mp!!.stop()
                    mp!!.release()
                    mp = null
                }
                return@OnTouchListener true
            }
            false
        }
        sendRecordBtn.setOnTouchListener(gestureListenerRecordBtn)

        ////////////////////
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
        llOuterConversation.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
        imgBackDefault.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        imgOptions.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        imgBackOption.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        imgFavourite.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        imgDelete.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        imgCopy.background = ContextCompat.getDrawable(this, R.drawable.white_ripple)
        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
        llOuterConversation.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
        imgBackDefault.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        imgOptions.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        imgBackOption.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        imgFavourite.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        imgDelete.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        imgCopy.background = ContextCompat.getDrawable(this, R.drawable.black_ripple)
        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        FirebaseListeners.getListenerClass(this).setProfileListener(mUtils!!.getString("user_id", ""))

        setReadMessages = ArrayList()
        mCurrentUser = db!!.getProfile(mUtils!!.getString("user_id", ""))
        if (mCurrentUser == null) {
            return
        }
        mCurrentUser!!.chat_dialog_ids = db!!.getDialogs(mCurrentUser!!.user_id)

        if (intent.hasExtra("participantIDs")) {
            mParticpantIDS = intent.getStringExtra("participantIDs")
            val particID = mParticpantIDS.split(",").toTypedArray()
            for (id in particID) {
                val userId = id.split("_").toTypedArray()
                if (!userId[0].trim().equals(mCurrentUser!!.user_id, ignoreCase = true)) {
                    mOpponentUserId = userId[0].trim()
                    mOpponentUser = db!!.getProfile(userId[0].trim())
                    break
                }
            }
        } else {
            mParticpantIDS = mUtils!!.getString("participant_ids", "")
            val particID = mParticpantIDS.split(",").toTypedArray()
            for (id in particID) {
                val userId = id.split("_").toTypedArray()
                if (!userId[0].trim().equals(mCurrentUser!!.user_id, ignoreCase = true)) {
                    mOpponentUserId = userId[0].trim()
                    mOpponentUser = db!!.getProfile(userId[0].trim())
                }
            }
            mUtils!!.setString("participant_ids", "")
        }
        FirebaseListeners.setChatDialogListenerForChat(this, mParticpantIDS)
        FirebaseListeners.setMessageListener(this, mParticpantIDS)
        UploadFileService.setUploadingListener(this)
        DownloadFileService.setDownloadingListener(this)
        chatSetUp()
    }

    override fun onNewIntent(intent: Intent?) {
        val parIds: String
        if (intent!!.hasExtra("participantIDs")) {
            parIds = intent.getStringExtra("participantIDs")
        } else {
            parIds = mUtils!!.getString("participant_ids", "")
            mUtils!!.setString("participant_ids", "")
        }

        if (parIds.equals(mParticpantIDS)) {
            // do nothing same chat
        } else {
            // change chat data
            if (usersQuery != null) {
                usersQuery!!.removeEventListener(mOpponentUserListener)
            }
            llDefaultActionbar.visibility = View.VISIBLE
            llOptionActionbar.visibility = View.GONE
            edMessage.setText("")
            val inp = arrayOf<InputFilter>(InputFilter.LengthFilter(4000))
            edMessage.filters = inp
            selectedPosition = 0
            setReadMessages = ArrayList()
            mMessagesMap = HashMap()
            mMessageIds = ArrayList<String>()
            mCurrentUser!!.chat_dialog_ids = db!!.getDialogs(mCurrentUser!!.user_id)
            mParticpantIDS = parIds
            val particID = mParticpantIDS.split(",").toTypedArray()
            for (id in particID) {
                val userId = id.split("_").toTypedArray()
                if (!userId[0].trim().equals(mCurrentUser!!.user_id, ignoreCase = true)) {
                    mOpponentUserId = userId[0].trim()
                    mOpponentUser = db!!.getProfile(userId[0].trim())
                }
            }
            FirebaseListeners.setChatDialogListenerForChat(this, mParticpantIDS)
            FirebaseListeners.setMessageListener(this, mParticpantIDS)
            chatSetUp()
            super.onNewIntent(intent)
        }
    }

    fun isOptionsVisible() {
        if (llDefaultActionbar.visibility == View.VISIBLE) {
            selectedPosition = 0
        } else {
            selectedPosition = 0
            llDefaultActionbar.visibility = View.VISIBLE
            llOptionActionbar.visibility = View.GONE
        }
    }

    fun makeOptionsVisible(pos: Int, messageId: String, type: Int) {
        selectedPosition = pos
        msgId = messageId
//        TYPE_TEXT = "1"
//        TYPE_IMAGE = "2"
//        TYPE_VIDEO = "3"
//        TYPE_DOCUMENT = "4"
//        TYPE_NOTES = "5"
//        TYPE_AUDIO = "6"

        if (type == 1) {
            imgCopy.visibility = View.VISIBLE
        } else {
            imgCopy.visibility = View.GONE
        }

        if (mMessagesMap!![msgId]!!.favourite_message.get(mCurrentUser!!.user_id).equals("0")) {
            imgFavourite.setImageResource(R.mipmap.ic_heart)
        } else {
            imgFavourite.setImageResource(R.mipmap.ic_heart_red)
        }

        llDefaultActionbar.visibility = View.GONE
        llOptionActionbar.visibility = View.VISIBLE
    }

    override fun onResume() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        for (i in setReadMessages.indices) {
            val message = setReadMessages[i]
            if (!message.sender_id.equals(mCurrentUser!!.user_id)) {
                // message has been received by me
                if (message.message_status != Constants.STATUS_MESSAGE_SEEN) {
                    mFirebaseConfigMessages.child(mPrivateChat!!.chat_dialog_id).child(message.message_id).child("message_status").setValue(Constants.STATUS_MESSAGE_SEEN)
                    val msgHashMap = HashMap<String, Any>()
                    msgHashMap.put("message_id", message.message_id)
                    msgHashMap.put("message_status", Constants.STATUS_MESSAGE_SEEN)
                    mFirebaseConfigReadStatus.child(message.message_id).setValue(msgHashMap)
                }
            }
        }
        if (mPrivateChat != null && mPrivateChat!!.unread_count.get(mCurrentUser!!.user_id) != 0) {
            mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("unread_count").child(mCurrentUser!!.user_id).setValue(0)
        }
        setReadMessages.clear()
        mUtils!!.setString("chat_dialog_id", mParticpantIDS)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mUtils!!.setString("chat_dialog_id", "")
    }

    fun chatSetUp() {
        if (mOpponentUser == null) {
            mOpponentUser = ProfileModel()
            mOpponentUser!!.user_id = mOpponentUserId
        }

        mPrivateChat = db!!.getChatDialog("" + mParticpantIDS,
                mUtils!!.getString("user_id", ""), mOpponentUserId)

        listenUser()

        if (mPrivateChat == null) {
            // no entry in database, check firebase
            val query = mFirebaseConfigChats.orderByChild("participant_ids").equalTo("" + mParticpantIDS)
            query.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onCancelled(firebaseError: DatabaseError) {
                    showAlert(txtActive, "" + firebaseError)
                    finish()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value == null) {
                        // no entry on firebase as well
//                        createNewChat()
                    } else {
                        // add value to db
                        for (postSnapshot in dataSnapshot.children) {
                            val mChat = ChatsModel.parseChat(postSnapshot, mUtils!!.getString("user_id", ""))
                            if (mChat != null) {
                                mChat.chat_dialog_id = postSnapshot.key
                                db!!.addNewChat(mChat, mUtils!!.getString("user_id", ""), mOpponentUserId)
                                mPrivateChat = mChat
                            }
                        }
                        if (mPrivateChat == null) {
//                            createNewChat()
                        } else {
                            setHeaderData()
                            addMessageListener()
                        }
                    }
                }
            })
        } else {
            setHeaderData()
            addMessageListener()
        }
    }

    internal fun listenUser() {
        usersQuery = mFirebaseConfigProfile.orderByKey().equalTo("id_$mOpponentUserId")
        usersQuery!!.addChildEventListener(mOpponentUserListener)
    }

    internal var mOpponentUserListener: ChildEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: com.google.firebase.database.DataSnapshot, s: String?) {
            val user = ProfileModel.parseProfile(dataSnapshot)
            db!!.addProfile(user)
            mOpponentUser = db!!.getProfile(user.user_id)
            setOnlineFlag(user)

            txtName.text = user.user_name
//            if (!user.user_pic.equals(mOpponentUser!!.user_pic)) {
//                Picasso.with(mContext).load(mOpponentUser!!.user_pic).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
//            }
        }

        override fun onChildChanged(dataSnapshot: com.google.firebase.database.DataSnapshot, s: String?) {
            val user = ProfileModel.parseProfile(dataSnapshot)
            db!!.addProfile(user)
            mOpponentUser = db!!.getProfile(user.user_id)
            setOnlineFlag(user)

            txtName.text = user.user_name
//            if (!user.user_pic.equals(mOpponentUser!!.user_pic)) {
//                Picasso.with(mContext).load(mOpponentUser!!.user_pic).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
//            }
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
            txtActive.text = Constants.ONLINE
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
            txtActive.text = getString(R.string.offline)
//            txtActive.setText(last_seen_status)
        }
    }

    override fun onDialogChanged(mChat: ChatsModel?, value: Int) {
        try {
            if (value == 0) {
                if (mPrivateChat!!.chat_dialog_id.equals(mChat!!.chat_dialog_id)) {
                    txtName.text = mPrivateChat!!.name.get(mOpponentUserId)
                    if (!mPrivateChat!!.profile_pic.get(mOpponentUserId).equals(mChat.profile_pic.get(mOpponentUserId))) {
                        Picasso.with(this).load(mChat.profile_pic.get(mOpponentUserId)).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
                    }
                    mPrivateChat = mChat
                }
            } else {
                Toast.makeText(mContext!!, mPrivateChat!!.name.get(mOpponentUserId) + " " + getString(R.string.unmatch_message), Toast.LENGTH_SHORT).show()
                moveBack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartUploading(message_id: String?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_UPLOADING
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onSuccessUploading(message_id: String?, attachment_data: String?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_SUCCESS
            mMessagesMap!![message_id]!!.attachment_url = attachment_data
            mMessagesMap!![message_id]!!.message_status = Constants.STATUS_MESSAGE_SENT
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onErrorUploading(message_id: String?, exception: java.lang.Exception?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_EREROR
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
            Toast.makeText(this, "" + exception, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onProgressUpdate(message_id: String?, progress: Int) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_progress = "" + progress
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onDownloadProgressUpdate(message_id: String?, progress: Int) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_progress = "" + progress
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onStartDownloading(message_id: String?) {
        if (mMessagesMap != null && mMessagesMap!!.containsKey(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_UPLOADING
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onSuccessDownloading(message_id: String?, path: String?, thumbPath: String?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_SUCCESS
            mMessagesMap!![message_id]!!.attachment_progress = "100"
            mMessagesMap!![message_id]!!.attachment_path = path
            mMessagesMap!![message_id]!!.custom_data = thumbPath
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onErrorDownloading(message_id: String?, exception: java.lang.Exception?) {
        if (mMessageIds.contains(message_id)) {
            mMessagesMap!![message_id]!!.attachment_status = Constants.FILE_EREROR
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
        }
    }

    fun setHeaderData() {
        txtName.text = mPrivateChat!!.name.get(mOpponentUserId)
        Picasso.with(this).load(mPrivateChat!!.profile_pic.get(mOpponentUserId)).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
    }

    internal fun addMessageListener() {
        FirebaseListeners.getListenerClass(applicationContext).setListener(mPrivateChat!!.chat_dialog_id)
        val deletetime = mPrivateChat!!.delete_dialog_time.get(mUtils!!.getString("user_id", ""))
        mMessagesMap = db!!.getAllMessages(mPrivateChat!!.chat_dialog_id, mCurrentUser!!.user_id, limit, deletetime!!, mOpponentUserId)
        makeHeaders()

        mConversationAdapter = ConversationAdapter(this, mConversationActivity, mWidth, mCurrentUser!!.user_id,
                mOpponentUserId, mPrivateChat!!.participant_ids, mPrivateChat)
        mConversationAdapter!!.animationStatus(false)
        lvChatList.adapter = mConversationAdapter
        displayRatingDialog()
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
        for (i in 0 until mMessagesMap!!.size) {
            val message = mMessagesMap!![mMessageIds[i]]
            if (!message!!.is_header) {
                if (!message.sender_id.equals(mCurrentUser!!.user_id) && (message.message_type.equals(Constants.TYPE_IMAGE) ||
                                message.message_type.equals(Constants.TYPE_AUDIO) || message.message_type.equals(Constants.TYPE_VIDEO) ||
                                message.message_type.equals(Constants.TYPE_DOCUMENT))) {
                    if (mMessageIds.contains(message.message_id)) {
                        if (TextUtils.isEmpty(message.attachment_path) && message.attachment_status.equals(Constants.FILE_EREROR) && !TextUtils.isEmpty(message.attachment_url)) {
                            if (connectedToInternet()) {
                                val intent = Intent(this, DownloadFileService::class.java)
                                intent.putExtra("message_id", "" + message.message_id)
                                startService(intent)
                            }
                        }
                    } else {
                        if (connectedToInternet()) {
                            if (!TextUtils.isEmpty(message.attachment_url)) {
                                val intent = Intent(this, DownloadFileService::class.java)
                                intent.putExtra("message_id", "" + message.message_id)
                                startService(intent)
                            }
                        }
                    }
                }
                if (!message.sender_id.equals(mCurrentUser!!.user_id)) {
                    // message has been received by me
                    if (message.message_status != Constants.STATUS_MESSAGE_SEEN) {
                        mFirebaseConfigMessages.child(message.chat_dialog_id).child(message.message_id).child("message_status").setValue(Constants.STATUS_MESSAGE_SEEN)
                        val msgHashMap = HashMap<String, Any>()
                        msgHashMap.put("message_id", message.message_id)
                        msgHashMap.put("message_status", Constants.STATUS_MESSAGE_SEEN)
                        mFirebaseConfigReadStatus.child(message.message_id).setValue(msgHashMap)
                    }
                }
            }
        }

        if (mPrivateChat!!.unread_count.get(mCurrentUser!!.user_id) != 0) {
            mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("unread_count").child(mCurrentUser!!.user_id).setValue(0)
        }
    }

    override fun initListener() {
        imgBackDefault.setOnClickListener(this)
        llViewProfile.setOnClickListener(this)
        imgOptions.setOnClickListener(this)
        imgBackOption.setOnClickListener(this)
        imgFavourite.setOnClickListener(this)
        imgDelete.setOnClickListener(this)
        imgCopy.setOnClickListener(this)
        imgAttachment.setOnClickListener(this)
        imgSendMessage.setOnClickListener(this)

        lvChatList.setOnScrollListener(object : AbsListView.OnScrollListener {

            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                // TODO Auto-generated method stub
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (lvChatList.firstVisiblePosition == 0 && mMessagesMap!!.size > 20 && !noResultsinPagination) {
                        loadMore()
                    }
                }
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int,
                                  visibleItemCount: Int, totalItemCount: Int) {
                // TODO Auto-generated method stub
            }
        })

    }

    internal fun loadMore() {
        limit = mMessagesMap!!.size + Constants.LOAD_MORE_VALUE
        val deletetime = mPrivateChat!!.delete_dialog_time.get(mUtils!!.getString("user_id", ""))
        val local = db!!.getAllMessages(mPrivateChat!!.chat_dialog_id, mCurrentUser!!.user_id, limit, deletetime!!, mOpponentUserId)
        if (local.size <= mMessagesMap!!.size) {
            // nothing new
            noResultsinPagination = true
        } else {
            noResultsinPagination = false
            val pos = mMessagesMap!!.size
            mMessagesMap = local
            makeHeaders()
            val pp = mMessagesMap!!.size - pos
            mConversationAdapter!!.animationStatus(false)
            mConversationAdapter!!.notifyDataSetChanged()
            lvChatList.setSelection(pp)
        }
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBackDefault -> {
                Constants.closeKeyboard(this, imgBackDefault)
                moveBack()
            }
            llViewProfile -> {
                val intent = Intent(mContext, OtherProfileActivity::class.java)
                intent.putExtra("otherUserId", mOpponentUserId)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            imgProfileAvatar, getString(R.string.transition_image))
                    startActivity(intent, option.toBundle())
                } else {
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                }
            }
            imgOptions -> {
                BlurBehind.getInstance().execute(mConversationActivity) {
                    val intent = Intent(this, ChatOptionsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    val messageCount = mPrivateChat!!.message_rating_count[mCurrentUser!!.user_id]!! +
                            mPrivateChat!!.message_rating_count[mOpponentUser!!.user_id]!!

                    if (messageCount < 30)
                        intent.putExtra("visibleRating", 0)
                    intent.putExtra("block_status", mPrivateChat!!.block_status.get(mCurrentUser!!.user_id))
                    startActivityForResult(intent, OPTIONS)
                    overridePendingTransition(0, 0)
                }
            }
            imgBackOption -> {
                selectedPosition = 0
                llOptionActionbar.visibility = View.GONE
                llDefaultActionbar.visibility = View.VISIBLE
                mConversationAdapter!!.remove_selection()
            }
            imgFavourite -> {
                if (connectedToInternet()) {
                    if (mMessagesMap!![msgId]!!.favourite_message[mCurrentUser!!.user_id].equals("0")) {
                        db!!.changFavouriteStatus(msgId, "1")
                        mMessagesMap!![msgId]!!.favourite_message[mCurrentUser!!.user_id] = "1"
                    } else {
                        db!!.changFavouriteStatus(msgId, "0")
                        mMessagesMap!![msgId]!!.favourite_message[mCurrentUser!!.user_id] = "0"
                    }
                    FavouriteMessageApi(this, msgId)
                } else {
                    showInternetAlert(txtName)
                }
                selectedPosition = 0
                llDefaultActionbar.visibility = View.VISIBLE
                llOptionActionbar.visibility = View.GONE
                mConversationAdapter!!.remove_selection()
            }
            imgDelete -> {
                if (connectedToInternet()) {
                    var status = 0
                    if (mMessagesMap!![msgId]!!.message_status < Constants.STATUS_MESSAGE_SEEN)
                        mFirebaseConfigMessages.child(mPrivateChat!!.chat_dialog_id).child(msgId).child("message_deleted").child(mCurrentUser!!.user_id).setValue("1")
                    if (msgId.equals(mMessageIds.get(mMessageIds.size - 1))) {
                        if (mMessageIds.size > 2) {
                            for (i in mMessageIds.size - 2 downTo 0) {
                                if (!mMessagesMap!![mMessageIds.get(i)]!!.is_header) {
                                    mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id)
                                            .child("last_message_data").child(mCurrentUser!!.user_id)
                                            .setValue(mMessagesMap!![mMessageIds.get(i)]!!.message)

                                    mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id)
                                            .child("last_message_type").child(mCurrentUser!!.user_id)
                                            .setValue(mMessagesMap!![mMessageIds.get(i)]!!.message_type)

                                    mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id)
                                            .child("last_message_time").child(mCurrentUser!!.user_id)
                                            .setValue(mMessagesMap!![mMessageIds.get(i)]!!.firebase_message_time)
                                    status = 1
                                    break
                                }
                            }
                        }
                    }
                    DeleteMessageApi(this, msgId)
                    db!!.deleteSingleMessage(msgId)
                    mMessagesMap!!.remove(msgId)
                    mMessageIds.remove(msgId)
                    limit = mMessagesMap!!.size
                    val deletetime1 = mPrivateChat!!.delete_dialog_time.get(mCurrentUser!!.user_id)
                    mMessagesMap = db!!.getAllMessages(mPrivateChat!!.chat_dialog_id, mCurrentUser!!.user_id, limit, deletetime1!!, mOpponentUserId)
                    makeHeaders()
                    if (status == 1) {
                        sendUnmatchBroadcast()
                    }
                } else {
                    showInternetAlert(txtName)
                }
                selectedPosition = 0
                llDefaultActionbar.visibility = View.VISIBLE
                llOptionActionbar.visibility = View.GONE
                mConversationAdapter!!.remove_selection()
            }
            imgCopy -> {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("simple text", mMessagesMap!![msgId]!!.message)
                clipboard.primaryClip = clip
                selectedPosition = 0
                llDefaultActionbar.visibility = View.VISIBLE
                llOptionActionbar.visibility = View.GONE
                mConversationAdapter!!.remove_selection()
                Toast.makeText(this, getString(R.string.text_copied), Toast.LENGTH_SHORT).show()
            }
            imgAttachment -> {
                if (mPrivateChat != null) {
                    if (mPrivateChat!!.block_status.get(mCurrentUser!!.user_id).equals("1")) {
                        status = 2
                        ShowUnblockPrompt()
                    } else {
                        if (connectedToInternet()) {
                            val intent = Intent(this, AttachmentSelectionActivity::class.java)
                            startActivityForResult(intent, ATTACHMENT)
                        } else
                            showInternetAlert(txtName)
                    }
                }
            }
            imgSendMessage -> {
                // Send Text Message
                if (connectedToInternet())
                    sendTextMessage()
                else
                    showInternetAlert(txtName)
            }
        }
    }

    override fun onBackPressed() {
        if (llOptionActionbar.visibility == View.VISIBLE) {
            selectedPosition = 0
            llOptionActionbar.visibility = View.GONE
            llDefaultActionbar.visibility = View.VISIBLE
            mConversationAdapter!!.remove_selection()
        } else {
            moveBack()
        }
    }

    private fun moveBack() {
        if (MainApplication.isLandingAvailable) {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        } else {
            val intent = Intent(mContext, LandingActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ATTACHMENT -> {
                    val type = data!!.getStringExtra("type")
                    when {
                        type.equals("gallery") -> showMediaDialog(2)
                        type.equals("camera") -> showMediaDialog(1)
                        type.equals("notes") -> {
                            val intent = Intent(this, MyNotesActivity::class.java)
                            startActivityForResult(intent, NOTES)
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                        }
                        type.equals("document") -> openFileChooser()
                    }
                }
                NOTES -> {
                    val noteId = data!!.getStringExtra("note_id")
                    val noteName = data.getStringExtra("note_name")
                    // Hit share note api
                    hitShareNotesAPI(noteId, noteName)
                }
                OPTIONS -> {
                    val type = data!!.getStringExtra("type")
                    when {
                        type == "search_message" -> {
                            val deletetime = mPrivateChat!!.delete_dialog_time.get(mUtils!!.getString("user_id", ""))
                            val intent = Intent(this, SearchChatMessagesActivity::class.java)
                            intent.putExtra("dialogId", mPrivateChat!!.chat_dialog_id)
                            intent.putExtra("opponentUserId", mOpponentUserId)
                            intent.putExtra("opponentUserPic", mOpponentUser!!.user_pic)
                            intent.putExtra("opponentName", txtName.text.toString())
                            intent.putExtra("deleteTime", deletetime)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                        }
                        type == "favourite_message" -> {
                            FavouriteMessageActivity.setUploadingListener(this)
                            val intent = Intent(this, FavouriteMessageActivity::class.java)
                            intent.putExtra("participantIDs", mParticpantIDS)
                            intent.putExtra("opponentUserId", mOpponentUserId)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                        }
                        type == "block" -> {
                            var status = mPrivateChat!!.block_status.get(mCurrentUser!!.user_id)
                            if (status.equals("0")) {
                                status = "1"
                                val dialog1 = AlertDialog.Builder(this)
                                dialog1.setMessage(getString(R.string.sure_to_block)).create()
                                dialog1.setPositiveButton(getString(R.string.block)) { dialog, which ->
                                    showLoader()
                                    // TODO Auto-generated method stub
                                    mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("block_status").child(mCurrentUser!!.user_id).setValue(status).addOnSuccessListener {
                                        mPrivateChat!!.block_status.put(mCurrentUser!!.user_id, status)
                                        dismissLoader()
                                        dialog.dismiss()
                                    }
                                }
                                dialog1.setNegativeButton(
                                        resources.getString(R.string.cancel), null)
                                dialog1.show()
                            } else {
                                status = "0"
                                val dialog1 = AlertDialog.Builder(this)
                                dialog1.setMessage(getString(R.string.sure_to_unblock)).create()
                                dialog1.setPositiveButton(getString(R.string.unblock)) { dialog, which ->
                                    showLoader()
                                    // TODO Auto-generated method stub
                                    mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("block_status").child(mCurrentUser!!.user_id).setValue(status).addOnSuccessListener {
                                        mPrivateChat!!.block_status.put(mCurrentUser!!.user_id, status)
                                        dismissLoader()
                                        dialog.dismiss()
                                    }
                                }
                                dialog1.setNegativeButton(
                                        resources.getString(R.string.cancel), null)
                                dialog1.show()
                            }
                        }
                        type == "clear_chat" -> {
                            val dialog1 = AlertDialog.Builder(this)
                            dialog1.setMessage(getString(R.string.sure_to_clear_chat)).create()
                            dialog1.setPositiveButton(getString(R.string.clear)) { dialog, which ->
                                // TODO Auto-generated method stub
                                showLoader()
                                clearConversation()
                                clearTime = Calendar.getInstance().timeInMillis
                                dialog.dismiss()
                            }
                            dialog1.setNegativeButton(
                                    resources.getString(R.string.cancel), null)
                            dialog1.show()
                        }
                        type == "unmatch" -> {
                            val dialog1 = AlertDialog.Builder(this)
                            dialog1.setMessage(getString(R.string.sure_to_unmatch)).create()
                            dialog1.setPositiveButton(getString(R.string.unmatch)) { dialog, which ->
                                // TODO Auto-generated method stub
                                hitUnmatchAPI()
                                dialog.dismiss()
                            }
                            dialog1.setNegativeButton(resources.getString(R.string.cancel), null)
                            dialog1.show()
                        }
                        type == "share_profile" -> {
                            val intent = Intent(this, ShareActivity::class.java)
                            intent.putExtra("path", 1)
                            intent.putExtra("postUrl", Constants.SHARE_URL + mOpponentUserId)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                        }
                        type == "report" -> {
                            val intent = Intent(this, ReportActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                        }
                        type == "rating" -> {
                            val intent = Intent(this, RatingActivity::class.java)
                            intent.putExtra("user_id", mOpponentUserId)
                            intent.putExtra("user_name", mOpponentUser!!.user_name)
                            intent.putExtra("chatDialogId", mPrivateChat!!.chat_dialog_id)
                            if (mPrivateChat!!.rating == null) {
                                intent.putExtra("status", "")
                            } else {
                                if (TextUtils.isEmpty(mPrivateChat!!.rating.get(mCurrentUser!!.user_id))
                                        || mPrivateChat!!.rating.get(mCurrentUser!!.user_id).equals("null")) {
                                    intent.putExtra("status", "")
                                } else {
                                    intent.putExtra("status", mPrivateChat!!.rating.get(mCurrentUser!!.user_id))
                                }
                            }
                            startActivity(intent)
                            overridePendingTransition(0, 0)
                        }
                    }
                }
                GALLERY -> {
                    if (data != null) {
                        try {
                            val uri = data.data
                            val cr = this.contentResolver
                            val mime = cr.getType(uri!!)
                            val path = FileUtils.getPath(this, uri)
                            if (TextUtils.isEmpty(mime)) {
                                val extenstion = path!!.substring(path.lastIndexOf("."))
                                if (extenstion.equals(".jpg", ignoreCase = true) || extenstion.equals(".jpeg", ignoreCase = true) ||
                                        extenstion.equals(".png", ignoreCase = true) || extenstion.equals(".bmp", ignoreCase = true)) {
                                    val intent = Intent(this, AttachmentActivity::class.java)
                                    intent.putExtra("select_path", path)
                                    intent.putExtra("selected_type", "image")
                                    intent.putExtra("pic", "" + mPrivateChat!!.profile_pic[mOpponentUserId])
                                    intent.putExtra("name", "" + mPrivateChat!!.name[mOpponentUserId])
                                    startActivityForResult(intent, GETIMAGE)
                                } else {
                                    val file = File(path)
                                    val length = file.length()
                                    if (length / 1024 > 1024 * 50) {//more than 300MB
                                        Toast.makeText(this, resources.getString(R.string.file_size_exceeds), Toast.LENGTH_SHORT).show()
                                        return
                                    }
                                    val intent = Intent(this, AttachVideoActivity::class.java)
                                    intent.putExtra("select_path", path)
                                    intent.putExtra("pic", "" + mPrivateChat!!.profile_pic[mOpponentUserId])
                                    intent.putExtra("name", "" + mPrivateChat!!.name[mOpponentUserId])
                                    intent.putExtra("selected_type", "video")
                                    startActivityForResult(intent, GETVIDEO)
                                }
                            } else {
                                if (mime!!.contains("image")) {
                                    //image
                                    val intent = Intent(this, AttachmentActivity::class.java)
                                    intent.putExtra("select_path", path)
                                    intent.putExtra("pic", "" + mPrivateChat!!.profile_pic[mOpponentUserId])
                                    intent.putExtra("name", "" + mPrivateChat!!.name[mOpponentUserId])
                                    intent.putExtra("selected_type", "image")
                                    startActivityForResult(intent, GETIMAGE)
                                } else {
                                    //video
                                    val file = File(path!!)
                                    val length = file.length()
                                    if (length / 1024 > 1024 * 50) {//more than 300MB
                                        Toast.makeText(this, resources.getString(R.string.file_size_exceeds), Toast.LENGTH_SHORT).show()
                                        return
                                    }

                                    val intent = Intent(this, AttachVideoActivity::class.java)
                                    intent.putExtra("select_path", path)
                                    intent.putExtra("pic", "" + mPrivateChat!!.profile_pic[mOpponentUserId])
                                    intent.putExtra("name", "" + mPrivateChat!!.name[mOpponentUserId])
                                    intent.putExtra("selected_type", "video")
                                    startActivityForResult(intent, GETVIDEO)
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                CAMERAIMAGE -> {
                    try {
                        if (camerapath != "") {
                            val file = File(camerapath)
                            refreshGallery(file)
                            val intent = Intent(this, AttachmentActivity::class.java)
                            intent.putExtra("select_path", camerapath)
                            intent.putExtra("selected_type", "image")
                            intent.putExtra("pic", "" + mPrivateChat!!.profile_pic[mOpponentUserId])
                            intent.putExtra("name", "" + mPrivateChat!!.name[mOpponentUserId])
                            startActivityForResult(intent, GETIMAGE)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CAMERAVIDEO -> {
                    try {
                        if (camerapathVideo != "") {
                            val file = File(camerapathVideo)
                            refreshGallery(file)

                            val length = file.length()
                            if (length / 1024 > 1024 * 50) {//more than 300MB
                                Toast.makeText(this, resources.getString(R.string.file_size_exceeds), Toast.LENGTH_SHORT).show()
                                return
                            }

                            val intent = Intent(this, AttachVideoActivity::class.java)
                            intent.putExtra("select_path", camerapathVideo)
                            intent.putExtra("pic", "" + mPrivateChat!!.profile_pic[mOpponentUserId])
                            intent.putExtra("name", "" + mPrivateChat!!.name[mOpponentUserId])
                            intent.putExtra("selected_type", "video")
                            startActivityForResult(intent, GETVIDEO)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                GETIMAGE -> {
                    if (mPrivateChat != null) {
                        addImageMessage(data!!.getStringExtra("selected_image"))
                        val intent = Intent(this, UploadFileService::class.java)
                        intent.putExtra("attachment_path", "" + data.getStringExtra("selected_image"))
                        startService(intent)
                    }
                }
                GETVIDEO -> {
                    if (mPrivateChat != null) {
                        addVideoMessage(data!!.getStringExtra("selected_video"), data.getStringExtra("selected_video_thumb"))
                        val intent = Intent(this, UploadFileService::class.java)
                        intent.putExtra("attachment_path", "" + data.getStringExtra("selected_video"))
                        startService(intent)
                    }
                }
                Constants.REQUEST_CODE_DOC -> {
                    val docPaths = ArrayList<String>()
                    docPaths.addAll(data!!.getStringArrayListExtra(Constants.KEY_SELECTED_DOCS))
                    docPath = docPaths.get(0)
                    if (mPrivateChat != null) {
                        addDocumentMessage(docPath)
                        val intent = Intent(this, UploadFileService::class.java)
                        intent.putExtra("attachment_path", "" + docPath)
                        startService(intent)
                    }
//                    openFile(docPath)
//                    val intent = Intent(this, DocActivity::class.java)
//                    intent.putExtra("select_path", docPath)
//                    intent.putExtra("name", txtName.getText().toString())
//                    startActivityForResult(intent, GETDOC)
                }
                GETDOC -> {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun clearConversation() {
        db!!.clearConversation(mPrivateChat!!.chat_dialog_id)
        mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("last_message_data").child(mUtils!!.getString("user_id", "")).setValue(Constants.DEFAULT_MESSAGE_REGEX)
        mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("unread_count").child(mUtils!!.getString("user_id", "")).setValue(0)
        mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("delete_dialog_time").child(mUtils!!.getString("user_id", "")).setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
            dismissLoader()
            mMessageIds.clear()
            mMessagesMap!!.clear()
            limit = 20
            val time = Constants.getUtcTime(Calendar.getInstance().timeInMillis)
            mMessagesMap = db!!.getAllMessages(mPrivateChat!!.chat_dialog_id, mCurrentUser!!.user_id, limit, time, mOpponentUserId)
            makeHeaders()
            mConversationAdapter!!.notifyDataSetChanged()
        }
    }

    private fun unMatchUser() {
        mFirebaseConfigProfile.child("id_" + mCurrentUser!!.user_id).child("chat_dialog_ids").child(mParticpantIDS).removeValue()
        mFirebaseConfigProfile.child("id_" + mOpponentUser!!.user_id).child("chat_dialog_ids").child(mParticpantIDS).removeValue()
        mFirebaseConfigChats.child(mParticpantIDS).removeValue().addOnSuccessListener {
            db!!.deleteDialogData(mPrivateChat!!.chat_dialog_id)
            sendUnmatchBroadcast()
            FirebaseListeners.getListenerClass(this).removeMessageListener(mParticpantIDS)
            FirebaseListeners.getListenerClass(this).removeChatsListener(mParticpantIDS)
            dismissLoader()
            moveBack()
        }
    }

    private fun hitUnmatchAPI() {

        showLoader()
        val call = RetrofitClient.getInstance().unmatch(mUtils!!.getString("access_token", ""),
                mOpponentUserId)
        call.enqueue(object : Callback<BaseSuccessModel> {

            override fun onResponse(call: Call<BaseSuccessModel>?, response: Response<BaseSuccessModel>) {
                if (response.body().response != null) {
                    unMatchUser()
                } else {
                    dismissLoader()
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(txtName, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<BaseSuccessModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtName, t!!.localizedMessage)
            }
        })
    }

    private fun hitShareNotesAPI(noteId: String, noteName: String) {
        showLoader()
        val call = RetrofitClient.getInstance().shareNotes(mUtils!!.getString("access_token", ""),
                noteId,
                mOpponentUserId,
                noteName)
        call.enqueue(object : Callback<NotesModel> {

            override fun onResponse(call: Call<NotesModel>?, response: Response<NotesModel>) {
                if (response.body().response != null) {
                    var mNotesData: NotesListingModel.ResponseBean? = null
                    mNotesData = response.body().response
                    sendNotesMessage(mNotesData.id.toString() + "," + mNotesData.name)
                } else {
                    dismissLoader()
                    if (response.body().error!!.code == Constants.INVALID_ACCESS_TOKEN) {
                        Toast.makeText(mContext!!, response.body().error!!.message, Toast.LENGTH_SHORT).show()
                        moveToSplash()
                    } else
                        showAlert(txtName, response.body().error!!.message!!)
                }
            }

            override fun onFailure(call: Call<NotesModel>?, t: Throwable?) {
                dismissLoader()
                showAlert(txtName, t!!.localizedMessage)
            }
        })
    }

    private fun openFileChooser() {
        val selectedPhotos = ArrayList<String>()
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(selectedPhotos)
                .enableDocSupport(true)
                .setActivityTheme(R.style.FilePickerTheme)
                .pickFile(this)
    }

    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun refreshGallery(file: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = Uri.fromFile(file)
        sendBroadcast(mediaScanIntent)
    }

    private fun showMediaDialog(flag: Int) {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.media_options_dialog)

        val dialogParms = CoordinatorLayout.LayoutParams(mWidth - (mWidth / 16), mHeight / 6)
        dialogParms.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        val bottomSheet = dialog.window.findViewById(android.support.design.R.id.design_bottom_sheet) as FrameLayout
        bottomSheet.setBackgroundResource(R.drawable.white_short_profile_background)
        bottomSheet.layoutParams = dialogParms

        val newGroupText = dialog.newGroupText
        val newChatText = dialog.newChatText

        if (flag == 1) {
            //camera
            newChatText.text = resources.getString(R.string.capture_image)
            newGroupText.text = resources.getString(R.string.capture_video)
        } else {
            //gallery
            newChatText.text = resources.getString(R.string.select_image)
            newGroupText.text = resources.getString(R.string.select_video)
        }

        newChatText.setOnClickListener {
            if (connectedToInternet()) {
                if (flag == 1) {
                    //camera
                    startCameraActivity()
                } else {
                    //gallery
                    showChooser(1)
                }
            } else
                showInternetAlert(txtName)
            dialog.dismiss()
        }

        newGroupText.setOnClickListener {
            if (connectedToInternet()) {
                if (flag == 1) {
                    //camera
                    startCameraActivityVideo()
                } else {
                    //gallery
                    showChooser(2)
                }
            } else
                showInternetAlert(txtName)
            dialog.dismiss()
        }

        dialog.show()
    }

    @SuppressLint("InlinedApi")
    private fun showChooser(flag: Int) {
        if (flag == 1) {
            //image
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, GALLERY)
        } else {
            //video
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, GALLERY)
        }
    }

    private fun startCameraActivity() {
        try {
            val c = Calendar.getInstance()
            camerapath = Environment.getExternalStorageDirectory().toString() + "/SeeASpark/Image/Sent/" + "IMG" + c.timeInMillis + ".jpg"
            val file = File(camerapath)
            file.parentFile.mkdirs()
            val intent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE)
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                val photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider", file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
            }

            startActivityForResult(intent, CAMERAIMAGE)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

    protected fun startCameraActivityVideo() {
        try {
            val c = Calendar.getInstance()
            camerapathVideo = Environment.getExternalStorageDirectory().toString() + "/SeeASpark/Video/Sent/" + "VID" + c.timeInMillis + ".mp4"
            val file = File(camerapathVideo)
            file.parentFile.mkdirs()
            val intent = Intent(
                    MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20)
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                val photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider", file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
            }

            startActivityForResult(intent, CAMERAVIDEO)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

    //// Audio message  /////////
// record button gesture
    private inner class RecordBtnGesture : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_MAX_OFF_PATH = 50
        private val SWIPE_MIN_DISTANCE = 50
        private val SWIPE_THRESHOLD_VELOCITY = 5

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                             velocityY: Float): Boolean {
            if (isRecording) {
                try {
                    if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH)
                        return false
                    // right to left swipe
                    if (e1.x - e2.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        cancelRecording = true
                    } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        cancelRecording = true
                    }
                } catch (e: Exception) {
                    // nothing
                    e.printStackTrace()
                }

            } else {
            }
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                              distanceX: Float, distanceY: Float): Boolean {
            if (isRecording) {
                try {
                    if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) {
                        cancelRecording = false
                    }
                    if (e1.x - e2.x > SWIPE_MIN_DISTANCE) {
                        cancelRecording = true

                    } else cancelRecording = e2.x - e1.x > SWIPE_MIN_DISTANCE
                } catch (e: Exception) {
                    // nothing
                    e.printStackTrace()
                }

            } else {

            }
            return false
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // TODO Auto-generated method stub
            if (checkAudioPermissions()) {
                if (edMessage.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    isRecording = false
                } else {
                    run {
                        isRecording = false
                        sendRecordBtn.animate().setInterpolator(sDecelerator)
                                .scaleX(1f).scaleY(1f)

                        timeRecordChrono.stop()
                        llRecordLayout.visibility = View.GONE
                        llTextMessageSend.visibility = View.VISIBLE
                        edMessage.requestFocus()
                        sendRecordIcon.setBackgroundResource(R.mipmap.ic_mic)
                        mediaRecorder = null
                    }

                }
            } else {
                requestAudioPermission(R.string.audio_permission)
            }
            return false
        }

        override fun onDown(e: MotionEvent): Boolean {
            if (checkAudioPermissions()) {
                x1 = e.x
                txtSlideCancel.text = resources.getString(R.string.slide_to_cancel)
                slide_arrow.visibility = View.VISIBLE
                imgDeleteRecording.clearAnimation()
                if (edMessage.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    isRecording = false
                } else {
                    sendRecordIcon.setBackgroundResource(R.mipmap.ic_mic_active)
                    try {
                        isRecording = true
                        audioLimitReached = false
                        cancelRecording = false
                        sendRecordBtn.animate().setInterpolator(sDecelerator)
                                .scaleX(1.5f).scaleY(1.5f)
                        llTextMessageSend.visibility = View.GONE
                        llRecordLayout.visibility = View.VISIBLE
                        if (mp != null) {
                            mp!!.stop()
                            mp!!.release()
                            mp = null
                        }
                        if (mpRecord != null) {
                            mpRecord!!.stop()
                            mpRecord!!.release()
                            mpRecord = null
                        }
                        val root = Environment.getExternalStorageDirectory()
                                .toString()
                        val myDir = File(root + "/SeeASpark/Audio/Sent")
                        if (!myDir.exists())
                            myDir.mkdirs()
                        val cal = Calendar.getInstance()
                        val fileName = "AUD" + cal.timeInMillis + ".m4a"
                        audioFile = File(myDir, fileName)
                        if (audioFile!!.exists()) {
                            audioFile!!.delete()
                        }
                        timeRecordChrono.base = SystemClock.elapsedRealtime()
                        timeRecordChrono.start()
                        mediaRecorder = null
                        mediaRecorder = MediaRecorder()
                        mediaRecorder!!.setAudioChannels(1)
                        mediaRecorder!!.setAudioSamplingRate(44100)
                        mediaRecorder!!.setAudioEncodingBitRate(64000)
                        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                        mediaRecorder!!.setOutputFile(audioFile!!.absolutePath)
                        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                        mediaRecorder!!.prepare()
                        if (mUtils!!.getBoolean("conversation_sound", true)) {
                            mpRecord = MediaPlayer.create(mContext, R.raw.start_record)
                            mpRecord!!.start()
                            mpRecord!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
                                // TODO Auto-generated method stub
                                if (mediaRecorder != null) {
                                    try {
//                                    mAdapter.remocecall()
                                        mediaRecorder!!.start()
                                        stopRecord.postDelayed(stop_rec, 60000)
                                    } catch (e: Exception) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace()
                                        mediaRecorder!!.release()
                                        mediaRecorder = null
                                        timeRecordChrono.stop()
                                        timeRecordChrono.text = ""
                                        sendRecordBtn.animate().setInterpolator(mOvershooter)
                                                .scaleX(1f).scaleY(1f)
                                        imgDeleteRecording.startAnimation(recordCancelAnim)
                                        sendRecordIcon.setBackgroundResource(R.mipmap.ic_mic)
                                        edMessage.requestFocus()
                                        isRecording = false
                                    }

                                }
                            })
                        }

                    } catch (ex: Exception) {
                        isRecording = false
                        ex.printStackTrace()
                    }

                }
                return true
            } else {
                requestAudioPermission(R.string.audio_permission)
                return false
            }
        }
    }

    internal var stop_rec: Runnable = Runnable {
        // TODO Auto-generated method stub
        try {
            txtSlideCancel.text = resources.getString(R.string.time_limit_reached)
            if (mediaRecorder != null) {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                mediaRecorder = null
            }
            timeRecordChrono.stop()
            val time = SystemClock.elapsedRealtime() - timeRecordChrono
                    .base
            showAudioTime = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(time),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES
                            .toSeconds(TimeUnit.MILLISECONDS
                                    .toMinutes(time)))
            audioLimitReached = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var listAudioPermissionsNeeded = ArrayList<String>()
    internal var audioPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
    private val AUDIO_PERMISSION_REQUEST_CODE = 15
    fun checkAudioPermissions(): Boolean {
        var result: Int
        listAudioPermissionsNeeded = ArrayList<String>()
        for (p in audioPermissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listAudioPermissionsNeeded.add(p)
            }
        }
        if (!listAudioPermissionsNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(this, listAudioPermissionsNeeded.toTypedArray(), AUDIO_PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    fun requestAudioPermission(text: Int) {
        if (hasPermissions(this, listAudioPermissionsNeeded)) {
            mUtils!!.setBoolean(Constants.AUDIO_PERMISSION, false)
            showSnackbar(text,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this, listAudioPermissionsNeeded
                        .toTypedArray(), AUDIO_PERMISSION_REQUEST_CODE)
            })
        } else {
            if (!mUtils!!.getBoolean(Constants.AUDIO_PERMISSION, false))
                ActivityCompat.requestPermissions(this, listAudioPermissionsNeeded
                        .toTypedArray(), AUDIO_PERMISSION_REQUEST_CODE)
            if (mUtils!!.getBoolean(Constants.AUDIO_PERMISSION, false)) {
                showSnackbar(text,
                        android.R.string.ok, View.OnClickListener {
                    // Request permission
                    mUtils!!.setBoolean(Constants.AUDIO_PERMISSION, false)
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    intent.data = Uri.parse("package:" + packageName)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(intent)
                })
            }
            mUtils!!.setBoolean(Constants.AUDIO_PERMISSION, true)
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

/////////////////////////////

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            AUDIO_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size == 3) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    }
                } else if (grantResults.size == 2) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    }
                } else if (grantResults.size == 1) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                return
            }
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size == 2) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    }
                } else if (grantResults.size == 1) {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun openFullViewActivity() {
        if (checkGalleryPermissions()) {
            var intent = Intent(this, FullViewActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else {
            requestGalleryPermission(R.string.gallery_permission)
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
                    intent.data = Uri.parse("package:" + packageName)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(intent)
                })
            }
            mUtils!!.setBoolean(Constants.GALLERY_PERMISSION, true)
        }
    }

    internal fun openFile(pathh: String?) {
        if (pathh != null) {
            val file = File(pathh)
            val filename = file.name
            if (filename.endsWith(".txt") || filename.endsWith(".doc") || filename.endsWith(".DOC") || filename.endsWith(".docx") || filename.endsWith(".DOCX")) {
                if (filename.endsWith(".txt")) {
                    println("---txt/doc file exists")
                    val path = Uri.fromFile(file)
                    val intent1 = Intent(Intent.ACTION_VIEW)
                    intent1.setDataAndType(path, "text/html")//---->application/msword
                    startActivity(intent1)
                } else {
                    println("---Its a doc file")
                    val path = Uri.fromFile(file)
                    val intent1 = Intent(Intent.ACTION_VIEW)
                    intent1.setDataAndType(path, "application/*")//---->application/msword
                    startActivity(intent1)

                }
            } else {
                if (filename.endsWith(".pdf")) {
                    println("---pdf file exists")

                    val path = Uri.fromFile(file)
                    val intent1 = Intent(Intent.ACTION_VIEW)
                    intent1.setDataAndType(path, "application/pdf")
                    intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    try {
                        startActivity(intent1)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(this,
                                "No Application Available to View PDF",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (usersQuery != null) {
            usersQuery!!.removeEventListener(mOpponentUserListener)
        }
        FirebaseListeners.setChatDialogListenerForChat(null, "")
        FirebaseListeners.setMessageListener(null, "")
//        if (ActivityGroupChat.mContext == null) {
//            DownloadGifReceived.setListener(null)
//            DownloadImageReceived.setListener(null)
//            MainActivity.setmChatDataChangedListenerForChat(null)
//        }
//        mOnlineHandler.removeCallbacks(mOnlineRunnable)
//        if (ActivityIncognitoChat.mContext == null) {
//            GetOnline.setOnlineCallback(null)
//        }
//        mContext = null
//        chatDialogId = ""
    }

    fun sendTextMessage() {
        if (edMessage.text.toString().trim().length > 0) {
            if (mPrivateChat != null) {
                // 0 for send message, 1 for send lacal, 2 for unblock alert
                if (mPrivateChat!!.block_status.get(mCurrentUser!!.user_id).equals("1")) {
                    status = 2
                    ShowUnblockPrompt()
                } else if (mPrivateChat!!.block_status.get(mOpponentUserId).equals("1")) {
                    status = 1
                } else {
                    status = 0
                }
                if (llDefaultActionbar.visibility != View.VISIBLE) {
                    selectedPosition = 0
                    llDefaultActionbar.visibility = View.VISIBLE
                    llOptionActionbar.visibility = View.GONE
                    mConversationAdapter!!.remove_selection()
                }
                if (status != 2) {
                    val mMessage = MessagesModel()
                    val messagePush = mFirebaseConfigMessages.push()
                    mMessage.message_id = messagePush.key
                    mMessage.message = edMessage.text.toString().trim()
                    mMessage.message_type = Constants.TYPE_TEXT
                    mMessage.message_time = "" + (Calendar.getInstance()).timeInMillis
                    mMessage.firebase_message_time = (Calendar.getInstance()).timeInMillis
                    mMessage.chat_dialog_id = mPrivateChat!!.chat_dialog_id
                    mMessage.sender_id = mCurrentUser!!.user_id
                    mMessage.message_status = Constants.STATUS_MESSAGE_SENT
                    mMessage.attachment_url = ""
                    mMessage.receiver_id = mOpponentUserId

                    val delete = HashMap<String, String>()
                    delete.put(mCurrentUser!!.user_id, "0")
                    if (status == 1) {
                        delete.put(mOpponentUserId, "1")
                    } else {
                        delete.put(mOpponentUserId, "0")
                    }
                    mMessage.message_deleted = delete

                    val favourite = HashMap<String, String>()
                    favourite.put(mCurrentUser!!.user_id, "0")
                    favourite.put(mOpponentUserId, "0")
                    mMessage.favourite_message = favourite

                    val msgHashMap = HashMap<String, Any>()
                    msgHashMap.put("message_id", mMessage.message_id)
                    msgHashMap.put("message", mMessage.message)
                    msgHashMap.put("message_type", mMessage.message_type)
                    msgHashMap.put("firebase_message_time", ServerValue.TIMESTAMP)
                    msgHashMap.put("chat_dialog_id", mMessage.chat_dialog_id)
                    msgHashMap.put("sender_id", mMessage.sender_id)
                    msgHashMap.put("message_status", mMessage.message_status)
                    msgHashMap.put("attachment_url", mMessage.attachment_url)
                    msgHashMap.put("message_deleted", mMessage.message_deleted)
                    msgHashMap.put("favourite_message", mMessage.favourite_message)
                    msgHashMap.put("receiver_id", mMessage.receiver_id)
                    msgHashMap.put("sender_name", mUtils!!.getString("user_name", ""))

                    if (edMessage.text.toString().trim().length > Constants.TEXT_LENGTH) {
                        val dialog1 = AlertDialog.Builder(this)
                        dialog1.setMessage(getString(R.string.character_limit_message) + " " + Constants.TEXT_LENGTH + " " + getString(R.string.character_sent)).create()
                        dialog1.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                            // TODO Auto-generated method stub
                            if (mp != null) {
                                mp!!.stop()
                                mp!!.release()
                                mp = null
                            }
//                            mp = MediaPlayer.create(this, R.raw.message_sent)
//                            mp!!.start()

                            mMessage.message = mMessage.message.substring(0, Constants.TEXT_LENGTH)
                            db!!.addMessage(mMessage, mCurrentUser!!.user_id)
                            addMessage(mMessage)

                            val time = Constants.getUtcTime((Calendar.getInstance()).timeInMillis)
                            msgHashMap.put("message", mMessage.message)
                            msgHashMap.put("message_time", "" + time)

                            mFirebaseConfigMessages.child(mPrivateChat!!.chat_dialog_id).child(mMessage.message_id).setValue(msgHashMap)
                            edMessage.setText("")
                            dialog.dismiss()
                        }
                        dialog1.setNegativeButton(
                                resources.getString(R.string.no), null)
                        dialog1.show()
                    } else {
                        if (mp != null) {
                            mp!!.stop()
                            mp!!.release()
                            mp = null
                        }
//                        mp = MediaPlayer.create(this, R.raw.message_sent)
//                        mp!!.start()

                        db!!.addMessage(mMessage, mCurrentUser!!.user_id)
                        addMessage(mMessage)

                        val time = Constants.getUtcTime((Calendar.getInstance()).timeInMillis)
                        msgHashMap.put("message_time", "" + time)

                        mFirebaseConfigMessages.child(mPrivateChat!!.chat_dialog_id).child(mMessage.message_id).setValue(msgHashMap)
                        edMessage.setText("")
                    }
                    if (status == 0)
                        mFirebaseConfigNotifications.child(mMessage.message_id).setValue(msgHashMap)

                }
            }
        }
    }

    fun sendNotesMessage(msg: String) {
        if (mPrivateChat != null) {
            // 0 for send message, 1 for send lacal, 2 for unblock alert
            if (mPrivateChat!!.block_status.get(mCurrentUser!!.user_id).equals("1")) {
                status = 2
                ShowUnblockPrompt()
            } else if (mPrivateChat!!.block_status.get(mOpponentUserId).equals("1")) {
                status = 1
            } else {
                status = 0
            }
            if (llDefaultActionbar.visibility != View.VISIBLE) {
                selectedPosition = 0
                llDefaultActionbar.visibility = View.VISIBLE
                llOptionActionbar.visibility = View.GONE
                mConversationAdapter!!.remove_selection()
            }
            if (status != 2) {
                val mMessage = MessagesModel()
                val messagePush = mFirebaseConfigMessages.push()
                mMessage.message_id = messagePush.key
                mMessage.message = msg
                mMessage.message_type = Constants.TYPE_NOTES
                mMessage.message_time = "" + (Calendar.getInstance()).timeInMillis
                mMessage.firebase_message_time = (Calendar.getInstance()).timeInMillis
                mMessage.chat_dialog_id = mPrivateChat!!.chat_dialog_id
                mMessage.sender_id = mCurrentUser!!.user_id
                mMessage.message_status = Constants.STATUS_MESSAGE_SENT
                mMessage.attachment_url = ""
                mMessage.receiver_id = mOpponentUserId

                val delete = HashMap<String, String>()
                delete.put(mCurrentUser!!.user_id, "0")
                if (status == 1) {
                    delete.put(mOpponentUserId, "1")
                } else {
                    delete.put(mOpponentUserId, "0")
                }
                mMessage.message_deleted = delete

                val favourite = HashMap<String, String>()
                favourite.put(mCurrentUser!!.user_id, "0")
                favourite.put(mOpponentUserId, "0")
                mMessage.favourite_message = favourite

                val msgHashMap = HashMap<String, Any>()
                msgHashMap.put("message_id", mMessage.message_id)
                msgHashMap.put("message", mMessage.message)
                msgHashMap.put("message_type", mMessage.message_type)
                msgHashMap.put("firebase_message_time", ServerValue.TIMESTAMP)
                msgHashMap.put("chat_dialog_id", mMessage.chat_dialog_id)
                msgHashMap.put("sender_id", mMessage.sender_id)
                msgHashMap.put("message_status", mMessage.message_status)
                msgHashMap.put("attachment_url", mMessage.attachment_url)
                msgHashMap.put("message_deleted", mMessage.message_deleted)
                msgHashMap.put("favourite_message", mMessage.favourite_message)
                msgHashMap.put("receiver_id", mMessage.receiver_id)
                msgHashMap.put("sender_name", mUtils!!.getString("user_name", ""))

                if (mp != null) {
                    mp!!.stop()
                    mp!!.release()
                    mp = null
                }
//                mp = MediaPlayer.create(this, R.raw.message_sent)
//                mp!!.start()
                db!!.addMessage(mMessage, mCurrentUser!!.user_id)
                addMessage(mMessage)
                val time = Constants.getUtcTime((Calendar.getInstance()).timeInMillis)
                msgHashMap.put("message_time", "" + time)
                mFirebaseConfigMessages.child(mPrivateChat!!.chat_dialog_id).child(mMessage.message_id).setValue(msgHashMap)

                if (status == 0)
                    mFirebaseConfigNotifications.child(mMessage.message_id).setValue(msgHashMap)

                dismissLoader()
            }
        }
    }

    fun addImageMessage(imagePath: String) {

        if (mPrivateChat != null) {

            if (mPrivateChat!!.block_status.get(mCurrentUser!!.user_id).equals("1")) {
                status = 2
                ShowUnblockPrompt()
            } else if (mPrivateChat!!.block_status.get(mOpponentUserId).equals("1")) {
                status = 1
            } else {
                status = 0
            }
            if (llDefaultActionbar.visibility != View.VISIBLE) {
                selectedPosition = 0
                llDefaultActionbar.visibility = View.VISIBLE
                llOptionActionbar.visibility = View.GONE
                mConversationAdapter!!.remove_selection()
            }
            if (status != 2) {
                val mMessage = MessagesModel()
                val messagePush = mFirebaseConfigMessages.push()
                mMessage.message_id = messagePush.key
                mMessage.message = "Image"
                mMessage.message_type = Constants.TYPE_IMAGE
                mMessage.message_time = "" + (Calendar.getInstance()).timeInMillis
                mMessage.firebase_message_time = Constants.getUtcTime((Calendar.getInstance()).timeInMillis)
                mMessage.chat_dialog_id = mPrivateChat!!.chat_dialog_id
                mMessage.sender_id = mCurrentUser!!.user_id
                mMessage.message_status = Constants.STATUS_MESSAGE_PENDING
                mMessage.attachment_url = ""
                mMessage.receiver_id = mOpponentUserId

                val delete = HashMap<String, String>()
                delete.put(mCurrentUser!!.user_id, "0")
                if (status == 1) {
                    delete.put(mOpponentUserId, "1")
                } else {
                    delete.put(mOpponentUserId, "0")
                }
                mMessage.message_deleted = delete

                val favourite = HashMap<String, String>()
                favourite.put(mCurrentUser!!.user_id, "0")
                favourite.put(mOpponentUserId, "0")
                mMessage.favourite_message = favourite

                mMessage.attachment_path = imagePath
                mMessage.attachment_status = Constants.FILE_EREROR
                mMessage.attachment_progress = "0"

                if (mp != null) {
                    mp!!.stop()
                    mp!!.release()
                    mp = null
                }
//                mp = MediaPlayer.create(this, R.raw.message_sent)
//                mp!!.start()

                db!!.addMessage(mMessage, mCurrentUser!!.user_id)
                addMessage(mMessage)

            }
        }
    }

    fun addVideoMessage(videoPath: String, videoThumb: String) {

        if (mPrivateChat != null) {

            if (mPrivateChat!!.block_status.get(mCurrentUser!!.user_id).equals("1")) {
                status = 2
                ShowUnblockPrompt()
            } else if (mPrivateChat!!.block_status.get(mOpponentUserId).equals("1")) {
                status = 1
            } else {
                status = 0
            }
            if (llDefaultActionbar.visibility != View.VISIBLE) {
                selectedPosition = 0
                llDefaultActionbar.visibility = View.VISIBLE
                llOptionActionbar.visibility = View.GONE
                mConversationAdapter!!.remove_selection()
            }
            if (status != 2) {
                val mMessage = MessagesModel()
                val messagePush = mFirebaseConfigMessages.push()
                mMessage.message_id = messagePush.key
                mMessage.message = "Video"
                mMessage.message_type = Constants.TYPE_VIDEO
                mMessage.message_time = "" + (Calendar.getInstance()).timeInMillis
                mMessage.firebase_message_time = Constants.getUtcTime((Calendar.getInstance()).timeInMillis)
                mMessage.chat_dialog_id = mPrivateChat!!.chat_dialog_id
                mMessage.sender_id = mCurrentUser!!.user_id
                mMessage.message_status = Constants.STATUS_MESSAGE_PENDING
                mMessage.attachment_url = ""
                mMessage.receiver_id = mOpponentUserId

                val delete = HashMap<String, String>()
                delete.put(mCurrentUser!!.user_id, "0")
                if (status == 1) {
                    delete.put(mOpponentUserId, "1")
                } else {
                    delete.put(mOpponentUserId, "0")
                }
                mMessage.message_deleted = delete

                val favourite = HashMap<String, String>()
                favourite.put(mCurrentUser!!.user_id, "0")
                favourite.put(mOpponentUserId, "0")
                mMessage.favourite_message = favourite

                mMessage.attachment_path = videoPath
                mMessage.attachment_status = Constants.FILE_EREROR
                mMessage.attachment_progress = "0"
                mMessage.custom_data = videoThumb

                if (mp != null) {
                    mp!!.stop()
                    mp!!.release()
                    mp = null
                }
//                mp = MediaPlayer.create(this, R.raw.message_sent)
//                mp!!.start()

                db!!.addMessage(mMessage, mCurrentUser!!.user_id)
                addMessage(mMessage)

            }
        }
    }

    fun addDocumentMessage(videoPath: String) {

        if (mPrivateChat != null) {

            if (mPrivateChat!!.block_status.get(mCurrentUser!!.user_id).equals("1")) {
                status = 2
                ShowUnblockPrompt()
            } else if (mPrivateChat!!.block_status.get(mOpponentUserId).equals("1")) {
                status = 1
            } else {
                status = 0
            }
            if (llDefaultActionbar.visibility != View.VISIBLE) {
                selectedPosition = 0
                llDefaultActionbar.visibility = View.VISIBLE
                llOptionActionbar.visibility = View.GONE
                mConversationAdapter!!.remove_selection()
            }
            if (status != 2) {
                val mMessage = MessagesModel()
                val messagePush = mFirebaseConfigMessages.push()
                mMessage.message_id = messagePush.key
                mMessage.message = "Document"
                mMessage.message_type = Constants.TYPE_DOCUMENT
                mMessage.message_time = "" + (Calendar.getInstance()).timeInMillis
                mMessage.firebase_message_time = Constants.getUtcTime((Calendar.getInstance()).timeInMillis)
                mMessage.chat_dialog_id = mPrivateChat!!.chat_dialog_id
                mMessage.sender_id = mCurrentUser!!.user_id
                mMessage.message_status = Constants.STATUS_MESSAGE_PENDING
                mMessage.attachment_url = ""
                mMessage.receiver_id = mOpponentUserId

                val delete = HashMap<String, String>()
                delete.put(mCurrentUser!!.user_id, "0")
                if (status == 1) {
                    delete.put(mOpponentUserId, "1")
                } else {
                    delete.put(mOpponentUserId, "0")
                }
                mMessage.message_deleted = delete

                val favourite = HashMap<String, String>()
                favourite.put(mCurrentUser!!.user_id, "0")
                favourite.put(mOpponentUserId, "0")
                mMessage.favourite_message = favourite

                mMessage.attachment_path = videoPath
                mMessage.attachment_status = Constants.FILE_EREROR
                mMessage.attachment_progress = "0"

                if (mp != null) {
                    mp!!.stop()
                    mp!!.release()
                    mp = null
                }
//                mp = MediaPlayer.create(this, R.raw.message_sent)
//                mp!!.start()

                db!!.addMessage(mMessage, mCurrentUser!!.user_id)
                addMessage(mMessage)

            }
        }
    }

    fun addAudioMessage(audioPath: String, time: String) {

        if (mPrivateChat != null) {

            if (mPrivateChat!!.block_status.get(mCurrentUser!!.user_id).equals("1")) {
                status = 2
                ShowUnblockPrompt()
            } else if (mPrivateChat!!.block_status.get(mOpponentUserId).equals("1")) {
                status = 1
            } else {
                status = 0
            }
            if (llDefaultActionbar.visibility != View.VISIBLE) {
                selectedPosition = 0
                llDefaultActionbar.visibility = View.VISIBLE
                llOptionActionbar.visibility = View.GONE
                mConversationAdapter!!.remove_selection()
            }

            if (status != 2) {
                val mMessage = MessagesModel()
                val messagePush = mFirebaseConfigMessages.push()
                mMessage.message_id = messagePush.key
                mMessage.message = time
                mMessage.message_type = Constants.TYPE_AUDIO
                mMessage.message_time = "" + (Calendar.getInstance()).timeInMillis
                mMessage.firebase_message_time = Constants.getUtcTime((Calendar.getInstance()).timeInMillis)
                mMessage.chat_dialog_id = mPrivateChat!!.chat_dialog_id
                mMessage.sender_id = mCurrentUser!!.user_id
                mMessage.message_status = Constants.STATUS_MESSAGE_PENDING
                mMessage.attachment_url = ""
                mMessage.receiver_id = mOpponentUserId

                val delete = HashMap<String, String>()
                delete.put(mCurrentUser!!.user_id, "0")
                if (status == 1) {
                    delete.put(mOpponentUserId, "1")
                } else {
                    delete.put(mOpponentUserId, "0")
                }
                mMessage.message_deleted = delete

                val favourite = HashMap<String, String>()
                favourite.put(mCurrentUser!!.user_id, "0")
                favourite.put(mOpponentUserId, "0")
                mMessage.favourite_message = favourite

                mMessage.attachment_path = audioPath
                mMessage.attachment_status = Constants.FILE_EREROR
                mMessage.attachment_progress = "0"

                if (mp != null) {
                    mp!!.stop()
                    mp!!.release()
                    mp = null
                }
//                mp = MediaPlayer.create(this, R.raw.message_sent)
//                mp!!.start()

                db!!.addMessage(mMessage, mCurrentUser!!.user_id)
                addMessage(mMessage)

            }
        }
    }

    internal fun ShowUnblockPrompt() {
        val dialog1 = AlertDialog.Builder(this)
        dialog1.setMessage(getString(R.string.unblock_message)).create()
        dialog1.setPositiveButton(getString(R.string.unblock_small)) { dialog, which ->
            showLoader()
            // TODO Auto-generated method stub
            mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("block_status").child(mCurrentUser!!.user_id).setValue("0").addOnSuccessListener {
                mPrivateChat!!.block_status.put(mCurrentUser!!.user_id, "0")
                dismissLoader()
                dialog.dismiss()
            }
        }
        dialog1.setNegativeButton(
                resources.getString(R.string.cancel), null)
        dialog1.show()
    }

    override fun onMessageAdd(message: MessagesModel?) {
        if (message != null) {
            var lastMessageTime: Long
            var newMessageTime: Long
            try {
                newMessageTime = message.firebase_message_time
            } catch (e: Exception) {
                newMessageTime = Calendar.getInstance().timeInMillis
            }

            try {
                lastMessageTime = mPrivateChat!!.last_message_time.get(mCurrentUser!!.user_id)!!
            } catch (e: Exception) {
                lastMessageTime = newMessageTime - 1
            }

            if (!message.sender_id.equals(mCurrentUser!!.user_id) && !message.is_header && (message.message_type.equals(Constants.TYPE_IMAGE) ||
                            message.message_type.equals(Constants.TYPE_AUDIO) || message.message_type.equals(Constants.TYPE_VIDEO) ||
                            message.message_type.equals(Constants.TYPE_DOCUMENT))) {
                if (mMessageIds.contains(message.message_id)) {
                    if (TextUtils.isEmpty(message.attachment_path) && (TextUtils.isEmpty(message.attachment_status) || message.attachment_status.equals(Constants.FILE_EREROR))) {
                        if (connectedToInternet()) {
                            if (!TextUtils.isEmpty(message.attachment_url)) {
                                val intent = Intent(this, DownloadFileService::class.java)
                                intent.putExtra("message_id", "" + message.message_id)
                                startService(intent)
                            }
                        }
                    }
                } else {
                    if (connectedToInternet()) {
                        if (!TextUtils.isEmpty(message.attachment_url)) {
                            val intent = Intent(this, DownloadFileService::class.java)
                            intent.putExtra("message_id", "" + message.message_id)
                            startService(intent)
                        }
                    }
                }
            }

            if (lastMessageTime < newMessageTime && message.sender_id.equals(mCurrentUser!!.user_id)) {
                // last message in chat dialog is less than current message
                val mChatUpdate = HashMap<String, Any>()
                mChatUpdate.put("last_message", message.message)

                val lastTime = HashMap<String, Long>()
                lastTime.put(mCurrentUser!!.user_id, message.firebase_message_time)
                lastTime.put(mOpponentUserId, message.firebase_message_time)

                mChatUpdate.put("last_message_time", lastTime)
                mChatUpdate.put("last_message_sender_id", message.sender_id)
                mChatUpdate.put("last_message_id", message.message_id)

                val lastMessage = HashMap<String, String>()
                lastMessage.put(mCurrentUser!!.user_id, message.message)
                lastMessage.put(mOpponentUserId, message.message)
                mChatUpdate.put("last_message_data", lastMessage)

                val lastMessageType = HashMap<String, String>()
                lastMessageType.put(mCurrentUser!!.user_id, message.message_type)
                lastMessageType.put(mOpponentUserId, message.message_type)
                mChatUpdate.put("last_message_type", lastMessageType)

                val totalMessageCount = mPrivateChat!!.message_rating_count.get(mCurrentUser!!.user_id)!! +
                        mPrivateChat!!.message_rating_count.get(mOpponentUser!!.user_id)!!

                if (totalMessageCount < 30) {

                    var ownMessageCount = mPrivateChat!!.message_rating_count.get(mCurrentUser!!.user_id)!!
                    ownMessageCount++

                    mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("message_rating_count")
                            .child(mCurrentUser!!.user_id).setValue(ownMessageCount)
                }

                val unread = mPrivateChat!!.unread_count
                if (unread != null && unread.containsKey(mOpponentUserId)) {
                    var lastUnreadCount: Int = unread.get(mOpponentUserId)!!
                    if (mPrivateChat!!.block_status.get(mOpponentUserId).equals("0")) {
                        lastUnreadCount++
                    }
                    unread.put(mOpponentUserId, lastUnreadCount)
                    unread.put(mCurrentUser!!.user_id, 0)
                    mChatUpdate.put("unread_count", unread)
                }
                mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id)
                        .updateChildren(mChatUpdate)

                displayRatingDialog()
            }

            if (mPrivateChat!!.chat_dialog_id.equals(message.chat_dialog_id)) {
                if (mUtils!!.getInt("Background", 0) == 1) {
                    // Add message in list
                    setReadMessages.add(message)
                } else {
                    if (!message.sender_id.equals(mCurrentUser!!.user_id)) {
                        // message has been received by me
                        if (message.message_status != Constants.STATUS_MESSAGE_SEEN) {
                            mFirebaseConfigMessages.child(mPrivateChat!!.chat_dialog_id).child(message.message_id).child("message_status").setValue(Constants.STATUS_MESSAGE_SEEN)
                            val msgHashMap = HashMap<String, Any>()
                            msgHashMap.put("message_id", message.message_id)
                            msgHashMap.put("message_status", Constants.STATUS_MESSAGE_SEEN)
                            mFirebaseConfigReadStatus.child(message.message_id).setValue(msgHashMap)
                        }
                    }
                }
            }

            if (lastMessageTime < newMessageTime && message.sender_id.equals(mOpponentUserId)) {
                mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("unread_count").child(mCurrentUser!!.user_id).setValue(0)
            }

//        if (!mMessageIds.contains(message!!.message_id)) {
//            if (mp != null) {
//                mp!!.stop()
//                mp!!.release()
//                mp = null
//            }
//            mp = MediaPlayer.create(applicationContext, R.raw.message_received)
//            mp!!.start()
//        }
            createHeader(message)
        }
    }

    override fun onMessageChanged(message: MessagesModel?) {
        if (message != null) {
            if (mPrivateChat!!.chat_dialog_id.equals(message.chat_dialog_id)) {
                if (mMessageIds.contains(message.message_id)) { // already
                    mMessagesMap!!.put(message.message_id, message)
                }
                mConversationAdapter!!.animationStatus(false)
                mConversationAdapter!!.notifyDataSetChanged()
                if (message.sender_id != mCurrentUser!!.user_id) {
                    if (message.message_status != Constants.STATUS_MESSAGE_SEEN) {
                        mFirebaseConfigMessages.child(message.chat_dialog_id).child(message.message_id).child("message_status").setValue(Constants.STATUS_MESSAGE_SEEN)
                        val msgHashMap = HashMap<String, Any>()
                        msgHashMap.put("message_id", message.message_id)
                        msgHashMap.put("message_status", Constants.STATUS_MESSAGE_SEEN)
                        mFirebaseConfigReadStatus.child(message.message_id).setValue(msgHashMap)
                    }
                }
            }
        }
    }

    internal fun createHeader(message: MessagesModel) {
        var lastMessageDate = ""
        var newMessageDate = ""
        val calNew = Calendar.getInstance()
        try {
            val timeinMillis1 = java.lang.Long.parseLong(message.message_time)
            calNew.timeInMillis = timeinMillis1
        } catch (e: Exception) {
            e.printStackTrace()
            calNew.timeInMillis = calNew.timeInMillis
        }
        newMessageDate = chat_date_format.format(calNew.time)

        if (mMessageIds.contains(message.message_id)) { // already
            mMessagesMap!!.put(message.message_id, message)
        } else {
            if (mMessageIds.size > 0) {
                val calDb = Calendar.getInstance()
                val timeinMillis: Long
                try {
                    timeinMillis = java.lang.Long.parseLong(mMessagesMap!![mMessageIds[mMessageIds.size - 1]]!!.message_time)
                    calDb.timeInMillis = timeinMillis
                } catch (e: Exception) {
                    e.printStackTrace()
                    calDb.timeInMillis = calDb.timeInMillis
                }

                lastMessageDate = chat_date_format.format(calDb.time)
            }
            if (!mMessageIds.contains(message.message_id)) {
                var state = 0
                if (message.sender_id.equals(mCurrentUser!!.user_id)) {
                    var newMessageTime: Long
                    try {
                        newMessageTime = message.message_time.toLong()
                    } catch (e: Exception) {
                        newMessageTime = Calendar.getInstance().timeInMillis
                    }

                    if (clearTime > newMessageTime) {
                        state = 1
                    }
                }

                if (state == 0) {
                    val cal = Calendar.getInstance()
                    val today = chat_date_format.format(cal.time)

                    val cal1 = Calendar.getInstance()
                    cal1.add(Calendar.DATE, -1)
                    val yesterday = chat_date_format.format(cal1.time)

                    if (!TextUtils.equals(lastMessageDate, newMessageDate)) {
                        val mMessage = MessagesModel()
                        mMessage.is_header = true
                        if (newMessageDate == today) {
                            mMessage.show_header_text = "Today"
                            mMessage.show_message_datetime = today_date_format.format(calNew.time)
                        } else if (newMessageDate == yesterday) {
                            mMessage.show_header_text = "Yesterday"
                            mMessage.show_message_datetime = today_date_format.format(calNew.time)
                        } else {
                            mMessage.show_header_text = show_dateheader_format.format(calNew.time)
                            mMessage.show_message_datetime = today_date_format.format(calNew.time)
                        }
                        mMessagesMap!!.put(newMessageDate, mMessage)
                        mMessageIds.add(newMessageDate)
                    }
                    mMessagesMap!!.put(message.message_id, message)
                    mMessageIds.add(message.message_id)
                }
            }
        }
        mConversationAdapter!!.animationStatus(true)
        mConversationAdapter!!.notifyDataSetChanged()
    }

    internal fun addMessage(mMessage: MessagesModel) {
        var lastMessageDate = ""
        var newMessageDate = ""
        val calNew = Calendar.getInstance()
        try {
            val timeinMillis1 = java.lang.Long.parseLong(mMessage.message_time)
            calNew.timeInMillis = timeinMillis1
        } catch (e: Exception) {
            e.printStackTrace()
            calNew.timeInMillis = calNew.timeInMillis
        }

        newMessageDate = chat_date_format.format(calNew.time)
        if (mMessageIds.size > 0) {
            val calDb = Calendar.getInstance()
            val timeinMillis: Long
            try {
                timeinMillis = java.lang.Long.parseLong(mMessagesMap!![mMessageIds[mMessageIds.size - 1]]!!.message_time)
                calDb.timeInMillis = timeinMillis
            } catch (e: Exception) {
                e.printStackTrace()
                calDb.timeInMillis = calDb.timeInMillis
            }
            lastMessageDate = chat_date_format.format(calDb.time)
        }

        val cal = Calendar.getInstance()
        val today = chat_date_format.format(cal.time)

        val cal1 = Calendar.getInstance()
        cal1.add(Calendar.DATE, -1)
        val yesterday = chat_date_format.format(cal1.time)

        if (!TextUtils.equals(lastMessageDate, newMessageDate)) {
            val mMessageLocal = MessagesModel()
            mMessageLocal.is_header = true
            if (newMessageDate == today) {
                mMessageLocal.show_header_text = "Today"
                mMessageLocal.show_message_datetime = today_date_format.format(calNew.time)
            } else if (newMessageDate == yesterday) {
                mMessageLocal.show_header_text = "Yesterday"
                mMessageLocal.show_message_datetime = today_date_format.format(calNew.time)
            } else {
                mMessageLocal.show_header_text = show_dateheader_format.format(calNew.time)
                mMessageLocal.show_message_datetime = today_date_format.format(calNew.time)
            }
            mMessagesMap!!.put(newMessageDate, mMessageLocal)
            mMessageIds.add(newMessageDate)
        }
        mMessage.is_header = false
        mMessage.show_message_datetime = today_date_format.format(calNew.time)

        mMessageIds.add(mMessage.message_id)
        mMessagesMap!!.put(mMessage.message_id, mMessage)
        mConversationAdapter!!.animationStatus(true)
        mConversationAdapter!!.notifyDataSetChanged()

        lvChatList.setSelection(lvChatList.count - 1)
    }

    override fun onChange(message_id: String) {
        mMessagesMap!![message_id]!!.favourite_message.set(mCurrentUser!!.user_id, "0")
        mConversationAdapter!!.notifyDataSetChanged()
    }

    private fun sendUnmatchBroadcast() {
        val broadCastIntent = Intent(Constants.UNMATCH)
        broadcaster!!.sendBroadcast(broadCastIntent)
    }

    private fun displayRatingDialog() {
        val totalMessageCount = mPrivateChat!!.message_rating_count.get(mCurrentUser!!.user_id)!! +
                mPrivateChat!!.message_rating_count.get(mOpponentUser!!.user_id)!!

        if (totalMessageCount >= 30) {
            if (mPrivateChat!!.rating.get(mUtils!!.getString("user_id", ""))
                            .equals("0")) {
                val intent = Intent(this, RatingActivity::class.java)
                intent.putExtra("user_id", mOpponentUserId)
                intent.putExtra("user_name", mOpponentUser!!.user_name)
                intent.putExtra("chatDialogId", mPrivateChat!!.chat_dialog_id)
                if (mPrivateChat!!.rating == null) {
                    intent.putExtra("status", "")
                } else {
                    if (TextUtils.isEmpty(mPrivateChat!!.rating.get(mCurrentUser!!.user_id))
                            || mPrivateChat!!.rating.get(mCurrentUser!!.user_id).equals("null")) {
                        intent.putExtra("status", "")
                    } else {
                        intent.putExtra("status", mPrivateChat!!.rating.get(mCurrentUser!!.user_id))
                    }
                }
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
    }


}