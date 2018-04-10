package project.cmpt276.androidui.walkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class GroupsLeaderActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    private User user;
    private SharedValues sharedValues;

    private ArrayList<String> groupsILead = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_leader);

        //Get instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //get proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());


        setupActionBarBack();
        getGroupsLead();

        setupListCallback();
    }


    private void getGroupsLead() {
        //refresh user
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(GroupsLeaderActivity.this, caller, returnedUser -> userResponse(returnedUser));

    }

    private void userResponse(User returnedUser) {
        List<Group> groupsLead = returnedUser.getLeadsGroups();
        sharedValues.setGroupList(returnedUser.getLeadsGroups());
        for(Group group : groupsLead){
            groupsILead.add(group.groupToListString());
        }
        populateList();
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

    private void populateList(){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.groups_i_lead_layout, groupsILead);
        ListView list = (ListView) findViewById(R.id.lstGroupsILead);
        list.setAdapter(adapter);
    }


    private void setupListCallback() {
        ListView list = findViewById(R.id.lstGroupsILead);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //extract the group selected and then populate the UserInfo accordingly
                Group group = sharedValues.getGroupList().get(i);
                sharedValues.setGroup(group);

                Intent intent = new Intent(GroupsLeaderActivity.this, ParentDashUserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

}
