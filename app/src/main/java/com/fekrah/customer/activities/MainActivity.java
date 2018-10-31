package com.fekrah.customer.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fekrah.customer.MessagesIntentService;
import com.fekrah.customer.R;
import com.fekrah.customer.SamplePresenter;
import com.fekrah.customer.adapters.PlaceAutocompleteAdapter;
import com.fekrah.customer.fragments.TalabatFragment;
import com.fekrah.customer.fragments.TawselFragment;
import com.fekrah.customer.helper.CalculateDistanceTime;
import com.fekrah.customer.models.PlaceInfo;
import com.fekrah.customer.models.User;
import com.fekrah.customer.places.PlacesActivity;
import com.fekrah.customer.places.PlacesAdapter;
import com.fekrah.customer.places.ResultLocation;
import com.fekrah.customer.places.Results;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yayandroid.locationmanager.base.LocationBaseActivity;
import com.yayandroid.locationmanager.configuration.Configurations;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.constants.FailType;
import com.yayandroid.locationmanager.constants.ProcessType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends LocationBaseActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, SamplePresenter.SampleView, PlaceSelectionListener, DirectionCallback {

    public static int recreat = 0;
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CHECK_SETTINGS = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int PLACE_PICKER_REQUEST = 3;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    //widgets
    private ImageView mGps, mInfo, mPlacePicker;

//    @BindView(R.id.receiver_place_actv)
//    AutoCompleteTextView mSearchText;

    @BindView(R.id.near_by_places)
    ImageView near_by_places;

    @BindView(R.id.cars_num)
    TextView cars_num;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker;

    private SlidingUpPanelLayout mLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private float DEFAULT_ZOOM = 15f;

    public static Location currentLocation;
    LatLng currentLocationLatLng;
    MarkerOptions currentLocationMarkerOption;
    MarkerOptions receiverLocationMarkerOption;
    MarkerOptions arrivalLocationMarkerOption;
    Marker currentLocationMarker;
    Marker receiverLocationMarker;
    Marker arrivalLocationMarker;

    Polyline polyline1;

    private ProgressDialog progressDialog;
    private TextView locationText;
    private SamplePresenter samplePresenter;
    public static Location location;
    private String recieverLocationAdress;
    private String arrivalLocationAddress;

    LatLng receiver;
    LatLng arrival;

    public static User currentUser;

    private View navHeader;
    TextView txtName;
    private SimpleDraweeView imgProfile;

//    @BindView(R.id.nav_view2)
//    NavigationView navigationView;

    private Polyline distancePoly;
    private String time;
    private String time2;
    private String routeDistance;
    private String routeTime;
    private static final int GET_PLACE_REQUEST = 68;
    private PlaceAutocompleteFragment autocompleteFragment;
    private PlaceAutocompleteFragment autocompleteFragment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ViewPump.init(ViewPump.builder()
//                .addInterceptor(new CalligraphyInterceptor(
//                        new CalligraphyConfig.Builder()
//                                .setDefaultFontPath("fonts/AdvertisingBold.ttf")
//                                .setFontAttrId(R.attr.fontPath)
//                                .build()))
//                .build());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        samplePresenter = new SamplePresenter(this);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        near_by_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
                startActivityForResult(intent, GET_PLACE_REQUEST);
            }
        });
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null)
                            currentUser = dataSnapshot.getValue(User.class);
                        if (currentUser != null) {
                            navHeader = navigationView.getHeaderView(0);
                            txtName = (TextView) navHeader.findViewById(R.id.usernameTxt);
                            imgProfile = navHeader.findViewById(R.id.profile_image);
                            txtName.setText(currentUser.getName());
                            imgProfile.setImageURI(currentUser.getImg());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Customer_current_order")
                .child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null&&arrivalLocationMarker!=null){
                    arrivalLocationMarker.remove();
                    if (polyline1!=null){
                        polyline1.remove();
                        distancePoly.remove();
                    }
                    if (distancePoly!=null) distancePoly.remove();
                    if (receiverLocationMarker!=null) receiverLocationMarker.remove();
                    if (currentLocationMarker!=null)currentLocationMarker.remove();
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    if (autocompleteFragment!=null){

                        EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
                        etPlace.setHint(getString(R.string.arrival_area));
                        etPlace.setText(null);

                        EditText etPlace2 = (EditText) autocompleteFragment2.getView().findViewById(R.id.place_autocomplete_search_input);
                        etPlace2.setHint(getString(R.string.receiver_location));
                        etPlace2.setText(null);

                    }
                    receiver=new LatLng(location.getLatitude(),location.getLongitude());
                    currentLocationLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                    moveCameraCurrentLocation(new LatLng(location.getLatitude(),location.getLongitude()),getString(R.string.my_location));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        startService(new Intent(getApplicationContext(),MessagesIntentService.class));
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // ResultLocation settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        getLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        samplePresenter.destroy();
    }

    boolean changed = false;

    @Override
    public void onLocationChanged(Location location2) {
        location = location2;
        if (!changed) {
            changed = true;
            //     arrivalLocationAddress = getString(R.string.my_location);
            samplePresenter.onLocationChanged(location);
            initMap();
        }

        if (currentLocationMarker != null) {
            currentLocationMarker.setPosition(new LatLng(location2.getLatitude(), location2.getLongitude()));
        }


    }

    @Override
    public void onLocationFailed(@FailType int failType) {
        samplePresenter.onLocationFailed(failType);
    }

    @Override
    public void onProcessTypeChanged(@ProcessType int processType) {
        samplePresenter.onProcessTypeChanged(processType);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getLocationManager().isWaitingForLocation()
                && !getLocationManager().isAnyDialogShowing()) {
            displayProgress();
        }
        hideKeyBoard();
        Log.d("aaaaaa", "onResume: ggggg");
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissProgress();
    }

    private void displayProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            progressDialog.setMessage(getString(R.string.getting_location));
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    String string;

    @Override
    public String getText() {
        return string;
    }

    @Override
    public void setText(String text) {
        string = text;
    }

    @Override
    public void updateProgress(String text) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(text);
        }
    }

    @Override
    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    TalabatFragment talabatFragment = new TalabatFragment();
    TawselFragment tawselFragment = new TawselFragment();


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return talabatFragment;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }
    }

    @Override
    public LocationConfiguration getLocationConfiguration() {
        return Configurations.defaultConfiguration(getString(R.string.get_location_permistion),
                getString(R.string.gps_message));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Route route = direction.getRouteList().get(0);
//        mMap.addMarker(new MarkerOptions().position(origin));
//        mMap.addMarker(new MarkerOptions().position(destination));

        ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
        distancePoly = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, getResources().getColor(R.color.colorPrimary)));
        setCameraWithCoordinationBounds(route);

    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    private String serverKey = "AIzaSyAnKvay92-zyf4Or37UL6tsEF7BL8PiC6U";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));


