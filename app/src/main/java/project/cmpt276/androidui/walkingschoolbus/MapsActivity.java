package project.cmpt276.androidui.walkingschoolbus;

import android.graphics.Camera;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Pin Types
    private final float GROUP_TYPE = HUE_RED;
    private final float USER_TYPE = HUE_GREEN;
    private final float START_TYPE = HUE_BLUE;


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

        // TODO: pin groups to map
        /*
            server call to get all groups
            for group in groups:
                if group is in current location radius - > pin to map

        */

        LatLng yourLoc = gMapsInterface.getDeviceLocation();
        mMap.addMarker(new MarkerOptions().position(yourLoc).title("Origin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLoc,15.0f));
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
                //Clicking a Marker will display the coordinates of the marker.
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


}

