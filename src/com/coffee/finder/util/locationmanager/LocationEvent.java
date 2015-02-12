package com.coffee.finder.util.locationmanager;

/**
 * Created by Josh on 11/02/2015.
 */
public abstract class LocationEvent {

    public LocationEvent(){}

    protected abstract void onLocationUpdated();

}
