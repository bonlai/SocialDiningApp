package com.bonlai.socialdiningapp.detail.profileEdit;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.Interest;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.network.AuthAPIclient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditHobbyActivity extends AppCompatActivity {
    private MultiAutoCompleteTextView mTextView;
    private List<Interest> mInterestList;
    private List<Interest> mMyInterestList;
    LinearLayout mList;
    Button mSave;
    CheckBox[] checkbox;
    private int checkedCount = 0;
    private final int maxLimit=3;
    ArrayList<String> selected =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hobby);

        initUI();

        getAllInterestList();
    }
    private void initUI() {
        //mGridView= (GridView) findViewById(R.id.districts_list);
        mList =  (LinearLayout) findViewById(R.id.interests_list);
        mSave=findViewById(R.id.save);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMyInterest();
            }
        });
    }


    private void addInterestList() {
        int index=0;
        for(Interest interest:mInterestList){
            CheckBox cb = new CheckBox(this);
            cb.setOnCheckedChangeListener(checker);
            if(mMyInterestList.contains(interest)){
                cb.setChecked(true);
                selected.add(interest.getName());
            }
            cb.setText(interest.getName());
            checkbox[index++] = cb;
            cb.setId(interest.getId());
            mList.addView(cb);
        }
    }

    private void getAllInterestList(){
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<List<Interest>> req = service.getInterestList();
        req.enqueue(new Callback<List<Interest>>() {
            @Override
            public void onResponse(Call<List<Interest>> call, Response<List<Interest>> response) {
                if(response.isSuccessful()){
                    mInterestList =response.body();
                    //choices=new String[mInterestList.size()];
                    checkbox=new CheckBox[mInterestList.size()];
                    int index=0;
                    /*for(Interest interest: mInterestList){
                        choices[index++]=interest.getName();
                    }*/
                    getMyInterestList();
                }

            }
            @Override
            public void onFailure(Call<List<Interest>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getMyInterestList(){
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<List<Interest>> req = service.getMyInterestList(MyUserHolder.getInstance().getUser().getPk());
        req.enqueue(new Callback<List<Interest>>() {
            @Override
            public void onResponse(Call<List<Interest>> call, Response<List<Interest>> response) {
                if(response.isSuccessful()) {
                    mMyInterestList = response.body();
                    addInterestList();
                }
            }
            @Override
            public void onFailure(Call<List<Interest>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void clearMyInterest(){
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<ResponseBody> req = service.clearInterest(MyUserHolder.getInstance().getUser().getPk());
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    if(!selected.isEmpty()){
                        for(String interest: selected){
                            postInterest(interest);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void postInterest(String interest){
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<ResponseBody> req = service.postInterest(interest);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",selected);
                    setResult(Activity.RESULT_OK,returnIntent);
                    EditHobbyActivity.this.finish();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private CheckBox.OnCheckedChangeListener checker = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton cb, boolean b) {
            if (checkedCount == maxLimit && b) {
                cb.setChecked(false);
                Toast.makeText(getApplicationContext(),
                        "Limit reached!!!", Toast.LENGTH_SHORT).show();
            } else if (b) {
                CharSequence myCheck = cb.getText();
                checkedCount++;
                //selected[selected.length-1]=myCheck.toString();
                selected.add(myCheck.toString());
                //Toast.makeText(getApplicationContext(),myCheck + " checked!",Toast.LENGTH_SHORT).show();
            } else if (!b) {
                selected.remove(cb.getText().toString());
                checkedCount--;
            }
        };
    };
}

