package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Proxy;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
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






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), getIntent().getStringExtra("Token"));

        user = User.getInstance();

        Log.i(TAG, ""+user.toString());

        groupList = GroupCollection.getInstance();
        setUpMapButton();

//        setupTestButton();
//        setupTestGroupButton();

    }

    private void setupTestGroupButton() {
        Button button = findViewById(R.id.groupTestBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //createGroup();
                //getGroup(Long.valueOf(21));
                //
//                deleteGroup();
                getGroups();
                //getGroupMembers();
                //addNewMember();
                //updateGroup(Long.valueOf(18));
                //deleteGroupMember();

            }
        });
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

    private void getGroups() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedGroups ->groupsResponse(returnedGroups));
    }

    private void groupsResponse(List<Group> returnedGroups) {

        Log.w(TAG, "All Groups:");
        for (Group group : returnedGroups) {
//            Log.w(TAG, "    Group: " + group.toString());
            groupList.addGroup(group);

        }
//        populateList();

    }

    private void getGroup(Long groupId) {
        Call<Group> caller = proxy.getGroupById(groupId);
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedGroup -> groupResponse(returnedGroup));
    }

    private void createGroup() {
        Group group = new Group();
        group.setGroupDescription("Test 2");
        group.setLeader(user);
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
//                R.layout.user_list,      //Layout used
//                groups);               //Groups/Users displayed
//
//        //Configure the list view
//        ListView list = findViewById(R.id.testListView);
//        list.setAdapter(adapter);
//    }

    private void setupTestButton() {
        Button button = findViewById(R.id.monitorUserBtn);
        button.setOnClickListener(view -> {

            Call<List<User>> caller = proxy.getUsers();
            ProxyBuilder.callProxy(mainMenu.this,caller, returnedUsers -> response(returnedUsers));

            //addUserToMonitor("anothertest@gmail.com");
            stopMonitoringUser("test@sfu.ca");





        });

    }

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

    public static Intent makeIntent(Context context, String token) {
        Intent intent = new Intent(context, mainMenu.class);
        intent.putExtra("Token", token);
        return intent;
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
}