//        LatLng origin = new LatLng(-7.788969, 110.338382);
//        LatLng destination = new LatLng(-7.781200, 110.349709);
//        DrawRouteMaps.getInstance(this)
//                .draw(origin, destination, mMap);
//        DrawMarker.getInstance(this).draw(mMap, origin, R.drawable.ic_location_on_black_24dp, "Origin ResultLocation");
//        DrawMarker.getInstance(this).draw(mMap, destination, R.drawable.ic_location_on_black_24dp, "Destination ResultLocation");
//
//        LatLngBounds bounds = new LatLngBounds.Builder()
//                .include(origin)
//                .include(destination).build();
//        Point displaySize = new Point();
//        getWindowManager().getDefaultDisplay().getSize(displaySize);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
        moveCameraCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()), "My location");
        init(new LatLng(location.getLatitude(), location.getLongitude()));

    }

    private void moveCamera(LatLng latLng, float zoom, String title, boolean mylocation) {

        if (mylocation) {
            if (currentLocationMarkerOption != null) {
                currentLocationMarkerOption = new MarkerOptions()
                        .position(latLng)
                        .title(title);
                Marker marker = mMap.addMarker(currentLocationMarkerOption);

                if (arrivalLocationMarkerOption == null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                } else {

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(currentLocationMarkerOption.getPosition());
                    builder.include(arrivalLocationMarkerOption.getPosition());
                    LatLngBounds bounds = builder.build();
                    int padding = 0; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                }

            }
        } else {
            arrivalLocationMarkerOption = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(arrivalLocationMarkerOption);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(currentLocationMarkerOption.getPosition());
            builder.include(arrivalLocationMarkerOption.getPosition());
            LatLngBounds bounds = builder.build();
            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        hideKeyBoard();
    }

    String[] results = new String[2];

    public void moveCameraArrivalLocation(LatLng latLng, String location) {

        if (arrivalLocationMarker != null) {
            arrivalLocationMarker.remove();
        }

        if (polyline1 != null)
            polyline1.remove();

        if (distancePoly != null) {
            distancePoly.remove();
        }

        arrivalLocationMarkerOption = new MarkerOptions()
                .position(latLng)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_on_black_24dp))
                .title(location);
        arrivalLocationMarker = mMap.addMarker(arrivalLocationMarkerOption);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng latLng1 = null;

        if (receiverLocationMarkerOption != null) {
            latLng1 = receiver;
//            builder.include(latLng1);
//            polyline1 = mMap.addPolyline(new PolylineOptions()
//                    .clickable(true)
//                    .color(R.color.colorPrimary)
//
//                    .add(receiverLocationMarkerOption.getPosition(),
//                            arrivalLocationMarkerOption.getPosition()));
            CalculateDistanceTime distance_task = new CalculateDistanceTime(this);

            distance_task.getDirectionsUrl(latLng1, latLng, serverKey);

            distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                @Override
                public void taskCompleted(String[] time_distance) {
//                approximate_time.setText("" + time_distance[1]);
//                approximate_diatance.setText("" + time_distance[0]);
//                results[0]= Float.parseFloat(time_distance[1]);
                    results[0] = time_distance[0];
                    results[1] = time_distance[1];

                    Log.d("aaaaaaaaaaaaaa", "distance =" + results[0]);
                    Log.d("aaaaaaaaaaaaaa", "time =" + results[1]);
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }

            });
            GoogleDirection.withServerKey(serverKey)
                    .from(latLng1)
                    .to(latLng)
                    .transportMode(TransportMode.DRIVING)
                    .alternativeRoute(true)
                    .execute(this);
        } else {
            latLng1 = currentLocationMarkerOption.getPosition();
            builder.include(latLng1);
//            polyline1 = mMap.addPolyline(new PolylineOptions()
//                    .clickable(true)
//                    .color(R.color.colorPrimary)
//
//                    .add(currentLocationMarkerOption.getPosition(),
//                            arrivalLocationMarkerOption.getPosition()));
            CalculateDistanceTime distance_task = new CalculateDistanceTime(this);

            distance_task.getDirectionsUrl(latLng1, latLng, serverKey);

            distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
                @Override
                public void taskCompleted(String[] time_distance) {
//                approximate_time.setText("" + time_distance[1]);
//                approximate_diatance.setText("" + time_distance[0]);
//                results[0]= Float.parseFloat(time_distance[1]);
                    results[0] = time_distance[0];
                    results[1] = time_distance[1];

                    Log.d("aaaaaaaaaaaaaa", "distance =" + results[0]);
                    Log.d("aaaaaaaaaaaaaa", "time =" + results[1]);
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }

            });
            GoogleDirection.withServerKey(serverKey)
                    .from(latLng1)
                    .to(latLng)
                    .transportMode(TransportMode.DRIVING)
                    .alternativeRoute(true)
                    .execute(this);
        }

