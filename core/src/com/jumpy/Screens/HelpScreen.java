package com.jumpy.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.HelpScene;
import com.jumpy.Scenes.LevelSelectScene;
import com.jumpy.Scenes.MainMenuScene;

public class HelpScreen implements Screen {

    private Jumpy game;
    private HelpScene help;
    Stage stage;

    public HelpScreen(Jumpy game){
        this.game = game;
        help = new HelpScene(game);
        stage = help.create();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(115/255f, 205/255f, 75/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        help.render();
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
