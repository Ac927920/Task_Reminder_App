package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnTaskCompletedListener {

    Button mCreateRem;
    RecyclerView mRecyclerview;
    ArrayList<Model> dataholder = new ArrayList<>();
    MyAdapter adapter, completedAdapter;
    private static final String CHANEL_ID = "New Chanel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerview = findViewById(R.id.recyclerView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        adapter = new MyAdapter(dataholder, this);
        mRecyclerview.setAdapter(adapter);

        completedAdapter = new MyAdapter(new ArrayList<Model>(), null);
//        mCompletedRecyclerView.setAdapter(completedAdapter);

        mCreateRem = findViewById(R.id.create_reminder);
        mCreateRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
                startActivity(intent);
            }
        });

        Cursor cursor = new dbManager(getApplicationContext()).readAllReminders();
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            Model model = new Model(title, description); // Create a Model instance with title and description only
            if (model.isCompleted()) {
                completedAdapter.dataholder.add(model);
            } else {
                dataholder.add(model);
            }
        }

        adapter.notifyDataSetChanged();
        completedAdapter.notifyDataSetChanged();


//        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.logo1,null);
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//        Bitmap largeICON = bitmapDrawable.getBitmap();
//
//
//        // Inside onCreate method
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = manager.getNotificationChannel(CHANEL_ID);
//            if (channel == null) {
//                channel = new NotificationChannel(CHANEL_ID, "New Channel", NotificationManager.IMPORTANCE_HIGH);
//                manager.createNotificationChannel(channel);
//            }
//
//            notification = new Notification.Builder(this, CHANEL_ID)
//                    .setSmallIcon(R.drawable.alaram)
//                    .setLargeIcon(largeICON)
//                    .setContentTitle("Notification Title")
//                    .setContentText("Hello")
//                    .setSubText("New Hello")
//                    .build();
//        } else {
//            notification = new Notification.Builder(this)
//                    .setSmallIcon(R.drawable.alaram)
//                    .setLargeIcon(largeICON)
//                    .setContentTitle("Notification Title")
//                    .setContentText("Hello")
//                    .setSubText("New Hello")
//                    .build();
//        }
//
//        manager.notify(100, notification);



    }

    @Override
    public void onTaskCompleted(Model completedTask) {
        completedAdapter.dataholder.add(completedTask);
        completedAdapter.notifyDataSetChanged();

        playRingtone();
    }

    private void playRingtone() {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
        ringtone.play();

        // Optionally, you can also show a toast message to indicate that the task is completed
        Toast.makeText(this, "Task Completed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
