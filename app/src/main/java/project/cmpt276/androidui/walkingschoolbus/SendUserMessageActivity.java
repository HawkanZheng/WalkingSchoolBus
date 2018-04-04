package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.Message;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class SendUserMessageActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    private SharedValues sharedValues;
    private User user;
    private Group groupSelected = new Group();

    private ArrayList<String> groups = new ArrayList<>();
    private View lastViewClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_user_message);
        //Get instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();

        //get proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());

        setupActionBarBack();

        setupSendGroupLeaderButton();
        setupSendParentsButton();
        populateList();
        listClickCallback();

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

    private String getMessage() {
        EditText edt = findViewById(R.id.edtUserSendMessageInput);
        return edt.getText().toString();
    }

    private void setupSendGroupLeaderButton() {
        Button btn = findViewById(R.id.btnUserSendGroupLeader);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getMessage();

                //Server call
                sendMessageToGroup(message);


            }
        });
    }

    private void setupSendParentsButton() {
        Button btn = findViewById(R.id.btnUserSendParents);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             String message = getMessage();
                //Server call
                sendMessageToParents(message);


            }
        });
    }


    private void populateList() {
        //make group list of strings
        for(Group group : user.getMemberOfGroups()){
            groups.add(group.groupToListString());
        }
        // Build adapter and show the items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_group_user_messaging_layout, groups);
        ListView list = (ListView) findViewById(R.id.lstUserGroupsSendable);
        list.setAdapter(adapter);
    }

    private void listClickCallback() {
        ListView list = (ListView) findViewById(R.id.lstUserGroupsSendable);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                if(viewClicked != lastViewClicked){
                    parent.getChildAt(position).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));

                    if(lastViewClicked != null){
                        lastViewClicked.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                    lastViewClicked = viewClicked;

                    //Extract group selected from onclick
                    groupSelected = user.getMemberOfGroups().get(position);

                }

                Toast.makeText(SendUserMessageActivity.this, "Clicked " + position,Toast.LENGTH_SHORT).show();
            }
        });
    }
//Send message to user's parents
    public void sendMessageToParents(String text){
        if(!text.isEmpty()) {
            Message message = new Message();
            message.setText(text);
            message.setEmergency(false);
            Call<Message> caller = proxy.parentMessage(user.getId(), message);
            ProxyBuilder.callProxy(SendUserMessageActivity.this, caller, returnedMessage -> messageResponse(returnedMessage));
        }
        else{
            Toast.makeText(SendUserMessageActivity.this, R.string.send_userMessage_activity_message_body, Toast.LENGTH_SHORT).show();
        }
    }

    //Send message to chosen group
    public void sendMessageToGroup(String text){
        if(groupSelected != null && !text.isEmpty()) {
            Message message = new Message();
            message.setText(text);
            message.setEmergency(false);
            Call<Message> caller = proxy.groupMessage(groupSelected.getId(), message);
            ProxyBuilder.callProxy(SendUserMessageActivity.this, caller, returnedMessage -> messageResponse(returnedMessage));
        }
        else if(groupSelected.getId() == 0){
            Toast.makeText(SendUserMessageActivity.this, "You Must Select A Group.", Toast.LENGTH_SHORT).show();
    }
        else if(text.isEmpty()){
            Toast.makeText(SendUserMessageActivity.this, R.string.send_userMessage_activity_message_body, Toast.LENGTH_SHORT).show();
    }

    }

    //response to message server call
    private void messageResponse(Message returnedMessage) {
        Log.i("Returned Message", "Message returned " + returnedMessage.getText() + "\n");
        Toast.makeText(SendUserMessageActivity.this,getMessage(),Toast.LENGTH_SHORT).show();
    }
}
