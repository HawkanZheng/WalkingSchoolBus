package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SendUserMessageActivity extends AppCompatActivity {

    private ArrayList<String> groups = new ArrayList<>();
    private View lastViewClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_user_message);

        setupActionBarBack();

        setupSendButton();
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

    private void setupSendButton() {
        Button btn = findViewById(R.id.btnUserSend);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: Server call
                Toast.makeText(SendUserMessageActivity.this,getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void populateList() {
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

                    // TODO: Extract group selected from onclick
                }

                Toast.makeText(SendUserMessageActivity.this, "Clicked" + position,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
