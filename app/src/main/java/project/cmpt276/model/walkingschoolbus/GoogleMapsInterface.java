package project.cmpt276.model.walkingschoolbus;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;

/**
 * Created by Jerry on 2018-03-02.
 */


public class GoogleMapsInterface {
    private Context context;
    private FusedLocationProviderClient locationService;
    private int userRadius = 500;
    private int locationRadius = 75;
    private static GoogleMapsInterface mapsInterface;
    private static final float CIRCLE_THICKNESS = 2.0f;
    private static Timer uploader;
    private boolean timerRunning = false;


    private GoogleMapsInterface(Context c){
        context = c;
        locationService = LocationServices.getFusedLocationProviderClient(c);
    }

    public static GoogleMapsInterface getInstance(Context c){
        if(mapsInterface == null){
            mapsInterface = new GoogleMapsInterface(c);
        }
        return mapsInterface;
    }

    public int getUserRadius() {
        return userRadius;
    }

    public void setUserRadius(int userRadius) {
        if (userRadius > 0) {
            this.userRadius = userRadius;
        } else {
            // Just a default value, should be moved to a const later
            this.userRadius = 100;
        }
    }

    // Computes if a location is within the userRadius of the person's current location
    public boolean isLocationInRadius(LatLng currentLocation, LatLng groupMeetLocation){
        // Needed to store the computed value
        float distanceResult[] = new float[2];

        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                groupMeetLocation.latitude, groupMeetLocation.longitude, distanceResult);

        // if the value is in the range -> true, else -> false
        Log.i("isLocationInRadius","distance: " + distanceResult[0]);
        return (distanceResult[0] < this.userRadius);
    }

    // Computes if the user is within the locationRadius.
    public boolean isUserInRadius(LatLng currentLocation, LatLng groupMeetLocation){
        // Needed to store the computed value
        float distanceResult[] = new float[2];

        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude,
                groupMeetLocation.latitude, groupMeetLocation.longitude, distanceResult);

        // if the value is in the range -> true, else -> false
        Log.i("isLocationInRadius","distance: " + distanceResult[0]);
        return (distanceResult[0] < this.locationRadius);
    }

    public LatLng calculateDeviceLocation(Location l){
        double lat = l.getLatitude();
        double lng = l.getLongitude();
        return new LatLng(lat, lng);
    }
    
    public Circle generateUserRadius(GoogleMap map, LatLng location, int outline){
        CircleOptions options = new CircleOptions().center(location).strokeWidth(CIRCLE_THICKNESS).radius(this.userRadius).strokeColor(outline);
        Circle userRadius = map.addCircle(options);
        return userRadius;
    }

    public Circle generateLocationRadius(GoogleMap map, LatLng location, int outline){
        CircleOptions options = new CircleOptions().center(location).strokeWidth(CIRCLE_THICKNESS).radius(this.locationRadius).strokeColor(outline);
        Circle locationRadius = map.addCircle(options);
        return locationRadius;
    }
    //Generalized custom marker.
    public MarkerOptions makeMarker(LatLng location, float type, String title, String snip){
        return new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(type)).title(title).snippet(snip);
    }

    //Generalized camera settings.
    public CameraUpdate cameraSettings(LatLng location, float zoomLevel){
        return CameraUpdateFactory.newLatLngZoom(location,zoomLevel);
    }

    //Creates the timer for uploading the last known coordinates to the server.
    public Timer getTimer(){
        if(uploader == null){
            uploader = new Timer();
        }
        return uploader;
    }

    public void killTimer(){
        uploader = null;
    }

    public void toggleTimer(boolean b){
        timerRunning = b;
    }

    //Cancel the timer and delete it (by setting it to null).
    public boolean timerIsUploading(){
        return timerRunning;
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
}
