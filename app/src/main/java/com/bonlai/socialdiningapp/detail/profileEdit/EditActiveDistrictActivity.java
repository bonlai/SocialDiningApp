package com.bonlai.socialdiningapp.detail.profileEdit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.network.AuthAPIclient;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditActiveDistrictActivity extends AppCompatActivity {
    LinearLayout mList;
    final CheckBox[] checkboxs = new CheckBox[18];
    private int checkedCount = 0;
    private final int maxLimit=1;
    private Button mSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_active_district);

        initUI();
        addDistrictsList();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedCount!=0){
                    String activeLocation=new String();
                    for(CheckBox checkbox: checkboxs){
                        if(checkbox.isChecked()){
                            activeLocation=checkbox.getText().toString();
                        }
                    }
                    AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
                    Profile myProfile= MyUserHolder.getInstance().getUser().getProfile();
                    int myUserId= MyUserHolder.getInstance().getUser().getPk();
                    myProfile.setLocation(activeLocation);
                    Call<Profile> req = service.editProfile(myUserId,myProfile);
                    req.enqueue(new Callback<Profile>() {
                        @Override
                        public void onResponse(Call<Profile> call, Response<Profile> response) {
                            MyUserHolder.getInstance().getUser().setProfile(response.body());
                            Toast toast = Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG);
                            toast.show();
                            finish();
                        }
                        @Override
                        public void onFailure(Call<Profile> call, Throwable t) {
                            t.printStackTrace();
                            Toast toast = Toast.makeText(getApplicationContext(), "Some problem occur! Please try again later.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Please at least choose one district",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void addDistrictsList() {
        List<String> districts = Arrays.asList(getResources().getStringArray(R.array.districts));
        int idIndex = 0;
        for (String district : districts) {
            CheckBox cb = new CheckBox(this);
            cb.setOnCheckedChangeListener(checker);
            cb.setText(district);
            checkboxs[idIndex] = cb;
            cb.setId(idIndex++);
            mList.addView(cb);
        }
    }

    private void initUI() {
        mList  = (LinearLayout) findViewById(R.id.districts_list);
        mSave = (Button) findViewById(R.id.save);
    }

    private CheckBox.OnCheckedChangeListener checker = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton cb, boolean b) {
            if (checkedCount == maxLimit && b) {
                cb.setChecked(false);
                Toast.makeText(getApplicationContext(),
                        "Limit reached!", Toast.LENGTH_SHORT).show();
            } else if (b) {
                checkedCount++;
                CharSequence myCheck = cb.getText();
            } else if (!b) {
                checkedCount--;
            }
        };
    };
}
