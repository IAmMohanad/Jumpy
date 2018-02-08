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
import com.badlogic.gdx.utils.Align;
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

    private void addRankingToTable(Table table, String labelText, String value){
        Label totalGoldLabel = new Label(labelText, skin, "skin-normal");
        Label totalGoldScore = new Label(value, skin, "skin-normal");
        table.add(totalGoldLabel).expandX().left().padLeft(5);
        table.add(totalGoldScore).expandX().left();
    }

    private Table isConnected(){
        getPlayerRankings(playerPrefs.getString("username"));
        Table table = new Table();
        Table innerTable = new Table();

        Label personalRankingTitle = new Label("PERSONAL RANKING - TOTAL PLAYERS: "+playerScoreMap.get("totalPlayers"), skin, "skin-normal");
        innerTable.add(personalRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);

        innerTable.row();
        addRankingToTable(innerTable, "Total Points: ", playerScoreMap.get("totalPoints"));

        innerTable.row();
        addRankingToTable(innerTable, "Total Gold: ", playerScoreMap.get("totalGold"));

        innerTable.row();
        addRankingToTable(innerTable, "Total Stars:", playerScoreMap.get("totalStars"));

        innerTable.row();
        addRankingToTable(innerTable, "Total Time Played:", playerScoreMap.get("totalTimePlayed"));

        innerTable.row();//empty line for formatting
        innerTable.add(new Label("", skin, "skin-normal"));

        innerTable.row();
        Label levelOneRankingTitle = new Label("LEVEL ONE RANKING", skin, "skin-normal");
        innerTable.add(levelOneRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);

        String suffix;
        if(Integer.parseInt(levelOneScoreMap.get("player_rank")) % 10 == 1){
            suffix = "st";
        } else if(Integer.parseInt(levelOneScoreMap.get("player_rank")) % 10 == 2){
            suffix = "nd";
        } else if(Integer.parseInt(levelOneScoreMap.get("player_rank")) % 10 == 3){
            suffix = "rd";
        } else{
            suffix = "th";
        }
        innerTable.row();

        addRankingToTable(innerTable, "Your Rank: ", levelOneScoreMap.get("player_rank")+suffix);

        innerTable.row();
        addRankingToTable(innerTable, "Fastest Time: ", levelOneScoreMap.get("fastest_time")+"secs");

        innerTable.row();
        addRankingToTable(innerTable, "Max Points Earned: ", levelOneScoreMap.get("max_points"));

        innerTable.row();//empty line for formatting
        innerTable.add(new Label("", skin, "skin-normal"));
        innerTable.row();
        Label levelTwoRankingTitle = new Label("LEVEL TWO RANKING", skin, "skin-normal");
        innerTable.add(levelTwoRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);

        if(Integer.parseInt(levelTwoScoreMap.get("player_rank")) % 10 == 1){
            suffix = "st";
        } else if(Integer.parseInt(levelTwoScoreMap.get("player_rank")) % 10 == 2){
            suffix = "nd";
        } else if(Integer.parseInt(levelTwoScoreMap.get("player_rank")) % 10 == 3){
            suffix = "rd";
        } else{
            suffix = "th";
        }
        innerTable.row();

        addRankingToTable(innerTable, "Your Rank: ", levelTwoScoreMap.get("player_rank")+suffix);

        innerTable.row();
        addRankingToTable(innerTable, "Fastest Time: ", levelTwoScoreMap.get("fastest_time")+"secs");

        innerTable.row();
        addRankingToTable(innerTable, "Max Points Earned: ", levelTwoScoreMap.get("max_points"));

        innerTable.row();//empty line for formatting
        innerTable.add(new Label("", skin, "skin-normal"));
        innerTable.row();
        Label levelThreeRankingTitle = new Label("LEVEL THREE RANKING", skin, "skin-normal");
        innerTable.add(levelThreeRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);

        if(Integer.parseInt(levelThreeScoreMap.get("player_rank")) % 10 == 1){
            suffix = "st";
        } else if(Integer.parseInt(levelThreeScoreMap.get("player_rank")) % 10 == 2){
            suffix = "nd";
        } else if(Integer.parseInt(levelThreeScoreMap.get("player_rank")) % 10 == 3){
            suffix = "rd";
        } else{
            suffix = "th";
        }
        innerTable.row();

        addRankingToTable(innerTable, "Your Rank: ", levelThreeScoreMap.get("player_rank")+suffix);

        innerTable.row();
        addRankingToTable(innerTable, "Fastest Time: ", levelThreeScoreMap.get("fastest_time")+"secs");

        innerTable.row();
        addRankingToTable(innerTable, "Max Points Earned: ", levelThreeScoreMap.get("max_points"));

        innerTable.row();

        ScrollPane rankingsScrollPane = new ScrollPane(innerTable, skin, "default-no-slider");
        table.add(rankingsScrollPane).height(200).left().padLeft(5).expandX();

        table.row();
        //add back button here
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
        table.add(backButton).left().expandX().bottom().padLeft(5).padTop(20);

        return table;
    }

    private void setConnected(boolean isConn){
        if(isConn){
            isConnected = true;
        } else{
            isConnected = false;
        }
    }

    private void checkConnection(){
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
        long startTime = System.currentTimeMillis();
        int elpasedTime = (int) (System.currentTimeMillis() - startTime) / 1000;
        while(elpasedTime < 0.5){
            System.out.println(String.valueOf(elpasedTime));
            elpasedTime = (int) (System.currentTimeMillis() - startTime) / 1000;
            //do nothing, 1sec delay to check for connection
        }
        return;
    }

    private Table showLoading(){
        Table rankingsTable = new Table();
        rankingsTable.add(new Label("PERSONAL RANKING", skin, "skin-normal")).right().expandX().colspan(2);

        Table table = new Table();
        Label message = new Label("Connecting to ranking server...", skin, "skin-normal");
        table.add(message).left().expandX();

        return table;
    }

    public Stage create(){
        loadSound();

        Table innerTable;
        Table table = new Table();
        table.setFillParent(true);

        checkConnection();
        if(isConnected){
            innerTable = isConnected();
        } else{
            innerTable = noConnection();
        }

        table.add(innerTable).expandX().expandY().top().left();

        //rankings row with scrollpane, scrollpane has table inside with 3 columns.
        //buttons row
        //ScrollPane rankingsPane = new ScrollPane(rankingsTable, skin, "default-no-slider");
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
                playerScoreMap.put("totalPlayers", array[5]);

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
                levelOneScoreMap.put("total_players", array[4]);
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
                levelTwoScoreMap.put("total_players", array[4]);

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
                levelThreeScoreMap.put("total_players", array[4]);
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
        long startTime = System.currentTimeMillis();
        int elpasedTime = (int) (System.currentTimeMillis() - startTime) / 1000;
        while(elpasedTime < 0.25){
            System.out.println(String.valueOf(elpasedTime));
            elpasedTime = (int) (System.currentTimeMillis() - startTime) / 1000;
            //do nothing, 1sec delay to check for connection
        }
        return;
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
