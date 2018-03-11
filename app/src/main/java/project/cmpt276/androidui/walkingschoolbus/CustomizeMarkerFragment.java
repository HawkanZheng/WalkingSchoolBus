package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.*;

public class CustomizeMarkerFragment extends AppCompatDialogFragment {

    private GoogleMap map;
    private GoogleMapsInterface gMapsOption;
    private LatLng coordinates;
    private EditText nameForm;
    private float type;

    private float[] colors = {HUE_RED, HUE_BLUE, HUE_GREEN};
    private String[] parallelTitle = {"Red", "Blue","Green"};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create the view to show.
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.marker_creation, null);
        gMapsOption = GoogleMapsInterface.getInstance(v.getContext());
        nameForm = v.findViewById(R.id.titleEntry);
        Button confirm = v.findViewById(R.id.confirmBtn);
        Button cancel = v.findViewById(R.id.cancelBtn);
        setUpTypeGroups(v);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String title = nameForm.getText().toString();
                map.addMarker(gMapsOption.makeMarker(coordinates,type,title));
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Dialog d = new Dialog(getActivity(),0);
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    public void setMap(GoogleMap gmap, LatLng location){
        map = gmap;
        coordinates = location;
    }

    private void setUpTypeGroups(View v){
        RadioGroup markerTypes = v.findViewById(R.id.typeGroups);
        for(int i = 0; i < 3; i++){
            final int finalI = i;
            RadioButton button = new RadioButton(v.getContext());
            button.setText(parallelTitle[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    type = colors[finalI];
                }
            });
            markerTypes.addView(button);
            //Default checked value
            if(Color.RED == colors[i]){
                button.setChecked(true);
            }
        }
    }

}
