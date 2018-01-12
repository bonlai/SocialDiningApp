package com.bonlai.socialdiningapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class NewGatheringActivity extends AppCompatActivity {
    private Button dateButton;
    private TextView dateText;

    private Button timeButton;
    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gathering);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateText = (TextView)findViewById(R.id.showDate);
        dateButton = (Button)findViewById(R.id.dateButton);

        timeText = (TextView)findViewById(R.id.showTime);
        timeButton = (Button)findViewById(R.id.timeButton);

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
                        dateText.setText(year + "/" + (monthOfYear+1) + "/" + dayOfMonth);
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
                        timeText.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false).show();
            }

        });

    }

    public void setDate(View v){

    }

}
