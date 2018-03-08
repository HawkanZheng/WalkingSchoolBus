package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class mainMenu extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private long userId = 0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);


        setupMonitorUserButton();
    }

    private void setupMonitorUserButton() {
        Button button = findViewById(R.id.monitorUserBtn);

        Call<List<User>> caller = proxy.getUsers();
        ProxyBuilder.callProxy(mainMenu.this, caller, returnedUsers -> response(returnedUsers));









    }

    private void response(List<User> returnedUsers) {
        Log.w(TAG, "All Users:");
        for (User user : returnedUsers) {
            Log.w(TAG, "    User: " + user.toString());
        }
    }

    private void response(User user){
        Log.w(TAG, "server replied with User: " + user.toString());
        TextView view = findViewById(R.id.displayUserView);
        view.setText(user.toString());
    }
}
