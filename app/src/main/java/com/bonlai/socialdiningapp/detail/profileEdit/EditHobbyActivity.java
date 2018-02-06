package com.bonlai.socialdiningapp.detail.profileEdit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.bonlai.socialdiningapp.R;

public class EditHobbyActivity extends AppCompatActivity {
    private MultiAutoCompleteTextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hobby);
        mTextView = (MultiAutoCompleteTextView) findViewById(R.id.hobbies);
        String [] choices={"test","test1","hi"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,choices);
        mTextView.setAdapter(adapter);
        mTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }
}

