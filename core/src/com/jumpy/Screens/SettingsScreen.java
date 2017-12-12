package com.jumpy.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.SettingsScene;

public class SettingsScreen implements Screen {

    private Jumpy game;
    private SettingsScene settings;
    private Stage stage;

    public SettingsScreen(Jumpy game){
        this.game = game;
        settings = new SettingsScene(game);
        stage = settings.create();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255/255f, 204/255f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        settings.render();
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

    }
}
