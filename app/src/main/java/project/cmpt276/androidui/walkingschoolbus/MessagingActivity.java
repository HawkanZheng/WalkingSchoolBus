package project.cmpt276.androidui.walkingschoolbus;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {

    ArrayList<String> unreadMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // Generate Junk Data for testing
        testFoo();

        // Setup Buttons
        setupActionBarBack();
        setupOldMessagesBtn();
        setupSendAMessageBtn();

        // Configure the List
        populateList();
        listClickCallback();
    }

    // Generate Junk Data for testing
    private void testFoo() {
        for(int i = 0;i<10;i++){
            unreadMessages.add("" + i);
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

    private void setupOldMessagesBtn() {
        Button btn = (Button) findViewById(R.id.btnViewOlderMessages);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagingActivity.this, OldMessagesActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setupSendAMessageBtn() {
        Button btn = (Button) findViewById(R.id.btnSendMessage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagingActivity.this, SendingMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    // Displays the unread Messages in the List View
    // Should be used in the callback from the server call
    private void populateList() {

        // Get the List
        ListView list = (ListView) findViewById(R.id.lstUnreadMessages);

        // Build the Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.unread_messages_layout,unreadMessages);

        list.setAdapter(adapter);

    }

    private void listClickCallback() {
        ListView list = (ListView) findViewById(R.id.lstUnreadMessages);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                // TODO: Clicked messages are considered 'Read'
                parent.getChildAt(position).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            }
        });
    }
}
