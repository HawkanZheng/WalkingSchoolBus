package project.cmpt276.model.walkingschoolbus;

import java.util.ArrayList;

/**
 * Created by Jorawar on 4/3/2018.
 */

public class RewardsAvatarCollection {

    private int defaultCost = 150;

    // Currently supported of avatars
    private String[] avatarNames = {"Aquaman", "Batman", "Black Canary",
                                    "Black Widow", "Captain America", "Flash",
                                    "Green Arrow", "Green Lantern", "Hawkeye",
                                    "Hulk", "Iron Man", "Martian Manhunter",
                                    "Ms. Marvel", "Red Tornado", "Sentry",
                                    "Spider Woman", "Spider Man", "Superman",
                                    "Thor"};

    private ArrayList<RewardAvatar> rewardList = new ArrayList<>();


    public RewardsAvatarCollection() {
        for(int i = 0; i < avatarNames.length; i++){
            RewardAvatar tmp = new RewardAvatar();
            tmp.setAvatarName(avatarNames[i]);
            tmp.setAvatarCost(defaultCost);
            tmp.setUnlocked(false);

            rewardList.add(tmp);
        }
    }

    // Returns the position of the string searched
    // Returns -1 if an error has occurred
    private int searchAvatarByString(String stringToSearch){
        int position = -1;
        for(int i = 0; i<rewardList.size(); i++){
            if(rewardList.get(i).getAvatarName() == stringToSearch){
                position = i;
            }
        }
        return position;
    }

    // Set the unlock value of a avatar
    public void setUnlockValueOfAvatar(String avatarName, Boolean unlockState){
        int position = searchAvatarByString(avatarName);
        if(position != -1){
            rewardList.get(position).setUnlocked(unlockState);
        }
    }

    // return all avatars
    public ArrayList<RewardAvatar> returnAllAvatars(){
        return rewardList;
    }

    // return a searched for avatar
    public RewardAvatar getAvatar(String avatarName){
        return rewardList.get(searchAvatarByString(avatarName));
    }
}
