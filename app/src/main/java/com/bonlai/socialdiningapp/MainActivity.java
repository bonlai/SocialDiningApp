package com.bonlai.socialdiningapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;


import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bonlai.socialdiningapp.helpers.BottomBarAdapter;
import com.bonlai.socialdiningapp.main.GatheringFragment;
import com.bonlai.socialdiningapp.main.GatheringFragment.Mode;
import com.bonlai.socialdiningapp.main.ProfileFragment.ProfileMode;
import com.bonlai.socialdiningapp.helpers.NoSwipePager;
import com.bonlai.socialdiningapp.main.ProfileFragment;
import com.bonlai.socialdiningapp.main.RestaurantFragment;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.network.AuthAPIclient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private NoSwipePager viewPager;
    private AHBottomNavigation bottomNavigation;
    private BottomBarAdapter pagerAdapter;
    private TabLayout tabLayout;
    private boolean notificationVisible = false;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        outState.putSerializable("Token", Token.getToken().getKey());

    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.i("MainAcitivty", "Location: " + location.getLatitude() + " " + location.getLongitude());
                AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
                Call<ResponseBody> register = service.returnLatLong(MyUserHolder.getInstance().getUser().getPk()
                        ,location.getLatitude(),location.getLongitude());
                register.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        setupViewPager();
        setupTabs();
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        setupBottomNavBehaviors();
        setupBottomNavStyle();
        addBottomNavigationItems();

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
//                fragment.updateColor(ContextCompat.getColor(MainActivity.this, colors[position]));
                if (!wasSelected)
                    viewPager.setCurrentItem(position);

                if(position>1){
                    viewPager.setCurrentItem(position+2);
                }
                if (position == 1){
                    tabLayout.setVisibility(View.VISIBLE);
                }else{
                    tabLayout.setVisibility(View.GONE);
                }

                return true;
            }
        });
        Log.d("Activity","create");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000); // two minute interval
        mLocationRequest.setFastestInterval(30000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void checkProfile(){
        if(!MyUserHolder.getInstance().getUser().getProfile().isCompleted()){
            bottomNavigation.hideBottomNavigation();
            bottomNavigation.setCurrentItem(3);
            viewPager.setCurrentItem(5);
            bottomNavigation.setVisibility(View.GONE);
            View container = findViewById(R.id.coordinator);
            final Snackbar snackbar=Snackbar.make(container, "Please update your profile to continue", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }else{
            bottomNavigation.setVisibility(View.VISIBLE);
            bottomNavigation.restoreBottomNavigation();
        }
    }
    public void navigateToRestList(){
        viewPager.setCurrentItem(2);
        bottomNavigation.setCurrentItem(2);
    }

    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Created"));
        tabLayout.addTab(tabLayout.newTab().setText("Joined"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("tab",String.valueOf(tab.getPosition()));
                viewPager.setCurrentItem(tab.getPosition()+1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();

        Log.d("Activity","onstop");
    }

    @Override
    protected void onResume() {
        // call the superclass method first
        super.onResume();
        checkProfile();
        Log.d("Activity","onResume");
    }


    @Override
    protected void onDestroy() {
        // call the superclass method first
        super.onDestroy();
        //APIclient.reset();
        Log.d("Activity","onDestroy");
    }

    private void setupViewPager() {
        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        pagerAdapter.addFragments(GatheringFragment.newInstance(Mode.ALL));
        pagerAdapter.addFragments(GatheringFragment.newInstance(Mode.CREATED));
        pagerAdapter.addFragments(GatheringFragment.newInstance(Mode.JOINED));
        pagerAdapter.addFragments(GatheringFragment.newInstance(Mode.PAST));
        pagerAdapter.addFragments(new RestaurantFragment());
        pagerAdapter.addFragments(ProfileFragment.newInstance(ProfileMode.MY));

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override public void onPageSelected(int position) {
                supportInvalidateOptionsMenu();
                if(position==5){
                    getSupportActionBar().hide();
                }else{
                    getSupportActionBar().show();
                }
            }
        });
    }

    public void setupBottomNavBehaviors() {
        bottomNavigation.setBehaviorTranslationEnabled(false);

        /*
        Before enabling this. Change MainActivity theme to MyTheme.TranslucentNavigation in
        AndroidManifest.
        Warning: Toolbar Clipping might occur. Solve this by wrapping it in a LinearLayout with a top
        View of 24dp (status bar size) height.
         */
        bottomNavigation.setTranslucentNavigationEnabled(false);
    }

    /**
     * Adds styling properties to {@link AHBottomNavigation}
     */
    private void setupBottomNavStyle() {
        /*
        Set Bottom Navigation colors. Accent color for active item,
        Inactive color when its view is disabled.
        Will not be visible if setColored(true) and profile_default_profile_pic current item is set.
         */
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(fetchColor(R.color.colorAccent));
        bottomNavigation.setInactiveColor(fetchColor(R.color.bottomtab_item_resting));

        // Colors for selected (active) and non-selected items.
        bottomNavigation.setColoredModeColors(Color.WHITE,
                fetchColor(R.color.bottomtab_item_resting));

        //  Enables Reveal effect
        bottomNavigation.setColored(false);

        //  Displays item Title always (for selected and non-selected items)
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }

    /**
     * Adds (items) {@link AHBottomNavigationItem} to {@link AHBottomNavigation}
     * Also assigns a distinct color to each Bottom Navigation item, used for the color ripple.
     */
    private void addBottomNavigationItems() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_all_gathering, R.color.bottomtab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("My Gathering", R.drawable.ic_my_gathering, R.color.bottomtab_2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_restaurant, R.color.bottomtab_3);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.ic_profile, R.color.bottomtab_4);
        //AHBottomNavigationItem item6 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.ic_my_gathering, R.color.bottomtab_4);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
    }

    /**
     * Simple facade to fetch color resource, so I avoid writing a huge line every time.
     *
     * @param color to fetch
     * @return int color value.
     */
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

}


