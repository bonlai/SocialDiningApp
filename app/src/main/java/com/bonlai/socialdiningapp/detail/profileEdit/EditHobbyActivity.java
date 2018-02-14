package com.bonlai.socialdiningapp.detail.profileEdit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.bonlai.socialdiningapp.network.APIclient;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.Interest;
import com.bonlai.socialdiningapp.network.AuthAPIclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditHobbyActivity extends AppCompatActivity {
    private MultiAutoCompleteTextView mTextView;
    private List<Interest> mInterest;
    String [] choices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hobby);
        mTextView = (MultiAutoCompleteTextView) findViewById(R.id.hobbies);
        mTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        getInterestList();
    }

    private void getInterestList(){
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<List<Interest>> req = service.getInterestList();
        req.enqueue(new Callback<List<Interest>>() {
            @Override
            public void onResponse(Call<List<Interest>> call, Response<List<Interest>> response) {
                if(response.isSuccessful()){
                    mInterest=response.body();
                    choices=new String[mInterest.size()];
                    int index=0;
                    for(Interest interest:mInterest){

                        choices[index++]=interest.getName();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditHobbyActivity.this,android.R.layout.simple_list_item_1,choices);
                    mTextView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Interest>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}

