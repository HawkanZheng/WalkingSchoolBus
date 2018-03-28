package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Message;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class ParentDashMessagingActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    private SharedValues sharedValues;
    private User user;

    private ArrayList<String> nonEmergencyMessages = new ArrayList<>();
    private ArrayList<String> emergencyMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dash_messaging);

        //Get instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //Get proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());

        setupActionBarBack();
        //Get unread non emergency messages for parent
        getNonEmergencyMessages();

        //Get unread emergency messages for parent
        getEmergencyMessages();

    }

    private void getEmergencyMessages() {
        Call<List<Message>> caller = proxy.getMessagesToUserUnreadEmergency(user.getId(), "unread", true );
        ProxyBuilder.callProxy(ParentDashMessagingActivity.this, caller, returnedMessages -> EmergencyResponse(returnedMessages));
    }

    private void EmergencyResponse(List<Message> returnedMessages) {
        //convert messages to strings
        for(Message message : returnedMessages){
            emergencyMessages.add(message.messageToString());
        }
        populateEmergencyList();
    }

    private void getNonEmergencyMessages() {
        Call<List<Message>> caller = proxy.getMessagesToUserUnread(user.getId(), "unread");
        ProxyBuilder.callProxy(ParentDashMessagingActivity.this, caller, returnedMessages -> nonEmergencyResponse(returnedMessages));
    }

    private void nonEmergencyResponse(List<Message> returnedMessages) {
        //convert messages to strings
        for(Message message : returnedMessages){
            if(!message.getEmergency())
            nonEmergencyMessages.add(message.messageToString());
        }
        populateNonEmergencyList();
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
