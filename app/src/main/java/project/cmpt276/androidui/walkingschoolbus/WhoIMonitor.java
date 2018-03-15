package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

/* Activity that list all users being monitored by the current users
 - User can remove anyone from the list of users whom they monitor
 - User can add another user to the set of users they monitor (add their child).
    The child account must already exist and is identified by its email address.
 */

public class WhoIMonitor extends AppCompatActivity {
    private WGServerProxy proxy;
    private User user;
    private SharedValues sharedValues;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_i_monitor);
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());

        //Initial population of current user list
        getMonitorsUsers(user);

        setUpAddButton();
        setUpReturnButton();
        setUpRemoveMonitoredUserButton();
        registerListClickCallBack();



    }



    private void getMonitorsUsers(User currUser) {
        //User user = User.getInstance();
        Call<List<User>> caller = proxy.getUsersMonitered(currUser.getId());
        ProxyBuilder.callProxy(WhoIMonitor.this, caller, returnedUsers -> monitorsResponse(returnedUsers));
    }

    private void monitorsResponse(List<User> returnedUsers) {
        Log.i("Monitoring", returnedUsers.toString());
        sharedValues.setUserList(returnedUsers);
        String[] users = new String[returnedUsers.size()];
        for (int i = 0;i < returnedUsers.size(); i++){
            User monitorUser = returnedUsers.get(i);
            users[i] = "Name: " + monitorUser.getName() + "\nEmail: " + monitorUser.getEmail();
        }
        user.setMonitorsUsersString(users);
        populateList(user);
    }

    private void populateList(User currUser) {

        //Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,           //Context for the activity
                R.layout.monitors_users_list,      //Layout used
                currUser.getMonitorsUsersString());               //Groups/Users displayed
        //Configure the list view
        ListView list = findViewById(R.id.currentlyMonitoringList);
        list.setAdapter(adapter);
    }

    private void setUpRemoveMonitoredUserButton() {
        Button button = findViewById(R.id.stopMonitoringBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.stopMonitoringText);
                Call<User> caller = proxy.getUserByEmail(text.getText().toString());
                ProxyBuilder.callProxy(WhoIMonitor.this, caller, returnedUser -> removeUserResponse(returnedUser));
            }
        });
    }


    private void removeUserResponse(User returnedUser) {
        Call<Void> caller = proxy.stopMonitoringUser(user.getId(), returnedUser.getId());
        ProxyBuilder.callProxy(WhoIMonitor.this, caller, returnedNothing -> voidResponse(returnedNothing));

    }

    private void voidResponse(Void returnedNothing) {
        Log.w("voidResponse", "Server returned nothing as expected.");
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(WhoIMonitor.this, caller, returnedUser -> userResponse(returnedUser));
    }

    private void userResponse(User returnedUser) {
        User.setUser(returnedUser);
        getMonitorsUsers(returnedUser);
    }

    private void setUpAddButton() {
        Button button = findViewById(R.id.addUserToMonitorBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.addSomeoneToMonitorEdit);
                Call<User> caller = proxy.getUserByEmail(email.getText().toString());
                ProxyBuilder.callProxy(WhoIMonitor.this, caller, returnedUser -> addUserResponse(returnedUser));
            }
        });


    }

    private void addUserResponse(User returnedUser) {
        Call<List<User>> caller = proxy.addUserToMonitor(user.getId(), returnedUser);
        ProxyBuilder.callProxy(WhoIMonitor.this, caller, returnedUsers -> usersResponse(returnedUsers));
    }

    private void usersResponse(List<User> returnedUsers) {
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(WhoIMonitor.this, caller, returnedUser -> userResponse(returnedUser));
    }

    private void setUpReturnButton() {
        Button button = findViewById(R.id.returnMainMenuBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerListClickCallBack() {
        ListView list = findViewById(R.id.currentlyMonitoringList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                user = User.getInstance();
                Log.i("Monitoring:",sharedValues.getUserList().toString() );
                sharedValues.setUser(sharedValues.getUserList().get(position));
                Intent intent = monitoredUserGroupsActivity.makeIntent(WhoIMonitor.this);
                startActivity(intent);
            }
        });

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, WhoIMonitor.class);

    }
}
