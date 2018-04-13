package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Screens.PlayScreen;
import com.jumpy.Screens.ScreenManager;

public class LevelSummaryScene {

    private Jumpy game;
    private PlayScreen playScreen;

    private Stage stage;
    private Viewport viewport;
    private Skin skin;

    private Sound click;

    private int totalScoreEarned;
    private int totalNumberOfStarsEarned;
    private boolean newPersonalBest = false;

    public LevelSummaryScene(Jumpy game, PlayScreen playScreen) {
        this.game = game;
        this.playScreen = playScreen;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));

        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
    }

    public int getHighScore(){
        Preferences levelPrefs = Gdx.app.getPreferences("1-3");
        return levelPrefs.getInteger("pointsEarned", 0);
    }

    private int calculateTotalScoreEarned(int coinsCollected, int enemiesKilled, int timePlayed){
        totalScoreEarned = ((coinsCollected * 50) + (enemiesKilled * 100)) * Math.max(1, (timePlayed / 10));

        if(totalScoreEarned == 0) totalScoreEarned = 1;
        return totalScoreEarned;
    }

    private int calculcateNumberOfStars(){
        totalNumberOfStarsEarned = 0;
        if(totalScoreEarned > 50) totalNumberOfStarsEarned++;
        if(totalScoreEarned > 100) totalNumberOfStarsEarned++;
        if(totalScoreEarned > 149) totalNumberOfStarsEarned++;
        return totalNumberOfStarsEarned;
    }

    //player.getCoinsCollected(), player.getEnemiesKilled(), ((300 - hud.getLevelTimer()) / 10))
    public Stage create(int coinsCollected, int enemiesKilled, int timeLeft){//(int points, int numberOfStars, int goldEarnedAmount){
        loadSound();
        int timePlayed = 300 - timeLeft;
        calculateTotalScoreEarned(coinsCollected, enemiesKilled, timePlayed);
        calculcateNumberOfStars();
        saveLevelDetails(coinsCollected, enemiesKilled, timePlayed, totalScoreEarned, totalNumberOfStarsEarned);
        Image levelClearedBackground = new Image(new Texture(Gdx.files.internal("ui/new ui/level_complete_generic.png")));
        Image activeStarSideLeft;
        Image activeStarSideRight;
        Image activeStarTop;
        if(totalNumberOfStarsEarned >= 1){
            activeStarSideLeft = new Image(new Texture(Gdx.files.internal("ui/new ui/active_star_side.png")));
        } else{
            activeStarSideLeft = new Image(new Texture(Gdx.files.internal("ui/new ui/inactive_star_top_base.png")));
        }
        if(totalNumberOfStarsEarned >= 2){
            activeStarSideRight = new Image(new Texture(Gdx.files.internal("ui/new ui/active_star_side.png")));
        } else{
            activeStarSideRight = new Image(new Texture(Gdx.files.internal("ui/new ui/inactive_star_top_base.png")));
        }
        if(totalNumberOfStarsEarned == 3) {
            activeStarTop = new Image(new Texture(Gdx.files.internal("ui/new ui/active_star_top.png")));
        } else {
            activeStarTop = new Image(new Texture(Gdx.files.internal("ui/new ui/inactive_star_top_base.png")));//inactive_star_top_base
        }

        Image square = new Image(new Texture(Gdx.files.internal("ui/new ui/invisible_square_21060_no_opacity.png")));
        Table outerTable = new Table();
        outerTable.top();
        outerTable.setFillParent(true);

        Table backgroundTable = new Table();
        backgroundTable.add(levelClearedBackground);

        Table innerInfoTable = new Table();
        innerInfoTable.top();
        innerInfoTable.setFillParent(true);
        innerInfoTable.row();
        innerInfoTable.add(square).left().colspan(3);
        innerInfoTable.row();
        innerInfoTable.add(activeStarSideLeft).colspan(1).center().right().padRight(10);
        innerInfoTable.add(activeStarTop).colspan(1).center().padBottom(30).padRight(10);
        innerInfoTable.add(activeStarSideRight).colspan(1).center().left().padRight(10);
        innerInfoTable.row();

        Label goldEarnedLabel = new Label("Gold:", skin, "medium");
        Label goldEarned = new Label(String.valueOf(coinsCollected), skin, "medium");
        Image goldBox = new Image(new Texture(Gdx.files.internal("ui/new ui/money_base.png")));

        if(newPersonalBest){
            Label newPersonalBestLabel = new Label("New High Score!", skin, "small");
            innerInfoTable.add(newPersonalBestLabel).colspan(3).center();
            innerInfoTable.row();

            Label newPersonalBestValue = new Label(String.valueOf(getHighScore()), skin, "small");
            innerInfoTable.add(newPersonalBestValue).colspan(3).center().padTop(10);
            innerInfoTable.row();
            //TODO add new ranking here
            //TODO think about changing the layout so that gold earned isnt shown, only the current score, and best score. gold earned can be seen in-game + in shop
            //TODO check mobile game GUI asset file for magnet, shield and multiplier upgrade assets.
        } else{
            Table goldBoxTable = new Table();
            goldBoxTable.top();
            goldBoxTable.setFillParent(true);
            goldBoxTable.add(goldEarned).center().padTop(15);

            Stack goldBoxStack = new Stack();
            goldBoxStack.add(goldBox);
            goldBoxStack.add(goldBoxTable);

            innerInfoTable.add(goldEarnedLabel).colspan(1).right().padTop(10);
            innerInfoTable.add(goldBoxStack).colspan(2).center();
        }

        Stack stack = new Stack();
        stack.add(backgroundTable);
        stack.add(innerInfoTable);

        innerInfoTable.row();
        /*Button replayButton  = new Button(skin, "blue_replay");
        innerInfoTable.add(replayButton).colspan(1).center().padTop(20);

        replayButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!game.mute){
                    click.play(Jumpy.volume);
                }
                //playScreen.reload();
                game.screenManager.setScreen(ScreenManager.GAME_STATE.LEVEL_SELECT);
                System.out.println("Clicked 44444444444!");
            }
        });*/

        Button continueButton  = new Button(skin, "blue_continue");
        innerInfoTable.add(continueButton).colspan(1).center().padTop(20);

        continueButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!game.mute){
                    click.play(Jumpy.volume);
                }
                game.setCurrentLevel("0");
                game.screenManager.setScreen(ScreenManager.GAME_STATE.LEVEL_SELECT);
                System.out.println("Clicked 1-3333333333333333!");
            }
        });

        outerTable.add(stack).center();

        stage.addActor(outerTable);

        return stage;
    }

    private void saveLevelDetails(int coinsCollected, int enemiesKilled, int timePlayed, int totalScore, int totalStars){//int numberOfStars, int goldEarned, int points){
        Preferences levelPrefs = Gdx.app.getPreferences(game.getCurrentLevel());//"1-3");
        Preferences userPrefs = Gdx.app.getPreferences("userPrefs");

        levelPrefs.putBoolean("completed", true);

        //save user statistics
        //update lifetime stats
        userPrefs.putInteger("lifetimeGoldEarned", userPrefs.getInteger("goldEarned", 0) + coinsCollected);
        userPrefs.putInteger("lifeTimepointsEarned", userPrefs.getInteger("lifeTimepointsEarned", 0) + totalScore);
        userPrefs.putInteger("lifeTimeTimePlayed", userPrefs.getInteger("lifeTimeTimePlayed", 0) + timePlayed);
        userPrefs.putInteger("lifeTimeEnemiesKilled", userPrefs.getInteger("lifeTimeEnemiesKilled", 0) + enemiesKilled);
        //update current # of gold
        userPrefs.putInteger("goldEarned", coinsCollected);

        //save level stats
        int highestGoldEarned = levelPrefs.getInteger("goldEarned", 0);
        int highestPointsEarned = levelPrefs.getInteger("pointsEarned", 0);
        int highestNumberOfStars = levelPrefs.getInteger("numberOfStars", 0);
        int highestNumberOfEnemiesKilled = levelPrefs.getInteger("enemiesKilled", 0);
        int fastestCompletionTime = levelPrefs.getInteger("fastestCompletionTime", 0);
        /*if(coinsCollected > highestGoldEarned || enemiesKilled > highestNumberOfEnemiesKilled || totalScore > highestPointsEarned || totalStars > highestNumberOfStars || timePlayed < fastestCompletionTime){
            if(fastestCompletionTime != 0){
                newPersonalBest = true;
            }
        }*/

        if(coinsCollected > highestGoldEarned){
            newPersonalBest = true;
            levelPrefs.putInteger("goldEarned", coinsCollected);
        }
        if(enemiesKilled > highestNumberOfEnemiesKilled){
            newPersonalBest = true;
            levelPrefs.putInteger("enemiesKilled", enemiesKilled);
        }
        if(totalScore > highestPointsEarned){
            newPersonalBest = true;
            levelPrefs.putInteger("pointsEarned", totalScore);
        }
        if(totalStars > highestNumberOfStars){
            newPersonalBest = true;
            levelPrefs.putInteger("numberOfStars", totalStars);
        }
        if(timePlayed < fastestCompletionTime && fastestCompletionTime != 0){
            newPersonalBest = true;
            levelPrefs.putInteger("fastestCompletionTime", timePlayed);
        }
        userPrefs.flush();
        levelPrefs.flush();
    }

    public void loadSound(){
        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
    }

    public void render(){
        stage.act();
        stage.draw();
    }
}