package com.coffee.finder.util.locationmanager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Josh on 10/02/2015.
 */
public class CurrentLocationListener implements LocationListener {

    public static double latitude;
    public static double longitude;

    private LocationEvent locationUpdatedEvent;

    public void setLocationUpdatedEvent(LocationEvent locationUpdatedEvent) {
        this.locationUpdatedEvent = locationUpdatedEvent;
    }

    public boolean hasLocationUpdatedEvent() { return locationUpdatedEvent != null; }

    @Override
    public void onLocationChanged(Location loc)
    {
        latitude=loc.getLatitude();
        longitude=loc.getLongitude();

        if (locationUpdatedEvent != null) {
            Log.d("CurrentLocationListener", "Firing location updated event");
            locationUpdatedEvent.onLocationUpdated();
            locationUpdatedEvent = null;
        }

        CurrentLocationManager.get().updateLocationText();

        Log.d("GPS", "LOCATION CHANGED");
        Log.d("GPS", "Lat: "+loc.getLatitude()+", Long: "+loc.getLatitude());
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        //print "Currently GPS is Disabled";
        Log.d("CurrentLocationListener", "GPS was Disabled");
    }
    @Override
    public void onProviderEnabled(String provider)
    {
        //print "GPS got Enabled";
        //Set up GPS location updates if they're not currently set up
        //Also try to get old GPS data until the user's geolocation updates
        CurrentLocationManager.get().setupLocationUpdates();
        CurrentLocationManager.get().requestLastKnownLocation();
        Log.d("CurrentLocationListener", "GPS was enabled");
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        //Occurs whenever geolocation checks whether it should update.
        Log.d("CurrentLocationListener", "GPS status changed");
    }

}
