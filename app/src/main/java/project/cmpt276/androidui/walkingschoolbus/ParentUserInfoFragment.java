package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.ParentDashDataCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;

/**
 * Created by Jorawar on 3/26/2018.
 */

public class ParentUserInfoFragment extends AppCompatDialogFragment {
    private WGServerProxy proxy;
    private SharedValues sharedValues;
    private User user;

    // Contains the group and selected user
    private ParentDashDataCollection parentData = ParentDashDataCollection.getInstance();

    private ArrayList<String> userInfo = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Get shareValues instance
        sharedValues = SharedValues.getInstance();
        //Get selected user
        user = sharedValues.getUser();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.parent_user_info_fragment_layout,null);


        // Create btn
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Empty on purpose right now
            }
        };

        populateList(v);

        // Build Dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.parent_fragment_user_info_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok,listener)
                .create();

    }

    private void populateList(View v) {
        //Get user parent info
        getUserInfo();

        // Build adapter and show the items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.parent_user_info_fragment_list_layout, userInfo);
        ListView list = (ListView) v.findViewById(R.id.lstParentUserInfo);
        list.setAdapter(adapter);
    }

    //Get user parent info
    private void getUserInfo(){
        List<User> userParents = user.getMonitoredByUsers();
        for(User parent : userParents){
            userInfo.add(parent.userInfoToString());
        }
    }
}
