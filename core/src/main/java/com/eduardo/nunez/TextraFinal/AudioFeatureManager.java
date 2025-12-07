package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.math.MathUtils;

public class AudioFeatureManager {

    public static class AudioFrame {
        public float time;
        public float rmsTotal;
        public float rmsHarmonic;
        public float rmsPercussive;
        public float centroid;

        public AudioFrame() {
        }

        public AudioFrame(float t, float rms, float rmsH, float rmsP, float cent) {
            this.time = t;
            this.rmsTotal = rms;
            this.rmsHarmonic = rmsH;
            this.rmsPercussive = rmsP;
            this.centroid = cent;
        }

        // Linear interpolation between two frames
        public static AudioFrame lerp(AudioFrame a, AudioFrame b, float alpha) {
            AudioFrame result = new AudioFrame();
            result.time = MathUtils.lerp(a.time, b.time, alpha);
            result.rmsTotal = MathUtils.lerp(a.rmsTotal, b.rmsTotal, alpha);
            result.rmsHarmonic = MathUtils.lerp(a.rmsHarmonic, b.rmsHarmonic, alpha);
            result.rmsPercussive = MathUtils.lerp(a.rmsPercussive, b.rmsPercussive, alpha);
            result.centroid = MathUtils.lerp(a.centroid, b.centroid, alpha);
            return result;
        }
    }

    private Array<AudioFrame> frames;
    private Array<Float> beatTimes;
    private Array<Float> onsetTimes;
    private float duration;

    public AudioFeatureManager() {
        frames = new Array<>();
        beatTimes = new Array<>();
        onsetTimes = new Array<>();
    }

    public void load(String jsonPath) {
        FileHandle file = Gdx.files.internal(jsonPath);
        if (!file.exists()) {
            Gdx.app.error("AudioFeatureManager", "File not found: " + jsonPath);
            return;
        }

        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(file);

        // Meta
        duration = root.get("meta").getFloat("duration");

        // Beats & Onsets
        for (JsonValue val : root.get("beat_times"))
            beatTimes.add(val.asFloat());
        for (JsonValue val : root.get("onset_times"))
            onsetTimes.add(val.asFloat());

        // Frames
        JsonValue framesJson = root.get("frames");
        frames.clear(); // Clear existing if reloading
        for (JsonValue f : framesJson) {
            float t = f.getFloat("t");
            float rms = f.getFloat("rms");
            float rmsH = f.getFloat("rms_h");
            float rmsP = f.getFloat("rms_p");
            float cent = f.getFloat("cent");
            frames.add(new AudioFrame(t, rms, rmsH, rmsP, cent));
        }

        Gdx.app.log("AudioFeatureManager", "Loaded " + frames.size + " frames from " + jsonPath);
    }

    public AudioFrame getFrameAtTime(float time) {
        if (frames.size == 0)
            return new AudioFrame(0, 0, 0, 0, 0);

        // Clamp time
        if (time <= 0)
            return frames.first();
        if (time >= duration)
            return frames.peek();

        // Binary search or simple index calculation (since fps is constant mostly)
        // Optimization: Assume constant FPS roughly, jump to index?
        // But for safety let's use binary search or just linear since array is sorted
        // by time.
        // Actually, frame index ~ time * fps. Let's try to guess index.

        // Simple search for now (can optimize if needed)
        // Since we are playing sequentially, we could cache last index.

        // Binary Search implementation for efficiency
        int low = 0;
        int high = frames.size - 1;
        int index = -1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            float midTime = frames.get(mid).time;

            if (midTime < time) {
                low = mid + 1;
            } else if (midTime > time) {
                high = mid - 1;
            } else {
                index = mid;
                break;
            }
        }

        // 'high' is now the index of element <= time
        // 'low' is index of element > time
        int idxA = high;
        if (idxA < 0)
            idxA = 0;
        if (idxA >= frames.size - 1)
            return frames.get(idxA);

        AudioFrame frameA = frames.get(idxA);
        AudioFrame frameB = frames.get(idxA + 1);

        float range = frameB.time - frameA.time;
        float alpha = (range <= 0) ? 0 : (time - frameA.time) / range;

        return AudioFrame.lerp(frameA, frameB, alpha);
    }

    /**
     * Checks if a beat occurred within the last 'window' seconds from 'currentTime'
     */
    public boolean isBeat(float currentTime, float window) {
        // Find if any beat time is in [currentTime - window, currentTime]
        // This is a bit tricky for exact matching every frame.
        // Better pattern: Shader logic usually just wants "is it a beat NOW?"
        // Simpler: Find closest beat to currentTime. If |beat - currentTime| < window,
        // return true.

        // Used primarily for discrete triggers
        for (Float b : beatTimes) {
            if (Math.abs(b - currentTime) < window)
                return true;
        }
        return false;
    }
}
