package com.bonlai.socialdiningapp.detail.profileEdit;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bonlai.socialdiningapp.R;


/**
 * Created by Bon Lai on 25/1/2018.
 */

public class GenderDialogFragment extends DialogFragment {
    public interface Callback {
        void onGenderSelect(String Gender);
    }

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private Callback callback;

    public static GenderDialogFragment newInstance(int radius) {
        GenderDialogFragment frag = new GenderDialogFragment();
        Bundle args = new Bundle();
        args.putInt("radius", radius);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_gender, null);
        callback =(Callback) getTargetFragment();
        builder.setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Set positive button","OK");
                        if (callback != null) {
                            radioGroup = (RadioGroup) view.findViewById(R.id.radio);
                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            radioButton = (RadioButton) view.findViewById(selectedId);
                            Log.d("check stirng",radioButton.getText().toString());
                            callback.onGenderSelect(radioButton.getText().toString());
                        }
                    }
                })
        ;
        return builder.create();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
    }
}
