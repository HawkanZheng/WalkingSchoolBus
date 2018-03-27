package project.cmpt276.model.walkingschoolbus;

/**
 * Created by Jorawar on 3/26/2018.
 */

public class ParentDashDataCollection {
    private static final ParentDashDataCollection ourInstance = new ParentDashDataCollection();

    private Group lastGroupSelected = new Group();
    private User lastUserSelected;

    public Group getLastGroupSelected() {
        return lastGroupSelected;
    }

    public void setLastGroupSelected(Group lastGroupSelected) {
        this.lastGroupSelected = lastGroupSelected;
    }

    public User getLastUserSelected() {
        return lastUserSelected;
    }

    public void setLastUserSelected(User lastUserSelected) {
        this.lastUserSelected = lastUserSelected;
    }

    public static ParentDashDataCollection getInstance() {
        return ourInstance;
    }

    private ParentDashDataCollection() {
        // singleton pattern
    }
}
