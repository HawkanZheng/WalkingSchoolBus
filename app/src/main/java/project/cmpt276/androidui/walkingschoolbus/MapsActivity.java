package project.cmpt276.androidui.walkingschoolbus;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Pin Types
    private final float GROUP_TYPE = HUE_RED;
    private final float USER_TYPE = HUE_GREEN;
    private final float START_TYPE = HUE_BLUE;

    // Waypoints for path
    List<Polyline> polylines = new ArrayList<Polyline>();


    private GoogleMap mMap;
    private static int markerId = 1;
    private GoogleMapsInterface gMapsInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Map stuffs
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gMapsInterface =  GoogleMapsInterface.getInstance(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng origin = new LatLng(0, 0);
        mMap.addMarker(placeMarkerAtLocation(origin, GROUP_TYPE, "Origin"));


        //Moves the camera to your location and pins it.
        LatLng yourLoc = gMapsInterface.getDeviceLocation();
        mMap.addMarker(new MarkerOptions().position(yourLoc).title("Origin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLoc,15.0f));

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
                String URL = gMapsInterface.getDirectionsUrl(gMapsInterface.getDeviceLocation(),marker.getPosition());
                DownloadDataFromUrl DownloadDataFromUrl = new DownloadDataFromUrl();

                DownloadDataFromUrl.execute(URL);

                Toast.makeText(MapsActivity.this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
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

