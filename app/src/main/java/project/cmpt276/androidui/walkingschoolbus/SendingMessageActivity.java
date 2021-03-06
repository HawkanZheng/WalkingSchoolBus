package project.cmpt276.androidui.walkingschoolbus;

import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.GamificationCollection;
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




    private ArrayList<String> groupSendList = new ArrayList<>();
    private ArrayList<Boolean> groupsSelected = new ArrayList<>();

    private View lastViewClicked;
    private Group groupSelected = new Group();


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


        // setup back button
        setupActionBarBack();

        // Create the List
        populateList();
        listClickCallback();

        // Setup Buttons
        setupSendToParentBtn();
        setupSelectedGroupsBtn();
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
        //make group list of strings
        List<String> groupList = new ArrayList<>();
        for(Group group : user.getLeadsGroups()){
            groupList.add(group.groupToListString());
        }
        user.setMemberOfGroupsString(groupList);


        // Build adapter and show the items
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.groups_to_send_messages_layout, user.getMemberOfGroupsString() );
        ListView list = (ListView) findViewById(R.id.lstGroupsToSendTo);
        list.setAdapter(adapter);

    }

    private void listClickCallback() {
        ListView list = (ListView) findViewById(R.id.lstGroupsToSendTo);
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
                    groupSelected = user.getLeadsGroups().get(position);
                }

                Toast.makeText(SendingMessageActivity.this, getString(R.string.sending_messaging_activity_clicked) + position,Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Junk Data to see UI elements
    private void testFunction() {
        groupsSelected = new ArrayList<>();
        for(int i = 0;i<10;i++){
            groupSendList.add("" + i);
            groupsSelected.add(false);
        }
    }

    // Returns the string that the user entered
    private String getMessage() {
        EditText message = (EditText) findViewById(R.id.edtMessageInput);
        return message.getText().toString();
    }

    private void setupSendToParentBtn() {
        Button btn = findViewById(R.id.btnSendToParents);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getMessage();

                //Server Send message;
                sendMessageToParents(message);

            }
        });
    }

    private void setupSelectedGroupsBtn() {
        Button btn = findViewById(R.id.btnSendToGroup);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getMessage();

                //Server Send message;
                sendMessageToGroup(message);


            }
        });
    }

//Send message to chosen group
    public void sendMessageToGroup(String text){
        if(groupSelected != null && !text.isEmpty()) {
            Message message = new Message();
            message.setText(text);
            message.setEmergency(false);
            Call<Message> caller = proxy.groupMessage(groupSelected.getId(), message);
            ProxyBuilder.callProxy(SendingMessageActivity.this, caller, returnedMessage -> messageResponse(returnedMessage));
            Toast.makeText(SendingMessageActivity.this, getString(R.string.sending_message_activity_Message) + text, Toast.LENGTH_SHORT).show();
        }
        else if(groupSelected.getId() == 0){
            Toast.makeText(SendingMessageActivity.this, R.string.sending_message_activity_you_must_leader, Toast.LENGTH_SHORT).show();
        }
        else if(text.isEmpty()){
            Toast.makeText(SendingMessageActivity.this, R.string.sending_message_activity_no_text_message, Toast.LENGTH_SHORT).show();
        }
    }

    private void messageResponse(Message returnedMessage) {
        Log.i("Returned Message", "Message returned " + returnedMessage.getText() + "\n");

    }

    //Send message to group members' parents
    public void sendMessageToParents(String text){
        Message message = new Message();
        message.setText(text);
        message.setEmergency(false);
        if(!text.isEmpty()) {
            for (User member : groupSelected.getMemberUsers()) {
                Call<Message> caller = proxy.parentMessage(member.getId(), message);
                ProxyBuilder.callProxy(SendingMessageActivity.this, caller, returnedMessage -> messageResponse(returnedMessage));
                Toast.makeText(SendingMessageActivity.this, "Message: " + text, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(SendingMessageActivity.this, R.string.sending_message_activity_message_can_be_sent, Toast.LENGTH_SHORT).show();
        }
    }
}