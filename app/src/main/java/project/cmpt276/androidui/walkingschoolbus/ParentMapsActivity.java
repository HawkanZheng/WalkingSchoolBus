package project.cmpt276.androidui.walkingschoolbus;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;
import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

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
    private final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
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
        checkLocationsEnabled();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        displayMonitoredUsers();
    }

    //Pull monitored users from the user's list of monitored users and displays them on this map.
    private void displayMonitoredUsers(){
        List<User> monitoring = user.getMonitorsUsers();
        for(int i = 0; i < monitoring.size(); i++){
            User u = monitoring.get(i);
            Log.i("DisplayUsers", u.getName() + " " + u.getLastGpsLocation().toString());
            //Only create user blips who have a last location registered.
            if(u.getLastGpsLocation() != null){
                Call<User> caller = proxy.getUserById(u.getId());
                ProxyBuilder.callProxy(this, caller, returnedUser -> createUserBlips(u));
            }
        }
    }

    private void createUserBlips(User u){
        LatLng lastLocation = new LatLng(u.getLastGpsLocation().getLat(), u.getLastGpsLocation().getLng());
        MarkerOptions userMarker = gMapsInterface.makeMarker(lastLocation,HUE_AZURE,u.getName(),"Last upload: " + u.getLastGpsLocation().getTimestamp().toString());
        mMap.addMarker(userMarker);
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
                    Toast.makeText(this, "Please allow device location", Toast.LENGTH_LONG);
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
