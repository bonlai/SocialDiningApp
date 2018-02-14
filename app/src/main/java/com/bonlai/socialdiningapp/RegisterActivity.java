package com.bonlai.socialdiningapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bonlai.socialdiningapp.models.Token;
import com.bonlai.socialdiningapp.network.APIclient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private AutoCompleteTextView mUsername;
    private EditText mPassword1;
    private EditText mPassword2;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    private void initUI(){
        mUsername = (AutoCompleteTextView) findViewById(R.id.username);
        mPassword1 = (EditText) findViewById(R.id.password1);
        mPassword2 = (EditText) findViewById(R.id.password2);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mProgressView = (View) findViewById(R.id.login_progress);
    }

    private void register(){
        mProgressView.setVisibility(View.VISIBLE);


        String username=mUsername.getText().toString();
        String password1=mPassword1.getText().toString();
        String password2=mPassword2.getText().toString();

        if (!password1.equals(password2)){
            mProgressView.setVisibility(View.GONE);
            Toast toast = Toast.makeText(RegisterActivity.this, "Password are not matched", Toast.LENGTH_LONG);
            toast.show();
        }else{
            APIclient.APIService service=APIclient.getAPIService();
            Call<Token> register = service.register(username,password1,password2);
            register.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response) {
                    if(response.isSuccessful()){

                        mProgressView.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(RegisterActivity.this, "Registration successful ", Toast.LENGTH_LONG);
                        toast.show();
                        //jump to mainactivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else{
                        mProgressView.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(RegisterActivity.this, "Failed to register with the given credential", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                @Override
                public void onFailure(Call<Token> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}
