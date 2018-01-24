package com.jumpy.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.LoadingScene;
import com.jumpy.Scenes.MainMenuScene;


public class LoadingScreen implements Screen {

    private Jumpy game;

    private LoadingScene loadingScene;
    private Stage stage;
    private AssetManager assetManager;
    private ScreenManager screenManager;

    public LoadingScreen(Jumpy game, ScreenManager screenManager){
        this.game = game;
        this.screenManager = screenManager;

        loadingScene = new LoadingScene(game);
        stage = loadingScene.create();
        assetManager = game.assetManager;
        load();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(assetManager.update()) {
            // we are done loading, let's move to another screen!
            screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
        }

        // display loading information
        float progress = assetManager.getProgress();
        loadingScene.updateProgressBar(progress);
        loadingScene.render();
        // ... left to the reader ...
    }

    public void load() {
        assetManager.load("characters/player/weapon/fire_bolt.png", Texture.class);
        assetManager.load("chaser_updated.png", Texture.class);
        assetManager.load("ui/skin/main_menu.json", Skin.class);
        assetManager.load("coin_animation/coin_animation.png", Texture.class);
    }

    public AssetManager getAssetManager(){
        return assetManager;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        assetManager.dispose();
        stage.dispose();
    }
}
