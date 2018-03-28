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

import project.cmpt276.model.walkingschoolbus.ParentDashDataCollection;

/**
 * Created by Jorawar on 3/26/2018.
 */

public class ParentUserInfoFragment extends AppCompatDialogFragment {

    // Contains the group and selected user
    private ParentDashDataCollection parentData = ParentDashDataCollection.getInstance();

    private ArrayList<String> userInfo = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create View

        // TODO: Remove this when done
        userInfo.add("TEST");

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
                .setTitle("User Info")
                .setView(v)
                .setPositiveButton(android.R.string.ok,listener)
                .create();

    }

    private void populateList(View v) {
        // Build adapter and show the items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.parent_user_info_fragment_list_layout, userInfo);
        ListView list = (ListView) v.findViewById(R.id.lstParentUserInfo);
        list.setAdapter(adapter);
    }
}
