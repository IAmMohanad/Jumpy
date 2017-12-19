package com.jumpy.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.MainMenuScene;
import com.jumpy.SoundManager;

public class MainMenuScreen implements Screen {

    private final Jumpy game;
    private MainMenuScene mainMenu;
    private Stage stage;

    public MainMenuScreen(Jumpy game){
        this.game = game;
        mainMenu = new MainMenuScene(game);
        stage = mainMenu.create();
    }

    @Override
    public void show() {
        System.out.println(SoundManager.backgroundMusic.getVolume()+"   isPLay? "+SoundManager.backgroundMusic.isPlaying());
        SoundManager.playBackgroundMusic();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(255/255f, 204/255f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mainMenu.render();
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
