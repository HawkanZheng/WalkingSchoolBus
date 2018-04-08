package project.cmpt276.model.walkingschoolbus;

import java.util.ArrayList;

/**
 * Created by Jorawar on 4/3/2018.
 */

class GamificationCollection {
    private static final GamificationCollection ourInstance = new GamificationCollection();
    static GamificationCollection getInstance() {
        return ourInstance;
    }

    private RewardsAvatarCollection userRewards = new RewardsAvatarCollection();
    private RewardAvatar lastAvatarSelected = new RewardAvatar();

    private GamificationCollection() {
        // Singleton Pattern
    }

    // Set user rewards by name
    public void setUserRewards(String avatarName, Boolean unlockState){
        userRewards.setUnlockValueOfAvatar(avatarName, unlockState);
    }

    // get all avatars and their state
    public ArrayList<RewardAvatar> returnAllAvatars(){
        return userRewards.returnAllAvatars();
    }

    // get a specific avatar
    public RewardAvatar getAvatar(String avatarName){
        return userRewards.getAvatar(avatarName);
    }

    public RewardAvatar getLastAvatarSelected() {
        return lastAvatarSelected;
    }

    public void setLastAvatarSelected(RewardAvatar lastAvatarSelected) {
        this.lastAvatarSelected = lastAvatarSelected;
    }
}
