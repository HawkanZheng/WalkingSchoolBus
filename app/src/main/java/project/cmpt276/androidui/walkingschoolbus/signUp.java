package project.cmpt276.androidui.walkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class signUp extends AppCompatActivity {

//    TextView errorMessage = (TextView) findViewById(R.id.errorMessages);
//    TextView name = (TextView) findViewById(R.id.getName);
//    String toName = name.toString();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setUpJoinButton();
    }


    private void setUpJoinButton()
    {
        Button button = (Button) findViewById(R.id.join);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if(!errorCheck())
                //{
                    Intent intent = new Intent(signUp.this, menu.class);
                    startActivity(intent);
                //}
            }
        });
    }


//
//    private boolean errorCheck()
//    {
//        if(toName.length()==0)
//        {
//            errorMessage.setText("name must be at least one character long\n");
//            return true;
//        }
//
//
//        return false;
//    }
}
