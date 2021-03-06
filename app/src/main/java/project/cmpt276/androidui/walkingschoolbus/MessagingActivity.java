package project.cmpt276.androidui.walkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Message;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.model.walkingschoolbus.lastGpsLocation;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class MessagingActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    private SharedValues sharedValues;
    private User user;

    ArrayList<String> unreadMessages = new ArrayList<>();
    ArrayList<Boolean> readMessageMap = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        //Get instances
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        //Get proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        //Get messages
        //getMessagesForUser();
        setupActionBarBack();
        setupOldMessagesBtn();
        setupSendAMessageAsLeaderBtn();
        setupSendAMessageAsUserBtn();

        // Configure the List
        //populateList();
        listClickCallback();
    }

    // Generate Junk Data for testing
    private void testFoo() {
        for(int i = 0;i<10;i++){
            unreadMessages.add("" + i);
        }
    }

    //refresh messaging list on resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("RESUME MESSAGING","MESSAGING REFRESHING");
        //user = User.getInstance();
        getMessagesForUser();
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
        ImageButton btn = findViewById(R.id.btnViewOlderMessages);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagingActivity.this, OldMessagesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupSendAMessageAsLeaderBtn() {
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
    private void populateList(){//List<String> messages) {


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
                if(!readMessageMap.get(position)){
                    //Clicked messages are considered 'Read'

                    parent.getChildAt(position).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    Call<List<Message>> caller = proxy.getMessagesToUserUnread(user.getId(), "unread");
                    ProxyBuilder.callProxy(MessagingActivity.this, caller, returnedMessages -> messagesMarkResponse(returnedMessages, position));


                    readMessageMap.set(position,true);
                    if(sharedValues.getMessagesUnread() > 0){
                        sharedValues.storeMessagesUnread(sharedValues.getMessagesUnread()-1);
                    }
                }
            }
        });
    }

    private void messagesMarkResponse(List<Message> returnedMessages, int position) {
        Call<User> caller = proxy.markMessage(returnedMessages.get(position).getId(), user.getId(), true);
        ProxyBuilder.callProxy(MessagingActivity.this, caller, returnedUser -> userResponse(returnedUser));
    }

    private void userResponse(User returnedUser) {
        Log.i("Message Marked:", "Message now read");
        Toast.makeText(MessagingActivity.this, R.string.message_read_messaging,Toast.LENGTH_SHORT).show();
        getMessagesForUser();
        //User.setUser(returnedUser);
    }

    private void getMessagesForUser(){
        Call<List<Message>> caller = proxy.getMessagesToUserUnread(user.getId(), "unread");
        ProxyBuilder.callProxy(MessagingActivity.this, caller, returnedMessages -> messagesResponse(returnedMessages));

    }

    private void messagesResponse(List<Message> returnedMessages) {
        Log.i("Response", "Call to server successful");

        //reset list and boolean map
        unreadMessages = new ArrayList<>();
        readMessageMap = new ArrayList<>();

        //FILL MESSAGE LIST
        for(Message message: returnedMessages){
            unreadMessages.add(message.messageToString());
            readMessageMap.add(false);
        }
        Log.i("BOOLEAN MAP:", readMessageMap.toString());
        populateList();
    }

    private void setupSendAMessageAsUserBtn() {
        Button btn = findViewById(R.id.btnUserSendMessage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagingActivity.this, SendUserMessageActivity.class);
                startActivity(intent);
            }
        });
    }
}
