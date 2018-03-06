package project.cmpt276.androidui.walkingschoolbus;

import android.graphics.Camera;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
        LatLng yourLoc = gMapsInterface.getDeviceLocation();
        mMap.addMarker(new MarkerOptions().position(yourLoc).title("Origin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLoc,15.0f));
        //Place Marker when long pressing on map.
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Custom Marker " + markerId++));
            }
        });

        //Allow something to happen when a marker is clicked.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //Clicking a Marker will display the coordinates of the marker.
                Toast.makeText(MapsActivity.this, marker.getPosition().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

}

