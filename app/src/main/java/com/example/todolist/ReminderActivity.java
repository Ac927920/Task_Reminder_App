package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

// Allows users to add reminders
public class ReminderActivity extends AppCompatActivity {

    TextView btnSubmit, btnCancel;
    EditText mTitledit, mTitlDesedit;
    String timeToNotify;
    Switch mSwitchDate, mSwitchTime;
    String date ;
    String time ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        mTitledit = findViewById(R.id.editTitle);
        mTitlDesedit = findViewById(R.id.editDes);

        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
        // Find switches by their IDs
        mSwitchDate = findViewById(R.id.switchDate);
        mSwitchTime = findViewById(R.id.switchTime);



        // Set listeners for switches
        mSwitchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    selectDate();
                } else {
                    // Perform actions when the date switch is turned off
                    // Clear date when the switch is turned off
                    date = null;
                }
            }
        });

        mSwitchTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    selectTime();
                } else {
                    // Perform actions when the time switch is turned off
                    // Clear time when the switch is turned off
                    time = null;
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitledit.getText().toString().trim();
                String desc = mTitlDesedit.getText().toString().trim();


                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter text", Toast.LENGTH_SHORT).show();
                } else if (desc.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Desc", Toast.LENGTH_SHORT).show();
                } else {
//                    if (time.equals("time") || date.equals("date")) {
//                        Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
//                    } else {
//                        processInsert(title,desc, date, time);
//                    }
                    // Check switch states and perform actions accordingly
                    if (mSwitchDate.isChecked() && mSwitchTime.isChecked()) {
                        // Both switches are on
                        processInsert(title,desc, date, time);

                    } else if (!mSwitchDate.isChecked() && !mSwitchTime.isChecked()) {
                        // Both switches are off
                        Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                    } else {
                        // One of the switches is off
                        Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void processInsert(String title, String description, String date, String time) {
        String result = new dbManager(this).addReminder(title, description, date, time);
        setAlarm(title, date, time);
//        Log.d("Date&Time"," " + date+" "+time);
        mTitledit.setText("");
        mTitlDesedit.setText(""); // Clear the description field after insertion
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    }

    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeToNotify = i + ":" + i1;
//                mTimebtn.setText(formatTime(i, i1));
                time = formatTime(i,i1);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                // Update the date variable when the user selects a date
                date = day + "-" + (month + 1) + "-" + year;
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public String formatTime(int hour, int minute) {
        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }

        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }

        return time;
    }


    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                   //assigining alaram manager object to set alaram

        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        intent.putExtra("event", text);                                                       //sending data to alarm class to create channel and notification
        intent.putExtra("time", date);
        intent.putExtra("date", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        String dateandtime = date + " " + timeToNotify;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateandtime);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
            Log.d("DATE", " " + date1);
            Log.d("DATE", " " + date1.getTime());
            Toast.makeText(getApplicationContext(), "Alaram", Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intentBack = new Intent(getApplicationContext(), MainActivity.class);                //this intent will be called once the setting alaram is completes
        intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentBack);                                                                  //navigates from adding reminder activity ot mainactivity

    }



//    private void setAlarm(String text, String date, String time) {
//        if (date != null && time != null) {
//            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
//            intent.putExtra("event", text);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
//            String dateAndTime = date + " " + time;
//            // Parse date and time in UTC timezone
//            DateFormat formatter = new SimpleDateFormat("d-M-yyyy HH:mm");
//            formatter.setTimeZone(TimeZone.getDefault());
//            try {
//                Date date1 = formatter.parse(dateAndTime);
//                am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
//                Log.d("DATE", " " + date1);
//                Log.d("DATE", " " + date1.getTime());
//                Toast.makeText(getApplicationContext(), "Alarm set", Toast.LENGTH_SHORT).show();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            Intent intentBack = new Intent(getApplicationContext(), MainActivity.class);
//            intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intentBack);
//        } else {
//            Toast.makeText(getApplicationContext(), "Please select both date and time", Toast.LENGTH_SHORT).show();
//        }
//    }

}
