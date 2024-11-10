import os
from moviepy.editor import AudioFileClip
from pydub import AudioSegment, silence

# Convert MP3 or 3GP to WAV using moviepy
def convert_to_wav(input_file, output_file):
    # Load the audio file using moviepy
    audio_clip = AudioFileClip(input_file)
    # Write the audio as WAV
    audio_clip.write_audiofile(output_file, codec='pcm_s16le')

# Convert WAV to 3GP using moviepy
def convert_wav_to_3gp(input_file, output_file):
    # Load the WAV file using moviepy
    audio_clip = AudioFileClip(input_file)
    # Write the audio as 3GP
    audio_clip.write_audiofile(output_file, codec='libvorbis')

# Process the audio file and save non-silent chunks
def process_audio(audio_file_path, output_path):
    # Step 1: Convert the input file to WAV
    wav_file_path = os.path.join(output_path, "converted_audio.wav")
    convert_to_wav(audio_file_path, wav_file_path)

    # Step 2: Load the WAV file using pydub
    audio = AudioSegment.from_wav(wav_file_path)

    # Detect non-silent ranges
    non_silent_ranges = silence.detect_nonsilent(audio, min_silence_len=2000, silence_thresh=-40)

    chunk_paths = []
    wav_chunk_paths = []  # List to store the paths of WAV chunks

    # Save non-silent chunks as WAV firstc
    for i, (start, end) in enumerate(non_silent_ranges):
        chunk = audio[start:end]  # Extract the non-silent chunk
        wav_chunk_path = os.path.join(output_path, f"chunk_{i}.wav")
        chunk.export(wav_chunk_path, format="wav")  # Export as WAV
        wav_chunk_paths.append(wav_chunk_path)

    # Step 3: Convert WAV chunks to 3GP and delete the WAV chunks
    for wav_chunk_path in wav_chunk_paths:
        # Convert each WAV chunk to 3GP
        chunk_3gp_path = wav_chunk_path.replace('.wav', '.3gp')
        convert_wav_to_3gp(wav_chunk_path, chunk_3gp_path)

        # Add the 3GP chunk to the list
        chunk_paths.append(chunk_3gp_path)

        # Delete the WAV chunk after conversion
        os.remove(wav_chunk_path)

    return chunk_paths
