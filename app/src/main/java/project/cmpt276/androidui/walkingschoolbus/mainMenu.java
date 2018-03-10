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

import java.util.List;

import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class mainMenu extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private long userId = 0;
    private User user;






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        user = User.getInstance();

        Log.i(TAG, ""+user.toString());


        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), getIntent().getStringExtra("Token"));

        setupTestButton();

    }

    private void populateList(String[] users) {
        //Create the list of pot

        //Build Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,           //Context for the activity
                R.layout.user_list,      //Layout used
                users);               //Pots displayed

        //Configure the list view
        ListView list = findViewById(R.id.testListView);
        list.setAdapter(adapter);
    }

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
        ProxyBuilder.callProxy(mainMenu.this, userCaller, returnedUser -> response(returnedUser));
    }

    private void response(List<User> returnedUsers) {
        String[] users = new String[returnedUsers.size()];
        Log.w(TAG, "All Users:");
        for (int i = 0; i < returnedUsers.size(); i++ ) {
            Log.w(TAG, "    User: " + returnedUsers.get(i).toString());
            users[i] = returnedUsers.get(i).toString();

        }
        populateList(users);
    }

    private void response(User returnedUser){
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
}

