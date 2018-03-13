package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
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

    private final float[] PIN_COLORS = {HUE_RED, HUE_BLUE};
    private final String[] PIN_OPTIONS = {"Start", "Finish"};
    private final int NUM_PIN_OPTIONS = 2;
    private RadioButton[] buttons = new RadioButton[NUM_PIN_OPTIONS];

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create the view to show.
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.marker_creation, null);
        gMapsOption = GoogleMapsInterface.getInstance(v.getContext());
        nameForm = v.findViewById(R.id.titleEntry);
        Button confirm = v.findViewById(R.id.confirmBtn);
        Button cancel = v.findViewById(R.id.cancelBtn);
        setUpTypeGroups(v);

        confirm.setOnClickListener(view -> manageMarkers());
        cancel.setOnClickListener(view -> dismiss());
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    //Function to pass the map to this fragment.
    public void setMap(GoogleMap gmap, LatLng location){
        map = gmap;
        coordinates = location;
    }

    //Manages markers on the map so that only two exist at a time.
    private void manageMarkers(){
        //Name the marker... may need error checking.
        String title = nameForm.getText().toString();
        //Create marker options.
        MarkerOptions options = gMapsOption.makeMarker(coordinates,type,title);
        //Manage start markers
        if(buttons[0].isChecked()){
            if(fragmentData.getStartMarker() != null){
                //Removes an existing start marker if there is one...
                Marker m = fragmentData.getStartMarker();
                m.remove();
            }
            //Place a marker
            Marker m = map.addMarker(options);
            fragmentData.storeStartMarker(m);
        }
        //Manage finish markers
        else{
            if(fragmentData.getEndMarker() != null){
                //Removes an existing start marker if there is one...
                Marker m = fragmentData.getEndMarker();
                m.remove();
            }
            //Place a marker
            Marker m = map.addMarker(options);
            fragmentData.storeEndMarker(m);
        }
        dismiss();
    }

    private void setUpTypeGroups(View v){
        RadioGroup markerTypes = v.findViewById(R.id.typeGroups);
        for(int i = 0; i < NUM_PIN_OPTIONS; i++){
            final int refIndex = i;
            RadioButton button = new RadioButton(v.getContext());
            button.setText(PIN_OPTIONS[i]);
            button.setOnClickListener(view -> type = PIN_COLORS[refIndex]);
            markerTypes.addView(button);
            buttons[i] = button;
            //Default checked value is "Start"
            if(buttons[i].getText() == PIN_OPTIONS[0]){
                buttons[i].setChecked(true);
            }
        }
    }

    private void clearAllMarkersFromMap(){
        fragmentData.getEndMarker().remove();
        fragmentData.getStartMarker().remove();

        fragmentData.storeStartMarker(null);
        fragmentData.storeEndMarker(null);
    }


}
