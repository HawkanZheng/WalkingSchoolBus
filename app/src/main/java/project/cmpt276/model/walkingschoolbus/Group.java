package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hawkan Zheng on 3/9/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    private Long id;
    private String href;
    private String groupDescription;
    private List<Double> routeLatArray = new ArrayList<>();
    private List<Double> routeLngArray = new ArrayList<>();
    private User leader;
    private String leaderHref;

//      /*
//        Singleton Support
//         */
//
//
//    private static Group instance;
//    private Group(){
//        //Private to prevent anyone else from instantiating
//    }
//    public static Group getInstance(){
//        if (instance == null){
//            instance = new Group();
//        }
//
//        return instance;
//
//    }

    public String getLeaderHref() {
        return leaderHref;
    }

    public void setLeaderHref(String leaderHref) {
        this.leaderHref = leaderHref;
    }

    private List<User> memberUsers;

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
        return "Group ID: " + id + "\nGroup Description: " + groupDescription;
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