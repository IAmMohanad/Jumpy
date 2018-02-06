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
        if(!game.mute){// game.soundOn){
           /* backgroundMusic.setVolume(game.volume);
            backgroundMusic.setLooping(true);
            backgroundMusic.play();*/
            SoundManager.playBackgroundMusic();
        }
        //System.out.println(settings.getBoolean("mute")+" playing? "+backgroundMusic.isPlaying());

        Table table = new Table();
        table.setFillParent(true);

        TextButton startButton = new TextButton("START", mainMenuSkin, "blueGreen");
        startButton.getLabelCell().padTop(15);
        TextButton settingButton = new TextButton("SETTINGS", mainMenuSkin, "blueGreen");
        settingButton.getLabelCell().padTop(15);
        TextButton creditButton = new TextButton("CREDITS", mainMenuSkin, "blueGreen");
        creditButton.getLabelCell().padTop(15);
        TextButton shopButton = new TextButton("SHOP", mainMenuSkin, "blueGreen");
        shopButton.getLabelCell().padTop(15);
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

        shopButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked settings!");
                if(!game.mute){// game.soundOn){
                    click.play(game.volume);//game.volume);
                }
                game.screenManager.setScreen(ScreenManager.GAME_STATE.SHOP);
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

        String status1;
        //update score
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest;
        /*httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("http://127.0.0.1:8000/hiscores/update/golden751/1/60012/11/3/20").content("").build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    return;
                }
                String status = httpResponse.getResultAsString();
                //System.out.println(status);
                someMethods(status);
        }

            public void failed(Throwable t) {
                String status = "failed";
                //System.out.println(status);
                someMethods(status);
            }

            @Override
            public void cancelled() {
                String status = "cancelled";
                //System.out.println(status);
                someMethods(status);
            }
        });*/

        //get uesr scores
        httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("http://127.0.0.1:8000/hiscores/getPlayerScore/golden751").content("").build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    return;
                }
                String status = httpResponse.getResultAsString();
                //System.out.println(status);
                someMethods(status);
                JsonValue json = new JsonReader().parse(status);
                JsonValue mess = json.get("message");//.get("player_rankings")
                //Store player rankings into hash maps
                JsonValue playerInfo = mess.get("player_rankings");
                String[] array = new String[playerInfo.size];
                int i=0;
                for (JsonValue info : playerInfo.iterator()){ // iterator() returns a list of children
                    //"total_points": 59696, "total_gold": 129, "total_stars": 17, "total_time_played": 584}, "level_one": {"completed": true, "player_rank": 2, "fastest_time": 0, "max_points": 6010}, "level_two": {"completed": true, "player_rank": 2, "fastest_time": 0, "max_points": 51}, "level_three": {"completed": false, "player_rank": 1, "fastest_time": 0, "max_points": 0}
                    System.out.println(info.isString());
                    if(!info.isString()){
                        array[i] = String.valueOf(info.asInt()).replace("\n", "");
                    } else{
                        array[i] = info.asString().replace("\n", "");
                    }
                    i++;
                }
                Map<String, String> playerScoreMap = new HashMap<String, String>();
                playerScoreMap.put("username", array[0]);
                playerScoreMap.put("totalPoints", array[1]);
                playerScoreMap.put("totalGold", array[2]);
                playerScoreMap.put("totalStars", array[3]);
                playerScoreMap.put("totalTimePlayed", array[4]);

                i=0;
                //get player rankings for level one, copy paste for level 2/3
                JsonValue levelOneInfo = mess.get("level_one");
                array = new String[levelOneInfo.size];
                for (JsonValue info : levelOneInfo){
                    if(!info.isString()){
                        array[i] = String.valueOf(info.asInt()).replace("\n", "");
                    } else{
                        array[i] = info.asString().replace("\n", "");
                    }
                    i++;
                }
                Map<String, String> levelOneScoreMap = new HashMap<String, String>();
                levelOneScoreMap.put("completed", array[0]);
                levelOneScoreMap.put("player_rank", array[1]);
                levelOneScoreMap.put("fastest_time", array[2]);
                levelOneScoreMap.put("max_points", array[3]);
                /*for(String s : array){
                    System.out.println(s);
                }*/

            }

            public void failed(Throwable t) {
                String status = "failed " + t.getMessage();
                //System.out.println(status);
                someMethods(status);
            }

            @Override
            public void cancelled() {
                String status = "cancelled";
                //System.out.println(status);
                someMethods(status);
            }
        });

        //add Net. before all HttpResponse if error
        /*Gdx.net.sendHttpRequest (httpPost, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    return;
                }
                status = httpResponse.getResultAsString();
            }

            public void failed(Throwable t) {
                status = "failed";
            }
        });*/


        table.add(startButton).padBottom(10);
        table.row();
        table.add(settingButton).padBottom(10);
        table.row();
        table.add(creditButton).padBottom(10);
        table.row();
        table.add(shopButton).padBottom(10);
        table.row();
        table.add(exitButton);


        stage.addActor(table);

        return stage;
    }

    public void someMethods(String status){
        System.out.println(status);
    }
    public void loadSound(){

        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
        //backgroundMusic = Gdx.audio.newMusic((Gdx.files.internal("music/Celestial Harps.ogg")));
        SoundManager.loadBackgroundMusic();
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
