package com.bonlai.socialdiningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Profile;
import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.models.User;
import com.bonlai.socialdiningapp.network.APIclient;
import com.bonlai.socialdiningapp.network.AuthAPIclient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bonlai.socialdiningapp.detail.gathering.GatheringDetailActivity.FROM_NOTIFICATION;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    public static final String USER_CREDENTIAL = "USER_CREDENTIAL";
    public static final String NAME = "NAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String TOKEN = "TOKEN";
    public static SharedPreferences credential;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginProgressView;
    private View mLoadingView;
    private View mLoginFormView;
    private Button mSignInButton;
    private Button mRegister;
    private CheckBox mRememberMe;

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
        mLoginProgressView = findViewById(R.id.login_progress);
        mLoadingView= findViewById(R.id.progress_bar);
        mSignInButton = (Button) findViewById(R.id.login_button);
        mRegister= (Button) findViewById(R.id.register);
        mRememberMe = (CheckBox) findViewById(R.id.remember_me);

        mLoadingView.setVisibility(View.GONE);

        if(credential.getString(NAME, "")!=""){
            mRememberMe.setChecked(true);
        }

        mRememberMe.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(!isChecked)
                {
                    credential.edit()
                            .remove(NAME)
                            .remove(PASSWORD)
                            .commit();
                }
            }
        });

        if(mRememberMe.isChecked()){
            String name = credential.getString(NAME, "");
            String password = credential.getString(PASSWORD, "");
            //restore previous successfully login credential
            mEmailView.setText(name);
            mPasswordView.setText(password);
        }

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin(){
        mLoginProgressView.setVisibility(View.VISIBLE);
        final String username=mEmailView.getText().toString();
        final String password=mPasswordView.getText().toString();

        APIclient.APIService service=APIclient.getAPIService();
        Call<Token> login = service.login(username,password);

        login.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if(response.isSuccessful()){
                    String key=response.body().getKey();

                    if(mRememberMe.isChecked()){
                        //store credential to sharedpreferences
                        credential.edit()
                                .putString(NAME, username)
                                .putString(PASSWORD, password)
                                .putString(TOKEN, key)
                                .commit();
                    }

                    Token.setToken(response.body());
                    AuthAPIclient.setAuthToken();
                    mLoginProgressView.setVisibility(View.GONE);

                    setUserDetail();

                }else{
                    mLoginProgressView.setVisibility(View.GONE);
                    String loginFailureMsg=getString(R.string.login_failure);
                    Toast.makeText(LoginActivity.this, loginFailureMsg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                t.printStackTrace();
                toastNetworkError();
                mLoginProgressView.setVisibility(View.GONE);
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
                toastNetworkError();
                mLoginProgressView.setVisibility(View.GONE);
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
                    if(getIntent().getExtras()!=null){
                        boolean checkNoti = getIntent().getExtras().getBoolean(FROM_NOTIFICATION,false);
                            if(checkNoti){
                                finish();
                            }
                        }else{
                        goToMain();
                    }
                }
            }
            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                t.printStackTrace();
                toastNetworkError();
                mLoginProgressView.setVisibility(View.GONE);
            }
        });
    }

    private void goToMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void toastNetworkError(){
        String networkFailureMsg=getString(R.string.network_connection_problem);
        Toast.makeText(LoginActivity.this, networkFailureMsg, Toast.LENGTH_LONG).show();
    }
}

