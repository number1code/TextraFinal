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
    private final Game game;
    private Stage stage;
    private Skin skin;
    private Skin testSkin;
    private TypingLabel typingLabel;
    // --- Lyrics Data and State ---
    private Array<String> lyrics;
    //private int currentLyricIndex = 0;

    //* NEW CODE for whisper AI based automatic timing *//
    // A simple helper class to store a lyric and its start time together
    private static class LyricLine {
        float startTime;
        String text;

        public LyricLine(float startTime, String text) {
            this.startTime = startTime;
            this.text = text;
        }
    }
    private Music music; // LibGDX class for handling streaming music
    private float elapsedTime = 0f; // Our authoritative timer
    // We will slightly change this from an Array<String>
    private Array<LyricLine> timedLyrics;
    private int currentLyricIndex = 0;
    private boolean isPaused = false;
    //* END NEW CODE for whisper AI based automatic timing *//

    // --- gdx-vfx Post-Processing Objects ---
    private VfxManager vfxManager;
    private CrtEffect crtEffect;
    private BloomEffect bloomEffect;
    private VignettingEffect vignettingEffect;
    private OldTvEffect oldTvEffect;
    private ChromaticAberrationEffect chromaticAberrationEffect;
    private FilmGrainEffect filmGrainEffect;
    private GaussianBlurEffect gaussianBlurEffect;
    private MotionBlurEffect    motionBlurEffect;
    private  RadialBlurEffect radialBlurEffect;
    private LensFlareEffect lensFlareEffect;
    private FisheyeEffect fisheyeEffect;
    private  LevelsEffect levelsEffect;
    private ZoomEffect zoomEffect;
    private FxaaEffect fxaaEffect;
    private NfaaEffect nfaaEffect;
    // === Effect State Flags ===
    // These are initialized to 'true' because they are added to the VfxManager in the show() method by default.
    private boolean isCrtEnabled = false;
    private boolean isBloomEnabled = false;
    private boolean isVignetteEnabled = false;
    private boolean isOldTvEnabled = false;
    private boolean isFilmGrainEnabled = false;
    private boolean isFisheyeEnabled = false;

    // These are initialized to 'false' because they are not added by default.
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


    public AutoProductionScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        music = Gdx.audio.newMusic(Gdx.files.internal("music/mr_lizard.wav"));
        timedLyrics = parseLyricsAndTimestamps();

        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        //FileHandle file = Gdx.files.internal("test_video.webm");
        //FileHandle file = Gdx.files.internal("warpath.webm");
        //FileHandle file = Gdx.files.internal("2023_video-effectapp.webm");
        //FileHandle file = Gdx.files.internal("monotone-weapons-bg-video.webm");//too narrow video
        //FileHandle file = Gdx.files.internal("manic-throne-video.webm");
        //FileHandle file = Gdx.files.internal("aggro_video-effectapp.webm");
//        FileHandle file = Gdx.files.internal("video/Vapor-vid-effectapp.webm");
        FileHandle file = Gdx.files.internal("video/mrlizardsongtimelapse.webm");
