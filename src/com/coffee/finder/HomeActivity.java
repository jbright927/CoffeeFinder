package com.coffee.finder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import com.coffee.finder.util.foursquarehelper.FoursquareHelper;
import com.coffee.finder.util.locationmanager.CurrentLocationManager;
import com.coffee.finder.util.networkmanager.CustomNetworkManager;

public class HomeActivity extends Activity {


    private boolean inBackground = true;
    private Menu splitActionBarMenu;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("SplashScreenActivity", "onCreate");

        setupCustomActionBar();
        setContentView(R.layout.main);

        FoursquareHelper.init(this);
        FoursquareHelper.get().loadFourSquareAPI();

        CurrentLocationManager.init(this);

        CustomNetworkManager.init(this);
        CustomNetworkManager.get().setupInternetConnectivityListener();

        setupListFragment();
        addActionBarRefreshHandler();

    }

    public void setupCustomActionBar() {
        //Custom action bar allows icons and text to display on the top and bottom parts of the split action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        View view = View.inflate(getApplicationContext(), R.layout.actionbar_top,
                null);
        actionBar.setCustomView(view);
    }

    public void addActionBarRefreshHandler() {
        //Set up the refresh button to search for nearby venues again
        ImageButton btn = (ImageButton)getActionBar().getCustomView().findViewById(R.id.actionbar_top_btn_refresh);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoursquareHelper.get().searchVenues();
            }
        });
    }

    public Menu getSplitActionBarMenu() {
        return splitActionBarMenu;
    }

    public boolean isInBackground() { return inBackground; }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        this.splitActionBarMenu = menu;

        //As soon as our split action bar is created, update it with useful information
        Log.d("splitActionBarMenu", "Created");
        CurrentLocationManager.get().updateLocationText();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("SplashScreenActivity", "onResume");

        //onResume also triggers the first time we open the app
        if (inBackground) {
            // You just came from the background
            inBackground = false;
            // GPS updates won't occur while the app is in the background, so re-enable those
            CurrentLocationManager.get().setupLocationUpdates();
        }
        else {
            // You just returned from another activity within your own app
        }
    }

    @Override
    public void onUserLeaveHint() {
        super.onUserLeaveHint();

        Log.d("SplashScreenActivity", "onUserLeaveHint");

        //App is getting put into the background. Stop location updates
        CurrentLocationManager.get().stopLocationUpdates();

        inBackground = true;
    }

    private void setupListFragment() {

        getFragmentManager().beginTransaction();
        Fragment newFragment = new ListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_shoplist, newFragment);
        transaction.commit();

    }


}
