package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.model.walkingschoolbus.fragmentDataCollection;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

/**
 * Created by Jerry on 3/13/2018.
 */

public class GroupCreationFragment extends AppCompatDialogFragment {

    private fragmentDataCollection fragmentData = fragmentDataCollection.getInstance();
    private EditText groupTitle;
    private View v;
    private TextView errorLog;
    private GroupCollection groupList;
    private SharedValues sharedValues;
    private WGServerProxy proxy;
    private User user;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Get shareValues (token)
        sharedValues = SharedValues.getInstance();
        //Get server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey),sharedValues.getToken());
        //Get current user instance
        user = User.getInstance();
        //Get the group collection list
        groupList = GroupCollection.getInstance();
        //Create the view to show.
        v = LayoutInflater.from(getActivity()).inflate(R.layout.group_creation, null);
        Button confirmGroup = v.findViewById(R.id.confirmGroup);
        Button cancel = v.findViewById(R.id.cancelGroup);
        groupTitle = v.findViewById(R.id.groupTitleEntry);
        errorLog = v.findViewById(R.id.errorBox);
        confirmGroup.setOnClickListener(view -> createGroup());
        cancel.setOnClickListener(view -> dismiss());
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    private void createGroup(){
        // Clear the error log between launches
        errorLog.setText("");

        Group group = new Group();

        if(groupTitle != null && !(groupTitle.getText().toString().isEmpty())){
            group.setGroupDescription(groupTitle.getText().toString());
        }else{
            errorLog.setText("A group needs a name...");
            Log.i("Fragment","No Group Name");
            return;
        }

        if(fragmentData.getWaypointsLats() != null && !(fragmentData.getWaypointsLats().isEmpty())){
            group.setRouteLatArray(fragmentData.getWaypointsLats());
        }else{
            errorLog.setText("No latitude data found...");
            Log.i("Fragment","No Latitude");
            return;
        }
        if(fragmentData.getWaypointsLngs() != null && !(fragmentData.getWaypointsLngs().isEmpty())){
            group.setRouteLngArray(fragmentData.getWaypointsLngs());
        }else{
            errorLog.setText("No longitude data found...");
            Log.i("Fragment","No Longitude");
            return;

        }
        //group.setId(-1);
        group.setLeader(user);
        Call<Group> caller = proxy.createGroup(group);
        ProxyBuilder.callProxy(getActivity(), caller, returnedGroup -> groupResponse(returnedGroup));
    }

    private void groupResponse(Group returnedGroup) {
        Log.i("Group returned: ", returnedGroup.toString());
        sharedValues.setGroup(returnedGroup);
        groupList.addGroup(returnedGroup);
        //Get updated user
        Toast.makeText(getActivity(),"New Group Saved!", Toast.LENGTH_SHORT).show();
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(getActivity(), caller, returnedUser -> userResponse(returnedUser));
    }

    private void getGroups() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(getActivity(), caller, returnedGroups ->groupsResponse(returnedGroups));
    }

    private void groupsResponse(List<Group> returnedGroups) {
        //refresh group list
        groupList.setGroups(returnedGroups);
    }

    private void userResponse(User returnedUser) {
        User.setUser(returnedUser);
        //Dismiss the dialog once a group is successfully made.
        dismiss();
//        Call<List<User>> caller = proxy.addNewMember(sharedValues.getGroup().getId(), returnedUser);
//        ProxyBuilder.callProxy(getActivity(), caller, returnedMembers -> membersResponse(returnedMembers));
    }

//    private void membersResponse(List<User> returnedMembers) {
//        Log.i("New Group:", "Now member of group");
//        //Dismiss the dialog once a group is successfully made.
//        dismiss();
//    }


}
