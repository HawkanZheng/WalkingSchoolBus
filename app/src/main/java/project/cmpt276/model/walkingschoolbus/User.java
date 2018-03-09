package project.cmpt276.model.walkingschoolbus;

import java.util.ArrayList;

/**
 * Created by bsing on 2018-03-02.
 */

public class User {



    private String name;
    private String password;
    private String userName;

//    private ArrayList<group> groups = new ArrayList<>();


    public User(String name, String password, String userName) {
        this.name = name;
        this.password = password;
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setName(String name) {
        this.name = name;
    }


    //not sure if getters are needed
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

//    private void addToGroup(group newGroup)
//    {
//        groups.add(newGroup);
//
//    }

    public String getUserName() {
        return userName;
    }








}
