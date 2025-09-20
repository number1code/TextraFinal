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
        //PRIMARY FONT SETUP
        FreeTypeFontGenerator generator1 = new FreeTypeFontGenerator(Gdx.files.internal("AdventPro-Regular.ttf"));
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
        lyrics.add("[%?SHINY][%150]Catwalk, Suno[%][%]");

// [Verse]
        lyrics.add("{FADE}{WAVE=0.05;2;2}Catwalk{ENDWAVE} like [%75](What?)[%] Model down [%125]Fifth Ave[%] [+statue of liberty] [%75](What?)[%]{ENDFADE}");
        lyrics.add("{FADE}Back it up like a computer  {JOLT=1.0;1.0;inf;0.6;ffffffff;ffff88ff}crash{ENDJOLT} [+desktop computer]{ENDFADE}");
        lyrics.add("{FADE}{EMERGE=0.1;false}[%?SHINY]Double-cheeked up[%]{ENDEMERGE} on a [%125]Thursday afternoon[%] [+spiral calendar]{ENDFADE}");
        lyrics.add("{FADE}He had it on him, I had it on me, had it on [%175]Zoom[%] [+video camera] [GRAY](On Zoom)[]{ENDFADE}");

        lyrics.add("{FADE}You gotta {SHAKE}watch him{ENDSHAKE}, baby, stay in {WAVE}tune{ENDWAVE} [+eyes][+musical notes]{ENDFADE}");
        lyrics.add("{FADE}You need a refill? I could get a {JUMP}spoon{ENDJUMP} [+cup with straw][+spoon]{ENDFADE}");
        lyrics.add("{FADE}Got a glass pipe and a [+light bulb]light, no {SHAKE}[GRAY]raccoon{ENDSHAKE} [+raccoon]{ENDFADE}");
        lyrics.add("{FADE}Baby, open up, let me get in the {EMERGE}cocoon{ENDEMERGE}  [+butterfly]{ENDFADE}");

        lyrics.add("{FADE}Goons get her room (Uh), smell the {WIND=0.1;1;3;0.3}perfume{ENDWIND} [+lotion bottle]{ENDFADE}");
        lyrics.add("{FADE}It's a couple [%150][GREEN]M's{CLEARCOLOR}[%] in the air like a {HANG=1;4}[+balloon]{ENDHANG}{ENDFADE}");
        lyrics.add("{FADE}{SLIDE=2;1;true}Moonwalkin'{ENDSLIDE}, bring it back, what you doin'?{ENDFADE}");
        lyrics.add("{FADE}It was high school, so I asked her for the [+newspaper] news (Uh){ENDFADE}");

        lyrics.add("{FADE}{JOLT=1.0;1.0;inf;0.6;ffffffff;ffff88ff}Newsflash{ENDJOLT}, it's an {SICK=0.8}epidemic{ENDSICK}, baby, too bad [+face with medical mask][GRAY](Too bad)[]{ENDFADE}");
        lyrics.add("{FADE}Waitin' on a mask, but I got a [BLUE]blue rag[]{ENDFADE}");
        lyrics.add("{FADE}Sittin' on a [%200][GREEN]million dollars[%][] in the [+handbag] duffel bag.{ENDFADE}");
        lyrics.add("{FADE}We ain't even gotta say it, know they {SHAKE}[RED]mad{ENDSHAKE}{CLEARCOLOR}. [+unamused face] {ENDFADE}");

 //[Bridge] - Slower pace, bigger flex
        lyrics.add("{FADE}{SLOW}I just gave a [%150][GREEN]$100,000[%][ENDCOLOR] to my dad [+old man]{ENDFADE}");
        lyrics.add("{FADE}{SLOW}He just pulled up on me with a hunnid {WAVE=0.1;2;1}[+oncoming automobile]slabs{ENDWAVE}.{ENDFADE}");
        lyrics.add("{FADE}{SLOW}I just hit the {WAVE=0.1;1;1} [+airplane] airport{ENDWAVE}, I ain't pack a[+luggage]bag.{ENDFADE}");
        lyrics.add("{FADE}{SLOW}I be gettin' [%175]paid[%], baby, that's a [+chart increasing] fact.{ENDFADE}");

