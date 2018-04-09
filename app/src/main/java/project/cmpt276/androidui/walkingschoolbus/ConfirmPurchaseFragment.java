package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import project.cmpt276.model.walkingschoolbus.GamificationCollection;
import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;
import project.cmpt276.model.walkingschoolbus.SharedValues;
import project.cmpt276.model.walkingschoolbus.User;
import project.cmpt276.model.walkingschoolbus.fragmentDataCollection;
import project.cmpt276.server.walkingschoolbus.ProxyBuilder;
import project.cmpt276.server.walkingschoolbus.WGServerProxy;
import retrofit2.Call;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;

public class ConfirmPurchaseFragment extends AppCompatDialogFragment {

    int position;
    private int imgW = 90;
    private int imgH = 250;
    private int currency;
    private GamificationCollection gameCollection;
    private User user;
    private WGServerProxy proxy;
    private SharedValues sharedValues;
    View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create the view to show.
        v = LayoutInflater.from(getActivity()).inflate(R.layout.purchase_confirmation, null);
        gameCollection = GamificationCollection.getInstance();
        sharedValues = SharedValues.getInstance();
        user = User.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apiKey), sharedValues.getToken());
        setButtons();
        setImage(position);
        setRewardInfo();
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    public void setPosition(int i, int j){
        //Converts the 2d array index to a 1d array index.
        position = (PointsStore.getNumColRewards() * i) + j;
    }

    private void setRewardInfo(){
        //Needs to grab the reward's name and price
        int price = 150;
        TextView rewardName = v.findViewById(R.id.rewardName);
        TextView rewardPrice = v.findViewById(R.id.rewardPrice);
        rewardName.setText(gameCollection.getAvatarAtPostion(position));
        rewardPrice.setText("Costs: " + price);
    }

    private void setImage(int i){
        Resources res = getResources();
        TypedArray imgArr = res.obtainTypedArray(R.array.avatars);
        ImageView iv = v.findViewById(R.id.rewardPreview);
        iv.setBackground(imgArr.getDrawable(i));
        Log.i("PointsStore","Index: " + position);
    }

    private void setButtons(){
        Button confirmBtn = v.findViewById(R.id.confirmPurchase);
        Button cancelBtn = v.findViewById(R.id.cancelPurchase);
        confirmBtn.setOnClickListener(v -> purchaseLogic());
        cancelBtn.setOnClickListener(v-> dismiss());
    }

    private void purchaseLogic(){
        Call<User> caller = proxy.getUserById(user.getId());
        ProxyBuilder.callProxy(v.getContext(), caller, returnedUser -> serverResponse(returnedUser));
        int temp = currency;
        if(temp - 150 >= 0){
            //Checks if the user has enough currency to buy the avatar. If so, flip the switch to say it is unlocked.
            gameCollection.setAvatarUnlockStateByPos(position,true);
            Call<User> transactionCall = proxy.editUser(user, user.getId());
            ProxyBuilder.callProxy(v.getContext(), transactionCall, returnedUser -> returnedUser.addUserPoints(-150));
        }else{
            Toast.makeText(v.getContext(), "Not enough points to unlock", Toast.LENGTH_SHORT).show();
        }
    }

    private void serverResponse(User u){
        currency = u.getCurrentPoints();
    }

}
