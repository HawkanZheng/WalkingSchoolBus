package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import project.cmpt276.model.walkingschoolbus.Group;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class ManageGroups extends AppCompatActivity {
    private WGServerProxy proxy;
    private User user;
    private SharedValues sharedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_groups);
        user = User.getInstance();
        sharedValues = SharedValues.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        getMemberOfGroups();
    }

    private void getMemberOfGroups() {
        for(int i = 0; i < user.getMemberOfGroups().size(); i++){
            Group aGroup = user.getMemberOfGroups().get(i);
            Call<Group> caller = proxy.getGroupById(aGroup.getId());
            ProxyBuilder.callProxy(ManageGroups.this, caller, returnedGroup -> groupResponse(returnedGroup));
        }
        populateList();
    }

    private void groupResponse(Group returnedGroup) {
        user.addMemberOfGroupsString(returnedGroup.groupToListString());
    }

    private void populateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,           //Context for the activity
                R.layout.my_groups_list,      //Layout used
                user.getMemberOfGroupsString());               //Groups/Users displayed

        //Configure the list view
        ListView list = findViewById(R.id.myGroupsList);
        list.setAdapter(adapter);
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, ManageGroups.class);

    }

}
