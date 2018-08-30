package services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.netcompss.loader.LoadJNI;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import database.Database;
import models.MessagesModel;
import utils.Constants;
import utils.Utils;

/**
 * Created by app on 8/5/2016.
 */
public class DownloadFileService extends Service {

    Database mDb;
    Utils util;
    int mProgress = 0;

    public interface FileDownloadInterface {
        void onStartDownloading(String message_id);

        void onSuccessDownloading(String message_id, String custom_data, String thumbPath);

        void onErrorDownloading(String message_id, Exception exception);

        void onDownloadProgressUpdate(String message_id, int progress);
    }

    public static FileDownloadInterface mFileDownloadInterface;

    public static void setDownloadingListener(FileDownloadInterface listsner) {
        mFileDownloadInterface = listsner;
    }

    public interface FileDownloadFavouriteInterface {
        void onStartDownloading(String message_id);

        void onSuccessDownloading(String message_id, String custom_data, String thumbPath);

        void onErrorDownloading(String message_id, Exception exception);

        void onDownloadProgressUpdate(String message_id, int progress);
    }

    public static FileDownloadFavouriteInterface mFileDownloadFavouriteInterface;

    public static void setFavouriteDownloadingListener(FileDownloadFavouriteInterface listsner) {
        mFileDownloadFavouriteInterface = listsner;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Firebase.setAndroidContext(this);
        mDb = new Database(getApplicationContext());
        util = new Utils(getApplicationContext());
        MessagesModel mMessage = null;
        Log.e("service1","service");
        try {
            if (intent != null) {
                Log.e("service2","service");
                if (intent.hasExtra("message_id")) {
                    Log.e("service3","service");
                    String message_id = intent.getStringExtra("message_id");
                    mMessage = mDb.getSingleMessage(message_id, util.getString("user_id", ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mMessage != null) {
            Log.e("service4","service");
            downloadFile(mMessage);
        }
        return START_REDELIVER_INTENT;
    }

    void downloadFile(final MessagesModel mMessage) {
        mProgress = 0;
        Log.e("service5","service");
        String root = Environment.getExternalStorageDirectory().getPath();
        File myDir = null;
        final Calendar cal = Calendar.getInstance();
        String fname = "";
        if (mMessage.message_type.equals(Constants.TYPE_IMAGE)) {
            myDir = new File(root + "/SeeASpark/Image");
            if (!myDir.isDirectory())
                myDir.mkdirs();
            fname = "REC" + cal.getTimeInMillis() + ".jpg";
        } else if (mMessage.message_type.equals(Constants.TYPE_VIDEO)) {
            myDir = new File(root + "/SeeASpark/Video");
            if (!myDir.isDirectory())
                myDir.mkdirs();
            fname = "VID" + cal.getTimeInMillis() + ".mp4";
        } else if (mMessage.message_type.equals(Constants.TYPE_AUDIO)) {
            myDir = new File(root + "/SeeASpark/Audio");
            if (!myDir.isDirectory())
                myDir.mkdirs();
            fname = "AUD" + cal.getTimeInMillis() + ".m4a";
        } else {
            myDir = new File(root + "/SeeASpark/Documents");
            if (!myDir.isDirectory())
                myDir.mkdirs();
            if (mMessage.attachment_url.contains(".pdf")) {
                fname = "Document" + cal.getTimeInMillis() + ".pdf";
            } else if (mMessage.attachment_url.contains(".txt")) {
                fname = "Document" + cal.getTimeInMillis() + ".txt";
            } else {
                fname = "Document" + cal.getTimeInMillis() + ".doc";
            }
        }
        final File localFile = new File(myDir, fname);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imagesRef = storage.getReferenceFromUrl(mMessage.attachment_url);
        mDb.changeDownloadStatus(mMessage.message_id, Constants.FILE_UPLOADING, "");
        if (mFileDownloadInterface != null) {
            mFileDownloadInterface.onStartDownloading(mMessage.message_id);
        }
        if (mFileDownloadFavouriteInterface != null) {
            mFileDownloadFavouriteInterface.onStartDownloading(mMessage.message_id);
        }
        try {
            final File finalMyDir = myDir;
            imagesRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.e("service6","service");
                    if (mMessage.message_type.equals(Constants.TYPE_VIDEO)) {
                        try {
                            LoadJNI vk = new LoadJNI();
                            try {
                                String after_convert = "";
                                String camname = "VIDRecieved" + cal.getTimeInMillis() + ".mp4";
                                File cam = new File(finalMyDir, camname);
                                if (cam.exists()) {
                                    cam.delete();
                                }
                                after_convert = cam.getAbsolutePath();
                                try {
                                    // to convert from mov to mp4
                                    String workFolder = getFilesDir()
                                            .getAbsolutePath();
                                    String[] complexCommand = {"ffmpeg", "-i",
                                            localFile.getAbsolutePath(), "-c", "copy",
                                            after_convert};
                                    vk.run(complexCommand, workFolder, getApplicationContext());
                                    Log.i("test",
                                            "ffmpeg4android finished successfully");
                                    File ff = new File(after_convert);
                                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
                                            ff.getAbsolutePath(),
                                            MediaStore.Video.Thumbnails.MINI_KIND);
                                    File filethumb = save_image(bitmap, "mp4");
                                    if (mFileDownloadInterface != null) {
                                        mFileDownloadInterface.onSuccessDownloading(mMessage.message_id, ff.getAbsolutePath(), filethumb.getAbsolutePath());
                                    }
                                    if (mFileDownloadFavouriteInterface != null) {
                                        mFileDownloadFavouriteInterface.onSuccessDownloading(mMessage.message_id, ff.getAbsolutePath(), filethumb.getAbsolutePath());
                                    }
                                    mDb.changeDownloadStatus(mMessage.message_id, Constants.FILE_SUCCESS,
                                            ff.getAbsolutePath(), filethumb.getAbsolutePath());
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (mMessage.message_type.equals(Constants.TYPE_AUDIO)) {
                        try {
                            if (mFileDownloadInterface != null) {
                                mFileDownloadInterface.onSuccessDownloading(mMessage.message_id, localFile.getAbsolutePath(), mMessage.custom_data);
                            }
                            if (mFileDownloadFavouriteInterface != null) {
                                mFileDownloadFavouriteInterface.onSuccessDownloading(mMessage.message_id, localFile.getAbsolutePath(), mMessage.custom_data);
                            }
                            mDb.changeDownloadStatus(mMessage.message_id, Constants.FILE_SUCCESS, localFile.getAbsolutePath(), mMessage.custom_data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (mMessage.message_type.equals(Constants.TYPE_IMAGE)) {
                        if (mFileDownloadInterface != null) {
                            mFileDownloadInterface.onSuccessDownloading(mMessage.message_id, localFile.getAbsolutePath(), "");
                        }
                        if (mFileDownloadFavouriteInterface != null) {
                            mFileDownloadFavouriteInterface.onSuccessDownloading(mMessage.message_id, localFile.getAbsolutePath(), "");
                        }
                        mDb.changeDownloadStatus(mMessage.message_id, Constants.FILE_SUCCESS, localFile.getAbsolutePath());
                    } else if (mMessage.message_type.equals(Constants.TYPE_DOCUMENT)) {
                        if (mFileDownloadInterface != null) {
                            mFileDownloadInterface.onSuccessDownloading(mMessage.message_id, localFile.getAbsolutePath(), "");
                        }
                        if (mFileDownloadFavouriteInterface != null) {
                            mFileDownloadFavouriteInterface.onSuccessDownloading(mMessage.message_id, localFile.getAbsolutePath(), "");
                        }
                        mDb.changeDownloadStatus(mMessage.message_id, Constants.FILE_SUCCESS, localFile.getAbsolutePath());
                    }
                    stopSelf();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Log.e("service7","service");
                    if (mFileDownloadInterface != null) {
                        mFileDownloadInterface.onErrorDownloading(mMessage.message_id, exception);
                    }
                    if (mFileDownloadFavouriteInterface != null) {
                        mFileDownloadFavouriteInterface.onErrorDownloading(mMessage.message_id, exception);
                    }
                    mDb.changeDownloadStatus(mMessage.message_id, Constants.FILE_EREROR, "");
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("service8","service");
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (((int) progress) - mProgress > 9 || progress == 100) {
                        if (mFileDownloadInterface != null) {
                            mFileDownloadInterface.onDownloadProgressUpdate(mMessage.message_id, (int) progress);
                        }
                        if (mFileDownloadFavouriteInterface != null) {
                            mFileDownloadFavouriteInterface.onDownloadProgressUpdate(mMessage.message_id, (int) progress);
                        }
                        mProgress = (int) progress;
                        mDb.changProgress(mMessage.message_id, "" + ((int) progress));
                        Log.e("service9","service"+progress);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    File save_image(Bitmap pic, String contentType) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir;
        if (contentType.equals("jpg")) {
            myDir = new File(root + "/SeeASpark/Image");
        } else if (contentType.equals("mp4")) {
            myDir = new File(root + "/SeeASpark/Video");
        } else {
            myDir = new File(root + "/SeeASpark/Audio");
        }

        if (!myDir.exists())
            myDir.mkdirs();
        Calendar cal = Calendar.getInstance();
        String fname = "";

        if (contentType.equals("jpg")) {
            fname = "VTH" + cal.getTimeInMillis() + ".jpg";
        } else if (contentType.equals("mp4")) {
            fname = ".VTH" + cal.getTimeInMillis() + ".jpg";
        }

        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            pic.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            refreshGallery(file);

            if (pic != null) {
                pic.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }
}