package com.bonlai.socialdiningapp.detail.profileEdit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.network.APIclient;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.network.AuthAPIclient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBioActivity extends AppCompatActivity {

    private TextView mBio;
    private Button mSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bio);

        mBio=(TextView)findViewById(R.id.bio);
        mSave=(Button)findViewById(R.id.save);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBio.setError(null);
                String bio=mBio.getText().toString();
                if (bio.length() < 4) {
                    //Log.v("CHECKING", "success");
                    mBio.setError("Write something pls<3");
                    mBio.requestFocus();
                }else{
                    AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
                    Profile myProfile= MyUserHolder.getInstance().getUser().getProfile();
                    int myUserId= MyUserHolder.getInstance().getUser().getPk();
                    Log.d("BIO",bio);
                    myProfile.setSelfIntroduction(bio);
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
        });
    }
}
