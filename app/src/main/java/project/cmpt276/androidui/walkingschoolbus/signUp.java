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

import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import android.widget.TextView;

/* Sign Up Activity
-Create new user:
    -name
    -email
    -password
-And logs users into main menu
 */

public class signUp extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private long userId = 0;
    private User user;
    private SharedValues sharedValues;


//    User user = new User();
//    TextView name = (TextView) findViewById(R.id.getName);
//    String toName = name.toString();

    String name;
    String password;
    String userName;

    EditText getName;
    EditText getPassword;
    EditText getUserName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);
        getInput();
        setupSignUpBtn();
//        setupSignUpBtn();
    }

    private void setupSignUpBtn() {
        Button button = findViewById(R.id.join);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Build new user

                setUserInfo();
                if(!errorCheck())
                {
                    user.setPassword(password);
                    user.setEmail(userName);
                    user.setName(name);
//                    user = User.getInstance();
                    Call<User> caller = proxy.createNewUser(user);
                    ProxyBuilder.callProxy(signUp.this, caller, returnedUser -> response(returnedUser));
                }

            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, signUp.class);
    }

    private void response(User returnedUser) {
        user.setName(null);
        Log.w(TAG, "Server replied with user: " + user.toString());
        ProxyBuilder.setOnTokenReceiveCallback(this::onReceiveToken);
        Call<Void> caller = proxy.login(user);
        ProxyBuilder.callProxy(signUp.this, caller, returnedNothing -> loginResponse(returnedNothing));

    }

    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), token);
        sharedValues.setToken(token);

        setUser();




    }

    private void setUser() {
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(signUp.this, caller, returnedUser -> userResponse(returnedUser));
        Log.i(TAG, "setUser used here");
    }

    private void userResponse(User returnedUser) {
        Log.i(TAG, "userResponse used here");
        User.setUser(returnedUser);

        Intent intent = mainMenu.makeIntent(signUp.this);
        startActivity(intent);


    }

    private void loginResponse(Void returnedNothing) {

    }


    //
    private boolean errorCheck()
    {
        boolean hasError = false;
        String errors = "please correct the following\n";

        if(name.length()==0)
        {
            errors = errors+"Name Invalid\n";
            hasError= true;
        }

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
        getUserName = (EditText) findViewById(R.id.createEmail);
        getPassword = (EditText) findViewById(R.id.createPassword);
        getName = (EditText) findViewById(R.id.createName);
    }

    private void setUserInfo()
    {
        userName = getUserName.getText().toString();
        password = getPassword.getText().toString();
        name = getName.getText().toString();

    }


    private void changeError(String error)
    {
        TextView test = findViewById(R.id.errorMessages);
        test.setText(error);

    }
}
