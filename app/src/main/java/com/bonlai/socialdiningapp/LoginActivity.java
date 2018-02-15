package com.bonlai.socialdiningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.models.User;
import com.bonlai.socialdiningapp.network.APIclient;
import com.bonlai.socialdiningapp.network.AuthAPIclient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    public static final String USER_CREDENTIAL = "USER_CREDENTIAL";
    public static final String NAME = "NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String TOKEN = "TOKEN";
    public static SharedPreferences credential;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoadingView;
    private View mLoginFormView;
    private Button mSignInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        credential = getSharedPreferences(USER_CREDENTIAL, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        checkTokenExist();
    }

    private void checkTokenExist(){
        if(credential.contains(TOKEN)){
            //Log.d("preference","token set"+Token.getToken().getKey());
            String key = credential.getString(TOKEN, "");
            //Log.d("preference","token setting"+json);
            //Log.d("preference","token set");
            Token.getToken().setKey(key);
            AuthAPIclient.setAuthToken();
            mLoadingView.setVisibility(View.VISIBLE);
            mLoginFormView.setVisibility(View.GONE);
            setUserDetail();
        }
    }

    private void initUI(){
        mEmailView = (AutoCompleteTextView) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mLoadingView= findViewById(R.id.progress_bar);
        mSignInButton = (Button) findViewById(R.id.login_button);

        mLoadingView.setVisibility(View.GONE);
        //restore previous successfully login credential
        String name = credential.getString(NAME, "");
        String password = credential.getString(PASSWORD, "");
        mEmailView.setText(name);
        mPasswordView.setText(password);

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin(){
        mProgressView.setVisibility(View.VISIBLE);
        final String username=mEmailView.getText().toString();
        final String password=mPasswordView.getText().toString();

        APIclient.APIService service=APIclient.getAPIService();
        Call<Token> login = service.login(username,password);

        login.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){
                    String key=response.body().getKey();
                    //store credential to sharedpreferences
                    SharedPreferences settings = getSharedPreferences(USER_CREDENTIAL, 0);
                    settings.edit()
                            .putString(NAME, username)
                            .putString(PASSWORD, password)
                            .putString(TOKEN, key)
                            .commit();

                    Token.setToken(response.body());
                    AuthAPIclient.setAuthToken();
                    mProgressView.setVisibility(View.GONE);

                    setUserDetail();

                }else{
                    mProgressView.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(LoginActivity.this, "Failed to login with the given credential", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    private void setUserDetail(){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<User> req = service.getMyDetail();
        req.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    MyUserHolder.getInstance().setUser(response.body());
                    setProfile();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, "Network problem. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setProfile(){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<Profile> getUserProfile = service.getProfile(MyUserHolder.getInstance().getUser().getPk());
        getUserProfile.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    MyUserHolder.getInstance().getUser().setProfile(response.body());
                    goToMain();
                }
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void goToMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void goToRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}

