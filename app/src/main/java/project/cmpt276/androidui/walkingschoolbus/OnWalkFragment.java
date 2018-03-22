package project.cmpt276.androidui.walkingschoolbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by Jerry on 3/21/2018.
 */

public class OnWalkFragment extends AppCompatDialogFragment {
    private View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        v = LayoutInflater.from(getActivity()).inflate(R.layout.on_walk_fragment, null);
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }

    private void setButtons(){
        Button startTracking = v.findViewById(R.id.startTrack);
        Button stopTracking = v.findViewById(R.id.stopTrack);

        //Starts the timer to upload every 30s, and stops the timer after 10 mins when in range.
        startTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //Manually stop tracking if you don't want it to automatically stop tracking.
        stopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


}
