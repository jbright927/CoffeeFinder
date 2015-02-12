package com.coffee.finder.util.customlistadapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 10/02/2015.
 */
public class ListCategory implements Serializable {

    private long id;
    private String name;
    private String address;
    private String dist;
    private String venueID;

    private List<ListItemDetails> itemList = new ArrayList<ListItemDetails>();

    public ListCategory(long id, String name, String address, String dist, String venueID) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.dist = dist;
        this.venueID = venueID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVenueID() { return venueID; }

    public void setVenueID(String venueID) { this.venueID = venueID; }

    public String getDist() { return dist; }

    public void setDist(String dist) {this.dist = dist; }

    public List<ListItemDetails> getItemList() {
        return itemList;
    }

    public void setItemList(List<ListItemDetails> itemList) {
        this.itemList = itemList;
    }

}