//        FileHandle file = Gdx.files.internal("video/sloppylizard.webm");
        try {
//            videoPlayer.load(file);
            videoPlayer.load(file);
        }catch (Exception e){
            Gdx.app.log("loading video", "error: " + e.toString());
        }
        videoPlayer.play();
        videoPlayer.setLooping(true);
        videoActor = new VideoActor(videoPlayer);
        // --- 1. Data and Asset Setup ---
        lyrics = createLyrics(); // <-- Refactored: Lyrics are now loaded from a clean, separate method.
        skin = new Skin();
        //Skin skin = new Skin(Gdx.files.internal("assets/skin.json")); //Error reading file
        batch = new SpriteBatch();
        // --- 2. Font and Skin Initialization ---
        // This could also be moved to a separate method if it grows larger.
        setupFontsAndSkin();

        // --- 3. Scene2D Setup ---
        stage = new Stage(new FitViewport(1080, 1920));//1080x1920 for non fisheye-videos

        //typingLabel = new TypingLabel(lyrics.get(currentLyricIndex), skin);
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
        vfxManager.setBlendingEnabled(true);//WHAT?
        crtEffect = new CrtEffect();
        bloomEffect = new BloomEffect();
        vignettingEffect = new VignettingEffect(false);//
        oldTvEffect = new OldTvEffect();
        chromaticAberrationEffect = new ChromaticAberrationEffect(2);//
        filmGrainEffect = new FilmGrainEffect();
        gaussianBlurEffect = new GaussianBlurEffect();
        //motionBlurEffect = new MotionBlurEffect(Pixmap.Format.RGBA8888,1.0);
        radialBlurEffect = new RadialBlurEffect(2);
        lensFlareEffect = new LensFlareEffect();
        fisheyeEffect = new FisheyeEffect();
        levelsEffect = new LevelsEffect();
        zoomEffect = new ZoomEffect();
        fxaaEffect = new FxaaEffect(0.15f,1,1,true);
        nfaaEffect = new NfaaEffect(true);


        // Configure the effects for the desired look
        //bloomEffect.setBaseIntensity(1.0f);
        bloomEffect.setBloomIntensity(1.0f);
        //bloomEffect.setThreshold(0.5f);
        //
        vignettingEffect.setIntensity(1f);
        levelsEffect.setSaturation(0.55f);
        levelsEffect.setHue(0.6f);
        //levelsEffect.setGamma(0.5f);
        // Add effects to the manager. The order matters.
        vfxManager.addEffect(crtEffect);
        isCrtEnabled = true;
        vfxManager.addEffect(bloomEffect);
        isBloomEnabled = true;
        vfxManager.addEffect(vignettingEffect);
        isVignetteEnabled = true;

        vfxManager.addEffect(oldTvEffect);
        isOldTvEnabled = true;
        vfxManager.addEffect(filmGrainEffect);
        isFilmGrainEnabled = true;
        filmGrainEffect.setNoiseAmount(0.2f);
        vfxManager.addEffect(fisheyeEffect);
        isFisheyeEnabled = true;
        // --- 5. Input Processor Setup ---
        setupInput(); // <-- Refactored: Input handling is now in its own method.

        // 5. Start the Music
        music.play();
        elapsedTime = 0f; // Reset our timer
        currentLyricIndex = 0;
    }

    private Array<LyricLine> parseLyricsAndTimestamps() {
        // Create an array to hold our final, structured lyric lines
        Array<LyricLine> timedLyrics = new Array<>();

        // Create a JSON reader
        JsonReader jsonReader = new JsonReader();

        // Point to the JSON file in your assets folder
        FileHandle file = Gdx.files.internal("song_jsons/mr_lizard.json"); // Make sure to name your file this!

        // Parse the entire file into a structured JSON object
        JsonValue base = jsonReader.parse(file);

        // The JSON file is an array of objects, so we iterate through it
        for (JsonValue lineJson : base) {
            // Get the "startTime" value as a float from the JSON object
            float startTime = lineJson.getFloat("startTime");

            // Get the "markup" value as a string from the JSON object
            String markupText = lineJson.getString("markup");

            // Create a new LyricLine object with the data and add it to our array
            LyricLine lyricLine = new LyricLine(startTime, markupText);
            timedLyrics.add(lyricLine);
        }

        // Return the fully populated array, ready for rendering
        return timedLyrics;
    }

    /**
     * The main game loop, now cleaned up to focus only on rendering.
     */
    @Override
    public void render(float delta) {
        // 1. Update our master timer
        if (isPaused){
            return;
        }
        elapsedTime += delta;

        // --- 2. AUTOMATIC LYRIC ADVANCEMENT ---
        // Check if there are more lyrics left to display
        if (currentLyricIndex < timedLyrics.size) {
            // Get the next lyric line we're waiting for
            LyricLine nextLine = timedLyrics.get(currentLyricIndex);

            // If our music playhead has passed the start time of this lyric...
            if (elapsedTime >= nextLine.startTime) {
                // ...display it on the screen!
                typingLabel.restart(nextLine.text);

                // And advance our index so we're ready for the *next* line
                currentLyricIndex++;
            }
        }

        // 1. Update your scene's logic
        stage.act(delta);

        // 2. Draw your scene into the VfxManager's framebuffer
        vfxManager.cleanUpBuffers();
        vfxManager.beginInputCapture();
        //ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        ScreenUtils.clear(0, 0, 0, 1f);
        //drawing video in the background
        Texture videoFrame = videoPlayer.getTexture();
        batch.begin();
        batch.draw(videoFrame,0,0, videoWidth, videoHeight/2);//for 720p x=180, vw + 180
        batch.end();

        stage.draw(); // The FitViewport correctly scales the drawing here

        vfxManager.endInputCapture();

        // 3. Apply the effects
        vfxManager.applyEffects();

        // 4. Clear the actual screen
        //ScreenUtils.clear(0, 0, 0, 1f);

        // 5. Render the final result to the specific screen area calculated by the viewport.
        // This is the key to solving the scaling and positioning problem.
        vfxManager.renderToScreen(
            stage.getViewport().getScreenX(),      // The x-coordinate of the letterbox/pillarbox
            stage.getViewport().getScreenY(),      // The y-coordinate of the letterbox/pillarbox
            stage.getViewport().getScreenWidth(),  // The pixel width of the scaled viewport
            stage.getViewport().getScreenHeight()  // The pixel height of the scaled viewport
        );
    }

    /**
     * Correctly handles resizing for both the Stage's viewport and the VfxManager's buffers.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        vfxManager.resize(width, height); // <-- CRITICAL: This was missing before.
        videoWidth = videoPlayer.getVideoWidth();
        videoHeight = videoPlayer.getVideoHeight();
        Gdx.app.log("video dimensions: " ,"width: " + videoWidth + " height: " + videoHeight);
    }

    /**
     * Disposes of all managed resources to prevent memory leaks.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        // --- PROPER DISPOSAL OF VFX RESOURCES ---
        vfxManager.dispose();
        crtEffect.dispose();      // <-- CRITICAL: Effects must be disposed manually.
        bloomEffect.dispose();    // <-- CRITICAL: Effects must be disposed manually.
        oldTvEffect.dispose();
        fisheyeEffect.dispose();
        filmGrainEffect.dispose();
        fxaaEffect.dispose();
    }

    // ===================================================================================
    // Refactored Helper Methods
    // ===================================================================================

    /**
     * Sets up an InputAdapter to handle user input in an event-driven way.
     * This version uses boolean flags to correctly add/remove effects for toggling.
     */
    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    // --- Lyric Cycling ---
                    case Input.Keys.SPACE:
                        currentLyricIndex++;
                        if (currentLyricIndex >= lyrics.size) {
                            currentLyricIndex = 0;
                        }
                        typingLabel.restart(lyrics.get(currentLyricIndex));
                        break;

                    // --- VFX Toggles ---
                    case Input.Keys.C:
                        if (isCrtEnabled) vfxManager.removeEffect(crtEffect); else vfxManager.addEffect(crtEffect);
                        isCrtEnabled = !isCrtEnabled;
                        Gdx.app.log("VFX_Toggle", "CRT Effect: " + (isCrtEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.B:
                        if (isBloomEnabled) vfxManager.removeEffect(bloomEffect); else vfxManager.addEffect(bloomEffect);
                        isBloomEnabled = !isBloomEnabled;
                        Gdx.app.log("VFX_Toggle", "Bloom Effect: " + (isBloomEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.V:
                        if (isVignetteEnabled) vfxManager.removeEffect(vignettingEffect); else vfxManager.addEffect(vignettingEffect);
                        isVignetteEnabled = !isVignetteEnabled;
                        Gdx.app.log("VFX_Toggle", "Vignette Effect: " + (isVignetteEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.O:
                        if (isOldTvEnabled) vfxManager.removeEffect(oldTvEffect); else vfxManager.addEffect(oldTvEffect);
                        isOldTvEnabled = !isOldTvEnabled;
                        Gdx.app.log("VFX_Toggle", "Old TV Effect: " + (isOldTvEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.A:
                        if (isChromaticAberrationEnabled) vfxManager.removeEffect(chromaticAberrationEffect); else vfxManager.addEffect(chromaticAberrationEffect);
                        isChromaticAberrationEnabled = !isChromaticAberrationEnabled;
                        Gdx.app.log("VFX_Toggle", "Chromatic Aberration: " + (isChromaticAberrationEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.F:
                        if (isFilmGrainEnabled) vfxManager.removeEffect(filmGrainEffect); else vfxManager.addEffect(filmGrainEffect);
                        isFilmGrainEnabled = !isFilmGrainEnabled;
                        Gdx.app.log("VFX_Toggle", "Film Grain Effect: " + (isFilmGrainEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.G:
                        if (isGaussianBlurEnabled) vfxManager.removeEffect(gaussianBlurEffect); else vfxManager.addEffect(gaussianBlurEffect);
                        isGaussianBlurEnabled = !isGaussianBlurEnabled;
                        Gdx.app.log("VFX_Toggle", "Gaussian Blur: " + (isGaussianBlurEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.R:
                        if (isRadialBlurEnabled) vfxManager.removeEffect(radialBlurEffect); else vfxManager.addEffect(radialBlurEffect);
                        isRadialBlurEnabled = !isRadialBlurEnabled;
                        Gdx.app.log("VFX_Toggle", "Radial Blur: " + (isRadialBlurEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.L:
                        if (isLensFlareEnabled) vfxManager.removeEffect(lensFlareEffect); else vfxManager.addEffect(lensFlareEffect);
                        isLensFlareEnabled = !isLensFlareEnabled;
                        Gdx.app.log("VFX_Toggle", "Lens Flare: " + (isLensFlareEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.E:
                        if (isFisheyeEnabled) vfxManager.removeEffect(fisheyeEffect); else vfxManager.addEffect(fisheyeEffect);
                        isFisheyeEnabled = !isFisheyeEnabled;
                        Gdx.app.log("VFX_Toggle", "Fisheye Effect: " + (isFisheyeEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.S:
                        if (isLevelsEnabled) vfxManager.removeEffect(levelsEffect); else vfxManager.addEffect(levelsEffect);
                        isLevelsEnabled = !isLevelsEnabled;
                        Gdx.app.log("VFX_Toggle", "Levels Effect: " + (isLevelsEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.Z:
                        if (isZoomEnabled) vfxManager.removeEffect(zoomEffect); else vfxManager.addEffect(zoomEffect);
                        isZoomEnabled = !isZoomEnabled;
                        Gdx.app.log("VFX_Toggle", "Zoom Effect: " + (isZoomEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.X:
                        if (isFxaaEnabled) vfxManager.removeEffect(fxaaEffect); else vfxManager.addEffect(fxaaEffect);
                        isFxaaEnabled = !isFxaaEnabled;
                        Gdx.app.log("VFX_Toggle", "FXAA Effect: " + (isFxaaEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.N:
                        if (isNfaaEnabled) vfxManager.removeEffect(nfaaEffect); else vfxManager.addEffect(nfaaEffect);
                        isNfaaEnabled = !isNfaaEnabled;
                        Gdx.app.log("VFX_Toggle", "NFAA Effect: " + (isNfaaEnabled ? "On" : "Off"));
                        break;

                    case Input.Keys.Q:
                        isPaused = !isPaused;
                        if (isPaused) {
                            music.pause(); // Pause the music
                            Gdx.app.log("PLAYER", "--- PAUSED ---");
                        } else {
                            music.play(); // Resume the music
                            Gdx.app.log("PLAYER", "--- RESUMED ---");
                        }
                        break;

                    default:
                        return false; // The input was not handled.
                }
                return true; // The input was handled.
            }
        });
    }

    /**
     * Initializes and configures all fonts and adds them to the skin.
     */
    private void setupFontsAndSkin() {
        // --- 1. Generate BitmapFonts (The Raw Ingredients) ---
        // The primary font is used for most text.
//        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RetroSide-MV0mY.otf"));
        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/TitanOne-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.size = 96;
        parameter1.color = Color.WHITE;
        parameter1.borderColor = Color.BLACK;
        parameter1.borderWidth = 5f;
        BitmapFont primaryBmp = generator1.generateFont(parameter1);

        // The secondary font is used for special emphasis via markup.
//        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Teko-VariableFont_wght.ttf"));
        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("fonts/RetroSide-MV0mY.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 96;
        parameter2.color = Color.WHITE; // Example color for the pixel font
        parameter2.borderColor = Color.BLACK;
        parameter2.borderWidth = 5f;
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
     * Creates and returns the array of lyrics.
     * This keeps the massive block of text out of the main setup logic in show().
     */
    private Array<String> createLyrics() {
        Array<String> lyrics = new Array<>();
        //lyrics.add("[%?SHINY][%150]Aggression, 10xdev_art[%][%]");
        for (LyricLine lyricLine : timedLyrics){
            lyrics.add(lyricLine.text);
        }



        return lyrics;
    }
}

