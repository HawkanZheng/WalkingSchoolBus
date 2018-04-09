package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.GamificationCollection;
import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class signUpOptionalInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static final String PASSED_IN_NAME = "passed in name";
    public static final String PASSED_IN_PASS_WORD = "passed in pass word";
    public static final String PASSED_IN_USER_NAME = "passed in user name";
    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private User user;
    private SharedValues sharedValues;
    private GroupCollection groupList;
    private GamificationCollection gameCollection;




    private String password;
    private String userName;
    private String name;

    private Integer birthYear = null;
    private Integer birthMonth = null;
    private String address = null;
    private String cellPhone = null;
    private String homePhone = null;
    private String grade = null;
    private String teacherName = null;
    private String emergencyContactInfo = null;


    private EditText getAddress;
    private EditText getCellPhone;
    private EditText getHomePhone;
    private EditText getTeacherName;
    private EditText getEmergencyContactInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_optional_info);
        extractDataFromIntent();
        user = User.getInstance();
        gameCollection = GamificationCollection.getInstance();
        gameCollection = GamificationCollection.trashInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), null);
        groupList = GroupCollection.getInstance();

        setUpMonthsSpinner();
        setUpYearsSpinner();
        setUpGradeSpinner();
        getInput();
        setUpJoinButton();



    }


    private void setUpJoinButton()
    {
        Button button = findViewById(R.id.join2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfo();
                greetingMessage();
                setNewUser();

                Call<User> caller = proxy.createNewUser(user);
                ProxyBuilder.callProxy(signUpOptionalInfo.this, caller, returnedUser -> response(returnedUser));

                SharedPreferences prefs = getSharedPreferences("user info", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user name", userName);
                editor.putString("password", password);
                editor.apply();

            }
        });
    }

    //create a new user with all the info provided
    private void setNewUser() {
        user.setPassword(password);
        user.setName(name);
        user.setEmail(userName);
        user.setBirthYear(birthYear);
        user.setBirthMonth(birthMonth);
        user.setAddress(address);
        user.setCellPhone(cellPhone);
        user.setHomePhone(homePhone);
        user.setTeacherName(teacherName);
        user.setEmergencyContactInfo(emergencyContactInfo);
        user.setGrade(grade);
        user.setCurrentPoints(0);
        user.setTotalPointsEarned(0);
        try{
            String customAsJson = new ObjectMapper().writeValueAsString(gameCollection);
            user.setCustomJson(customAsJson);
        }catch(JsonProcessingException e){
            e.printStackTrace();
        }

    }

    private void response(User returnedUser) {
        //user.setName(null);
        Log.w(TAG, "Server replied with user: " + user.toString());
        ProxyBuilder.setOnTokenReceiveCallback(this::onReceiveToken);
        Call<Void> caller = proxy.login(user);
        ProxyBuilder.callProxy(signUpOptionalInfo.this, caller, returnedNothing -> loginResponse(returnedNothing));
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
        ProxyBuilder.callProxy(signUpOptionalInfo.this, caller, returnedUser -> userResponse(returnedUser));
        Log.i(TAG, "setUser used here");
    }

    private void userResponse(User returnedUser) {
        Log.i(TAG, "userResponse used here");
        User.setUser(returnedUser);
        if(returnedUser.getCustomJson() != null) {
            try {
                GamificationCollection gameficationFromServer =
                        new ObjectMapper().readValue(
                                returnedUser.getCustomJson(),
                                GamificationCollection.class);
                Log.i("CHECK CUSTOMJSON", returnedUser.getCustomJson());
                GamificationCollection.setOurInstance(gameficationFromServer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finish();
        Intent intent = mainMenu.makeIntent(signUpOptionalInfo.this);
        startActivity(intent);
    }

    private void loginResponse(Void returnedNothing) {
        Log.w(TAG, "Server replied to login request (no content was expected).");
        Toast.makeText(signUpOptionalInfo.this, "You have created an account and logged in.", Toast.LENGTH_LONG).show();

    }

    private void getGroups() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(signUpOptionalInfo.this, caller, returnedGroups ->groupsResponse(returnedGroups));
    }

    private void groupsResponse(List<Group> returnedGroups) {
        groupList.setGroups(returnedGroups);
    }


    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }


    private void setUpMonthsSpinner()
    {

        Spinner spinner = findViewById(R.id.months);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    private void setUpGradeSpinner()
    {
        Spinner spinner = findViewById(R.id.grade);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grades_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void setUpYearsSpinner()
    {
        ArrayList<String> years = new ArrayList<>();
        years.add("Year");
        int currentYear = 2018;
        int minYear = 1900;
        for(int i = currentYear; i>=minYear; i--)
        {
            String year = Integer.toString(i);
            years.add(year);
        }


        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years);
        Spinner spinner = findViewById(R.id.years);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



    }

    //set user info from the item selected in each spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String info = adapterView.getItemAtPosition(i).toString();
        switch (adapterView.getId())
        {
            case R.id.years:

                if(i!=0)
                {
                    birthYear = Integer.parseInt(info);
                }

                else
                {
                    birthYear = null;
                }


                break;

            case R.id.months:

                birthMonth = i;
                break;

            case R.id.grade:

                if(i!=0) {
                    grade = info;
                }

                else
                {
                    grade = null;
                }
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void getInput()
    {
        getAddress = findViewById(R.id.getAddress);
        getCellPhone = findViewById(R.id.cellNumber);
        getHomePhone = findViewById(R.id.phoneNumber);
        getTeacherName = findViewById(R.id.teachersName);
        getEmergencyContactInfo = findViewById(R.id.emergencyInfo);

    }


    private void setInfo()
    {
        address = getAddress.getText().toString();
        cellPhone = getCellPhone.getText().toString();
        homePhone = getHomePhone.getText().toString();
        teacherName = getTeacherName.getText().toString();
        emergencyContactInfo = getEmergencyContactInfo.getText().toString();

    }


    //displays message when the user logs in
    private void greetingMessage(){
        TextView message = findViewById(R.id.greetingMessage);
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                message.setText("Welcome!\nLogging You in Now!");
            }
            public void onFinish() {
                message.setText("");
            }
        }.start();
    }


    public static Intent makeIntent(Context context, String name, String password, String userName)
    {
        Intent intent = new Intent(context, signUpOptionalInfo.class);
        intent.putExtra(PASSED_IN_NAME, name);
        intent.putExtra(PASSED_IN_PASS_WORD, password);
        intent.putExtra(PASSED_IN_USER_NAME, userName);

        return intent;
    }

    private void extractDataFromIntent()
    {
        Intent intent = getIntent();
        name = intent.getStringExtra(PASSED_IN_NAME);
        password = intent.getStringExtra(PASSED_IN_PASS_WORD);
        userName = intent.getStringExtra(PASSED_IN_USER_NAME);
    }

}
