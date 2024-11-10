package com.example.recorderchunks;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recordings.db";
    private static final int DATABASE_VERSION = 3; // Update to version 3
    private static final String TABLE_NAME = "recordings";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_UNIQUE_CODE = "unique_code";
    private static final String COLUMN_AUDIO_CHUNKS = "audio_chunks"; // New column for audio chunks

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FILE_PATH + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_DURATION + " INTEGER,"
                + COLUMN_UNIQUE_CODE + " TEXT,"
                + COLUMN_AUDIO_CHUNKS + " TEXT" + ")"; // Add the audio_chunks column
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            // Add the audio_chunks column in version 3
            String ADD_AUDIO_CHUNKS_COLUMN = "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_AUDIO_CHUNKS + " TEXT";
            db.execSQL(ADD_AUDIO_CHUNKS_COLUMN);
        }
    }

    public void addRecording(Recording recording) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_PATH, recording.getFilePath());
        values.put(COLUMN_DATE, recording.getDate());
        values.put(COLUMN_TIME, recording.getTime());
        values.put(COLUMN_DURATION, recording.getDuration());
        values.put(COLUMN_UNIQUE_CODE, recording.getUniqueCode());

        // Convert the audioChunks list to a JSON string
        Gson gson = new Gson();
        String audioChunksJson = gson.toJson(recording.getAudioChunks());
        values.put(COLUMN_AUDIO_CHUNKS, audioChunksJson);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<Recording> getAllRecordings() {
        ArrayList<Recording> recordings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String filePath = cursor.getString(cursor.getColumnIndex(COLUMN_FILE_PATH));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                long duration = cursor.getLong(cursor.getColumnIndex(COLUMN_DURATION));
                String uniqueCode = cursor.getString(cursor.getColumnIndex(COLUMN_UNIQUE_CODE));
                String audioChunksJson = cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO_CHUNKS));

                // Convert the JSON string back into a List of strings
                Gson gson = new Gson();
                ArrayList<String> audioChunks = gson.fromJson(audioChunksJson, ArrayList.class);

                Recording recording = new Recording(filePath, date, time, duration, uniqueCode, audioChunks);
                recordings.add(recording);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return recordings;
    }
}
