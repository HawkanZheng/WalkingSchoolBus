package project.cmpt276.model.walkingschoolbus;

import java.util.ArrayList;

/**
 * Created by Jorawar on 4/3/2018.
 */

public class GamificationCollection {
    private static GamificationCollection ourInstance = new GamificationCollection();
    public static GamificationCollection getInstance() {
        if(ourInstance == null){
            ourInstance = new GamificationCollection();
        }
        return ourInstance;
    }
    private String[] avatarNames = {"Aquaman", "Batman", "Black Canary",
            "Black Widow", "Captain America", "Flash",
            "Green Arrow", "Green Lantern", "Hawkeye",
            "Hulk", "Iron Man", "Martian Manhunter",
            "Ms. Marvel", "Red Tornado", "Sentry",
            "Spider Woman", "Spider Man", "Superman",
            "Thor"};

    private boolean[] avatarUnlockState = new boolean[avatarNames.length];

    private GamificationCollection() {
        // Singleton Pattern
    }

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

    public boolean getAvatarStateByPosition(int pos){
        if(0 <= pos && pos < getNumRewards()){
            return avatarUnlockState[pos];
        }
        else{
            return false;
        }
    }

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

    public String getAvatarAtPostion(int position){
        return avatarNames[position];
    }

}
