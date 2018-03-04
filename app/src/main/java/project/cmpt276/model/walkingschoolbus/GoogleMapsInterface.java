package project.cmpt276.model.walkingschoolbus;

/**
 * Created by Jerry on 2018-03-02.
 */

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