//        builder.include(arrivalLocationMarkerOption.getPosition());
//        LatLngBounds bounds = builder.build();
//        int padding = 0; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 250);
//        mMap.animateCamera(cu);
//        ResultLocation.distanceBetween(latLng.latitude, latLng.longitude,
//                latLng1.latitude, latLng1.longitude, results);

        arrival = latLng;

        //   Toast.makeText(this, "" + results[0], Toast.LENGTH_SHORT).show();
    }

    public void moveCameraReceiverLocation(LatLng latLng, String location) {
        receiver = latLng;
        if (arrivalLocationMarker == null) {
            if (distancePoly != null) {
                distancePoly.remove();
            }
            if (currentLocationMarker != null)
                currentLocationMarker.remove();

            if (polyline1 != null) polyline1.remove();

            if (receiverLocationMarker != null)
                receiverLocationMarker.remove();
            receiverLocationMarkerOption = new MarkerOptions()
                    .position(latLng)
                    .icon(bitmapDescriptorFromVector(this,R.drawable.ic_my_location_marker))
                    .title(location);
            receiverLocationMarker = mMap.addMarker(receiverLocationMarkerOption);

            CameraPosition SENDBIS = CameraPosition.builder()
                    .target(latLng)
                    .zoom(17)
                    .build();
            Log.d(TAG, "movCamera: move to latlang " + latLng.toString());

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SENDBIS), 5000, null);

            return;
        }

        if (distancePoly != null) {
            distancePoly.remove();
        }
        if (currentLocationMarker != null)
            currentLocationMarker.remove();

        if (polyline1 != null) polyline1.remove();

        if (receiverLocationMarker != null)
            receiverLocationMarker.remove();

        receiverLocationMarkerOption = new MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptorFromVector(this,R.drawable.ic_my_location_marker))
                .title(location);
        receiverLocationMarker = mMap.addMarker(receiverLocationMarkerOption);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng latLng1 = arrivalLocationMarkerOption.getPosition();
