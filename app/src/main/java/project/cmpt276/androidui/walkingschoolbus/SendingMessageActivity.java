package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.Message;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class SendingMessageActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    private SharedValues sharedValues;
    private User user;

    public SendingMessageActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_message);
        //Get instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //get proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());


        setupActionBarBack();
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

//Send message to chosen group
    public void sendMessageToGroup(Group group, String text){
        Message message = new Message();
        message.setText(text);
        message.setEmergency(false);
        Call<Message> caller = proxy.groupMessage(group.getId(), message);
        ProxyBuilder.callProxy(SendingMessageActivity.this, caller, returnedMessage -> messageResponse(returnedMessage) );


    }

    private void messageResponse(Message returnedMessage) {
        Log.i("Returned Message", "Message returned " + returnedMessage.getText() + "\n");
    }

    //Send message to users parents
    public void sendMessageToParents(String text){
        Message message = new Message();
        message.setText(text);
        message.setEmergency(false);
        Call<Message> caller = proxy.parentMessage(user.getId(), message);
        ProxyBuilder.callProxy(SendingMessageActivity.this, caller, returnedMessage -> messageResponse(returnedMessage));

    }
}