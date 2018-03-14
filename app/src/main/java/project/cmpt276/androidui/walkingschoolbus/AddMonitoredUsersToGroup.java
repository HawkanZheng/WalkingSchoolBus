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

import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.User;

public class AddMonitoredUsersToGroup extends AppCompatDialogFragment {
    private User user;
    private View v;
    private Group currGroup;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create the view to show.
        v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_monitored_users_to_group, null);
        user = User.getInstance();
        fillGroupsList();
        fillMonitorsList();
        setFinishBtn();
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    private void fillGroupsList(){
        List<Group> groups = user.getWalkingGroups();
        Log.i("MonitorFragment","Group # :" + groups.size());
        Log.i("MonitorFragment","GroupCall # :" + user.getWalkingGroups());
        ListView listview = v.findViewById(R.id.usersGroups);
        ArrayAdapter<Group> groupAdapter = new ArrayAdapter<Group>(getActivity(),R.layout.user_groups_list,groups);
        listview.setAdapter(groupAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Select a group to add a monitored user to.
                currGroup = groups.get(position);
            }
        });
    }

    private void fillMonitorsList(){
        List<User> monitored = user.getMonitorsUsers();
        Log.i("MonitorFragment","Monitor # :" + monitored.size());
        Log.i("MonitorFragment","MonitorCall # :" + user.getMonitorsUsers());
        ListView listview = v.findViewById(R.id.usersMonitored);
        ArrayAdapter<Group> monitoredAdapter = new ArrayAdapter<Group>(v.getContext(),R.layout.monitors_users_list);
        listview.setAdapter(monitoredAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currGroup != null){
                    //If there is a current group selected, clicking on an element within the list will add the user to that group.
                    if(isInGroup(monitored, monitored.get(position).getId())){
                        currGroup.addMembertoGroup(monitored.get(position));
                    }
                }
            }
        });
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