//        builder.include(receiverLocationMarkerOption.getPosition());
//        builder.include(arrivalLocationMarkerOption.getPosition());
//        LatLngBounds bounds = builder.build();
//        int padding = 0; // offset from edges of the map in pixels
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 250);
//        mMap.animateCamera(cu);
//
//        polyline1 = mMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .color(R.color.colorPrimary)
//                .add(receiverLocationMarker.getPosition(),
//                        arrivalLocationMarkerOption.getPosition()));

        CalculateDistanceTime distance_task = new CalculateDistanceTime(this);

        distance_task.getDirectionsUrl(latLng1, latLng, serverKey);

        distance_task.setLoadListener(new CalculateDistanceTime.taskCompleteListener() {
            @Override
            public void taskCompleted(String[] time_distance) {
//                approximate_time.setText("" + time_distance[1]);
//                approximate_diatance.setText("" + time_distance[0]);
//                results[0]= Float.parseFloat(time_distance[1]);
                results[0] = time_distance[0];
                results[1] = time_distance[1];
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                Log.d("aaaaaaaaaaaaaa", "distance =" + results[0]);
                Log.d("aaaaaaaaaaaaaa", "time =" + results[1]);

            }

        });

        Log.d("aaaaaaaaaaaaaa", "moveCameraReceiverLocation: " + time2);
