package com.bonlai.socialdiningapp.helpers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bonlai.socialdiningapp.network.AuthAPIclient;
import com.bonlai.socialdiningapp.network.GeocodeAPIclient;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.detail.gathering.GatheringDetailActivity;
import com.bonlai.socialdiningapp.models.MapMarkerInfo;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;


import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        ClusterManager.OnClusterItemInfoWindowClickListener<MapMarkerInfo>{

    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    private static final int PLACE_PICKER_REQUEST = 1000;

    private GoogleMap mMap;
    private GoogleApiClient mClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Marker mCurrLocationMarker;
    private Circle mCircle;

    private List<MapMarkerInfo> mMarkers;

    private ClusterManager<MapMarkerInfo> mClusterManager;

    private HashMap<LatLng, Float> rotation= new HashMap<LatLng, Float>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnSearchPlace();
            }
        });

        Button circleButton = (Button) findViewById(R.id.circle_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnSearchPlace();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getGathering();
    }

    private void getGathering(){
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<List<MapMarkerInfo>> getGatheringLocationList = service.getGatheringLocationList();
        getGatheringLocationList.enqueue(new Callback<List<MapMarkerInfo>>() {
            @Override
            public void onResponse(Call<List<MapMarkerInfo>> call, Response<List<MapMarkerInfo>> response) {
                mMarkers=response.body();

                for(MapMarkerInfo marker:mMarkers){
                    getLatLng(marker);
                }
            }
            @Override
            public void onFailure(Call<List<MapMarkerInfo>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getLatLng(final MapMarkerInfo marker){
        GeocodeAPIclient.APIService mService=GeocodeAPIclient.getAPIService();
        String key = getString(R.string.google_maps_key);
        Call<ResponseBody> service=mService.getCityResults(marker.getRestaurant().getAddress(),key);

        Callback<ResponseBody> responseBodyCallback=new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObj = new JSONObject(response.body().string());
                        //Log.d("JSON obj", "JSON obj: " + jsonObj);

                        double longitude = ((JSONArray)jsonObj.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lng");
                        //Log.d("JSON longitude", "JSON longitude: " + longitude);

                        double latitude = ((JSONArray)jsonObj.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getDouble("lat");
                        LatLng latLng=new LatLng(latitude,longitude);
                        marker.setLatLng(latLng);
                        marker.setrotationValue(getRotation(latLng));

                        mClusterManager.addItem(marker);
                        mClusterManager.cluster();
                        //createMarker(marker);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        };
        service.enqueue(responseBodyCallback);
    }

    private float getRotation(LatLng latLng){
        Float value = rotation.get(latLng);
        if (value != null) {
            Log.d("rotation1 ",""+value);
            rotation.put(latLng, value+30);
            return value+30;
        } else {
            rotation.put(latLng, (float)0);
            Log.d("rotation2 "," ");
            return 0;
        }
    }

    private void buildGoogleAPIClient() {
        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    private void returnSearchPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String placeName = String.format("Place: %s", place.getName());
                String placeAddress = String.format("Address: %s", place.getAddress());
                LatLng toLatLng = place.getLatLng();

                // Add Marker
                mMap.addMarker(new MarkerOptions().position(toLatLng)
                        .title(placeName).snippet(placeAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                // Move Camera to selected place
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toLatLng, 16));
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setOnInfoWindowClickListener(this);
        buildGoogleAPIClient();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
        setClusterManager();

    }

    private void setClusterManager(){
        mClusterManager = new ClusterManager<>(this, mMap);
        RenderClusterInfoWindow render=new RenderClusterInfoWindow(this,mMap,mClusterManager);
        mClusterManager.setRenderer(render);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());

                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                if (mCircle != null) {
                    mCircle.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                drawCurrentLocationMarker(latLng);
                drawCircle(latLng);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        };
    };

    private void drawCurrentLocationMarker(LatLng point){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
    }

    private void drawCircle(LatLng point){
        mCircle = mMap.addCircle(new CircleOptions()
                .center(point)
                .radius(500)
                .strokeColor(Color.RED));
    }
    @Override
    protected void onStart() {
        super.onStart();
        //mClient.connect();
    }

    @Override
    protected void onStop() {
        mClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
/*        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
            }*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


/*    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent (this, GatheringDetailActivity.class);
        intent.putExtra(GatheringDetailActivity.GATHERING_ID, (int)marker.getTag());
        this.startActivity(intent);
    }*/

    @Override
    public void onClusterItemInfoWindowClick(MapMarkerInfo mapMarkerInfo) {
        Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
        Intent intent = new Intent (this, GatheringDetailActivity.class);
        intent.putExtra(GatheringDetailActivity.GATHERING_ID, (int) mapMarkerInfo.getId());
        this.startActivity(intent);
    }

    private class RenderClusterInfoWindow extends DefaultClusterRenderer<MapMarkerInfo> {

        RenderClusterInfoWindow(Context context, GoogleMap map, ClusterManager<MapMarkerInfo> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onClusterItemRendered(MapMarkerInfo item, Marker marker) {
            marker.setTag(item.getId());
            super.onClusterItemRendered(item, marker);
        }

        @Override
        protected void onClusterRendered(Cluster<MapMarkerInfo> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
        }

        @Override
        protected void onBeforeClusterItemRendered(MapMarkerInfo item, MarkerOptions markerOptions) {
            markerOptions.position(item.getLatLng()).rotation(item.getrotationValue())
                    .title(item.getName()).snippet(item.getRestaurant().getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            //super.onBeforeClusterItemRendered(item, markerOptions);
        }


    }
}