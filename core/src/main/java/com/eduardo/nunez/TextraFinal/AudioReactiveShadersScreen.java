package com.eduardo.nunez.TextraFinal;

//run with .\gradlew lwjgl3:run
//import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
// import com.badlogic.gdx.video.VideoPlayer;
// import com.badlogic.gdx.video.VideoPlayerCreator;
// import com.badlogic.gdx.video.scenes.scene2d.VideoActor;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.github.tommyettinger.textra.*;
import by.bonenaut7.gdxpsx.vfxintegration.PSXPostProcessingEffect;
import by.bonenaut7.gdxpsx.postprocessing.PSXPostProcessingShader;
import by.bonenaut7.gdxpsx.postprocessing.DitheringMatrix;

public class AudioReactiveShadersScreen extends ScreenAdapter {

    private SpriteBatch batch;
    // --- Core LibGDX and UI Objects ---
    // private final Game game;
    private Stage stage;
    private Skin skin;
    private TypingLabel typingLabel;
    // --- Lyrics Data and State ---
    private Array<String> lyrics;

    private AudioFeatureManager audioFeatureManager;

    // * NEW CODE for whisper AI based automatic timing *//
    // A simple helper class to store a lyric and its start time together
    private static class LyricLine {
        float startTime;
        String text;

        public LyricLine(float startTime, String text) {
            this.startTime = startTime;
            this.text = text;
        }

