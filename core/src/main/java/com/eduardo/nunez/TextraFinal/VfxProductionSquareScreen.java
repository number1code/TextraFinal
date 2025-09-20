package com.eduardo.nunez.TextraFinal; // Assuming this is your package

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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.github.tommyettinger.textra.*;

public class VfxProductionSquareScreen extends ScreenAdapter {
    // --- Core and UI Objects ---
    private final Game game;
    private Stage stage;
    private Skin skin;
    private TypingLabel typingLabel;

    // --- Lyrics Data ---
    private Array<String> lyrics;
    private int currentLyricIndex = 0;

    // --- GDX-VFX Objects ---
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
    private boolean isCrtEnabled = true, isBloomEnabled = true, isVignetteEnabled = true,
        isOldTvEnabled = true, isFilmGrainEnabled = true, isFisheyeEnabled = true,
        isChromaticAberrationEnabled = false, isGaussianBlurEnabled = false,
        isRadialBlurEnabled = false, isLensFlareEnabled = false, isLevelsEnabled = false,
        isZoomEnabled = false, isFxaaEnabled = false, isNfaaEnabled = false;

    public VfxProductionSquareScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        lyrics = createLyrics();
        skin = new Skin();
        setupFontsAndSkin();

        // --- PILLAR 1: Use a square viewport for the Stage ---
        // This makes the scene render to a square, which is essential for the fisheye effect.
        stage = new Stage(new FitViewport(1080, 1080));

        typingLabel = new TypingLabel(lyrics.get(currentLyricIndex), skin);
        typingLabel.setAlignment(Align.center);
        typingLabel.setWrap(true);

        Table table = new Table();
        table.setFillParent(true);
        table.add(typingLabel).width(1020f);
        stage.addActor(table);

        // --- GDX-VFX SETUP ---
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
        fxaaEffect = new FxaaEffect(0.5f, 0.5f, 0.5f, true);
        nfaaEffect = new NfaaEffect(true);

        bloomEffect.setBaseIntensity(1.5f);
        bloomEffect.setBloomIntensity(2.0f);
        bloomEffect.setThreshold(0.3f);

        // Add default effects
        vfxManager.addEffect(crtEffect);
        vfxManager.addEffect(bloomEffect);
        vfxManager.addEffect(vignettingEffect);
        vfxManager.addEffect(oldTvEffect);
        vfxManager.addEffect(filmGrainEffect);
        vfxManager.addEffect(fisheyeEffect);

