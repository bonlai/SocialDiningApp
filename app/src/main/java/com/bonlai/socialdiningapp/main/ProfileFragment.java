package com.bonlai.socialdiningapp.main;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.profileEdit.EditBioActivity;
import com.squareup.picasso.Picasso;

import java.io.File;

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
    private FloatingActionButton mEditButton;
    private Button mLogout;
    private RelativeLayout mBioHolder;

    public static final int PICK_IMAGE = 100;

    private int myUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myUserId=MyUserHolder.getInstance().getUser().getPk();
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


        initUI(rootView);

        updateProfile();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity mainActivity = (AppCompatActivity) getActivity();
        mainActivity.getSupportActionBar().hide();
        updateProfile();
    }

    private void updateProfile(){
        Profile myProfile=MyUserHolder.getInstance().getUser().getProfile();
        String imgPath=myProfile.getImage();
        Picasso.with(getActivity()).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mProfilePic);
        mBio.setText(myProfile.getSelfIntroduction());
    }

    private void initUI(View rootView ){
        mProfilePic=(ImageView) rootView.findViewById(R.id.profile_pic);
        mBio=(TextView)rootView.findViewById(R.id.bioText);
        mEditButton=(FloatingActionButton)rootView.findViewById(R.id.edit_pic);
        mLogout=(Button)rootView.findViewById(R.id.logout);
        mBioHolder = (RelativeLayout) rootView.findViewById(R.id.bioHolder);

        mEditButton.setOnClickListener(this);
        mBioHolder.setOnClickListener(this);
        mLogout.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //((AppCompatActivity)getActivity()).getSupportActionBar().show();
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

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);

            APIclient.APIService service=APIclient.getAPIService();
            Call<ResponseBody> req = service.postImage(body,myUserId);

            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.v("Upload", "success");

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
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

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.bioHolder:
                intent = new Intent(getContext(), EditBioActivity.class);
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

                Token.getToken().setKey(null);
                APIclient.reset();

                intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
                break;

        }
    }
}