        // Helper for equality check during hot-reload
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            LyricLine lyricLine = (LyricLine) o;
            return Float.compare(lyricLine.startTime, startTime) == 0 && text.equals(lyricLine.text);
        }
    }

    private Music music; // LibGDX class for handling streaming music

    // Precise Timing Variables
    private long startTimeNanos;
    private long pauseTimeNanos;
    private float elapsedTime = 0f;

    private Array<LyricLine> timedLyrics;
    private int currentLyricIndex = 0;
    private boolean isPaused = false;
    // * END NEW CODE for whisper AI based automatic timing *//

    // --- Hot Reloading State ---
    private long lastModifiedTime = 0;
    private float reloadCheckTimer = 0f;
    private static final float RELOAD_CHECK_INTERVAL = 1.0f; // Check every 1 second
    private String currentJsonPath;

    // --- Configuration ---
    private JsonValue config;

    // --- gdx-vfx Post-Processing Objects ---
    private VfxManager vfxManager;
    private CrtEffect crtEffect;
    private BloomEffect bloomEffect;
    private VignettingEffect vignettingEffect;
    private OldTvEffect oldTvEffect;
    private ChromaticAberrationEffect chromaticAberrationEffect;
    private FilmGrainEffect filmGrainEffect;
    private GaussianBlurEffect gaussianBlurEffect;
    private RadialBlurEffect radialBlurEffect;
    private LensFlareEffect lensFlareEffect;
    private FisheyeEffect fisheyeEffect;
    private LevelsEffect levelsEffect;
    private ZoomEffect zoomEffect;
    private FxaaEffect fxaaEffect;
    private NfaaEffect nfaaEffect;
    // === Effect State Flags ===
    private boolean isCrtEnabled = false;
    private boolean isBloomEnabled = false;
    private boolean isVignetteEnabled = false;
    private boolean isOldTvEnabled = false;
    private boolean isFilmGrainEnabled = false;
    private boolean isFisheyeEnabled = false;

    private boolean isChromaticAberrationEnabled = false;
    private boolean isGaussianBlurEnabled = false;
    private boolean isRadialBlurEnabled = false;
    private boolean isLensFlareEnabled = false;
    private boolean isLevelsEnabled = false;
    private boolean isZoomEnabled = false;
    private boolean isFxaaEnabled = false;
    private boolean isNfaaEnabled = false;
    private boolean isPsxEnabled = false;

    private PSXPostProcessingEffect psxEffect;

    // Shader Content
    private ShaderProgram shader;
    private Texture blankTexture;
    private float beatIntensity = 0f;

    // private VideoPlayer videoPlayer;
    // private VideoActor videoActor;
    private int videoWidth = 1920;
    private int videoHeight = 1080;
    private VideoRecorder videoRecorder;

    // private final Game game; // Removed unused field

    public AudioReactiveShadersScreen() {
        // this.game = game;
    }

    @Override
    public void show() {
        // 1. Load Configuration
        loadConfig();

        // 2. Load Assets based on Config
        String musicPath = config.getString("musicPath", "music/mr_lizard.wav");
        String videoPath = config.getString("videoPath", "video/mrlizardsongtimelapse.webm");
        currentJsonPath = config.getString("jsonPath", "song_jsons/mr_lizard.json");

        music = Gdx.audio.newMusic(Gdx.files.internal(musicPath));

        // --- RECORDER SETUP ---
        videoRecorder = new VideoRecorder();

        // Initial Lyric Load
        timedLyrics = parseLyricsAndTimestamps(currentJsonPath);
        FileHandle jsonFile = Gdx.files.internal(currentJsonPath);
        lastModifiedTime = jsonFile.lastModified();

        // --- AUDIO FEATURE SETUP ---
        audioFeatureManager = new AudioFeatureManager();
        String audioDataPath = config.getString("audioDataPath");
        audioFeatureManager.load(audioDataPath);

        // --- SHADER SETUP ---
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/default.vert"),
                Gdx.files.internal("shaders/cybertruck.frag"));
        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", "Compilation failed:\n" + shader.getLog());
        } else {
            Gdx.app.log("Shader", "Compiled successfully!");
        }

        // Create a 1x1 white texture for full-screen drawing
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        blankTexture = new Texture(pixmap);
        pixmap.dispose();

        // videoPlayer = VideoPlayerCreator.createVideoPlayer();
        // FileHandle file = Gdx.files.internal(videoPath);
        // try {
        // videoPlayer.load(file);
        // } catch (Exception e) {
        // Gdx.app.log("loading video", "error: " + e.toString());
        // }
        // videoPlayer.play();
        // videoPlayer.setLooping(true);
        // videoActor = new VideoActor(videoPlayer);

        // --- 1. Data and Asset Setup ---
        lyrics = createLyrics();
        skin = new Skin();
        batch = new SpriteBatch();

        // --- 2. Font and Skin Initialization ---
        setupFontsAndSkin();

        // --- 3. Scene2D Setup ---
        stage = new Stage(new FitViewport(1080, 1920));

        typingLabel = new TypingLabel("", skin); // Start with an empty label
        typingLabel.setAlignment(Align.center);
        typingLabel.setWrap(true);

        Table table = new Table();
        table.setFillParent(true);
        table.add(typingLabel).width(900f);
        stage.addActor(table);

        // stage.addActor(videoActor);

        // --- 4. GDX-VFX SETUP ---
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
        vfxManager.setBlendingEnabled(true);
        crtEffect = new CrtEffect();
        bloomEffect = new BloomEffect();
        vignettingEffect = new VignettingEffect(false);
        oldTvEffect = new OldTvEffect();
        chromaticAberrationEffect = new ChromaticAberrationEffect(2);
        filmGrainEffect = new FilmGrainEffect();
        gaussianBlurEffect = new GaussianBlurEffect();
        radialBlurEffect = new RadialBlurEffect(2);
        lensFlareEffect = new LensFlareEffect();
        fisheyeEffect = new FisheyeEffect();
        levelsEffect = new LevelsEffect();
        zoomEffect = new ZoomEffect();
        fxaaEffect = new FxaaEffect(0.15f, 1, 1, true);
        nfaaEffect = new NfaaEffect(true);

        // PSX Effect
        psxEffect = new PSXPostProcessingEffect();
        // Configure PSX Effect
        PSXPostProcessingShader psxShader = psxEffect.getConfiguration();

        psxShader.setDownscalingEnabled(true);
        psxShader.setDownscalingFromScale(4f); // Downscale by 4x (e.g. 1080p -> ~270p)

        psxShader.setDitheringEnabled(true);
        psxShader.setDitheringScale(4f); // Match downscale
        psxShader.setDitheringIntensity(0f); // Hidden by default
        psxShader.setDitheringMatrix(DitheringMatrix.BAYER_8x8);

        psxShader.setColorReductionEnabled(true);
        psxShader.setColorReduction(32f); // Low color depth

        // Configure the effects for the desired look
        bloomEffect.setBloomIntensity(1.0f);
        vignettingEffect.setIntensity(1f);
        levelsEffect.setSaturation(0.55f);
        levelsEffect.setHue(0.6f);

        // Add effects to the manager. The order matters.
        // vfxManager.addEffect(crtEffect);
        // isCrtEnabled = true;
        vfxManager.addEffect(bloomEffect);
        isBloomEnabled = true;
        // vfxManager.addEffect(vignettingEffect);
        // isVignetteEnabled = true;

        // vfxManager.addEffect(oldTvEffect);
        // isOldTvEnabled = true;
        // vfxManager.addEffect(filmGrainEffect);
        // isFilmGrainEnabled = true;
        // filmGrainEffect.setNoiseAmount(0.2f);
        // vfxManager.addEffect(fisheyeEffect);
        // isFisheyeEnabled = true;

        // --- 5. Input Processor Setup ---
        setupInput();

        // 5. Start the Music
        music.play();
        startTimeNanos = TimeUtils.nanoTime(); // Initialize precise timer
        elapsedTime = 0f;
        currentLyricIndex = 0;
    }

    private void loadConfig() {
        JsonReader reader = new JsonReader();
        FileHandle configFile = Gdx.files.internal("config.json");
        if (configFile.exists()) {
            config = reader.parse(configFile);
        } else {
            // Fallback default config if file is missing
            Gdx.app.error("Config", "config.json not found! Using defaults.");
            config = new JsonValue(JsonValue.ValueType.object);
            config.addChild("musicPath", new JsonValue("music/mr_lizard.wav"));
            config.addChild("videoPath", new JsonValue("video/mrlizardsongtimelapse.webm"));
            config.addChild("jsonPath", new JsonValue("song_jsons/mr_lizard.json"));
            config.addChild("primaryFont", new JsonValue("fonts/TitanOne-Regular.ttf"));
            config.addChild("secondaryFont", new JsonValue("fonts/RetroSide-MV0mY.otf"));
            config.addChild("audioDataPath", new JsonValue("audio_data/A_Life_Without_Law_data.json"));
        }
    }

    private Array<LyricLine> parseLyricsAndTimestamps(String jsonPath) {
        Array<LyricLine> timedLyrics = new Array<>();
        JsonReader jsonReader = new JsonReader();
        FileHandle file = Gdx.files.internal(jsonPath);

        if (!file.exists()) {
            Gdx.app.error("Lyrics", "JSON file not found: " + jsonPath);
            return timedLyrics;
        }

        JsonValue base = jsonReader.parse(file);

        for (JsonValue lineJson : base) {
            float startTime = lineJson.getFloat("startTime");
            String markupText = lineJson.getString("markup");
            LyricLine lyricLine = new LyricLine(startTime, markupText);
            timedLyrics.add(lyricLine);
        }
        return timedLyrics;
    }

    @Override
    public void render(float delta) {
        // 1. Hot-Reload Check
        reloadCheckTimer += delta;
        if (reloadCheckTimer >= RELOAD_CHECK_INTERVAL) {
            reloadCheckTimer = 0;
            checkForJsonUpdates();
        }

        // 2. Update Timer
        if (isPaused) {
            // When paused, we don't advance time.
            // We might want to update startTimeNanos so that when we unpause,
            // the elapsed time calculation remains correct relative to the pause duration.
            // However, simpler logic is:
            return;
        }

        // Calculate elapsed time using TimeUtils for precision
        long currentNanos = TimeUtils.nanoTime();
        elapsedTime = (currentNanos - startTimeNanos) / 1_000_000_000f;

        // Sync music if it drifts too much (optional, but good practice)
        // if (Math.abs(music.getPosition() - elapsedTime) > 0.1f) { ... }

        // --- 3. AUTOMATIC LYRIC ADVANCEMENT ---
        if (currentLyricIndex < timedLyrics.size) {
            LyricLine nextLine = timedLyrics.get(currentLyricIndex);
            if (elapsedTime >= nextLine.startTime) {
                typingLabel.restart(nextLine.text);
                currentLyricIndex++;
            }
        }

        // 4. Update scene logic
        stage.act(delta);

        // --- AUDIO REACTIVE UPDATE ---
        AudioFeatureManager.AudioFrame frame = audioFeatureManager.getFrameAtTime(elapsedTime);

        // 1. Bloom (Harmonic - Smooth)
        // Scaled up as analyzed (RMS ~0.01 -> * 20 -> 0.2)
        if (isBloomEnabled) {
            float bloomInt = frame.rmsHarmonic * 25.0f;// 35
            bloomEffect.setBloomIntensity(bloomInt);
        }

        // 2. Chromatic Aberration (Percussive - Glitch)
        if (isChromaticAberrationEnabled) {
            float distortion = 0f;
            // Threshold for impact
            if (frame.rmsPercussive > 0.003f) {
                // Scale heavily for visibility
                distortion = (frame.rmsPercussive - 0.003f) * 40.0f;// 80
            }
            chromaticAberrationEffect.setMaxDistortion(distortion);
        }

        // 3. Vignette (Beats + High Centroid)
        if (isVignetteEnabled) {
            // Pulse on beats if high frequency content (snare/hats) -> Centroid > 2000
            boolean isBeat = audioFeatureManager.isBeat(elapsedTime, 0.025f); // 50ms window
            float targetVignette = 0.4f; // Base

            if (isBeat && frame.centroid > 2000) {
                targetVignette = 0.85f; // 1.0f = Snap shut
            }

            // Smooth decay could be handled by a persistent float state,
            // but for now let's try direct mapping or simple frame decay if I had a state
            // var.
            // Simplified: If beat, high intensity, else base.
            // To make it look good without state, we rely on the beat "window" being short.
            // Better: use interpolation or just precise mapping if beat_times aligned well.
            vignettingEffect.setIntensity(targetVignette);
        }

        // 4. Fisheye (General Volume)
        // 4. PSX Dither (Beat Reactive)
        if (isPsxEnabled) {
            boolean isBeat = audioFeatureManager.isBeat(elapsedTime, 0.05f); // Slightly wider window to catch the hit
            float ditherIntensity = 0f;
            if (isBeat) {
                // Use Percussive RMS for cleaner beat isolation
                // Scale heavily because we want it visible
                ditherIntensity = MathUtils.clamp(frame.rmsPercussive * 100.0f, 0.2f, 1.0f);
            }
            psxEffect.getConfiguration().setDitheringIntensity(ditherIntensity);
            psxEffect.getConfiguration().update();
        }

        // 5. Color Shift (Centroid)
        if (isLevelsEnabled) {
            // Map 1000 - 3000 to 0.0 - 1.0 for Hue?
            // Hue shift usually 0-1.
            float norm = MathUtils.clamp((frame.centroid - 1000f) / 2000f, 0f, 1f);
            // Shift mostly in a subtle range or full spectrum?
            // Let's try mapping to hue shift.
            levelsEffect.setHue(norm);
        }

        // 5. Draw scene
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        ScreenUtils.clear(0, 0, 0, 1f);

        // --- DRAW SHADER BACKGROUND ---
        if (shader.isCompiled()) {
            batch.setShader(shader);
            batch.begin();

            // Update Uniforms
            shader.setUniformf("iResolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0f);
            shader.setUniformf("iTime", elapsedTime);
            shader.setUniformf("iMouse", Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), 0f, 0f); // Mouse
                                                                                                                 // coords

            // Audio Uniforms
            // Switch to Percussive Data for more activity (responding to every hit, not
            // just main beats)
            // Scale: rmsPercussive is usually small (0.0 - 0.1). * 30.0 makes strong hits
            // ~1.0
            float percussiveSignal = frame.rmsPercussive * 80.0f;
            beatIntensity = MathUtils.clamp(percussiveSignal, 0.0f, 1.0f);

            shader.setUniformf("u_beat", beatIntensity);
            shader.setUniformf("u_intensity", frame.rmsTotal * 5.0f); // Scale RMS

            // Draw full screen quad
            // Note: resizing handled by iResolution, we just draw over the view
            batch.draw(blankTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            batch.end();
            batch.setShader(null); // Reset for other things
        }

        /*
         * Texture videoFrame = videoPlayer.getTexture();
         * if (videoFrame != null) {
         * batch.begin();
         * batch.draw(videoFrame, 0, 0, videoWidth * 2, videoHeight);
         * // batch.draw(videoFrame, 0, 0, videoWidth, videoHeight);
         * batch.end();
         * }
         */

        stage.draw();
        vfxManager.endInputCapture();

        // 6. Apply effects and render
        vfxManager.applyEffects();
        vfxManager.renderToScreen(
                stage.getViewport().getScreenX(),
                stage.getViewport().getScreenY(),
                stage.getViewport().getScreenWidth(),
                stage.getViewport().getScreenHeight());

        // 7. Record Frame
        if (videoRecorder.isRecording()) {
            videoRecorder.captureFrame();
        }
    }

    private void checkForJsonUpdates() {
        FileHandle jsonFile = Gdx.files.internal(currentJsonPath);
        if (jsonFile.exists()) {
            long modified = jsonFile.lastModified();
            if (modified > lastModifiedTime) {
                Gdx.app.log("HotReload", "Changes detected in " + currentJsonPath + ". Reloading...");
                lastModifiedTime = modified;
                performHotReload(jsonFile);
            }
        }
    }

    private void performHotReload(FileHandle jsonFile) {
        Array<LyricLine> newLyrics = parseLyricsAndTimestamps(currentJsonPath);

        // Smart Rewind Logic: Find the first difference
        float rewindTime = -1f;

        int limit = Math.min(timedLyrics.size, newLyrics.size);
        for (int i = 0; i < limit; i++) {
            LyricLine oldLine = timedLyrics.get(i);
            LyricLine newLine = newLyrics.get(i);

            if (!oldLine.equals(newLine)) {
                // Found a change!
                rewindTime = newLine.startTime;
                Gdx.app.log("HotReload", "Change found at index " + i + " time: " + rewindTime);
                break;
            }
        }

        // If no change found in the overlapping part, maybe a line was added at the
        // end?
        if (rewindTime == -1f && newLyrics.size != timedLyrics.size) {
            if (newLyrics.size > timedLyrics.size) {
                rewindTime = newLyrics.get(timedLyrics.size).startTime;
            } else {
                // Lines removed? Rewind to the start of the cut or just stay put?
                // Let's rewind to the last valid line of the new file.
                if (newLyrics.size > 0)
                    rewindTime = newLyrics.get(newLyrics.size - 1).startTime;
            }
        }

        // Update the data
        this.timedLyrics = newLyrics;
        this.lyrics = createLyrics(); // Update the simple string array too if needed

        // Apply Rewind
        if (rewindTime != -1f) {
            float targetTime = Math.max(0, rewindTime - 2.5f);
            Gdx.app.log("HotReload", "Rewinding to " + targetTime + "s");

            music.setPosition(targetTime);

            // Reset our precise timer to match the new music position
            elapsedTime = targetTime;
            startTimeNanos = TimeUtils.nanoTime() - (long) (targetTime * 1_000_000_000L);

            // Reset lyric index to match the new time
            currentLyricIndex = 0;
            for (int i = 0; i < timedLyrics.size; i++) {
                if (timedLyrics.get(i).startTime < targetTime) {
                    currentLyricIndex = i + 1;
                } else {
                    break;
                }
            }

            // Clear current text so it doesn't linger if we rewound before it
            typingLabel.setText("");
            // Or better, find the active lyric at this time?
            // For simplicity, we'll let the loop catch up or just clear it.
            // If we are in the middle of a lyric, we might want to show it.
            if (currentLyricIndex > 0 && currentLyricIndex <= timedLyrics.size) {
                // This is a bit tricky because we only store start times.
                // We don't know duration.
                // But usually we just wait for the next one.
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        vfxManager.resize(width, height);
        // if (videoPlayer != null) {
        // videoWidth = videoPlayer.getVideoWidth();
        // videoHeight = videoPlayer.getVideoHeight();
        // }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        vfxManager.dispose();
        crtEffect.dispose();
        bloomEffect.dispose();
        oldTvEffect.dispose();
        fisheyeEffect.dispose();
        filmGrainEffect.dispose();
        fxaaEffect.dispose();
        if (shader != null) {
            shader.dispose();
        }
        if (blankTexture != null) {
            blankTexture.dispose();
        }
        // if (videoPlayer != null) {
        // videoPlayer.dispose();
        // }
        if (psxEffect != null) {
            psxEffect.dispose();
        }
        if (videoRecorder != null && videoRecorder.isRecording()) {
            videoRecorder.stopRecording();
        }
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Input.Keys.NUM_1:
                        toggleEffect(psxEffect, isPsxEnabled = !isPsxEnabled, "PSX Effect");
                        break;
                    case Input.Keys.ESCAPE:
                        Gdx.app.exit();
                        break;
                    case Input.Keys.SPACE:
                        // Manual advance (legacy/debug)
                        currentLyricIndex++;
                        if (currentLyricIndex >= lyrics.size)
                            currentLyricIndex = 0;
                        typingLabel.restart(lyrics.get(currentLyricIndex));
                        break;
                    case Input.Keys.C:
                        toggleEffect(crtEffect, isCrtEnabled = !isCrtEnabled, "CRT");
                        break;
                    case Input.Keys.B:
                        toggleEffect(bloomEffect, isBloomEnabled = !isBloomEnabled, "Bloom");
                        break;
                    case Input.Keys.V:
                        toggleEffect(vignettingEffect, isVignetteEnabled = !isVignetteEnabled, "Vignette");
                        break;
                    case Input.Keys.O:
                        toggleEffect(oldTvEffect, isOldTvEnabled = !isOldTvEnabled, "Old TV");
                        break;
                    case Input.Keys.A:
                        toggleEffect(chromaticAberrationEffect,
                                isChromaticAberrationEnabled = !isChromaticAberrationEnabled, "Chromatic Aberration");
                        break;
                    case Input.Keys.F:
                        toggleEffect(filmGrainEffect, isFilmGrainEnabled = !isFilmGrainEnabled, "Film Grain");
                        break;
                    case Input.Keys.G:
                        toggleEffect(gaussianBlurEffect, isGaussianBlurEnabled = !isGaussianBlurEnabled,
                                "Gaussian Blur");
                        break;
                    case Input.Keys.R:
                        toggleEffect(radialBlurEffect, isRadialBlurEnabled = !isRadialBlurEnabled, "Radial Blur");
                        break;
                    case Input.Keys.L:
                        toggleEffect(lensFlareEffect, isLensFlareEnabled = !isLensFlareEnabled, "Lens Flare");
                        break;
                    case Input.Keys.E:
                        toggleEffect(fisheyeEffect, isFisheyeEnabled = !isFisheyeEnabled, "Fisheye");
                        break;
                    case Input.Keys.S:
                        toggleEffect(levelsEffect, isLevelsEnabled = !isLevelsEnabled, "Levels");
                        break;
                    case Input.Keys.Z:
                        toggleEffect(zoomEffect, isZoomEnabled = !isZoomEnabled, "Zoom");
                        break;
                    case Input.Keys.X:
                        toggleEffect(fxaaEffect, isFxaaEnabled = !isFxaaEnabled, "FXAA");
                        break;
                    case Input.Keys.N:
                        toggleEffect(nfaaEffect, isNfaaEnabled = !isNfaaEnabled, "NFAA");
                        break;
                    case Input.Keys.Q:
                        isPaused = !isPaused;
                        if (isPaused) {
                            music.pause();
                            pauseTimeNanos = TimeUtils.nanoTime();
                            Gdx.app.log("PLAYER", "--- PAUSED ---");
                        } else {
                            music.play();
                            // Adjust start time to account for the pause duration
                            long pauseDuration = TimeUtils.nanoTime() - pauseTimeNanos;
                            startTimeNanos += pauseDuration;
                            Gdx.app.log("PLAYER", "--- RESUMED ---");
                        }
                        break;
                    case Input.Keys.F12:
                        toggleRecording();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

    }

    private void toggleEffect(ChainVfxEffect effect, boolean enabled, String name) {
        if (enabled)
            vfxManager.addEffect(effect);
        else
            vfxManager.removeEffect(effect);
        Gdx.app.log("VFX_Toggle", name + ": " + (enabled ? "On" : "Off"));
    }

    private void toggleRecording() {
        if (videoRecorder.isRecording()) {
            videoRecorder.stopRecording();
        } else {
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String songName = config.getString("songName", "UnknownSong").replaceAll("[^a-zA-Z0-9.-]", "_");
            String outputPath = "recording_" + songName + "_" + timestamp + ".mp4";
            String musicPath = config.getString("musicPath", "music/mr_lizard.wav"); // Use configured music
            // Absolute path might be safer for FFmpeg
            FileHandle audioFile = Gdx.files.internal(musicPath);
            // We need a real file path. If it's internal in a jar this won't work, but for
            // desktop dev it usually works if file exists.
            String absoluteAudioPath = audioFile.file().getAbsolutePath();

            videoRecorder.startRecording(outputPath, absoluteAudioPath, Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight());
        }
    }

    private void setupFontsAndSkin() {
        String primaryFontPath = config.getString("primaryFont", "fonts/TitanOne-Regular.ttf");
        String secondaryFontPath = config.getString("secondaryFont", "fonts/RetroSide-MV0mY.otf");

        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal(primaryFontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.size = 132;// 96
        parameter1.color = Color.WHITE;
        parameter1.borderColor = Color.BLACK;
        parameter1.borderWidth = 5f;
        BitmapFont primaryBmp = generator1.generateFont(parameter1);

        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal(secondaryFontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 96;
        parameter2.color = Color.WHITE;
        parameter2.borderColor = Color.BLACK;
        parameter2.borderWidth = 5f;
        BitmapFont pixelBmp = generator2.generateFont(parameter2);

        generator1.dispose();
        generator2.dispose();

        Font primaryFont = new Font(primaryBmp);
        Font pixelFont = new Font(pixelBmp);

        String[] names = { "regular", "pixel" };
        Font[] fonts = { primaryFont, pixelFont };
        Font.FontFamily mainFamily = new Font.FontFamily(names, fonts);

        primaryFont.family = mainFamily;

        Styles.LabelStyle defaultLabelStyle = new Styles.LabelStyle();
        defaultLabelStyle.font = primaryFont;
        skin.add("default", defaultLabelStyle);

        KnownFonts.addGameIcons(primaryFont);
        KnownFonts.addNotoEmoji(primaryFont);
    }

    private Array<String> createLyrics() {
        Array<String> lyrics = new Array<>();
        for (LyricLine lyricLine : timedLyrics) {
            lyrics.add(lyricLine.text);
        }
        return lyrics;
    }
}
