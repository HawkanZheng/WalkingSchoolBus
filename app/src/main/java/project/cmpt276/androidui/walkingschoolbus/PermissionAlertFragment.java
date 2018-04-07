package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by bsing on 2018-04-05.
 */

public class PermissionAlertFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.permissions, null);
        String message = ((Permissions)getActivity()).getMessage();

//        TextView textView = getActivity().findViewById(R.id.appr)

        DialogInterface.OnClickListener listener =  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int position = ((Permissions)getActivity()).getPosition();
            switch(i)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    ((Permissions)getActivity()).showGrantedToast();
                    ((Permissions)getActivity()).deleteItem(position);
                    ((Permissions)getActivity()).grantPermission();

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    ((Permissions)getActivity()).showDeniedToast();
                    ((Permissions)getActivity()).deleteItem(position);
                    ((Permissions)getActivity()).denyPermission();

                    break;

                case DialogInterface.BUTTON_NEUTRAL:
                    break;
            }




            }
        };


        return new AlertDialog.Builder(getActivity()).setTitle("Permission")
                .setView(v).setPositiveButton("Approve", listener)
                .setNegativeButton("Deny", listener)
                .setNeutralButton("Cancel", listener)
                .create();
    }
}
