package com.jumpy.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.HiscoreScene;

public class HiscoreScreen implements Screen{

    private Jumpy game;
    private Stage stage;
    private HiscoreScene hiscoreScene;

    public HiscoreScreen(Jumpy game){
        this.game = game;
        hiscoreScene = new HiscoreScene(game);
        stage = hiscoreScene.create();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(115/255f, 205/255f, 75/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        hiscoreScene.render();
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
        stage.clear();
        stage = null;
    }

    @Override
    public void dispose() {
        if(stage != null){
            stage.dispose();
        }
    }
}
