package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Game;
import com.github.tommyettinger.textra.TypingConfig;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGame extends Game {
    @Override
    public void create() {
        //Useful combined effects saved into variables
        //Subzero Trashcore
        TypingConfig.GLOBAL_VARS.put("SUBZERO", "{VAR=SHIVERINGBLIZZARD}[%125]");
        TypingConfig.GLOBAL_VARS.put("ENDSUBZERO", "[%]{VAR=ENDSHIVERINGBLIZZARD}");

        TypingConfig.GLOBAL_VARS.put("GHOST_WALK", "{FADE}{WAVE=0.05;3;0.5}{COLOR=pale weak gray}");
        TypingConfig.GLOBAL_VARS.put("ENDGHOST_WALK", "{CLEARCOLOR}{ENDWAVE}");

        TypingConfig.GLOBAL_VARS.put("HELLGATE", "{VAR=SPUTTERINGFIRE}[darkest red]");
        TypingConfig.GLOBAL_VARS.put("ENDHELLGATE", "{CLEARCOLOR}{VAR=ENDSPUTTERINGFIRE}");

        TypingConfig.GLOBAL_VARS.put("GLOCK_HIT", "{SLAM}[%150][metallic silver]");
        TypingConfig.GLOBAL_VARS.put("ENDGLOCK_HIT", "[%]{CLEARCOLOR}{ENDSLAM}");

        //Efficient Machine
        TypingConfig.GLOBAL_VARS.put("BLUEPRINT_SCAN", "{SLIDE=2;2.5;false}{GRADIENT=lightest cyan;dark blue;1;1}");
        TypingConfig.GLOBAL_VARS.put("ENDBLUEPRINT_SCAN", "{ENDGRADIENT}{ENDSLIDE}");

        TypingConfig.GLOBAL_VARS.put("GEAR_GRIND", "{CROWD=5;1.5}{COLOR=dark gray}");
        TypingConfig.GLOBAL_VARS.put("ENDGEAR_GRIND", "{CLEARCOLOR}{ENDCROWD}");

        TypingConfig.GLOBAL_VARS.put("EARTHQUAKE", "{SHAKE=3;2.5;0.8}[%175][dark dirt brown]");
        TypingConfig.GLOBAL_VARS.put("ENDEARTHQUAKE", "[%]{CLEARCOLOR}{ENDSHAKE}");

        TypingConfig.GLOBAL_VARS.put("NEON_GLINT", "{STYLE=NEON}{COLOR=light metallic cyan}");
        TypingConfig.GLOBAL_VARS.put("ENDNEON_GLINT", "{CLEARCOLOR}{STYLE=DEFAULT}");

        //Iron Howl
        TypingConfig.GLOBAL_VARS.put("IRON_HOWL", "{SHAKE=1.0;0.6}{JOLT=1.0;1.0;inf;0.6;white;light metallic gray}[%150]");
        TypingConfig.GLOBAL_VARS.put("ENDIRON_HOWL", "[%]{ENDJOLT}{ENDSHAKE}");

        TypingConfig.GLOBAL_VARS.put("GHOST_ECHO", "{FADE}{WAVE=0.05;3;0.5}{COLOR=pale weak gray}");
        TypingConfig.GLOBAL_VARS.put("ENDGHOST_ECHO", "{CLEARCOLOR}{ENDWAVE}{ENDFADE}");

        TypingConfig.GLOBAL_VARS.put("CLEAVER_CUT", "{SLIDE=3;3;false}{COLOR=light metallic silver}");
        TypingConfig.GLOBAL_VARS.put("ENDCLEAVER_CUT", "{CLEARCOLOR}{ENDSLIDE}");

        TypingConfig.GLOBAL_VARS.put("BLACK_TAR", "{SICK=1;1;inf}{COLOR=light black}");
        TypingConfig.GLOBAL_VARS.put("ENDBLACK_TAR", "{CLEARCOLOR}{ENDSICK}");

        TypingConfig.GLOBAL_VARS.put("WAR_DRUM", "{HEARTBEAT=1.2;0.8}{COLOR=rich dark red}");
        TypingConfig.GLOBAL_VARS.put("ENDWAR_DRUM", "{CLEARCOLOR}{ENDHEARTBEAT}");

        // NEW (Corrected and Thematic):
//        TypingConfig.GLOBAL_VARS.put("WAR_DRUM", "{SHAKE=1.5;1.5;0.5}{SQUASH=1.5;2;false}[%125][dark red]");
//        TypingConfig.GLOBAL_VARS.put("ENDWAR_DRUM", "[%]{ENDSQUASH}{ENDSHAKE}{CLEARCOLOR}");

        TypingConfig.GLOBAL_VARS.put("FROST_BREATH", "{VAR=SHIVERINGBLIZZARD}");
        TypingConfig.GLOBAL_VARS.put("ENDFROST_BREATH", "{VAR=ENDSHIVERINGBLIZZARD}");
        //setScreen(new VfxProductionSquareScreen(this));
        //setScreen(new VfxProductionScreen(this));
        setScreen(new AutoProductionScreen(this));
        //setScreen(new TwoFontScreen(this));
    }

}
