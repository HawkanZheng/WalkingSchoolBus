package project.cmpt276.androidui.walkingschoolbus;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Permissions extends AppCompatActivity {



    ArrayList<String> items = new ArrayList<>();
    int listPosition;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items.add("person A wants permission");
        items.add("person B want permission");
        items.add("person C wants permission");
        items.add("person D wants permission");
        setContentView(R.layout.activity_permissions);
        populateList(items);
        listClickCallback();

    }


    private void listClickCallback() {
        ListView list = (ListView) findViewById(R.id.listOfPendingPermissions);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {

                listPosition = position;
                message = items.get(position);
                FragmentManager manager = getFragmentManager();
                PermissionAlertFragment dialog = new PermissionAlertFragment();
                dialog.show(manager, "hello there");

            }
        });
    }


    private void populateList(ArrayList<String> info){

        ListView list = (ListView) findViewById(R.id.listOfPendingPermissions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.permissions, info);
        list.setAdapter(adapter);

    }

    public void showGrantedToast()
    {
        Toast.makeText(Permissions.this, "permission granted", Toast.LENGTH_SHORT).show();
    }

    public void showDeniedToast()
    {
        Toast.makeText(Permissions.this, "permission denied", Toast.LENGTH_SHORT).show();
    }


    public void deleteItem(int position)
    {
        items.remove(position);
        populateList(items);
    }



    private void addToList(String newInfo)
    {
        items.add(newInfo);
        populateList(items);

    }


    public int getPosition()
    {
        return listPosition;
    }

    public void grantPermission()
    {

    }

    public void denyPermission()
    {

    }


    public String getMessage()
    {
        return message;
    }


}


