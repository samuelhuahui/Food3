package com.huaye.food.fragment;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
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
import com.huaye.food.DirectionFragment;
import com.huaye.food.OnMapAndViewReadyListener;
import com.huaye.food.R;
import com.huaye.food.bean.Direction;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.AsyncRequestExecutor;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by sunhuahui on 2017/10/12.
 */

public class MapFragment extends SupportMapFragment implements OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnPolylineClickListener {
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
    private LatLng mLocation = new LatLng(41.6994831, -86.2413696);


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NoHttp.initialize(getContext());
        MapsInitializer.initialize(getContext());

        new OnMapAndViewReadyListener(MapFragment.this, this);
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(getContext(), "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
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
                if (!"OK".equals(direction.status)){
                    Toast.makeText(getContext(), direction.status, Toast.LENGTH_LONG).show();
                    return;
                }
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
                fragment.show(getFragmentManager(), DirectionFragment.class.getSimpleName());
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                // 请求失败。
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location == null) {
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        } else {
            mLocation = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(getContext(), "获取位置成功", Toast.LENGTH_LONG).show();
            Log.d("samuel", "lon : " + location.getLongitude());
        }
    }

    @Override
    public void onPause() {
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
            Toast.makeText(getContext(), "异步获取位置成功", Toast.LENGTH_LONG).show();
            Log.d("samuel", "lon : " + location.getLongitude());
        }
    };

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setContentDescription("Map with lots of markers.");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(LIBRARY_CAFE)
                .include(GRACE_HALL)
                .include(SOUTH_DINNING_HALL)
                .include(NORTH_DINNING_HALL)
                .build();

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnPolylineClickListener(this);
        addMarkersToMap();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
//        getRoutes();
    }

    private void addMarkersToMap() {
        mMap.addMarker(new MarkerOptions()
                .position(SOUTH_DINNING_HALL)
                .title("SOUTH DINNING HALL")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        mMap.addMarker(new MarkerOptions()
                .position(LIBRARY_CAFE)
                .title("LIBRARY CAFE")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .infoWindowAnchor(0.5f, 0.5f));

        mMap.addMarker(new MarkerOptions()
                .position(NORTH_DINNING_HALL)
                .title("NORTH DINNING HALL")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                .draggable(true));

        mMap.addMarker(new MarkerOptions()
                .position(GRACE_HALL)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("GRACE HALL"));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        getRoutes(marker.getPosition().latitude, marker.getPosition().longitude);
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
                fragment.show(getFragmentManager(), DirectionFragment.class.getSimpleName());
            }
        }
    }
}
