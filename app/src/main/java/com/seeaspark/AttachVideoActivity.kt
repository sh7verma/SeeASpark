package com.seeaspark

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.edmodo.rangebar.RangeBar
import com.netcompss.loader.LoadJNI
import com.squareup.picasso.Picasso
import helper.VideoCompressionHelper
import kotlinx.android.synthetic.main.activity_attach_video.*
import utils.TimeUtilsTrim
import video.VideoCompressListener
import video.VideoCompressor
import videoUtils.SGLog
import videoUtils.Worker
import java.io.*
import java.util.*

@Suppress("DEPRECATION")
/**
 * Created by dev on 8/8/18.
 */
class AttachVideoActivity : BaseActivity() {

    internal var select_path = ""
    internal var selected_type = ""
    internal var saved_path = ""
    internal var saved_video = ""
    internal var duartion_sec = 0
    internal var stopIt = 0
    internal var leftThumb = 0
    internal var max_color = 0
    internal var rightThumb = 0
    internal var handleStop = Handler()
    internal var handleProgress = Handler()
    internal var camerapathVideo = ""
    internal var name = ""
    internal var mPic = ""

    internal var mgetFramesAsync: GetFrames? = null

    override fun getContentView() = R.layout.activity_attach_video

    override fun initUI() {
        select_path = intent.getStringExtra("select_path")
        name = intent.getStringExtra("name")
        mPic = intent.extras!!.getString("pic")
        Picasso.with(this).load(mPic).placeholder(R.drawable.placeholder_image).into(imgProfileAvatar)
        txtName.setText(name)
        rangeSeekbar.setEnabled(false)

        val thumb_lay_pams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (mWidth * 0.13).toInt())
        thumb_lay_pams.addRule(RelativeLayout.CENTER_IN_PARENT)
        thumbLay.setLayoutParams(thumb_lay_pams)

        val thumb_img_pams = LinearLayout.LayoutParams(((mWidth - mWidth / 32) / 7).toInt(), (mWidth * 0.11).toInt())
        thumb1.layoutParams = thumb_img_pams
        thumb2.layoutParams = thumb_img_pams
        thumb3.layoutParams = thumb_img_pams
        thumb4.layoutParams = thumb_img_pams
        thumb5.layoutParams = thumb_img_pams
        thumb6.layoutParams = thumb_img_pams
        thumb7.layoutParams = thumb_img_pams
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayDayMode() {
//        llOuterAttachVideo.setBackgroundColor(ContextCompat.getColor(this, R.color.white_color))
//        txtSendVideo.setTextColor(ContextCompat.getColor(this, R.color.black_color))
//        txtName.setTextColor(ContextCompat.getColor(this, R.color.black_color))
//        showSelectedDuration.setTextColor(ContextCompat.getColor(this, R.color.black_color))
//        maxDuration.setTextColor(ContextCompat.getColor(this, R.color.black_color))
//        fileSize.setTextColor(ContextCompat.getColor(this, R.color.black_color))
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun displayNightMode() {
//        llOuterAttachVideo.setBackgroundColor(ContextCompat.getColor(this, R.color.black_color))
//        txtSendVideo.setTextColor(ContextCompat.getColor(this, R.color.white_color))
//        txtName.setTextColor(ContextCompat.getColor(this, R.color.white_color))
//        showSelectedDuration.setTextColor(ContextCompat.getColor(this, R.color.white_color))
//        maxDuration.setTextColor(ContextCompat.getColor(this, R.color.white_color))
//        fileSize.setTextColor(ContextCompat.getColor(this, R.color.white_color))
    }

    override fun onCreateStuff() {
        try {
            duartion_sec = MediaPlayer.create(this, Uri.fromFile(File(select_path))).duration
            rangeSeekbar.setEnabled(true)
            if (duartion_sec < 2000) {
                Toast.makeText(this, resources.getString(R.string.video_size_limit), Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            save_video_thumbnail()
            rightThumb = duartion_sec / 1000
            videoSeek.setTickCount((duartion_sec / 1000).toInt())
            rangeSeekbar.setTickCount((duartion_sec / 1000).toInt())
            stopIt = (duartion_sec / 1000).toInt()

            showSelectedDuration.setText("00:00 - " + TimeUtilsTrim.toFormattedTime(duartion_sec))
            maxDuration.setText(TimeUtilsTrim.toFormattedTime(duartion_sec))
            getFrames()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        selected_type = intent.getStringExtra("selected_type")
        val c = Calendar.getInstance()
        val myDir = File(Environment.getExternalStorageDirectory().toString() + "/SeeASpark/Video/Sent")
        if (!myDir.exists())
            myDir.mkdirs()
        val fname = "REDVID" + c.timeInMillis + ".mp4"
        val cam = File(myDir, fname)
        if (cam.exists()) {
            cam.delete()
        }
        camerapathVideo = cam.absolutePath

        initVideo(select_path)
        save_video_thumbnail()

    }

    override fun initListener() {
        imgBack.setOnClickListener(this)
        imgSend.setOnClickListener(this)
        imgPlayVideo.setOnClickListener(this)
        videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            rangeSeekbar.setEnabled(true)
            save_video_thumbnail()
            duartion_sec = videoView.getDuration()
            if (duartion_sec < 2000) {
                Toast.makeText(this, resources.getString(R.string.video_size_limit), Toast.LENGTH_SHORT).show()
                finish()
                return@OnPreparedListener
            }
            rightThumb = duartion_sec / 1000
            videoSeek.setTickCount((videoView.getDuration() / 1000).toInt())
            rangeSeekbar.setTickCount((videoView.getDuration() / 1000).toInt())
            stopIt = (videoView.getDuration() / 1000).toInt()

            showSelectedDuration.setText("00:00 - " + TimeUtilsTrim.toFormattedTime(videoView.getDuration()))
            maxDuration.setText(TimeUtilsTrim.toFormattedTime(videoView.getDuration()))
            getFrames()
        })

        videoView.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
            // TODO Auto-generated method stub
            Toast.makeText(this, getString(R.string.unable_to_play_video),
                    Toast.LENGTH_SHORT).show()
            val file = File(select_path)
            val ist: InputStream
            try {
                ist = FileInputStream(file)
                mp.setDataSource(ist.fd)
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            true
        })

        videoView.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            // TODO Auto-generated method stub
            imgPlayVideo.setVisibility(View.VISIBLE)
            handleProgress.removeCallbacks(onEverySecond)
            handleStop.removeCallbacks(stopVideo)

            videoView.seekTo(leftThumb * 1000)
            if (videoView.getCurrentPosition() / 1000 < duartion_sec / 1000)
                videoSeek.setThumbIndices(leftThumb, videoView.getCurrentPosition() / 1000)
        })

