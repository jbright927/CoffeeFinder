package com.coffee.finder.util.locationmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import com.coffee.finder.HomeActivity;
import com.coffee.finder.R;
import com.coffee.finder.util.networkmanager.CustomNetworkManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Josh on 10/02/2015.
 */
public class CurrentLocationManager {

    public static CurrentLocationManager instance;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Context ctx;

    private boolean isListeningForGeolocationUpdates = false;

    private CurrentLocationManager(Context ctx) {
        this.ctx = ctx;
    }

    public static void init(Context ctx) {
        if (instance != null) {
            Log.d("CurrentLocationManager", "CurrentLocationManager already initialised, not attempting to reinitialise");
            return;
        }

        instance = new CurrentLocationManager(ctx);
        Log.d("CurrentLocationManager", "CurrentLocationManager initialised");

        instance.setupLocationUpdates();

    }

    public boolean isGeoLocationUpdating() { return isListeningForGeolocationUpdates; }

    public boolean hasValidLocationListener() { return locationListener != null; }

    public LocationListener getCurrentLocationListener() { return locationListener; }

    public void directToPhone(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        ctx.startActivity(intent);
    }

    public void directToGoogleMaps(String destination) {
        String currentLocation = CurrentLocationListener.latitude+","+CurrentLocationListener.longitude;

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + currentLocation + "&daddr=" + destination));
        ctx.startActivity(intent);
    }

    public void updateLocationText() {
        Menu optionsMenu = ((HomeActivity)ctx).getSplitActionBarMenu();

        double lat = CurrentLocationListener.latitude;
        double lng = CurrentLocationListener.longitude;

        String addressString = "";

        //If the split action bar hasn't initialised, don't attempt to update it.
        if (optionsMenu == null)
            return;

        Log.d("CurrentLocationManager", "Updating Location Text");

        final LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        //If GPS is not enabled, add that to the message
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER) ) {
            addressString = "GPS Disabled";
        }

        //If we can't access the internet, add that to the message as well
        if (!CustomNetworkManager.get().getIsMobileDataEnabled() && !CustomNetworkManager.get().getIsWifiConnected())
        {
            if (!addressString.equals(""))
                addressString += ", ";

            addressString += "Internet Not Connected";
        }

        //If GPS is disabled or we can't access the internet, don't attempt to display address data
        if (addressString.equals("")) {

            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());

            //Get the nearest street address for the user and add that to the message
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

                addressString += addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);
                Log.d("CurrentLocationManager", addressString);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Log.d("CurrentLocationManager", "User status output: "+addressString);

        optionsMenu.findItem(R.id.action_bar_status_text).setTitle(addressString);
    }

    public void setupLocationUpdates() {

        if (locationManager != null) {
            Log.d("CurrentLocationManager", "Location already updating...");
            return;
        }

        //Listening for geolocation updates also lets us know when GPS is enabled or disabled

        Log.d("CurrentLocationManager", "Setting up location updates");
        locationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new CurrentLocationListener();
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 300000, 50, locationListener);
        isListeningForGeolocationUpdates = true;

        //Display a warning message if GPS is disabled

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("CurrentLocationManager", "GPS is not enabled");

            AlertDialog.Builder dialogue = new AlertDialog.Builder(ctx);
            dialogue.setTitle("Error");
            dialogue.setMessage("GPS is not enabled. Enable GPS before attempting to make a search.");
            dialogue.setPositiveButton("Continue", null);
            dialogue.show();
            return;
        }

        //Attempt to get last known location from old GPS data, if anything exists.

        Log.d("CurrentLocationManager", "Requesting last known location");
        requestLastKnownLocation();

    }

    public void requestLastKnownLocation() {
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation != null) {
            locationListener.onLocationChanged(lastKnownLocation);
        }
    }

    public void stopLocationUpdates() {

        Log.d("CurrentLocationManager", "Stopping location updates...");

        if (locationListener != null)
            locationManager.removeUpdates(locationListener);

        locationListener = null;
        locationManager = null;
        isListeningForGeolocationUpdates = false;

    }

    public static CurrentLocationManager get() {
        if (instance == null)
            throw new RuntimeException("Please initialise FoursquareHelper first.");

        return instance;
    }

    public void setupLocationUpdatedEvent(LocationEvent event) {

        if (locationListener != null)
            ((CurrentLocationListener)locationListener).setLocationUpdatedEvent(event);

    }

}
