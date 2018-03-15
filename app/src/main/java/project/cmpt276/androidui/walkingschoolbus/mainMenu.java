package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class mainMenu extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    //    private long userId = 0;
    private User user;
    private GroupCollection groupList;
    private SharedValues sharedValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        user = User.getInstance();


        Log.i(TAG, ""+user.toString());

        groupList = GroupCollection.getInstance();

        setUpMapButton();
        setUpWhoIMonitorBtn();
        setUpWhoMonitorsMeBtn();
        setUpLogoutBtn();
        setUpManageGroupsBtn();

    }



    private void deleteGroupMember() {
        Call<Void> caller = proxy.deleteGroupMember(Long.valueOf(19), user.getId());
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedNothing -> response(returnedNothing));
    }

    private void addNewMember() {
        Call<List<User>> caller = proxy.addNewMember(Long.valueOf(19), user);
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedUsers -> response(returnedUsers));
    }

    private void getGroupMembers() {
        Call<List<User>> caller = proxy.getGroupMembers(Long.valueOf(19));
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedUsers -> response(returnedUsers));
    }

    private void deleteGroup() {
        Call<Void> caller = proxy.deleteGroup(Long.valueOf(18));
        ProxyBuilder.callProxy(mainMenu.this, caller,returnedNothing -> response(returnedNothing));


    }

    private void updateGroup(Long groupId) {

        Group group = new Group();
        group.setLeader(user);
        group.addRouteLatArray(Double.valueOf(49.15523));
        group.addRouteLngArray(Double.valueOf(157.25322));

        Call<Group> caller = proxy.updateGroup(groupId, group);
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedGroup -> groupResponse(returnedGroup));
    }



    private void getGroup(Long groupId) {
        Call<Group> caller = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedGroup -> groupResponse(returnedGroup));
    }

    private void createGroup() {
        Group group = new Group();
        group.setId(-1);
        group.setGroupDescription("Test 6");
        group.setLeader(user);
        //group.setRouteLatArray(Arrays.asList(Double.valueOf(115.2344), Double.valueOf(225.3432)));
        //group.setRouteLngArray(Arrays.asList(Double.valueOf(142.6621), Double.valueOf(265.3455)));
        Call<Group> caller = proxy.createGroup(group);
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedGroup -> groupResponse(returnedGroup));
    }

    private void groupResponse(Group returnedGroup){
        Log.w(TAG, "Server replied with Group: " + returnedGroup.toString());
    }


//    private void populateList() {
//        //Create the list
//        String[] groups = groupList.getGroupDetails();
//        //Build Adapter
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this,           //Context for the activity
//                R.layout.monitors_users_list,      //Layout used
//                groups);               //Groups/Users displayed
//
//        //Configure the list view
//        ListView list = findViewById(R.id.testListView);
//        list.setAdapter(adapter);
//    }


    private void stopMonitoringUser(String email) {
        Call<User> userCaller = proxy.getUserByEmail(email);
        ProxyBuilder.callProxy(mainMenu.this, userCaller, returnedUser -> stopMonitoringResponse(returnedUser));

    }

    private void stopMonitoringResponse(User returnedUser) {
        Call<Void> caller = proxy.stopMonitoringUser(user.getId(), returnedUser.getId());
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedNothing -> response(returnedNothing));
    }

    private void addUserToMonitor(String email) {
        Call<User> userCaller = proxy.getUserByEmail(email);
        ProxyBuilder.callProxy(mainMenu.this, userCaller, returnedUser -> addUserToMonitorResponse(returnedUser));
    }

    private void response(List<User> returnedUsers) {
        String[] users = new String[returnedUsers.size()];
        Log.w(TAG, "All Users:");
        for (int i = 0; i < returnedUsers.size(); i++ ) {
            Log.w(TAG, "    User: " + returnedUsers.get(i).toString());
            users[i] = returnedUsers.get(i).toString();

        }
//        populateList(users);
    }

    private void addUserToMonitorResponse(User returnedUser){
        Log.w(TAG, "server replied with User: " + returnedUser.toString());
        Call<List<User>> caller = proxy.addUserToMonitor(user.getId(), returnedUser);
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedUsers -> response(returnedUsers));


    }

    private void response(Void returnedNothing){
        Log.w(TAG, "Server replied with nothing");
    }



    private void setUpMapButton()
    {
        Button button = (Button) findViewById(R.id.goToMap);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainMenu.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }




    private void setUpWhoIMonitorBtn() {
        Button button = findViewById(R.id.whoIMonitorBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = WhoIMonitor.makeIntent(mainMenu.this);
                startActivity(intent);

            }
        });
    }

    private void setUpWhoMonitorsMeBtn() {
        Button button = findViewById(R.id.whoMonitorsMeBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = WhoMonitorsMe.makeIntent(mainMenu.this);
                startActivity(intent);
            }
        });
    }

    private  void  setUpManageGroupsBtn(){
        Button button = findViewById(R.id.manageGroupsBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ManageGroups.makeIntent(mainMenu.this);
                startActivity(intent);
            }
        });
    }

    private void setUpLogoutBtn() {
        Button button = findViewById(R.id.logoutBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedValues.setToken(null);
                finish();
            }
        });

    }


    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, mainMenu.class);

        return intent;
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}



