package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
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

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class signUp extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private long userId = 0;
    private User user;


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
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);
//        setupSignUpBtn();
    }

//    private void setupSignUpBtn() {
//        Button button = findViewById(R.id.signUpBtn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Build new user
//                EditText newName = findViewById(R.id.signUpName);
//                String name = newName.getText().toString();
//                EditText newEmail = findViewById(R.id.signUpEmail);
//                String email = newEmail.getText().toString();
//                EditText newPassword = findViewById(R.id.signUpPassword);
//                String password = newPassword.getText().toString();
//                user.setPassword(password);
//                user.setEmail(email);
//                user.setName(name);
//
//                Call<User> caller = proxy.createNewUser(user);
//                ProxyBuilder.callProxy(signUp.this, caller, returnedUser -> response(returnedUser));
//
//            }
//        });
//    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, signUp.class);
    }

    private void response(User user) {
        Log.w(TAG, "Server replied with user: " + user.toString());
        userId = user.getId();
        getInput();
        setUpJoinButton();
    }


    private void setUpJoinButton()
    {

        Button button = (Button) findViewById(R.id.join);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUserInfo();
                if(!errorCheck())
                {
                    //create a new user
                    Intent intent = new Intent(signUp.this, menu.class);
                    startActivity(intent);
                }
            }
        });
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
