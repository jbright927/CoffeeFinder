package com.coffee.finder.util.foursquarehelper;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.coffee.finder.R;
import com.coffee.finder.util.customlistadapter.CustomExpandableListAdapter;
import com.coffee.finder.util.customlistadapter.ListItemDetails;
import com.coffee.finder.util.customquicksort.CustomQuickSort;
import com.coffee.finder.util.locationmanager.CurrentLocationListener;
import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompleteVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

/**
 * Created by Josh on 10/02/2015.
 */
public class FoursquareHelper {

    public static FoursquareHelper instance;
    private Context ctx;
    private FoursquareApi foursquareApi;
    private Result<VenuesSearchResult> searchResults;
    public Runnable listUpdateRunnable;

    private String clientID = "ACAO2JPKM1MXHQJCK45IIFKRFR2ZVL0QASMCBCG5NPJQWF2G";
    private String clientSecret = "YZCKUYJ1WHUV2QICBXUBEILZI1DMPUIDP5SHV043O04FKBHL";

    //Checkin search intent displays the venues that you are most likely to visit, given current position
    private String searchIntent = "checkin";

    //The Category ID of coffee shops on Foursquare
    private String coffeeShopCategoryID = "4bf58dd8d48988d1e0931735";

    //Search a 7.5km radius
    private int searchRadius = 7500;

    private FoursquareHelper(Context ctx) {
        this.ctx = ctx;
    }

    public static void init(Context ctx) {
        if (instance != null) {
            Log.d("FoursquareHelper", "FoursquareHelper already initialised, not attempting to reinitialise");
            return;
        }

        instance = new FoursquareHelper(ctx);
        Log.d("FoursquareHelper", "FoursquareHelper initialised");
    }

    public static FoursquareHelper get() {
        if (instance == null)
            throw new RuntimeException("Please initialise FoursquareHelper first.");

        return instance;
    }

    public boolean hasFourSquareAPILoaded() {
        return foursquareApi != null;
    }

    public boolean hasSearchUpdateRunnable() { return listUpdateRunnable != null; }

    public void loadFourSquareAPI() {

        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {

            Exception APIException;

            protected Void doInBackground(Void... Void) {
                try {
                    // First we need a initialize FoursquareApi.
                    foursquareApi = new FoursquareApi(clientID, clientSecret, "http://localhost/app");
                    return null;

                } catch (Exception e) {
                    APIException = e;
                    return null;
                }
            }

            protected void onPostExecute(Void aVoid) {
                if (APIException != null)
                    throw new RuntimeException(APIException);
            }
        };

        task.execute(null, null, null);

    }

    public void searchVenues(View rootView, final Runnable listUpdateRunnable) {
        LinearLayout progressBarView = (LinearLayout) rootView.findViewById(R.id.progress_bar_view);
        progressBarView.setVisibility(View.VISIBLE);

        this.listUpdateRunnable = listUpdateRunnable;
        searchVenues();
    }

    public void searchVenues() {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            Exception APIException;
            boolean wasSuccessful = false;
            String latLong = CurrentLocationListener.latitude+","+CurrentLocationListener.longitude;

            protected Void doInBackground(Void... Void) {
                try {
                    // After client has been initialized we can make queries.
                    searchResults = foursquareApi.venuesSearch(latLong, null, null, null, null, null, searchIntent, coffeeShopCategoryID, null, null, null, searchRadius, null);

                    if (searchResults.getMeta().getCode() == 200) {

                        CustomQuickSort.sort(searchResults.getResult().getVenues());
                        wasSuccessful = true;

                        return null;

                    } else {
                        // TODO: Proper error handling
                        System.out.println("Error occured: ");
                        System.out.println("  code: " + searchResults.getMeta().getCode());
                        System.out.println("  type: " + searchResults.getMeta().getErrorType());
                        System.out.println("  detail: " + searchResults.getMeta().getErrorDetail());

                        return null;
                    }

                } catch (FoursquareApiException e) {
                    System.out.println("CRITICAL FAILURE");
                    throw new RuntimeException(e);
                }

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (wasSuccessful)
                    listUpdateRunnable.run();
                else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                    dialog.setTitle("Network Error");
                    dialog.setMessage("Couldn't connect to the network");
                    dialog.setPositiveButton("Continue", null);
                    dialog.show();
                }
            }
        };

        task.execute(null, null, null);

    }

    public Result<VenuesSearchResult> GetPreviousSearchResults() {
        if (searchResults != null)
            return searchResults;
        else
            throw new RuntimeException("No valid searches have been made!");
    }


    public void setCompleteVenueData(final String venueID, final CustomExpandableListAdapter exAdpt, final int position) {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            Exception APIException;
            CompleteVenue completeVenue;

            protected Void doInBackground(Void... Void) {
                try {
                    //Search results don't contain complete data for a venue.
                    //Further queries have to be made for data such as hours, contact, etc
                    completeVenue = foursquareApi.venue(venueID).getResult();
                    return null;

                } catch (FoursquareApiException e) {
                    System.out.println("CRITICAL FAILURE");
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                ListItemDetails shopDetails = exAdpt.getListCategory(position).getItemList().get(0);

                if (completeVenue == null) {
                    Log.d("Venue", "Foursquare probably refused the request if you're spamming.");
                    this.execute(null, null, null);
                    return;
                }

                if (completeVenue.getHours() != null) {
                    shopDetails.setHours(completeVenue.getHours().getStatus());
                    Log.d("Venue", "Updating Hours");
                }
                else {
                    shopDetails.setHours("Opening Hours: Unknown");
                }

                if (completeVenue.getRating() != null) {
                    shopDetails.setRating("Rating: " + completeVenue.getRating().toString() + "/10");
                    Log.d("Venue", "Updating Rating");
                }
                else {
                    shopDetails.setRating("Rating: Not rated");
                }

                if (completeVenue.getContact().getPhone() != null)
                {
                    shopDetails.setContact(completeVenue.getContact().getPhone());
                    Log.d("Venue", "Updating Contact");
                }

                exAdpt.notifyDataSetChanged();

            }
        };

        task.execute(null, null, null);

    }
}
