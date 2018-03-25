package com.bonlai.socialdiningapp.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bonlai.socialdiningapp.detail.gathering.PastGatheringActivity;
import com.bonlai.socialdiningapp.detail.profileEdit.GenderDialogFragment;
import com.bonlai.socialdiningapp.models.Interest;
import com.bonlai.socialdiningapp.network.AuthAPIclient;
import com.bonlai.socialdiningapp.LoginActivity;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.detail.profileEdit.EditActiveDistrictActivity;
import com.bonlai.socialdiningapp.detail.profileEdit.EditDOBActivity;
import com.bonlai.socialdiningapp.detail.profileEdit.EditHobbyActivity;
import com.bonlai.socialdiningapp.detail.profileEdit.OtherProfileActivity;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.detail.profileEdit.EditBioActivity;
import com.bonlai.socialdiningapp.models.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.bonlai.socialdiningapp.LoginActivity.USER_CREDENTIAL;

public class ProfileFragment extends Fragment implements View.OnClickListener
        ,GenderDialogFragment.Callback {

    private ImageView mProfilePic;
    private TextView mUsername;
    private TextView mBio;
    private TextView mDOB;
    private TextView mGender;
    private TextView mInterest;
    private TextView mDistrict;
    private FloatingActionButton mEditButton;
    private Button mLogout;
    private RelativeLayout mBioHolder;
    private RelativeLayout mHobbyHolder;
    private RelativeLayout mDOBHolder;
    private RelativeLayout mDistrictsHolder;
    private RelativeLayout mGenderHolder;

    private int myUserId;
    private ProfileMode mMode;

    private Profile mProfile;
    private User mUser;

    private static final String ARG_MODE = "mode";

    final int INTEREST_EDIT = 100;

    @Override
    public void onGenderSelect(String Gender) {
        //Toast.makeText(getContext(),Gender,Toast.LENGTH_SHORT).show();
        //Log.d("check stirng",Gender);

        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Profile myProfile= MyUserHolder.getInstance().getUser().getProfile();

        myProfile.setGender(Gender);
        Call<Profile> req = service.editProfile(myUserId,myProfile);
        req.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                Log.v("Upload", "success");
                MyUserHolder.getInstance().getUser().setProfile(response.body());
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_LONG).show();
                updateProfile();
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    public static enum ProfileMode {
        OTHER,
        MY
    }

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(ProfileMode mode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_MODE, mode);

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myUserId=MyUserHolder.getInstance().getUser().getPk();
        if (getArguments() != null) {
            mMode = (ProfileMode)getArguments().getSerializable(ARG_MODE);
        }

        if (getActivity() instanceof OtherProfileActivity) {
            int userId = getActivity().getIntent().getExtras().getInt("userId");
            getOtherUserProfile(userId);
            getInterestsList(userId);
        }else{
            getMyProfile();
            getInterestsList(myUserId);
        }

        isStoragePermissionGranted();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initUI(rootView);

        if(mMode!=ProfileMode.MY){
            mLogout.setVisibility(View.GONE);
            mEditButton.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateProfile();
    }

    private void initUI(View rootView ){
        mProfilePic=(ImageView) rootView.findViewById(R.id.profile_pic);
        mBio=(TextView)rootView.findViewById(R.id.bioText);
        mDOB=(TextView)rootView.findViewById(R.id.DOB);
        mGender=(TextView)rootView.findViewById(R.id.gender);
        mUsername=(TextView)rootView.findViewById(R.id.username);
        mInterest=(TextView)rootView.findViewById(R.id.interests);
        mDistrict=(TextView)rootView.findViewById(R.id.active_district);

        mEditButton=(FloatingActionButton)rootView.findViewById(R.id.edit_pic);
        mLogout=(Button)rootView.findViewById(R.id.logout);

        mBioHolder = (RelativeLayout) rootView.findViewById(R.id.bio_holder);
        mHobbyHolder = (RelativeLayout) rootView.findViewById(R.id.hobby_holder);
        mDOBHolder = (RelativeLayout) rootView.findViewById(R.id.DOB_holder);
        mDistrictsHolder = (RelativeLayout) rootView.findViewById(R.id.districts_holder);
        mGenderHolder= (RelativeLayout) rootView.findViewById(R.id.gender_holder);

        if(mMode==ProfileMode.MY){
            mEditButton.setOnClickListener(this);
            mBioHolder.setOnClickListener(this);
            mHobbyHolder.setOnClickListener(this);
            mDOBHolder.setOnClickListener(this);
            mDistrictsHolder.setOnClickListener(this);
            mGenderHolder.setOnClickListener(this);
            mLogout.setOnClickListener(this);
        }
    }

    private void updateProfile(){
        mUsername.setText(mUser.getUsername());
        String imgPath=mProfile.getImage();
        Picasso.with(getActivity()).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mProfilePic);
        mBio.setText(mProfile.getSelfIntroduction());
        mDOB.setText(mProfile.getDob());
        mGender.setText(mProfile.getGender());
        mDistrict.setText(mProfile.getLocation());
    }

    private void getInterestsList(int userId){
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<List<Interest>> req = service.getMyInterestList(userId);
        req.enqueue(new Callback<List<Interest>>() {
            @Override
            public void onResponse(Call<List<Interest>> call, Response<List<Interest>> response) {
                if(response.isSuccessful()) {
                    //mMyInterestList = response.body();
                    StringBuilder builder = new StringBuilder();
                    boolean firstItem=true;
                    for(Interest interest:response.body()){
                        if(firstItem){
                            firstItem=false;
                        }else{
                            builder.append(", ");
                        }
                        builder.append(interest.getName());
                    }
                    mInterest.setText(builder.toString());
                }
            }
            @Override
            public void onFailure(Call<List<Interest>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getOtherUserProfile(final int userId){
        AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
        Call<List<User>> getOthersDetail = service.getOthersDetail(userId);
        getOthersDetail.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    Log.d("adding ",""+userId);
                    mUser=response.body().get(0);
                    mProfile=response.body().get(0).getProfile();
                    updateProfile();
                }else{

                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void getMyProfile(){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<Profile> getUserProfile = service.getProfile(MyUserHolder.getInstance().getUser().getPk());
        getUserProfile.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    MyUserHolder.getInstance().getUser().setProfile(response.body());
                    mUser=MyUserHolder.getInstance().getUser();
                    mProfile=response.body();
                    updateProfile();
                }
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File imageFile = new File(resultUri.getPath());
                postImage(imageFile);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == INTEREST_EDIT) {
            if(resultCode == Activity.RESULT_OK){
                getInterestsList(myUserId);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void postImage(File file){
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);

        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<ResponseBody> req = service.postImage(body,myUserId);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.v("Upload", "success");
                getMyProfile();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("Permission","called");
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
/*            for (String str : permissions) {
                if (getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }*/
            ActivityCompat.requestPermissions(getActivity(),permissions,REQUEST_CODE_CONTACT);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.bio_holder:
                intent = new Intent(getContext(), EditBioActivity.class);
                startActivity(intent);
                break;

            case R.id.hobby_holder:
                intent = new Intent(getContext(), EditHobbyActivity.class);
                startActivityForResult(intent,INTEREST_EDIT);
                //startActivity(intent);
                break;


            case R.id.DOB_holder:
                intent = new Intent(getContext(), EditDOBActivity.class);
                startActivity(intent);
                break;

            case R.id.gender_holder:
                GenderDialogFragment genderDialogFragment=new GenderDialogFragment();
                genderDialogFragment.setTargetFragment(this,1);
                genderDialogFragment.show(getFragmentManager().beginTransaction(), null);
                break;

            case R.id.districts_holder:
                intent = new Intent(getContext(), EditActiveDistrictActivity.class);
                startActivity(intent);
                break;

            case R.id.edit_pic:
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(getContext(), this);
                break;

            case R.id.logout:
                SharedPreferences settings = getActivity().getSharedPreferences(USER_CREDENTIAL, 0);
                settings.edit().remove("TOKEN").commit();
                Token.getToken().setKey(null);

                //APIclient.reset();

                intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                break;

        }
    }
}
