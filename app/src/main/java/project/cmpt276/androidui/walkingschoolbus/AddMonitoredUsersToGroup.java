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

public class AddMonitoredUsersToGroup extends AppCompatDialogFragment {
    private User user;
    private View v;
    private Group currGroup = new Group();
    private fragmentDataCollection fragmentData = fragmentDataCollection.getInstance();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create the view to show.
        v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_add_monitored_users_to_group, null);
        user = User.getInstance();
        currGroup = fragmentData.getGroupToBeAdded();
        fillMonitorsList();
        setFinishBtn();
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }


    private void fillMonitorsList(){
        user = User.getInstance();
        List<User> monitored = user.getMonitorsUsers();
        List<Long> monitoredUsersID = new ArrayList<>();

        for(int i = 0; i<monitored.size();i++){
            monitoredUsersID.add(monitored.get(i).getId());
        }

        Log.i("Size","" +monitoredUsersID.size());

        Log.i("MonitorFragment","Monitor # :" + monitored.size());
        Log.i("MonitorFragment","MonitorCall # :" + user.getMonitorsUsers());
        ListView listview = v.findViewById(R.id.usersMonitored);
        ArrayAdapter<Long> monitoredAdapter = new ArrayAdapter<Long>(v.getContext(),R.layout.monitors_users_list,monitoredUsersID);
        listview.setAdapter(monitoredAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(currGroup != null){

                    Toast.makeText(getActivity(),"You Picked: " + monitored.get(position).toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(),"Group:" + currGroup.getGroupDescription(), Toast.LENGTH_SHORT).show();

                    //If there is a current group selected, clicking on an element within the list will add the user to that group.
                    if(isInGroup(monitored, monitored.get(position).getId())){
                       // currGroup.addMembertoGroup(monitored.get(position));
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
