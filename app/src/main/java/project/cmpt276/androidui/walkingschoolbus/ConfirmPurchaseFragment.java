package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;
import project.cmpt276.model.walkingschoolbus.fragmentDataCollection;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

public class ConfirmPurchaseFragment extends AppCompatDialogFragment {

    int position;
    private int imgW = 90;
    private int imgH = 250;
    View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create the view to show.
        v = LayoutInflater.from(getActivity()).inflate(R.layout.purchase_confirmation, null);
        setButtons();
        setImage(position);
        setRewardInfo();
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    public void setPosition(int i, int j){
        //Converts the 2d array index to a 1d array index.
        position = (10 * i) + j;
    }

    private void setRewardInfo(){
        //Needs to grab the reward's name and price
        int price = 150;
        TextView rewardName = v.findViewById(R.id.rewardName);
        TextView rewardPrice = v.findViewById(R.id.rewardPrice);
        rewardName.setText("REWARD_NAME");
        rewardPrice.setText("Costs: " + price);
    }

    private void setImage(int i){
        Resources res = getResources();
        TypedArray imgArr = res.obtainTypedArray(R.array.avatars);
        Bitmap originalImage = BitmapFactory.decodeResource(getResources(),imgArr.getResourceId(i,0));
        Bitmap scaledImage = Bitmap.createScaledBitmap(originalImage, imgW,imgH,true);
        ImageView iv = v.findViewById(R.id.rewardPreview);
        iv.setBackground(imgArr.getDrawable(i));
        Log.i("PointsStore","Index: " + position);
    }

    private void setButtons(){
        Button confirmBtn = v.findViewById(R.id.confirmPurchase);
        Button cancelBtn = v.findViewById(R.id.cancelPurchase);
        confirmBtn.setOnClickListener(v -> dismiss());
        cancelBtn.setOnClickListener(v-> dismiss());
    }
}
