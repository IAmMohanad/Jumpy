package com.jumpy.Screens;

import com.badlogic.gdx.Screen;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.HiscoreScene;

import java.util.HashMap;

public class ScreenManager{

    private Jumpy game;

    private HashMap<GAME_STATE, Screen> gameScreens;
    private GAME_STATE currentGameState;

    public enum GAME_STATE{
        MAIN_MENU,
        PLAY,
        LEVEL_SELECT,
        MAIN_SETTINGS,
        GAME_SETTINGS,
        SHOP,
        LOADING,
        HISCORE
    }

    public ScreenManager(Jumpy g){
        this.game = g;
        initGameScreens();
    }

    public void initGameScreens(){
        this.gameScreens = new HashMap<GAME_STATE, Screen>();
        this.gameScreens.put(GAME_STATE.MAIN_MENU, new MainMenuScreen(game));
        //this.gameScreens.put(GAME_STATE.PLAY, new PlayScreen(game));
        this.gameScreens.put(GAME_STATE.LEVEL_SELECT, new LevelSelectScreen(game));
        this.gameScreens.put(GAME_STATE.MAIN_SETTINGS, new SettingsScreen(game));
       // this.gameScreens.put(GAME_STATE.SHOP, new ShopScreen(game));

        // this.gameScreens.put(STATE.GAME_SETTINGS, new MainMenuScreen(game));// TODO create in-game setting screen
        // this.gameScreens.put(STATE.GAME_SETTINGS, new MainMenuScreen(game));// TODO create in-game pause screen
    }

    public void setScreen(GAME_STATE screen){
        if(screen == GAME_STATE.PLAY){
            diposeScreen(GAME_STATE.PLAY);
            this.gameScreens.put(GAME_STATE.PLAY, new PlayScreen(game));
        }
        if(screen == GAME_STATE.SHOP){
            diposeScreen(GAME_STATE.SHOP);
            this.gameScreens.put(GAME_STATE.SHOP, new ShopScreen(game));
        }
        if(screen == GAME_STATE.HISCORE){
            diposeScreen(GAME_STATE.HISCORE);
            this.gameScreens.put(GAME_STATE.HISCORE, new HiscoreScreen(game));
        }

        currentGameState = screen;
        game.setScreen(gameScreens.get(screen));
    }

    public void diposeScreen(GAME_STATE st){
        for(GAME_STATE state : gameScreens.keySet()){
            if(state != null && state == st){
                Screen screen = gameScreens.get(st);
                screen.dispose();
            }
        }
    }

    public void dispose(){
        for(Screen screen : gameScreens.values()){
            if(screen != null){
                screen.dispose();
            }
        }
    }

    public GAME_STATE getGameState(){
        return currentGameState;
    }
}