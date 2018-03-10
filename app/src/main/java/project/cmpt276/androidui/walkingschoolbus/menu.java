package project.cmpt276.androidui.walkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setUpMapButton();
    }


    private void setUpMapButton()
    {
        Button button = (Button) findViewById(R.id.goToMap);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
