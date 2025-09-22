package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.scenes.scene2d.VideoActor;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.github.tommyettinger.textra.*;

public class VfxProductionScreen extends ScreenAdapter {

    private SpriteBatch batch;
    // --- Core LibGDX and UI Objects ---
    private final Game game;
    private Stage stage;
    private Skin skin;
    private TypingLabel typingLabel;
    // --- Lyrics Data and State ---
    private Array<String> lyrics;
    private int currentLyricIndex = 0;

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

    public VfxProductionScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        FileHandle file = Gdx.files.internal("test_video.webm");
        try {
            videoPlayer.load(file);
        }catch (Exception e){
            Gdx.app.log("loading video", "error: " + e.toString());
        }
        videoPlayer.play();
        videoActor = new VideoActor(videoPlayer);
        // --- 1. Data and Asset Setup ---
        lyrics = createLyrics(); // <-- Refactored: Lyrics are now loaded from a clean, separate method.
        skin = new Skin();
        batch = new SpriteBatch();
        // --- 2. Font and Skin Initialization ---
        // This could also be moved to a separate method if it grows larger.
        setupFontsAndSkin();

        // --- 3. Scene2D Setup ---
        stage = new Stage(new FitViewport(1080, 1920));//1080x1920 for non fisheye-videos

        typingLabel = new TypingLabel(lyrics.get(currentLyricIndex), skin);
        typingLabel.setAlignment(Align.center);
        typingLabel.setWrap(true);

        Table table = new Table();
        table.setFillParent(true);
        table.add(typingLabel).width(1000f);
        stage.addActor(table);

        stage.addActor(videoActor);

