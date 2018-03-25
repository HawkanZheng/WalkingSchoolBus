package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple User class to store the data the server expects and returns.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    //Variables
    private Long id;
    private String name;
    private String email;
    private String password;
    private String href;
    private lastGpsLocation lastGpsLocation;

    //Containers
    private List<User> monitoredByUsers = new ArrayList<>();
    private List<User> monitorsUsers = new ArrayList<>();
    private List<Group> walkingGroups = new ArrayList<>();



    private List<Group> memberOfGroups = new ArrayList<>();
    private List<Group> leadsGroups = new ArrayList<>();
    //For UI Display
    private String[] monitorsUsersString;
    private String[] monitoredByUsersString;
    private List<String> memberOfGroupsString = new ArrayList<>();


//For messaging
    private List<Message> unreadMessages = new ArrayList<>();
    private List<Message> readMessages = new ArrayList<>();

    /*
    Singleton Support
    */
    private static User instance;
    private User(){
        //Private to prevent anyone else from instantiating
    }
    public static User getInstance(){
        if (instance == null){
            instance = new User();
        }
        return instance;
    }

    public static void setUser(User user){
        instance = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    //@JsonIgnore
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
//    @JsonIgnore
    public List<User> getMonitoredByUsers() {
        return monitoredByUsers;
    }

    public void setMonitoredByUsers(List<User> monitoredByUsers) {
        this.monitoredByUsers = monitoredByUsers;
    }
    //@JsonIgnore
    public List<User> getMonitorsUsers() {
        return monitorsUsers;
    }

    public void setMonitorsUsers(List<User> monitorsUsers) {
        this.monitorsUsers = monitorsUsers;
    }
   // @JsonIgnore
    public List<Group> getWalkingGroups() {
        return walkingGroups;
    }

    public void setWalkingGroups(List<Group> walkingGroups) {
        this.walkingGroups = walkingGroups;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
    //@JsonIgnore
    public List<Group> getMemberOfGroups() {
        return memberOfGroups;
    }

    public void setMemberOfGroups(List<Group> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }
//    @JsonIgnore
    public List<Group> getLeadsGroups() {
        return leadsGroups;
    }

    public void setLeadsGroups(List<Group> leadsGroups) {
        this.leadsGroups = leadsGroups;
    }

//Getters and Setters for messaging


    public List<Message> getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(List<Message> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public List<Message> getReadMessages() {
        return readMessages;
    }

    public void setReadMessages(List<Message> readMessages) {
        this.readMessages = readMessages;
    }

    //Get and set last gps location
    public lastGpsLocation getLastGpsLocation() {
        return lastGpsLocation;
    }

    public void setLastGpsLocation (lastGpsLocation lastGpsLocation) {
        this.lastGpsLocation = lastGpsLocation;
    }

//Getters and setters for UI display arrays

    public String[] getMonitorsUsersString() {
        return monitorsUsersString;
    }

    public void setMonitorsUsersString(String[] monitorsUsersString) {
        this.monitorsUsersString = monitorsUsersString;
    }

    public String[] getMonitoredByUsersString() {
        return monitoredByUsersString;
    }

    public void setMonitoredByUsersString(String[] monitoredByUsersString) {
        this.monitoredByUsersString = monitoredByUsersString;
    }

    public List<String> getMemberOfGroupsString() {
        return memberOfGroupsString;
    }

    public void setMemberOfGroupsString(List<String> memberOfGroupsString) {
        this.memberOfGroupsString = memberOfGroupsString;
    }
    public void addMemberOfGroupsString(String group){
        this.memberOfGroupsString.add(group);
    }

    public void setMemberInList(int i, String string){
        this.memberOfGroupsString.set(i, string );
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", monitoredByUsers=" + monitoredByUsers +
                ", monitorsUsers=" + monitorsUsers +
                ", memberOfGroups=" + memberOfGroups +
                '}';
    }
}
