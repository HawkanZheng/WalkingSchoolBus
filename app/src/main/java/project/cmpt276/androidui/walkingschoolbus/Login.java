package project.cmpt276.androidui.walkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;

public class Login extends AppCompatActivity {

    public GoogleMapsInterface gmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gmaps = GoogleMapsInterface.getInstance(this);
        setUpSkipButton();
        setUpSignUpButton();
    }


    private void setUpSkipButton()
    {
        Button button = (Button) findViewById(R.id.skip);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setUpSignUpButton()
    {

        Button button = (Button) findViewById(R.id.skip);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Login.this, MapsActivity.class);
                startActivity(intent);

            }
        });
    }



}
