package com.bonlai.socialdiningapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RestaurantSearchActivity extends AppCompatActivity {

    public static final String RESTAURANT_NAME="RESTAURANT_NAME";
    public static final String CATEGORY="CATEGORY";
    public static final String ADDRESS="ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        final TextView mRestaurantName=findViewById(R.id.restaurant_name);
        final TextView mCategory=findViewById(R.id.category);
        final TextView mAddress=findViewById(R.id.address);

        Button mSearch = (Button) findViewById(R.id.search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESTAURANT_NAME,mRestaurantName.getText().toString());
                returnIntent.putExtra(CATEGORY,mCategory.getText().toString());
                returnIntent.putExtra(ADDRESS,mAddress.getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
               /* String query=mRestaurantName.getText().toString()+","+
                                mCategory.getText().toString()+","+
                                mAddress.getText().toString();
                RestaurantFragment myf = RestaurantFragment.newInstance(query);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.profileframe, myf);
                transaction.commit();*/

            }
        });

    }


}
