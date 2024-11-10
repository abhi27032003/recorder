package com.example.recorderchunks;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final double SILENCE_THRESHOLD = 60.0; // Silence threshold in


    private static final int PERMISSION_CODE = 200;
    private MediaRecorder mediaRecorder;
    private String audioFilePath,audioFilePath2;
    private long startTime, stopTime;
    private DatabaseHelper databaseHelper;
    private ArrayList<Recording> recordingsList;
    private RecordingAdapter recordingAdapter;

    private Button startRecordingButton, stopRecordingButton;
    private RecyclerView recordingRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startRecordingButton = findViewById(R.id.startRecordingButton);
        stopRecordingButton = findViewById(R.id.stopRecordingButton);
        recordingRecyclerView = findViewById(R.id.recordingRecyclerView);

        // Initialize DB helper
        databaseHelper = new DatabaseHelper(this);

        // Set up RecyclerView
        recordingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordingsList = databaseHelper.getAllRecordings();
        recordingAdapter = new RecordingAdapter(this, recordingsList);
        recordingRecyclerView.setAdapter(recordingAdapter);

        // Check for permissions
        if (!checkPermissions()) {
            requestPermissions();
        }

        startRecordingButton.setOnClickListener(v -> {
            if (checkPermissions()) {
                startRecording();
            } else {
                requestPermissions();
            }
        });

        stopRecordingButton.setOnClickListener(v -> stopRecording());
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_CODE);
    }

    private void startRecording() {
        File audioDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (audioDir != null) {
            // Create timestamp for file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            audioFilePath = audioDir.getAbsolutePath() + "/recording_" + timeStamp + ".3gp";
            audioFilePath2 = audioDir.getAbsolutePath() + "/recording_" + timeStamp ;
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
            startRecordingButton.setEnabled(false);
            stopRecordingButton.setEnabled(true);
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

            startRecordingButton.setEnabled(true);
            stopRecordingButton.setEnabled(false);

            long duration = (stopTime - startTime) / 1000; // in seconds

            // Generate unique code
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(startTime));
            String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(startTime));
            String uniqueCode = date.replace("-", "") + time.replace(":", "") + duration;

            // Chunk the recording (for demonstration, we'll split the file into arbitrary parts)



            AudioProcessor ap = new AudioProcessor(this);
            List<String> chunks = ap.processAudio(audioFilePath, audioFilePath2 + uniqueCode + "/chunks", new AudioProcessor.OnAudioProcessedListener() {
                @Override
                public void onProcessingComplete(ArrayList<String> audioChunksPaths) {
                    // This callback is triggered when all chunks are processed.
                    List<String> chunks = audioChunksPaths;

                    // Now you can use the 'chunks' list
                    // Example: Log the chunk paths
                    for (String chunk : chunks) {
                        Log.d("AudioProcessor", "Chunk saved at : " + chunk);
                    }

                    // You can perform further operations with the chunks list here.
                }

                @Override
                public void onProcessingError(String errorMessage) {
                    // Handle errors if any
                    Log.e("AudioProcessor", "Error: " + errorMessage);
                }
            });
            // Save recording details in the database
            Recording newRecording = new Recording(audioFilePath, date, time, duration, uniqueCode, chunks);
            databaseHelper.addRecording(newRecording);

            // Update RecyclerView
            recordingsList.add(newRecording);
            recordingAdapter.notifyDataSetChanged();

            Toast.makeText(this, "Recording saved: " + audioFilePath, Toast.LENGTH_LONG).show();
        }
    }


    // Helper function to create a chunk file from start to end time


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
