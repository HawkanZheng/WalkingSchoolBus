package project.cmpt276.androidui.walkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;

public class Login extends AppCompatActivity {

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
        setUpSkipButton();
        setUpSignUpButton();
    }


    private void setUpSkipButton()
    {
        Button button = (Button) findViewById(R.id.skip);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setUserInfo();

                if(!errorCheck())
                {
                    //look for the user in the server and proceed accordingly
                    Intent intent = new Intent(Login.this, menu.class);
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

    }


    private void changeError(String error)
    {
        TextView test = findViewById(R.id.loginErrorMessages);
        test.setText(error);

    }



}
