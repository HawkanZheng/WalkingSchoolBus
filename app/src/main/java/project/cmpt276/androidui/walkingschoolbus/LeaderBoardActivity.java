package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class LeaderBoardActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    private SharedValues sharedValues;
    private User user;

    ArrayList<String> leaderBoardData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        //Get instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //get proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());

        setupActionBarBack();
        getAllUsers();
    }

    //Get users from server
    private void getAllUsers() {
        Call<List<User>> caller = proxy.getUsers();
        ProxyBuilder.callProxy(LeaderBoardActivity.this, caller, returnedUsers -> response(returnedUsers));
    }

    //response to user server call
    private void response(List<User> returnedUsers) {
        leaderBoardData = new ArrayList<>();
        String info;
        int i = 1;

        for(User curr : returnedUsers){
            info = i + ". Name: " + user.getName() +
                    "\n   Current Points: " + user.getCurrentPoints() +
                    "\n   Total Points Earned: " + user.getTotalPointsEarned();

        }



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

    private void populateList() {
        // Build adapter and show the items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.leader_board_list_layout, leaderBoardData);
        ListView list = (ListView) findViewById(R.id.lstLeaderBoardList);
        list.setAdapter(adapter);
    }
}
