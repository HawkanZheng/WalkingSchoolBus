package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;

public class editUserInfo extends AppCompatActivity {


    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private User user;
    private GroupCollection groupList;
    private SharedValues sharedValues;

    private EditText updateAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        user = User.getInstance();
        updateAddress  = findViewById(R.id.updateAddress);
        updateAddress.setText(user.getAddress(), TextView.BufferType.EDITABLE);
//

//        sharedValues = SharedValues.getInstance();
//        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());

//        Log.i(TAG, ""+user.toString());
//        groupList = GroupCollection.getInstance();

        setupGreeting();
        setUpDoneButton();
    }


    private void setupGreeting() {
        TextView view = findViewById(R.id.testGreeting);
        view.setText(user.getName() + " " + user.getTeacherName() + " " + " you live at " + user.getAddress());
    }


    private void setUpDoneButton()
    {
        Button button = findViewById(R.id.doneBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newAddress = updateAddress.getText().toString();

                if(!newAddress.equals(""))
                {
                    user.setAddress(newAddress);

                }

                finish();
            }
        });
    }
}
