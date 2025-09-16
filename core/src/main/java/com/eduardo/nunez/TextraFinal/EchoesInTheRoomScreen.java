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
import com.github.tommyettinger.textra.Font.FontFamily; // NEW IMPORT


public class EchoesInTheRoomScreen extends ScreenAdapter {

    private final Game game;
    private Stage stage;
    private Skin skin;
    private TextraLabel typingLabel;

    private Array<String> lyrics;
    private int currentLyricIndex = 0;

    public EchoesInTheRoomScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // --- 1. Scene2D Setup (Unchanged) ---
        stage = new Stage(new FitViewport(1280, 720));
        skin = new Skin();

        // --- 2. Correct Font and FontFamily Creation ---

        // Create a shared parameter object for all fonts.
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64; // A good base size
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2f;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;

        // Load the main cursive font
        FreeTypeFontGenerator cursiveGenerator = new FreeTypeFontGenerator(Gdx.files.internal("GreatVibes-Regular.ttf"));
        Font cursiveFont = new Font(cursiveGenerator.generateFont(parameter));
        cursiveGenerator.dispose();

        // Load the clean sans-serif font
        FreeTypeFontGenerator sansGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Lato-Regular.ttf"));
        Font sansFont = new Font(sansGenerator.generateFont(parameter));
        sansGenerator.dispose();

        // --- THIS IS THE CRUCIAL FIX ---
        // Register the Game Icons with the specific font object that will act as our default.
        KnownFonts.addGameIcons(sansFont);

        // Now, create the FontFamily, which will hold all our fonts.
        FontFamily fontFamily = new FontFamily();
        // Set the "sans" font (the one with the icons) as the default font for the family.
        //fontFamily.add(sansFont, "sans", true); // The `true` here marks it as the default.
        // Add the cursive font with a different name.
        //fontFamily.add(cursiveFont, "cursive");

        // Add the completed FontFamily to the skin. This is the only font the skin needs to know about.
        skin.add("default", fontFamily);

        // --- 3. Create the LabelStyle (Unchanged) ---
        Styles.LabelStyle labelStyle = new Styles.LabelStyle();
        labelStyle.font = skin.get("default", Font.class); // This will now correctly get the FontFamily
        skin.add("default", labelStyle);

        // --- 4. Lyrics Data Setup (Your perfected version) ---
        lyrics = new Array<>();
        // [Verse 1]
        lyrics.add("{FADE=0.5;1.0}A [%75]single[%] [@cursive][%150]note[] {HANG}[+musical-notes]{ENDHANG} [%75]hangs in the air[%]{ENDFADE}");
        lyrics.add("{FADE}Like a {WAVE=0.05;0.8;3.0}thought{ENDWAVE} I can't [%125]{SHAKE}repair{ENDSHAKE}...{ENDFADE} [+broken-heart]");
        lyrics.add("{FADE}It {WIND=0.1;1.0;2.0;0.5}echoes{ENDWIND} softly then it's {FADE=1;0;0.5}gone{ENDFADE}.{ENDFADE} [+sound-waves]");
        // ... (the rest of your lyrics array) ...
        //voltage lyrics
        lyrics.add("{FADE;FAST}Let's see if we can go over this...{ENDFADE}");
        lyrics.add("{FADE}Fend for myself, I don't need no {JUMP}[+shield]assist{ENDJUMP}.{ENDFADE}");
        lyrics.add("{FADE}I'ma go pull up with [%150]twins[%], that's a {HEARTBEAT}[+kiss]{ENDHEARTBEAT}.{ENDFADE}");
        lyrics.add("{FADE}And I ain't even talking 'bout [@pixel]Mary-Kate[@sans].{ENDFADE}");

        lyrics.add("{FADE;FASTER}Get the [+money-stack]bag, {SPIN=1;1}flip it{ENDSPIN}, go check my [%125]flip rate[%].{ENDFADE}");
        lyrics.add("{FADE}If I don't make it back, then I {SPIRAL=1;0.5}[PURPLE]reincarnate{ENDSPIRAL}.{ENDFADE} [+reincarnation]");
        lyrics.add("{FADE}And I'm back in this bitch, new [%125]hair[%], new [%125]'fit[%]! [+hair-tie][+t-shirt]{ENDFADE}");
        lyrics.add("{FADE}Back in this bitch, don't need a new whip. {WAVE=0.1;5;2}[+car]{ENDWAVE}{ENDFADE}");

        lyrics.add("{FADE}Pull up to the scene with a {SHAKE}[CYAN]ice cold{ENDSHAKE} sip... [+goblet]{ENDFADE}");
        lyrics.add("{FADE;SPEED=0.2}{BUMP}Gulp,{ENDBUMP} {BUMP}gulp,{ENDBUMP} {BUMP}gulp,{ENDBUMP} {BUMP}gulp,{ENDBUMP} {BUMP}gulp...{ENDFADE}");
        lyrics.add("{FADE}Everywhere I go, all these hoes tryna fuck.{ENDFADE}");
        lyrics.add("{FADE}I just want a [%150]nut[%] like the [+acorn]squirrel on the cup.{ENDFADE}");

