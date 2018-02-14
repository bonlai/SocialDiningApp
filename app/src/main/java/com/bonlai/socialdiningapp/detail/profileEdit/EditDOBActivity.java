package com.bonlai.socialdiningapp.detail.profileEdit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bonlai.socialdiningapp.network.APIclient;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditDOBActivity extends AppCompatActivity {
    private Button saveButton;
    private DatePicker datePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dob);

        saveButton = (Button)findViewById(R.id.save);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth()+1;
                int day = datePicker.getDayOfMonth();
                String date=year + "-" + month + "-" + day;
                Toast toast = Toast.makeText(getApplicationContext(), date, Toast.LENGTH_LONG);
                toast.show();
                setDOB(date);
            }
        });
    }

    private void setDOB(String DOB){
        APIclient.APIService service=APIclient.getAPIService();
        Profile myProfile= MyUserHolder.getInstance().getUser().getProfile();
        int myUserId= MyUserHolder.getInstance().getUser().getPk();
        //Log.d("BIO",bio);
        myProfile.setDob(DOB);
        myProfile.setGender("Male");
        Call<Profile> req = service.editProfile(myUserId,myProfile);
        req.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.v("Upload", "success");
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
    }
}