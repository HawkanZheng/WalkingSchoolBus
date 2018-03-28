package project.cmpt276.androidui.walkingschoolbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import project.cmpt276.model.walkingschoolbus.GroupCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;

public class editUserInfo extends AppCompatActivity {


    private WGServerProxy proxy;
    private static final String TAG = "Test";
    private User user;
    private GroupCollection groupList;
    private SharedValues sharedValues;

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
}
