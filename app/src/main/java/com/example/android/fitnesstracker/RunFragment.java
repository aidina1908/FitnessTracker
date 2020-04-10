package com.example.android.fitnesstracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.android.fitnesstracker.data.Run;
import com.example.android.fitnesstracker.data.RunDao;
import com.example.android.fitnesstracker.data.RunDatabase;
import com.example.android.fitnesstracker.direction.GetDirections;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RunFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, LocationListener,
GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener{

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;

    GoogleMap googleMap;
    MapView mapView;
    View view;
    static TextView speeds, hours, lengths, km;
    EditText distance;

    public long time;
    public String speed;
    public String length;
    Button start, stop;
    Chronometer chronometer;
    boolean running;
    double latitude, longitude;
    double end_latitude, end_longitude;

    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currenLocationMarker;
    private GoogleApiClient client;
    private RunDatabase runDatabase;
    private RunDao runDao;

    public RunFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_run, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        speeds = (TextView) view.findViewById(R.id.average_speed);
        km = (TextView) view.findViewById(R.id.km);
        hours = (TextView) view.findViewById(R.id.time_spent);
        distance = (EditText) view.findViewById(R.id.distance);
        chronometer = (Chronometer) view.findViewById(R.id.chronometer);
        lengths = (TextView) view.findViewById(R.id.route_length);

        start = (Button) view.findViewById(R.id.start);
        stop = (Button) view.findViewById(R.id.stop);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChronometer(v);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object dataTransfer[] = new Object[2];
                String url = getUrl(latitude,longitude);

                dataTransfer = new Object[3];
                url = getDirectionUrl();
                GetDirections getDirections = new GetDirections();
                dataTransfer[0] = googleMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(end_latitude,end_longitude);
                getDirections.execute(dataTransfer);

               /* Object dataTransfer[] = new Object[2];
                String url = getUrl(latitude,longitude);
                dataTransfer = new Object[3];
                url = getDirectionUrl();
                GetDirections getDirections = new GetDirections();
                dataTransfer[0] = googleMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(end_latitude,end_longitude);
                getDirections.execute(dataTransfer);*/

                /*googleMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(end_latitude,end_longitude));
                markerOptions.title("Destination");
                float results[] = new float[10];
                Location.distanceBetween(latitude,longitude,end_latitude,end_longitude,results);
                markerOptions.snippet("Distance ="+results[0]);
                googleMap.addMarker(markerOptions);*/
                stopChronometer(v);
                //getDistance();
            }
        });
        // return view;

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        return view;
    }

    private void saveRun(){
        time = chronometer.getDrawingTime();
        speed = speeds.getText().toString();
        length = lengths.getText().toString();
    }

    private class AddRunAsyncTask extends AsyncTask<Void,Void,Void> {
        private RunDao dao;
        AddRunAsyncTask(RunDao dao){
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (this == null){
                Run run = new Run(length,speed,time);
                dao.insert(run);
            }
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.second_activity_menu,menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveRun();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private String getDirectionUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+latitude+","+longitude);
        googleDirectionsUrl.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsUrl.append("&key="+"AIzaSyA4lKRUfHOozoGH1dhUTJ_EYea1l03oqsw");

        return googleDirectionsUrl.toString();
    }

    private String getUrl(double latitude, double longitude) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyB80LkzZHCFDdvmTpJiyctqSp8ZbbXpOqc");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

        public void startChronometer (View view){
            if (!running) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                running = true;
            }
        }

        public void stopChronometer (View view){
            if (running) {
                chronometer.stop();
                running = false;
            }
        }

        @Override
        public void onSaveInstanceState (Bundle outState){
            super.onSaveInstanceState(outState);

            Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
            if (mapViewBundle == null) {
                mapViewBundle = new Bundle();
                outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
            }

            mapView.onSaveInstanceState(mapViewBundle);
        }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

        @Override
        public void onStart () {
            super.onStart();
            mapView.onStart();
        }

        @Override
        public void onStop () {
            super.onStop();
            mapView.onStop();
        }


        @Override
        public void onPause () {
            mapView.onPause();
            super.onPause();
        }

        @Override
        public void onDestroy () {
            mapView.onDestroy();
            super.onDestroy();
        }

        @Override
        public void onLowMemory () {
            super.onLowMemory();
            mapView.onLowMemory();
        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMarkerClickListener(this);
    }

    protected synchronized void buildGoogleApiClient(){
        client = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;
        if(currenLocationMarker != null){
            currenLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("I'm here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        currenLocationMarker = googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
                    { if (client == null){
                        buildGoogleApiClient();
                    }
                    googleMap.setMyLocationEnabled(true);
                    }
                }else {
                    Toast.makeText(getActivity(),"Permission denied",Toast.LENGTH_SHORT).show();
                }return;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

  /*  public void getDistance(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(end_latitude,end_latitude));
        markerOptions.title("Destination");

        float results[] = new float[10];
        Location.distanceBetween(latitude,longitude,end_latitude,end_longitude,results);
        markerOptions.snippet("Distance = "+ results[0]);
        googleMap.addMarker(markerOptions);
    }*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_longitude = marker.getPosition().longitude;

    }
}
