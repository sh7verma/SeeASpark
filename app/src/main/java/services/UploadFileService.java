package services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import database.Database;
import models.MessagesModel;
import utils.Constants;

/**
 * Created by app on 8/4/2016.
 */
public class UploadFileService extends Service {

    Database mDb;
    SharedPreferences sp;

    public interface FileUploadInterface {
        void onStartUploading(String message_id);

        void onSuccessUploading(String message_id, String attachment_data);

        void onErrorUploading(String message_id, Exception exception);

        void onProgressUpdate(String message_id, int progress);
    }

    public static FileUploadInterface mFileUploadInterface;

    public static void setUploadingListener(FileUploadInterface listsner) {
        mFileUploadInterface = listsner;
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
        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        mDb = new Database(getApplicationContext());
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//        ViewProfileModel mCurrentUser = mDb.getCurrentUser();
        String mPushToken = "", mAccessToken = "", mParticipantIds = "", mAttachmentPath = "";
        String groupName = "";
        try {
            if (intent != null) {
                if (intent.hasExtra("attachment_path")) {
                    mAttachmentPath = intent.getStringExtra("attachment_path");
                    mParticipantIds = intent.getStringExtra("participant_ids");
                    if (intent.hasExtra("push_token")) {
                        mPushToken = intent.getStringExtra("push_token");
                        mAccessToken = intent.getStringExtra("access_token");
                    } else {
                        groupName = intent.getStringExtra("group_name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MessagesModel mMessage = null;
//        if (!TextUtils.isEmpty(mAttachmentPath)) {
//            mMessage = mDb.getPendingUploads(mAttachmentPath);
//        } else {
//            mMessage = mDb.getPendingUploads();
//        }

        if (mMessage != null) {
//            uploadFile(mAttachmentPath, mMessage, mPushToken, mAccessToken, mParticipantIds, mCurrentUser.name, groupName);
        }

        return START_REDELIVER_INTENT;
    }

    void uploadFile(String mAttachmentPath, final MessagesModel mMessage, final String mPushToken,
                    final String mAccessToken, final String mParticipantIds, final String name,
                    final String groupName) {
        try {
            File filename = new File(mAttachmentPath);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL);
            StorageReference imagesRef = storageRef.child(Constants.FIRBASE_STORAGE_DIRECTRY + filename.getName());
            StorageMetadata metadata = null;
            if (mMessage.message_type.equals(Constants.TYPE_IMAGE)) {
                metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .build();
            } else if (mMessage.message_type.equals(Constants.TYPE_VIDEO)) {
                metadata = new StorageMetadata.Builder()
                        .setContentType("video/mp4")
                        .build();
            } else if (mMessage.message_type.equals(Constants.TYPE_AUDIO)) {
                metadata = new StorageMetadata.Builder()
                        .setContentType("Audio/m4a")
                        .build();
            }

            Uri file = Uri.fromFile(new File(mMessage.attachment_path));
            UploadTask uploadTask = imagesRef.putFile(file, metadata);

//            mDb.changeUploadStatus(mMessage.attachment_path, Consts.FILE_UPLOADING, Consts.STATUS_MESSAGE_PENDING);
            if (mFileUploadInterface != null) {
                mFileUploadInterface.onStartUploading(mMessage.message_id);
            }

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    if (mFileUploadInterface != null) {
//                        mFileUploadInterface.onSuccessUploading(mMessage.message_id, "" + downloadUrl);
//                    }
//
//                    Firebase mFirebaseConfigMessages = new Firebase(Consts.fireBase_Database_Url).child("MessagesModel");
//                    mMessage.attachment_status = Consts.FILE_SUCCESS;
//                    mMessage.attachment_url = "" + downloadUrl;
//                    mMessage.attachment_progress = "0";
//                    long time = Consts.getUtcTime((Calendar.getInstance()).getTimeInMillis());
//                    mMessage.message_time = "" + time;
//                    if (mMessage.message_type.equals(Consts.TYPE_AUDIO) || mMessage.message_type.equals(Consts.TYPE_TIME_BOMB_IMAGE)) {
//                        mMessage.audio_duration = mMessage.custom_data;
//                    }
//                    mMessage.message_status = Consts.STATUS_MESSAGE_SENT;
//                    mMessage.total_delete_ids = new HashMap<>();
//                    HashMap<String, String> contactNumbers = mDb.getContactNumbers(mMessage.chat_dialog_id);
//                    for (String userid : contactNumbers.keySet()) {
//                        if (!userid.equals(sp.getString("user_id", ""))) {
//                            mMessage.total_delete_ids.put(userid, userid);
//                        }
//                    }
//
//                    mFirebaseConfigMessages.child(mMessage.message_id).setValue(mMessage);
//                    mDb.changeUploadStatus(mMessage.attachment_path, Consts.FILE_SUCCESS, Consts.STATUS_MESSAGE_SENT);
                    stopSelf();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    if (mFileUploadInterface != null) {
//                        mFileUploadInterface.onErrorUploading(mMessage.message_id, exception);
//                    }
//                    mDb.changeUploadStatus(mMessage.attachment_path, Consts.FILE_EREROR, Consts.STATUS_MESSAGE_PENDING);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (mFileUploadInterface != null) {
                        mFileUploadInterface.onProgressUpdate(mMessage.message_id, (int) progress);
                    }
//                    mDb.changProgress(mMessage.attachment_path, "" + ((int) progress));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
