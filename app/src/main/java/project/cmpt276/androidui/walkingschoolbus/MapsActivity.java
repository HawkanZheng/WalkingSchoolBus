package project.cmpt276.androidui.walkingschoolbus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;
import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.MapsJsonParser;
import project.cmpt276.model.walkingschoolbus.fragmentDataCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CHECK_SETTINGS = 100 ;
    private GroupCollection groupCollection = GroupCollection.getInstance();
    private ArrayList<Group> groupInRadius = new ArrayList<Group>();
    private ArrayList<Marker> groupMarkersPlaced = new ArrayList<Marker>();
    private Marker grpEndLocationMarker;

    //Pin Types
    private final float GROUP_TYPE = HUE_RED;
    private final float USER_TYPE = HUE_GREEN;
    private final float END_TYPE = HUE_BLUE;

    // Create a new group
    fragmentDataCollection fragmentData = fragmentDataCollection.getInstance();

    // Join a existing group
    Group joinGroup = new Group();


    //Waypoints for path
    List<Polyline> polylines = new ArrayList<Polyline>();
    private List<Double> latsWaypoints;
    private List<Double> lngsWaypoints;

    //Un-instantiated Objects
    private GoogleMap mMap;
    private GoogleMapsInterface gMapsInterface;
    private FusedLocationProviderClient locationService;
    private LatLng deviceLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationSettingsRequest locationSettings;
    private Circle userRadius;
    private GoogleApiClient gApi;

    //Variables in use
    private static final int LOCATION_PERMISSION_REQUESTCODE = 076;
    private final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static int markerId = 1;
    private static final long LOCATION_UPDATE_RATE_IN_MS = 10000;

    private WGServerProxy proxy;
    private GroupCollection groupList;
    private User user;
    private SharedValues sharedValues;
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
        checkLocationsEnabled();
        // Setup  buttons -- These need to come after the map creation
        setupSaveButton();
        setupJoinGroupButton();
        setupAddUserButton();

        groupList = GroupCollection.getInstance();
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.addMarker(placeMarkerAtLocation(origin, GROUP_TYPE, "Origin"));
        //Moves the camera to your location and pins it.
        //Also displays nearby groups
        //checkLocationsEnabled();
        //Place Marker when long pressing on map.
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                FragmentManager manager = getSupportFragmentManager();
                CustomizeMarkerFragment markerShop = new CustomizeMarkerFragment();
                markerShop.setMap(mMap,latLng);
                markerShop.show(manager,"MesageDialog");
                clearDisplayInfo();
            }
        });

        //Allow something to happen when a marker is clicked.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                clearDisplayInfo();


                // Select Start location marker
                if(fragmentData.getStartMarker() != null && fragmentData.getEndMarker() != null &&
                        Objects.equals(marker.getId(), fragmentData.getStartMarker().getId())){

                    Log.i("Marker","CustomMarkerClicked");

                    // Clicking a Marker will display the coordinates of the marker.
                    String URL = gMapsInterface.getDirectionsUrl(marker.getPosition(),fragmentData.getEndMarker().getPosition());
                    DownloadDataFromUrl DownloadDataFromUrl = new DownloadDataFromUrl();

                    DownloadDataFromUrl.execute(URL);
                }

                // Select a group marker
                else if(fragmentData.getEndMarker() == null || (fragmentData.getEndMarker() != null && !Objects.equals(fragmentData.getEndMarker().getId(), marker.getId()))){
                    // Grabs the group objected tagged to the marker
                    Log.i("Marker", "Non-Custom Marker Clicked");
                    Group grp = (Group) marker.getTag();
                    joinGroup = grp;
                    Log.i("Marker","" + (joinGroup == null));

                    if(grp != null){
                        // Draws a path from the Group start location to the end location
                        mapSelectedGroupPath mapSelectedGroupPath = new mapSelectedGroupPath();
                        mapSelectedGroupPath.execute(grp);
                        Toast.makeText(MapsActivity.this, grp.getGroupDescription(), Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Log.i("onMarkerClicked", "Group Invalid");
                    }
                }
                else{
                    // if end location marker is clicked
                    Log.i("Marker", "ElseStateTriggerd");
                    fragmentData.clearRoutes();
                    clearDisplayInfo();
                }

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


    /**
     * Follows:
     * https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient (for base code).
     * https://stackoverflow.com/questions/45504515/changing-location-settings (for the onFailureListener idea).
     * */
    private void checkLocationsEnabled() {
        createLocationRequest();
        createLocationCallback();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, e -> addUserBlip());
        task.addOnFailureListener(this, e -> locationsNotOn((ApiException) e));
    }

    private void locationsNotOn(ApiException e){
        switch (e.getStatusCode()) {
            case CommonStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                    finish(); //Temporary solution.
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way
                // to fix the settings so we won't show the dialog.
                break;
        }
    }

    private void addUserBlip() {
        if (ActivityCompat.checkSelfPermission(this, perms[0]) == PackageManager.PERMISSION_GRANTED) {
            //This sets up a user location blip.
            mMap.setMyLocationEnabled(true);
            locationService.getLastLocation().addOnSuccessListener(l -> moveCameraToUser(l));
        } else {
            //Prompt user for access to their device's location.
            ActivityCompat.requestPermissions(this, perms, LOCATION_PERMISSION_REQUESTCODE);
        }
    }

    private void moveCameraToUser(Location location){
        deviceLocation = gMapsInterface.calculateDeviceLocation(location);
        mMap.moveCamera(gMapsInterface.cameraSettings(deviceLocation,15.0f));
        displayNearbyGroups();
    }

    private void displayNearbyGroups() {
        Log.i("Location","" + deviceLocation);
        Log.i("All Groups","" + groupCollection.numGroups());
        for(int i = 0; i<groupCollection.numGroups(); i++) {
            Group grp = new Group();
            grp = groupCollection.getGroup(i);

            List<Double> grpLatLocation = new ArrayList<Double>();
            List<Double> grpLngLocation = new ArrayList<Double>();

            grpLatLocation = grp.getRouteLatArray();
            grpLngLocation = grp.getRouteLngArray();


            Log.i("grpLatNull","" + (grp.getRouteLatArray() == null));
            Log.i("grpLngNull","" + (grpLatLocation == null));


            if(grpLatLocation != null && grpLngLocation != null && !grpLatLocation.isEmpty() && !grpLngLocation.isEmpty()){
                LatLng grpStartLocation = new LatLng(grpLatLocation.get(0), grpLngLocation.get(0));
                Log.i("grp@",""+grpStartLocation);
                Log.i("isLocationInRadius","" + gMapsInterface.isLocationInRadius(deviceLocation,grpStartLocation));
                placeMarkerAtLocation(grpStartLocation,GROUP_TYPE,grp.getGroupDescription());
                if (gMapsInterface.isLocationInRadius(deviceLocation, grpStartLocation)) {
                    groupInRadius.add(grp);
                    //
                    //Marker marker = mMap.addMarker(placeMarkerAtLocation(grpStartLocation,GROUP_TYPE,grp.getGroupDescription()));
                    Marker marker = mMap.addMarker(gMapsInterface.makeMarker(grpStartLocation, GROUP_TYPE, grp.getGroupDescription()));
                    marker.setTag(grp);
                    groupMarkersPlaced.add(marker);
                }
            }
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
                if(userRadius == null){
                    //If there is no circle, make one.
                    userRadius = gMapsInterface.generateRadius(mMap, deviceLocation,Color.RED);
                } else{
                    //otherwise, recenter the circle.
                    userRadius.setCenter(deviceLocation);
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


    // string temporary, info should be extracted from group class
    public MarkerOptions placeMarkerAtLocation(LatLng location, float type, String title){
        return new MarkerOptions().position(location).title(title).icon(BitmapDescriptorFactory.defaultMarker(type));
    }


    // Clears all polylines and end locations on the map
    public void clearDisplayInfo(){

        latsWaypoints = new ArrayList<Double>();
        lngsWaypoints = new ArrayList<Double>();

        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
        if(grpEndLocationMarker != null){
            grpEndLocationMarker.remove();
        }
    }

    // Created draw a path via waypoints
    private class mapSelectedGroupPath extends AsyncTask<Group, Void, ArrayList<LatLng>>{
        @Override
        protected ArrayList<LatLng> doInBackground(Group... grp){

            // Convert Group object to ArrayList of waypoints
            ArrayList<LatLng> waypoints = new ArrayList<LatLng>();
            List<Double> latWaypoints = grp[0].getRouteLatArray();
            List<Double> lngWaypoints = grp[0].getRouteLngArray();

            Log.i("MapDraw","" +  grp[0].getRouteLatArray().size());
            Log.i("MapDraw","" +  grp[0].getRouteLatArray());
            for (int i = 0; i < grp[0].getRouteLatArray().size(); i++) {
                LatLng currentPosition = new LatLng(latWaypoints.get(i), lngWaypoints.get(i));
                waypoints.add(currentPosition);
            }

            return waypoints;
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> waypointsToBeMapped){
            PolylineOptions lineOptions = null;

            for(int i=0; i<waypointsToBeMapped.size(); i++){
                lineOptions = new PolylineOptions();

                lineOptions.addAll(waypointsToBeMapped);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                // Drawing polyline in the Google Map for the i-th route
                if(lineOptions != null) {
                    polylines.add(mMap.addPolyline(lineOptions));
                }
                else{
                    Log.i("OnPostExecute","Could not draw");
                }
            }
           grpEndLocationMarker =  mMap.addMarker(gMapsInterface.makeMarker(waypointsToBeMapped.get(waypointsToBeMapped.size()-1),END_TYPE,"End Location"));
        }
    }


    // -- Classes needed to map routes asynchronously -- //
    // -- Code courtesy of  Anupam Chugh -- //
    // -- Full write up found here: https://www.journaldev.com/13373/android-google-map-drawing-route-two-points -- //


    // -- Class used to create new groups -- //
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

                // Initialize the lat/lng arrays to store waypoints
                latsWaypoints = new ArrayList<Double>();
                lngsWaypoints = new ArrayList<Double>();

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    createLatLngList(lat,lng);
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Store the data in a the data collection class
                fragmentData.setWaypointsLatsLngs(latsWaypoints,lngsWaypoints);

                Log.i("Lats", latsWaypoints.toString());
                Log.i("Lngs", lngsWaypoints.toString());

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

    @Override
    protected void onPause(){
        super.onPause();
        fragmentData.clearData();
    }

    private void createLatLngList(double lat, double lng){
        latsWaypoints.add(lat);
        lngsWaypoints.add(lng);
    }

    // TODO: add server call to push data
    // Saves the newest created route to the server as a group
    private void setupSaveButton(){
        Button saveBtn = (Button) findViewById(R.id.btnSaveGroup);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                GroupCreationFragment groupCreation = new GroupCreationFragment();
                FragmentManager manager = getSupportFragmentManager();
                groupCreation.show(manager,"GroupCreation");
                /*
                    Open fragment to get a group title -> Must be valid name
                    Create new group using latest fragmentData
                    Push to server
                */

                Log.i("Group Save - Lats",""+fragmentData.getWaypointsLats());
                Log.i("Group Save - Lngs", ""+fragmentData.getWaypointsLngs());


            }
        });
    }

    // TODO: implement server logic
    // Joins a pre-existing group
    private void setupJoinGroupButton(){
        Button joinBtn = (Button) findViewById(R.id.btnJoinGroup);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // This is the group that needs to be added
                // joinGroup

                /*
                    Get data of latest marker clicked
                    Make Server call

                 */
                if(joinGroup != null) {
                    Call<List<User>> caller = proxy.addNewMember(joinGroup.getId(), user);
                    ProxyBuilder.callProxy(MapsActivity.this, caller, returnedMembers -> memberResponse(returnedMembers));

                    Toast.makeText(MapsActivity.this, "Joined Group!", Toast.LENGTH_SHORT).show();
                }
                refreshUser();
            }
        });
    }

    private void refreshUser() {
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(MapsActivity.this, caller, returnedUser -> userResponse(returnedUser));
        //Log.i(TAG, "setUser used here");
    }

    private void userResponse(User returnedUser) {
        //Log.i(TAG, "userResponse used here");
        User.setUser(returnedUser);

    }

    private void setupAddUserButton(){
        Button addBtn = (Button) findViewById(R.id.btnAddUserToGrp);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this,"Added User",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void memberResponse(List<User> returnedMembers) {

    }

    // TODO: Creates a new group
    private void pushNewGroupToServer(){
        // from Group Class
        // creatNewGroup(latsWaypoints, lngsWaypoints);
    }

    // Makes an Intent for other activities to call
    // launches Maps activity
    public static Intent makeIntentForMapsActivity(Context context){
        return new Intent(context, MapsActivity.class);
    }
}

