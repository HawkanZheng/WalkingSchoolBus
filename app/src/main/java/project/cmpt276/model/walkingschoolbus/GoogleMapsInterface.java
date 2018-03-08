package project.cmpt276.model.walkingschoolbus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Jerry on 2018-03-02.
 */


public class GoogleMapsInterface {
    private int radius;
    private Context context;
    private FusedLocationProviderClient locationService;
    private LatLng deviceLocation;

    private static GoogleMapsInterface mapsInterface;



    private GoogleMapsInterface(Context c){
        context = c;
        locationService = LocationServices.getFusedLocationProviderClient(c);
        initializeDeviceLocation();
    }

    public static GoogleMapsInterface getInstance(Context c){
        if(mapsInterface == null){
            mapsInterface = new GoogleMapsInterface(c);
        }
        return mapsInterface;
    }


    public LatLng getDeviceLocation(){
        return deviceLocation;
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
    // Computes if a location is within the radius of the person's current location
    public boolean isLocationInRadius(LatLng currentLocation, LatLng groupMeetLocation){
        // Needed to store the computed value
        float distanceResult[] = new float[2];

        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                groupMeetLocation.latitude, groupMeetLocation.longitude, distanceResult);

        // if the value is in the range -> true, else -> false
        return (distanceResult[0] < this.radius);

    }

    //Checks for consent by the user to use their locational data.
    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},514);
            Log.i("Interface_Maps", "Denied access to user's location");
        }
    }

    //Calculate user's location
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

    // Constructs google url to create a path
    public String getDirectionsUrl(LatLng origin, LatLng dest){

        // Construct the URL request
        String originUrl = "origin=" + origin.latitude + "," + origin.longitude;
        String destUrl = "destination=" + dest.latitude + "," + dest.longitude;

        // -- Options Params -- //
        // Mode of transportation
        String mode = "mode=walking";

        String params = originUrl + "&" + destUrl + "&" + mode;

        // TODO: Move API key
        String constructedURL = "https://maps.googleapis.com/maps/api/directions/json?" + params + "&key=AIzaSyA6LfimFEM754npAhMt6A_QG4gpeHXlrdg";

        return constructedURL;
    }

    // Download JSON data from google api
    public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
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
