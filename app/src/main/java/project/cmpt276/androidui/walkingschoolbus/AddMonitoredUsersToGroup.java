package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.model.walkingschoolbus.fragmentDataCollection;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class AddMonitoredUsersToGroup extends AppCompatDialogFragment {
    private User user;
    private View v;
    private SharedValues sharedValues;
    private WGServerProxy proxy;
    private GroupCollection groupList;
    private Group currGroup = new Group();
    private fragmentDataCollection fragmentData = fragmentDataCollection.getInstance();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create the view to show.
        v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_monitored_users_to_group, null);
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        groupList = GroupCollection.getInstance();
        getMemberOfGroups(user);
        getMonitorsUsers(user);
        currGroup = fragmentData.getGroupToBeAdded();
        setFinishBtn();
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    private void getMemberOfGroups(User currUser) {
        currUser.setMemberOfGroupsString(new ArrayList<String>());

        for(int i = 0; i < currUser.getMemberOfGroups().size(); i++){
            Group aGroup = currUser.getMemberOfGroups().get(i);
            user.addMemberOfGroupsString(aGroup.groupToListString());
            Log.i("MonitorFragment","GroupCall # :" +aGroup.getId() + "\nString Group: " + aGroup.groupToListString());
            Call<Group> caller = proxy.getGroupById(aGroup.getId());

            int finalI = i;
            ProxyBuilder.callProxy(getActivity(), caller, returnedGroup -> groupResponse(returnedGroup, finalI));
        }
    }

    private void groupResponse(Group returnedGroup, int i) {
        user.setMemberInList(i,returnedGroup.groupToListString());
    }



    private void getMonitorsUsers(User currUser) {
        //User user = User.getInstance();
        Call<List<User>> caller = proxy.getUsersMonitered(currUser.getId());
        ProxyBuilder.callProxy(getActivity(), caller, returnedUsers -> monitorsResponse(returnedUsers));
    }

    private void monitorsResponse(List<User> returnedUsers) {
        String[] users = new String[returnedUsers.size()];
        for (int i = 0;i < returnedUsers.size(); i++){
            User monitorUser = returnedUsers.get(i);
            users[i] = "Name: " + monitorUser.getName() + "\nEmail: " + monitorUser.getEmail();
        }
        user.setMonitorsUsersString(users);
        fillMonitorsList(users);
    }

    private void fillMonitorsList(String[] users){
        user = User.getInstance();
        List<User> monitored = user.getMonitorsUsers();
        List<String> monitoredUsersName = new ArrayList<>();


        Log.i("Size","" +monitoredUsersName.size());

        Log.i("MonitorFragment","Monitor # :" + monitored.size());
        Log.i("MonitorFragment","MonitorCall # :" + user.getMonitorsUsers());

        ListView listview = v.findViewById(R.id.usersMonitored);
        ArrayAdapter<String> monitoredAdapter = new ArrayAdapter<String>(v.getContext(),R.layout.monitors_users_list,users);
        listview.setAdapter(monitoredAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(currGroup != null){
                    Log.i("MonitorFragment", "Group ID: " + currGroup.getId());

                    //If there is a current group selected, clicking on an element within the list will add the user to that group.
                    Log.i("Condition","bool:" + isInGroup(monitored, monitored.get(position).getId()));
                    Log.i("Condition","monitored" + monitored.get(position));

                    //if(isInGroup(monitored, monitored.get(position).getId())){
                        //currGroup.addMembertoGroup(monitored.get(position));
                        Call<List<User>> caller = proxy.addNewMember(currGroup.getId(), monitored.get(position));
                        ProxyBuilder.callProxy(getActivity(), caller, returnedMembers -> memberResponse(returnedMembers));
                   // }
                }
            }
        });
    }

    private void memberResponse(List<User> returnedMembers) {
        Toast.makeText(getActivity(), "User Added.", Toast.LENGTH_SHORT).show();
        getGroups();
    }

    private void getGroups() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(getActivity(), caller, returnedGroups ->groupsResponse(returnedGroups));
    }

    private void groupsResponse(List<Group> returnedGroups) {
        groupList.setGroups(returnedGroups);
    }

    private void setFinishBtn(){
        Button btn = v.findViewById(R.id.finishBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private boolean isInGroup(List<User> users, long userId){
        for(int i = 0; i < users.size(); i++){
            if(userId == users.get(i).getId()){
                return false;
            }
        }
        return true;
    }
}
