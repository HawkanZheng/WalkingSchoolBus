package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.fragmentDataCollection;

/**
 * Created by Jerry on 3/13/2018.
 */

public class GroupCreationFragment extends AppCompatDialogFragment {

    private fragmentDataCollection fragmentData = fragmentDataCollection.getInstance();
    private EditText groupTitle;
    private View v;
    private TextView errorLog;
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
        Group group = new Group();

        if(groupTitle != null && !(groupTitle.getText().toString().isEmpty())){
            group.setGroupDescription(groupTitle.getText().toString());
        }else{
            errorLog.setText("A group needs a name...");
        }

        if(fragmentData.getWaypointsLats() != null){
            group.setRouteLatArray(fragmentData.getWaypointsLats());
        }else{
           errorLog.setText("No latitude data found...");
        }
        if(fragmentData.getWaypointsLngs() != null){
            group.setRouteLngArray(fragmentData.getWaypointsLngs());
        }else{
            errorLog.setText("No longitude data found...");
        }

        /*
        * Set leader
        * Set id
        * Push group to server and add it to client.
        */
        //Dismiss the dialog once a group is successfully made.
        //dismiss();
    }

}
