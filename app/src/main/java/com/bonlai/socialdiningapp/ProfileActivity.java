package com.bonlai.socialdiningapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbar=(CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ImageUploadActivity.class);
                startActivity(intent);
            }
        });

        APIclient.APIService service=APIclient.getAPIService();
        Call<User> req = service.getMyDetail();
        req.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){

                    User user=response.body();
                    MyUserHolder.getInstance().setUser(response.body());

                    Log.v("test",""+MyUserHolder.getInstance().getUser().getPk());
                    collapsingToolbar.setTitle(response.body().getFirstName()+" "+response.body().getLastName());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
