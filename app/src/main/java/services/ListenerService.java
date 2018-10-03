package services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.seeaspark.R;

import helper.FirebaseListeners;
import utils.Utils;

/**
 * Created by dev on 3/8/18.
 */

public class ListenerService extends Service {

    Context mContext;
    Utils util;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notification =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher_studio)
                            .setContentIntent(pendingIntent)
                            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.black_color))
                            .build();
            startForeground(63, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        util = new Utils(getApplicationContext());
        if (!TextUtils.isEmpty(util.getString("user_id", ""))) {
            FirebaseListeners.getListenerClass(getApplicationContext()).setProfileListener(util.getString("user_id", ""));
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Intent broadcastIntent = new Intent("ActivityRecognition.RestartService");
//        sendBroadcast(broadcastIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
    }

}
