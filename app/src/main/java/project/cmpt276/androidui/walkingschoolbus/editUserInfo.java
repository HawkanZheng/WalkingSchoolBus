package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);

//        sharedValues = SharedValues.getInstance();
//        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        user = User.getInstance();
//        Log.i(TAG, ""+user.toString());
//        groupList = GroupCollection.getInstance();

        setupGreeting();
    }


    private void setupGreeting() {
        TextView view = findViewById(R.id.testGreeting);
        view.setText(user.getName());
    }
}
