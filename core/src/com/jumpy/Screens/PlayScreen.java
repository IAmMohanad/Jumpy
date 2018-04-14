package com.jumpy.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Characters.Player;
import com.jumpy.Scenes.Hud;
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
    public Player player;
    private Viewport gamePort;
    private Hud hud;

    private Stage hudStage;
    private String loadedLevel = "";

    private boolean loadComplete = false;
    private String mapLocation;


    private boolean isPause = false;
    public PlayScreen(Jumpy game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 960, 540);//960, 540);//Gdx.graphics.getWidth(), Gdx.graphics.getHeight());//800, 480

        gamePort = new FitViewport(game.V_WIDTH, game.V_HEIGHT, camera);

        inputController = new inputController();
        //inputMultiplexer.addProcessor(inputController);
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
            mapLocation = "retro_game_map3_collidable_objects.tmx";
        }
        //if(!loadedLevel.equals(game.getCurrentLevel()) || reload) {
            reload = false;
            if(game.getCurrentLevel().equals("1-3") || game.getCurrentLevel().equals("1-2") || game.getCurrentLevel().equals("1-1")) {
                this.loadedLevel = "1-3";
                this.map = new Level(game,/* hud,*/ this);
                map.load(mapLocation);//"retro_game_map3_collidable_objects.tmx");
                //TODO create inputProcessor in Hud for a jump button at bottom right, return the inputProcessor here and add to multiplexer.
                //TODO add pause button and pause screen / scene
                /*
                pause screen has: 1. resume button. 2.mute/unmute button (maybe volume slider) 3. exit (return to main menu)
                preferably should be stacked on top of  playScreen - change delta to 0?
                 */
         //   }
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
        /*pauseGroup =  new Group();

        Image levelPausedBackground = new Image(new Texture(Gdx.files.internal("ui/new ui/paused_background.png")));
        Image invisibleSquare = new Image(new Texture(Gdx.files.internal("ui/new ui/invisible_square_paused_screen.png")));
        Image muteButton;
        Image resumeButton;
        Image restartButton;
        Image exitButton;

        Table outerTable = new Table();
        outerTable.top();
        outerTable.setFillParent(true);

        //add backgrounds
        Table backgroundTable = new Table();
        backgroundTable.add(levelPausedBackground);

        //add buttons
        Table innerInfoTable = new Table();
        innerInfoTable.top();
        innerInfoTable.setFillParent(true);
        innerInfoTable.row();
        innerInfoTable.add(invisibleSquare).left().colspan(3);

        innerInfoTable.row();
        if(Jumpy.mute){
            muteButton = new Image(new Texture("ui/new ui/volume_off_32x32.png"));
        } else{
            muteButton = new Image(new Texture("ui/new ui/volume_on_32x32.png"));
        }
        innerInfoTable.add(muteButton).expandX().left().padTop(20);
        innerInfoTable.row();

        resumeButton = new Image(new Texture("ui/new ui/blue_button_resume_92x42.png"));
        innerInfoTable.add(resumeButton).expandX().center().padTop(20);
        resumeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resumeGame();
            }
        });

        Stack stack = new Stack();
        stack.add(backgroundTable);
        stack.add(innerInfoTable);


        outerTable.add(stack).center();
        pauseGroup.addActor(outerTable);

        hud.stage.addActor(pauseGroup);*/
    }

    public void resumeGame(){
        if(isPause){
            isPause = false;
           // pauseGroup.remove();
        }
    }
}
