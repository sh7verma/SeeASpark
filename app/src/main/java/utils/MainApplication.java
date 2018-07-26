package utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;


public class MainApplication extends MultiDexApplication {

    private static MainApplication instance;
    public static final String TAG = MainApplication.class
            .getSimpleName();
    public static boolean isLandingAvailable;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        FirebaseApp.initializeApp(this);
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

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
