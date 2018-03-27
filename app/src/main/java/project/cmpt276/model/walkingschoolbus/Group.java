package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hawkan Zheng on 3/9/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    private long id;
    private String href;
    private String groupDescription;
    private List<Double> routeLatArray = new ArrayList<>();
    private List<Double> routeLngArray = new ArrayList<>();
    private User leader;
    private String leaderHref;
    private List<User> memberUsers;
    private LatLng startMarker;
    private LatLng endMarker;

    public String getLeaderHref() {
        return leaderHref;
    }

    public void setLeaderHref(String leaderHref) {
        this.leaderHref = leaderHref;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public List<User> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(List<User> memberUsers) {
        this.memberUsers = memberUsers;
    }

    public List<Double> getRouteLatArray() {
        return routeLatArray;
    }

    public List<Double> getRouteLngArray() {
        return routeLngArray;
    }

    public void setRouteLatArray(List<Double> routeLatArray) {
        this.routeLatArray = routeLatArray;
    }

    public void setRouteLngArray(List<Double> routeLngArray) {
        this.routeLngArray = routeLngArray;
    }

    public void addRouteLatArray(Double coordinate) {
        this.routeLatArray.add(coordinate);
    }

    public void addRouteLngArray(Double coordinate) {
        this.routeLngArray.add(coordinate);
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    //Needed for defining the group's start location.
    public void setStartMarker(LatLng s){
        startMarker = s;
    }

    //Needed to pull the group's starting location.
    public LatLng getStartMarker(){
        return startMarker;
    }

    //Needed for defining the group's end location.
    public void setEndMarker(LatLng e){
        endMarker = e;
    }

    //Needed to pull the group's end location.
    public LatLng getEndMarker(){
        return endMarker;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String groupToListString(){
        return "Group ID: " + id + "\n Group Description: " + groupDescription;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id :" + id +
                ", groupDescription : '" + groupDescription+ '\'' +
                ", routeLatArray : '" + routeLatArray + '\'' +
                ", routeLngArray : '" + routeLngArray + '\'' +
                ", leader : {" +
                "id : " + leader +
                "}, " + '\'' + "memberUsers[] : " + memberUsers +
                '}';
    }
}