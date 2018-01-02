package com.jumpy.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.ShopScene;

public class ShopScreen implements Screen {

    private ShopScene shopScene;
    private Stage stage;

    public ShopScreen(){
        shopScene = new ShopScene(this);
        stage = shopScene.create();
    }

    @Override
    public void show() {
        System.out.println("ENTERED SHOP");
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(50/255f, 204/255f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shopScene.render();
    }

    public void reloadScene(){
        stage.dispose();
        shopScene = null;
        stage = null;
        shopScene = new ShopScene(this);
        stage = shopScene.create();
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
        stage.dispose();
    }
}
