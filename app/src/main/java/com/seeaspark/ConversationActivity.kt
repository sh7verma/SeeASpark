package com.seeaspark

import adapters.ConversationAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.animation.*
import android.widget.*
import com.firebase.client.Firebase
import com.google.firebase.database.*
import com.ipaulpro.afilechooser.utils.FileUtils
import com.squareup.picasso.Picasso
import filePicker.FilePickerBuilder
import helper.FirebaseListeners
import kotlinx.android.synthetic.main.activity_conversation.*
import kotlinx.android.synthetic.main.activity_events_details.*
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.media_options_dialog.*
import models.ChatsModel
import models.MessagesModel
import models.ProfileModel
import services.DownloadFileService
import utils.Constants
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ConversationActivity : BaseActivity(), FirebaseListeners.ChatDialogsListenerInterfaceForChat {

    internal val ATTACHMENT = 12
    internal val OPTIONS = 13
    internal val CAMERAVIDEO = 14
    internal val CAMERAIMAGE = 15
    internal val GALLERY = 16
    internal val GETIMAGE = 17
    internal val GETVIDEO = 18
    internal val GETDOC = 19

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

    internal var camerapath = ""
    internal var camerapathVideo = ""
    internal var status = 0

    internal var docPath = ""

    internal var mOpponentUser: ProfileModel? = null
    internal var mCurrentUser: ProfileModel? = null
    internal var mPrivateChat: ChatsModel? = null
    internal var mParticpantIDS = ""
    internal var mOpponentUserId = ""

    internal var usersQuery: Query? = null
    internal var mFirebaseConfigProfile = FirebaseDatabase.getInstance().getReference().child(Constants.USERS)
    internal var mFirebaseConfigChats = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS)
    internal var mFirebaseConfigMessages = FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGES)

    internal var chat_date_format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    internal var today_date_format = SimpleDateFormat("hh:mm aa", Locale.US)
    internal var only_date_format = SimpleDateFormat("dd MMMM", Locale.US)
    internal var mTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    internal var limit = 20
    internal var setReadMessages: ArrayList<MessagesModel> = ArrayList()

    var mMessagesMap: HashMap<String, MessagesModel>? = null
    var mMessageIds: ArrayList<String> = ArrayList()

    override fun getContentView() = R.layout.activity_conversation

    override fun initUI() {
        mMessagesMap = HashMap()
        mMessageIds = ArrayList<String>()
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
        FirebaseListeners.setChatDialogListenerForChat(this)
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
        chatSetUp()
    }

    override fun onNewIntent(intent: Intent?) {
        var parIds = ""
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
            llDefaultActionbar.setVisibility(View.VISIBLE)
            llOptionActionbar.setVisibility(View.GONE)
            edMessage.setText("")
            val inp = arrayOf<InputFilter>(InputFilter.LengthFilter(4000))
            edMessage.filters = inp
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
            chatSetUp()
            super.onNewIntent(intent)
        }
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
                }
            }
        }
        if (mPrivateChat != null && mPrivateChat!!.unread_count.get(mCurrentUser!!.user_id) != 0) {
            mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("unread_count").child(mCurrentUser!!.user_id).setValue(0)
        }
        setReadMessages.clear()
        super.onResume()
    }

    fun chatSetUp() {
        if (mOpponentUser == null) {
            mOpponentUser = ProfileModel()
            mOpponentUser!!.user_id = mOpponentUserId
        }

        mPrivateChat = db!!.getChatDialog("" + mParticpantIDS, mUtils!!.getString("user_id", ""), mOpponentUserId)

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
                            val mChat = ChatsModel.parseChat(postSnapshot,mUtils!!.getString("user_id", ""))
                            mChat.chat_dialog_id = postSnapshot.key
                            db!!.addNewChat(mChat, mUtils!!.getString("user_id", ""), mOpponentUserId)
                            mPrivateChat = mChat
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
        usersQuery = mFirebaseConfigProfile.orderByKey().equalTo("id_" + mOpponentUserId)
        usersQuery!!.addChildEventListener(mOpponentUserListener)
    }

    internal var mOpponentUserListener: ChildEventListener = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: com.google.firebase.database.DataSnapshot, s: String?) {
            val user = ProfileModel.parseProfile(dataSnapshot)
            db!!.addProfile(user)
            mOpponentUser = db!!.getProfile(user.user_id)
            setOnlineFlag(user)
        }

        override fun onChildChanged(dataSnapshot: com.google.firebase.database.DataSnapshot, s: String?) {
            val user = ProfileModel.parseProfile(dataSnapshot)
            db!!.addProfile(user)
            mOpponentUser = db!!.getProfile(user.user_id)
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

    override fun onDialogChanged(mChat: ChatsModel?, value: Int) {
        try {
            if (value == 0) {
                if (mPrivateChat!!.chat_dialog_id.equals(mChat!!.chat_dialog_id)) {
                    txtName.setText(mPrivateChat!!.name.get(mOpponentUserId))
                    if (!mPrivateChat!!.profile_pic.get(mOpponentUserId).equals(mChat!!.profile_pic.get(mOpponentUserId))) {
                        Picasso.with(this).load(mChat!!.profile_pic.get(mOpponentUserId)).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
                    }
                    mPrivateChat = mChat
                }
            } else {
                moveBack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setHeaderData() {
        txtName.setText(mPrivateChat!!.name.get(mOpponentUserId))
        Picasso.with(this).load(mPrivateChat!!.profile_pic.get(mOpponentUserId)).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
    }

    internal fun addMessageListener() {
        FirebaseListeners.getListenerClass(applicationContext).setListener(mPrivateChat!!.chat_dialog_id)
        val deletetime = mPrivateChat!!.delete_dialog_time.get(mUtils!!.getString("user_id", ""))
        mMessagesMap = db!!.getAllMessages(mPrivateChat!!.chat_dialog_id, mCurrentUser!!.user_id, limit, deletetime!!, mOpponentUserId)
        makeHeaders()

        mConversationAdapter = ConversationAdapter(this, mConversationActivity, mWidth, mCurrentUser!!.user_id,
                mOpponentUserId, mPrivateChat!!.participant_ids)
        lvChatList.adapter = mConversationAdapter
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
                        message.message_type.equals(Constants.TYPE_AUDIO) || message.message_type.equals(Constants.TYPE_VIDEO)/* ||
                        message.message_type.equals(Constants.TYPE_DOCUMENT)*/)) {
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
            }

            if (mPrivateChat!!.unread_count.get(mCurrentUser!!.user_id) != 0) {
                mFirebaseConfigChats.child(mPrivateChat!!.chat_dialog_id).child("unread_count").child(mCurrentUser!!.user_id).setValue(0)
            }

//            if (mMessagesMap!![mMessageIds[i]]!!.is_header) {
//                mMessagesMap!![mMessageIds[i]].message_flag = 0
//            } else if (mMessagesMap!![mMessageIds[i]]!!.sender_id.equals(mCurrentUser!!.user_id)) {
//                mMessagesMap!![mMessageIds[i]].message_flag = 0
//            } else {
//                if (i >= mMessagesMap!!.size - 1) {
//                    mMessagesMap!![mMessageIds[i]].message_flag = 0
//                } else {
//                    if (mMessagesMap!![mMessageIds[i]]!!.show_message_datetime.equals(mMessagesMap!![mMessageIds[i + 1]]!!.show_message_datetime) && !mMessagesMap!![mMessageIds[i + 1]]!!.sender_id.equals(mCurrentUser!!.user_id)) {
//                        mMessagesMap!![mMessageIds[i]].message_flag = 1
//                    } else {
//                        mMessagesMap!![mMessageIds[i]].message_flag = 0
//                    }
//                }
//            }
        }
    }

    override fun initListener() {
        imgBackDefault.setOnClickListener(this)
        imgOptions.setOnClickListener(this)
        imgBackOption.setOnClickListener(this)
        imgFavourite.setOnClickListener(this)
        imgDelete.setOnClickListener(this)
        imgCopy.setOnClickListener(this)
        imgAttachment.setOnClickListener(this)
        imgSendMessage.setOnClickListener(this)
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBackDefault -> {
                Constants.closeKeyboard(this, imgBackDefault)
                moveBack()
            }
            imgOptions -> {
                val intent = Intent(this, ChatOptionsActivity::class.java)
                startActivityForResult(intent, OPTIONS)
            }
            imgBackOption -> {
                llOptionActionbar.visibility = View.GONE
                llDefaultActionbar.visibility = View.VISIBLE
            }
            imgFavourite -> {
            }
            imgDelete -> {
            }
            imgCopy -> {
            }
            imgAttachment -> {
                val intent = Intent(this, AttachmentSelectionActivity::class.java)
                startActivityForResult(intent, ATTACHMENT)
            }
            imgSendMessage -> {
                // Send Text Message
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ATTACHMENT -> {
                    var type = data!!.getStringExtra("type")
                    if (type.equals("gallery")) {
                        showMediaDialog(2)
                    } else if (type.equals("camera")) {
                        showMediaDialog(1)
                    } else if (type.equals("notes")) {
                        var intent = Intent(this, MyNotesActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                    } else if (type.equals("document")) {
                        openFileChooser()
                    }
                }
                OPTIONS -> {
                    var type = data!!.getStringExtra("type")
                    if (type.equals("favourite_message")) {
                        var intent = Intent(this, FavouriteMessageActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
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
                                    intent.putExtra("name", txtName.getText().toString())
                                    intent.putExtra("selected_type", "image")
                                    startActivityForResult(intent, GETIMAGE)
                                } else {
                                    val file = File(path)
                                    val length = file.length()
                                    if (length / 1024 > 1024 * 300) {//more than 300MB
                                        Toast.makeText(this, resources.getString(R.string.file_size_exceeds), Toast.LENGTH_SHORT).show()
                                        return
                                    }
                                    val intent = Intent(this, AttachVideoActivity::class.java)
                                    intent.putExtra("select_path", path)
                                    intent.putExtra("name", txtName.getText().toString())
                                    intent.putExtra("selected_type", "video")
                                    startActivityForResult(intent, GETVIDEO)
                                }
                            } else {
                                if (mime!!.contains("image")) {
                                    //image
                                    val intent = Intent(this, AttachmentActivity::class.java)
                                    intent.putExtra("select_path", path)
                                    intent.putExtra("name", txtName.getText().toString())
                                    intent.putExtra("selected_type", "image")
                                    startActivityForResult(intent, GETIMAGE)
                                } else {
                                    //video
                                    val file = File(path!!)
                                    val length = file.length()
                                    if (length / 1024 > 1024 * 300) {//more than 300MB
                                        Toast.makeText(this, resources.getString(R.string.file_size_exceeds), Toast.LENGTH_SHORT).show()
                                        return
                                    }

                                    val intent = Intent(this, AttachVideoActivity::class.java)
                                    intent.putExtra("select_path", path)
                                    intent.putExtra("name", txtName.getText().toString())
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
                            intent.putExtra("name", txtName.getText().toString())
                            intent.putExtra("selected_type", "image")
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
                            if (length / 1024 > 1024 * 300) {//more than 300MB
                                Toast.makeText(this, resources.getString(R.string.file_size_exceeds), Toast.LENGTH_SHORT).show()
                                return
                            }

                            val intent = Intent(this, AttachVideoActivity::class.java)
                            intent.putExtra("select_path", camerapathVideo)
                            intent.putExtra("name", txtName.getText().toString())
                            intent.putExtra("selected_type", "video")
                            startActivityForResult(intent, GETVIDEO)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                GETIMAGE -> {
//                    status = 0
//                    val blockIds = db.getBlockedByOtherIds()
//                    if (blockIds != null && blockIds!!.size > 0 && blockIds!!.containsKey(mOpponentUser.user_id)) {
//                        status = 1
//                    } else {
//                        status = 0
//                    }
//
//                    val strColor = String.format("#%06X", 0xFFFFFF and data!!.getIntExtra("selected_color", 0))
//                    addImageMessage(data.getStringExtra("caption"), data!!.getStringExtra("selected_image"),
//                            strColor, status)
//
//                    if (status == 0) {
//                        val intent = Intent(this, UploadFileService::class.java)
//                        intent.putExtra("attachment_path", "" + data!!.getStringExtra("selected_image"))
//                        intent.putExtra("push_token", "" + mOpponentUser.push_token)
//                        intent.putExtra("access_token", "" + mOpponentUser.access_token)
//                        intent.putExtra("participant_ids", "" + mPrivateChat.participant_ids)
//                        startService(intent)
//                    }
                }
                GETVIDEO -> {
//                    status = 0
//                    val blockIds = db.getBlockedByOtherIds()
//                    if (blockIds != null && blockIds!!.size > 0 && blockIds!!.containsKey(mOpponentUser.user_id)) {
//                        status = 1
//                    } else {
//                        status = 0
//                    }
//
//                    val strColor = String.format("#%06X", 0xFFFFFF and data!!.getIntExtra("selected_color", 0))
//                    addVideoMessage(data.getStringExtra("caption"), data!!.getStringExtra("selected_video"),
//                            strColor, data.getStringExtra("selected_video_thumb"), status)
//
//                    if (status == 0) {
//                        val intent = Intent(this, UploadFileService::class.java)
//                        intent.putExtra("attachment_path", "" + data!!.getStringExtra("selected_video"))
//                        intent.putExtra("push_token", "" + mOpponentUser.push_token)
//                        intent.putExtra("access_token", "" + mOpponentUser.access_token)
//                        intent.putExtra("participant_ids", "" + mPrivateChat.participant_ids)
//                        startService(intent)
//                    }
                }
                Constants.REQUEST_CODE_DOC -> {
                    var docPaths = ArrayList<String>()
                    docPaths.addAll(data!!.getStringArrayListExtra(Constants.KEY_SELECTED_DOCS))
                    docPath = docPaths.get(0)
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

    private fun openFileChooser() {
        var selectedPhotos = ArrayList<String>()
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

        var dialogParms = CoordinatorLayout.LayoutParams(mWidth - (mWidth / 16), mHeight / 6)
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
                showInternetAlert(llGoingEvents)
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
                showInternetAlert(llGoingEvents)
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

    protected fun startCameraActivity() {
        try {
            val c = Calendar.getInstance()
            camerapath = Environment.getExternalStorageDirectory().toString() + "/SeeASpark/Image/Sent/" + "IMG" + c.timeInMillis + ".jpg"
            val file = File(camerapath)
            file.getParentFile().mkdirs()
            val outputFileUri = Uri.fromFile(file)
            val intent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
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
            file.getParentFile().mkdirs()
            val outputFileUri = Uri.fromFile(file)
            val intent = Intent(
                    MediaStore.ACTION_VIDEO_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)

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
                requestAudioPermission()
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
                requestAudioPermission()
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
    internal var audioPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
    private val AUDIO_PERMISSION_REQUEST_CODE = 15
    private fun checkAudioPermissions(): Boolean {
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

    fun requestAudioPermission() {
        if (hasPermissions(this, listAudioPermissionsNeeded)) {
            mUtils!!.setBoolean(Constants.AUDIO_PERMISSION, false)
            showSnackbar(R.string.audio_permission,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this, listAudioPermissionsNeeded.toTypedArray(), AUDIO_PERMISSION_REQUEST_CODE)
            })
        } else {
            if (!mUtils!!.getBoolean(Constants.AUDIO_PERMISSION, false))
                ActivityCompat.requestPermissions(this, listAudioPermissionsNeeded.toTypedArray(), AUDIO_PERMISSION_REQUEST_CODE)
            if (mUtils!!.getBoolean(Constants.AUDIO_PERMISSION, false)) {
                showSnackbar(R.string.audio_permission,
                        android.R.string.ok, View.OnClickListener {
                    // Request permission
                    mUtils!!.setBoolean(Constants.AUDIO_PERMISSION, false)
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
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults.size > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    }
                } else if (grantResults.size == 2) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    }
                } else if (grantResults.size == 1) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    }
                }
                return
            }
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size == 1) {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else {
            requestGalleryPermission()
        }
    }

    var listGalleryPermissionsNeeded = ArrayList<String>()
    internal var galleryPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val GALLERY_PERMISSION_REQUEST_CODE = 18
    private fun checkGalleryPermissions(): Boolean {
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

    fun requestGalleryPermission() {
        if (hasPermissions(this, listGalleryPermissionsNeeded)) {
            mUtils!!.setBoolean(Constants.GALLERY_PERMISSION, false)
            showSnackbar(R.string.gallery_permission,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this, listGalleryPermissionsNeeded.toTypedArray(), GALLERY_PERMISSION_REQUEST_CODE)
            })
        } else {
            if (!mUtils!!.getBoolean(Constants.GALLERY_PERMISSION, false))
                ActivityCompat.requestPermissions(this, listGalleryPermissionsNeeded.toTypedArray(), GALLERY_PERMISSION_REQUEST_CODE)
            if (mUtils!!.getBoolean(Constants.GALLERY_PERMISSION, false)) {
                showSnackbar(R.string.gallery_permission,
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

}