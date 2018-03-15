package project.cmpt276.model.walkingschoolbus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hawkan Zheng on 3/12/2018.
 */
/*
Shared values to be used between activities such as token, user...
 */
public class SharedValues {
    private  String token;
    private Group group;



    private User user;

    private List<User> userList = new ArrayList<>();



    private static SharedValues instance;
    private SharedValues(){
    }

    public static SharedValues getInstance(){
        if (instance == null){
            instance = new SharedValues();
        }

        return instance;

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
