package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.PermissionRequest;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import static project.cmpt276.server.walkingschoolbus.WGServerProxy.PermissionStatus.PENDING;

public class PreviousPermissions extends AppCompatActivity {

    private int listPosition;
    private String message;
    private User user;
    private WGServerProxy proxy;
    private SharedValues sharedValues;
    private ArrayList<String> listProcessedString = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_permissions);
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //Get server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        getPendingPermissions();
    }


    private void getPendingPermissions() {
        Call<List<PermissionRequest>> caller = proxy.getPermissionFoUserPending(user.getId(), PENDING);
        ProxyBuilder.callProxy(PreviousPermissions.this, caller, returnedPermissions -> permissionsResponse(returnedPermissions));
    }

    //server response
    private void permissionsResponse(List<PermissionRequest> returnedPermissions) {
        listProcessedString = new ArrayList<>();
        for (PermissionRequest request : returnedPermissions) {

            if(request.getStatus()!=PENDING)
            {
                listProcessedString.add(request.getMessage() + request.getStatus().toString());
            }


        }
        sharedValues.setRequests(returnedPermissions);
        populateList(listProcessedString);

    }

    public void populateList(ArrayList<String> info){

        ListView list = (ListView) findViewById(R.id.processedRequests);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.permissions, info);
        list.setAdapter(adapter);

    }


}