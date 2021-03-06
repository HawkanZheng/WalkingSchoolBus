package project.cmpt276.model.walkingschoolbus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * Created by Jorawar on 4/3/2018.
 */

public class GamificationCollection {

    public static final int POINTS = 50;
    public static final int IMGW = 80;
    public static  final int IMGH = 120;
    private static GamificationCollection ourInstance = new GamificationCollection();
    public static GamificationCollection getInstance() {
        if(ourInstance == null){
            ourInstance = new GamificationCollection();
        }
        return ourInstance;
    }
    public static void setOurInstance(GamificationCollection obj){
        ourInstance = obj;
    }
    private String[] avatarNames = {"Aquaman", "Batman", "Black Canary",
            "Black Widow", "Captain America", "Flash",
            "Green Arrow", "Green Lantern", "Hawkeye",
            "Hulk", "Iron Man", "Martian Manhunter",
            "Ms. Marvel", "Red Tornado", "Sentry",
            "Spider Woman", "Spider Man", "Superman",
            "Thor", "Wonder Woman"};

    public boolean[] getAvatarUnlockState() {
        return avatarUnlockState;
    }
    private boolean[] avatarUnlockState = new boolean[avatarNames.length];
    private int avatarSelectedPosition = -1;

    private GamificationCollection() {
        // Singleton Pattern
    }

    @JsonIgnore
    public int getNumRewards(){
        return avatarNames.length;
    }

    public void setAvatarUnlockStateByName(String avatarName, boolean state){
        int numAvatar = getNumRewards();
        for(int i = 0; i<numAvatar;i++){
            if(avatarName.equals(avatarNames[i])){
                avatarUnlockState[i] = state;
            }
        }
    }

    public void setAvatarUnlockStateByPos(int pos, boolean state){
        if(0 <= pos && pos < getNumRewards()){
            avatarUnlockState[pos] = state;
        }
    }
    @JsonIgnore
    public boolean getAvatarStateByName(String avatarName){
        boolean state = false;
        int numAvatar = getNumRewards();
        for(int i = 0; i<numAvatar;i++){
            if(avatarName.equals(avatarNames[i])){
                state = avatarUnlockState[i];
            }
        }
        return state;
    }
    @JsonIgnore
    public boolean getAvatarStateByPosition(int pos){
        if(0 <= pos && pos < getNumRewards()){
            return avatarUnlockState[pos];
        }
        else{
            return false;
        }
    }
    @JsonIgnore
    // Returns -1 if error
    public int getPostionOfAvatar(String avatarName){
        int pos = -1;
        int numAvatar = getNumRewards();
        for(int i = 0; i<numAvatar; i++){
            if(avatarName.equals(avatarNames[i])){
                pos = i;
            }
        }
        return pos;
    }
    @JsonIgnore
    public String getAvatarAtPostion(int position){
        return avatarNames[position];
    }

    public int getAvatarSelectedPosition() {
        return avatarSelectedPosition;
    }


    public void setAvatarSelectedPosition(int avatarSelectedPosition) {
        this.avatarSelectedPosition = avatarSelectedPosition;
    }

    public static GamificationCollection trashInstance(){
        ourInstance = new GamificationCollection();
        return ourInstance;
    }
}
