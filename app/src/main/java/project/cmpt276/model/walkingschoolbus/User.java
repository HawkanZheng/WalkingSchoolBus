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
    private Integer birthYear = null;
    private Integer birthMonth = null;
    private String address = null;
    private String cellPhone = null;
    private String homePhone = null;
    private String grade = null;
    private String teacherName = null;
    private String emergencyContactInfo = null;
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


    // Gamification Support
    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private Integer currentPoints;
    private Integer totalPointsEarned;
    private String customJson;

    // Permissions
    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<PermissionRequest> pendingPermissionRequests;

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

    //Getters and setters
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

    public List<User> getMonitoredByUsers() {
        return monitoredByUsers;
    }

    public void setMonitoredByUsers(List<User> monitoredByUsers) {
        this.monitoredByUsers = monitoredByUsers;
    }

    public List<User> getMonitorsUsers() {
        return monitorsUsers;
    }

    public void setMonitorsUsers(List<User> monitorsUsers) {
        this.monitorsUsers = monitorsUsers;
    }

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

    public List<Group> getMemberOfGroups() {
        return memberOfGroups;
    }

    public void setMemberOfGroups(List<Group> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }
    //@JsonIgnore
    public List<Group> getLeadsGroups() {
        return leadsGroups;
    }

    public void setLeadsGroups(List<Group> leadsGroups) {
        this.leadsGroups = leadsGroups;
    }

//Getters and Setters for messaging

    //@JsonIgnore
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
    //@JsonIgnore
    public lastGpsLocation getLastGpsLocation() {
        return lastGpsLocation;
    }

    public void setLastGpsLocation (lastGpsLocation lastGpsLocation) {
        this.lastGpsLocation = lastGpsLocation;
    }
//Geters and setters for gamefication
    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Integer getTotalPointsEarned() {
        return totalPointsEarned;
    }

    public void setTotalPointsEarned(Integer totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }

    public String getCustomJson() {
        return customJson;
    }

    public void setCustomJson(String customJson) {
        this.customJson = customJson;
    }
//getters and setters for permissions
    public List<PermissionRequest> getPendingPermissionRequests() {
        return pendingPermissionRequests;
    }

    public void setPendingPermissionRequests(List<PermissionRequest> pendingPermissionRequests) {
        this.pendingPermissionRequests = pendingPermissionRequests;
    }


    //Getters and setters for UI display arrays
@JsonIgnore
    public String[] getMonitorsUsersString() {
        return monitorsUsersString;
    }

    public void setMonitorsUsersString(String[] monitorsUsersString) {
        this.monitorsUsersString = monitorsUsersString;
    }
    @JsonIgnore
    public String[] getMonitoredByUsersString() {
        return monitoredByUsersString;
    }

    public void setMonitoredByUsersString(String[] monitoredByUsersString) {
        this.monitoredByUsersString = monitoredByUsersString;
    }
    @JsonIgnore
    public List<String> getMemberOfGroupsString() {
        return memberOfGroupsString;
    }

    public void setMemberOfGroupsString(List<String> memberOfGroupsString) {
        this.memberOfGroupsString = memberOfGroupsString;
    }


    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(Integer birthMonth) {
        this.birthMonth = birthMonth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getEmergencyContactInfo() {
        return emergencyContactInfo;
    }

    public void setEmergencyContactInfo(String emergencyContactInfo) {
        this.emergencyContactInfo = emergencyContactInfo;
    }

    public static void setInstance(User instance) {
        User.instance = instance;
    }

    public void addMemberOfGroupsString(String group){
        this.memberOfGroupsString.add(group);
    }

    public void setMemberInList(int i, String string){
        this.memberOfGroupsString.set(i, string );
    }

    public String toNameAndEmailString(){
        return name + "\n" + "email: " + email;
    }

    public String userInfoToString() {
        return "Name: " + name +
                "\nHome Phone Number: " + homePhone +
                "\nCell Phone Number: " + cellPhone +
                "\nAddress: " + address;
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
