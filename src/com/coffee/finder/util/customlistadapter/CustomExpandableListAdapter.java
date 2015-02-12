package com.coffee.finder.util.customlistadapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.coffee.finder.R;
import com.coffee.finder.util.locationmanager.CurrentLocationManager;

import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private List<ListCategory> catList;
    private Context ctx;

    public CustomExpandableListAdapter(List<ListCategory> catList, Context ctx) {

        this.catList = catList;
        this.ctx = ctx;

    }

    public ListCategory getListCategory(int categoryID) {
        return catList.get(categoryID);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return catList.get(groupPosition).getItemList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return catList.get(groupPosition).getItemList().get(childPosition).getId();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_details, parent, false);
        }

        TextView itemRating = (TextView) v.findViewById(R.id.list_details_rating);
        TextView itemHours = (TextView) v.findViewById(R.id.list_details_hours);

        ImageButton itemCall = (ImageButton) v.findViewById(R.id.list_details_call);
        ImageButton itemDirections = (ImageButton) v.findViewById(R.id.list_details_search);

        final ListItemDetails det = catList.get(groupPosition).getItemList().get(childPosition);

        if (det.getContact() != null) {
            itemCall.setVisibility(View.VISIBLE);
            itemCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CurrentLocationManager.get().directToPhone(det.getContact());
                }
            });
        }
        else {
            itemCall.setVisibility(View.INVISIBLE);
        }

        itemDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentLocationManager.get().directToGoogleMaps(det.getGeolocation());
            }
        });

        itemRating.setText(det.getRating());
        itemHours.setText(det.getHours());

        return v;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int size = catList.get(groupPosition).getItemList().size();
        System.out.println("Child for group ["+groupPosition+"] is ["+size+"]");
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return catList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return catList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return catList.get(groupPosition).getId();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_category, parent, false);
        }

        TextView groupName = (TextView) v.findViewById(R.id.list_category_name);
        TextView groupAddress = (TextView) v.findViewById(R.id.list_category_address);
        TextView groupDist = (TextView) v.findViewById(R.id.list_category_dist);

        ListCategory cat = catList.get(groupPosition);

        groupName.setText(cat.getName());
        groupAddress.setText(cat.getAddress());
        groupDist.setText(cat.getDist());

        return v;

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}