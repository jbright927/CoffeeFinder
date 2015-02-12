package com.coffee.finder.util.foursquarehelper;

import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.Location;

/**
 * Created by Josh on 12/02/2015.
 */
public class CompactVenueStub extends CompactVenue {

    //Stub created for unit testing purposes

    LocationStub locationStub;

    public CompactVenueStub (Double dist) {
        this.locationStub = new LocationStub(dist);
    }

    @Override
    public Location getLocation() {
        return locationStub;
    }
}
