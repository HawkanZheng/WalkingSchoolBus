package project.cmpt276.androidui.walkingschoolbus;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.PermissionRequest;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import static project.cmpt276.server.walkingschoolbus.WGServerProxy.PermissionStatus.PENDING;

public class Permissions extends AppCompatActivity implements DialogInterface.OnDismissListener {


    private ArrayList<String> items = new ArrayList<>();
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

    }



    //get permissions from server
    private void getPendingPermissions() {
        Call<List<PermissionRequest>> caller = proxy.getPermissionFoUserPending(user.getId(), PENDING);
        ProxyBuilder.callProxy(Permissions.this, caller, returnedPermissions -> permissionsResponse(returnedPermissions));
    }

    //server response
    private void permissionsResponse(List<PermissionRequest> returnedPermissions) {
        items = new ArrayList<>();
        for(PermissionRequest request : returnedPermissions){
            items.add(request.getMessage());
        }
        sharedValues.setRequests(returnedPermissions);
        populateList(items);

    }


    private void listClickCallback() {
        ListView list = (ListView) findViewById(R.id.listOfPendingPermissions);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                listPosition = position;
                message = items.get(position);
                FragmentManager manager = getFragmentManager();
                PermissionAlertFragment dialog = new PermissionAlertFragment();
                dialog.show(manager, "hello there");

            }
        });
    }


    private void populateList(ArrayList<String> info){

        ListView list = (ListView) findViewById(R.id.listOfPendingPermissions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.permissions, info);
        list.setAdapter(adapter);

    }

    public void showGrantedToast()
    {
        Toast.makeText(Permissions.this, "permission granted", Toast.LENGTH_SHORT).show();
    }

    public void showDeniedToast()
    {
        Toast.makeText(Permissions.this, "permission denied", Toast.LENGTH_SHORT).show();
    }


    public void deleteItem(int position)
    {
        items.remove(position);
        getPendingPermissions();
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


    //on dialog fragment dismiss refresh list
    @Override
    public void onDismiss(final DialogInterface dialog){
        Log.i("On Dismiss", "On Dismiss Refresh list");
        items = new ArrayList<>();
        for(PermissionRequest request : sharedValues.getRequests()){
            items.add(request.getMessage());
        }
        populateList(items);
    }





    private void addToList(String newInfo)
    {
        items.add(newInfo);
        populateList(items);

    }


    public int getPosition()
    {
        return listPosition;
    }

    public void grantPermission()
    {

    }

    public void denyPermission()
    {

    }


    public String getMessage()
    {
        return message;
    }


}


