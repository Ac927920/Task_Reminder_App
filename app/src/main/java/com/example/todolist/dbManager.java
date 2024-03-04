package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Manages database operations
public class dbManager extends SQLiteOpenHelper {
    private static final String dbname = "reminder";

    public dbManager(Context context) {
        super(context, dbname, null, 2); // Increment the database version
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "create table tbl_reminder(id integer primary key autoincrement,title text, description text, date text, time text)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // If upgrading from version 1 to version 2, drop the old table and recreate it
            String query = "DROP TABLE IF EXISTS tbl_reminder";
            sqLiteDatabase.execSQL(query);
            onCreate(sqLiteDatabase);
        }
    }

    public String addReminder(String title, String description, String date, String time) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("date", date);
        contentValues.put("time", time);

        long result = database.insert("tbl_reminder", null, contentValues);

        if (result == -1) {
            return "Failed";
        } else {
            return "Successfully inserted";
        }
    }

    public Cursor readAllReminders() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "select * from tbl_reminder order by id desc";
        return database.rawQuery(query, null);
    }
}