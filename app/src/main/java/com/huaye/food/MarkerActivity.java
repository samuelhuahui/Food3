package com.huaye.food;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.huaye.food.bean.Direction;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.AsyncRequestExecutor;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * This shows how to place markers on a map.
 */
public class MarkerActivity extends AppCompatActivity implements OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnPolylineClickListener {

    private static final LatLng SOUTH_DINNING_HALL = new LatLng(41.6994831, -86.2413696);

    private static final LatLng NORTH_DINNING_HALL = new LatLng(41.7044217, -86.2359478);

    private static final LatLng LIBRARY_CAFE = new LatLng(41.7023435, -86.2340916);

    private static final LatLng GRACE_HALL = new LatLng(41.7048248, -86.2339147);

    private static final int[] COLORS = new int[]{R.color.primary_dark, R.color.primary, R.color.accent, R.color.primary_light, R.color.primary_dark_material_light};

    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private String locationProvider;
    private ArrayList<Direction.Route> mRoutes;
    private int index = 0;
    private LatLng mLocation = new LatLng(41.6994831, -86.2413696);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        index = getIntent().getIntExtra("id", 0);
        NoHttp.initialize(this);
        MapsInitializer.initialize(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
        location();
    }

    private void location() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });

    }

    private DirectionFragment fragment;
    private List<Polyline> polylines = new ArrayList<>();

    private void getRoutes(double lat, double lon) {
        final StringRequest request = new StringRequest("https://maps.googleapis.com/maps/api/directions/json?origin=" + mLocation.latitude + "," + mLocation.longitude + "&destination=" + lat + "," + lon + "&mode=walking&alternatives=true&key=AIzaSyBTUbdcPKElh4aKq4Tyj_6c-8IBrbXKIBQ");
        AsyncRequestExecutor.INSTANCE.execute(0, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                // 请求成功。
                if (polylines != null) {
                    for (Polyline polyline : polylines) {
                        polyline.remove();
                    }
                }
                polylines.clear();
                Direction direction = new Gson().fromJson(response.get(), Direction.class);
                direction.parse();
                Direction.Route route = null;
                mRoutes = direction.routes;
                for (int i = 0; i < mRoutes.size(); i++) {
                    int colorIndex = i % COLORS.length;
                    if (route == null) {
                        route = mRoutes.get(i);
                    }
                    PolylineOptions polyOptions = new PolylineOptions();
                    polyOptions.color(getResources().getColor(COLORS[colorIndex]));
                    polyOptions.width(30);
                    polyOptions.addAll(mRoutes.get(i).points);
                    Polyline polyline = mMap.addPolyline(polyOptions);
                    polyline.setClickable(true);
                    polylines.add(polyline);
                }
                if (route == null || route.latLgnBounds == null) return;
                MarkerOptions options = new MarkerOptions();
                options.position(route.latLgnBounds.northeast);
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                mMap.addMarker(options);

                options = new MarkerOptions();
                options.position(route.latLgnBounds.southwest);
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(route.latLgnBounds, 150));
                fragment = DirectionFragment.newInstance(mRoutes, 0, mRoutes.get(0).legs.get(0).start_location, mRoutes.get(0).legs.get(0).end_location);
                fragment.show(getSupportFragmentManager(), DirectionFragment.class.getSimpleName());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                // 请求失败。
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location == null) {
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        } else {
            Toast.makeText(this, "获取位置成功", Toast.LENGTH_LONG).show();
            Log.d("samuel", "lon : " + location.getLongitude());
        }
    }

    @Override
    protected void onPause() {
        locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(MarkerActivity.this, "异步获取位置成功", Toast.LENGTH_LONG).show();
            Log.d("samuel", "lon : " + location.getLongitude());
        }
    };

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setContentDescription("Map with lots of markers.");

        LatLngBounds bounds = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (index == 0) {
            bounds = builder.include(SOUTH_DINNING_HALL).build();
        }
        if (index == 1) {
            bounds = builder.include(SOUTH_DINNING_HALL).build();
        }

        if (index == 2) {
            bounds = builder.include(LIBRARY_CAFE).build();
        }

        if (index == 3) {
            bounds = builder.include(GRACE_HALL).build();
        }
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnPolylineClickListener(this);
        addMarkersToMap();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
//        getRoutes();
    }

    private void addMarkersToMap() {
        if (index == 0) {
            mMap.addMarker(new MarkerOptions()
                    .position(SOUTH_DINNING_HALL)
                    .title("SOUTH DINNING HALL")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }
        if (index == 1)
            mMap.addMarker(new MarkerOptions()
                    .position(LIBRARY_CAFE)
                    .title("LIBRARY CAFE")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .infoWindowAnchor(0.5f, 0.5f));

        if (index == 2)
            mMap.addMarker(new MarkerOptions()
                    .position(NORTH_DINNING_HALL)
                    .title("NORTH DINNING HALL")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .draggable(true));

        if (index == 3)
            mMap.addMarker(new MarkerOptions()
                    .position(GRACE_HALL)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("GRACE HALL"));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        getRoutes(marker.getPosition().latitude, marker.getPosition().longitude);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        for (int i = 0; i < mRoutes.size(); i++) {
            if (mRoutes.get(i).points.equals(polyline.getPoints())) {
                fragment = DirectionFragment.newInstance(mRoutes, i, mRoutes.get(i).legs.get(0).start_location, mRoutes.get(i).legs.get(0).end_location);
                fragment.show(getSupportFragmentManager(), DirectionFragment.class.getSimpleName());
            }
        }
    }
}
