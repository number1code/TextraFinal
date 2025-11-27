import whisper
import json

# --- 1. Configuration ---
AUDIO_FILE = "A_Life_Without_Law.wav"
MODEL_SIZE = "turbo" # Options: "tiny", "base", "small", "medium", "large", "turbo"

# The lyrics serve as a "prompt" to guide the model. 
# This tells Whisper: "This is the text you are likely to hear."
# It helps significantly with instrumental sections, screaming vocals, or unclear audio.
LYRICS_PROMPT = """
Body's a wreck, but the fight's locked in
Thought I'd collapse, but I'm back for a win
I'm out the gate with the beast of my kin
Eyes roll back as the curtains get pinned
This an attempt at life
My mind in a test of the might
Lips tucked in, I'm chomping my bite
Gods don't care, so I'm serving my rights
We were born for war
A life without law
We're the ghosts that haunt
We're the devil's sport
Okay, the mic just opened up, I'm waltzing on in
Brought a trench full of tools for a line that's so thin
Prayers for your men if you dare me to sin
When the water's filled with blood, it's a frenzy
The sharks are all about, I've been lost in the chaos
Of the past, for the pain, but the passion is passed
As a god among gods, I've been king for so long
But the crown don't weigh a thing, and I just don't give a
"""

# --- 2. Load the Model ---
print(f"Loading Whisper model '{MODEL_SIZE}'...")
model = whisper.load_model(MODEL_SIZE)
print("Model loaded.")

# --- 3. Transcribe with Prompt ---
print(f"Transcribing '{AUDIO_FILE}' using initial prompt...")
# initial_prompt: Provides context to the model.
# condition_on_previous_text=False: Prevents the model from getting stuck in loops (like "Thank you").
# temperature=0.2: Makes the model more deterministic and focused on the prompt.
result = model.transcribe(
    AUDIO_FILE, 
    word_timestamps=True, 
    initial_prompt=LYRICS_PROMPT,
    condition_on_previous_text=False,
    temperature=0.2
)
print("Transcription complete.")

# --- 4. Output Results ---
print("\n--- Word-by-Word Timestamps ---")
for segment in result["segments"]:
    for word in segment["words"]:
        print(f'[{word["start"]:.2f} -> {word["end"]:.2f}] {word["word"]}')

# Save to JSON for use in your app
output_filename = AUDIO_FILE.replace(".wav", ".json")
with open(output_filename, "w") as f:
    json.dump(result, f, indent=2)

print(f"\nFull data saved to: {output_filename}")
