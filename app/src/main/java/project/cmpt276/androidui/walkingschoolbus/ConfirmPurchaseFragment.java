package project.cmpt276.androidui.walkingschoolbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        getUserCurrency();
        setButtons();
        setImage(position);
        setRewardInfo();
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }


    public void setPosition(int i){
        position = i;
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
        int temp = currency;
        if(temp - 150 >= 0){
            //Checks if the user has enough currency to buy the avatar. If so, flip the switch to say it is unlocked.
            gameCollection.setAvatarUnlockStateByPos(position,true);
            user.addUserPoints(-150);
            //Updates the boolean array to the server for which avatar was unlocked.
            try{
                String customAsJson = new ObjectMapper().writeValueAsString(gameCollection);
                user.setCustomJson(customAsJson);
            }catch(JsonProcessingException e){
                e.printStackTrace();
            }
            Call<User> transactionCall = proxy.editUser(user, user.getId());
            ProxyBuilder.callProxy(v.getContext(), transactionCall, returnedUser -> Toast.makeText(v.getContext(), "Purchased!", Toast.LENGTH_SHORT).show());
            dismiss();
        }else{
            Toast.makeText(v.getContext(), "Not enough points to unlock", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserCurrency(){
        Call<User> caller = proxy.getUserById(user.getId());
        ProxyBuilder.callProxy(v.getContext(), caller, returnedUser -> serverResponse(returnedUser));
    }

    private void serverResponse(User u){
        currency = u.getCurrentPoints();
    }

}
