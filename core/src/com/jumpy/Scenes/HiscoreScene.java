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
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumpy.Jumpy;
import com.jumpy.Screens.ScreenManager;
import com.jumpy.SoundManager;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static com.jumpy.Jumpy.HISCORE_SERVER_URL;

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
    private boolean httpRequestFinished;
    private boolean updatingPlayerLevelssFinished;
    private boolean registeringNewPlayerFinished;
    private boolean checkingConnectionFinished;

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

    //Show no connection message
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
                if(!game.mute){
                    click.play(game.volume);
                }
                game.screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
            }
        });
        table.add(backButton).left().expandX().bottom().padTop(20);
        return table;
    }

    //Adds a row with the name labelText to the rankings table
    private void addRankingToTable(Table table, String labelText, String value){
        Label totalGoldLabel = new Label(labelText, skin, "skin-normal");
        Label totalGoldScore = new Label(value, skin, "skin-normal");
        table.add(totalGoldLabel).expandX().left().padLeft(5);
        table.add(totalGoldScore).expandX().left();
    }

    //Retrieve all the rankings for the player and populate them into tables
    private Table isConnected(){
        registerNewPlayer();
        updatePlayerRankings();
        updateLevelRankings(playerPrefs.getString("username"));
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
        addRankingToTable(innerTable, "Total Time Played:", playerScoreMap.get("totalTimePlayed"));

        innerTable.row();//empty line for formatting
        innerTable.add(new Label("", skin, "skin-normal"));

        innerTable.row();
        String suffix;

        //populateScores(innerTable, "ONE", levelOneScoreMap);
        //populateScores(innerTable, "TWO", levelTwoScoreMap);
        //populateScores(innerTable, "THREE", levelThreeScoreMap);

        if(levelOneScoreMap.get("completed").equals("true")){
            Label levelOneRankingTitle = new Label("LEVEL ONE RANKING", skin, "skin-normal");
            innerTable.add(levelOneRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);

            suffix = getNumberSuffix(Integer.parseInt(levelOneScoreMap.get("player_rank")));

            innerTable.row();

            addRankingToTable(innerTable, "Your Rank: ", levelOneScoreMap.get("player_rank")+suffix);

            innerTable.row();
            addRankingToTable(innerTable, "Fastest Time: ", levelOneScoreMap.get("fastest_time")+"secs");

            innerTable.row();
            addRankingToTable(innerTable, "Max Points Earned: ", levelOneScoreMap.get("max_points"));

            innerTable.row();//empty line for formatting
            innerTable.add(new Label("", skin, "skin-normal"));
            innerTable.row();
        } else{
            Label levelOneRankingTitle = new Label("LEVEL ONE NOT COMPLETE", skin, "skin-normal");
            innerTable.add(levelOneRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);
            innerTable.add(new Label("", skin, "skin-normal")).expandX().expandY().center().padLeft(5).colspan(3);
            innerTable.row();
        }

        if(levelTwoScoreMap.get("completed").equals("true")) {
            Label levelTwoRankingTitle = new Label("LEVEL TWO RANKING", skin, "skin-normal");
            innerTable.add(levelTwoRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);

            suffix = getNumberSuffix(Integer.parseInt(levelTwoScoreMap.get("player_rank")));

            innerTable.row();

            addRankingToTable(innerTable, "Your Rank: ", levelTwoScoreMap.get("player_rank") + suffix);

            innerTable.row();
            addRankingToTable(innerTable, "Fastest Time: ", levelTwoScoreMap.get("fastest_time") + "secs");

            innerTable.row();
            addRankingToTable(innerTable, "Max Points Earned: ", levelTwoScoreMap.get("max_points"));

            innerTable.row();//empty line for formatting
            innerTable.add(new Label("", skin, "skin-normal"));
            innerTable.row();
        } else{
            Label levelTwoRankingTitle = new Label("LEVEL TWO NOT COMPLETE", skin, "skin-normal");
            innerTable.add(levelTwoRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);
            innerTable.add(new Label("", skin, "skin-normal")).expandX().expandY().center().padLeft(5).colspan(3);
            innerTable.row();
        }

        if(levelThreeScoreMap.get("completed").equals("true")) {
            Label levelThreeRankingTitle = new Label("LEVEL THREE RANKING", skin, "skin-normal");
            innerTable.add(levelThreeRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);

            suffix = getNumberSuffix(Integer.parseInt(levelThreeScoreMap.get("player_rank")));

            innerTable.row();

            addRankingToTable(innerTable, "Your Rank: ", levelThreeScoreMap.get("player_rank") + suffix);

            innerTable.row();
            addRankingToTable(innerTable, "Fastest Time: ", levelThreeScoreMap.get("fastest_time") + "secs");

            innerTable.row();
            addRankingToTable(innerTable, "Max Points Earned: ", levelThreeScoreMap.get("max_points"));
            innerTable.row();
        } else{
            Label levelThreeRankingTitle = new Label("LEVEL THREE NOT COMPLETE", skin, "skin-normal");
            innerTable.add(levelThreeRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);
            innerTable.add(new Label("", skin, "skin-normal")).expandX().expandY().center().padLeft(5).colspan(3);
            innerTable.row();
        }

        ScrollPane rankingsScrollPane = new ScrollPane(innerTable, skin, "default-no-slider");
        table.add(rankingsScrollPane).height(200).left().padLeft(5).expandX();
        table.row();

        //add back button here
        Label backButton = new Label("BACK", skin, "skin-normal");

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked bacak in no connection scene!");
                if(!game.mute){
                    click.play(game.volume);
                }
                game.screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
            }
        });
        table.add(backButton).left().expandX().bottom().padLeft(5).padTop(20);

        return table;
    }

    //get the suffix of the given number
    private String getNumberSuffix(int number){
        int j = number % 10;
        int k = number % 100;
        if (j == 1 && k != 11) {
            return "st";
        }
        if (j == 2 && k != 12) {
            return "nd";
        }
        if (j == 3 && k != 13) {
            return "rd";
        }
        return "th";
    }

    //po
    private void populateScores(Table innerTable, String level, Map<String, String> scoreMap){
        String suffix;
        if(scoreMap.get("completed").equals("true")) {
            Label levelTwoRankingTitle = new Label("LEVEL "+level+" RANKING", skin, "skin-normal");
            innerTable.add(levelTwoRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);

            suffix = getNumberSuffix(Integer.parseInt(scoreMap.get("player_rank")));
            innerTable.row();

            addRankingToTable(innerTable, "Your Rank: ", scoreMap.get("player_rank") + suffix);

            innerTable.row();
            addRankingToTable(innerTable, "Fastest Time: ", scoreMap.get("fastest_time") + "secs");

            innerTable.row();
            addRankingToTable(innerTable, "Max Points Earned: ", scoreMap.get("max_points"));

            innerTable.row();//empty line for formatting
            innerTable.add(new Label("", skin, "skin-normal"));
            innerTable.row();
        } else{
            Label levelTwoRankingTitle = new Label("LEVEL "+level+" NOT COMPLETE", skin, "skin-normal");
            innerTable.add(levelTwoRankingTitle).expandX().expandY().center().padLeft(5).colspan(3);
            innerTable.add(new Label("", skin, "skin-normal")).expandX().expandY().center().padLeft(5).colspan(3);
            innerTable.row();
        }
    }

    private void setConnected(boolean isConn){
        if(isConn){
            isConnected = true;
        } else{
            isConnected = false;
        }
    }

    //sends a light-weight request to the hiscore server to check if the application has an internet connection
    private void checkConnection(){
        httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(Jumpy.HISCORE_SERVER_URL+"/checkConnection").content("").build();
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
                //checkConnection();
            }

            public void failed(Throwable t) {
                String status = "failed";
                setConnected(false);
                //noConnection();
            }

            @Override
            public void cancelled() {
                String status = "cancelled";
                System.out.println(status);
                setConnected(false);
                //noConnection();
            }
        });
        long startTime = System.currentTimeMillis();
        int elpasedTime = (int) (System.currentTimeMillis() - startTime) / 1000;
        while(elpasedTime < 0.5 || !isConnected){
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

        stage.addActor(table);
        return stage;
    }

    //updates the players personal rankings, NOT the levels
    private void updatePlayerRankings(){
        //path('update/<int:username>/<int:levelCompleted>/<int:pointsEarned>/<int:goldEarned>/<int:starsEarned>/<int:timeToComplete>'),
        httpRequestFinished = false;

        sendUpdateHttpRequest(Jumpy.HISCORE_SERVER_URL, "/updatePlayer/"+playerPrefs.getString("username")+"/"+playerPrefs.getInteger("lifetimeGoldEarned")+"/"+playerPrefs.getInteger("lifeTimeTimePlayed")+"/"+playerPrefs.getInteger("lifeTimeEnemiesKilled")+"/"+playerPrefs.getInteger("lifeTimepointsEarned"));

        while(!httpRequestFinished){
            //do nothing
            System.out.println("waiting for updatePlayerRankings to finish");
            if(httpRequestFinished){
                break;
            }
        }
        return;
    }

    //Sends Http request to update all levels in the game
    private void updateLevelRankings(String playerName){
        //path('update/<int:username>/<int:levelCompleted>/<int:pointsEarned>/<int:goldEarned>/<int:starsEarned>/<int:timeToComplete>'),
        Preferences levelOnePrefs = Gdx.app.getPreferences("1-1");
        Preferences levelTwoPrefs = Gdx.app.getPreferences("1-2");
        Preferences levelThreePrefs = Gdx.app.getPreferences("1-3");
        //Preferences levelFourPrefs = Gdx.app.getPreferences("1-4");

        httpRequestFinished = false;
        if(levelOnePrefs != null){
                sendUpdateHttpRequest(Jumpy.HISCORE_SERVER_URL, "/update/"+playerPrefs.getString("username")+"/1/"+levelOnePrefs.getInteger("pointsEarned")+"/"+levelOnePrefs.getInteger("goldEarned")+"/"+levelOnePrefs.getInteger("numberOfStars")+"/"+levelOnePrefs.getInteger("fastestCompletionTime"));
                levelOnePrefs.putBoolean("needsUpdate", false);
                levelOnePrefs.flush();
        }
        if(levelTwoPrefs != null) {
                sendUpdateHttpRequest(Jumpy.HISCORE_SERVER_URL, "/update/" + playerPrefs.getString("username") + "/2/" + levelTwoPrefs.getInteger("pointsEarned") + "/" + levelTwoPrefs.getInteger("goldEarned") + "/" + levelTwoPrefs.getInteger("numberOfStars") + "/" + levelTwoPrefs.getInteger("fastestCompletionTime"));
                levelTwoPrefs.putBoolean("needsUpdate", false);
                levelTwoPrefs.flush();
        }
        if(levelThreePrefs != null) {
                sendUpdateHttpRequest(Jumpy.HISCORE_SERVER_URL, "/update/" + playerPrefs.getString("username") + "/3/" + levelThreePrefs.getInteger("pointsEarned") + "/" + levelThreePrefs.getInteger("goldEarned") + "/" + levelThreePrefs.getInteger("numberOfStars") + "/" + levelThreePrefs.getInteger("fastestCompletionTime"));
                levelThreePrefs.putBoolean("needsUpdate", false);
                levelThreePrefs.flush();
        }

        /*
        if(levelFourPrefs != null) {
            sendUpdateHttpRequest(Jumpy.HISCORE_SERVER_URL, "/update/" + playerPrefs.getString("username") + "/3/" + levelFourPrefs.getInteger("pointsEarned") + "/" + levelFourPrefs.getInteger("goldEarned") + "/" + levelFourPrefs.getInteger("numberOfStars") + "/" + levelFourPrefs.getInteger("fastestCompletionTime"));
            levelFourPrefs.putBoolean("needsUpdate", false);
            levelFourPrefs.flush();
        }*/

        while(!httpRequestFinished){
            //do nothing
            System.out.println("waiting for updateLevelRankings to finish");
        }
        return;
    }
    //http://hiscores-mas34.apps.devcloud.eecs.qmul.ac.uk/hiscores/update/32796459/3/300/2/3/17
    //http://hiscores-mas34.apps.devcloud.eecs.qmul.ac.uk/hiscores/getPlayerScore/32796459
    //Sends a Http request with the queryString as the parameter
    private void sendUpdateHttpRequest(String url, String queryString){
        httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(url + queryString).content("").build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    httpRequestFinished = true;
                    return;
                } else{
                    httpRequestFinished = true;
                }
            }

            @Override
            public void failed(Throwable t) {
                String status = "failed " + t.getMessage();
                System.out.println(status);
                httpRequestFinished = true;
            }

            @Override
            public void cancelled() {
                httpRequestFinished = true;
                //String status = "failed " + t.getMessage();
                //System.out.println(status);
            }
        });
    }
    String response;
    private void registerNewPlayer(){
        httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(Jumpy.HISCORE_SERVER_URL +"/register/"+playerPrefs.getString("username")).content("").build();
        registeringNewPlayerFinished = false;

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                response = httpResponse.getResultAsString();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    registeringNewPlayerFinished = true;
                    return;
                } else{
                    registeringNewPlayerFinished = true;
                }
            }

            @Override
            public void failed(Throwable t) {
                registeringNewPlayerFinished = true;
            }

            @Override
            public void cancelled() {
                registeringNewPlayerFinished = true;
            }
        });

        while(!registeringNewPlayerFinished){
            //do nothing
            System.out.println("Waiting for registeringNewPlayerFinished");
        }
        return;
    }

    //Sends Http request to retrieve the players scores and populates a hashmap with them.
    private void getPlayerRankings(String playerName){
        //get player scores
        httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(game.HISCORE_SERVER_URL+"/getPlayerScore/"+playerName).content("").build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int statusCode = httpResponse.getStatus().getStatusCode();
                if(statusCode != HttpStatus.SC_OK) {
                    System.out.println("Request Failed");
                    return;
                }
                String status = httpResponse.getResultAsString();
                JsonValue json = new JsonReader().parse(status);
                JsonValue mess = json.get("message");
                //Store player rankings into hash maps
                JsonValue playerInfo = mess.get("player_rankings");

                playerScoreMap = new HashMap<String, String>();
                for (JsonValue info : playerInfo.iterator()){ // iterator() returns a list of children
                    System.out.println(info.isString());
                    String nodeName = info.name;
                    System.out.println(info.asString());
                    if(nodeName.equals("total_players")){
                        playerScoreMap.put("totalPlayers", info.asString());
                    } else if(nodeName.equals("user_name")){
                        playerScoreMap.put("username", info.asString());
                    } else if(nodeName.equals("total_points")){
                        playerScoreMap.put("totalPoints", info.asString());
                    } else if(nodeName.equals("total_gold")){
                        playerScoreMap.put("totalGold", info.asString());
                    } else if(nodeName.equals("total_stars")){
                        playerScoreMap.put("totalStars", info.asString());
                    } else if(nodeName.equals("total_time_played")){
                        playerScoreMap.put("totalTimePlayed", info.asString());
                    }
                }
                levelOneScoreMap = getLevelRankingDetails(mess.get("level_one"));
                levelTwoScoreMap = getLevelRankingDetails(mess.get("level_two"));
                levelThreeScoreMap = getLevelRankingDetails(mess.get("level_three"));
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

    private HashMap<String, String> getLevelRankingDetails(JsonValue levelInfo){
        HashMap<String, String> levelScoreMap = new HashMap<String, String>();
        for (JsonValue info : levelInfo){
            String nodeName = info.name;
            System.out.println(info.asString());
            if(nodeName.equals("completed")){
                levelScoreMap.put("completed", info.asString());
            } else if(nodeName.equals("player_rank")){
                levelScoreMap.put("player_rank", info.asString());
            } else if(nodeName.equals("fastest_time")){
                levelScoreMap.put("fastest_time", info.asString());
            } else if(nodeName.equals("max_points")){
                levelScoreMap.put("max_points", info.asString());
            } else if(nodeName.equals("total_players")){
                levelScoreMap.put("total_players", info.asString());
            }
        }
        return levelScoreMap;
    }

    public void loadSound(){
        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
    }

    public void render() {
        stage.act();
        stage.draw();
    }
}
