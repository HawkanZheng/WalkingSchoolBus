package project.cmpt276.model.walkingschoolbus;

/**
 * Created by Jorawar on 4/3/2018.
 */

public class RewardAvatar {
    private String avatarName;
    private Boolean isUnlocked;
    private int avatarCost;

    public Boolean getUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        isUnlocked = unlocked;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public int getAvatarCost() {
        return avatarCost;
    }

    public void setAvatarCost(int avatarCost) {
        this.avatarCost = avatarCost;
    }
}
