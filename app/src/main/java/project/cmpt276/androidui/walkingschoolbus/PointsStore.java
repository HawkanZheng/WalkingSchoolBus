package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.cmpt276.model.walkingschoolbus.GamificationCollection;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

public class PointsStore extends AppCompatActivity implements DialogInterface.OnDismissListener{

    private SharedValues sharedValues;
    private static final int row = 2;
    private static final int col = 10;
    private Button[][] buttons = new Button[row][col];

    private int imgW = 90;
    private int imgH = 250;

    private User user;
    private WGServerProxy proxy;
    private GamificationCollection gameCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_store);
        sharedValues = SharedValues.getInstance();
        user = User.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        gameCollection = GamificationCollection.getInstance();
        createStoreStock();
        displayCurrency();
        cleanButtons();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        cleanButtons();
        displayCurrency();
    }

    //Displays the user's currency.
    private void displayCurrency() {
        Call<User> caller = proxy.getUserById(user.getId());
        ProxyBuilder.callProxy(this, caller, returnedUser -> setCurrencyText(returnedUser));
    }

    //Handles a user's currency obtained from the server.
    private void setCurrencyText(User u){
        TextView t = findViewById(R.id.userCurrency);
        if(u.getCurrentPoints() == null){
            t.setText("Balance: " + 0);
        }else{
            t.setText("Balance: " + u.getCurrentPoints());
        }
    }

    //Set up table to display items in the shop.
    private void createStoreStock(){
        TableLayout table = findViewById(R.id.storeStock);
        for(int i = 0; i < row; i++){
            TableRow tableRow = new TableRow(this);
            table.addView(tableRow);
            for(int j = 0; j < col; j++){
                final Button item = new Button(this);
                final int r = i;
                final int c = j;
                item.setOnClickListener(v -> purchasingAvatar(r,c));
                item.setOnLongClickListener(v -> setAvatar(r,c));
                buttons[r][c] = item;
                tableRow.addView(item);
            }
        }
        lockButtonSize();
        applyImages();
    }

    //Locks button size.
    private void lockButtonSize(){
        for(int i = 0; i < row; i++){
            for(int j = 0 ; j < col; j++){
                Button button = buttons[i][j];
                button.setMinimumWidth(imgW);
                button.setMaxWidth(imgW);
                button.setMinimumHeight(imgH);
                button.setMaxHeight(imgH);
            }
        }
    }

    //Applies images to the button.
    private void applyImages(){
        int itr = 0;
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                Resources resources = getResources();
                int index = col * i + j;
                //Obtain the images from an xml array.
                TypedArray imgArr = resources.obtainTypedArray(R.array.avatars);
                //Create and scale the image.
                Bitmap originalImage = BitmapFactory.decodeResource(getResources(),imgArr.getResourceId(itr++,-1));
                Bitmap scaledImage = Bitmap.createScaledBitmap(originalImage, imgW,imgH,true);
                //Adds the obtained image and set a border background.
                buttons[i][j].setForeground(new BitmapDrawable(resources,scaledImage));
                buttons[i][j].setBackgroundResource(R.drawable.button_border);
            }
        }
    }

    //Update the currency on click if a transaction is made.
    private void purchasingAvatar(int i, int j){
        //Open purchase confirmation dialog
        if(!gameCollection.getAvatarStateByPosition((col*i)+j)){
            int index = (col * i) + j;
            ConfirmPurchaseFragment fragment = new ConfirmPurchaseFragment();
            fragment.setPosition(index);
            Log.i("PointsStore","Index: " + index);
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            fragment.show(manager,"ConfirmPurchase");
        }
    }

    private boolean setAvatar(int i, int j){
        int index = (col * i) + j;
        //Checks if the user owns this avatar.
        if(gameCollection.getAvatarStateByPosition(index)){
            //Shows which avatar is in use.
            Button btn = buttons[i][j];
            btn.setBackgroundResource(R.drawable.button_border_current);
            //Get selected image to save to sharedValues.
            Resources resources = getResources();
            TypedArray imgArr = resources.obtainTypedArray(R.array.avatars);
            Drawable d = imgArr.getDrawable(index);
            sharedValues.setUserAvatar(d);
            gameCollection.setAvatarSelectedPosition(index);
            //Write to the server which avatar is currently in use so it will persist on next launch.
            try{
                String customAsJson = new ObjectMapper().writeValueAsString(gameCollection);
                user.setCustomJson(customAsJson);
            }catch(JsonProcessingException e){
                e.printStackTrace();
            }
            Call<User> caller = proxy.editUser(user, user.getId());
            ProxyBuilder.callProxy(PointsStore.this, caller, returnedUser -> Toast.makeText(PointsStore.this, gameCollection.getAvatarAtPostion(index) + " avatar selected!", Toast.LENGTH_SHORT).show());
            //Cleans buttons, marks purchased ones and marks a current one if it there is one.
            cleanButtons();
        }else{
            Toast.makeText(this, "You do not own this...", Toast.LENGTH_SHORT);
        }
        return true;
    }

    private void cleanButtons(){
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                //Set back to the default background.
                buttons[i][j].setBackgroundResource(R.drawable.button_border);
            }
        }
        markPurchased();
        markCurrent();
    }

    //Marks which avatars are already unlocked.
    private void markPurchased(){
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                int index =  (col * i) + j;
                Log.i("PointsStoreLog", index + " " + gameCollection.getAvatarAtPostion(index));
                if(gameCollection.getAvatarStateByPosition(index)) {
                    buttons[i][j].setBackgroundResource(R.drawable.button_border_unlocked);
                }
            }
        }
    }

    //Marks your current avatar.
    private void markCurrent(){
        try{
            String current = gameCollection.getAvatarAtPostion(gameCollection.getAvatarSelectedPosition());
            for(int i = 0; i < row; i++){
                for(int j = 0; j < col; j++){
                    //Set back to the default background.
                    String avatar = gameCollection.getAvatarAtPostion((col * i) + j);
                    if(current == avatar){
                        buttons[i][j].setBackgroundResource(R.drawable.button_border_current);
                    }
                }
            }
        }catch(ArrayIndexOutOfBoundsException e){
            Log.i("PointsStore", "No current avatar set.");
        }
    }

    //Intent to reach the shop activity.
    public static Intent makeIntent(Context c){
        return new Intent(c, PointsStore.class);
    }
}
