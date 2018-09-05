package utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import helper.FirebaseListeners;

@SuppressLint("NewApi")
public class Foreground implements Application.ActivityLifecycleCallbacks {
    public static final long CHECK_DELAY = 500;
    public static final String TAG = Foreground.class.getName();
    SharedPreferences sp;
    DatabaseReference mFirebaseConfig;
    Utils mUtil;

    public interface Listener {

        public void onBecameForeground();

        public void onBecameBackground();

    }

    private static Foreground instance;

    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    private Runnable check;

    /**
     * Its not strictly necessary to use this method - _usually_ invoking get
     * with a Context gives us a path to retrieve the Application and
     * initialise, but sometimes (e.g. in view_tul harness) the ApplicationContext
     * is != the Application, and the docs make no guarantees.
     *
     * @param application
     * @return an initialised Foreground instance
     */
    @SuppressLint("NewApi")
    public static Foreground init(Application application) {
        if (instance == null) {
            instance = new Foreground();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static Foreground get(Application application) {
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static Foreground get(Context ctx) {
        if (instance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            throw new IllegalStateException(
                    "Foreground is not initialised and "
                            + "cannot obtain the Application object");
        }
        return instance;
    }

    public static Foreground get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Foreground is not initialised - invoke "
                            + "at least once with parameterised init/get");
        }
        return instance;
    }

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        sp = activity.getSharedPreferences(activity.getPackageName(),
                Context.MODE_PRIVATE);
        mFirebaseConfig = FirebaseDatabase.getInstance().getReference().child(Constants.USERS);
        mUtil = new Utils(activity);

        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground) {
            Log.i(TAG, "went foreground");
            setOnline();
            for (Listener l : listeners) {
                try {
                    l.onBecameForeground();
                } catch (Exception exc) {
                    Log.e(TAG, "Listener threw exception!", exc);
                }
            }
        } else {
            Log.i(TAG, "still foreground");
            setOnline();
        }


    }

    @Override
    public void onActivityPaused(final Activity activity) {
        sp = activity.getSharedPreferences(activity.getPackageName(),
                Context.MODE_PRIVATE);
        paused = true;
        mFirebaseConfig = FirebaseDatabase.getInstance().getReference().child("Users");

        if (check != null)
            handler.removeCallbacks(check);

        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    Log.i(TAG, "went background");
                    setOffline();

                    for (Listener l : listeners) {
                        try {
                            l.onBecameBackground();
                        } catch (Exception exc) {
                            Log.e(TAG, "Listener threw exception!", exc);
                        }
                    }
                } else {
                    Log.i(TAG, "still foreground");
                    setOnline();
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void setOnline() {
        //andar hai
        sp.edit().putInt("Background", 0).apply();
        Log.e("Inside ", "Yes");
        FirebaseListeners.getTime(mUtil);
        if (!TextUtils.isEmpty(sp.getString("access_token", "")) && sp.getInt("profile_status", 0) == 2) {
            mFirebaseConfig.child("id_" + sp.getString("user_id", "")).child("online_status").setValue(Constants.ONLINE_LONG);
//            (new SetOnline(sp.getString("user_access_token", ""), 1)).execute();
        }

    }

    private void setOffline() {
        /// bhar hai
        sp.edit().putInt("Background", 1).apply();
        Log.e("OutSide  ", "Yes");
        if (!TextUtils.isEmpty(sp.getString("access_token", "")) && sp.getInt("profile_status", 0) == 2) {
//            long time = Constants.getUtcTime((Calendar.getInstance()).getTimeInMillis());
//            Log.e("local", "" + time);
//            Log.e("server", "" + ServerValue.TIMESTAMP);
            mFirebaseConfig.child("id_" + sp.getString("user_id", "")).child("online_status").setValue(ServerValue.TIMESTAMP);
//            (new SetOnline(sp.getString("user_access_token", ""), 0)).execute();
        }
    }
}

