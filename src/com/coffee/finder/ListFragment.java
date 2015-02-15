package com.coffee.finder;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import com.coffee.finder.util.customlistadapter.CustomExpandableListAdapter;
import com.coffee.finder.util.customlistadapter.ListCategory;
import com.coffee.finder.util.customlistadapter.ListItemDetails;
import com.coffee.finder.util.foursquarehelper.FoursquareHelper;
import com.coffee.finder.util.locationmanager.CurrentLocationListener;
import com.coffee.finder.util.locationmanager.CurrentLocationManager;
import com.coffee.finder.util.locationmanager.LocationEvent;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Josh on 10/02/2015.
 */
public class ListFragment extends Fragment {

    private View rootView;
    private List<ListCategory> catList;

    private ExpandableListView exList;
    private CustomExpandableListAdapter exAdpt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);

        catList = new ArrayList<ListCategory>();

        //If our location has already updated when the fragment opens, use that data for our search
        if (CurrentLocationListener.longitude != 0) {
            Log.d("ListFragment", "Location Already found");
            setupListUpdateRunnable();
        }
        //Otherwise, wait until we get GPS data and then make a search
        else {
            Log.d("ListFragment", "Setting update notification event");

            CurrentLocationManager.get().setupLocationUpdatedEvent(new LocationEvent() {
                @Override
                protected void onLocationUpdated() {
                    setupListUpdateRunnable();
                }
            });
        }

        exList = (ExpandableListView) rootView.findViewById(R.id.listview_base);
        exAdpt = new CustomExpandableListAdapter(catList, getActivity());
        exList.setAdapter(exAdpt);

        exList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                String venueID = catList.get(groupPosition).getVenueID();

                //When a group item expands, query Foursquare for more in depth venue data
                FoursquareHelper.get().setCompleteVenueData(venueID, exAdpt, groupPosition);

            }
        });

        return rootView;
    }

    private void setupListUpdateRunnable() {

        FoursquareHelper.get().searchVenues(rootView, new Runnable() {
            @Override
            public void run() {
                Log.d("ListFragment", "Updating List");

                //FoursquareHelper handles the actual search.
                //Once that completes, get the results and fill out our list
                Result<VenuesSearchResult> searchResults = FoursquareHelper.get().GetPreviousSearchResults();

                if (searchResults == null | searchResults.getResult() == null)
                    return;

                LinearLayout progressBarView = (LinearLayout) rootView.findViewById(R.id.progress_bar_view);
                progressBarView.setVisibility(View.GONE);

                catList.clear();

                for (int i = 0; i < searchResults.getResult().getVenues().length; i++)
                {
                    //Get data for each venue. Name, Distance, Address, Geolocation and VenueID
                    CompactVenue venue = searchResults.getResult().getVenues()[i];

                    String name = venue.getName();
                    String address = venue.getLocation().getAddress();

                    if (address == null)
                        address = "Geolocation provided";

                    float dist = venue.getLocation().getDistance().floatValue();
                    String distFormatted = dist+"m";

                    if (dist > 1000f)
                        distFormatted = dist/1000f + "km";

                    //Create the group item for our expandable list with general information for our venue
                    ListCategory cat = createCategory(name, address, distFormatted, i, venue.getId());

                    String geoLocation = venue.getLocation().getLat()+","+venue.getLocation().getLng();

                    //Create list details for our venue, that update when the group item is expanded
                    cat.setItemList(createItems("Loading hours...", "Loading rating...", geoLocation, 1));
                    catList.add(cat);

                }

                exAdpt.notifyDataSetChanged();
                CurrentLocationManager.get().updateLocationText();

            }
        });
    }

    public ListCategory createCategory(String name, String address, String dist, long listID, String venueID) {
        return new ListCategory(listID, name, address, dist, venueID);
    }


    public List<ListItemDetails> createItems(String hours, String rating, String geolocation, int num) {
        List<ListItemDetails> result = new ArrayList<ListItemDetails>();

        for (int i=0; i < num; i++) {
            ListItemDetails item = new ListItemDetails(i, 0, hours, rating, geolocation);
            result.add(item);
        }

        return result;
    }

}