// [Chorus] - Rhythmic and repetitive
        lyrics.add("{FADE}{FASTER}[%150]What you doin'?[%]{ENDFADE}");
        lyrics.add("{FADE}{FASTER}How you gettin' [GREEN]paid{CLEARCOLOR}, [%150]what you doin'?[%] [+money with wings]{ENDFADE}");
        lyrics.add("{FADE}{FASTER}Where you stay, [%150]what you doin'?[%]  [+house]{ENDFADE}");
        lyrics.add("{FADE}{EMERGE=1.0;false}{FASTER}How you gettin' [GREEN]paid{CLEARCOLOR}, [%150]what you doin'?[%] [+money with wings]{ENDEMERGE}{ENDFADE}");
        lyrics.add("{FADE}{SICK}{FASTER}How you gettin' [GREEN]paid{CLEARCOLOR}, [%150]what you doin'?[%] [+money with wings]{ENDSICK}{ENDFADE}");
        lyrics.add("{FADE}{SICK}[%?JOSTLE]{FASTER}How you gettin' [GREEN]paid{CLEARCOLOR}, [%150]what you doin'?[%] [+money with wings][%]{ENDSICK}{ENDFADE}");
        lyrics.add("{FADE}{FASTER}Where you stay, [%150]what you doin'?[%]  [+house]{ENDFADE}");
        lyrics.add("{FADE}{SICK}{EASE}How you gettin' [GREEN]paid{CLEARCOLOR}, [%150]what you doin'?[%] [+money with wings]{ENDEASE}{ENDSICK}{ENDFADE}");
        lyrics.add("{FADE}{JUMP}{FASTER}[%150]What you doin'?[%]{ENDJUMP}{ENDFADE}");

