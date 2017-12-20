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

    public Stage create(int points, int numberOfStars){
        loadSound();
        Texture texture = new Texture(Gdx.files.internal("ui/new ui/level_complete_generic.png"));
        Image levelClearedBackground = new Image(texture);

        Table outerTable = new Table();
        outerTable.top();
        outerTable.setFillParent(true);



        Table table = new Table();
        //table.top();
        //table.setFillParent(true);
        table.add(levelClearedBackground);
        Stack stack = new Stack();

        stack.add(table);

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
