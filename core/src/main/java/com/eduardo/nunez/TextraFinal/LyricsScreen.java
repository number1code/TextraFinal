package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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
import com.github.tommyettinger.textra.*;

// By extending ScreenAdapter, we only have to implement the methods we actually need.
public class LyricsScreen extends ScreenAdapter {

    // --- Core LibGDX and UI Objects ---
    private final Game game; // A reference to the main game class, good practice for screen management.
    private Stage stage;
    private Skin skin;
    private TypingLabel typingLabel; // The label from the tommyettinger.textra library.

    // --- Lyrics Data and State ---
    private Array<String> lyrics;
    private int currentLyricIndex = 0;

    // The constructor takes a Game instance, allowing this screen to switch to other screens.
    public LyricsScreen(Game game) {
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
        stage = new Stage(new FitViewport(1280, 720));

        // --- 2. Programmatic Font and Skin Creation (The FreeTypist Core) ---
        // A Skin is like a stylesheet for our UI, holding fonts, colors, etc.
        skin = new Skin();

        // Load our .ttf font file from the assets folder.
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf")); // <-- MAKE SURE YOUR FONT FILE IS HERE!
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PressStart2P-Regular.ttf"));
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("GreatVibes-Regular.ttf"));
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Lato-Regular.ttf"));
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("PlaywriteES-VariableFont_wght.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 56;//48
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2f;

        // --- THIS IS THE FIX ---
        // Apply Linear filtering for smooth scaling.
        // magFilter is for when the font is scaled UP (magnified).
        // minFilter is for when the font is scaled DOWN (minified).
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        // --- END OF FIX ---

        // Generate the standard BitmapFont.
        BitmapFont bitmapFont = generator.generateFont(parameter);

        generator.dispose(); // IMPORTANT: Dispose of the generator to free up memory.

        // Now, create a `textra` Font object, which is a more powerful wrapper.
        Font textraFont = new Font(bitmapFont);

        // Add our new Font to the skin. TextraLabel will look for a Font named "default".
        skin.add("default", textraFont);

        // --- 2. THE FIX: Create and Add the LabelStyle ---
        // Create a new LabelStyle object from the correct textra class.
        Styles.LabelStyle labelStyle = new Styles.LabelStyle();
        // Tell the style to use the font we just added to the skin.
        labelStyle.font = skin.get("default", Font.class);
        // Now, add the completed STYLE to the skin with the name "default".
        skin.add("default", labelStyle);
        // --- 3. Lyrics Data Setup ---
        // We define our array of marked-up phrases.

        KnownFonts.addEmoji(textraFont);
        KnownFonts.addGameIcons(textraFont);
        KnownFonts.addNotoEmoji(textraFont);
        KnownFonts.addMaterialDesignIcons(textraFont);
        KnownFonts.addOpenMoji(textraFont,true);
        lyrics = new Array<>();

        // --- 3. THE MARKED-UP LYRICS ---
        // This is the artistic core. Each line uses a combination of effects to match the mood.
        lyrics = new Array<>();
        lyrics.add("[%?SHINY][%150]Echoes In The Room, Suno[%][%]");
        // [Verse 1]
        lyrics.add("{FADE}A [%75]single[%] [%150]note[%] {HANG=1.0;6}[+musical notes]{ENDHANG} [%75]hangs in the air[%]{ENDFADE}");
        lyrics.add("{FADE}Like a {WAVE=0.05;0.8;1.5}thought{ENDWAVE} I can't [%125]{SHAKE=1.0;0.5}repair{ENDSHAKE}[%]...{ENDFADE} [+broken-heart]");
        lyrics.add("{FADE}It {WIND=0.1;1.0;2.0;0.5}echoes{ENDWIND} softly then it's {FADE=1;0;1.2}[%125]gone[%]{ENDFADE}{ENDFADE}");//[+dotted line face]
        // [Pre-Chorus]
        lyrics.add("{FADE}The [%150]walls[%] are {SICK=0.5}speaking...{ENDSICK}{ENDFADE}");
        lyrics.add("{FADE}But [%75]I can't hear[%] [+hear-no-evil monkey]{SPEED=0.5}...{SPEED} {ENDFADE}");
        lyrics.add("{FADE}Their voices [%50]fading...[%]{ENDFADE} [+ghost-ally]");
        lyrics.add("{FADE}[%200]Crystal clear[%]   [+crystal-ball]{ENDFADE}");
        // [Chorus] - More dramatic effects here
        lyrics.add("{FADE}[%150]Echoes[%] in the room {WAVE=0.05;0.1;0.7}[+musical notes]{ENDWAVE}{ENDFADE}");
        lyrics.add("{FADE}They {JUMP}sing{ENDJUMP}{ENDFADE}");
        lyrics.add("{FADE}{EMERGE}They bloom{ENDEMERGE}{SPEED=0.5}{EMERGE}   [+lotus]{ENDEMERGE}{ENDFADE}");
        lyrics.add("{FADE}A {SPIN=1;1}loop{ENDSPIN}...{ENDFADE}");
        lyrics.add("{FADE}A {WAVE=0.05;5.0;5.0}hum{ENDWAVE}...{ENDFADE}");
        lyrics.add("{FADE}I come {SHAKE}[RED]undone{CLEARCOLOR}{ENDSHAKE}   [+falling]{ENDFADE}");

        // [Verse 2]
        lyrics.add("{FADE}Your [%150]shadow[%] {WIND=0.05;0.5;3;0.2}lingers{ENDWIND} by the door{ENDFADE}  [+hooded-figure]");
        lyrics.add("{FADE}A ghost of us... a [%175]quiet roar[%]... [+lion]{ENDFADE}");
        lyrics.add("{FADE}The {BLINK=838181ff;000000;1.5;0.5}light flickers{ENDBLINK}, I count the {HEARTBEAT=1.5}beats{ENDHEARTBEAT}{ENDFADE} {BLINK=838181ff;ffffff;1.5;0.5}[+light-bulb]{ENDBLINK}[+heart-beats]");
        // [Pre-Chorus 2]
        lyrics.add("{FADE}The [%150]space[%] [+left-right arrow] between us...{ENDFADE}");
        lyrics.add("{FADE}{SPEED=0.2}Grows so{SPEED}{ENDFADE}");
        lyrics.add("{FADE} [%200]{SHRINK}{SPEED=0.2}w i  d  e{SPEED}{ENDSHRINK}[%]{ENDSHRINK}");
        lyrics.add("{SPEED=0.5}{FADE}I [%125]reach...[%] [+palm down hand, medium skin tone]{ENDFADE}{SPEED}");
        lyrics.add("{SPEED=0.5}{FADE}I {SICK=0.8}falter...{ENDSICK} [+woman facepalming, light skin tone]{ENDFADE}{SPEED}");
        lyrics.add("{FADE}But {SLAM}you{ENDSLAM} {JOLT=0.5;10;0.5}collide{ENDJOLT}.{ENDFADE} [+collision]");

        // [Chorus] - Repeat with slight variations if desired
        lyrics.add("{FADE}[%150]Echoes[%] in the room {WAVE=0.05;0.1;0.7}[+musical notes]{ENDWAVE}{ENDFADE}");
        lyrics.add("{FADE}They {JUMP}sing{ENDJUMP}{ENDFADE}");
        lyrics.add("{FADE}{EMERGE}They bloom{ENDEMERGE}{SPEED=0.5}{EMERGE}   [+lotus]{ENDEMERGE}{ENDFADE}");
        lyrics.add("{FADE}A {SPIN=1;1}loop{ENDSPIN}...{ENDFADE}");
        lyrics.add("{FADE}A {WAVE=0.02;5.0;5.0}hum{ENDWAVE}...{ENDFADE}");
        lyrics.add("{FADE}I come {SHAKE}[RED]undone{CLEARCOLOR}{ENDSHAKE}   [+falling]{ENDFADE}");
        lyrics.add("{FADE}[%150]Echoes[%] in the room {WAVE=0.05;0.1;0.7}[+musical notes]{ENDWAVE}{ENDFADE}");
        lyrics.add("{FADE}They {JUMP}sing{ENDJUMP}{ENDFADE}");
        lyrics.add("{FADE}{EMERGE}They bloom{ENDEMERGE}{SPEED=0.5}{EMERGE}   [+lotus]{ENDEMERGE}{ENDFADE}");
        lyrics.add("{FADE}A {SPIN=1;1}loop{ENDSPIN}...{ENDFADE}");
        lyrics.add("{FADE}A {WAVE=0.02;5.0;5.0}hum{ENDWAVE}...{ENDFADE}");
        lyrics.add("{FADE}I come {SHAKE}[RED]undone{CLEARCOLOR}{ENDSHAKE}   [+falling]{ENDFADE}");
//        lyrics.add("{FADE}[%150]Echoes[%] in the room [+sound-waves]{ENDFADE}");
//        lyrics.add("{FADE}They {JUMP}sing{ENDJUMP}, They {EMERGE}bloom{ENDEMERGE}... [+sing][+sprout]{ENDFADE}");
//        lyrics.add("{FADE}A {SPIN=1;1}loop{ENDSPIN}... A {WAVE=0.02;5.0;5.0}hum{ENDWAVE}... [+loop]{ENDFADE}");
//        lyrics.add("{FADE}I come {SHAKE;RED}undone{ENDSHAKE}.{ENDFADE} [+falling]");
        lyrics.add("[%?NOTE][%?SHINY][%150]Echoes In The Room, Suno[%][%][%]");
        //tests
        lyrics.add("{FADE=0.5;1.0}The {WAVE=0.05;0.8;2.0}[SKY]ocean{CLEARCOLOR}{ENDWAVE} breathes, a {ENDFADE}{SLOW}slow and steady beat...");
        lyrics.add("{FADE}A sudden {JOLT=0.2;20.0;0.5}[YELLOW]flash[/]!{ENDFADE} a crackle in the air!");
        lyrics.add("{FADE}The old machine {SICK=0.5}shudders[/] to a halt, a final, dying bleat.{ENDFADE}");
        lyrics.add("{FADE}And in the silence, a {HEARTBEAT}single pulse{ENDHEARTBEAT}... a fear beyond compare.{ENDFADE}");
        lyrics.add("{FADE}{VAR=FIRE}Burning with a new intensity!{VAR=ENDFIRE}{ENDFADE}");
        lyrics.add("{FADE}A {SPIRAL=1.5;0.5}whisper[/] lost in the code, a ghost within the wire.{ENDFADE}");
        lyrics.add("{FADE}From {GRADIENT=BLUE;MAGENTA;1.0}binary{ENDGRADIENT} to a full spectrum {RAINBOW}reality.{ENDRAINBOW}{ENDFADE}");
        lyrics.add("{FADE}The {WAVE=1.0;0.4;0.3}[BLUE]ocean{CLEARCOLOR}{ENDWAVE} breathes, a {ENDFADE}{SLOW}slow and steady beat...");
        lyrics.add("{VAR=FIRE}Burning with a new intensity!{VAR=ENDFIRE}");
        lyrics.add("[+evergreen tree] Welcome to the [GREEN]forest[]!");
        lyrics.add("Feeling strong! [+flexed biceps, medium-light skin tone]");
        lyrics.add("Let's make some music! [+saxophone][+drum][+guitar]");
        lyrics.add("{CAROUSEL=0.3}Banner effect built in...{ENDCAROUSEL}");
        lyrics.add("{SPIN}This is the first phrase...{ENDSPIN}");
        lyrics.add("[%250]The[%] {ATTENTION}quick{ENDATTENTION} [BROWN]brown{CLEARCOLOR} [%200]fox[%] {JUMP}jumped{ENDJUMP} [^][%200]over[%][^] {SQUASH=0.8;true}{ATTENTION}the [/][%300]lazy[%][/] [%150]dog.[%].{ENDATTENTION}{ENDSQUASH}");
        lyrics.add("{HEARTBEAT}Let's [+evergreen tree] spruce this up!{ENDHEARTBEAT}"); // Note: Bold uses [*] in textra
        lyrics.add("{FADE}{CAROUSEL=0.3}GO [YELLOW]NAVI{CLEARCOLOR}!{ENDCAROUSEL}{ENDFADE}");
        lyrics.add("Press [BUMP;RED]SPACEBAR[/BUMP;] to cycle through the lyrics.");
        lyrics.add("{EASE=-8.0;1.35;true}We know this works{ENDEASE}  {SLIDE}Let's try some of this!{ENDSLIDE}");
        lyrics.add("Tap [BLUE]{LINK=https://www.instagram.com/pikapix2025/}here{ENDLINK}{CLEARCOLOR} for the tickets!");

        // --- 4. Create and Position the TextraLabel ---
        // We create the TextraLabel, giving it the first lyric and the skin.
        typingLabel = new TypingLabel(lyrics.get(currentLyricIndex), skin);
        typingLabel.setAlignment(Align.center);
        typingLabel.setWrap(true);

        // A Table is the best way to organize actors on a stage.
        Table table = new Table();
        table.setFillParent(true); // Make the table fill the screen
        table.add(typingLabel).width(1000f); // Add the label and set a max width for wrapping
        stage.addActor(table);

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
