package utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;


public class MainApplication extends Application {

    private static MainApplication instance;
    public static final String TAG = MainApplication.class
            .getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Foreground.init(this);
        instance = this;
    }

    public static MainApplication getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