        // --- 4. GDX-VFX SETUP ---
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);
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
        fxaaEffect = new FxaaEffect(0.5f,0.5f,0.5f,true);
        nfaaEffect = new NfaaEffect(true);


        // Configure the effects for the desired look
        bloomEffect.setBaseIntensity(1.5f);
        bloomEffect.setBloomIntensity(2.0f);
        bloomEffect.setThreshold(0.3f);
        //
        vignettingEffect.setIntensity(2);
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
        vfxManager.addEffect(fisheyeEffect);
        isFisheyeEnabled = true;
        // --- 5. Input Processor Setup ---
        setupInput(); // <-- Refactored: Input handling is now in its own method.
    }

    /**
     * The main game loop, now cleaned up to focus only on rendering.
     */
    @Override
    public void render(float delta) {
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
        batch.draw(videoFrame,0,0);
        batch.end();
        stage.draw(); // The FitViewport correctly scales the drawing here

        vfxManager.endInputCapture();

        // 3. Apply the effects
        vfxManager.applyEffects();

        // 4. Clear the actual screen
        ScreenUtils.clear(0, 0, 0, 1f);

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
        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("BitcountInk-VariableFont_CRSV,ELSH,ELXP,SZP1,SZP2,XPN1,XPN2,YPN1,YPN2,slnt,wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.size = 96;
        parameter1.color = Color.WHITE;
        parameter1.borderColor = Color.BLACK;
        parameter1.borderWidth = 2f;
        parameter1.magFilter = Texture.TextureFilter.Linear;
        parameter1.minFilter = Texture.TextureFilter.Linear;

        BitmapFont bitmapFont1 = generator1.generateFont(parameter1);
        generator1.dispose();

        Font primaryTextraFont = new Font(bitmapFont1);
        Font pixelTextraFont = KnownFonts.getIBM8x16();

        skin.add("primary", primaryTextraFont);
        skin.add("pixel", pixelTextraFont);

        Styles.LabelStyle defaultLabelStyle = new Styles.LabelStyle();
        defaultLabelStyle.font = skin.get("primary", Font.class);
        skin.add("default", defaultLabelStyle);

        KnownFonts.addEmoji(primaryTextraFont);//disabling this allows some from GameIcons to work, this does seem to work with noto?
        //KnownFonts.addGameIcons(primaryTextraFont);//disabling this allows some from OpenMoji to work. Doesn't work with some noto.
        KnownFonts.addNotoEmoji(primaryTextraFont);
        KnownFonts.addMaterialDesignIcons(primaryTextraFont);
        KnownFonts.addOpenMoji(primaryTextraFont, true);

        KnownFonts.getStandardFamily();
    }

    /**
     * Creates and returns the array of lyrics.
     * This keeps the massive block of text out of the main setup logic in show().
     */
    private Array<String> createLyrics() {
        Array<String> lyrics = new Array<>();

        // lyrics.add("[%?SHINY][%150]Grime Gospel, Suno[%][%]");
        lyrics.add("[%?SHINY][%150]Concrete Requiem, Suno [%][%]");

// [Verse 1]
        lyrics.add("{FADE}{SLAM}[%125][%^neon][RED]City eats souls,[] [RED]{SQUASH}jaws unhinge{ENDSQUASH} for the feast{CLEARCOLOR} [+needle-jaws]");
        lyrics.add("{FADE}{FAST}Pavement cracked, [DARK_RED]blood seeps[/], {JOLT=0.5;10;0.5}baptized in the beast{ENDJOLT} [+drop of blood][+church]");
        lyrics.add("{FADE}{WIND=0.1;1.5;2;0.5}Wind howls[/], a {EMERGE}[CYAN]banshee[/]{ENDEMERGE} caught in the breeze [+wind face][+ghost]");
        lyrics.add("{FADE}[GRAY]Grime-covered dreams[/], they {SICK=0.8}rot[/]{ENDSICK}, no {SHRINK}release{ENDSHRINK}. [+zzz][+biohazard]");
        lyrics.add("{FADE}{BUMP}[%125][GRAY]Steel toes on the asphalt[/], {ENDJUMP}stomping the weak{ENDBUMP} [+boot][+person falling]");
        lyrics.add("{FADE}{SHAKE}Shadow-boxing with [RED]demons[/],{ENDSHAKE} they don't ever retreat [+person boxing][+devil]");
        lyrics.add("{FADE}[GREEN]Currency smells {SICK=0.5}sour[/]{ENDSICK}, hands dirty for keeps [+money with wings][+nauseated face]");
        lyrics.add("{FADE}{WAVE=0.05;2;2}Sleep's a mirage[/], {JOLT}[BLACK]nightmares sewn in the sheets{ENDJOLT} [+sleeping face][+scream]");

// [Verse 2]
        lyrics.add("{FADE}{WIND=0.1;2;3;0.5}[BLACK]Smoke stacks exhale[/], lungs {SICK=0.8}inhale the sin{ENDSICK} [+factory][+lungs]");
        lyrics.add("{FADE}[PURPLE]Knuckles bruised[/], they {SHAKE}scream[/]{ENDSHAKE}, {SLAM}[%150]this is how we begin{ENDSLAM} [+fist][+bomb]");
        lyrics.add("{FADE}The block's a {VAR=FIRE}furnace[/], we {BUMP}forge kin[/]{ENDBUMP} from the din [+fire][+people hugging]");
        lyrics.add("{FADE}[GRAY]Ashes in my veins[/], but the {HEARTBEAT=1.2}[ORANGE]fire's still within{ENDHEARTBEAT} [+bone][+red heart]");
        lyrics.add("{FADE}{BLINK=838181ff;000000;1.0;0.5}Streetlights flicker[/], {FASTER}Morse code from the ghosts{FASTER} [+light bulb][+ghost]");
        lyrics.add("{FADE}{SLOW}[%75]Kingpins whisper tales[/], but they all {SHRINK=1.0;1.0;true}decomposed{ENDSHRINK}. [+crown][+skull and crossbones]");
        lyrics.add("{FADE}{HEARTBEAT=0.8}Hunger’s a drumbeat[/], keeps my heart {EMERGE}composed{ENDEMERGE} [+drum][+beating heart]");
        lyrics.add("{FADE}Every crack in the sidewalk's a {SLIDE=1;0.5;true}map to the [PURPLE]unknown[/]{ENDSLIDE}. [+map][+question mark]");
        return lyrics;
    }
}
