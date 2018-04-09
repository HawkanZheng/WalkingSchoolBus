package project.cmpt276.androidui.walkingschoolbus;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.PermissionRequest;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import static project.cmpt276.server.walkingschoolbus.WGServerProxy.PermissionStatus.PENDING;

public class Permissions extends AppCompatActivity /*implements DialogInterface.OnDismissListener */{


    private ArrayList<String> listPendingString = new ArrayList<>();
    private ArrayList<PermissionRequest> listPending = new ArrayList<>();

    private int listPosition;
    private String message;
    private User user;
    private WGServerProxy proxy;
    private SharedValues sharedValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //Get server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        setContentView(R.layout.activity_permissions);

        getPendingPermissions();
        setupActionBarBack();
        listClickCallback();
        goToPreviousRequestsBtn();


    }



    //get permissions from server
    private void getPendingPermissions() {
        Call<List<PermissionRequest>> caller = proxy.getPermissionFoUserPending(user.getId(), PENDING);
        ProxyBuilder.callProxy(Permissions.this, caller, returnedPermissions -> permissionsResponse(returnedPermissions));
    }



    //server response
    public void permissionsResponse(List<PermissionRequest> returnedPermissions) {
        listPendingString = new ArrayList<>();
        for(PermissionRequest request : returnedPermissions){
            listPendingString.add(request.getMessage());
        }
        sharedValues.setRequests(returnedPermissions);
        populateList(listPendingString);

    }


    //for registering a click on an item in the list
    private void listClickCallback() {
        ListView list = (ListView) findViewById(R.id.listOfPendingPermissions);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                listPosition = position;
                message = listPendingString.get(position);
                FragmentManager manager = getFragmentManager();
                PermissionAlertFragment dialog = new PermissionAlertFragment();
                dialog.show(manager, "hello there");

            }
        });
    }


    //adds values to the array list to display in the list view
    public void populateList(ArrayList<String> info){

        ListView list = (ListView) findViewById(R.id.listOfPendingPermissions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.permissions, info);
        list.setAdapter(adapter);

    }

    // Add a Back button on the Action Bar
    private void setupActionBarBack() {
        // set the button to be visible
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // On back button click, finish the activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }



    public int getPosition()
    {
        return listPosition;
    }


    public String getMessage()
    {
        return message;
    }


    //set up button to go to all previous requests
    private void goToPreviousRequestsBtn()
    {
        Button button = findViewById(R.id.goToPreviousRequests);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Permissions.this, PreviousPermissions.class);

                startActivity(intent);

            }
        });
    }







}


