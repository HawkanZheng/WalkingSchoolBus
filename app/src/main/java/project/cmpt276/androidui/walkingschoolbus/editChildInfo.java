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
//        getUser(user);
        setCurrentInfo();
        setUpDoneButton();
        setUpMonthSpinner();
        setUpYearSpinner();
        setUpGradeSpinner();
    }


//    private void getUser(User aUser) {
//        Call<User> caller = proxy.getUserById(aUser.getId());
//        ProxyBuilder.callProxy(editChildInfo.this, caller, returnedUser -> userResponse(returnedUser));
//    }
//
//    private void userResponse(User returnedUser) {
//        //Set text view
////        setView(returnedUser);
//        sharedValues.setUser(returnedUser);
//        user = sharedValues.getUser();
////        getMemberOfGroups(user);
//    }

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
                finish();
            }
        });
    }
    //response to edit user server call
    private void userResponse(User returnedUser) {
        Log.i("Edited User", "User Edit Successful.");
        finish();
    }

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


    private void setNewInfo()
    {
        // TODO: 2018-03-27  make it so all changed info is saved on the server
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

    private void setUpMonthSpinner()
    {
        Spinner spinner = findViewById(R.id.updateChildMonth);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(user.getBirthMonth());
        spinner.setOnItemSelectedListener(this);
    }

    private void setUpYearSpinner()
    {
        ArrayList<String> years = new ArrayList<>();
        years.add("Year");
        int currentYear = 2018;
//        int yearForCalculation = currentYear++;
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        // TODO: 2018-03-27 make it so changed info from spinners is saved on the server
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
}
