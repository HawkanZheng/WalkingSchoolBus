package project.cmpt276.model.walkingschoolbus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by Jerry on 2018-03-02.
 */

public class GoogleMapsInterface {
    private int radius;
    private Context context;
    private FusedLocationProviderClient locationService;
    private LatLng deviceLocation;

    private static GoogleMapsInterface mapsInterface;

    public static GoogleMapsInterface getInstance(Context c){
        if(mapsInterface == null){
            mapsInterface = new GoogleMapsInterface(c);
        }
        return mapsInterface;
    }

    private GoogleMapsInterface(Context c){
        context = c;
        locationService = LocationServices.getFusedLocationProviderClient(c);
        initializeDeviceLocation();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        if (radius > 0) {
            this.radius = radius;
        } else {
            // Just a default value, should be moved to a const later
            this.radius = 100;
        }
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},514);
            Log.i("Interface_Maps", "Denied access to user's location");
        }
    }

    private void initializeDeviceLocation() {
        checkPermission();
        locationService.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override public void onSuccess(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                deviceLocation = new LatLng(lat, lng);
            }
        });
    }
    public LatLng getDeviceLocation(){
       return deviceLocation;
    }

    /*
    *
    * public void pinRadius{
    *
    * }
    *
    * generateMap
    *
    * pinCurrentSelectedRoute
    *
    * pinNearbyGroups
    */





}
