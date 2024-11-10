package com.example.recorderchunks;

import java.util.List;
import java.util.Objects;

public class Recording {
    private String filePath;
    private String date;
    private String time;
    private long duration;
    private String uniqueCode;
    private List<String> audioChunks; // New field for chunks

    // Default constructor (if needed)
    public Recording() {}

    // Constructor to initialize all fields
    public Recording(String filePath, String date, String time, long duration, String uniqueCode, List<String> audioChunks) {
        this.filePath = filePath;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.uniqueCode = uniqueCode;
        this.audioChunks = audioChunks;
    }

    // Getters and setters
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public List<String> getAudioChunks() {
        return audioChunks;
    }

    public void setAudioChunks(List<String> audioChunks) {
        this.audioChunks = audioChunks;
    }

    // Override toString() for better representation
    @Override
    public String toString() {
        return "Recording{" +
                "filePath='" + filePath + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", duration=" + duration +
                ", uniqueCode='" + uniqueCode + '\'' +
                ", audioChunks=" + audioChunks +
                '}';
    }

    // Override equals and hashCode to handle object comparisons (if necessary)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recording recording = (Recording) obj;
        return duration == recording.duration &&
                filePath.equals(recording.filePath) &&
                date.equals(recording.date) &&
                time.equals(recording.time) &&
                uniqueCode.equals(recording.uniqueCode) &&
                audioChunks.equals(recording.audioChunks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, date, time, duration, uniqueCode, audioChunks);
    }
}
