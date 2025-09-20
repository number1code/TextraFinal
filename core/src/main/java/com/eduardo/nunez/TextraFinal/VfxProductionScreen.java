package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
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
    public VfxProductionScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
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
        table.add(typingLabel).width(1020f);
        stage.addActor(table);

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

        // Add effects to the manager. The order matters.
        //vfxManager.addEffect(crtEffect);
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

        KnownFonts.addEmoji(primaryTextraFont);
        KnownFonts.addGameIcons(primaryTextraFont);
        KnownFonts.addNotoEmoji(primaryTextraFont);
        KnownFonts.addMaterialDesignIcons(primaryTextraFont);
        KnownFonts.addOpenMoji(primaryTextraFont, true);
    }

    /**
     * Creates and returns the array of lyrics.
     * This keeps the massive block of text out of the main setup logic in show().
     */
    private Array<String> createLyrics() {
        Array<String> lyrics = new Array<>();
        lyrics.add("[%?SHINY][%150]Phantom Glow, Suno[%][%]");

// [Verse 1]
        lyrics.add("{FADE=1;1;0.5}{FAST}{WIND=0.1;1;2;0.5}[GRAY]Shadows[CLEARCOLOR] creeping, past midnight tickin{SLOWER}g slow, [+new moon][+alarm clock]");
        lyrics.add("{FADE}{FAST}{EMERGE}[CYAN]Hoodie up, I'm the {RAINBOW}phantom glow{ENDRAINBOW},[CLEARCOLOR] [+man superhero, medium-dark skin tone] [+glowing star]");
        lyrics.add("{FADE}{FAST}{SICK=0.8}[BLACK]Smoke thick[/], {HEARTBEAT=1.0}lungs tight[/], {WAVE=0.05;1;1}streets whisper low{ENDWAVE}, [+lungs] [+cigarette]");
        lyrics.add("{FADE}{FAST}[%125]Concrete jungle[%] where the {SPIRAL=0.8;0.3}lost souls grow{ENDSPIRAL}. [+cityscape][+person walking]");

        lyrics.add("{FADE}{FAST}[RED]Eyes red[/], mind spinning like a {SPIN}carousel{ENDSPIN}, [+eyes] [+ferris wheel]");
        lyrics.add("{FADE}{FAST}{JOLT=1.0;5.0;inf;0.5;000000ff;777777ff}Memories cut deep, sharper than a{ENDJOLT} {SHAKE}scalpel,{ENDSHAKE} [+brain] [+kitchen knife]");
        lyrics.add("{FADE}{FAST}[%75]Dreams dissolve[%] in the {HANG=0.4;1}devil's cracked chapel{ENDHANG}, [+zzz][+ogre]");
        lyrics.add("{FADE}{FAST}{SLOW}{HEARTBEAT=0.15;1.0}Each breath stolen, life fragile as an apple [+lungs]{ENDHEARTBEAT} [+red apple]");

// [Chorus]
        lyrics.add("{FADE}{FAST}{GRADIENT=BLACK;PURPLE;0.5}[%175]{CROWD}Phantom glow{ENDCROWD}{ENDGRADIENT}[%], where the {WIND=0.1;2;3;0.5}dark winds blow{ENDWIND}, [+wind face] {CROWD}[+ghost]{ENDCROWD}");
        lyrics.add("{FADE}[%125]{FASTER}Every step heavy[%], every move {SLOW}too slow, {HANG}[+foot]{ENDHANG}[+snail]");
        lyrics.add("{FADE}{FAST}{GRADIENT=BLACK;PURPLE;0.5}[%175]{CROWD}Phantom glow{ENDCROWD}{ENDGRADIENT}[%], I'm the {EMERGE}ghost they know{ENDEMERGE}, {CROWD}[+ghost] [+waving hand]{ENDCROWD}");
        lyrics.add("{FADE}Through the {WAVE=0.05;1.5;1}haze[/], I'm the {SICK=0.8}[RED]curse they sow{ENDSICK}.{CLEARCOLOR}");

// [Verse 2]
        lyrics.add("{FADE}{SHAKE=0.3;1.0}Echoes of sirens{ENDSHAKE}, blend with the {SQUASH}bassline{ENDSQUASH}, [+police car light] [+speaker high volume] ");
        lyrics.add("{FADE}{HEARTBEAT=0.4;1.0}Hearbeat syncopated, life on a{ENDHEARTBEAT} {BLINK=FF0000ff;000000;0.8;0.7}[RED]flatline,[/]{ENDBLINK}  [+beating heart] [+chart decreasing]");
        lyrics.add("{FADE}[%125][BLACK]Corners cold[/], {HANG=0.8;0.5}currency in [%?WHITE OUTLINE]chalk outlines[%]{ENDHANG}{CLEARCOLOR},");
        lyrics.add("{FADE}{FAST}Fate's a {SPIN=0.6;1.0;false}dealer{ENDSPIN}, hand dealt in {JOLT=1.0;1.0;inf;0.5;ffffffff;f99d0fff}landmines{ENDJOLT}.[+game die][+bomb]");

        lyrics.add("{FADE}{FAST}[GRAY]Hollow laughs ricochet off the {SLIDE=1;0.5;true}liquor store{ENDSLIDE},[CLEARCOLOR][+skull][+bottle with popping cork]");
        lyrics.add("{FADE}[%75]Futures pawned[%], {SICK=0.8}[BLACK]dreams dead on the floor[/]{ENDSICK}, [+broken heart][+coffin]");
        lyrics.add("{FADE}[%125]Grit so thick[%], it {RAINBOW}stains to the core{ENDRAINBOW}, [+grinning face][+brown heart]");
        lyrics.add("{FADE}{ATTENTION}Reality's a wolf{ENDATTENTION} and it's {SHAKE}[RED]scratching at my door{ENDSHAKE}. [+wolf] [+door]");

// [Bridge]
        lyrics.add("{FADE}{FAST}[%125]Fingers on the glass[%], {WIND=0.1;1;2;0.5}fogged with regret{ENDWIND}, [+backhand index pointing right] [+fog]");
        lyrics.add("{FADE}{SPIRAL=1;0.5}Chasing shadows{ENDSPIRAL}, can't {JUMP}outrun the debt{ENDJUMP}, [+person running][+money with wings]");
        lyrics.add("{FADE}Past in the rearview{ENDFADE}, [GRAY]{WAVE}[%?SHADOW]smoke silhouettes[%]{ENDWAVE}{CLEARCOLOR}, [+oncoming automobile] [+man detective]");
        lyrics.add("{FADE}{RAINBOW}[%150]Phantom glow lingers[%]{ENDRAINBOW}, {HEARTBEAT=1.0}[RED]never forget{ENDHEARTBEAT}.{CLEARCOLOR} [+glowing star][+red heart]");

        lyrics.add("[%?SHINY][%150]Phantom Glow, Suno[%][%]");
        return lyrics;
    }
}
