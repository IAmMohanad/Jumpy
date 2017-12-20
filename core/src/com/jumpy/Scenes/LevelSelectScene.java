package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Screens.ScreenManager;
import com.jumpy.World.LevelOne;

public class LevelSelectScene {

    private Jumpy game;

    private Stage stage;
    private Viewport viewport;
    private Skin skin;

    private Sound click;

    public LevelSelectScene(Jumpy game) {
        this.game = game;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));

        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        // Make the stage consume events

        //InputProcessor inputProcessorOne = new CustomInputProcessorOne();
        //InputProcessor inputProcessorTwo = new CustomInputProcessorTwo();
        //InputMultiplexer inputMultiplexer = new InputMultiplexer();
    }

    public Stage create(){
        loadSound();

        Label title = new Label("CHOOSE YOUR LEVEL:", skin, "large");
        Label oneOne = new Label("1-1", skin, "medium");
        Label oneTwo = new Label("1-2", skin, "medium");
        Label oneThree = new Label("1-3", skin, "medium");

        oneOne.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!game.mute){
                    click.play(game.volume);
                }
                System.out.println("Clicked 1-1!");
            }
        });

        oneTwo.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!game.mute){
                    click.play(game.volume);
                    //GameMap level = new LevelOne();
                }
                System.out.println("Clicked 1-2!");
            }
        });

        oneThree.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!game.mute){
                    click.play(game.volume);
                }
                game.setCurrentLevel("1-3");
                //game.setPlay(); //commented out during screenManager changes
                game.screenManager.setScreen(ScreenManager.GAME_STATE.PLAY);
                System.out.println("Clicked 1-3!");
            }
        });

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        table.add(title).padTop(50).colspan(3);//.padTop(15).padLeft(15);
        table.row();
        table.add(oneOne).expand();
        table.add(oneTwo).expand();
        table.add(oneThree).expand();

        stage.addActor(table);
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
