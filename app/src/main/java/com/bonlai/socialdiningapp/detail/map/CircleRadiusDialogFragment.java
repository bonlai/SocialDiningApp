package com.bonlai.socialdiningapp.detail.map;


import android.app.Activity;
import android.app.Dialog;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bonlai.socialdiningapp.R;
import com.google.android.gms.maps.SupportMapFragment;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


/**
 * Created by Bon Lai on 25/1/2018.
 */

public class CircleRadiusDialogFragment extends DialogFragment {
    public interface Callback {
        void onSeekbarChange(int radius);
    }

    private Callback callback;

    public static CircleRadiusDialogFragment newInstance(int radius) {
        CircleRadiusDialogFragment frag = new CircleRadiusDialogFragment();
        Bundle args = new Bundle();
        args.putInt("radius", radius);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int radius = getArguments().getInt("radius");

        Log.d("Create Dialog","OK");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_circle_radius, null);
        builder.setView(view);

        Dialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        final TextView radiusIndicater=view.findViewById(R.id.radius_indicater);
        radiusIndicater.setText(radius/1000+ "km");

        SeekBar seekBar=view.findViewById(R.id.seekBar);
        seekBar.setProgress((radius-500)/100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // use progress set map zoom level
                // update map zoom level here
                if (callback != null) {
                    Log.d("Onclick infrag","OK");
                    float radiusValue=((float)progress*100+500)/1000;
                    radiusIndicater.setText(String.format("%.1f km", radiusValue));
                    callback.onSeekbarChange(progress);
                }
            }
        });

        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
            }
        });
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("onAttach","OK");
        if (activity instanceof Callback) {
            Log.d("Attach","OK");
            callback = (Callback) activity;
        } else {
            throw new RuntimeException(activity.toString() + " must implement Callback");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
    }
}