        setupInput();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);

        // --- Draw scene into the square framebuffer ---
        vfxManager.beginInputCapture();
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        stage.draw();
        vfxManager.endInputCapture();

        vfxManager.applyEffects();

        // --- Clear the main screen (to create black bars) ---
        ScreenUtils.clear(0, 0, 0, 1f);

        // --- PILLAR 3: Render the final result to the specific area calculated by the viewport ---
        // This draws the final square image, with all effects, into the centered square on your vertical screen.
        vfxManager.renderToScreen(
            stage.getViewport().getScreenX(),
            stage.getViewport().getScreenY(),
            stage.getViewport().getScreenWidth(),
            stage.getViewport().getScreenHeight()
        );
    }

    @Override
    public void resize(int width, int height) {
        // Update the stage's viewport. It will calculate the centered square.
        stage.getViewport().update(width, height, true);

        // --- PILLAR 2: Resize the VfxManager's buffer to match the viewport's square dimensions ---
        // This is the critical link that ensures the fisheye effect is applied to a square image.
        vfxManager.resize(stage.getViewport().getScreenWidth(), stage.getViewport().getScreenHeight());
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        vfxManager.dispose();
        // Dispose all effects...
        crtEffect.dispose(); bloomEffect.dispose(); vignettingEffect.dispose(); oldTvEffect.dispose();
        chromaticAberrationEffect.dispose(); filmGrainEffect.dispose(); gaussianBlurEffect.dispose();
        radialBlurEffect.dispose(); lensFlareEffect.dispose(); fisheyeEffect.dispose();
        levelsEffect.dispose(); zoomEffect.dispose(); fxaaEffect.dispose(); nfaaEffect.dispose();
    }

    // --- Your setupInput, setupFontsAndSkin, and createLyrics methods remain the same ---
    // (I've included them here for completeness)

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Input.Keys.SPACE:
                        currentLyricIndex++;
                        if (currentLyricIndex >= lyrics.size) {
                            currentLyricIndex = 0;
                        }
                        typingLabel.restart(lyrics.get(currentLyricIndex));
                        break;
                    case Input.Keys.C:
                        if (isCrtEnabled) vfxManager.removeEffect(crtEffect); else vfxManager.addEffect(crtEffect);
                        isCrtEnabled = !isCrtEnabled;
                        break;
                    case Input.Keys.B:
                        if (isBloomEnabled) vfxManager.removeEffect(bloomEffect); else vfxManager.addEffect(bloomEffect);
                        isBloomEnabled = !isBloomEnabled;
                        break;
                    // ... include all other key toggles here using the boolean flags...
                    case Input.Keys.V:
                        if (isVignetteEnabled) vfxManager.removeEffect(vignettingEffect); else vfxManager.addEffect(vignettingEffect);
                        isVignetteEnabled = !isVignetteEnabled;
                        break;
                    case Input.Keys.O:
                        if (isOldTvEnabled) vfxManager.removeEffect(oldTvEffect); else vfxManager.addEffect(oldTvEffect);
                        isOldTvEnabled = !isOldTvEnabled;
                        break;
                    case Input.Keys.A:
                        if (isChromaticAberrationEnabled) vfxManager.removeEffect(chromaticAberrationEffect); else vfxManager.addEffect(chromaticAberrationEffect);
                        isChromaticAberrationEnabled = !isChromaticAberrationEnabled;
                        break;
                    case Input.Keys.F:
                        if (isFilmGrainEnabled) vfxManager.removeEffect(filmGrainEffect); else vfxManager.addEffect(filmGrainEffect);
                        isFilmGrainEnabled = !isFilmGrainEnabled;
                        break;
                    case Input.Keys.G:
                        if (isGaussianBlurEnabled) vfxManager.removeEffect(gaussianBlurEffect); else vfxManager.addEffect(gaussianBlurEffect);
                        isGaussianBlurEnabled = !isGaussianBlurEnabled;
                        break;
                    case Input.Keys.R:
                        if (isRadialBlurEnabled) vfxManager.removeEffect(radialBlurEffect); else vfxManager.addEffect(radialBlurEffect);
                        isRadialBlurEnabled = !isRadialBlurEnabled;
                        break;
                    case Input.Keys.L:
                        if (isLensFlareEnabled) vfxManager.removeEffect(lensFlareEffect); else vfxManager.addEffect(lensFlareEffect);
                        isLensFlareEnabled = !isLensFlareEnabled;
                        break;
                    case Input.Keys.E:
                        if (isFisheyeEnabled) vfxManager.removeEffect(fisheyeEffect); else vfxManager.addEffect(fisheyeEffect);
                        isFisheyeEnabled = !isFisheyeEnabled;
                        break;
                    case Input.Keys.S:
                        if (isLevelsEnabled) vfxManager.removeEffect(levelsEffect); else vfxManager.addEffect(levelsEffect);
                        isLevelsEnabled = !isLevelsEnabled;
                        break;
                    case Input.Keys.Z:
                        if (isZoomEnabled) vfxManager.removeEffect(zoomEffect); else vfxManager.addEffect(zoomEffect);
                        isZoomEnabled = !isZoomEnabled;
                        break;
                    case Input.Keys.X:
                        if (isFxaaEnabled) vfxManager.removeEffect(fxaaEffect); else vfxManager.addEffect(fxaaEffect);
                        isFxaaEnabled = !isFxaaEnabled;
                        break;
                    case Input.Keys.N:
                        if (isNfaaEnabled) vfxManager.removeEffect(nfaaEffect); else vfxManager.addEffect(nfaaEffect);
                        isNfaaEnabled = !isNfaaEnabled;
                        break;
                    default: return false;
                }
                return true;
            }
        });
    }

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

    private Array<String> createLyrics() {
        Array<String> lyrics = new Array<>();
        lyrics.add("[%?SHINY][%150]Phantom Glow, Suno[%][%]");
        lyrics.add("{FADE=1;1;0.5}{WIND=0.1;1;2;0.5}[GRAY]Shadows[CLEARCOLOR] creeping, past midnight tickin{SLOWER}g slow, [+new moon][+alarm clock]");
        lyrics.add("{FADE}{EMERGE}[CYAN]Hoodie up, I'm the {RAINBOW}phantom glow{ENDRAINBOW},[CLEARCOLOR] [+man superhero, medium-dark skin tone] [+glowing star]");
        // ... all your lyrics go here ...
        lyrics.add("{FADE}{RAINBOW}[%150]Phantom glow lingers[%]{ENDRAINBOW}, {HEARTBEAT=1.0}[RED]never forget{ENDHEARTBEAT}.{CLEARCOLOR} [+glowing star][+red heart]");
        lyrics.add("[%?SHINY][%150]Phantom Glow, Suno[%][%]");
        return lyrics;
    }
}
