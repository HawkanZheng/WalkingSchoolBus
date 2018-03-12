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

import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;

public class Login extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private User user;

    public GoogleMapsInterface gmaps;
    String password;
    String userName;

    EditText getPassword;
    EditText getUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gmaps = GoogleMapsInterface.getInstance(this);

        getInput();
        user = User.getInstance();

        //Build server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);
        setUpSkipButton();
        setUpLoginButton();
        setUpSignUpButton();
    }


    private void setUpSkipButton()
    {
        Button button = (Button) findViewById(R.id.loginBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setUserInfo();

                if(!errorCheck())
                {
                    //look for the user in the server and proceed accordingly
                    Intent intent = new Intent(Login.this, mainMenu.class);
                    startActivity(intent);
                }


            }
        });

    }

    private void setUpSignUpButton()
    {

        Button button = (Button) findViewById(R.id.createUser);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, signUp.class);
                startActivity(intent);

            }
        });

    }

    private boolean errorCheck()
    {
        boolean hasError = false;
        String errors = "please correct the following\n";

        if(userName.length()==0)
        {
            errors = errors +"User Name Invalid\n";
            hasError = true;
        }

        if(password.length()==0)
        {
            errors = errors+"Password Invalid\n";
            hasError=true;
        }


        if(hasError)
        {
            changeError(errors);
        }


        return hasError;
    }

    private void getInput()
    {
        getUserName = (EditText) findViewById(R.id.userName);
        getPassword = (EditText) findViewById(R.id.enterPassWord);

    }

    private void setUserInfo()
    {
        userName = getUserName.getText().toString();
        password = getPassword.getText().toString();
        user.setEmail(userName);
        user.setPassword(password);

    }


    private void changeError(String error)
    {
        TextView test = findViewById(R.id.loginErrorMessages);
        test.setText(error);

    }

    private void setUpLoginButton()
    {

        Button button = (Button) findViewById(R.id.loginBtn);

        button.setOnClickListener((View view) -> {

            setUserInfo();

            if(!errorCheck()) {

                //Set new user
                ProxyBuilder.setOnTokenReceiveCallback(this::onReceiveToken);

                //ProxyBuilder.setOnErrorCallback(this::onReceiveError);

                Call<Void> caller = proxy.login(user);
                ProxyBuilder.callProxy(Login.this, caller, this::response);

//            Intent intent = mainMenu.makeIntent(Login.this, newToken);
//            startActivity(intent);

            }



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


        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(Login.this, caller, returnedUser -> response(returnedUser));

        Intent intent = mainMenu.makeIntent(Login.this, token);
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
        user = User.getInstance();

        Log.w(TAG, "After Singleton test, server replied with User: " + user.toString());
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, Login.class);
    }

}
