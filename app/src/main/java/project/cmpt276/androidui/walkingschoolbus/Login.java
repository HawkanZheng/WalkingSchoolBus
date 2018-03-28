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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;
import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;
/*Login Screen
Login with user email and password
Create account button takes you to signup activity
 */

public class Login extends AppCompatActivity {
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private User user;
    private SharedValues sharedValues;
    private GroupCollection groupList;

    public GoogleMapsInterface gmaps;
    private String password = "";
    private String userName = "";

    private EditText getPassword;
    private EditText getUserName;
    private boolean skip = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gmaps = GoogleMapsInterface.getInstance(this);
        String savedPassword = getSavedPassword(this);
        String savedUserName = getSavedUserName(this);
        password = savedPassword;
        userName =savedUserName;
        user = User.getInstance();
        getInput();
        sharedValues = SharedValues.getInstance();
        groupList = GroupCollection.getInstance();
        //Build server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);
        setUpLoginButton();
        setUpSignUpButton();
        previousLogin();
    }


    private void setUpSignUpButton(){
        Button button = findViewById(R.id.createUser);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, signUp.class);
                startActivity(intent);
            }
        });

    }

    private boolean errorCheck() {
        boolean hasError = false;
        String errors = "Invalid Credentials\nPlease correct the following\n";
        if(userName.length()==0){
            errors = errors +"User Name Invalid\n";
            hasError = true;
        }

        if(password.length()==0){
            errors = errors+"Password Invalid\n";
            hasError=true;
        }

        if(hasError) {
            changeError(errors);
        }


        return hasError;
    }

    private void getInput(){
        getUserName = findViewById(R.id.userName);
        getPassword = findViewById(R.id.enterPassWord);
    }

    private void clearInput(){
        getUserName.setText("");
        getPassword.setText("");
    }

    private void setUserInfo(){
        userName = getUserName.getText().toString();
        password = getPassword.getText().toString();
        user.setEmail(userName);
        user.setPassword(password);

    }


    private void changeError(String error){
        TextView test = findViewById(R.id.loginErrorMessages);
        test.setText(error);

    }

    private void setUpLoginButton(){
        TextView message = findViewById(R.id.loginErrorMessages);
        Button button = findViewById(R.id.loginBtn);
        button.setOnClickListener((View view) -> {
            setUserInfo();
            if(!errorCheck()){
                greetingMessage();
                clearInput();
                saveUserInfo();
                //Set new user
                ProxyBuilder.setOnTokenReceiveCallback(this::onReceiveToken);
                Call<Void> loginCaller = proxy.login(user);
                ProxyBuilder.callProxy(Login.this, loginCaller, this::response);
            }
        });
    }

    private void onReceiveToken(String token){
        // Replace the current proxy with one that uses the token!
        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), token);
        sharedValues.setToken(token);
        setUser();
        getGroups();
    }

    private void setUser() {
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(Login.this, caller, returnedUser -> userResponse(returnedUser));
        Log.i(TAG, "setUser used here");
    }

    private void userResponse(User returnedUser){
        Log.i(TAG, "userResponse used here");
        User.setUser(returnedUser);
        Intent intent = mainMenu.makeIntent(Login.this);
        startActivity(intent);
    }

    private void getGroups() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(Login.this, caller, returnedGroups ->groupsResponse(returnedGroups));
    }

    private void groupsResponse(List<Group> returnedGroups){
        groupList.setGroups(returnedGroups);
    }

    private void response(Void returnedNothing) {
        Log.w(TAG, "Server replied to login request (no content was expected).");
        Toast.makeText(Login.this, "You have logged in.", Toast.LENGTH_LONG).show();
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, Login.class);
    }

    private void greetingMessage(){
        TextView message = findViewById(R.id.loginErrorMessages);
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
            ProxyBuilder.callProxy(Login.this, loginCaller, this::response);
            userName = "";
            password = "";
        }
    }


    @Override
    public void onBackPressed()
    {
        finish();
        moveTaskToBack(true);

    }

}
