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
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Screens.ScreenManager;
import com.jumpy.SoundManager;

import java.util.HashMap;
import java.util.Map;

public class HiscoreScene {
    private Stage stage;
    private Jumpy game;
    private Viewport viewport;

    private Skin skin;

    private Sound click;

    private Preferences playerPrefs;
    private String username;

    private boolean isConnected;
    private Map<String, String> playerScoreMap;
    private Map<String, String> levelOneScoreMap;
    private Map<String, String> levelTwoScoreMap;
    private Map<String, String> levelThreeScoreMap;

    private HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
    private Net.HttpRequest httpRequest;

    public HiscoreScene(Jumpy game){
        this.game = game;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));

        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        playerPrefs = Gdx.app.getPreferences("userPrefs");
        username = playerPrefs.getString("username", null);
        isConnected = false;
    }

    private Table noConnection(){
        Table table = new Table();
        //table.setFillParent(true);
        Label message = new Label("Could not connect to internet,\ntry again in a few minutes...", skin, "skin-normal");
        table.add(message).left().expandX();
        table.row();
        Label backButton = new Label("BACK", skin, "skin-normal");

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked bacak in no connection scene!");
                if(!game.mute){// game.soundOn){
                    click.play(game.volume);//game.volume);
                }
                game.screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
            }
        });
        table.add(backButton).left().expandX().bottom().padTop(20);
        return table;
    }

    private void isConnected(){
        Table rankingsTable = new Table();
        rankingsTable.setFillParent(true);
        rankingsTable.add(new Label("PERSONAL RANKING", skin, "skin-normal")).right().expandX().colspan(2);
    }

    private void setConnected(boolean isConn){
        if(isConn){
            isConnected = true;
        } else{
            isConnected = false;
        }
    }

    private void checkConnection(){
        float timer = 0f;
        httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("http://127.0.0.1:8000/hiscores/checkConnection").content("").build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    return;
                }
                String status = httpResponse.getResultAsString();
                System.out.println(status);
                setConnected(true);
            }

            public void failed(Throwable t) {
                String status = "failed";
                setConnected(false);
            }

            @Override
            public void cancelled() {
                String status = "cancelled";
                System.out.println(status);
                setConnected(false);
            }
        });
        timer = timer + Gdx.graphics.getDeltaTime();
        while(timer < 1f){
            timer = timer + Gdx.graphics.getDeltaTime();
            //do nothing, 1sec delay to check for connection
        }
        return;
    }

    public Stage create(){
        loadSound();

        Table innerTable = new Table();
        Table table = new Table();
        table.setFillParent(true);

        checkConnection();

        if(isConnected){
            isConnected();
        } else{
            innerTable = noConnection();
        }

        table.add(innerTable);

        //rankings row with scrollpane, scrollpane has table inside with 3 columns.
        //buttons row
        //ScrollPane rankingsPane = new ScrollPane(rankingsTable, skin, "default-no-slider");

        //check if connected
        //update score
        /*httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("http://127.0.0.1:8000/hiscores/update/golden751/1/60012/11/3/20").content("").build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    return;
                }
                String status = httpResponse.getResultAsString();
                System.out.println(status);
        }

            public void failed(Throwable t) {
                String status = "failed";
                System.out.println(status + " " + t.getMessage());
            }

            @Override
            public void cancelled() {
                String status = "cancelled";
                System.out.println(status);
            }
        });*/

        stage.addActor(table);
        return stage;
    }

    private void getPlayerRankings(String playerName){
        //get player scores
        httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("http://127.0.0.1:8000/hiscores/getPlayerScore/"+playerName).content("").build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    return;
                }
                String status = httpResponse.getResultAsString();
                //System.out.println(status);
                JsonValue json = new JsonReader().parse(status);
                JsonValue mess = json.get("message");
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
                playerScoreMap = new HashMap<String, String>();
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
                levelOneScoreMap = new HashMap<String, String>();
                levelOneScoreMap.put("completed", array[0]);
                levelOneScoreMap.put("player_rank", array[1]);
                levelOneScoreMap.put("fastest_time", array[2]);
                levelOneScoreMap.put("max_points", array[3]);
                /*for(String s : array){
                    System.out.println(s);
                }*/

                i=0;
                //get player rankings for level one, copy paste for level 2/3
                JsonValue levelTwoInfo = mess.get("level_two");
                array = new String[levelTwoInfo.size];
                for (JsonValue info : levelTwoInfo){
                    if(!info.isString()){
                        array[i] = String.valueOf(info.asInt()).replace("\n", "");
                    } else{
                        array[i] = info.asString().replace("\n", "");
                    }
                    i++;
                }
                levelTwoScoreMap = new HashMap<String, String>();
                levelTwoScoreMap.put("completed", array[0]);
                levelTwoScoreMap.put("player_rank", array[1]);
                levelTwoScoreMap.put("fastest_time", array[2]);
                levelTwoScoreMap.put("max_points", array[3]);

                i=0;
                //get player rankings for level one, copy paste for level 2/3
                JsonValue levelThreeInfo = mess.get("level_three");
                array = new String[levelTwoInfo.size];
                for (JsonValue info : levelThreeInfo){
                    if(!info.isString()){
                        array[i] = String.valueOf(info.asInt()).replace("\n", "");
                    } else{
                        array[i] = info.asString().replace("\n", "");
                    }
                    i++;
                }
                levelThreeScoreMap = new HashMap<String, String>();
                levelThreeScoreMap.put("completed", array[0]);
                levelThreeScoreMap.put("player_rank", array[1]);
                levelThreeScoreMap.put("fastest_time", array[2]);
                levelThreeScoreMap.put("max_points", array[3]);
            }

            public void failed(Throwable t) {
                String status = "failed " + t.getMessage();
                //System.out.println(status);
            }

            @Override
            public void cancelled() {
                String status = "cancelled";
                //System.out.println(status);
            }
        });
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
