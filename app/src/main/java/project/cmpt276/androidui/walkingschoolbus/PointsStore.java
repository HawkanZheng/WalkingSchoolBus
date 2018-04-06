package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
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

import project.cmpt276.model.walkingschoolbus.SharedValues;

public class PointsStore extends AppCompatActivity {

    //Conversion: px = dp * (dpi / 160)
    private SharedValues sharedValues;
    private final int row = 2;
    private final int col = 10;
    private Button[][] buttons = new Button[row][col];

    private int imgW = 90;
    private int imgH = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_store);
        sharedValues = SharedValues.getInstance();
        createStoreStock();
        displayCurrency();
    }

    //Displays the user's currency.
    private void displayCurrency() {
        //TODO -- Grab user's points from server.
        int points = 0;
        TextView t = findViewById(R.id.userCurrency);
        t.setText("Balance: " + points);
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
        //TODO -- Purchasing an avatar logic.
        //Open purchase confirmation dialog
        ConfirmPurchaseFragment fragment = new ConfirmPurchaseFragment();
        fragment.setPosition(i,j);
        Log.i("PointsStore","Index: " + i + " " + j);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        fragment.show(manager,"ConfirmPurchase");
        displayCurrency();
        Toast.makeText(PointsStore.this,"Selected " + i +", "+ j, Toast.LENGTH_SHORT).show();
    }

    private boolean setAvatar(int i, int j){
        //TODO -- Needs to find out if the user owns the selected avatar before setting it.
        //Avoids duplicate buttons being highlighted.
        cleanButtons();
        //Shows which avatar is in use.
        Button btn = buttons[i][j];
        btn.setBackgroundResource(R.drawable.button_border_current);
        //TODO -- Upload to server in order to save on login.
        //Get selected image to save to sharedValues.
        Resources resources = getResources();
        TypedArray imgArr = resources.obtainTypedArray(R.array.avatars);
        Drawable d = imgArr.getDrawable((10*i)+j);
        sharedValues.setUserAvatar(d);
        Log.i("SettingImg", d.toString());
        return true;
    }

    private void cleanButtons(){
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                //Set back to the default background.
                buttons[i][j].setBackgroundResource(R.drawable.button_border);
            }
        }
    }

    //Intent to reach the shop activity.
    public static Intent makeIntent(Context c){
        return new Intent(c, PointsStore.class);
    }
}
