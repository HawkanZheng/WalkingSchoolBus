package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class Login extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private User user;
    private String newToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = User.getInstance();

        //Build server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);
        setUpSkipButton();
        setUpLoginButton();
        setUpNewUserButton();
    }

    private void setUpNewUserButton() {
        Button button = findViewById(R.id.newUserBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = signUp.makeIntent(Login.this);
                startActivity(intent);
            }
        });
    }


    private void setUpSkipButton()
    {
        Button button = (Button) findViewById(R.id.skip);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, mainMenu.class);

                startActivity(intent);

            }
        });

    }

    private void setUpLoginButton()
    {

        Button button = (Button) findViewById(R.id.loginBtn);

        button.setOnClickListener((View view) -> {
            EditText userName = findViewById(R.id.userName);
            String email = userName.getText().toString();

            EditText password = findViewById(R.id.enterPassWord);
            String pw = password.getText().toString();
            //Set new user
            user.setEmail(email);
            user.setPassword(pw);

            ProxyBuilder.setOnTokenReceiveCallback(this::onReceiveToken);

            //ProxyBuilder.setOnErrorCallback(this::onReceiveError);

            Call<Void> caller = proxy.login(user);
            ProxyBuilder.callProxy(Login.this, caller, this::response);

//            Intent intent = mainMenu.makeIntent(Login.this, newToken);
//            startActivity(intent);





        });
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        ProxyBuilder.setOnErrorCallback(null);
//
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        ProxyBuilder.setOnErrorCallback(this::onReceiveError);
//    }

    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), token);
        newToken = token;

        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(Login.this, caller, returnedUser -> response(returnedUser));

        Intent intent = mainMenu.makeIntent(Login.this, newToken);
        startActivity(intent);


    }

    private void onReceiveError(String message){
//        Log.w(TAG, "   --> ERROR: " + message);
//
//        Intent intent = Login.makeIntent(Login.this);
//        startActivity(intent);

    }


    private void response(Void returnedNothing) {
        Log.w(TAG, "Server replied to login request (no content was expected).");
    }

    private void response(User returnedUser){
        User.setUser(returnedUser);

        Log.w(TAG, "After Singleton test, server replied with User: " + returnedUser.toString());
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, Login.class);
    }

}
