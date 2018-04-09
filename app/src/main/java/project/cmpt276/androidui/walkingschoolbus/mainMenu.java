package project.cmpt276.androidui.walkingschoolbus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import project.cmpt276.model.walkingschoolbus.GamificationCollection;
import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.Message;
import project.cmpt276.model.walkingschoolbus.RewardAvatar;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;


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

    private boolean isEmergencyVisible = false;
    private final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        user = User.getInstance();
        Log.i(TAG, ""+user.toString());
        groupList = GroupCollection.getInstance();


        // Action bar setup
        actionBarSetup();
        setupNavigationDrawer();

        //Set up Main Menu views.
        setupLeaderboardBtn();
        setUserAvatar();
        setUpMapButton();
        setUpMessagingButton();
        setupEmergencyBtn();
        setupEmergencySendBtn();
        setUpShopBtn();
        getUnreadMessages();
        setUpPermissionsButton();
    }

    private void setupLeaderboardBtn() {
        Button btn = findViewById(R.id.btnLeaderboards);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainMenu.this, LeaderBoardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void actionBarSetup() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();

        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }


    private void setupNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        user = User.getInstance();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);

        TextView email = v.findViewById(R.id.txtNavEmailHeader);
        TextView name = v.findViewById(R.id.txtNavNameHeader);

        name.setText(user.getName());
        email.setText(user.getEmail());

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        Intent i;
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch (menuItem.getItemId()){
                            case R.id.nav_Monitors:
                                i = WhoMonitorsMe.makeIntent(mainMenu.this);
                                startActivity(i);
                                return true;

                            case R.id.nav_mygroups:
                                i = ManageGroups.makeIntent(mainMenu.this);
                                startActivity(i);
                                return true;

                            case R.id.nav_parentdash:
                                i = ParentsDashboardActivity.makeIntent(mainMenu.this);
                                startActivity(i);
                                return true;
                        }
                        return true;
                    }
                });
    }

    //refresh name in greeting after it has been modified in the edit user activity and avatar.
    @Override
    protected void onResume() {
        super.onResume();
        getUnreadMessages();
        setUserAvatar();
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
                if (ActivityCompat.checkSelfPermission(mainMenu.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(mainMenu.this, MapsActivity.class);
                        startActivity(intent);

                }else {
                    //Prompt user for access to their device's location.
                    ActivityCompat.requestPermissions(mainMenu.this, perms, 076);
                }
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


    private void logoutCall(){
        sharedValues.setToken(null);

        // Trash saved values
        SharedPreferences prefs = getSharedPreferences("user info", MODE_PRIVATE) ;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user name", "");
        editor.putString("password", "");
        editor.apply();

        Intent intent = new Intent(mainMenu.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(mainMenu.this, "Settings not yet supported", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_edit_info:
                // start the edit info activity
                Intent intent = new Intent(mainMenu.this, editUserInfo.class);
                startActivity(intent);
                return true;

            case R.id.action_logout:
                logoutCall();
                return true;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUserAvatar(){
        ImageView iv = findViewById(R.id.userAvatar);
        iv.setBackground(sharedValues.getUserAvatar());
    }


    private void setUpShopBtn(){
        Button btn = findViewById(R.id.storeBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = PointsStore.makeIntent(mainMenu.this);
                startActivity(intent);
            }
        });
    }

    private String getEmergencyMessage(){
        EditText edit = findViewById(R.id.edtEmergencyMessage);
        return edit.getText().toString();
    }

    private void setupEmergencyBtn() {
        Button emergencyBtn = findViewById(R.id.btnPanic);
        emergencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button sendEmergencyBtn = findViewById(R.id.btnSendEmergencyText);
                EditText messageEdt = findViewById(R.id.edtEmergencyMessage);

                if(!isEmergencyVisible){
                    sendEmergencyBtn.setVisibility(View.VISIBLE);
                    messageEdt.setVisibility(View.VISIBLE);

                    messageEdt.getText().clear();

                    isEmergencyVisible = true;
                }
                else{
                    sendEmergencyBtn.setVisibility(View.INVISIBLE);
                    messageEdt.setVisibility(View.INVISIBLE);

                    // Hide Keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    isEmergencyVisible = false;

                }
            }
        });
    }

    private void setupEmergencySendBtn() {
        Button emergencySendBtn = findViewById(R.id.btnSendEmergencyText);
        emergencySendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //refresh user
                user = User.getInstance();
                //Emergency message Server Calls
                if (!getEmergencyMessage().isEmpty()){
                    Message message = new Message();
                    message.setText(getEmergencyMessage());
                    message.setEmergency(true);
                    //if user is part of at least one group, sent to group leader, group members and parents
                    if (!user.getMemberOfGroups().isEmpty()) {

                        for (Group group : user.getMemberOfGroups()) {
                            Call<Message> caller = proxy.groupMessage(group.getId(), message);
                            ProxyBuilder.callProxy(mainMenu.this, caller, returnedMessage -> emergencyResponse(returnedMessage));
                        }
                    }
                    //if user is not part of any group, send to parents
                    else {

                        Call<Message> caller = proxy.parentMessage(user.getId(), message);
                        ProxyBuilder.callProxy(mainMenu.this, caller, returnedMessage -> emergencyResponse(returnedMessage));
                    }
                    Toast.makeText(mainMenu.this, getString(R.string.emergency_sent) + getEmergencyMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mainMenu.this, R.string.emergency_notext, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
//Emergency message server response
    private void emergencyResponse(Message returnedMessage) {
        Log.i("Emergency Message", "Emergency message sent");
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, mainMenu.class);
        return intent;
    }

    //server call that gets all the unread messages
    private void getUnreadMessages(){
        Button messagesBtn = findViewById(R.id.btnMessagingActivity);
        messagesBtn.setText("Messages (" + sharedValues.getMessagesUnread() + ")");
        //Timer to refresh the UI for messages notification.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Pull from the server every minute
                Call<List<Message>> caller = proxy.getMessagesToUserUnread(user.getId(), "unread");
                ProxyBuilder.callProxy(mainMenu.this, caller, returnedMessages -> messagesResponse(returnedMessages));
            }
        }, 0,60000);

    }

    private void messagesResponse(List<Message> returnedMessages) {
        //number of unread messages
        Button messagesBtn = findViewById(R.id.btnMessagingActivity);
        int numNewMessages = returnedMessages.size();
        sharedValues.storeMessagesUnread(numNewMessages);
        messagesBtn.setText("Messages (" + sharedValues.getMessagesUnread() + ")");
        Log.i("MessagesBtn", "Refreshing button");
    }


    private void setUpPermissionsButton()
    {
        Button button = findViewById(R.id.getPermission);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainMenu.this, Permissions.class);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
