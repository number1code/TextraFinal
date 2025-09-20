package com.eduardo.nunez.TextraFinal;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.CrtEffect;
import com.github.tommyettinger.textra.*;

// By extending ScreenAdapter, we only have to implement the methods we actually need.
public class ProductionScreen extends ScreenAdapter {

    // --- Core LibGDX and UI Objects ---
    private final Game game; // A reference to the main game class, good practice for screen management.
    private Stage stage;
    private Skin skin;
    private TypingLabel typingLabel; // The label from the tommyettinger.textra library.

    // --- Lyrics Data and State ---
    private Array<String> lyrics;
    private int currentLyricIndex = 0;

    // --- gdx-vfx Post-Processing Objects ---
    private VfxManager vfxManager;
    private CrtEffect crtEffect;
    private BloomEffect bloomEffect; // Used for the "phosphor glow"

    // --- Control Flags ---
    private boolean isCrtEnabled = true;
    private boolean isBloomEnabled = true;

    // The constructor takes a Game instance, allowing this screen to switch to other screens.
    public ProductionScreen(Game game) {
        this.game = game;
    }

    /**
     * Called once when this screen becomes the active screen.
     * This is the ideal place for all setup and initialization.
     */
    @Override
    public void show() {
        // --- 1. Scene2D Setup ---
        // A Stage is an invisible container for our UI elements ("Actors").
        // A FitViewport maintains our virtual resolution (e.g., 1280x720) on any screen size.
        stage = new Stage(new FitViewport(1080, 1920));

        // --- 2. Programmatic Font and Skin Creation (The FreeTypist Core) ---
        // A Skin is like a stylesheet for our UI, holding fonts, colors, etc.
        skin = new Skin();

        // Load our .ttf font file from the assets folder.
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf")); // <-- MAKE SURE YOUR FONT FILE IS HERE!
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PressStart2P-Regular.ttf"));
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("GreatVibes-Regular.ttf"));
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Lato-Regular.ttf"));
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PlaywriteES-VariableFont_wght.ttf"));
        //FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("ShareTech-Regular.ttf"));
        //FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("AdventPro-Regular.ttf"));
        //FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("FontdinerSwanky-Regular.ttf"));
        //FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("PermanentMarker-Regular.ttf"));
        //FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("IBMPlexMono-Regular.ttf"));
        //PRIMARY FONT SETUP
        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("BitcountInk-VariableFont_CRSV,ELSH,ELXP,SZP1,SZP2,XPN1,XPN2,YPN1,YPN2,slnt,wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter1 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter1.size = 96;//48
        parameter1.color = Color.WHITE;
        parameter1.borderColor = Color.BLACK;
        parameter1.borderWidth = 2f;
        parameter1.magFilter = Texture.TextureFilter.Linear;
        parameter1.minFilter = Texture.TextureFilter.Linear;
        //SECONDARY FONT SETUP
        FreeTypeFontGenerator generator2 = new FreeTypeFontGenerator(Gdx.files.internal("PressStart2P-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter2.size = 56;//48
        parameter2.color = Color.WHITE;
        parameter2.borderColor = Color.BLACK;
        parameter2.borderWidth = 2f;
        parameter2.magFilter = Texture.TextureFilter.Linear;
        parameter2.minFilter = Texture.TextureFilter.Linear;

        BitmapFont bitmapFont1 = generator1.generateFont(parameter1);
        BitmapFont bitmapFont2 = generator2.generateFont(parameter2);
        generator1.dispose();
        generator2.dispose();

        // Create Textra Font objects from BitmapFonts
        Font primaryTextraFont = new Font(bitmapFont1);
        Font pixelTextraFont = KnownFonts.getIBM8x16();;
//        Font pixelTextraFont = new Font(bitmapFont2);

        // --- IMPORTANT: Add the textra.Font objects to the skin with distinct names ---
        // These names ("primary" and "pixel") are what you'll use in your markup tags.
        // DO NOT name your primary Font object "default" if you also plan to name a LabelStyle "default".
        skin.add("primary", primaryTextraFont); // Add your main font
        skin.add("pixel", pixelTextraFont);     // Add your secondary font

        // --- Create a default LabelStyle that references your primary font ---
        // This is the style that TypingLabel will use by default if no other style or tag is specified.
        Styles.LabelStyle defaultLabelStyle = new Styles.LabelStyle();
        defaultLabelStyle.font = skin.get("primary", Font.class); // Reference your primary font here
        // Add the DEFAULT LabelStyle. TypingLabel will look for a LabelStyle named "default".
        skin.add("default", defaultLabelStyle);

        // You do NOT need to add "pixel" as a LabelStyle for the [@pixel] tag to work for fonts.
        // The [@tag] syntax directly references Font objects in the skin.
        // If you *wanted* a completely separate LabelStyle (e.g., for an entirely different label),
        // you would do:
        // Styles.LabelStyle pixelLabelStyle = new Styles.LabelStyle();
        // pixelLabelStyle.font = skin.get("pixel", Font.class);
        // skin.add("pixelLabel", pixelLabelStyle); // Use a different name to avoid collision
        // --- 3. Lyrics Data Setup ---
        // We define our array of marked-up phrases.

        KnownFonts.addEmoji(primaryTextraFont);
        KnownFonts.addGameIcons(primaryTextraFont);
        KnownFonts.addNotoEmoji(primaryTextraFont);
        KnownFonts.addMaterialDesignIcons(primaryTextraFont);
        KnownFonts.addOpenMoji(primaryTextraFont,true);

        // --- 3. THE MARKED-UP LYRICS ---
        // This is the artistic core. Each line uses a combination of effects to match the mood.
        // --- THE MARKED-UP "CATWALK" LYRICS ---
        lyrics = new Array<>();

        lyrics.add("[%?SHINY][%150]Phantom Glow, Suno[%][%]");

// [Verse 1]
        lyrics.add("{FADE=1;1;0.5}{WIND=0.1;1;2;0.5}[GRAY]Shadows[CLEARCOLOR] creeping, past midnight tickin{SLOWER}g slow, [+new moon][+alarm clock]");
        lyrics.add("{FADE}{EMERGE}[CYAN]Hoodie up, I'm the {RAINBOW}phantom glow{ENDRAINBOW},[CLEARCOLOR] [+man superhero, medium-dark skin tone] [+glowing star]");
        lyrics.add("{FADE}{SICK=0.8}[BLACK]Smoke thick[/], {HEARTBEAT=1.0}lungs tight[/], {WAVE=0.05;1;1}streets whisper low{ENDWAVE}, [+lungs] [+cigarette]");
        lyrics.add("{FADE}[%125]Concrete jungle[%] where the {SPIRAL=0.8;0.3}lost souls grow{ENDSPIRAL}. [+cityscape][+person walking]");

        lyrics.add("{FADE}{FAST}[RED]Eyes red[/], mind spinning like a {SPIN}carousel{ENDSPIN}, [+eyes] [+ferris wheel]");
        lyrics.add("{FADE}{JOLT=1.0;5.0;inf;0.5;000000ff;777777ff}Memories cut deep, sharper than a{ENDJOLT} {SHAKE}scalpel,{ENDSHAKE} [+brain] [+kitchen knife]");
        lyrics.add("{FADE}[%75]Dreams dissolve[%] in the {HANG=0.4;1}devil's cracked chapel{ENDHANG}, [+zzz][+ogre]");
        lyrics.add("{FADE}{SLOW}{HEARTBEAT=0.15;1.0}Each breath stolen, life fragile as an apple [+lungs]{ENDHEARTBEAT} [+red apple]");

// [Chorus]
        lyrics.add("{FADE}{FAST}{GRADIENT=BLACK;PURPLE;0.5}[%175]{CROWD}Phantom glow{ENDCROWD}{ENDGRADIENT}[%], where the {WIND=0.1;2;3;0.5}dark winds blow{ENDWIND}, [+wind face] {CROWD}[+ghost]{ENDCROWD}");
        lyrics.add("{FADE}[%125]{FAST}Every step heavy[%], every move {SLOWER}too slow, {HANG}[+foot]{ENDHANG}[+snail]");
        lyrics.add("{FADE}{FAST}{GRADIENT=BLACK;PURPLE;0.5}[%175]{CROWD}Phantom glow{ENDCROWD}{ENDGRADIENT}[%], I'm the {EMERGE}ghost they know{ENDEMERGE}, {CROWD}[+ghost] [+waving hand]{ENDCROWD}");
        lyrics.add("{FADE}Through the {WAVE=0.05;1.5;1}haze[/], I'm the {SICK=0.8}[RED]curse they sow{ENDSICK}.{CLEARCOLOR}");

// [Verse 2]
        lyrics.add("{FADE}{SHAKE=0.3;1.0}Echoes of sirens{ENDSHAKE}, blend with the {SQUASH}bassline{ENDSQUASH}, [+police car light] [+speaker high volume] ");
        lyrics.add("{FADE}{HEARTBEAT=0.4;1.0}Hearbeat syncopated, life on a{ENDHEARTBEAT} {BLINK=FF0000ff;000000;0.8;0.7}[RED]flatline,[/]{ENDBLINK}  [+beating heart] [+chart decreasing]");
        lyrics.add("{FADE}[%125][BLACK]Corners cold[/], {HANG=0.8;0.5}currency in chalk outlines{ENDHANG}{CLEARCOLOR}, [+classical building][+money bag]");
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
        // --- 4. Create and Position the TextraLabel ---
        // We create the TextraLabel, giving it the first lyric and the skin.
        typingLabel = new TypingLabel(lyrics.get(currentLyricIndex), skin);
        typingLabel.setAlignment(Align.center);
        typingLabel.setWrap(true);

        // A Table is the best way to organize actors on a stage.
        Table table = new Table();
        table.setFillParent(true); // Make the table fill the screen
        table.add(typingLabel).width(1020f); // Add the label and set a max width for wrapping
        stage.addActor(table);

        // --- 3. GDX-VFX SETUP ---
        // Create a VfxManager with a standard pixel format. This manager will handle our effects chain.
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);

        // Create the desired effects. You can tweak their settings here or at runtime.
        crtEffect = new CrtEffect();
        //crtEffect.setLineDistortion(0.2f); // Example of tweaking a parameter
        bloomEffect = new BloomEffect(); // This will create our "phosphor glow"
        bloomEffect.setBaseIntensity(1.5f);
        bloomEffect.setBloomIntensity(2.0f);
        bloomEffect.setThreshold(0.3f);


        // Add the effects to the manager in the order you want them to be applied.
        vfxManager.addEffect(crtEffect);
        vfxManager.addEffect(bloomEffect);
        // --- 5. Set the Input Processor ---
        // This CRUCIAL line tells LibGDX to send all input events (like key presses) to our stage.
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * The main game loop for this screen, called up to 60 times per second.
     */
    @Override
    public void render(float delta) {
        // --- 1. Handle Input ---
        // `isKeyJustPressed` returns true only on the single frame the key is pressed down.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Move to the next lyric
            currentLyricIndex++;
            // If we go past the end of the array, loop back to the beginning.
            if (currentLyricIndex >= lyrics.size) {
                currentLyricIndex = 0;
            }
            // In tommyettinger's textra, setText() is overridden to restart the typing animation with new text.
            typingLabel.setText(lyrics.get(currentLyricIndex));
        }

        // --- 2. Clear the Screen ---
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // --- 3. Update and Draw the UI ---
        // `stage.act()` updates all the actors (e.g., advances the typing animation).
        // `stage.draw()` draws all the actors.
        stage.act(delta);
        stage.draw();
    }

    /**
     * Called when the application window is resized.
     */
    @Override
    public void resize(int width, int height) {
        // We must update our viewport when the screen is resized to prevent distortion.
        stage.getViewport().update(width, height, true);
    }

    /**
     * Called when the application is closing to free up memory.
     */
    @Override
    public void dispose() {
        // It's very important to dispose of Scene2D resources to prevent memory leaks.
        stage.dispose();
        skin.dispose(); // The skin will dispose of the font it contains.
    }
}

