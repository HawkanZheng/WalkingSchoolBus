package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;

public class editUserInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private WGServerProxy proxy;
    private User user;
    private GroupCollection groupList;
    private SharedValues sharedValues;

    private EditText updateName;
    private EditText updateAddress;
    private EditText updateHomePhone;
    private EditText updateCellPhone;
    private EditText updateTeacherName;
    private EditText updateEmergencyInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        user = User.getInstance();

        setCurrentInfo();
        setUpDoneButton();
        setUpMonthSpinner();
        setUpYearSpinner();
        setUpGradeSpinner();
    }


    private void setUpDoneButton()
    {
        Button button = findViewById(R.id.doneBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setNewInfo();


                finish();
            }
        });
    }

    private void setCurrentInfo()
    {
        updateName = findViewById(R.id.updateName);
        updateName.setText(user.getName(), TextView.BufferType.EDITABLE);

        updateAddress = findViewById(R.id.updateAddress);
        updateAddress.setText(user.getAddress(), TextView.BufferType.EDITABLE);

        updateHomePhone = findViewById(R.id.updateHomeNumber);
        updateHomePhone.setText(user.getHomePhone(), TextView.BufferType.EDITABLE);

        updateCellPhone = findViewById(R.id.updateCellNumber);
        updateCellPhone.setText(user.getCellPhone(), TextView.BufferType.EDITABLE);

        updateTeacherName = findViewById(R.id.updateTeacher);
        updateTeacherName.setText(user.getTeacherName(), TextView.BufferType.EDITABLE);

        updateEmergencyInfo = findViewById(R.id.updateEmergencyInfo);
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
        Spinner spinner = findViewById(R.id.updateMonth);
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
        Spinner spinner = findViewById(R.id.updateYear);
        spinner.setAdapter(adapter);

        if(user.getBirthYear()!=null)
        {
            spinner.setSelection((currentYear-user.getBirthYear())+1);
        }

        spinner.setOnItemSelectedListener(this);
    }

    private void setUpGradeSpinner()
    {
        Spinner spinner = findViewById(R.id.updateGrade);
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
            case R.id.updateYear:
                if(i!=0)
                {
                    user.setBirthYear(Integer.parseInt(info));
                }

                else
                {
                    user.setBirthYear(null);
                }
                break;

            case R.id.updateMonth:
                user.setBirthMonth(i);
                break;

            case R.id.updateGrade:

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
}
