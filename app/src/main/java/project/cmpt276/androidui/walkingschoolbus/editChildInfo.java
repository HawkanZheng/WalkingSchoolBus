package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import project.cmpt276.model.walkingschoolbus.ParentDashDataCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

/**
 * class that allows the user to edit the info of someone they monitor
 */
public class editChildInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private SharedValues sharedValues;
    private User user;
    private User theUser;
    private WGServerProxy proxy;

    private ParentDashDataCollection parentData = ParentDashDataCollection.getInstance();

    private EditText updateName;
    private EditText updateAddress;
    private EditText updateHomePhone;
    private EditText updateCellPhone;
    private EditText updateTeacherName;
    private EditText updateEmergencyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child_info);

        //get proxy and instances
        theUser = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        user = sharedValues.getUser();
        setCurrentInfo();
        setUpDoneButton();
        setUpMonthSpinner();
        setUpYearSpinner();
        setUpGradeSpinner();
        setUpGreeting();
    }

    private void setUpDoneButton()
    {
        Button button = findViewById(R.id.doneChildBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setNewInfo();
                //Push new info to server
                Call<User> caller = proxy.editUser(user, user.getId());
                ProxyBuilder.callProxy(editChildInfo.this, caller, returnedUser -> userResponse(returnedUser));

            }
        });
    }
    //response to edit user server call
    private void userResponse(User returnedUser) {
        Log.i("Edited User", "User Edit Successful.");
        this.finish();
    }
    //set current views on start activity
    private void setCurrentInfo()
    {
        updateName = findViewById(R.id.updateChildName);
        updateName.setText(user.getName(), TextView.BufferType.EDITABLE);

        updateAddress = findViewById(R.id.updateChildAddress);
        updateAddress.setText(user.getAddress(), TextView.BufferType.EDITABLE);

        updateHomePhone = findViewById(R.id.updateChildHomeNumber);
        updateHomePhone.setText(user.getHomePhone(), TextView.BufferType.EDITABLE);

        updateCellPhone = findViewById(R.id.updateChildCellNumber);
        updateCellPhone.setText(user.getCellPhone(), TextView.BufferType.EDITABLE);

        updateTeacherName = findViewById(R.id.updateChildTeacher);
        updateTeacherName.setText(user.getTeacherName(), TextView.BufferType.EDITABLE);

        updateEmergencyInfo = findViewById(R.id.updateChildEmergencyInfo);
        updateEmergencyInfo.setText(user.getEmergencyContactInfo(), TextView.BufferType.EDITABLE);

    }

    //set new user info
    private void setNewInfo()
    {

        String newName = updateName.getText().toString();
        user.setName(newName);

        String newAddress = updateAddress.getText().toString();
        user.setAddress(newAddress);

        String newHomePhone = updateHomePhone.getText().toString();
        user.setHomePhone(newHomePhone);

        String newCellPhone = updateCellPhone.getText().toString();
        user.setCellPhone(newCellPhone);

        String newTeacherName = updateTeacherName.getText().toString();
        user.setTeacherName(newTeacherName);

        String newEmergencyInfo = updateEmergencyInfo.getText().toString();
        user.setEmergencyContactInfo(newEmergencyInfo);


    }
    //month spinner for birthday
    private void setUpMonthSpinner()
    {
        Spinner spinner = findViewById(R.id.updateChildMonth);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(user.getBirthMonth());
        spinner.setOnItemSelectedListener(this);
    }
    //year spinner for birthday
    private void setUpYearSpinner()
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
        Spinner spinner = findViewById(R.id.updateChildYear);
        spinner.setAdapter(adapter);

        if(user.getBirthYear()!=null)
        {
            spinner.setSelection((currentYear-user.getBirthYear())+1);
        }

        spinner.setOnItemSelectedListener(this);
    }
    //grade spinner for grade
    private void setUpGradeSpinner()
    {
        Spinner spinner = findViewById(R.id.updateChildGrade);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grades_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(user.getGrade()!=null)
        {
            spinner.setSelection(Integer.parseInt(user.getGrade()));
        }

        spinner.setOnItemSelectedListener(this);

    }
    //when spinner item selected, set user info
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


        String info = adapterView.getItemAtPosition(i).toString();

        switch (adapterView.getId())
        {
            case R.id.updateChildYear:
                if(i!=0)
                {
                    user.setBirthYear(Integer.parseInt(info));
                }

                else
                {
                    user.setBirthYear(null);
                }
                break;

            case R.id.updateChildMonth:
                user.setBirthMonth(i);
                break;

            case R.id.updateChildGrade:

                if(i!=0)
                {
                    user.setGrade(info);
                }

                else
                {
                    user.setGrade(null);
                }

                break;

            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public static Intent makeIntent(Context context){
        return new Intent(context, editChildInfo.class);
    }

    //customizes the greeting at the top with the child's name
    private void setUpGreeting()
    {
        TextView greeting = findViewById(R.id.changeChildInfoGreeting);
        greeting.setText("Change " + user.getName()+ "'s info");
    }
}
