package com.bonlai.socialdiningapp.detail.gathering;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bonlai.socialdiningapp.network.APIclient;
import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.models.Gathering;
import com.bonlai.socialdiningapp.models.MyUserHolder;

import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGatheringActivity extends AppCompatActivity {
    private Button dateButton;
    private TextView dateText;

    private Button timeButton;
    private TextView timeText;

    private EditText gatheringTitle;
    private EditText restaurantID;
    private EditText mDetail;

    private int userID;
    private int restaurantId;

    private String date;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID= MyUserHolder.getInstance().getUser().getPk();
        setContentView(R.layout.activity_new_gathering);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dateText = (TextView)findViewById(R.id.showDate);
        dateButton = (Button)findViewById(R.id.dateButton);

        timeText = (TextView)findViewById(R.id.showTime);
        timeButton = (Button)findViewById(R.id.timeButton);

        gatheringTitle=(EditText)findViewById(R.id.gatheringTitle);
        restaurantID=(EditText)findViewById(R.id.restaurantID);
        mDetail=(EditText)findViewById(R.id.detail);
        //userID=(EditText)findViewById(R.id.userID);
        if(getIntent()!=null){
            restaurantId = getIntent().getIntExtra("restaurantId",0);
            restaurantID.setText(getIntent().getStringExtra("restaurantName"));
        }
        date="";
        time="";

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                // Create a new instance of TimePickerDialog and return it
                DatePickerDialog dpDialog = new DatePickerDialog(NewGatheringActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date=year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                        dateText.setText(date);
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
                new TimePickerDialog(NewGatheringActivity.this, new TimePickerDialog.OnTimeSetListener(){

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time=hourOfDay + ":" + minute;
                        timeText.setText(time);
                    }
                }, hour, minute, false).show();
            }
        });



    }

    public void setDate(View v){

    }

    public void submitGathering(View v){
        mDetail.setError(null);
        //View focusView = null;
        boolean isValidated=validation();
        if(!isValidated){

            Log.v("CHECKING", "if statement");
        }else{
            String dateTime=date+" "+time;

            Gathering gathering=new Gathering();
            gathering.setName(gatheringTitle.getText().toString());
            gathering.setStartDatetime(dateTime);
            gathering.setdetail(mDetail.getText().toString());
            gathering.setIsStart(false);
            gathering.setCreatedBy(userID);
            gathering.setRestaurant(restaurantId);

            APIclient.APIService service=APIclient.getAPIService();
            Call<ResponseBody> req = service.createGathering(gathering);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.v("Upload", "success");
                    Toast toast = Toast.makeText(getApplicationContext(), "Created gathering!", Toast.LENGTH_LONG);
                    toast.show();
                    finish();
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
