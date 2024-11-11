package com.example.recorderchunks;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    
    private RecyclerView recordingRecyclerView;
    private FloatingActionButton goTo_Add_event_Page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordingRecyclerView = findViewById(R.id.recordingRecyclerView);
        goTo_Add_event_Page = findViewById(R.id.add_event);

        // Set up OnClickListener for "Add Event" button
        goTo_Add_event_Page.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, Add_Event.class);
            startActivity(i);
        });

        // Initialize Database Helper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Set up RecyclerView
        recordingRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get all events from the database
        List<Event> eventsList = databaseHelper.getAllEvents();

        // Set up the adapter and attach it to the RecyclerView
        EventAdapter eventAdapter = new EventAdapter(this, eventsList);
        recordingRecyclerView.setAdapter(eventAdapter);



    }






}
