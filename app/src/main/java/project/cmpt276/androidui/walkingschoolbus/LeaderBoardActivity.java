package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LeaderBoardActivity extends AppCompatActivity {

    ArrayList<String> leaderBoardData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        setupActionBarBack();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.leader_board_list_layout, leaderBoardData);
        ListView list = (ListView) findViewById(R.id.lstLeaderBoardList);
        list.setAdapter(adapter);
    }
}