//        ResultLocation.distanceBetween(latLng.latitude, latLng.longitude,
//                latLng1.latitude, latLng1.longitude, results);

        GoogleDirection.withServerKey(serverKey)
                .from(latLng1)
                .to(latLng)
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(true)
                .execute(this);


    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
       // Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        //vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        //vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void moveCameraCurrentLocation(LatLng latLng, String location) {
        recieverLocationAdress = getString(R.string.my_location);
        receiver = latLng;
        if (currentLocationMarker != null)
            currentLocationMarker.remove();

        currentLocationMarkerOption = new MarkerOptions()
                .position(latLng)
                 .icon(bitmapDescriptorFromVector(this,R.drawable.ic_my_location_marker))
                .title(location);
        CameraPosition SENDBIS = CameraPosition.builder()
                .target(latLng)
                .zoom(17)
                .build();
        Log.d(TAG, "movCamera: move to latlang " + latLng.toString());

        currentLocationMarker = mMap.addMarker(currentLocationMarkerOption);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SENDBIS), 5000, null);
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        // mMap.addMarker(new MarkerOptions().position(latLng));
        hideKeyBoard();

    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void hideKeyBoard() {
        Activity activity = MainActivity.this;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void init(LatLng latLng) {

        mLayout = findViewById(R.id.sliding_layout);
        mLayout.setAnchorPoint(1f);
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                //Toast.makeText(MainActivity.this, ""+ slideOffset, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                // setDistance(results[0]);

                talabatFragment.change(receiver, arrival, recieverLocationAdress, arrivalLocationAddress,
                        results, 8);
                // tawselFragment.change(recieverLocationAdress, arrivalLocationAddress, results[0], 8);


            }
        });

        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (arrivalLocationAddress != null)
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                if (arrivalLocationAddress != null)
                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                else
                    Toast.makeText(MainActivity.this, R.string.choose_place_text, Toast.LENGTH_SHORT).show();
            }
        });
        // mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        //mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        // Toast.makeText(getActivity(), ""+currentLocation.getLatitude()+" -- " +
        //       currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

        //currentLocationLatLng =latLng;
        //  Toast.makeText(getActivity(), ""+currentLocation.getLatitude(), Toast.LENGTH_SHORT).show();

        initAutoComplete(latLng);

        getDriversAround();
    }

    private void initAutoComplete(LatLng latLng) {
        LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(latLng, latLng);

        //  new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS).build();
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .setCountry("EG")
                .build();
//        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
//                LAT_LNG_BOUNDS, typeFilter);
//
//        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
//
//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
//
//                    //execute our method for searching
//                    geoLocate();
//                }
//
//                return false;
//            }
//        });

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setBoundsBias(LAT_LNG_BOUNDS);
        autocompleteFragment.setFilter(typeFilter);
        EditText etPlace = (EditText) autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);
        etPlace.setHint(getString(R.string.arrival_area));
        etPlace.setTextSize(15.0f);

        etPlace.setHintTextColor(getResources().getColor(R.color.gray));
        etPlace.setTextColor(getResources().getColor(android.R.color.white));
        ////
        autocompleteFragment2 = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment2);


        autocompleteFragment2.setBoundsBias(LAT_LNG_BOUNDS);
        autocompleteFragment2.setFilter(typeFilter);
        EditText etPlace2 = (EditText) autocompleteFragment2.getView()
                .findViewById(R.id.place_autocomplete_search_input);
        etPlace2.setHint(getString(R.string.receiver_place));
        etPlace2.setTextSize(15.0f);
        etPlace2.setHintTextColor(getResources().getColor(R.color.gray));
        etPlace2.setTextColor(getResources().getColor(android.R.color.white));

        AppCompatImageButton clearbtn2 = autocompleteFragment2.getView()
                .findViewById(R.id.place_autocomplete_clear_button);
        clearbtn2.setImageResource(R.drawable.ic_close);
        AppCompatImageButton clearbtn = autocompleteFragment.getView()
                .findViewById(R.id.place_autocomplete_clear_button);
        clearbtn.setImageResource(R.drawable.ic_close);

        AppCompatImageButton clearbtn3 = autocompleteFragment2.getView()
                .findViewById(R.id.place_autocomplete_search_button);
        clearbtn3.setImageResource(R.drawable.ic_magnify_white);
        AppCompatImageButton clearbtn4 = autocompleteFragment.getView()
                .findViewById(R.id.place_autocomplete_search_button);
        clearbtn4.setImageResource(R.drawable.ic_magnify_white);

        View view = autocompleteFragment.getView()
                .findViewById(R.id.place_autocomplete_powered_by_google);
        if (view != null)
            view.setVisibility(View.GONE);
        //powred.setImageResource(R.drawable.ic_dummy_user);
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                moveCameraReceiverLocation(place.getLatLng(), "Arrival ResultLocation");
                //  arrivalPlaceTv.setText(place.getAddress());
                recieverLocationAdress = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    /**
     * Callback invoked when a place has been selected from the PlaceAutocompleteFragment.
     */
    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place Selected: " + place.getName());
        moveCameraArrivalLocation(place.getLatLng(), "Arrival ResultLocation");
        //  arrivalPlaceTv.setText(place.getAddress());
        arrivalLocationAddress = place.getAddress().toString();
