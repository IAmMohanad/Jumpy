package com.jumpy.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Characters.Player;
import com.jumpy.Scenes.Hud;
import com.jumpy.Scenes.PauseScene;
import com.jumpy.World.GameMap;
import com.jumpy.World.Level;
import com.jumpy.inputController;

public class PlayScreen implements Screen {
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    inputController inputController;
    private boolean firstStart = true;
    private float firstStartTimer = 0f;

    private boolean reload = false;
    private Jumpy game;

    private OrthographicCamera camera;
    private GameMap map;
    private Viewport gamePort;
    private Hud hud;
    private PauseScene pauseScene;
    private Stage pauseSceneStage;

    private Stage hudStage;
    private String loadedLevel = "";

    private boolean loadComplete = false;
    private String mapLocation;

    private boolean isPause = false;
    public PlayScreen(Jumpy game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 960, 540);

        gamePort = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);

        inputController = new inputController();
    }

    public void loadLevel(){
        if(game.getCurrentLevel().equals("1-1")){
            if(Gdx.app.getType() == Application.ApplicationType.Desktop){
                mapLocation = "maps/tutorialMap/tutorialMapDesktop.tmx";
            } else{
                mapLocation = "maps/tutorialMap/tutorialMapAndroid.tmx";
            }
        } else if(game.getCurrentLevel().equals("1-2")){
            mapLocation = "maps/completed_map/level_2/level_two_complete.tmx";
        } else if(game.getCurrentLevel().equals("1-3")){
            mapLocation = "maps/completed_map/level_4/level_4-old.tmx";
        }
        reload = false;
        if(game.getCurrentLevel().equals("1-3") || game.getCurrentLevel().equals("1-2") || game.getCurrentLevel().equals("1-1")) {
            this.map = new Level(game,this);
            map.load(mapLocation);
            hud = new Hud(game.batch, map, this);
            hudStage = hud.getStage();
            map.setHud(hud);

            inputMultiplexer.addProcessor(hudStage);
            inputMultiplexer.addProcessor(inputController);

            loadComplete = true;
        }
    }

    public void reload(){
        reload = true;
        loadComplete = false;
        loadLevel();
    }

    @Override
    public void show() {
        loadLevel();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        if(loadComplete){
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            game.batch.setProjectionMatrix(camera.combined);

            if(firstStart && firstStartTimer < 1){
                firstStartTimer += Gdx.graphics.getDeltaTime();
                if(firstStartTimer > 1){
                    firstStart = false;
                }
            } else{
                if(delta > 1.2) delta = 1.2f;
                if(isGamePaused()){
                    delta = 0f;
                    //pauseScene.render();
                }

                if(game.exitPressed){
                    game.exitPressed = false;
                    if(isGamePaused()){
                        game.screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
                    }
                }

                map.render(camera, game.batch, delta);
                game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
                hud.render(delta);
                map.cameraStop(camera);
            }

        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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
        //game.dispose();
        //map.dispose();
        //player.dispose();
    }

    public boolean isGamePaused(){
        return isPause;
    }

    public void pauseGame(){
        isPause = true;
    }

    public void resumeGame(){
        if(isPause){
            isPause = false;
        }
    }
}
