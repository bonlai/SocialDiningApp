package com.bonlai.socialdiningapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import is.arontibo.library.ElasticDownloadView;
import android.content.Intent;
import android.util.Log;

public class TestActivity extends AppCompatActivity {
    ElasticDownloadView mElasticDownloadView;
    private static final String LOG_TAG = TestActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
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


        mElasticDownloadView=(ElasticDownloadView) findViewById(R.id.elastic_download_view);
        mElasticDownloadView.startIntro();
        mElasticDownloadView.setProgress(25);
        Log.d(LOG_TAG, "onCreate");
    }

    public void showToast(View view) {
        // Create a toast show it.
        Toast toast = Toast.makeText(this, "Testing toast 2", Toast.LENGTH_LONG);
        toast.show();
        mElasticDownloadView.success();
    }
    public void restart(View view) {
        // Create a toast show it.

        mElasticDownloadView.fail();
        Intent intent = new Intent(this, ImageUploadActivity.class);
        startActivity(intent);
    }

    public void nextActivity(View view){
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }
}
