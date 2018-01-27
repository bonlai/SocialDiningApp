package com.bonlai.socialdiningapp.main_page;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bonlai.socialdiningapp.APIclient;
import com.bonlai.socialdiningapp.LoginActivity;
import com.bonlai.socialdiningapp.MainActivity;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.models.User;
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

public class ProfileFragment extends Fragment {

    private ImageView mProfilePic;
    private TextView mBio;
    private FloatingActionButton mEditButton;
    private Button mLogout;

    public static final int PICK_IMAGE = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) actionBar.findViewById(R.id.toolbar);
        actionBar.setSupportActionBar(toolbar);
        actionBar.getSupportActionBar().hide();

        initUI(rootView);

        int myId=MyUserHolder.getInstance().getUser().getPk();

        APIclient.APIService service=APIclient.getAPIService();
        Call<Profile> getUserImg = service.getProfile(myId);
        getUserImg.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    //load image
                    String imgPath=response.body().getImage();
                    Picasso.with(getActivity()).load(imgPath).placeholder( R.drawable.progress_animation ).fit().centerCrop().into(mProfilePic);

                    mBio.setText(response.body().getSelfIntroduction());
                }else{

                }
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return rootView;
    }

    private void initUI(View rootView ){
        mProfilePic=(ImageView) rootView.findViewById(R.id.profile_pic);
        mBio=(TextView)rootView.findViewById(R.id.bioText);
        mEditButton=(FloatingActionButton)rootView.findViewById(R.id.edit_pic);
        mLogout=(Button)rootView.findViewById(R.id.logout);
        if (mEditButton != null) {
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
                }
            });
        }


        if (mLogout != null) {
            mLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences settings = getActivity().getSharedPreferences(SETTING_INFOS, 0);
                    settings.edit().remove("TOKEN").commit();

                    Token.getToken().setKey(null);
                    APIclient.reset();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
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

            //"/storage/emulated/0/DCIM/100ANDRO/DSC_0001.jpg"
            File file = new File(imagePath);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            //RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

            int myId=MyUserHolder.getInstance().getUser().getPk();
            APIclient.APIService service=APIclient.getAPIService();
            Call<ResponseBody> req = service.postImage(body,myId);

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
}
