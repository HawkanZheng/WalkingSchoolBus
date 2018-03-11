package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hawkan Zheng on 3/9/2018.
 */

public class Group {
    private Long id;
    private String href;
    private String groupDescription;

    public List<Double> getRouteLatArray() {
        return routeLatArray;
    }

    public void addRouteLatArray(Double coordinate) {
        this.routeLatArray.add(coordinate);
    }

    public List<Double> getRouteLngArray() {
        return routeLngArray;
    }

    public void addRouteLngArray(Double coordinate) {
        this.routeLngArray.add(coordinate);
    }

    private List<Double> routeLatArray = new ArrayList<>();
    private List<Double> routeLngArray = new ArrayList<>();
    private User leader;
    private String leaderHref;

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