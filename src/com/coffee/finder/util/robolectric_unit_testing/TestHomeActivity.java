package com.coffee.finder.util.robolectric_unit_testing;

/**
 * Created by Josh on 10/02/2015.
 */

import android.app.Fragment;
import android.view.Menu;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.coffee.finder.HomeActivity;

import com.coffee.finder.ListFragment;
import com.coffee.finder.R;
import com.coffee.finder.util.customlistadapter.CustomExpandableListAdapter;
import com.coffee.finder.util.customlistadapter.ListCategory;
import com.coffee.finder.util.customlistadapter.ListItemDetails;
import com.coffee.finder.util.customquicksort.CustomQuickSort;
import com.coffee.finder.util.foursquarehelper.CompactVenueStub;
import com.coffee.finder.util.foursquarehelper.FoursquareHelper;
import com.coffee.finder.util.locationmanager.CurrentLocationListener;
import com.coffee.finder.util.locationmanager.CurrentLocationManager;
import com.coffee.finder.util.networkmanager.CustomNetworkManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TestHomeActivity {

    @Test
    public void onCreateActionBarTest() throws Exception {
        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);
        activityController.setup();

        //Make sure the generic split action bar is loading alongside the custom action bar.
        final Menu menu = Robolectric.shadowOf(activityController.get()).getOptionsMenu();

        assertTrue(menu.findItem(R.id.action_bar_status_text).getTitle() != null);

        //Make sure the action bar is loading correctly
        View actionBarView = activityController.get().getActionBar().getCustomView();

        ImageButton btn = (ImageButton)actionBarView.findViewById(R.id.actionbar_top_btn_refresh);
        ImageView iv = (ImageView)actionBarView.findViewById(R.id.actionbar_top_launcher_icon);
        TextView tv = (TextView)actionBarView.findViewById(R.id.actionbar_top_title);

        assertNotNull(btn);
        assertNotNull(iv);
        assertNotNull(tv);
        assertTrue(btn.hasOnClickListeners());

    }

    @Test
    public void onCreateManagerTest() throws Exception {

        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);
        activityController.setup();

        //Make sure the managers have all initialised
        assertNotNull(CurrentLocationManager.get());
        assertNotNull(CustomNetworkManager.get());
        assertNotNull(FoursquareHelper.get());

        //Make sure the FourSquareAPI has loaded correctly
        assertTrue(FoursquareHelper.get().hasFourSquareAPILoaded());

        //Make sure that geolocation updates are being listened to
        assertTrue(CurrentLocationManager.get().hasValidLocationListener());
    }

    @Test
    public void ListFragmentTest() {
        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);
        activityController.setup();

        //Check that ListFragment exists and that it's resolved as the correct class
        Fragment fragment = activityController.get().getFragmentManager().findFragmentById(R.id.fragment_shoplist);

        assertNotNull(fragment);
        assertTrue(fragment.getClass() == ListFragment.class);

    }

    @Test
    public void ListFragmentCreateListCategoryTest() {

        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);
        activityController.setup();

        ListFragment listFragment = (ListFragment)activityController.get().getFragmentManager().findFragmentById(R.id.fragment_shoplist);

        //Check that createCategory function creates valid outputs
        ListCategory listCategory = listFragment.createCategory("DummyName", "DummyAddress", "DummyDist", 0, "DummyVenueID");

        assertNotNull(listCategory);
        assertNotNull(listCategory.getName());
        assertNotNull(listCategory.getAddress());
        assertNotNull(listCategory.getDist());
        assertNotNull(listCategory.getId());
        assertNotNull(listCategory.getVenueID());

    }

    @Test
    public void ListFragmentCreateListDetailsTest() {
        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);
        activityController.setup();

        ListFragment listFragment = (ListFragment)activityController.get().getFragmentManager().findFragmentById(R.id.fragment_shoplist);

        //Check that createItems function creates valid outputs
        List<ListItemDetails> listDetails = listFragment.createItems("DummyHours", "DummyRating", "DummyLocation", 1);

        assertNotNull(listDetails);
        assertTrue(listDetails.size() == 1);
        assertNotNull(listDetails.get(0).getHours());
        assertNotNull(listDetails.get(0).getRating());
        assertNotNull(listDetails.get(0).getGeolocation());

    }

    @Test
    public void ListFragmentGPSListenerTest() {
        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);
        activityController.setup();

        ListFragment listFragment = (ListFragment)activityController.get().getFragmentManager().findFragmentById(R.id.fragment_shoplist);

        //GPS location shouldn't update automatically for unit tests
        assertTrue(CurrentLocationListener.longitude == 0);

        //Check that ListFragment is listening for GPS location updates
        CurrentLocationListener listener = (CurrentLocationListener)CurrentLocationManager.get().getCurrentLocationListener();

        assertNotNull(listener);
        assertTrue(listener.hasLocationUpdatedEvent());

    }

    @Test
    public void CustomExpandableListAdapterTest() {

        //Make sure that the CustomExpandableListAdapter is handling inputs correctly

        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);
        activityController.setup();

        ListFragment listFragment = (ListFragment)activityController.get().getFragmentManager().findFragmentById(R.id.fragment_shoplist);

        List<ListCategory> catList = new ArrayList<ListCategory>();
        ExpandableListView exList = new ExpandableListView(activityController.get());
        CustomExpandableListAdapter exAdpt = new CustomExpandableListAdapter(catList, activityController.get());

        exList.setAdapter(exAdpt);

        ListCategory listCategory = listFragment.createCategory("DummyName", "DummyAddress", "DummyDist", 0, "DummyVenueID");
        List<ListItemDetails> listDetails = listFragment.createItems("DummyHours", "DummyRating", "DummyLocation", 1);


        listCategory.setItemList(listDetails);

        catList.add(listCategory);

        exAdpt.notifyDataSetChanged();

        assertNotNull(exAdpt.getGroup(0));
        assertNotNull(exAdpt.getChild(0,0));
        assertTrue(exAdpt.getGroupCount() == 1);
        assertTrue(exAdpt.getChildrenCount(0) == 1);

    }

    @Test
    public void CustomQuickSortTest() {

        //Make sure that the QuickSort algorithm is sorting items correctly.
        CompactVenueStub[] stubs = new CompactVenueStub[5];

        stubs[0] = new CompactVenueStub(12d);
        stubs[1] = new CompactVenueStub(7d);
        stubs[2] = new CompactVenueStub(28d);
        stubs[3] = new CompactVenueStub(4d);
        stubs[4] = new CompactVenueStub(438d);

        CustomQuickSort.sort(stubs);

        Double highest = 0d;

        for (int i = 0; i < 5; i++) {
            assertTrue(stubs[i].getLocation().getDistance() > highest);
            highest = stubs[i].getLocation().getDistance();
        }

    }

    @Test
    public void onUserLeaveHintTest() throws Exception {

        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);

        activityController.setup();
        activityController.userLeaving();

        //Make sure that GPS is not updating.
        assertFalse(CurrentLocationManager.get().isGeoLocationUpdating());
        assertFalse(CurrentLocationManager.get().hasValidLocationListener());

        //Make sure that the app gets flagged as being in the background
        assertTrue(activityController.get().isInBackground());
    }

    @Test
    public void onResumeTest() throws Exception {
        ActivityController<HomeActivity> activityController = Robolectric.buildActivity(HomeActivity.class);

        activityController.setup();
        activityController.userLeaving();
        activityController.resume();
        activityController.postResume();

        //Make sure that GPS updates resume correctly
        assertTrue(CurrentLocationManager.get().isGeoLocationUpdating());
        assertTrue(CurrentLocationManager.get().hasValidLocationListener());

        //Make sure that the app is flagged as not being in the background
        assertFalse(activityController.get().isInBackground());
    }

}
