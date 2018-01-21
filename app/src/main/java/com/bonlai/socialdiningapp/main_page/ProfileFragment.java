package com.bonlai.socialdiningapp.main_page;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bonlai.socialdiningapp.APIclient;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.User;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView mProfilePic;
    private TextView mBio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) actionBar.findViewById(R.id.toolbar);
        actionBar.setSupportActionBar(toolbar);
        actionBar.getSupportActionBar().hide();

        initUI(rootView);

        int myId=MyUserHolder.getInstance().getUser().getPk();

        APIclient.APIService service=APIclient.getAPIService();
        Call<Profile> getUserImg = service.getProfile(myId);
        getUserImg.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    //load image
                    String imgPath=response.body().getImage();
                    Picasso.with(getActivity()).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mProfilePic);

                    mBio.setText(response.body().getSelfIntroduction());
                }else{

                }
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return rootView;
    }

    private void initUI(View rootView ){
        mProfilePic=(ImageView) rootView.findViewById(R.id.profile_pic);
        mBio=(TextView)rootView.findViewById(R.id.bioText);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}