//        // Format the returned place's details and display them in the TextView.
//        mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(), place.getId(),
//                place.getAddress(), place.getPhoneNumber(), place.getWebsiteUri()));

        CharSequence attributions = place.getAttributions();
        if (!TextUtils.isEmpty(attributions)) {
            // mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
        } else {
            //  mPlaceAttribution.setText("");
        }
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }
//    private void geoLocate() {
//        Log.d(TAG, "geoLocate: geolocating");
//
//        String searchString = mSearchText.getText().toString();
//
//        Geocoder geocoder = new Geocoder(this);
//        List<Address> list = new ArrayList<>();
//        try {
//            list = geocoder.getFromLocationName(searchString, 1);
//        } catch (IOException e) {
//            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
//        }
//
//        if (list.size() > 0) {
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found a location: " + address.toString());
//            //   Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//
//            // moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 17.0f, "place",true);
//        }
//    }


    private void initMap() {

        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @CallSuper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = com.google.android.gms.location.places.Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }

        } else if (requestCode == GET_PLACE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Results result = (Results) data.getSerializableExtra("result");
                // Toast.makeText(this, result.getName(), Toast.LENGTH_SHORT).show();
                arrivalLocationAddress = result.getName();
                autocompleteFragment.setText(arrivalLocationAddress);
                moveCameraArrivalLocation(new LatLng(result.getGeometry().getResultLocation().getLat(),
                        result.getGeometry().getResultLocation().getLng()), arrivalLocationAddress);

            }
        } else {
            locationManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideKeyBoard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = com.google.android.gms.location.places.Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mReceiverPlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
//                myplace.setText(place.getAddress());
                //  Toast.makeText(MainActivity.this, "Arrival ResultLocation" + place.getLatLng(), Toast.LENGTH_SHORT).show();
                moveCameraArrivalLocation(place.getLatLng(), "Arrival ResultLocation");
                //arrivalPlaceTv.setText(place.getAddress());
                arrivalLocationAddress = place.getAddress().toString();
            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            places.release();
        }
    };

    private ResultCallback<PlaceBuffer> mReceiverPlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

//                Log.d(TAG, "onResult: place: " + mPlace.toString());
                recieverLocationAdress = place.getAddress().toString();
                moveCameraReceiverLocation(place.getLatLng(), getString(R.string.receiver_location));

            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            places.release();
        }
    };


    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                        mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    //    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
//    }

    //    public void getResultLocation() {
//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(5000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
//
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                Toast.makeText(MainActivity.this, "location settings success", Toast.LENGTH_SHORT).show();
//                //getLocationPermission();
//            }
//        });
//
//        task.addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    // ResultLocation settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
//                    try {
//                        // Show the dialog by calling startResolutionForResult(),
//                        // and check the result in onActivityResult().
//                        ResolvableApiException resolvable = (ResolvableApiException) e;
//                        resolvable.startResolutionForResult(MainActivity.this,
//                                REQUEST_CHECK_SETTINGS);
//                    } catch (IntentSender.SendIntentException sendEx) {
//                        // Ignore the error.
//                    }
//                }
//            }
//        });
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
//    }
//
//    private void startLocationUpdates() {
//        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
//                mLocationCallback,
//                null /* Looper */);
//    }

    //    private void getLocationPermission() {
