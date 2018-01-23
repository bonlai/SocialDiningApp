package com.bonlai.socialdiningapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.bonlai.socialdiningapp.models.Review;
import com.bonlai.socialdiningapp.models.Token;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        int restaurantId = getIntent().getExtras().getInt("restaurantId");
        getReview(restaurantId);
    }

    private void getReview(int restaurantId){
        APIclient.APIService service=APIclient.getAPIService();
        Call<List<Review>> getReview = service.getReview(restaurantId);
        getReview.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if(response.isSuccessful()){
                }
            }
            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
