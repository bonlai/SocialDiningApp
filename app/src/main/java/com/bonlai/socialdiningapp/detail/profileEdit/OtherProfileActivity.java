package com.bonlai.socialdiningapp.detail.profileEdit;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.main.ProfileFragment;

public class OtherProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        ProfileFragment myf = ProfileFragment.newInstance(ProfileFragment.ProfileMode.OTHER);
        int userId = getIntent().getExtras().getInt("userId");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.profileframe, myf);
        transaction.commit();
    }
}
