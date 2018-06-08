package services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.seeaspark.CreateProfileActivity;
import com.seeaspark.R;

import java.util.Map;

import utils.Constants;
import utils.Utils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Utils utils;
    private LocalBroadcastManager broadcaster;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
        utils = new Utils(getApplicationContext());
        broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(data);
        }
    }

    private void sendNotification(Map<String, String> messageBody) {
        Intent intent = null;
        String message = messageBody.get("message");
        int notificationId;

//        {access_token=37ba65a651debf9a74a897a0a65d1c53, push_type=1,
// user_id=0, id=, body=Email verified successfully, sound=default,
// title=See A Spark, message=Email verified successfully}

        if (messageBody.get("push_type").equalsIgnoreCase("1")) {
            if (utils.getInt("inside_verify", 0) == 0) {
                /// outside verify email activity
                intent = new Intent(this, CreateProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ringNotification(intent, message, 0, messageBody.get("body"));
            } else {
                /// inside verify email activity
                Intent notificationIntent = new Intent(Constants.EMAIL_VERIFY);
                broadcaster.sendBroadcast(notificationIntent);
            }
        }
    }

    void ringNotification(Intent intent, String mess, int notificationId, String title) {
        int mIcon;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mIcon = R.mipmap.ic_launcher;
        else
            mIcon = R.mipmap.ic_launcher;

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(mIcon)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(mess)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mess))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());

        if (utils.getInt("Background", 0) == 1) {
            int mBadgecount = utils.getInt("badge_count", 0);
            mBadgecount++;
            utils.setInt("badge_count", mBadgecount);
//            ShortcutBadger.applyCount(getApplicationContext(), utils.getInt("badge_count", 0));
        }
    }


    void silentNotification(Intent intent, String mess, int notificationId, String title) {
        int mIcon;
        long pattern[] = {01};
        long pattern_vibrate[] = {0, 100, 200, 300, 400};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mIcon = R.mipmap.ic_launcher;

        else
            mIcon = R.mipmap.ic_launcher;

        long pattern_noti[] = null;
        if (utils.getInt("vibration", 0) == 1)
            pattern_noti = pattern_vibrate;
        else
            pattern_noti = pattern;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(mIcon)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setContentText(mess)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mess))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setVibrate(pattern_noti)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());

        if (utils.getInt("Background", 0) == 1) {
            int mBadgecount = utils.getInt("badge_count", 0);
            mBadgecount++;
            utils.setInt("badge_count", mBadgecount);
//            ShortcutBadger.applyCount(getApplicationContext(), utils.getInt("badge_count", 0));
        }
    }

}