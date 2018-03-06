package project.cmpt276.model.walkingschoolbus;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class GoogleMapsInterface {
    private int radius;

    public int getRadius(){
        return radius;
    }

    public void setRadius(int radius){
        if(radius > 0){
            this.radius = radius;
        }
        else{
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
    /*
    *
    * getCurrentLocation
    *
    * generateMap
    *
    * pinCurrentSelectedRoute
    *
    * pinRadius
    *
    * pinNearbyGroups
    *
    * getRadius
    *
    * setRadius
    *
    */





}
