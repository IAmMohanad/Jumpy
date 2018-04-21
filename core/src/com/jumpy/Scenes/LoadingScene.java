package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Screens.ScreenManager;
import com.jumpy.SoundManager;

public class LoadingScene {

    private Stage stage;
    private Jumpy game;
    private Viewport viewport;

    private Skin loadingSkin;

    private Sound click;
    private Music backgroundMusic;

    private Preferences settings;
    private ProgressBar progressBar;


    public LoadingScene(Jumpy game){
        this.game = game;

        loadingSkin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        settings = Gdx.app.getPreferences("settings");
    }

    public Stage create(){
        loadSound();

        Table table = new Table();
        table.setFillParent(true);
        progressBar = new ProgressBar(0, 1, 1, false, loadingSkin, "default-horizontal");
        table.add(progressBar).fillX().fillY().center();
        stage.addActor(table);

        return stage;
    }

    public void loadSound(){

        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
    }

    public void render() {
        stage.act();
        stage.draw();
    }

    public void updateProgressBar(float progress){
        progressBar.setValue(progress);
    }


}
