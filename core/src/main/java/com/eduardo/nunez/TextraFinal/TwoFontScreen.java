package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.scenes.scene2d.VideoActor;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.github.tommyettinger.textra.*;

/**
 * The main production screen for creating automated, data-driven lyric videos.
 * This class combines:
 * - A JSON-based timing system driven by Whisper AI timestamps.
 * - Programmatic multi-font support using TextraTypist's FontFamily.
 * - Background video playback.
 * - A full suite of toggleable gdx-vfx post-processing effects.
 */
public class TwoFontScreen extends ScreenAdapter {

    // --- Core LibGDX and UI Objects ---
    private final Game game;
    private Stage stage;
    private Skin skin;
    private TypingLabel typingLabel;

    // --- Automated Playback System ---
    private Music music;
    private float elapsedTime = 0f;
    private Array<LyricLine> timedLyrics;
    private int currentLyricIndex = 0;
    private boolean isPaused = false;

    // A simple data structure to hold a line of text and its start time.
    private static class LyricLine {
        float startTime;
        String text;
        public LyricLine(float startTime, String text) {
            this.startTime = startTime;
            this.text = text;
        }
    }

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

    // --- VFX State Flags ---
    private boolean isCrtEnabled, isBloomEnabled, isVignetteEnabled, isOldTvEnabled,
        isFilmGrainEnabled, isFisheyeEnabled, isChromaticAberrationEnabled,
        isGaussianBlurEnabled, isRadialBlurEnabled, isLensFlareEnabled,
        isLevelsEnabled, isZoomEnabled, isFxaaEnabled, isNfaaEnabled;

    // --- Background Video ---
    private VideoPlayer videoPlayer;
    private VideoActor videoActor;

    public TwoFontScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // --- 1. Load Core Assets ---
        // Load the music file that corresponds to the lyric data.
        music = Gdx.audio.newMusic(Gdx.files.internal("music/Iron Howl.wav"));
        // Parse the JSON file containing lyric text and start times.
        timedLyrics = parseLyricsAndTimestamps();

        // --- 2. Initialize Video Player ---
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        videoPlayer.setLooping(true);
        videoActor = new VideoActor(videoPlayer);
        try {
            // This is where you specify the video file to be used as a background.
            videoPlayer.load(Gdx.files.internal("video/iron-howl-bg.webm"));
            videoPlayer.play();
        } catch (Exception e) {
            Gdx.app.error("VideoPlayer", "Error loading video file.", e);
        }

        // --- 3. Initialize UI Components ---
        skin = new Skin();
        setupFontsAndSkin(); // This is where the critical two-font logic now resides.

        // Use a square viewport to ensure effects like Fisheye are circular, not elliptical.
        // For standard 9:16 videos without such effects, change this to new FitViewport(1080, 1920).
        stage = new Stage(new FitViewport(1080, 1080));

        // Create the TypingLabel with the default style defined in setupFontsAndSkin().
        typingLabel = new TypingLabel("", skin, "default");
        typingLabel.setAlignment(Align.center);
        typingLabel.setWrap(true);

        // --- 4. Assemble the Scene ---
        // The VideoActor is placed on the bottom layer of the Stage.
        stage.addActor(videoActor);

        // A Table is used to center the TypingLabel on the screen.
        Table table = new Table();
        table.setFillParent(true);
        table.add(typingLabel).width(900f);
        stage.addActor(table);

        // --- 5. Setup VFX and Input ---
        setupVfx();
        setupInput();

