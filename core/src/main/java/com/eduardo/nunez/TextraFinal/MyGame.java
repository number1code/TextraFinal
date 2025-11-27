package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Game;
import com.github.tommyettinger.textra.TypingConfig;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class MyGame extends Game {
        @Override
        public void create() {
                // Useful combined effects saved into variables
                // Subzero Trashcore
                TypingConfig.GLOBAL_VARS.put("SUBZERO", "{VAR=SHIVERINGBLIZZARD}[%125]");
                TypingConfig.GLOBAL_VARS.put("ENDSUBZERO", "[%]{VAR=ENDSHIVERINGBLIZZARD}");

                TypingConfig.GLOBAL_VARS.put("GHOST_WALK", "{FADE}{WAVE=0.05;3;0.5}{COLOR=pale weak gray}");
                TypingConfig.GLOBAL_VARS.put("ENDGHOST_WALK", "{CLEARCOLOR}{ENDWAVE}");

                TypingConfig.GLOBAL_VARS.put("HELLGATE", "{VAR=SPUTTERINGFIRE}[darkest red]");
                TypingConfig.GLOBAL_VARS.put("ENDHELLGATE", "{CLEARCOLOR}{VAR=ENDSPUTTERINGFIRE}");

                TypingConfig.GLOBAL_VARS.put("GLOCK_HIT", "{SLAM}[%150][metallic silver]");
                TypingConfig.GLOBAL_VARS.put("ENDGLOCK_HIT", "[%]{CLEARCOLOR}{ENDSLAM}");

                // Efficient Machine
                TypingConfig.GLOBAL_VARS.put("BLUEPRINT_SCAN",
                                "{SLIDE=2;2.5;false}{GRADIENT=lightest cyan;dark blue;1;1}");
                TypingConfig.GLOBAL_VARS.put("ENDBLUEPRINT_SCAN", "{ENDGRADIENT}{ENDSLIDE}");

                TypingConfig.GLOBAL_VARS.put("GEAR_GRIND", "{CROWD=5;1.5}{COLOR=dark gray}");
                TypingConfig.GLOBAL_VARS.put("ENDGEAR_GRIND", "{CLEARCOLOR}{ENDCROWD}");

                TypingConfig.GLOBAL_VARS.put("EARTHQUAKE", "{SHAKE=3;2.5;0.8}[%175][dark dirt brown]");
                TypingConfig.GLOBAL_VARS.put("ENDEARTHQUAKE", "[%]{CLEARCOLOR}{ENDSHAKE}");

                TypingConfig.GLOBAL_VARS.put("NEON_GLINT", "{STYLE=NEON}{COLOR=light metallic cyan}");
                TypingConfig.GLOBAL_VARS.put("ENDNEON_GLINT", "{CLEARCOLOR}{STYLE=DEFAULT}");

                // Iron Howl
                TypingConfig.GLOBAL_VARS.put("IRON_HOWL",
                                "{SHAKE=1.0;0.6}{JOLT=1.0;1.0;inf;0.6;white;light metallic gray}[%150]");
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
                // TypingConfig.GLOBAL_VARS.put("WAR_DRUM",
                // "{SHAKE=1.5;1.5;0.5}{SQUASH=1.5;2;false}[%125][dark red]");
                // TypingConfig.GLOBAL_VARS.put("ENDWAR_DRUM",
                // "[%]{ENDSQUASH}{ENDSHAKE}{CLEARCOLOR}");
                TypingConfig.GLOBAL_VARS.put("FROST_BREATH", "{VAR=SHIVERINGBLIZZARD}");
                TypingConfig.GLOBAL_VARS.put("ENDFROST_BREATH", "{VAR=ENDSHIVERINGBLIZZARD}");

                // Dream in the Vapor
                TypingConfig.GLOBAL_VARS.put("VAPOR_DREAM",
                                "{FADE}{WAVE=0.05;3;0.5}{GRADIENT=light cyan;light magenta;1;1}");
                TypingConfig.GLOBAL_VARS.put("ENDVAPOR_DREAM", "{ENDGRADIENT}{ENDWAVE}{ENDFADE}");

                TypingConfig.GLOBAL_VARS.put("SKANK_RHYTHM", "{JUMP=0.7;0.05;1.0}");
                TypingConfig.GLOBAL_VARS.put("ENDSKANK_RHYTHM", "{ENDJUMP}");

                TypingConfig.GLOBAL_VARS.put("GHOST_ECHO", "{FADE}{WAVE=0.05;3;0.5}{COLOR=pale weak gray}");
                TypingConfig.GLOBAL_VARS.put("ENDGHOST_ECHO", "{CLEARCOLOR}{ENDWAVE}{ENDFADE}");

                TypingConfig.GLOBAL_VARS.put("NEON_MOON", "{STYLE=NEON}{COLOR=light magenta}");
                TypingConfig.GLOBAL_VARS.put("ENDNEON_MOON", "{CLEARCOLOR}{STYLE=DEFAULT}");

                // MR. LIZARD - GLOBAL STYLES

                // The Main Brand: Friendly, confident green gradient with a slight bounce
                TypingConfig.GLOBAL_VARS.put("LIZARD_BRAND", "{JUMP=0.5;1;1}{GRADIENT=lime;forest green;1;1}[@pixel]");
                TypingConfig.GLOBAL_VARS.put("ENDLIZARD_BRAND", "[@]{ENDGRADIENT}{ENDJUMP}");

                // Construction Impacts: For "Fixed", "Build", "Mend" - metallic and heavy
                TypingConfig.GLOBAL_VARS.put("FIX_HIT", "{SLAM}[%135][metallic silver]");
                TypingConfig.GLOBAL_VARS.put("ENDFIX_HIT", "[%]{CLEARCOLOR}{ENDSLAM}");

                // Water/Leaks: Wobbly, blue effect
                TypingConfig.GLOBAL_VARS.put("WATER_WOBBLE", "{SICK=1;0.5;inf}{COLOR=sky blue}");
                TypingConfig.GLOBAL_VARS.put("ENDWATER_WOBBLE", "{CLEARCOLOR}{ENDSICK}");

                // Warmth/Light: For "Sun", "Shine", "Flow" - Warm glow
                TypingConfig.GLOBAL_VARS.put("SUN_GLOW", "{WAVE=0.05;2;0.5}{STYLE=NEON}{COLOR=gold}");
                TypingConfig.GLOBAL_VARS.put("ENDSUN_GLOW", "{CLEARCOLOR}{STYLE=DEFAULT}{ENDWAVE}");

                // The "Squeak": A high frequency shake
                TypingConfig.GLOBAL_VARS.put("SQUEAK", "{SHAKE=0.5;3;3}[orange]");
                TypingConfig.GLOBAL_VARS.put("ENDSQUEAK", "{CLEARCOLOR}{ENDSHAKE}");

                // This an Attempt at Life global styles
                // The War Cry: Aggressive red shake with military stencil emphasis
                TypingConfig.GLOBAL_VARS.put("WAR_SCREAM", "{SHAKE=1;1;inf}{COLOR=dark red}[@pixel]");
                TypingConfig.GLOBAL_VARS.put("ENDWAR_SCREAM", "[@]{CLEARCOLOR}{ENDSHAKE}");

                // Ghostly Haunting: Fading, waving grey
                TypingConfig.GLOBAL_VARS.put("GHOST_MIST", "{WAVE=0.05;3;0.5}{FADE}[light gray]");
                TypingConfig.GLOBAL_VARS.put("ENDGHOST_MIST", "[%]{ENDFADE}{ENDWAVE}");

                // Blood/Body Horror: Sick melting effect for "Wreck", "Beast", "Blood"
                TypingConfig.GLOBAL_VARS.put("BLOOD_DRIP", "{SICK=1;1;inf}{COLOR=blood red}");
                TypingConfig.GLOBAL_VARS.put("ENDBLOOD_DRIP", "{CLEARCOLOR}{ENDSICK}");

                // Metallic Impact: For "Locked", "Pinned", "Tools"
                TypingConfig.GLOBAL_VARS.put("STEEL_SLAM", "{SLAM}[%135][metallic silver][@pixel]");
                TypingConfig.GLOBAL_VARS.put("ENDSTEEL_SLAM", "[@][white][%]{ENDSLAM}");

                // The "Thin Line": A sliding gradient
                TypingConfig.GLOBAL_VARS.put("THIN_LINE", "{SLIDE=2;1;false}{GRADIENT=white;dark gray;1;1}");
                TypingConfig.GLOBAL_VARS.put("ENDTHIN_LINE", "{ENDGRADIENT}{ENDSLIDE}");
                // setScreen(new VfxProductionSquareScreen(this));
                // setScreen(new VfxProductionScreen(this));
                setScreen(new AutoProductionScreen());
                // setScreen(new TwoFontScreen(this));
        }

}
