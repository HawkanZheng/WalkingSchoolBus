package project.cmpt276.model.walkingschoolbus;

/**
 * Created by Hawkan Zheng on 3/12/2018.
 */

public class SharedValues {
    private  String token;
    private Group group;



    private User user;

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
}
