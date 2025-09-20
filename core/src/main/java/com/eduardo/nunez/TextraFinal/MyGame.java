package com.eduardo.nunez.TextraFinal;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MyGame extends Game {
    @Override
    public void create() {
        //setScreen(new VfxProductionSquareScreen(this));
        setScreen(new VfxProductionScreen(this));
    }

}
