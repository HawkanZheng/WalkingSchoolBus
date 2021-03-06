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

public class OldMessagesActivity extends AppCompatActivity {
    User user;
    SharedValues sharedValues;
    WGServerProxy proxy;


    ArrayList<String> readMessages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_messages);
        //Get class instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());

        setupActionBarBack();
        //get read messages from server
        getReadMessagesForUser();

    }

    //get all read messages from server
    private void getReadMessagesForUser() {
        Call<List<Message>> caller = proxy.getMessagesToUserRead(user.getId(), "read");
        ProxyBuilder.callProxy(OldMessagesActivity.this, caller, returnedMessages -> messagesResponse(returnedMessages));
    }


    private void messagesResponse(List<Message> returnedMessages) {
        //add string messages to readMessages list
        for(Message message: returnedMessages){
            readMessages.add(message.messageToString());
        }

        // List Config
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.older_messages_layout, readMessages);
        ListView list = (ListView) findViewById(R.id.lstOldMessagesList);
        list.setAdapter(adapter);

    }
}
