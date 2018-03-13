package project.cmpt276.model.walkingschoolbus;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorawar on 3/12/2018.
 */

public class fragmentDataCollection {

    // True = finished was the last marker
    // False = start was last marker
    private boolean lastSelcted;

    // Track how many markers are on the map
    private int startCount;
    private int endCount;

    // Track the markers made
    private Marker userStartMarker;
    private Marker userEndMarker;
    private String markerTitle;
    private List<Double> waypointsLats;
    private List<Double> waypointsLngs;

    // Singleton
    private static fragmentDataCollection instance;


    private fragmentDataCollection(){
        // Singleton Pattern
        waypointsLats = new ArrayList<Double>();
        waypointsLngs = new ArrayList<Double>();
        this.startCount = 0;
        this.endCount = 0;
        this.lastSelcted = false;
    }

    public static fragmentDataCollection getInstance(){
        if(instance == null){
            instance = new fragmentDataCollection();
        }
        return instance;
    }


    public Marker getStartMarker(){
        return userStartMarker;
    }

    public Marker getEndMarker(){
        return userEndMarker;
    }

    public boolean getLastSelected(){
        return lastSelcted;
    }

    public int getStartCount(){
        return startCount;
    }

    public int getEndCount(){
        return endCount;
    }

    public String getMarkerTitle(){
        return markerTitle;
    }

    private void setLastSelected(boolean lastSelcted){
        this.lastSelcted = lastSelcted;
    }

    private void incStartCount(){
        this.startCount += 1;
    }

    public void decStartCount(){
        this.startCount -= 1;
    }

    private void incEndCount(){
        this.endCount += 1;
    }

    public void decEndCount(){
        this.endCount -= 1;
    }

    public void setStartMarker(Marker startMarker){
        incStartCount();
        setLastSelected(false);
        this.userStartMarker = startMarker;
    }

    public void setEndMarker(Marker endMarker){
        incEndCount();
        setLastSelected(true);
        this.userEndMarker = endMarker;
    }

    public void setMarkerTitle(String title){
        markerTitle = title;
    }

    public void setWaypointsLatsLngs(List<Double> lats, List<Double> lngs){
        waypointsLngs = new ArrayList<>(lats);
        waypointsLats = new ArrayList<>(lngs);
    }

    public List<Double> getWaypointsLats(){
        return waypointsLats;
    }
    public List<Double> getWaypointsLngs(){
        return waypointsLngs;
    }

    public void clearData() {
        waypointsLats = new ArrayList<Double>();
        waypointsLngs = new ArrayList<Double>();
        this.startCount = 0;
        this.endCount = 0;
        this.lastSelcted = false;
        this.userEndMarker = null;
        this.userStartMarker = null;
    }
}
