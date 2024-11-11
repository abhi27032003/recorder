package com.example.recorderchunks;// MainActivity.java
import android.Manifest;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Add_Event extends AppCompatActivity {

    private TextView selectedDateTime;
    private EditText eventDescription;

    private  Button datePickerBtn,timePickerBtn;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private DatabaseHelper databaseHelper;

    //////////////////////////////////saving audio//////////////////////////////////////////
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private long startTime, stopTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        datePickerBtn = findViewById(R.id.datePickerBtn);
        timePickerBtn = findViewById(R.id.timePickerBtn);
        FloatingActionButton recordButton = findViewById(R.id.recordButton);
        selectedDateTime = findViewById(R.id.selectedDateTime);
        eventDescription = findViewById(R.id.eventDescription);
        // Set up the toolbar as the app bar
        Toolbar toolbar = findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        // Enable back button in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button saveEventButton = findViewById(R.id.saveEventButton);  // Make sure you have a save button in your layout
        saveEventButton.setOnClickListener(view -> {
            // Get the event title, description, selected date, and selected time
            String title = ((EditText) findViewById(R.id.eventTitle)).getText().toString();  // Assuming you have an EditText for the title
            String eventdescription = eventDescription.getText().toString();
            String selectedDate = selectedDateTime.getText().toString();  // Assuming this TextView holds the selected date and time
            String selectedTime = timePickerBtn.getText().toString();  // Assuming this button holds the selected time

            // Call the saveEventData method with the parameters
            saveEventData(title, eventdescription, selectedDate, selectedTime);
        });

        // Display Current Date and Time
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());
        selectedDateTime.setText("Current Date and Time: " + currentDateTime);

        // Date Picker
        datePickerBtn.setOnClickListener(view -> showDatePicker());

        // Time Picker
        timePickerBtn.setOnClickListener(view -> showTimePicker());

        // Speech to Text
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        recordButton.setOnClickListener(view -> startSpeechToText());
    }

    // Method to show DatePicker
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update the button text to the selected date
            datePickerBtn.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

            // Update the date and time display
            updateDateTimeDisplay(calendar);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    // Method to show TimePicker
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new android.app.TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            // Update the button text to the selected time
            timePickerBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));

            // Update the date and time display
            updateDateTimeDisplay(calendar);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    // Update DateTime Display
    private void updateDateTimeDisplay(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault());


    }

    // Start Speech-to-Text Intent
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak about the event...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle Speech-to-Text Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                eventDescription.setText(result.get(0));
            }
        }
    }

    private void saveEventData(String title, String eventDescription, String selectedDate, String selectedTime) {
        if (title == null || title.trim().isEmpty()) {
            Toast.makeText(this, "Event title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventDescription == null || eventDescription.trim().isEmpty()) {
            Toast.makeText(this, "Event description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate == null || selectedDate.trim().isEmpty()) {
            Toast.makeText(this, "Event date cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTime == null || selectedTime.trim().isEmpty()) {
            Toast.makeText(this, "Event time cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming `DatabaseHelper` is your class that interacts with the SQLite database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String creationDate = dateFormat.format(new Date());
        String creationTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Save event to database
        boolean isInserted = databaseHelper.insertEvent(
                title,
                eventDescription,
                creationDate,
                creationTime,
                selectedDate,
                selectedTime
        );

        // Show success or failure message
        if (isInserted) {
            Toast.makeText(this, "Event saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
        }
    }

    //////////////////////////////////functions to save audio///////////////////////////////////////////
    private void startRecording() {
        File audioDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (audioDir != null) {
            // Create timestamp for file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            audioFilePath = audioDir.getAbsolutePath() + "/recording_" + timeStamp + ".3gp";
        } else {
            Toast.makeText(this, "Failed to get storage directory", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();


            startTime = System.currentTimeMillis();
            Toast.makeText(this, "Recording started...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing MediaRecorder: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            stopTime = System.currentTimeMillis();



            long duration = (stopTime - startTime) / 1000; // in seconds

            // Generate unique code
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(startTime));
            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(startTime));
            String uniqueCode = date.replace("-", "") + time.replace(":", "") + duration;

            // Chunk the recording (using AudioProcessor if implemented)
            // List<String> chunks = ap.processAudio(audioFilePath, audioFilePath + uniqueCode + "/chunks");

            // Extract text from each chunk
            List<String> textChunks = new ArrayList<>();
            textChunks.add("wert");

            // Save recording details with extracted text in the database

           // databaseHelper.addRecording(newRecording);

            // Update RecyclerView


            Toast.makeText(this, "Recording saved: " + audioFilePath, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
