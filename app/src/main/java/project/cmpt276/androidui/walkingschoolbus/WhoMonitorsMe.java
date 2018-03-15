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

import java.util.List;

import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

/* Activity that list all users being monitored by the current users
 - User can remove anyone from the list of users who monitors them.
 - User can add another user to the set of users who monitor them (add their parent).
    The parent account must already exist and is identified by its email address.
 */

public class WhoMonitorsMe extends AppCompatActivity {
    private WGServerProxy proxy;
    private User user;
    private SharedValues sharedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_monitors_me);
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());

        //Initial population of current user list
        getMonitoredByUsers(user);

        setUpAddButton();
        setUpReturnButton();
        setUpRemoveMonitoredByUserButton();
    }
    private void getMonitoredByUsers(User currUser) {
        Call<List<User>> caller = proxy.getUsersMonitoredBy(currUser.getId());
        ProxyBuilder.callProxy(WhoMonitorsMe.this, caller, returnedUsers -> monitoredByResponse(returnedUsers));
    }

    private void monitoredByResponse(List<User> returnedUsers) {
        String[] users = new String[returnedUsers.size()];
        for (int i = 0;i < returnedUsers.size(); i++){
            User monitoredByUser = returnedUsers.get(i);
            users[i] = "Name: " + monitoredByUser.getName() + "\nEmail: " + monitoredByUser.getEmail();
        }
        user.setMonitoredByUsersString(users);
        populateList(user);
    }

    private void populateList(User currUser) {
        //Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,           //Context for the activity
                R.layout.monitored_by_users_list,      //Layout used
                currUser.getMonitoredByUsersString());               //Groups/Users displayed
        //Configure the list view
        ListView list = findViewById(R.id.currentlyMonitoredByList);
        list.setAdapter(adapter);
    }

    private void setUpRemoveMonitoredByUserButton() {
        Button button = findViewById(R.id.stopBeingMonitoredBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = findViewById(R.id.stopBeingMonitoredText);
                Call<User> caller = proxy.getUserByEmail(text.getText().toString());
                ProxyBuilder.callProxy(WhoMonitorsMe.this, caller, returnedUser -> removeUserResponse(returnedUser));
            }
        });
    }

    private void removeUserResponse(User returnedUser) {
        Call<Void> caller = proxy.stopBeingMonitoredByUser(user.getId(), returnedUser.getId());
        ProxyBuilder.callProxy(WhoMonitorsMe.this, caller, returnedNothing -> voidResponse(returnedNothing));
    }

    private void voidResponse(Void returnedNothing) {
        Log.w("voidResponse", "Server returned nothing as expected.");
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(WhoMonitorsMe.this, caller, returnedUser -> userResponse(returnedUser));
    }

    private void userResponse(User returnedUser) {
        User.setUser(returnedUser);
        user = User.getInstance();
        getMonitoredByUsers(user);
    }

    private void setUpAddButton() {
        Button button = findViewById(R.id.addUserToMonitorMeBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = findViewById(R.id.addSomeoneToMonitorMeEdit);
                Call<User> caller = proxy.getUserByEmail(email.getText().toString());
                ProxyBuilder.callProxy(WhoMonitorsMe.this, caller, returnedUser -> addUserResponse(returnedUser));
            }
        });
    }

    private void addUserResponse(User returnedUser) {
        Call<List<User>> caller = proxy.addUserMonitoredBy(user.getId(), returnedUser);
        ProxyBuilder.callProxy(WhoMonitorsMe.this, caller, returnedUsers -> usersResponse(returnedUsers));
    }

    private void usersResponse(List<User> returnedUsers) {
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(WhoMonitorsMe.this, caller, returnedUser -> userResponse(returnedUser));
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

    public static Intent makeIntent(Context context){
        return new Intent(context, WhoMonitorsMe.class);
    }
}


