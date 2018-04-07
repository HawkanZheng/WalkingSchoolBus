package project.cmpt276.model.walkingschoolbus;

/**
 * Created by Jorawar on 4/4/2018.
 */

public class RewardRank {
    private String[] ranks = {"Rookie",
                              "Protege",
                              "Hero",
                              "Super Hero",
                              "Avenger",
                              "Legend"};

    public String getRankTitle(int totalPointsEarned){
        int rankPosition = 0;

        if(totalPointsEarned < 100){
            rankPosition = 0;
        }
        else if(100 <= totalPointsEarned && totalPointsEarned < 200){
            rankPosition = 1;
        }
        else if(200 <= totalPointsEarned && totalPointsEarned < 300){
            rankPosition = 2;
        }
        else if(300 <= totalPointsEarned && totalPointsEarned < 400){
            rankPosition = 3;
        }
        else if(400 <= totalPointsEarned && totalPointsEarned < 500){
            rankPosition = 4;
        }
        else if(500 <= totalPointsEarned){
            rankPosition = 5;
        }

        return ranks[rankPosition];
    }
}
