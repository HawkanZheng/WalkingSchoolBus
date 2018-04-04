package project.cmpt276.androidui.walkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class PointsStore extends AppCompatActivity {

    private final int row = 5;
    private final int col = 4;
    private Button[][] buttons = new Button[row][col];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_store);
        createStoreStock();
    }


    //Set up table to display items in the shop.
    private void createStoreStock(){
        TableLayout table = findViewById(R.id.storeStock);
        for(int i = 0; i < col; i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f));
            table.addView(tableRow);
            for(int j = 0; j < row; j++){
                final Button item = new Button(this);
                item.setLayoutParams(new TableRow.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT,
                        1.0f));
                final int r = i;
                final int c = j;
                item.setOnClickListener(v -> buttonAction(r,c));
                buttons[c][r] = item;
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
                button.setMinimumWidth(84);
                button.setMaxWidth(84);
                button.setMinimumHeight(128);
                button.setMaxHeight(128);
            }
        }
    }


    //Applies images to the button.
    private void applyImages(){
        //Setting image.
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                int imgWidth = 21;
                int imgHeight = 32;
                Bitmap originalImage = BitmapFactory.decodeResource(getResources(),R.drawable.batman);
                Bitmap scaledImage = Bitmap.createScaledBitmap(originalImage, imgWidth,imgHeight,true);
                Resources resources = getResources();
                buttons[i][j].setBackground(new BitmapDrawable(resources,scaledImage));
            }
        }
    }

    //action performed when a button is pressed.
    private void buttonAction(int i, int j){
        Toast.makeText(PointsStore.this,"Selected " + i +", "+ j, Toast.LENGTH_SHORT).show();
    }

    //Intent to reach the shop activity.
    public static Intent makeIntent(Context c){
        return new Intent(c, PointsStore.class);
    }
}
