package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Proxy;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import android.widget.TextView;
import android.widget.Toast;

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
    private User user;
    private SharedValues sharedValues;
    private GroupCollection groupList;

    private String name;
    private String password;
    private String userName;

    private EditText getName;
    private EditText getPassword;
    private EditText getUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        groupList = GroupCollection.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);
        groupList = GroupCollection.getInstance();
        getInput();
        previousLogin();
        setUpNextButton();
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
        getGroups();
    }

    private void setUser() {
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(signUp.this, caller, returnedUser -> userResponse(returnedUser));
        Log.i(TAG, "setUser used here");
    }

    private void userResponse(User returnedUser) {
        Log.i(TAG, "userResponse used here");
        finish();
        User.setUser(returnedUser);
        Intent intent = mainMenu.makeIntent(signUp.this);
        startActivity(intent);
    }

    private void loginResponse(Void returnedNothing) {
        Log.w(TAG, "Server replied to login request (no content was expected).");
        Toast.makeText(signUp.this, "You have created an account and logged in.", Toast.LENGTH_LONG).show();

    }

    private void getGroups() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(signUp.this, caller, returnedGroups ->groupsResponse(returnedGroups));
    }

    private void groupsResponse(List<Group> returnedGroups) {
        groupList.setGroups(returnedGroups);
    }

    private boolean errorCheck()
    {
        boolean hasError = false;
        String errors = "Credentials Invalid\nPlease Correct the Following\n";

        if(name.length()==0){
            errors = errors+"Name Invalid\n";
            hasError= true;
        }

        if(userName.length()==0){
            errors = errors +"User Name Invalid\n";
            hasError = true;
        }

        if(password.length()==0){
            errors = errors+"Password Invalid\n";
            hasError=true;
        }

        if(hasError){
            changeError(errors);
        }
        return hasError;
    }

    private void getInput(){
        getUserName = findViewById(R.id.createEmail);
        getPassword = findViewById(R.id.createPassword);
        getName = findViewById(R.id.createName);
    }

    private void setUserInfo(){
        userName = getUserName.getText().toString();
        password = getPassword.getText().toString();
        name = getName.getText().toString();
    }


    private void changeError(String error){
        TextView test = findViewById(R.id.errorMessages);
        test.setText(error);
    }

    private void greetingMessage(){
        TextView message = findViewById(R.id.errorMessages);
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                message.setText("Welcome!\nLogging You in Now!");
            }
            public void onFinish() {
                message.setText("");
            }
        }.start();
    }

    private void saveUserInfo(){
        SharedPreferences prefs = this.getSharedPreferences("user info", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user name", userName);
        editor.putString("password", password);
        editor.apply();
    }

    static public String getSavedPassword(Context context){
        SharedPreferences prefs = context.getSharedPreferences("user info", MODE_PRIVATE);
        return prefs.getString("password", "");
    }

    static public String getSavedUserName(Context context){
        SharedPreferences prefs = context.getSharedPreferences("user info", MODE_PRIVATE);
        return prefs.getString("user name", "");
    }

    //Allows instant login if previously logged in. "remember me" feature.
    private void previousLogin() {
        String savedPassword = getSavedPassword(this);
        String savedUserName = getSavedUserName(this);
        if(!savedUserName.equals("")&&!savedPassword.equals("")) {
            greetingMessage();
            ProxyBuilder.setOnTokenReceiveCallback(this::onReceiveToken);
            userName = savedUserName;
            password = savedPassword;
            user.setEmail(userName);
            user.setPassword(password);

            Call<Void> loginCaller = proxy.login(user);
            ProxyBuilder.callProxy(signUp.this, loginCaller, this::loginResponse);
            userName = "";
            password = "";
        }
    }


    private void setUpNextButton()
    {

        Button button = findViewById(R.id.nextStepSignUp);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setUserInfo();
                if(!errorCheck()) {
                    Intent intent = signUpOptionalInfo.makeIntent(signUp.this, name, password, userName);
                    startActivity(intent);

                }
            }
        });
    }

}
