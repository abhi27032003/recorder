package com.example.recorderchunks;

public  class TimeSpan {
    public long startMs;
    public long endMs;

    public TimeSpan(long startMs, long endMs) {
        this.startMs = startMs;
        this.endMs = endMs;
    }

    @Override
    public String toString() {
        return "Start: " + startMs + " ms, End: " + endMs + " ms";
    }
}