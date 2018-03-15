package project.cmpt276.model.walkingschoolbus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hawkan Zheng on 3/10/2018.
 */

public class GroupCollection {
    private List<Group> groups = new ArrayList<>();

    /*
    Singleton Support
   */
    private static GroupCollection instance;
    private GroupCollection(){
        //Private to prevent anyone else from instantiating
    }
    public static GroupCollection getInstance(){
        if (instance == null){
            instance = new GroupCollection();
        }
        return instance;
    }

    public void addGroup(Group group)
    {
        groups.add(group);
    }

    public int numGroups(){
        return groups.size();
    }

    public Group getGroup(int index){
        if (index < 0 || index >= numGroups()) {
            throw new IllegalArgumentException();
        }
        return groups.get(index);
    }

    public String[] getGroupDetails(){
        String [] details = new String[numGroups()];
        for (int i = 0;i < numGroups(); i++){
            Group group = getGroup(i);
            details[i] = group.toString();
        }
        return details;
    }
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
