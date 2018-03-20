package com.bonlai.socialdiningapp.detail.gathering;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.bonlai.socialdiningapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GatheringSearchActivity extends AppCompatActivity {

    public static final String KEYWORD="KEYWORD";
    public static final String LOCATION="LOCATION";
    public static final String MIN_COUNT="MIN_COUNT";
    public static final String MAX_COUNT="MAX_COUNT";
    public static final String START_DATE="START_DATE";
    public static final String END_DATE="END_DATE";


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String minDate=new String();
    String maxDate=new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gathering_search);

        final TextView mKeyword=findViewById(R.id.restaurant_name);
        final TextView mLocation=findViewById(R.id.location);
        final TextView mMinimum=findViewById(R.id.minimum);
        final TextView mMaximum=findViewById(R.id.maximum);
        final TextView mStartDate=findViewById(R.id.start_date);
        final TextView mEndDate=findViewById(R.id.end_date);

        Button mStarDateButton = (Button) findViewById(R.id.start_date_button);
        mStarDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                // Create a new instance of TimePickerDialog and return it
                DatePickerDialog dpDialog = new DatePickerDialog(GatheringSearchActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        minDate=new String(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                        mStartDate.setText(minDate);
                    }
                }, year, month, day);
                try {
                    if(maxDate!=null) {
                        dpDialog.getDatePicker().setMaxDate(dateFormat.parse(maxDate).getTime());
                    }
                    dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dpDialog.show();
            }
        });


        Button mEndDateButton = (Button) findViewById(R.id.end_date_button);
        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                // Create a new instance of TimePickerDialog and return it
                DatePickerDialog dpDialog = new DatePickerDialog(GatheringSearchActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        maxDate=new String(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                        mEndDate.setText(maxDate);
                    }
                }, year, month, day);
                try {
                    if(minDate!=null){
                        dpDialog.getDatePicker().setMinDate(dateFormat.parse(minDate).getTime());
                    }else{
                        dpDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dpDialog.show();
            }
        });

        Button mSearch = (Button) findViewById(R.id.search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();

                returnIntent.putExtra(KEYWORD,mKeyword.getText().toString());
                returnIntent.putExtra(LOCATION,mLocation.getText().toString());
                returnIntent.putExtra(MIN_COUNT,mMinimum.getText().toString());
                returnIntent.putExtra(MAX_COUNT,mMaximum.getText().toString());
                returnIntent.putExtra(START_DATE,minDate);
                returnIntent.putExtra(END_DATE,maxDate);


                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }
}
