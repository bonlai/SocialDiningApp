package com.bonlai.socialdiningapp.main;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bonlai.socialdiningapp.APIclient;
import com.bonlai.socialdiningapp.LoginActivity;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.detail.profileEdit.EditDOBActivity;
import com.bonlai.socialdiningapp.detail.profileEdit.EditHobbyActivity;
import com.bonlai.socialdiningapp.detail.profileEdit.OtherProfileActivity;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.detail.profileEdit.EditBioActivity;
import com.bonlai.socialdiningapp.models.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bonlai.socialdiningapp.LoginActivity.SETTING_INFOS;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView mProfilePic;
    private TextView mBio;
    private TextView mDOB;
    private TextView mGender;
    private FloatingActionButton mEditButton;
    private Button mLogout;
    private RelativeLayout mBioHolder;
    private RelativeLayout mHobbyHolder;
    private RelativeLayout mDOBHolder;


    public static final int PICK_IMAGE = 100;
    private int myUserId;
    private ProfileMode mMode;

    private Profile mProfile;

    private static final String ARG_MODE = "mode";

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
        if (getArguments() != null) {
            mMode = (ProfileMode)getArguments().getSerializable(ARG_MODE);
        }

        if (getActivity() instanceof OtherProfileActivity) {
            int userId = getActivity().getIntent().getExtras().getInt("userId");
            getOtherUserProfile(userId);
        }else{
            getMyProfile();
        }

        isStoragePermissionGranted();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myUserId=MyUserHolder.getInstance().getUser().getPk();
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

        mEditButton=(FloatingActionButton)rootView.findViewById(R.id.edit_pic);
        mLogout=(Button)rootView.findViewById(R.id.logout);

        mBioHolder = (RelativeLayout) rootView.findViewById(R.id.bio_holder);
        mHobbyHolder = (RelativeLayout) rootView.findViewById(R.id.hobby_holder);
        mDOBHolder = (RelativeLayout) rootView.findViewById(R.id.DOB_holder);

        if(mMode==ProfileMode.MY){
            mEditButton.setOnClickListener(this);
            mBioHolder.setOnClickListener(this);
            mHobbyHolder.setOnClickListener(this);
            mDOBHolder.setOnClickListener(this);
            mLogout.setOnClickListener(this);
        }
    }

    private void updateProfile(){
        String imgPath=mProfile.getImage();
        Picasso.with(getActivity()).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mProfilePic);
        mBio.setText(mProfile.getSelfIntroduction());
        mDOB.setText(mProfile.getDob());
        mGender.setText(mProfile.getGender());
    }

    private void getOtherUserProfile(final int userId){
        APIclient.APIService service=APIclient.getAPIService();
        Call<List<User>> getOthersDetail = service.getOthersDetail(userId);
        getOthersDetail.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    Log.d("adding ",""+userId);
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
        APIclient.APIService service=APIclient.getAPIService();
        Call<Profile> getUserProfile = service.getProfile(MyUserHolder.getInstance().getUser().getPk());
        getUserProfile.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    MyUserHolder.getInstance().getUser().setProfile(response.body());
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
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            String imagePath = null;
            Uri uri = data.getData();

            if (DocumentsContract.isDocumentUri((AppCompatActivity) getActivity(), uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    //Log.d(TAG, uri.toString());
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    //Log.d(TAG, uri.toString());
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                //Log.d(TAG, "content: " + uri.toString());
                imagePath = getImagePath(uri, null);
            }
            File file = new File(imagePath);

            postImage(file);
        }
    }

    private void postImage(File file){
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);

        APIclient.APIService service=APIclient.getAPIService();
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

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Cursor cursor = activity.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            for (String str : permissions) {
                if (getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
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
                startActivity(intent);
                break;


            case R.id.DOB_holder:
                intent = new Intent(getContext(), EditDOBActivity.class);
                startActivity(intent);
                break;

            case R.id.edit_pic:
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
                break;
            case R.id.logout:
                SharedPreferences settings = getActivity().getSharedPreferences(SETTING_INFOS, 0);
                settings.edit().remove("TOKEN").commit();

                APIclient.reset();

                intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                break;

        }
    }
}
