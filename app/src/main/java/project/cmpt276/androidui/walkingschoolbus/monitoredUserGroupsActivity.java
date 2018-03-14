package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

/*Monitored User Group Managing Activity
-Can remove monitored user from groups they are in
 */

public class monitoredUserGroupsActivity extends AppCompatActivity {
    private SharedValues sharedValues;
    private User user;
    private WGServerProxy proxy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitored_user_groups);
        //Get shared values instance
        sharedValues = SharedValues.getInstance();

        //Set the proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());


        user = sharedValues.getUser();
        //Get full user info
        getUser(user);

        setupRemoveFromGroupButton();
        setUpReturnToWhoIMonitorButton();
    }

    private void getUser(User aUser) {
        Call<User> caller = proxy.getUserById(aUser.getId());
        ProxyBuilder.callProxy(monitoredUserGroupsActivity.this, caller, returnedUser -> userResponse(returnedUser));
    }

    private void getMemberOfGroups(User currUser) {
        currUser.setMemberOfGroupsString(new ArrayList<String>());
        for(int i = 0; i < currUser.getMemberOfGroups().size(); i++){
            Group aGroup = currUser.getMemberOfGroups().get(i);
            Call<Group> caller = proxy.getGroupById(aGroup.getId());
            ProxyBuilder.callProxy(monitoredUserGroupsActivity.this, caller, returnedGroup -> groupResponse(returnedGroup));
        }
        populateList();
    }

    private void groupResponse(Group returnedGroup) {
        user.addMemberOfGroupsString(returnedGroup.groupToListString());
        Log.w("Group added", returnedGroup.toString() );

    }

    private void populateList() {
//        List<String> list = user.getMemberOfGroupsString();
//        for(int i = 0; i < user.getMemberOfGroups().size(); i++ ) {
//            Log.i("Group added", list.get(i) );
//        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,           //Context for the activity
                R.layout.users_group_list,      //Layout used
                user.getMemberOfGroupsString());               //Groups/Users displayed

        //Configure the list view
        ListView view = findViewById(R.id.usersGroupsList);
        view.setAdapter(adapter);
    }

    private void setupRemoveFromGroupButton() {
        Button button = findViewById(R.id.removeFromGroupBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = findViewById(R.id.userGroupIDEdit);
                String input = edit.getText().toString();
                if(!input.isEmpty()) {
                    long id = Long.parseLong(input);
                    Call<Void> caller = proxy.deleteGroupMember(id, user.getId());
                    ProxyBuilder.callProxy(monitoredUserGroupsActivity.this, caller, returnedNothing -> leaveGroupResponse(returnedNothing));
                }
                else{
                    Toast.makeText(monitoredUserGroupsActivity.this, "Please enter a Group ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void leaveGroupResponse(Void returnedNothing) {
        Call<User> caller = proxy.getUserById(user.getId());
        ProxyBuilder.callProxy(monitoredUserGroupsActivity.this, caller, returnedUser -> userResponse(returnedUser));
    }

    private void userResponse(User returnedUser) {
        //Set text view
        setView(returnedUser);
        sharedValues.setUser(returnedUser);
        user = sharedValues.getUser();
        getMemberOfGroups(user);
    }

    private void setUpReturnToWhoIMonitorButton(){
        Button button = findViewById(R.id.returnToMonitorsBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void setView(User theUser){
        TextView view = findViewById(R.id.userGroupsView);
        view.setText(theUser.getName() + getString(R.string.userGroupsView));

    }


    public static Intent makeIntent(Context context){
        return new Intent(context, monitoredUserGroupsActivity.class);
    }
}
