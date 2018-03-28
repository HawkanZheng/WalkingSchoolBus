package project.cmpt276.androidui.walkingschoolbus;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.ParentDashDataCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class ParentDashUserInfoActivity extends AppCompatActivity {

    private WGServerProxy proxy;
    private SharedValues sharedValues;
    private User user;
    private ArrayList<String> users = new ArrayList<>();


    private ParentDashDataCollection parentData = ParentDashDataCollection.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dash_user_info);
        //Get instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //get proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());

        setupActionBarBack();
        //get member users and populate list
        getUsers();
        setListCallback();
    }

    private void getUsers() {
        Call<List<User>> caller = proxy.getGroupMembers(sharedValues.getGroup().getId());
        ProxyBuilder.callProxy(ParentDashUserInfoActivity.this, caller, returnedUsers -> usersResponse(returnedUsers));

    }

    private void usersResponse(List<User> returnedUsers) {
        sharedValues.setUserList(returnedUsers);
        for(User member : returnedUsers){
            users.add(member.toNameAndEmailString());
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

    private void populateList() {

        // Build adapter and show the items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.parent_dash_user_info_layout, users);
        ListView list = (ListView) findViewById(R.id.lstUserInGroupSelected);
        list.setAdapter(adapter);
    }

    private void setListCallback() {
        ListView list = findViewById(R.id.lstUserInGroupSelected);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //save the user in the singleton, in order to view the correct users info
                User userSelected = sharedValues.getUserList().get(i);
                sharedValues.setUser(userSelected);
                setupDialog();

            }
        });
    }

    private void setupDialog(){
        ParentUserInfoFragment dialog = new ParentUserInfoFragment();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        dialog.show(manager,"ParentInfoDialog");

        Log.i("Dialog", "Opened new frag");
    }

}
