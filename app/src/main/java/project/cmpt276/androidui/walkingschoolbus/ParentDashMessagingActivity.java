package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ParentDashMessagingActivity extends AppCompatActivity {

    private ArrayList<String> nonEmergencyMessages = new ArrayList<>();
    private ArrayList<String> emergencyMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dash_messaging);

        setupActionBarBack();
        populateNonEmergencyList();
        populateEmergencyList();
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

    private void populateNonEmergencyList() {
        // Build adapter and show the items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.parent_dash_messaging_nonemergency_layout, nonEmergencyMessages);
        ListView list = (ListView) findViewById(R.id.lstParentMessageingNonEmergency);
        list.setAdapter(adapter);
    }

    private void populateEmergencyList() {
        // Build adapter and show the items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.parent_dash_emergency_messages_layout, emergencyMessages);
        ListView list = (ListView) findViewById(R.id.lstEmergencyMessages);
        list.setAdapter(adapter);
    }
}