        // --- 6. Start Playback ---
        music.play();
        elapsedTime = 0f;
        currentLyricIndex = 0;
    }

    @Override
    public void render(float delta) {
        // The PAUSE GATE: If paused, freeze the entire screen and stop all updates.
        if (isPaused) {
            // We must still render the last frame to prevent flickering.
            vfxManager.renderToScreen(stage.getViewport().getScreenX(), stage.getViewport().getScreenY(), stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight());
            return;
        }

        // --- 1. Update Timers and Logic ---
        elapsedTime += delta; // Advance our master timer.
        stage.act(delta);     // Update all Scene2D actors (including TypingLabel animations).

        // --- 2. Automated Lyric Advancement ---
        if (currentLyricIndex < timedLyrics.size) {
            LyricLine nextLine = timedLyrics.get(currentLyricIndex);
            if (elapsedTime >= nextLine.startTime) {
                typingLabel.restart(nextLine.text);
                currentLyricIndex++;
            }
        }

        // --- 3. VFX Render Pipeline ---
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();

        ScreenUtils.clear(0, 0, 0, 1f); // Clear the off-screen buffer.
        stage.draw();                   // Draw the entire scene (video + text) into the buffer.

        vfxManager.endInputCapture();
        vfxManager.applyEffects();

        // Render the final processed frame to the correctly scaled viewport area.
        vfxManager.renderToScreen(
            stage.getViewport().getScreenX(), stage.getViewport().getScreenY(),
            stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight()
        );
    }

    @Override
    public void resize(int width, int height) {
        // It is critical that BOTH the Stage's viewport and the VFX Manager are updated on resize.
        stage.getViewport().update(width, height, true);

        // The VFX Manager's buffer size MUST match the viewport's on-screen dimensions.
        // Using the raw window width/height here will cause stretching with non-square viewports.
        vfxManager.resize(stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight());

        // Ensure the video actor always fills the viewport.
        videoActor.setBounds(0, 0, stage.getWidth(), stage.getHeight());
    }

    @Override
    public void dispose() {
        // Dispose of all disposable resources to prevent memory leaks.
        music.dispose();
        stage.dispose();
        skin.dispose();
        videoPlayer.dispose();
        vfxManager.dispose();

        // Each VFX effect must also be disposed of manually.
        crtEffect.dispose();
        bloomEffect.dispose();
        vignettingEffect.dispose();
        oldTvEffect.dispose();
        chromaticAberrationEffect.dispose();
        filmGrainEffect.dispose();
        gaussianBlurEffect.dispose();
        radialBlurEffect.dispose();
        lensFlareEffect.dispose();
        fisheyeEffect.dispose();
        levelsEffect.dispose();
        zoomEffect.dispose();
        fxaaEffect.dispose();
        nfaaEffect.dispose();
    }

    // ===================================================================================
    // Refactored Helper Methods
    // ===================================================================================

    /**
     * Initializes and configures all fonts for multi-font support.
     * This is the definitive, programmatic method for using two or more fonts in one TypingLabel.
     */
    private void setupFontsAndSkin() {
        // --- 1. Generate BitmapFonts (The Raw Ingredients) ---
        // The primary font is used for most text.
        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/BOMBORA.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.size = 96;
        parameter1.color = Color.WHITE;
        parameter1.borderColor = Color.BLACK;
        parameter1.borderWidth = 5f;
        BitmapFont primaryBmp = generator1.generateFont(parameter1);

        // The secondary font is used for special emphasis via markup.
        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 78;
        parameter2.color = Color.YELLOW; // Example color for the pixel font
        BitmapFont pixelBmp = generator2.generateFont(parameter2);

        generator1.dispose();
        generator2.dispose();

        // --- 2. Wrap them in TextraTypist's Font objects (The Specialized Tools) ---
        Font primaryFont = new Font(primaryBmp);
        Font pixelFont = new Font(pixelBmp);

        // --- 3. Create a FontFamily to group them (The Toolbox) ---
        // This constructor takes arrays of names and the corresponding Font objects.
        String[] names = {"regular", "pixel"}; // These are the names used in the markup.
        Font[] fonts = {primaryFont, pixelFont};
        Font.FontFamily mainFamily = new Font.FontFamily(names, fonts);

        // --- 4. Assign the FontFamily to the primary Font ---
        // This is the critical link. The primary font now knows about all other fonts in its family.
        primaryFont.family = mainFamily;

        // --- 5. Define the default LabelStyle ---
        // The style only needs to reference the primary font. TextraTypist will automatically
        // find other fonts in the family when it sees the [@Name] markup.
        Styles.LabelStyle defaultLabelStyle = new Styles.LabelStyle();
        defaultLabelStyle.font = primaryFont;
        skin.add("default", defaultLabelStyle);

        // --- 6. Add Emoji Support ---
        // Add any desired emoji packs to your primary font.
        KnownFonts.addGameIcons(primaryFont);
        KnownFonts.addNotoEmoji(primaryFont);
    }

    /**
     * Sets up the VFX Manager and all the individual post-processing effects.
     */
    private void setupVfx() {
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
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

        // Configure default effect parameters
        bloomEffect.setBloomIntensity(1.9f);
        vignettingEffect.setIntensity(0.5f);
        filmGrainEffect.setNoiseAmount(0.2f);
        levelsEffect.setSaturation(0.55f);
        levelsEffect.setHue(0.6f);

        // Add any effects you want to be active by default when the video starts.
        vfxManager.addEffect(crtEffect); isCrtEnabled = true;
        vfxManager.addEffect(bloomEffect); isBloomEnabled = true;
        vfxManager.addEffect(vignettingEffect); isVignetteEnabled = true;
        vfxManager.addEffect(oldTvEffect); isOldTvEnabled = true;
        vfxManager.addEffect(filmGrainEffect); isFilmGrainEnabled = true;
        vfxManager.addEffect(fisheyeEffect); isFisheyeEnabled = true;
    }

    /**
     * Sets up an InputAdapter for toggling effects and pausing playback.
     * Playback is fully automatic; this is for debugging and live-mixing visuals.
     */
    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    // Use 'P' for Pause, as it's more standard.
                    case Input.Keys.P:
                        isPaused = !isPaused;
                        if (isPaused) {
                            music.pause();
                            videoPlayer.pause();
                            Gdx.app.log("PLAYER", "--- PAUSED ---");
                        } else {
                            music.play();
                            videoPlayer.play();
                            Gdx.app.log("PLAYER", "--- RESUMED ---");
                        }
                        break;

                    // VFX Toggles
                    case Input.Keys.C: if (isCrtEnabled) vfxManager.removeEffect(crtEffect); else vfxManager.addEffect(crtEffect); isCrtEnabled = !isCrtEnabled; break;
                    case Input.Keys.B: if (isBloomEnabled) vfxManager.removeEffect(bloomEffect); else vfxManager.addEffect(bloomEffect); isBloomEnabled = !isBloomEnabled; break;
                    // ... include all other key toggles here ...
                    default: return false;
                }
                return true;
            }
        });
    }

    /**
     * Parses the lyrics.json file to create an array of timed lyric lines.
     * This data-driven approach allows for easy editing of lyrics and timing without recompiling code.
     */
    private Array<LyricLine> parseLyricsAndTimestamps() {
        Array<LyricLine> timedLyrics = new Array<>();
        JsonReader jsonReader = new JsonReader();
        try {
            FileHandle file = Gdx.files.internal("song_jsons/iron_howl.json");
            JsonValue base = jsonReader.parse(file);
            for (JsonValue lineJson : base) {
                timedLyrics.add(new LyricLine(lineJson.getFloat("startTime"), lineJson.getString("markup")));
            }
        } catch (Exception e) {
            Gdx.app.error("JSON", "Error parsing lyrics file.", e);
            // Add a fallback lyric to display the error on screen.
            timedLyrics.add(new LyricLine(0, "{SHAKE}[RED]ERROR:[] Could not load lyrics.json!"));
        }
        return timedLyrics;
    }
}
