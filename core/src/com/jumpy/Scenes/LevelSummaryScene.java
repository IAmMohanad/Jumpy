package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
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
import com.jumpy.Screens.ScreenManager;


public class LevelSummaryScene {

    private Jumpy game;

    private Stage stage;
    private Viewport viewport;
    private Skin skin;

    private Sound click;

    public LevelSummaryScene() {
        //this.game = game;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));

        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        // Make the stage consume events

        //InputProcessor inputProcessorOne = new CustomInputProcessorOne();
        //InputProcessor inputProcessorTwo = new CustomInputProcessorTwo();
        //InputMultiplexer inputMultiplexer = new InputMultiplexer();
    }

    public Stage create(int points, int numberOfStars, int goldEarnedAmount){
        loadSound();
        Image levelClearedBackground = new Image(new Texture(Gdx.files.internal("ui/new ui/level_complete_generic.png")));
        Image activeStarTop = new Image(new Texture(Gdx.files.internal("ui/new ui/active_star_top.png")));
        Image activeStarSideLeft = new Image(new Texture(Gdx.files.internal("ui/new ui/active_star_side.png")));
        Image activeStarSideRight = new Image(new Texture(Gdx.files.internal("ui/new ui/active_star_side.png")));
        Image square = new Image(new Texture(Gdx.files.internal("ui/new ui/invisible_square_21060_no_opacity.png")));
        Image moneyBase = new Image(new Texture(Gdx.files.internal("ui/new ui/money_base.png")));



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

        Button continueButton  = new Button(skin, "blue_continue");
        innerInfoTable.add(continueButton).colspan(1).center().padTop(20);


        outerTable.add(stack).center();

        stage.addActor(outerTable);
        return stage;
    }

    public void loadSound(){
        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
    }

    public void render(){
        stage.act();
        stage.draw();
    }
}