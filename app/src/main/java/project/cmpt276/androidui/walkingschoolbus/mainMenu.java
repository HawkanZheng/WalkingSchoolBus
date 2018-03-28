package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;

/*Main Menu
Access to :
            -Map
            -Who Monitors You
            -Who You Monitor
            -Manage Your Groups
            -Logout
 */

public class mainMenu extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
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

        //Set up Main Menu views.
        setupGreeting();
        setUpMapButton();
        setUpMessagingButton();
        setUpWhoIMonitorBtn();
        setUpWhoMonitorsMeBtn();
        setUpLogoutBtn();
        setUpManageGroupsBtn();
    }

    private void setupGreeting() {
        TextView view = findViewById(R.id.greeting);
        view.setText(getString(R.string.hi) + " " + user.getName() + ". " + getString(R.string.welcome_to_the_walking_school_bus_app));
    }

    private void response(List<User> returnedUsers) {
        String[] users = new String[returnedUsers.size()];
        Log.w(TAG, "All Users:");
        for (int i = 0; i < returnedUsers.size(); i++ ) {
            Log.w(TAG, "    User: " + returnedUsers.get(i).toString());
            users[i] = returnedUsers.get(i).toString();
        }
    }


    private void setUpMapButton()
    {
        Button button = findViewById(R.id.goToMap);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainMenu.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpMessagingButton()
    {
        Button button = findViewById(R.id.btnMessagingActivity);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainMenu.this, MessagingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpWhoIMonitorBtn(){
        Button button = findViewById(R.id.whoIMonitorBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = WhoIMonitor.makeIntent(mainMenu.this);
                startActivity(intent);
            }
        });
    }

    private void setUpWhoMonitorsMeBtn(){
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

    private void setUpLogoutBtn(){
        Button button = findViewById(R.id.logoutBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedValues.setToken(null);

                // Trash saved values
                SharedPreferences prefs = getSharedPreferences("user info", MODE_PRIVATE) ;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user name", "");
                editor.putString("password", "");
                editor.apply();


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



