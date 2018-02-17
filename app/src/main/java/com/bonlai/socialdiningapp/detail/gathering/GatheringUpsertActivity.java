package com.bonlai.socialdiningapp.detail.gathering;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;
import com.bonlai.socialdiningapp.models.Restaurant;
import com.bonlai.socialdiningapp.network.AuthAPIclient;

import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GatheringUpsertActivity extends AppCompatActivity {
    private Button dateButton;
    private TextView dateText;

    private Button timeButton;
    private TextView timeText;

    private EditText gatheringTitle;
    private EditText restaurantName;
    private EditText mDetail;

    private Button mSubmit;

    private int userID;
    private int restaurantId;
    private Gathering mGathering;

    private Mode mMode;

    public static enum Mode {
        POST,
        EDIT
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gathering);
        initUI();
        initVar();
        initAccordingMode();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                // Create a new instance of TimePickerDialog and return it
                DatePickerDialog dpDialog = new DatePickerDialog(GatheringUpsertActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateText.setText(new String(year + "-" + (monthOfYear+1) + "-" + dayOfMonth));
                    }
                }, year, month, day);
                dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dpDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                // Create a new instance of TimePickerDialog and return it
                new TimePickerDialog(GatheringUpsertActivity.this, new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeText.setText(new String(hourOfDay + ":" + minute));
                    }
                }, hour, minute, false).show();
            }
        });
    }

    private void initUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateText = (TextView)findViewById(R.id.showDate);
        dateButton = (Button)findViewById(R.id.dateButton);

        timeText = (TextView)findViewById(R.id.showTime);
        timeButton = (Button)findViewById(R.id.timeButton);

        gatheringTitle=(EditText)findViewById(R.id.gatheringTitle);
        restaurantName =(EditText)findViewById(R.id.restaurantID);
        mDetail=(EditText)findViewById(R.id.detail);
        mSubmit=(Button)findViewById(R.id.submitGathering);
    }

    private void initVar(){
        userID= MyUserHolder.getInstance().getUser().getPk();
        Intent intent;
        if((intent=getIntent())!=null){
            mMode=(Mode)intent.getSerializableExtra("mode");
            switch (mMode){
                case POST:
                    restaurantId = intent.getIntExtra("restaurantId",0);
                    restaurantName.setText(getIntent().getStringExtra("restaurantName"));
                    mSubmit.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            postGathering();
                        }
                    });
                    break;
                case EDIT:
                    mGathering = (Gathering)intent.getSerializableExtra("gathering");

                    mSubmit.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            editGathering();
                        }
                    });
                    break;
            }
        }
    }

    private void initAccordingMode(){
        switch (mMode){
            case POST:
                mSubmit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        postGathering();
                    }
                });
                break;
            case EDIT:
                getRestaurantInfo();
                gatheringTitle.setText(mGathering.getName());
                mDetail.setText(mGathering.getDetail());
                String dateTime[]=mGathering.getStartDatetime().split("\\s+");
                dateText.setText(dateTime[0]);
                timeText.setText(dateTime[1]);
                break;
        }
    }
    private void getRestaurantInfo(){
        AuthAPIclient.APIService service=AuthAPIclient.getAPIService();
        Call<Restaurant> getRestaurantInfo = service.getRestaurantInfo(mGathering.getRestaurant());
        getRestaurantInfo.enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                if(response.isSuccessful()){
                    restaurantName.setText(response.body().getName());
                }
            }
            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void postGathering(){
        mDetail.setError(null);
        //View focusView = null;
        boolean isValidated=validation();
        if(!isValidated){

            Log.v("CHECKING", "if statement");
        }else{
            String dateTime=dateText.getText().toString()+" "+timeText.getText().toString();

            mGathering=new Gathering();
            mGathering.setName(gatheringTitle.getText().toString());
            mGathering.setStartDatetime(dateTime);
            mGathering.setdetail(mDetail.getText().toString());
            mGathering.setIsStart(false);
            mGathering.setCreatedBy(userID);
            mGathering.setRestaurant(restaurantId);

            AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
            Call<ResponseBody> req = service.createGathering(mGathering);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Log.v("Upload", "success");
                        Toast.makeText(getApplicationContext(), "Created gathering!", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Please fill in necessary information.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), "Some problem occur! Please try again later.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }

    public void editGathering(){
        mDetail.setError(null);
        //View focusView = null;
        boolean isValidated=validation();
        if(!isValidated){

            Log.v("CHECKING", "if statement");
        }else{
            String dateTime=dateText.getText().toString()+" "+timeText.getText().toString();;

            mGathering.setName(gatheringTitle.getText().toString());
            mGathering.setStartDatetime(dateTime);
            mGathering.setdetail(mDetail.getText().toString());

            AuthAPIclient.APIService service= AuthAPIclient.getAPIService();
            Call<Gathering> req = service.putGathering(mGathering.getId(),mGathering);
            req.enqueue(new Callback<Gathering>() {
                @Override
                public void onResponse(Call<Gathering> call, Response<Gathering> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Edited gathering!", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "Please fill in necessary information.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Gathering> call, Throwable t) {
                    t.printStackTrace();
                    Toast toast = Toast.makeText(getApplicationContext(), "Some problem occur! Please try again later.", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }
    }

    public boolean validation(){
        View focusView = null;
        String detail=mDetail.getText().toString();
        if (!isWritten(detail)) {
            //Log.v("CHECKING", "success");
            mDetail.setError("Write something pls<3");
            focusView= mDetail;
            return false;
        }

        if(focusView!=null){
            focusView.requestFocus();
        }
        return true;
    }

    private boolean isWritten(String detail) {
        return detail.length() > 4;
    }



}