        lyrics.add("{FADE;SLOW}Told the ho, \"You better gimme some [%200]space[%]...{ENDFADE}");
        lyrics.add("{FADE}...Or I'ma send you to {VAR=FIRE}outer space{VAR=ENDFIRE}\". [+rocket]{ENDFADE}");
        lyrics.add("{FADE}They gon' remember my name, they gon' call me [@pixel]what's his face[@sans].{ENDFADE}");
        lyrics.add("{FADE}And they gon' {WAVE=0.1;1;5}[RED]wish I was replaced{ENDWAVE}.{ENDFADE} [+cancel]");

        lyrics.add("{FADE;FASTER}Just got a new [+credit-card]card, 'boutta [%150]max it out[%].{ENDFADE}");
        lyrics.add("{FADE;FASTER}And I just got some {WIND=0.1;2;3;0.5}[GREEN]gas{ENDWIND}, 'boutta [%150]pass it out[%].{ENDFADE}");
        lyrics.add("{FADE;FASTER}And I just hit a [+robber]stain, 'boutta [%150]cash it out[%].{ENDFADE}");
        lyrics.add("{FADE}I'm a [%125]white man[%] in a [%125]black man house[%].{ENDFADE}");

        lyrics.add("{FADE}And you know I gotta make it out, can't go without.{ENDFADE}");
        lyrics.add("{FADE}Got a new [%150]piece[%], 'boutta go put on some {SHAKE}[WHITE]teeth{ENDSHAKE}. [+grin]{ENDFADE}");
        lyrics.add("{FADE}Got a new [%150]crib[%], 'boutta go invite some [+group]friends.{ENDFADE}");
        lyrics.add("{FADE}Gettin' bored of this {SPIN=1;1}[+earth-america]Earth{ENDSPIN}, 'boutta go create some ends.{ENDFADE}");

        lyrics.add("{FADE}They don't want me to {EMERGE}begin{ENDEMERGE}, they just want me to {SHRINK}end{ENDSHRINK}.{ENDFADE}");
        lyrics.add("{FADE}I ain't 'boutta pretend, 'boutta go call the {JOLT}[BLUE]feds{ENDJOLT}. [+fbi]{ENDFADE}");
        lyrics.add("{FADE}I don't like these hoes, 'boutta go call they friends.{ENDFADE}");
        lyrics.add("{FADE}And the plug call me, he 'boutta send a [+cardboard-box]care package.{ENDFADE}");

        lyrics.add("{FADE;SPEED=0.5}I'ma open that bitch and go {JUMP}grab it{ENDJUMP}.{ENDFADE}");
        lyrics.add("{FADE;SPEED=0.5}I just get to it, I gotta have it.{ENDFADE}");
        lyrics.add("{FADE}They gon' need a [%250]big casket[%]. [+coffin]{ENDFADE}");
        lyrics.add("{FADE}And the hoes wanna meet, wanna know my [%150]status[%].{ENDFADE}");

        lyrics.add("{FADE;FASTER}They wanna know what I eat, wanna know my habits.{ENDFADE}");
        lyrics.add("{FADE;FASTER}They wanna see what I do, wanna know my patterns.{ENDFADE}");
        lyrics.add("{FADE;FASTER}They wanna see what I wear, wanna see my fabrics.{ENDFADE}");
        lyrics.add("{FADE}And I'm {EMERGE}fresh out the box{ENDEMERGE} like I was a [+rabbit]rabbit.{ENDFADE}");

        lyrics.add("{FADE}And the plug came through, he was wrapped in {SHAKE}[WHITE]plastic{ENDSHAKE}.{ENDFADE}");
        lyrics.add("{FADE}I ain't even know what was in the [%150]package[%].{ENDFADE}");
        lyrics.add("{FADE}We was like a {RAINBOW}band{ENDRAINBOW} the way we came in with...{ENDFADE}");

        // --- 5. Create and Position the CORRECT Label ---
        // We must use TypingLabel to get the animated effects.
        typingLabel = new TypingLabel(lyrics.get(currentLyricIndex), skin);
        typingLabel.setAlignment(Align.center);
        typingLabel.setWrap(true);

        Table table = new Table();
        table.setFillParent(true);
        table.add(typingLabel).width(1200f);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            currentLyricIndex++;
            if (currentLyricIndex >= lyrics.size) {
                currentLyricIndex = 0;
            }
            typingLabel.setText(lyrics.get(currentLyricIndex));
        }

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f); // A slightly different background color
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
