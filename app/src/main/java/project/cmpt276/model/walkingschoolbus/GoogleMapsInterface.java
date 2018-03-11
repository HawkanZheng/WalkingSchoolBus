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

/**
 * Created by Jerry on 2018-03-02.
 */


public class GoogleMapsInterface {
    private Context context;
    private FusedLocationProviderClient locationService;
    private int radius = 100;
    private static GoogleMapsInterface mapsInterface;
    private static final float CIRCLE_THICKNESS = 2.0f;
    private static Circle userProximity;

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

    public LatLng calculateDeviceLocation(Location l){
        double lat = l.getLatitude();
        double lng = l.getLongitude();
        return new LatLng(lat, lng);
    }
    
    public Circle generateRadius(GoogleMap map, LatLng location,int outline){
        CircleOptions options = new CircleOptions().center(location).strokeWidth(CIRCLE_THICKNESS).radius(this.radius).strokeColor(outline);
        Circle userRadius = map.addCircle(options);
        return userRadius;
    }

    //Generalized custom marker.
    public MarkerOptions makeMarker(LatLng location, float type, String title){
        return new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(type)).title(title);
    }

    //Generalized camera settings.
    public CameraUpdate cameraSettings(LatLng location, float zoomLevel){
        return CameraUpdateFactory.newLatLngZoom(location,zoomLevel);
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
