package com.bonlai.socialdiningapp.detail.restaurant;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.bonlai.socialdiningapp.R;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


/**
 * Created by Bon Lai on 25/1/2018.
 */

public class ReviewDialogFragment extends DialogFragment {
    public interface Callback {
        void onClick(String comment, int rating);
    }

    private Callback callback;

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "ReviewDialogFragment");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("Create Dialog","OK");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_review, null);
        builder.setView(view)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Set positive button","OK");
                        if (callback != null) {
                            Log.d("Onclick infrag","OK");
                            EditText et_userName = (EditText) view.findViewById(R.id.comment);
                            MaterialRatingBar rating=(MaterialRatingBar) view.findViewById(R.id.rating);
                            callback.onClick(et_userName.getText().toString(),(int)(rating.getRating()));
                        }
                    }
                })
        ;
        return builder.create();
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
