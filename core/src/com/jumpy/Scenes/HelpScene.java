package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Screens.ScreenManager;

public class HelpScene {

    private Jumpy game;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;

    private Sound click;
    private boolean isHardMode;

    public HelpScene(Jumpy game) {
        this.game = game;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));

        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        isHardMode = false;
    }

    public Stage create(){
        //loadSound();
        // Jumpy.soundManager.playMusic(Jumpy.screenManager.getGameState());
        Label title = new Label("INSTRUCTIONS:", skin, "large");
        Label welcomeLabel = new Label("Welcome to SunnyLand!", skin, "medium");
        Label backButton = new Label("BACK", skin, "medium");

        Table outerTable = new Table();
        outerTable.setFillParent(true);
        outerTable.top();

        outerTable.row();
        outerTable.add(title).expandX().padTop(30);
        outerTable.row();
        outerTable.add(welcomeLabel).left().expandX().padTop(10).padLeft(5);
        outerTable.row();

        Table innerTable = new Table();
        Label jumpLabel = new Label("Jump", skin, "medium");
        Label jumpButtonLabel = new Label("X", skin, "medium");
        Label attackLabel = new Label("Attack (When bought)", skin, "medium");
        Label attackButtonLabel = new Label("C", skin, "medium");
        Label boostLabel = new Label("Boost (When bought)", skin, "medium");
        Label boostButtonLabel = new Label("Z", skin, "medium");
        innerTable.add(jumpLabel).left().expandX().padTop(15);
        innerTable.add(jumpButtonLabel).left().expandX().padTop(15);
        innerTable.row();
        innerTable.add(attackLabel).left().expandX().padTop(10);
        innerTable.add(attackButtonLabel).left().expandX().padTop(10);
        innerTable.row();
        innerTable.add(boostLabel).left().expandX().padTop(10);
        innerTable.add(boostButtonLabel).left().expandX().padTop(10);
        innerTable.row();
        innerTable.add(new Label("", skin, "medium"));//empty line for formatting
        innerTable.row();
        String description = "The game is simple. You will start the game in one of several entry ways marked with a red diamond, you must find your way to one of several different exit points marked with a green diamond. At the same time you must avoid all the creatures on the way, once you purchase a weapon, you can defeat them for bonus points. Take as many gold coins with you as you can, but keep an eye on the time! Once you earn enough gold, you can buy a teleporter to find your way back home!";
        Label descriptionLabel = new Label(description, skin, "medium");
        descriptionLabel.setWrap(true);
        descriptionLabel.setWidth(450);
        innerTable.add(descriptionLabel).width(450f).padTop(10).colspan(2);

        ScrollPane instructionsScrollPane = new ScrollPane(innerTable, skin, "default-no-slider");
        outerTable.add(instructionsScrollPane).height(150).padTop(10).expandX();
        //outerTable.add(innerTable).expandX().padTop(50);
        outerTable.row();

        outerTable.add(backButton).bottom().right().padTop(20).padRight(10);

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
            }
        });

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
