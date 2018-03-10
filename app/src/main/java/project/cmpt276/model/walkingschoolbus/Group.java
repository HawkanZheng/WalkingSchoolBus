package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Created by Hawkan Zheng on 3/9/2018.
 */

public class Group {
    private Long id;
    private String href;
    private String groupDescription;
    private double[] routeLatArray;
    private double[] routeLngArray;

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public List<User> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(List<User> memberUsers) {
        this.memberUsers = memberUsers;
    }

    private Long leaderId;
    private List<User> memberUsers;


    public double[] getRouteLngArray() {
        return routeLngArray;
    }

    public void setRouteLngArray(double[] routeLngArray) {
        this.routeLngArray = routeLngArray;
    }

    public double[] getRouteLatArray() {
        return routeLatArray;
    }

    public void setRouteLatArray(double[] routeLatArray) {
        this.routeLatArray = routeLatArray;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }
    @JsonIgnore
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
