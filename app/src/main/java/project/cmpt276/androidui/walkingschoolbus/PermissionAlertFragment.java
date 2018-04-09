package project.cmpt276.androidui.walkingschoolbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.PermissionRequest;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import static project.cmpt276.server.walkingschoolbus.WGServerProxy.PermissionStatus.APPROVED;
import static project.cmpt276.server.walkingschoolbus.WGServerProxy.PermissionStatus.DENIED;

/**
 * Created by bsing on 2018-04-05.
 */

public class PermissionAlertFragment extends DialogFragment {
    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }


    private User user;
    private WGServerProxy proxy;
    private SharedValues sharedValues;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Get Instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //Get server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.permissions, null);
        String message = ((Permissions) getActivity()).getMessage();

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int position = ((Permissions) getActivity()).getPosition();
                List<PermissionRequest> requests = sharedValues.getRequests();
                switch (i) {
                    //Approve permission request
                    case DialogInterface.BUTTON_POSITIVE:
                        //server call
                        Call<PermissionRequest> approveCaller = proxy.approveOrDenyPermissionRequest(requests.get(position).getId(), APPROVED);
                        requests.remove(position);

                        sharedValues.setRequests(requests);


                        ((Permissions) getActivity()).permissionsResponse(requests);
                        Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                        ProxyBuilder.callProxy(getActivity(), approveCaller, returnedPermission -> approvePermissionResponse(returnedPermission, position));
                        break;

                    //Deny permission request
                    case DialogInterface.BUTTON_NEGATIVE:
                        //server call
                        Call<PermissionRequest> denyCaller = proxy.approveOrDenyPermissionRequest(requests.get(position).getId(), DENIED);

                        requests.remove(position);
                        sharedValues.setRequests(requests);

                        ((Permissions) getActivity()).permissionsResponse(requests);
                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                        ProxyBuilder.callProxy(getActivity(), denyCaller, returnedPermission -> denyPermissionResponse(returnedPermission, position));
                        break;

                    //Cancel alert
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


    //deny request server response
    private void denyPermissionResponse(PermissionRequest returnedPermission, int position) {

        dismiss();


    }

    //approve request server response
    private void approvePermissionResponse(PermissionRequest returnedPermission, int position) {

        dismiss();
    }


}