//
//        String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
//
//        Log.d(TAG, "getLocationPermission: getting location permissions");
//        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
//
//        if (ContextCompat.checkSelfPermission(this,
//                COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionsGranted = true;
//            initMap();
//        } else {
//
//            ActivityCompat.requestPermissions(this, permissions,
//                    LOCATION_PERMISSION_REQUEST_CODE);
//        }
//
//    }
    private void displayLocationSettingsRequest(Context context) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        //     getLocationPermission();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "ResultLocation settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "ResultLocation settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_current_order) {
            Intent intent = new Intent(this, CurrentOrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_chats) {
            startActivity(new Intent(this, ChatsRoomsActivity.class));
        } else if (id == R.id.nav_edit) {
            startActivity(new Intent(this, EditProfileActivity.class));
        } else if (id == R.id.nav_log_out) {
            startActivity(new Intent(this, LoginActivity.class));
            FirebaseAuth.getInstance().signOut();
            finish();
        } else if (id == R.id.nav_places) {
            Intent intent = new Intent(this, PlacesActivity.class);
            startActivityForResult(intent, GET_PLACE_REQUEST);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: called.");
//        mLocationPermissionsGranted = false;
//
//        switch (requestCode) {
//            case LOCATION_PERMISSION_REQUEST_CODE: {
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < grantResults.length; i++) {
//                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                            mLocationPermissionsGranted = false;
//                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
//                            return;
//                        }
//                    }
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
//                    mLocationPermissionsGranted = true;
//                    /ialize our map
//                    initMap();
//                    Toast.makeText(MainActivity.this, "geranteed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        if (requestCode == REQUEST_CHECK_SETTINGS) {
//            if (resultCode == RESULT_OK) {
//                getLocationPermission();
//            }
//        } else if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(this, data);
//
//                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                        .getPlaceById(mGoogleApiClient, place.getId());
//                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
//            }
//        }
//
//    }
    //    private void getDeviceLocation() {
//        Log.d(TAG, "getDeviceLocation: getting the devices current location");
//        FusedLocationProviderClient mFusedLocationClient;
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<ResultLocation>() {
//                    @Override
//                    public void onSuccess(ResultLocation location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                            //currentLocation = location;
//                            moveCameraCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()), "My location");
//                            init(new LatLng(location.getLatitude(), location.getLongitude()));
//
//                        } else {
//                            Toast.makeText(MainActivity.this, "ResultLocation Null", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                });
//    }

    List<Marker> markers = new ArrayList<Marker>();

    private void getDriversAround() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child("drivers_location");
        GeoFire geoFire = new GeoFire(ref);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLongitude(), location.getLatitude()), 10000);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {


                for (Marker markerIt : markers) {
                    if (Objects.equals(markerIt.getTag(), key))
                        return;
                }

                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation)
                        .title(key).icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.car_marker)));
                mDriverMarker.setTag(key);

                markers.add(mDriverMarker);
                Log.d("ddddd", "onKeyExited: added");

//                cars_num.setText(String.valueOf(markers.size()));
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markers) {
                    if (Objects.equals(markerIt.getTag(), key)) {
                        Log.d("ddddd", "onKeyExited: removed");
                        markerIt.remove();
                    }
                }
//                    cars_num.setText(""+markers.size());
//                    Log.d("ddddd", "onKeyExited: removed doo");

            }

            @SuppressLint("NewApi")
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markers) {
                    if (Objects.equals(markerIt.getTag(), key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
               cars_num.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                else cars_num.setText("0");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
