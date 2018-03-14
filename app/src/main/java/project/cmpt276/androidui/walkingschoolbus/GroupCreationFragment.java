package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.model.walkingschoolbus.fragmentDataCollection;

/**
 * Created by Jerry on 3/13/2018.
 */

public class GroupCreationFragment extends AppCompatDialogFragment {

    private fragmentDataCollection fragmentData = fragmentDataCollection.getInstance();
    private EditText groupTitle;
    private View v;
    private TextView errorLog;
    private List<User> monitoredUsers;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

        Toast.makeText(getActivity(),"New Group Saved!", Toast.LENGTH_SHORT).show();

        /*
        * Set leader
        * Set id
        * Push group to server and add it to client.
        */
        //Dismiss the dialog once a group is successfully made.
        //dismiss();
    }

}
