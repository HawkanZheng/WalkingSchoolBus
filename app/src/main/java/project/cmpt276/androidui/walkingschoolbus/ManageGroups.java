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
import android.widget.Toast;
import java.util.ArrayList;

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
        getMemberOfGroups(user);
        setUpReturnToMainMenuButton();
        setupLeaveGroupButton();
        setupListCallback();
        setupGroupLeadBtn();
    }

    private void getMemberOfGroups(User currUser) {
        currUser.setMemberOfGroupsString(new ArrayList<String>());
        for(Group group : user.getMemberOfGroups()){
            user.addMemberOfGroupsString(group.groupToListString());
//            Call<Group> caller = proxy.getGroupById(aGroup.getId());
//            ProxyBuilder.callProxy(ManageGroups.this, caller, returnedGroup -> groupResponse(returnedGroup));
        }
        populateList();
    }

    private void groupResponse(Group returnedGroup) {
        user.addMemberOfGroupsString(returnedGroup.groupToListString());
    }

    private void populateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,                     //Context for the activity
                R.layout.my_groups_list,         //Layout used
                user.getMemberOfGroupsString());//Groups/Users displayed
        //Configure the list view
        ListView list = findViewById(R.id.myGroupsList);
        list.setAdapter(adapter);

        //On long click, set the current walking group to the group selected.
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Group aGroup = user.getMemberOfGroups().get(i);
                Call<Group> caller = proxy.getGroupById(aGroup.getId());
                ProxyBuilder.callProxy(ManageGroups.this, caller, response -> saveGroupDataLocal(response));
                return false;
            }
        });
    }
//on click takes you to view members of the group
    private void setupListCallback() {
        ListView list = findViewById(R.id.myGroupsList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //extract the group selected and then populate the UserInfo accordingly
                Group group = user.getMemberOfGroups().get(i);
                sharedValues.setGroup(group);
                Intent intent = new Intent(ManageGroups.this, ParentDashUserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveGroupDataLocal(Group grp){
        sharedValues.setGroup(grp);
        Toast.makeText(ManageGroups.this, getString(R.string.manage_current_group) + grp.getGroupDescription(), Toast.LENGTH_SHORT).show();
        Log.i("GroupsList", grp.groupToListString());
    }

    private void setupLeaveGroupButton() {
        Button button = findViewById(R.id.leaveGroupBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edit = findViewById(R.id.groupIDEdit);
                String input = edit.getText().toString();
                if(!input.isEmpty()) {
                    long id = Long.parseLong(input);
                    Call<Void> caller = proxy.deleteGroupMember(id, user.getId());
                    ProxyBuilder.callProxy(ManageGroups.this, caller, returnedNothing -> leaveGroupResponse(returnedNothing));
                }else{
                    Toast.makeText(ManageGroups.this, R.string.Enter_group_id_manage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void leaveGroupResponse(Void returnedNothing) {
        Call<User> caller = proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(ManageGroups.this, caller, returnedUser -> userResponse(returnedUser));
        Toast.makeText(ManageGroups.this, R.string.left_group_manage, Toast.LENGTH_SHORT).show();
    }

    private void userResponse(User returnedUser) {
        User.setUser(returnedUser);
        user = User.getInstance();
        getMemberOfGroups(user);
    }

    private void setUpReturnToMainMenuButton(){
        Button button = findViewById(R.id.returnToMainMenuBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupGroupLeadBtn() {
        Button btn = findViewById(R.id.btnGroupsILead);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageGroups.this, GroupsLeaderActivity.class);
                startActivity(intent);
            }
        });
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, ManageGroups.class);
    }
}
