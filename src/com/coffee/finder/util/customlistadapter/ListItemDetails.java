package com.coffee.finder.util.customlistadapter;

import java.io.Serializable;

/**
 * Created by Josh on 10/02/2015.
 */
public class ListItemDetails implements Serializable {

    private long id;
    private int imgId;
    private String rating;
    private String hours;
    private String geolocation;
    private String contact;

    public ListItemDetails(long id, int imgId, String name, String hours, String geolocation) {
        this.id = id;
        this.imgId = imgId;
        this.rating = name;
        this.hours = hours;
        this.geolocation = geolocation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getGeolocation() { return geolocation; }

    public void setGeolocation(String geolocation) { this.geolocation = geolocation; }

    public String getContact() { return contact; }

    public void setContact(String contact) { this.contact = contact; }

}
