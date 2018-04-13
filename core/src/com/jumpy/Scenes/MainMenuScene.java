package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Screens.ScreenManager;
import com.jumpy.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class MainMenuScene {

    private Stage stage;
    private Jumpy game;
    private Viewport viewport;

    private Skin mainMenuSkin;

    private Sound click;
    private Music backgroundMusic;

    private Preferences settings;

    public MainMenuScene(Jumpy game){
        this.game = game;
        mainMenuSkin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        settings = Gdx.app.getPreferences("settings");
    }

    public Stage create(){
        loadSound();
        // game.soundManager.playMusic(Jumpy.screenManager.getGameState());
        //System.out.println(settings.getBoolean("mute")+" playing? "+backgroundMusic.isPlaying());

        Table table = new Table();
        table.setFillParent(true);

        TextButton startButton = new TextButton("START", mainMenuSkin, "blueGreen");
        startButton.getLabelCell().padTop(15);
        TextButton settingButton = new TextButton("SETTINGS", mainMenuSkin, "blueGreen");
        settingButton.getLabelCell().padTop(15);
        TextButton hiscoreButton = new TextButton("HISCORE", mainMenuSkin, "blueGreen");
        hiscoreButton.getLabelCell().padTop(15);
        TextButton helpButton = new TextButton("HELP", mainMenuSkin, "blueGreen");
        helpButton.getLabelCell().padTop(15);
        TextButton exitButton = new TextButton("EXIT", mainMenuSkin, "blueGreen");
        exitButton.getLabelCell().padTop(15);

        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked start!");
                if(!game.mute){// game.soundOn){
                    click.play(game.volume);//game.volume);
                }
                //game.setLevel();//commented out during screenManager changes
                game.screenManager.setScreen(ScreenManager.GAME_STATE.LEVEL_SELECT);
                //game.setPlay();
            }
        });

        settingButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked settings!");
                if(!game.mute){// game.soundOn){
                    click.play(game.volume);//game.volume);
                }
                //game.setSettings();//commented out during screenManager changes
                game.screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_SETTINGS);
            }
        });

        hiscoreButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked settings!");
                if(!game.mute){// game.soundOn){
                    click.play(game.volume);//game.volume);
                }
                game.screenManager.setScreen(ScreenManager.GAME_STATE.HISCORE);
            }
        });

        helpButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked settings!");
                if(!game.mute){// game.soundOn){
                    click.play(game.volume);//game.volume);
                }
                game.screenManager.setScreen(ScreenManager.GAME_STATE.HELP);
            }
        });

        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked exit!");
                if(!game.mute){// game.soundOn){
                    click.play(game.volume);//game.volume);
                }
                Gdx.app.exit();
            }
        });

        table.add(startButton).padBottom(10);
        table.row();
        table.add(settingButton).padBottom(10);
        table.row();
        table.add(hiscoreButton).padBottom(10);
        table.row();
        table.add(helpButton).padBottom(10);
        table.row();
        table.add(exitButton);


        stage.addActor(table);

        return stage;
    }

    public void loadSound(){
        game.soundManager.playMusic(ScreenManager.GAME_STATE.MAIN_MENU);
        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
        //backgroundMusic = Gdx.audio.newMusic((Gdx.files.internal("music/Celestial Harps.ogg")));
        //SoundManager.loadBackgroundMusic();
    }

    public void render() {
        /*if(game.mute){
            backgroundMusic.pause();
        }*/
        //backgroundMusic.setVolume(game.volume);
        stage.act();
        stage.draw();
    }

}
