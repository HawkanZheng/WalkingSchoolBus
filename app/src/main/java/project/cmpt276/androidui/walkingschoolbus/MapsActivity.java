package project.cmpt276.androidui.walkingschoolbus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;
import project.cmpt276.model.walkingschoolbus.MapsJsonParser;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback{

    // Pin Types
    private final float GROUP_TYPE = HUE_RED;
    private final float USER_TYPE = HUE_GREEN;
    private final float START_TYPE = HUE_BLUE;

    // Waypoints for path
    List<Polyline> polylines = new ArrayList<Polyline>();

    //Un-instantiated Objects
    private GoogleMap mMap;
    private GoogleMapsInterface gMapsInterface;
    private FusedLocationProviderClient locationService;
    private LatLng deviceLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationSettingsRequest locationSettings;
    private Circle singleCircle;

    //Variables in use
    private static final int LOCATION_PERMISSION_REQUESTCODE = 076;
    private final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static int markerId = 1;
    private static final long LOCATION_UPDATE_RATE_IN_MS = 10000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Map stuffs
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationService = LocationServices.getFusedLocationProviderClient(this);
        gMapsInterface =  GoogleMapsInterface.getInstance(this);
        createLocationRequest();
        createLocationCallback();
        createLocationSettings();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng origin = new LatLng(0, 0);
        //mMap.addMarker(placeMarkerAtLocation(origin, GROUP_TYPE, "Origin"));

        // TODO: pin groups to map
        /*
            server call to get all groups
            for group in groups:
                if group is in current location radius - > pin to map

        */
        //Moves the camera to your location and pins it.
        addUserBlip();
        // TODO: pin groups to map
        /*
            server call to get all groups
            for group in groups:
                if group is in current location radius - > pin to map

        */


        //Place Marker when long pressing on map.
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                mMap.addMarker(placeMarkerAtLocation(latLng, USER_TYPE, "Custom Marker " + markerId++));
            }
        });

        //Allow something to happen when a marker is clicked.
        // TODO: show path from start location to end location on pin clicked
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                clearLines();
                // Clicking a Marker will display the coordinates of the marker.
                String URL = gMapsInterface.getDirectionsUrl(deviceLocation,marker.getPosition());
                DownloadDataFromUrl DownloadDataFromUrl = new DownloadDataFromUrl();

                DownloadDataFromUrl.execute(URL);

                Toast.makeText(MapsActivity.this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUESTCODE: {
                if (grantResults.length == 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addUserBlip();
                } else {
                    ActivityCompat.requestPermissions(this,perms ,LOCATION_PERMISSION_REQUESTCODE);
                    Toast.makeText(this, "Please enable device location", Toast.LENGTH_LONG);
                }
                return;
            }
        }
    }

    private void addUserBlip() {
        if (ActivityCompat.checkSelfPermission(this, perms[0]) == PackageManager.PERMISSION_GRANTED) {
            //This sets up a user location blip.
            mMap.setMyLocationEnabled(true);
        } else {
            //Prompt user for access to their device's location.
            ActivityCompat.requestPermissions(this, perms, LOCATION_PERMISSION_REQUESTCODE);
        }
    }

    //This places a marker at user's last known location... Maybe implement the panic button to drop a last known location as well?
    private void placeLastLocationMarker(Location location){
        deviceLocation = gMapsInterface.calculateDeviceLocation(location);
        Toast.makeText(MapsActivity.this,"Adding marker at " + deviceLocation.latitude + ", " + deviceLocation.longitude, Toast.LENGTH_LONG).show();
        mMap.addMarker(new MarkerOptions().position(deviceLocation).title("Your location"));
    }

    /**
     * createLocationRequest, createLocationCallback, createLocationSettings, startLocationUpdates and onResume follows the Android Training Guide.
     * https://developer.android.com/training/location/receive-location-updates.html
     **/


    @Override
    public void onResume(){
        super.onResume();
        startLocationUpdates();
    }
    
    //Initialize the locationRequest object.
    private void createLocationRequest(){
        locationRequest = new LocationRequest()
                //High accuracy may be power consuming according to the API
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_RATE_IN_MS);
    }

    //Create the listener to listen to locational updates and update the map accordingly.
    private void createLocationCallback(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location lastLocation = locationResult.getLastLocation();
                deviceLocation = gMapsInterface.calculateDeviceLocation(lastLocation);
                if(singleCircle == null){
                    //If there is no circle, make one.
                    singleCircle = gMapsInterface.generateRadius(mMap, deviceLocation,Color.RED);
                } else{
                    //otherwise, recenter the circle.
                    singleCircle.setCenter(deviceLocation);
                }
            }
        };
    }

    //Create the location request settings
    private void createLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettings = builder.build();
    }

    //Begin listening for updates.
    private void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, perms[0]) == PackageManager.PERMISSION_GRANTED) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }else {
            //Prompt user for access to their device's location.
            ActivityCompat.requestPermissions(this, perms, LOCATION_PERMISSION_REQUESTCODE);
        }
    }

    // TODO: convert this to accept a group object which translates into a pin
    // string temporary, info should be extracted from group class
    public MarkerOptions placeMarkerAtLocation(LatLng location, float type, String title){
        // TODO: pin should be customized to show related information
        return new MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.defaultMarker(type));
    }


    // Clears all polylines on the map
    private void clearLines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }


    // -- Classes needed to map routes asynchronously -- //
    // -- Code courtesy of  Anupam Chugh -- //
    // -- Full write up found here: https://www.journaldev.com/13373/android-google-map-drawing-route-two-points -- //

    // Fetches data from url passed
    private class DownloadDataFromUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... URL) {

            // For storing data from web service
            String data = "";
            GoogleMapsInterface mapsInterface = GoogleMapsInterface.getInstance(MapsActivity.this);

            try {
                // Fetching the data from web service
                data = mapsInterface.downloadUrl(URL[0]);
                Log.i("Background Download", data.toString());
            } catch (Exception e) {
                Log.i("Background Download", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    // Parse downloaded JSON file, and convert to mappable data
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                MapsJsonParser parser = new MapsJsonParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                polylines.add(mMap.addPolyline(lineOptions));


            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

}

