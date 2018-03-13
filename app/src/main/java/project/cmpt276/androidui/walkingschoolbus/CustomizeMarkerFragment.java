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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import project.cmpt276.model.walkingschoolbus.GoogleMapsInterface;
import project.cmpt276.model.walkingschoolbus.fragmentDataCollection;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.*;

public class CustomizeMarkerFragment extends AppCompatDialogFragment {

    // Transfer data
    fragmentDataCollection fragmentData = fragmentDataCollection.getInstance();

    private GoogleMap map;
    private GoogleMapsInterface gMapsOption;
    private LatLng coordinates;
    private EditText nameForm;
    private float type;

    private float[] colors = {HUE_RED, HUE_BLUE};
    private String[] parallelTitle = {"Start", "Finish"};
    private RadioButton[] buttons = new RadioButton[2];

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
                MarkerOptions m = gMapsOption.makeMarker(coordinates,type,title);

                if(buttons[0].isChecked()){
                    //TODO: Find a way to remove markers from the map.

                    // If there is a start marker on the map remove it
                    if(fragmentData.getStartCount() > 0){
                        fragmentData.getStartMarker().remove();
                        fragmentData.setStartMarker(null);
                        fragmentData.decStartCount();
                        fragmentData.setMarkerTitle(null);
                    }

                    gMapsOption.addMarker(m,0);

                    // Creates, displays, and stores a marker
                    fragmentData.setStartMarker(map.addMarker(gMapsOption.getStartMarker()));
                    fragmentData.setMarkerTitle(title);

                }else{

                    if(fragmentData.getEndCount() > 0){
                        fragmentData.getEndMarker().remove();
                        fragmentData.setEndMarker(null);
                        fragmentData.decEndCount();
                    }
                    gMapsOption.addMarker(m,1);

                    // Creates, displays, and stores a marker
                    fragmentData.setEndMarker(map.addMarker(gMapsOption.getFinishMarker()));

                }

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
        for(int i = 0; i < 2; i++){
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
            buttons[i] = button;
            //Default checked value
            if(Color.RED == colors[i]){
                button.setChecked(true);
            }
        }
    }

    private void clearAllMarkersFromMap(){
        fragmentData.getEndMarker().remove();
        fragmentData.getStartMarker().remove();

        fragmentData.setStartMarker(null);
        fragmentData.setEndMarker(null);
    }


}
