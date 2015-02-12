package com.coffee.finder.util.networkmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.coffee.finder.util.locationmanager.CurrentLocationManager;

import java.lang.reflect.Method;

/**
 * Created by Josh on 12/02/2015.
 */
public class CustomNetworkManager {

    public static CustomNetworkManager instance;
    private Context ctx;

    private CustomNetworkManager(Context ctx) {
        this.ctx = ctx;
    }

    public static void init(Context ctx) {
        if (instance != null) {
            Log.d("CurrentLocationManager", "CurrentLocationManager already initialised, not attempting to reinitialise");
            return;
        }

        instance = new CustomNetworkManager(ctx);
        Log.d("CurrentLocationManager", "CurrentLocationManager initialised");

    }

    public static CustomNetworkManager get() {
        if (instance == null)
            throw new RuntimeException("Please initialise CustomNetworkManager first.");

        return instance;
    }

    public void setupInternetConnectivityListener() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService( Context.CONNECTIVITY_SERVICE );

        //Listen for updates to internet connectivity.
        //If we go offline, display appropriate information on the split action bar

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ctx.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                CurrentLocationManager.get().updateLocationText();
            }
        }, intentFilter);

    }

    public boolean getIsMobileDataEnabled() {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
            return mobileDataEnabled;
        } catch (Exception e) {
            // Some problem accessible private API
            return false;
        }
    }

    public boolean getIsWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifi.isConnected();
    }
}
