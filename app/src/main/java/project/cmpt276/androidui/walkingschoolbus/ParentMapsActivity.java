package project.cmpt276.androidui.walkingschoolbus;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;
import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;

public class ParentMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private User user;
    private SharedValues sharedValues;
    private WGServerProxy proxy;
    private GoogleMapsInterface gMapsInterface;
    private FusedLocationProviderClient locationService;
    private FusedLocationProviderClient uploadLocationService;
    private LatLng deviceLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private static final int REQUEST_CHECK_SETTINGS = 100 ;
    private static final long LOCATION_UPDATE_RATE_IN_MS = 10000;
    private static final int LOCATION_PERMISSION_REQUESTCODE = 076;
    private static int cycle = 0;
    private final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
    private ArrayList<Marker> markers;
    private List<User> monitoring;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        gMapsInterface = GoogleMapsInterface.getInstance(this);
        locationService = LocationServices.getFusedLocationProviderClient(this);
        markers = new ArrayList<Marker>();
        checkLocationsEnabled();
        traverseMonitoredUsers();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        displayMonitoredUsers();
        displayMonitoredUsersLeaders();
    }

    //Pull monitoring users from the user's list of monitoring users and displays them on this map.
    private void displayMonitoredUsers(){
        monitoring = user.getMonitorsUsers();
        for(int i = 0; i < monitoring.size(); i++){
            //Get monitored user
            User monitoredUser = monitoring.get(i);
            Call<User> caller = proxy.getUserById(monitoredUser.getId());
            ProxyBuilder.callProxy(this, caller, returnedUser -> createUserBlips(monitoredUser));
        }
    }

    private void displayMonitoredUsersLeaders(){
        for(int i = 0; i < monitoring.size(); i++){
            //Get a monitored user's groups.
            User monitoredUser = monitoring.get(i);
            List<Group> groupsOfMonitored = monitoredUser.getMemberOfGroups();
                for(int j = 0; j < groupsOfMonitored.size(); j++){
                    if(groupsOfMonitored.get(j) != null && !groupsOfMonitored.isEmpty()){
                        Call<Group> caller = proxy.getGroupById(groupsOfMonitored.get(j).getId());
                        ProxyBuilder.callProxy(this, caller, returnedGroup -> createUserBlips(returnedGroup.getLeader()));
                    }
                }
        }
    }

    private void createUserBlips(User u){
        //Make sure the user has a last location registered.
        Log.i("DebugParentsDash", u.getLastGpsLocation().toString());
        if(u.getLastGpsLocation().getLat() != null && u.getLastGpsLocation().getLng()  != null &&  u.getLastGpsLocation().getTimestamp() != null) {
            LatLng lastLocation = new LatLng(u.getLastGpsLocation().getLat(), u.getLastGpsLocation().getLng());
            MarkerOptions userMarker = gMapsInterface.makeMarker(lastLocation, HUE_AZURE, "User: " + u.getName(), "Last upload: " + u.getLastGpsLocation().getTimestamp().toString());
            Marker m = mMap.addMarker(userMarker);
            //Add it to a list of markers so we can traverse through all the markers by moving the camera to the marker.
            markers.add(m);
        }
    }


    private void traverseMonitoredUsers(){
        Button btn = findViewById(R.id.nextUserBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(markers != null && !markers.isEmpty()){
                    mMap.moveCamera(gMapsInterface.cameraSettings(markers.get(cycle++%markers.size()).getPosition(),15.0f));
                }
            }
        });
    }

    //Begin listening for updates.
    private void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, perms[0]) == PackageManager.PERMISSION_GRANTED) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            locationService.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    moveCameraToUser(location);
                }
            });
        }else {
            //Prompt user for access to their device's location.
            ActivityCompat.requestPermissions(this, perms, LOCATION_PERMISSION_REQUESTCODE);
        }
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
            }
        };
    }

    private void checkLocationsEnabled() {
        //Start all location related activities.
        createLocationRequest();
        createLocationCallback();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        //handling the failure.
        task.addOnFailureListener(this, e -> locationsNotOn((ApiException) e));
    }

    private void locationsNotOn(ApiException e){
        switch (e.getStatusCode()) {
            case CommonStatusCodes.RESOLUTION_REQUIRED:
                //Locational error, attempt resolution based on error.
                try {
                    // Ask for Locations to be enabled using startResolutionForResult(),
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                    finish(); //Temporary solution.
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                //Error is out of our control. Ignore.
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUESTCODE: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(this,perms ,LOCATION_PERMISSION_REQUESTCODE);
                    Toast.makeText(this, R.string.parent_map_activity, Toast.LENGTH_LONG);
                }
                return;
            }
        }
    }

    private void moveCameraToUser(Location location){
        deviceLocation = gMapsInterface.calculateDeviceLocation(location);
        mMap.moveCamera(gMapsInterface.cameraSettings(deviceLocation,15.0f));
    }

    @Override
    public void onResume(){
        super.onResume();
        startLocationUpdates();
    }

}
