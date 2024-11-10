package com.example.recorderchunks;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder> {

    private Context context;
    private ArrayList<Recording> recordings;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private int playingPosition = -1;
    private Runnable updateSeekBar;

    public RecordingAdapter(Context context, ArrayList<Recording> recordings) {
        this.context = context;
        this.recordings = recordings;
    }

    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recording, parent, false);
        return new RecordingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingViewHolder holder, int position) {
        Recording recording = recordings.get(position);
        holder.textViewFileName.setText(recording.getUniqueCode());
        holder.textViewDetails.setText("Date: " + recording.getDate() + "\nTime: " + recording.getTime() +
                "\nDuration: " + recording.getDuration() + " sec");

        holder.playPauseButton.setOnClickListener(view -> {
            if (position == playingPosition) {
                pauseAudio(holder);
            } else {
                playAudio(holder, recording.getFilePath(), position);
            }
        });

        // Toggle audio chunks visibility
        holder.chunksButton.setOnClickListener(v -> {
            if (holder.audioChunksRecyclerView.getVisibility() == View.GONE) {
                holder.audioChunksRecyclerView.setVisibility(View.VISIBLE);
                AudioChunkAdapter audioChunkAdapter = new AudioChunkAdapter(context,(ArrayList<String>) recording.getAudioChunks());
                holder.audioChunksRecyclerView.setAdapter(audioChunkAdapter);
                holder.audioChunksRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                holder.audioChunksRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    private void playAudio(RecordingViewHolder holder, String filePath, int position) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            playingPosition = position;
            holder.playPauseButton.setText("Pause");

            updateSeekBar = new Runnable() {
                @Override
                public void run() {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int total = mediaPlayer.getDuration();
                    holder.progressBar.setMax(total);
                    holder.progressBar.setProgress(currentPosition);

                    handler.postDelayed(this, 100);
                }
            };
            handler.postDelayed(updateSeekBar, 100);

            mediaPlayer.setOnCompletionListener(mp -> {
                holder.playPauseButton.setText("Play");
                holder.progressBar.setProgress(0);
                playingPosition = -1;
                handler.removeCallbacks(updateSeekBar);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio(RecordingViewHolder holder) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            holder.playPauseButton.setText("Play");
            handler.removeCallbacks(updateSeekBar);
        }
    }

    public static class RecordingViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFileName, textViewDetails;
        Button playPauseButton, chunksButton;
        SeekBar progressBar;
        RecyclerView audioChunksRecyclerView;

        public RecordingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
            playPauseButton = itemView.findViewById(R.id.playPauseButton);
            progressBar = itemView.findViewById(R.id.progressBar);
            chunksButton = itemView.findViewById(R.id.chunksButton);
            audioChunksRecyclerView = itemView.findViewById(R.id.audioChunksRecyclerView);
        }
    }
}
