package com.coffee.finder.util.foursquarehelper;

import fi.foyt.foursquare.api.entities.Location;

/**
 * Created by Josh on 12/02/2015.
 */
public class LocationStub extends Location {

    //Location stub created for unit testing purposes

    Double distance;

    public LocationStub(Double distance) {
        this.distance = distance;
    }

    @Override
    public Double getDistance() {
        return distance;
    }
}
