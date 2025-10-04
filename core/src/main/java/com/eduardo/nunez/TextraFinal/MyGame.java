package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Game;
import com.github.tommyettinger.textra.TypingConfig;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGame extends Game {
    @Override
    public void create() {
        //Useful combined effects saved into variables
        TypingConfig.GLOBAL_VARS.put("DECAY", "{SICK=0.5;0.8}{COLOR=dull weak green}");
        TypingConfig.GLOBAL_VARS.put("ENDDECAY", "{ENDSICK}{CLEARCOLOR}");
        //Ashes in the air
        TypingConfig.GLOBAL_VARS.put("ASH_FALL", "{FADE=0;1;2}{WIND=0.05;0.05;4;0.5}{COLOR=pale weak gray}");
        TypingConfig.GLOBAL_VARS.put("ENDASH_FALL", "{CLEARCOLOR}{ENDWIND}{ENDFADE}");

        TypingConfig.GLOBAL_VARS.put("FLICKER_LIGHT", "{JOLT=1;1;0.3;0.1;black;pale yellow}");
        TypingConfig.GLOBAL_VARS.put("ENDFLICKER_LIGHT", "{ENDJOLT}");

        TypingConfig.GLOBAL_VARS.put("SMOKE_LUNGS", "{SICK=0.5;0.8}{WAVE=0.05;3;0.5}{COLOR=darkest gray}");
        TypingConfig.GLOBAL_VARS.put("ENDSMOKE_LUNGS", "{CLEARCOLOR}{ENDWAVE}{ENDSICK}");

        TypingConfig.GLOBAL_VARS.put("GHOST_WHISPER", "{FADE=0.2;0.8;1.5}[%80][pale cyan]");
        TypingConfig.GLOBAL_VARS.put("ENDGHOST_WHISPER", "[%]{CLEARCOLOR}{ENDFADE}");

        //setScreen(new VfxProductionSquareScreen(this));
        //setScreen(new VfxProductionScreen(this));
        setScreen(new AutoProductionScreen(this));
    }

}