        rangeSeekbar.setOnRangeBarChangeListener(RangeBar.OnRangeBarChangeListener { rangeBar, leftThumbIndex, rightThumbIndex ->
            // TODO Auto-generated method stub
            leftThumb = leftThumbIndex
            rightThumb = rightThumbIndex+1
            videoView.pause()
            imgPlayVideo.setVisibility(View.VISIBLE)
            handleProgress.removeCallbacks(onEverySecond)

            videoView.seekTo(leftThumbIndex * 1000)
            if (videoView.getCurrentPosition() / 1000 < duartion_sec / 1000)
                videoSeek.setThumbIndices(leftThumbIndex, videoView.getCurrentPosition() / 1000)
            showSelectedDuration.setText(TimeUtilsTrim.toFormattedTime(leftThumbIndex * 1000) + " - " + TimeUtilsTrim.toFormattedTime(rightThumbIndex * 1000))
            maxDuration.setText(TimeUtilsTrim.toFormattedTime((rightThumbIndex - leftThumbIndex) * 1000))

            stopIt = rightThumbIndex - leftThumbIndex
        })
    }

    override fun getContext() = this

    override fun onClick(v: View?) {
        when (v) {
            imgBack -> {
                moveBack()
            }
            imgPlayVideo -> {
                videoView.start()
                imgPlayVideo.setVisibility(View.GONE)
                if (videoView.getCurrentPosition() / 1000 < duartion_sec / 1000)
                    videoSeek.setThumbIndices(leftThumb, videoView.getCurrentPosition() / 1000)
                handleProgress.postDelayed(onEverySecond, 1000)
                handleStop.postDelayed(stopVideo, ((stopIt + 1) * 1000).toLong())
            }
            imgSend -> {
                if (connectedToInternet()) {
                    sendMessage("")
                } else {
                    showInternetAlert(txtName)
                }
            }
        }
    }

    override fun onBackPressed() {
        moveBack()
    }

    private fun moveBack() {
        try {
            if (videoView.isPlaying()) {
                videoView.pause()
                imgPlayVideo.setVisibility(View.VISIBLE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (mgetFramesAsync != null) {
            if (mgetFramesAsync!!.getStatus() == AsyncTask.Status.RUNNING) {
                mgetFramesAsync!!.cancel(true)
            }
        }
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    internal fun initVideo(select_path: String) {
        if (selected_type == "video") {
            val file = File(select_path)
            file.setReadable(true, false)
            videoView.setVideoURI(Uri.fromFile(file))
            videoView.requestFocus()
            imgPlayVideo.setVisibility(View.VISIBLE)

            val length = file.length()
            if (length < 1024) {
                fileSize.setText(length.toString() + "Bytes")
            } else if (length / 1024 >= 1 && length / 1024 < 1024) {
                fileSize.setText((length / 1024).toString() + "KB")
            } else if (length / 1024 >= 1024) {
                fileSize.setText((length / 1024 / 1024).toString() + "MB")
            }

        }
    }

    private val stopVideo = Runnable {
        // TODO Auto-generated method stub
        videoView.pause()
        imgPlayVideo.setVisibility(View.VISIBLE)

        videoView.seekTo(leftThumb * 1000)
        if (videoView.getCurrentPosition() / 1000 < duartion_sec / 1000)
            videoSeek.setThumbIndices(leftThumb, videoView.getCurrentPosition() / 1000)
    }
    private val onEverySecond = object : Runnable {

        override fun run() {
            if (videoSeek != null && videoView.getCurrentPosition() / 1000 < duartion_sec / 1000) {
                videoSeek.setThumbIndices(leftThumb, videoView.getCurrentPosition() / 1000)
            }
            if (videoView.isPlaying()) {
                handleProgress.postDelayed(this, 1000)
            }
        }
    }

    internal fun save_video_thumbnail() {
        if (TextUtils.isEmpty(saved_path)) {
            try {
                val pic = ThumbnailUtils.createVideoThumbnail(select_path,
                        MediaStore.Video.Thumbnails.MINI_KIND)

                val bytes = ByteArrayOutputStream()
                pic.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(contentResolver, pic,
                        "Title", null)

                val cursor = contentResolver.query(Uri.parse(path), null, null, null, null)
                cursor!!.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                saved_path = cursor.getString(idx)
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }
    }

    internal fun sendMessage(message: String) {
        try {
            if (videoView.isPlaying()) {
                videoView.pause()
                videoView.setVisibility(View.VISIBLE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (mgetFramesAsync != null) {
            if (mgetFramesAsync!!.getStatus() == AsyncTask.Status.RUNNING) {
                mgetFramesAsync!!.cancel(true)
            }
        }

        if (rightThumb - leftThumb > 1) {
            if (leftThumb == 0 && rightThumb >= ((videoView.getDuration() / 1000).toInt() - 1)) {
                // thumb not moved
                VideoCompressionHelper.initCompressor()
                        .startCompression(this, select_path, camerapathVideo)
//                compressVideo(select_path)
            } else {
                (CropVideo()).execute()
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.video_size_limit), Toast.LENGTH_SHORT).show()
        }
    }

    inner class CropVideo : AsyncTask<Void, Void, Void>() {

        var croppd: ProgressDialog? = null

        init {
            // TODO Auto-generated constructor stub
            val c = Calendar.getInstance()
            val myDir = File(Environment.getExternalStorageDirectory().toString() + "/SeeASpark/Video/Sent")
            if (!myDir.exists())
                myDir.mkdirs()
            val fname = "CROPVID" + c.timeInMillis + ".mp4"
            val cam = File(myDir, fname)
            if (cam.exists()) {
                cam.delete()
            }
            saved_video = cam.absolutePath
        }

        override fun onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute()
            try {
                croppd = ProgressDialog(this@AttachVideoActivity)
                croppd!!.setMessage("Cropping video")
                croppd!!.setCancelable(false)
                croppd!!.show()
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }

        override fun doInBackground(vararg params: Void): Void? {
            // TODO Auto-generated method stub
            val vk = LoadJNI()
            try {
                try {
                    val workFolder = filesDir
                            .absolutePath
                    val complexCommand = arrayOf("ffmpeg", "-ss", TimeUtilsTrim.toFormattedTime(leftThumb * 1000), "-i", select_path, "-c:v", "copy", "-c:a", "copy", "-t", "" + (rightThumb - leftThumb), saved_video)
                    vk.run(complexCommand, workFolder, this@AttachVideoActivity)
                    Log.i("test", "ffmpeg4android finished successfully")
                } catch (e: Throwable) {
                    Log.e("test", "vk run exception.", e)
                    finish()
                }

            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            // TODO Auto-generated method stub
            super.onPostExecute(result)
            try {
                if (croppd != null)
                    if (croppd!!.isShowing) {
                        croppd!!.dismiss()
                    }
//                compressVideo(saved_video)

                VideoCompressionHelper.initCompressor()
                        .startCompression(this@AttachVideoActivity, saved_video, camerapathVideo)
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }
    }

    internal fun getFrames() {
        mgetFramesAsync = GetFrames(select_path)
        mgetFramesAsync!!.execute()
    }

    internal inner class GetFrames(select_path: String) : AsyncTask<Void, Void, ArrayList<BitmapDrawable>>() {
        var retriever: MediaMetadataRetriever? = null

        init {
            // TODO Auto-generated constructor stub
            retriever = MediaMetadataRetriever()
            val fis: FileInputStream
            try {
                fis = FileInputStream(File(select_path))
                retriever!!.setDataSource(fis.fd)
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }

        override fun doInBackground(vararg params: Void): ArrayList<BitmapDrawable>? {
            // TODO Auto-generated method stub
            if (retriever != null) {
                val bmdrawable = ArrayList<BitmapDrawable>()
                val div = duartion_sec / 7
                var last_frame = 0
                for (i in 1..7) {
                    bmdrawable.add(BitmapDrawable(resources, retriever!!.getFrameAtTime((last_frame * 1000).toLong(), MediaMetadataRetriever.OPTION_CLOSEST)))
                    last_frame = last_frame + div
                }
                return bmdrawable
            } else
                return null
        }

        @SuppressLint("NewApi")
        override fun onPostExecute(result: ArrayList<BitmapDrawable>?) {
            // TODO Auto-generated method stub
            super.onPostExecute(result)
            if (result != null) {
                for (i in 1..7) {
                    try {
                        val id = resources.getIdentifier("thumb" + i, "id", packageName)
                        val thumb = findViewById<View>(id) as ImageView
                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            thumb.setBackgroundDrawable(result[i - 1])
                        } else {
                            thumb.background = result[i - 1]
                        }
                    } catch (e: Exception) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    fun afterCompressionSuccesful(result: String) {
        val fn = File(result)
        if (fn.exists()) {
            val intent = Intent()
//            intent.putExtra("caption", "")
            intent.putExtra("selected_video_thumb", saved_path)
            intent.putExtra("selected_video", result)
//            intent.putExtra("selected_name", fn.name)
//            intent.putExtra("selected_color", max_color)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        unbindDrawables(findViewById(R.id.llOuterAttachVideo))
        System.gc()
        super.onDestroy()
    }

    private fun unbindDrawables(view: View) {
        try {
            if (view.background != null) {
                view.background.callback = null
            }
            if (view is ViewGroup) {
                for (i in 0 until view.childCount) {
                    unbindDrawables(view.getChildAt(i))
                }
                view.removeAllViews()
            }
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }


    ////

    private fun compressVideo(path: String) {

        val progDailog = ProgressDialog.show(this, "Please wait ...", "Compressing Video ...", true)
        progDailog.setCancelable(false)

        VideoCompressor.compress(this, path, object : VideoCompressListener {
            override fun onSuccess(outputFile: String, filename: String, duration: Long) {
                Worker.postMain {
                    SGLog.e("video compress success:$outputFile")
                    progDailog.dismiss()
                    afterCompressionSuccesful(outputFile)
                }
            }

            override fun onFail(reason: String) {
                Worker.postMain {
                    Toast.makeText(mContext, "video compress failed:$reason", Toast.LENGTH_SHORT).show()
                    SGLog.e("video compress failed:$reason")
                    progDailog.dismiss()
                }
            }

            override fun onProgress(progress: Int) {
                Worker.postMain { SGLog.e("video compress progress:$progress") }
            }
        })
    }


}