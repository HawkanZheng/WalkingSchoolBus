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

public class GroupsLeaderActivity extends AppCompatActivity {

    private ArrayList<String> groupsILead = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_leader);

        // TODO: Remove when server calls to populate list are added
        groupsILead.add("TMP");

        setupActionBarBack();
        populateList();

        setupListCallback();
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
        // TODO: populate with server pulled groups
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.groups_i_lead_layout, groupsILead);
        ListView list = (ListView) findViewById(R.id.lstGroupsILead);
        list.setAdapter(adapter);
    }


    private void setupListCallback() {
        ListView list = findViewById(R.id.lstGroupsILead);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: extract the group selected and then populate the UserInfo accordingly

                Intent intent = new Intent(GroupsLeaderActivity.this, ParentDashUserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

}
