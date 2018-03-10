package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Proxy;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class signUp extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private long userId = 0;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user = User.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);
        setupSignUpBtn();
    }

    private void setupSignUpBtn() {
        Button button = findViewById(R.id.signUpBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Build new user
                EditText newName = findViewById(R.id.signUpName);
                String name = newName.getText().toString();
                EditText newEmail = findViewById(R.id.signUpEmail);
                String email = newEmail.getText().toString();
                EditText newPassword = findViewById(R.id.signUpPassword);
                String password = newPassword.getText().toString();
                user.setPassword(password);
                user.setEmail(email);
                user.setName(name);

                Call<User> caller = proxy.createNewUser(user);
                ProxyBuilder.callProxy(signUp.this, caller, returnedUser -> response(returnedUser));

            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, signUp.class);
    }

    private void response(User user) {
        Log.w(TAG, "Server replied with user: " + user.toString());
        userId = user.getId();
    }
}
