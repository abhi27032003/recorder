package com.example.recorderchunks;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class AudioChunkAdapter extends RecyclerView.Adapter<AudioChunkAdapter.AudioChunkViewHolder> {

    private ArrayList<String> audioChunks;
    private Context context;
    private MediaPlayer mediaPlayer;

    public AudioChunkAdapter(Context context, ArrayList<String> audioChunks) {
        this.context = context;
        this.audioChunks = audioChunks;
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public AudioChunkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio_chunk, parent, false);
        return new AudioChunkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioChunkViewHolder holder, int position) {
        String chunkPath = audioChunks.get(position);
        holder.chunkPath.setText("Chunk Path: " + chunkPath);

        holder.playButton.setOnClickListener(v -> {
            playAudio(chunkPath);
        });
    }

    @Override
    public int getItemCount() {
        return audioChunks.size();
    }

    private void playAudio(String chunkPath) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(chunkPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewRecycled(@NonNull AudioChunkViewHolder holder) {
        super.onViewRecycled(holder);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static class AudioChunkViewHolder extends RecyclerView.ViewHolder {
        TextView chunkPath;
        Button playButton;

        public AudioChunkViewHolder(@NonNull View itemView) {
            super(itemView);
            chunkPath = itemView.findViewById(R.id.chunkPath);
            playButton = itemView.findViewById(R.id.playButton);
        }
    }
}
