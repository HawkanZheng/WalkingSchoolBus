package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.ParentDashDataCollection;
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

    private ParentDashDataCollection parentData = ParentDashDataCollection.getInstance();


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
        setupActionBarBack();

        listCallback();
    }

    private void getUser(User aUser) {
        Call<User> caller = proxy.getUserById(aUser.getId());
        ProxyBuilder.callProxy(monitoredUserGroupsActivity.this, caller, returnedUser -> userResponse(returnedUser));
    }

    private void getMemberOfGroups(User currUser){
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,           //Context for the activity
                R.layout.users_group_list,      //Layout used
                user.getMemberOfGroupsString());               //Groups/Users displayed
        //Configure the list view
        ListView view = findViewById(R.id.usersGroupsList);
        adapter.notifyDataSetChanged();
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
                }else{
                    Toast.makeText(monitoredUserGroupsActivity.this, "Please enter a Group ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void leaveGroupResponse(Void returnedNothing) {
        Toast.makeText(monitoredUserGroupsActivity.this, "Removed from group.", Toast.LENGTH_SHORT).show();
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

    private void setView(User theUser){
        TextView view = findViewById(R.id.userGroupsView);
        view.setText(theUser.getName() + getString(R.string.userGroupsView));

    }



    private void listCallback() {
        ListView list = findViewById(R.id.usersGroupsList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //TODO: extract group and save it to the singleton to access group info later
                //parentData.setLastGroupSelected( GROUP_ADDED_HERE );

                Intent intent = new Intent(monitoredUserGroupsActivity.this, ParentDashUserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, monitoredUserGroupsActivity.class);
    }
}
