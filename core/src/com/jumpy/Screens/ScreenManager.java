package com.jumpy.Screens;

import com.badlogic.gdx.Screen;
import com.jumpy.Jumpy;

import java.util.HashMap;

public class ScreenManager{

    private Jumpy game;

    private HashMap<STATE, Screen> gameScreens;

    public enum STATE{
        MAIN_MENU,
        PLAY,
        LEVEL_SELECT,
        MAIN_SETTINGS,
        GAME_SETTINGS
    }

    public ScreenManager(Jumpy g){
        this.game = g;
        initGameScreens();
    }

    public void initGameScreens(){
        this.gameScreens = new HashMap<STATE, Screen>();
        this.gameScreens.put(STATE.MAIN_MENU, new MainMenuScreen(game));
        this.gameScreens.put(STATE.PLAY, new PlayScreen(game));
        this.gameScreens.put(STATE.LEVEL_SELECT, new LevelSelectScreen(game));
        this.gameScreens.put(STATE.MAIN_SETTINGS, new SettingsScreen(game));
        // this.gameScreens.put(STATE.GAME_SETTINGS, new MainMenuScreen(game));// TODO create in-game setting screen
        // this.gameScreens.put(STATE.GAME_SETTINGS, new MainMenuScreen(game));// TODO create in-game pause screen
    }

    public void setScreen(STATE screen){
        game.setScreen(gameScreens.get(screen));
    }

    public void dispose(){
        for(Screen screen : gameScreens.values()){
            if(screen != null){
                screen.dispose();
            }

        }
    }
}
