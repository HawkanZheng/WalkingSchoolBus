package project.cmpt276.model.walkingschoolbus;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorawar on 3/12/2018.
 */

public class fragmentDataCollection {
    // Track the markers made
    private Marker[] markers = new Marker[2];
    private String markerTitle;
    private List<Double> waypointsLats;
    private List<Double> waypointsLngs;

    // Used to pass the last selected group
    private Group groupToBeAdded = new Group();

    // Singleton
    private static fragmentDataCollection instance;


    private fragmentDataCollection(){
        // Singleton Pattern
        waypointsLats = new ArrayList<Double>();
        waypointsLngs = new ArrayList<Double>();
    }

    public static fragmentDataCollection getInstance(){
        if(instance == null){
            instance = new fragmentDataCollection();
        }
        return instance;
    }

    public Group getGroupToBeAdded(){
        return groupToBeAdded;
    }

    public Marker getStartMarker(){
        return markers[0];
    }

    public Marker getEndMarker(){
        return markers[1];
    }

    public String getMarkerTitle(){
        return markerTitle;
    }

    public void storeStartMarker(Marker startMarker){
        markers[0] = startMarker;
    }

    public void storeEndMarker(Marker endMarker){
        markers[1] = endMarker;
    }

    public void setMarkerTitle(String title){
        markerTitle = title;
    }

    public void setWaypointsLatsLngs(List<Double> lats, List<Double> lngs){
        waypointsLngs = new ArrayList<>(lngs);
        waypointsLats = new ArrayList<>(lats);
    }

    public void setGroupToBeAdded(Group group){
        groupToBeAdded = group;
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
        markerTitle = null;
        markers = new Marker[2];
    }

    public void clearRoutes(){
        waypointsLats = new ArrayList<Double>();
        waypointsLngs = new ArrayList<Double>();
    }
}
