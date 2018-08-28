package services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import database.Database;
import models.MessagesModel;
import utils.Constants;
import utils.Utils;

/**
 * Created by app on 8/4/2016.
 */
public class UploadFileService extends Service {

    Database mDb;
    Utils mUtil;

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
//        if (!FirebaseApp.getApps(this).isEmpty()) {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        }
        mDb = new Database(getApplicationContext());
        mUtil = new Utils(getApplicationContext());
        String mAttachmentPath = "";
        try {
            if (intent != null) {
                if (intent.hasExtra("attachment_path")) {
                    mAttachmentPath = intent.getStringExtra("attachment_path");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MessagesModel mMessage = null;
        if (!TextUtils.isEmpty(mAttachmentPath)) {
            mMessage = mDb.getPendingUploads(mAttachmentPath, mUtil.getString("user_id", ""));
        } else {
            mMessage = mDb.getPendingUploads(mUtil.getString("user_id", ""));
        }

        if (mMessage != null) {
            uploadFile(mAttachmentPath, mMessage);
        }
        return START_REDELIVER_INTENT;
    }

    void uploadFile(String mAttachmentPath, final MessagesModel mMessage) {
        try {
            File filename = new File(mAttachmentPath);
            final String name = filename.getName();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(Constants.FIREBASE_STORAGE_URL);
            final StorageReference imagesRef = storageRef.child(Constants.FIRBASE_STORAGE_DIRECTRY + name);
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
            } else if (mMessage.message_type.equals(Constants.TYPE_DOCUMENT)) {
//                metadata = new StorageMetadata.Builder()
//                        .setContentType("Audio/m4a")
//                        .build();
            }

            Uri file = Uri.fromFile(new File(mMessage.attachment_path));
            UploadTask uploadTask;
            if (mMessage.message_type.equals(Constants.TYPE_DOCUMENT)) {
                uploadTask = imagesRef.putFile(file);
            } else {
                uploadTask = imagesRef.putFile(file, metadata);
            }
            mDb.changeUploadStatus(mMessage.message_id, Constants.FILE_UPLOADING, Constants.STATUS_MESSAGE_PENDING);
            if (mFileUploadInterface != null) {
                mFileUploadInterface.onStartUploading(mMessage.message_id);
            }

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imagesRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String downloadUri = task.getResult().toString();
                        if (mFileUploadInterface != null) {
                            mFileUploadInterface.onSuccessUploading(mMessage.message_id, "" + downloadUri);
                        }
                        HashMap<String, Object> msgHashMap = new HashMap<>();
                        msgHashMap.put("message_id", mMessage.message_id);
                        msgHashMap.put("message", mMessage.message);
                        msgHashMap.put("message_type", mMessage.message_type);
                        msgHashMap.put("firebase_message_time", ServerValue.TIMESTAMP);
                        msgHashMap.put("chat_dialog_id", mMessage.chat_dialog_id);
                        msgHashMap.put("sender_id", mMessage.sender_id);
                        msgHashMap.put("message_status", Constants.STATUS_MESSAGE_SENT);
                        msgHashMap.put("attachment_url", downloadUri);

                        HashMap<String, String> blockStatus = mDb.getBlockStatus(mMessage.chat_dialog_id);
                        HashMap<String, String> delete = new HashMap<>();
                        HashMap<String, String> favourite = new HashMap<>();
                        delete.put(mUtil.getString("user_id", ""), "0");
                        favourite.put(mUtil.getString("user_id", ""), "0");
                        for (String key : blockStatus.keySet()) {
                            if (!key.equals(mUtil.getString("user_id", ""))) {
                                favourite.put(key, "0");
                                if (blockStatus.get(key).equals("1")) {
                                    delete.put(key, "1");
                                } else {
                                    delete.put(key, "0");
                                }
                                break;
                            }
                        }
                        msgHashMap.put("message_deleted", delete);
                        msgHashMap.put("favourite_message", favourite);
                        Long time = Constants.getUtcTime(Calendar.getInstance().getTimeInMillis());
                        msgHashMap.put("message_time", "" + time);

                        DatabaseReference mFirebaseConfigMessages = FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGES);
                        mFirebaseConfigMessages.child(mMessage.chat_dialog_id).child(mMessage.message_id).setValue(msgHashMap);
                        mDb.changeUploadStatus(mMessage.message_id, Constants.FILE_SUCCESS, Constants.STATUS_MESSAGE_SENT);
                        stopSelf();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (mFileUploadInterface != null) {
                        mFileUploadInterface.onErrorUploading(mMessage.message_id, exception);
                    }
                    mDb.changeUploadStatus(mMessage.message_id, Constants.FILE_EREROR, Constants.STATUS_MESSAGE_PENDING);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (mFileUploadInterface != null) {
                        mFileUploadInterface.onProgressUpdate(mMessage.message_id, (int) progress);
                    }
                    mDb.changProgress(mMessage.attachment_path, "" + ((int) progress));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
