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

    private boolean newPersonalBestPoints = false;

    public LevelSummaryScene(Jumpy game, PlayScreen playScreen) {
        this.game = game;
        this.playScreen = playScreen;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));

        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
    }

    public Stage create(int points, int numberOfStars, int goldEarnedAmount){
        loadSound();
        Image levelClearedBackground = new Image(new Texture(Gdx.files.internal("ui/new ui/level_complete_generic.png")));
        Image activeStarSideLeft;
        Image activeStarSideRight;
        Image activeStarTop;
        if(numberOfStars >= 1){
            activeStarSideLeft = new Image(new Texture(Gdx.files.internal("ui/new ui/active_star_side.png")));
        } else{
            activeStarSideLeft = new Image(new Texture(Gdx.files.internal("ui/new ui/inactive_star_top_base.png")));
        }
        if(numberOfStars >= 2){
            activeStarSideRight = new Image(new Texture(Gdx.files.internal("ui/new ui/active_star_side.png")));
        } else{
            activeStarSideRight = new Image(new Texture(Gdx.files.internal("ui/new ui/inactive_star_top_base.png")));
        }
        if(numberOfStars == 3) {
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
        innerInfoTable.add(activeStarSideLeft).colspan(1).center().right();
        innerInfoTable.add(activeStarTop).colspan(1).center().padBottom(30);
        innerInfoTable.add(activeStarSideRight).colspan(1).center().left();
        innerInfoTable.row();

        Label goldEarnedLabel = new Label("Gold:", skin, "medium");
        Label goldEarned = new Label(String.valueOf(goldEarnedAmount), skin, "medium");
        Image goldBox = new Image(new Texture(Gdx.files.internal("ui/new ui/money_base.png")));

        Table goldBoxTable = new Table();
        goldBoxTable.top();
        goldBoxTable.setFillParent(true);
        goldBoxTable.add(goldEarned).center().padTop(15);

        Stack goldBoxStack = new Stack();
        goldBoxStack.add(goldBox);
        goldBoxStack.add(goldBoxTable);

        innerInfoTable.add(goldEarnedLabel).colspan(1).right().padTop(10);
        innerInfoTable.add(goldBoxStack).colspan(2).center();

        Stack stack = new Stack();
        stack.add(backgroundTable);
        stack.add(innerInfoTable);

        innerInfoTable.row();
        Button replayButton  = new Button(skin, "blue_replay");
        innerInfoTable.add(replayButton).colspan(1).center().padTop(20);

        replayButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!game.mute){
                    click.play(Jumpy.volume);
                }
                playScreen.reload();
                game.screenManager.setScreen(ScreenManager.GAME_STATE.PLAY);
                System.out.println("Clicked 44444444444!");
            }
        });

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

        saveLevelDetails(numberOfStars, goldEarnedAmount, points);

        return stage;
    }

    //check if prefs exists
    /*Preferences tmprefs = Gdx.app.getPreferences ( prefname );

    Map tmpmap = tmprefs.get();

      if ( tmpmap.isEmpty() == true )
              return false;
      else
              return true;*/
    private void saveLevelDetails(int numberOfStars, int goldEarned, int points){
        Preferences levelPrefs = Gdx.app.getPreferences("1-3");
        Preferences userPrefs = Gdx.app.getPreferences("userPrefs");

        levelPrefs.putBoolean("completed", true);

        //save total gold/points earned by user
        int userGoldEarned = userPrefs.getInteger("goldEarned");
        int userPointsEarned = userPrefs.getInteger("pointsEarned");
        userPrefs.putInteger("goldEarned", userGoldEarned + goldEarned);
        userPrefs.putInteger("pointsEarned", userPointsEarned + points);

        //current values
        int currentNumberOfStars = levelPrefs.getInteger("numberOfStars", 0);
        int currentGoldEarned = levelPrefs.getInteger("goldEarned", 0);
        int currentPointsEarned = levelPrefs.getInteger("pointsEarned", 0);

        if(numberOfStars > currentNumberOfStars){
            levelPrefs.putInteger("numberOfStars", numberOfStars);
        }
        if(goldEarned > currentGoldEarned){
            levelPrefs.putInteger("goldEarned", goldEarned);
        }
        if(points > currentPointsEarned){
            levelPrefs.putInteger("pointsEarned", points);
            newPersonalBestPoints = true;
        }

        //userPrefs.putInteger("goldEarned", 0);
		//userPrefs.putInteger("pointsEarned", 0);

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