// Final title card
        lyrics.add("[%?SHINY][%150]Catwalk, Suno[%][%]");


        lyrics.add("[%?SHINY][%150]High Voltage, Suno[%][%]");

        lyrics.add("{FADE}Let's see if we can go over this...{ENDFADE}");
        lyrics.add("{FADE}Fend for myself, I don't need no {JUMP}[+shield]assist{ENDJUMP}.{ENDFADE}");
        lyrics.add("{FADE}I'ma go pull up with [%150]twins[%], that's a {HEARTBEAT}[+kiss]{ENDHEARTBEAT}.{ENDFADE}");
        lyrics.add("{FADE}And I ain't even talking 'bout [@pixel]Mary-Kate[@sans].{ENDFADE}");

        lyrics.add("{FADE}{FASTER}Get the  [+money-stack]bag, {SPIN=1;1}flip it{ENDSPIN}, go check my [%125]flip rate[%].{ENDFADE}");
        lyrics.add("{FADE}If I don't make it back, then I {SPIRAL=1;0.5}[PURPLE]reincarnate[ENDCOLOR]{ENDSPIRAL}. [+coffin]{ENDFADE}");
        lyrics.add("{FADE}And I'm back in this bitch, new [%125]hair[%], new [%125]'fit[%]! [+man getting haircut, medium-dark skin tone] [+necktie] [+man's shoe] {ENDFADE}");//[+t-shirt]
        lyrics.add("{FADE}Back in this bitch, don't need a new whip. {WAVE=0.1;2;1} [+automobile]{ENDWAVE}{ENDFADE}");

        lyrics.add("{FADE}Pull up to the scene with a {SHAKE}[CYAN]ice cold[ENDCOLOR]{ENDSHAKE} sip... [+cup with straw]{ENDFADE}");
        lyrics.add("{FADE}{SLOWER}gulp, gulp, gulp, gulp, gulp...{ENDFADE} ");
        lyrics.add("{FADE}Everywhere I go, all these hoes tryna fuck.{ENDFADE}");
        lyrics.add("{FADE}I just want a [%150]nut[%] like the [+acorn]squirrel on the cup.{ENDFADE}");

        lyrics.add("{FADE}{SLOW}Told the ho, [*]You better gimme some [%200]space[%]...[*]{ENDFADE}");
        lyrics.add("{FADE}[*]...Or I'ma send you to {VAR=FIRE}outer space{VAR=ENDFIRE}[*]. [+rocket]{ENDFADE}");
        lyrics.add("{FADE}They gon' remember my name, they gon' call me [@pixel]what's his face[@sans].{ENDFADE}");
        lyrics.add("{FADE}And they gon' {WAVE=0.1;1;5}[RED]wish I was replaced{ENDWAVE}.{ENDFADE} [+cancel]");

        lyrics.add("{FADE}{FASTER}Just got a new [+credit card] card, 'boutta [%150]max it out[%].{ENDFADE}");
        lyrics.add("{FADE}{FASTER}And I just got some {WIND=0.1;2;3;0.5}[GREEN]gas{ENDWIND}, 'boutta [%150]pass it out[%].{ENDFADE}");
        lyrics.add("{FADE}{FASTER}And I just hit a{HANG=0.2;0.2} [+money bag] {ENDHANG}stain, 'boutta [%150]cash it out[%].{ENDFADE}");
        lyrics.add("{FADE}I'm a [%125]white man[%] in a [%125]black man house[%].{ENDFADE}");

        lyrics.add("{FADE}And you know I gotta make it out, can't go without..{ENDFADE}");
        lyrics.add("{FADE}{FAST}Got a new [%150]piece[%], 'boutta go put on some {SHAKE}[WHITE]teeth{ENDSHAKE}. [+grinning face with big eyes]{ENDFADE}");
        lyrics.add("{FADE}Got a new [%150]crib[%], 'boutta go invite some  [+people holding hands, light skin tone, medium-dark skin tone] friends.{ENDFADE}");
        lyrics.add("{FADE}{FAST}Gettin' bored of this {SPIN=1;1} [+earth-america]Earth{ENDSPIN}, 'boutta go create some ends.{ENDFADE}");

        lyrics.add("{FADE}They don't want me to {EMERGE}begin{ENDEMERGE}, they just want me to {ENDFADE}{SHRINK=1.0;1.0;true}end.{ENDSHRINK}");
        lyrics.add("{FADE}I ain't 'boutta pretend, 'boutta go call the [BLUE][%?JOSTLE]feds[%]{CLEARCOLOR}. [+man police officer, light skin tone]{ENDFADE}");
        lyrics.add("{FADE}I don't like these hoes, 'boutta go call they friends.{ENDFADE}");
        lyrics.add("{FADE}And the plug call me, he 'boutta send a [+cardboard-box]care package.{ENDFADE}");

        lyrics.add("{FADE}{SPEED=0.95}I'ma open that bitch and go {JUMP}grab it{ENDJUMP}.{ENDFADE}");
        lyrics.add("{FADE}{SPEED=0.5}I just get to it, I gotta have it.{ENDFADE}");
        lyrics.add("{FADE}They gon' need a [%250]big casket[%]. [+coffin]{ENDFADE}");
        lyrics.add("{FADE}And the hoes wanna meet, wanna know my [%150]status[%].{ENDFADE}");

        lyrics.add("{FADE}{FASTER}They wanna know what I eat, wanna know my habits[+repeat button]{ENDFADE}");
        lyrics.add("{FADE}{FASTER}They wanna see what I do, wanna know my patterns[+poultry leg]{ENDFADE}");
        lyrics.add("{FADE}{FASTER}They wanna see what I wear, wanna see my fabrics [+t-shirt]{ENDFADE}");
        lyrics.add("{FADE}And I'm {EMERGE}fresh out the box{ENDEMERGE} like I was a [+rabbit]rabbit.{ENDFADE}");

        lyrics.add("{FADE}And the plug came through, he was wrapped in {SHAKE}[WHITE]plastic{ENDSHAKE}.{ENDFADE}");
        lyrics.add("{FADE}I ain't even know what was in the [%150]package[%].{ENDFADE}");
        lyrics.add("{FADE}We was like a {RAINBOW}band{ENDRAINBOW} the way we came in with...{ENDFADE}");




        //Completed echoe in the room lyric markup
        lyrics.add("[%?SHINY][%150]High Voltage, Suno[%][%]");
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
        table.add(typingLabel).width(1020f); // Add the label and set a max width for wrapping
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
