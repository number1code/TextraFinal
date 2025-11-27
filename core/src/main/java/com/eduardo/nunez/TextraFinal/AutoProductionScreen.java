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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.scenes.scene2d.VideoActor;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.github.tommyettinger.textra.*;

public class AutoProductionScreen extends ScreenAdapter {

    private SpriteBatch batch;
    // --- Core LibGDX and UI Objects ---
    // private final Game game;
    private Stage stage;
    private Skin skin;
    private TypingLabel typingLabel;
    // --- Lyrics Data and State ---
    private Array<String> lyrics;

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

    private VideoPlayer videoPlayer;
    private VideoActor videoActor;
    private int videoWidth;
    private int videoHeight;

    // private final Game game; // Removed unused field

    public AutoProductionScreen() {
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

        // Initial Lyric Load
        timedLyrics = parseLyricsAndTimestamps(currentJsonPath);
        FileHandle jsonFile = Gdx.files.internal(currentJsonPath);
        lastModifiedTime = jsonFile.lastModified();

        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        FileHandle file = Gdx.files.internal(videoPath);
        try {
            videoPlayer.load(file);
        } catch (Exception e) {
            Gdx.app.log("loading video", "error: " + e.toString());
        }
        videoPlayer.play();
        videoPlayer.setLooping(true);
        videoActor = new VideoActor(videoPlayer);

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

        stage.addActor(videoActor);

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

        // Configure the effects for the desired look
        bloomEffect.setBloomIntensity(1.0f);
        vignettingEffect.setIntensity(1f);
        levelsEffect.setSaturation(0.55f);
        levelsEffect.setHue(0.6f);

        // Add effects to the manager. The order matters.
        // vfxManager.addEffect(crtEffect);
        // isCrtEnabled = true;
        // vfxManager.addEffect(bloomEffect);
        // isBloomEnabled = true;
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

        // 5. Draw scene
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        ScreenUtils.clear(0, 0, 0, 1f);

        Texture videoFrame = videoPlayer.getTexture();
        if (videoFrame != null) {
            batch.begin();
            // batch.draw(videoFrame, 0, 0, videoWidth, videoHeight / 2);
            batch.draw(videoFrame, 0, 0, videoWidth, videoHeight);
            batch.end();
        }

        stage.draw();
        vfxManager.endInputCapture();

        // 6. Apply effects and render
        vfxManager.applyEffects();
        vfxManager.renderToScreen(
                stage.getViewport().getScreenX(),
                stage.getViewport().getScreenY(),
                stage.getViewport().getScreenWidth(),
                stage.getViewport().getScreenHeight());
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
        if (videoPlayer != null) {
            videoWidth = videoPlayer.getVideoWidth();
            videoHeight = videoPlayer.getVideoHeight();
        }
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
        if (videoPlayer != null) {
            videoPlayer.dispose();
        }
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
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

    private void setupFontsAndSkin() {
        String primaryFontPath = config.getString("primaryFont", "fonts/TitanOne-Regular.ttf");
        String secondaryFontPath = config.getString("secondaryFont", "fonts/RetroSide-MV0mY.otf");

        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal(primaryFontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.size = 96;
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
