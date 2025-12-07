import json
import librosa
import numpy as np
import os

# --- CONFIGURATION ---
# Hardcoded paths for ease of use
INPUT_FILE = r"assets\music\A_Life_Without_Law.wav" 
OUTPUT_FILE = r"assets\audio_data\A_Life_Without_Law_data.json"
FPS = 30
# ---------------------

def analyze_audio(input_file, output_file, fps=30):
    print(f"Loading audio: {input_file}...")
    try:
        # Load audio (always mono for feature extraction)
        y, sr = librosa.load(input_file, sr=None)
    except Exception as e:
        print(f"Failed to load audio: {e}")
        return

    duration = librosa.get_duration(y=y, sr=sr)
    print(f"Loaded {duration:.2f} seconds of audio.")
    
    print("Separating Harmonic and Percussive components...")
    # HPSS: Separate Harmonic (melody/pads) and Percussive (drums/transients)
    y_harmonic, y_percussive = librosa.effects.hpss(y)
    
    # --- Feature Extraction ---
    
    # 1. RMS Energy (Volume) - Calculation per frame
    # We want 'fps' samples per second.
    hop_length = int(sr / fps)
    
    print("Extracting RMS Energy...")
    rms_total = librosa.feature.rms(y=y, hop_length=hop_length)[0]
    rms_harmonic = librosa.feature.rms(y=y_harmonic, hop_length=hop_length)[0]
    rms_percussive = librosa.feature.rms(y=y_percussive, hop_length=hop_length)[0]
    
    # 2. Spectral Centroid (Brightness/Timbre)
    print("Extracting Spectral Centroid...")
    centroid = librosa.feature.spectral_centroid(y=y, sr=sr, hop_length=hop_length)[0]
    
    # 3. Beat Tracking
    print("Tracking beats...")
    tempo, beat_frames = librosa.beat.beat_track(y=y, sr=sr, hop_length=hop_length)
    beat_times = librosa.frames_to_time(beat_frames, sr=sr, hop_length=hop_length)
    
    # 4. Onset Detection (More granular hits than beats)
    print("Detecting onsets...")
    onset_frames = librosa.onset.onset_detect(y=y, sr=sr, hop_length=hop_length)
    onset_times = librosa.frames_to_time(onset_frames, sr=sr, hop_length=hop_length)
    
    # --- Formatting for Export ---
    
    # Helper to prevent JSON serialization errors with numpy types
    def clean(val):
        return round(float(val), 4)

    data_points = []
    num_frames = len(rms_total)
    
    print(f"Packaging {num_frames} frames of data...")
    
    for i in range(num_frames):
        time_sec = librosa.frames_to_time(i, sr=sr, hop_length=hop_length)
        if time_sec > duration:
            break
            
        data_points.append({
            "t": clean(time_sec),
            "rms": clean(rms_total[i]),
            "rms_h": clean(rms_harmonic[i]),
            "rms_p": clean(rms_percussive[i]),
            "cent": clean(centroid[i])
        })

    output_data = {
        "meta": {
            "file": os.path.basename(input_file),
            "duration": clean(duration),
            "fps": fps,
            "tempo": clean(tempo)
        },
        "beat_times": [clean(b) for b in beat_times],
        "onset_times": [clean(o) for o in onset_times],
        "frames": data_points
    }
    
    # Ensure output directory exists (relative to script or absolute)
    output_dir = os.path.dirname(output_file)
    if output_dir and not os.path.exists(output_dir):
        os.makedirs(output_dir)
    
    print(f"Saving to {output_file}...")
    with open(output_file, 'w') as f:
        json.dump(output_data, f, indent=0)
        
    print("Done!")

if __name__ == "__main__":
    if not os.path.exists(INPUT_FILE):
        # Fallback check if running from a different directory
        print(f"Warning: '{INPUT_FILE}' not found in current directory: {os.getcwd()}")
        print("Please check the 'INPUT_FILE' variable at the top of the script.")
    else:
        analyze_audio(INPUT_FILE, OUTPUT_FILE, FPS)
