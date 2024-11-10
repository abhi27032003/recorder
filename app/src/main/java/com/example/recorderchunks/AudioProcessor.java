package com.example.recorderchunks;

import android.content.Context;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.List;

public class AudioProcessor {
    private final Context context;

    public AudioProcessor(Context context) {
        this.context = context;
    }

    public ArrayList<String> processAudio(String audioFilePath, String optPath, OnAudioProcessedListener listener) {
        ArrayList<String> audioChunksPaths = new ArrayList<>();

        // Display input and output paths to the user
        Toast.makeText(context, "Input Path: " + audioFilePath, Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Output Path: " + optPath, Toast.LENGTH_LONG).show();

        // Initialize Chaquopy Python instance
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
            Toast.makeText(context, "Python initialized for audio processing", Toast.LENGTH_SHORT).show();
        }

        Python py = Python.getInstance();
        PyObject pyModule = py.getModule("audio_processor"); // Name of your Python script without ".py"

        try {
            // Display toast before calling the Python function
            Toast.makeText(context, "Starting audio processing...", Toast.LENGTH_SHORT).show();

            // Call the process_audio function
            PyObject result = pyModule.callAttr("process_audio", audioFilePath, optPath,"3gp");
            List<PyObject> pyList = result.asList();

            // Convert the Python list to Java ArrayList
            for (PyObject obj : pyList) {
                audioChunksPaths.add(obj.toString());
            }

            // Display success toast
            Toast.makeText(context, "Audio processing complete", Toast.LENGTH_SHORT).show();
            listener.onProcessingComplete(audioChunksPaths);
        } catch (Exception e) {
            // Display error toast
            String errorMessage = "Error processing audio: " + e.getMessage();
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            listener.onProcessingError(errorMessage);
        }

        return audioChunksPaths;
    }

    public interface OnAudioProcessedListener {
        void onProcessingComplete(ArrayList<String> audioChunksPaths);
        void onProcessingError(String errorMessage);
    }
}
