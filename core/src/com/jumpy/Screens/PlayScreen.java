package com.jumpy.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Characters.Player;
import com.jumpy.Scenes.Hud;
import com.jumpy.World.GameMap;
import com.jumpy.World.LevelOne;
import com.jumpy.World.TileType;
import com.jumpy.inputController;

public class PlayScreen implements Screen {
    //test multiplexer
    InputMultiplexer inputMultiplexer = new InputMultiplexer();
    inputController inputController;
    //create multiplexer here, make private
    //add multiplexer to inputProcessor in show method.

    private boolean reload = false;
    private Jumpy game;

    private OrthographicCamera camera;
    private GameMap map;
    public Player player;
    private Viewport gamePort;
    private Hud hud;

    private Stage hudStage;
    private String loadedLevel = "";

    private boolean loaded;

    public PlayScreen(Jumpy game){
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 960, 540);//960, 540);//Gdx.graphics.getWidth(), Gdx.graphics.getHeight());//800, 480

        gamePort = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);

        inputController = new inputController();
        //inputMultiplexer.addProcessor(inputController);
        loaded = false;

       // load(game.getCurrentLevel());//commented out during screenManager changes
    }

    public void loadLevel(){
        if(!loadedLevel.equals(game.getCurrentLevel()) || reload) {
            reload = false;
            hud = new Hud(game.batch, map);
            if(game.getCurrentLevel().equals("1-3")) {
                this.loadedLevel = "1-3";
                this.map = new LevelOne(game, hud, this);
                map.load("retro_game_map2.tmx");
                //TODO create inputProcessor in Hud for a jump button at bottom right, return the inputProcessor here and add to multiplexer.
                //TODO add pause button and pause screen / scene
                /*
                pause screen has: 1. resume button. 2.mute/unmute button (maybe volume slider) 3. exit (return to main menu)
                preferably should be stacked on top of  playScreen - change delta to 0?
                 */
            }
            hudStage = hud.getStage();
            inputMultiplexer.addProcessor(hudStage);
            inputMultiplexer.addProcessor(inputController);
            loaded = true;
        }
    }

    public void reload(){
        reload = true;
        loadLevel();
    }

    @Override
    public void show() {
        loadLevel();
        //Gdx.input.setInputProcessor(inputController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);

        map.render(camera, game.batch, Gdx.graphics.getDeltaTime());

        /*if(Gdx.input.isTouched()){
            camera.translate(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
            camera.update();
        }

        if(Gdx.input.isTouched()){
            Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            TileType tile = map.getTileTypeByLocation(0, pos.x, pos.y);
            if(tile != null){
                System.out.println(tile.getId()+"  "+ tile.getName()+"  "+tile.isCollidable());
            }
        }*/


        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.render();
        map.cameraStop(camera);

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
}
