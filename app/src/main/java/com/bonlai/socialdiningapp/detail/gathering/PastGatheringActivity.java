package com.bonlai.socialdiningapp.detail.gathering;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.main.GatheringFragment;

public class PastGatheringActivity extends AppCompatActivity {
    Fragment fragment ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_gathering);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentByTag("myFragmentTag");
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment =GatheringFragment.newInstance(GatheringFragment.Mode.PAST);
            ft.add(R.id.frame_container,fragment,"myFragmentTag");
            ft.commit();
        }
    }

}
