package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Hawkan Zheng on 3/21/2018.
 */
/*
Message Object Class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    //private variables
    private long id;
    private String text;
    private User fromUser;
    private Group toGroup;
    private Boolean emergency;

    /*
   Singleton Support
   */
//    private static Message instance;
//    private Message(){
//        //Private to prevent anyone else from instantiating
//    }
//    public static Message getInstance(){
//        if (instance == null){
//            instance = new Message();
//        }
//        return instance;
//    }

    //set instance
//    public static void setMessage(Message message){
//        instance = message;
//    }

    //getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public Group getToGroup() {
        return toGroup;
    }

    public void setToGroup(Group toGroup) {
        this.toGroup = toGroup;
    }

    public Boolean getEmergency() {
        return emergency;
    }

    public void setEmergency(Boolean emergency) {
        this.emergency = emergency;
    }





}
