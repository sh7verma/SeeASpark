package services;

import android.app.NotificationChannel;
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
import com.google.gson.Gson;
import com.seeaspark.BroadcastActivity;
import com.seeaspark.ConversationActivity;
import com.seeaspark.CreateProfileActivity;
import com.seeaspark.HandshakeActivity;
import com.seeaspark.LandingActivity;
import com.seeaspark.QuestionnariesActivity;
import com.seeaspark.R;
import com.seeaspark.ReviewActivity;

import java.util.Map;

import models.SignupModel;
import utils.Constants;
import utils.Utils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Utils utils;
    private LocalBroadcastManager broadcaster;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

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
        } else if (messageBody.get("push_type").equalsIgnoreCase("2")) {
            if (utils.getInt("inside_review", 0) == 0 &&
                    utils.getInt("inside_reviewFull", 0) == 0) {
                /// outside review activity
                intent = new Intent(this, QuestionnariesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ringNotification(intent, message, 0, messageBody.get("body"));
                if (utils.getInt("inside_profileDialog", 0) == 1) {
                    utils.setInt("open_questionnaries", 1);
                }
            } else {
                /// inside review activity
                Intent notificationIntent = new Intent(Constants.REVIEW);
                broadcaster.sendBroadcast(notificationIntent);
            }
        } else if (messageBody.get("push_type").equalsIgnoreCase("3")) {

            SignupModel.ResponseBean mMatchModel = new SignupModel.ResponseBean();
            mMatchModel.setAccess_token(messageBody.get("access_token"));
            mMatchModel.setFull_name(messageBody.get("full_name"));
            mMatchModel.setGender(messageBody.get("gender"));
            SignupModel.ResponseBean.AvatarBean avatarBean = new SignupModel.ResponseBean.AvatarBean();
            avatarBean.setAvtar_url(messageBody.get("avatar"));
            mMatchModel.setAvatar(avatarBean);
            mMatchModel.setId(Integer.parseInt(messageBody.get("user_id")));

            String matchData = new Gson().toJson(mMatchModel);

            if (utils.getInt("Background", 0) == 1) {
                intent = new Intent(this, LandingActivity.class);
                intent.putExtra("matchData", matchData);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ringNotification(intent, message, 1, messageBody.get("title"));
            } else {
                intent = new Intent(this, HandshakeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("matchData", matchData);
                startActivity(intent);
            }
        } else if (messageBody.get("push_type").equalsIgnoreCase("5")) {
            if (utils.getInt("Background", 0) == 1) {// background
                intent = new Intent(this, LandingActivity.class);
                intent.putExtra("broadcastData", "Yes");
                intent.putExtra("broadcastTitle", messageBody.get("title"));
                intent.putExtra("broadcastMessage", message);
                ringNotification(intent, message, 0, messageBody.get("title"));
            } else {//in app
                intent = new Intent(this, BroadcastActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("broadcastTitle", messageBody.get("title"));
                intent.putExtra("broadcastMessage", message);
                startActivity(intent);
            }
        } else if (messageBody.get("push_type").equalsIgnoreCase("6")) {
            if (utils.getInt("inside_review", 0) == 0 &&
                    utils.getInt("inside_reviewFull", 0) == 0) {
                /// outside review activity
                intent = new Intent();
                ringNotification(intent, message, 0, messageBody.get("body"));
            } else {
                /// inside review activity
                Intent notificationIntent = new Intent(Constants.UNVERIFIED);
                broadcaster.sendBroadcast(notificationIntent);
            }
        } else if (messageBody.get("push_type").equalsIgnoreCase("7")) {
            utils.setInt("document_verified", 2);
            if (utils.getInt("inside_review", 0) == 0 &&
                    utils.getInt("inside_reviewFull", 0) == 0) {
                /// outside review activity
                intent = new Intent(this, ReviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ringNotification(intent, message, 0, messageBody.get("title"));
            } else {
                /// inside review activity
                Intent notificationIntent = new Intent(Constants.REVIEW);
                notificationIntent.putExtra("type", "inReview");
                notificationIntent.putExtra("displayMessage", messageBody.get("message"));
                broadcaster.sendBroadcast(notificationIntent);
            }
        } else if (messageBody.get("push_type").equalsIgnoreCase("8")) {
            if (utils.getString("access_token", "").equals(messageBody.get("access_token"))) {
                if (!utils.getString("chat_dialog_id", "").equals(messageBody.get("chat_dialog_id"))) {
                    utils.setString("participant_ids", messageBody.get("chat_dialog_id"));
                    intent = new Intent(this, ConversationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ringNotification(intent, message, 0, messageBody.get("sender_name"));
                }
            }
        }
    }

    void ringNotification(Intent intent, String mess, int notificationId, String title) {
        int mIcon;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mIcon = R.drawable.ic_app_icon;
        else
            mIcon = R.mipmap.ic_launcher_studio;

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            notificationChannel.enableVibration(true);
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }

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
            mIcon = R.drawable.ic_app_icon;
        else
            mIcon = R.mipmap.ic_launcher_studio;

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