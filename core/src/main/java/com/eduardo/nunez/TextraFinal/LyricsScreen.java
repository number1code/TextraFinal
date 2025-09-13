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
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Styles;
import com.github.tommyettinger.textra.TextraLabel;
import com.github.tommyettinger.textra.TypingLabel;

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
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("GreatVibes-Regular.ttf"));
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
        lyrics = new Array<>();
        lyrics.add("{SPIN}This is the first phrase...{ENDSPIN}");
        lyrics.add("[%250]The[%] {ATTENTION}quick{ENDATTENTION} [BROWN]brown{CLEARCOLOR} [%200]fox[%] {JUMP}jumped{ENDJUMP} [^][%200]over[%][^] {SQUASH=0.8;true}{ATTENTION}the [/][%300]lazy[%][/] [%150]dog.[%].{ENDATTENTION}{ENDSQUASH}");
        lyrics.add("{HEARTBEAT}Let's spruce  this up!{ENDHEARTBEAT}"); // Note: Bold uses [*] in textra
        lyrics.add("{FADE}[BROWN]Does any of this work{CLEARCOLOR}{ENDFADE}");
        lyrics.add("Press [BUMP;RED]SPACEBAR[/BUMP] to cycle through the lyrics.");
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
