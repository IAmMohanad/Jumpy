package com.jumpy.Scenes;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Characters.Player;
import com.jumpy.Jumpy;
import com.jumpy.Screens.PlayScreen;
import com.jumpy.World.GameMap;

public class Hud{
    private Player player;
    private GameMap level;
    private Preferences settings;
    private float timer = 0;
    private float originalZ =0;

    private double runThreshold = 1.5;
    private boolean isTouchPad;
    float accelX;
    float accelY;
    float accelZ;
    public Stage stage;
    private Viewport viewport;

    private int score;

    private Touchpad touchpad;
    private boolean android;

    private int clock;
    private final int CLOCK_START_VALUE = 300;
    private float timeCounter = 0;

    private Label scoreLabel;
    private Stack life;
    private Label currentLife;
    private Label coinsCollectedLabel;
    private Image dollarSign;
    private Label clockLabel;
    private Image pauseButton;

    private PlayScreen playScreen;

    private Group pauseGroup;
    private boolean isPause = false;

    Skin skin;

    private Stack pausedStack;

    private void updateClock(float delta){
        if(delta == 0){
            System.out.println("here");
        }
        timeCounter += delta; //Gdx.graphics.getDeltaTime();
        if(timeCounter >= 1f){
            timeCounter = 0;
            clock -= 1;
            clockLabel.setText(String.format("%ds", clock));
        }

    }

    public boolean isGamePaused(){
        return playScreen.isGamePaused();
    }

    public int getClockTime(){
        return clock;
    }

    public Hud(SpriteBatch batch, GameMap level, final PlayScreen playScreen){
        //this.player = level.getPlayer();
        this.playScreen = playScreen;
        this.level = level;
        clock = CLOCK_START_VALUE;

        settings = Gdx.app.getPreferences("settings");
        isTouchPad = settings.getBoolean("isTouchPad", true);
        //make this global in jumpy
        if(Gdx.app.getType() == Application.ApplicationType.Android){
            android = true;
        } else{
            android = false;
        }

        //TODO Finish adding score, level label, lives. Add getter/setter methods for relevant labels.

        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));
        score = 0;

        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        Table outerTable = new Table();
        Stack outerStack = new Stack();

        Table table = new Table();
        table.top().left();//set table position to top of stage
        table.setFillParent(true);//table is size of stage


        Table topRowTable = new Table();
        topRowTable.bottom().left();
        topRowTable.setFillParent(true);

        //create heart with current life inside it
        life = new Stack();
        life.add(new Image(new Texture(Gdx.files.internal("ui/new ui/heart_resized.png"))));
        Table healthAmountTable = new Table();
        healthAmountTable.top();
        healthAmountTable.setFillParent(true);
        currentLife = new Label(String.valueOf("1"), skin, "small");
        healthAmountTable.add(currentLife).center().padTop(12);
        life.add(healthAmountTable);
        topRowTable.add(life).colspan(1).left().padTop(5).padLeft(5).padRight(15);

        //create gold earned counter
        Table coinsCollectedTable = new Table();
        dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        coinsCollectedTable.add(dollarSign).expandX().left();
        coinsCollectedLabel = new Label("0", skin, "small");
        coinsCollectedTable.add(coinsCollectedLabel).expandX().left().padTop(10);
        topRowTable.add(coinsCollectedTable).colspan(1).left().padLeft(15).padRight(15);
        //scoreLabel = new Label(String.format("%06d", score), skin, "small");//%06d
        //table.add(scoreLabel).colspan(1).left().padTop(20).padLeft(5);//.expandX().left().padTop(20).padLeft(5);
        //clock label
        clockLabel = new Label(String.format("%ds", clock), skin, "small");
        topRowTable.add(clockLabel).colspan(1).left().padTop(10).padLeft(15);

        //pause button
        pauseButton = new Image(new Texture("ui/new ui/pause_button_16.png"));
        pauseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!isPause){
                    //playScreen.pauseGame();
                    pauseGame();
                } else{
                    //playScreen.resumeGame();
                    resumeGame();
                }

            }
        });
        topRowTable.add(pauseButton).expandX().right().padRight(30);

        table.add(topRowTable).expandX().left();

        //touch pad
       // if(isTouchPad && android){
            touchpad = new Touchpad(10, skin, "touchPad3");
            table.row();
            table.add(touchpad).expandY().expandX().bottom().left().padLeft(35).padBottom(35);

            Table controlButtonsTable = new Table();
            //jump button
            ImageButton jumpButton = new ImageButton(skin, "jumpButton24");
            //table.add(jumpButton).expandY().expandX().bottom().right().padBottom(45).padRight(20);
            ImageButton attackButton = new ImageButton(skin, "attackButton");

            //boost button, bottom of stack is image, top is countdown text
            Stack boostButtonStack = new Stack();
            Image boostImage = new Image(new Texture(Gdx.files.internal("ui/new ui/boost_button_symbol_34x34.png")));
            Label boostDurationLeft = new Label(String.valueOf("10"), skin, "small");//TODO add public method in player to get duration left in boost.
            boostButtonStack.add(boostImage);//TODO lightning image when not clicked, replace with countdown when its clicked?
            boostButtonStack.add(boostDurationLeft);

            controlButtonsTable.add(attackButton).expandX().left().padRight(40);
            controlButtonsTable.add(boostButtonStack).expandX().padRight(40);
            controlButtonsTable.add(jumpButton).expandX().right();

            table.add(controlButtonsTable).expandY().expandX().bottom().right().padBottom(45).padRight(20);

            jumpButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Player.up = true;
                    System.out.println("########## clicked boost button");
                }
            });
            attackButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Player.shootPressed = true;
                    System.out.println("------------------------------------------------------------");
                }
            });
       // }

        stage.addActor(table);
    }

    public void pauseGame(){
        playScreen.pauseGame();
        isPause = true;
        //pauseGroup =  new Group();

       /* Image levelPausedBackground = new Image(new Texture(Gdx.files.internal("ui/new ui/paused_background_102x143.png")));
        Image restartSmallButton = new Image(new Texture(Gdx.files.internal("ui/new ui/blue_button_restart_46x21.png")));
        Table pausedTable = new Table();
        pausedTable.add(restartSmallButton).expandX().center();
        pausedStack.add(levelPausedBackground);
        pausedStack.add(pausedTable);*/

/*
        Image levelPausedBackground = new Image(new Texture(Gdx.files.internal("ui/new ui/paused_background.png")));
        Image invisibleSquare = new Image(new Texture(Gdx.files.internal("ui/new ui/invisible_square_paused_screen.png")));
        Image muteButton;
        Image resumeButton;
        Image restartButton;
        Image exitButton;

        Table outerTable = new Table();
        outerTable.setFillParent(true);
        outerTable.top();

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
        //pauseGroup.addActor(outerTable);
        //pauseGroup.addActor(stack);

        stage.addActor(pauseGroup);*/
    }

    public void resumeGame(){
        if(isPause){
            playScreen.resumeGame();
            isPause = false;
         /*   pausedStack.removeActor(pausedTable);
            pausedStack.removeActor(levelPausedBackground);
            pauseGroup.remove();*/
        }
    }

    private void getPhoneInput(){
        accelX = Gdx.input.getAccelerometerX();
        accelY = Gdx.input.getAccelerometerY();
        accelZ = Gdx.input.getAccelerometerZ();


       // System.out.println("x: "+accelX+"    y: "+accelY+"      z: "+accelZ);
        if(android){
            if(isTouchPad){
                if(touchpad.getKnobPercentX() > 0){
                    Player.right = true;
                }else if(touchpad.getKnobPercentX() < 0){
                    Player.left = true;
                }
            } else{
                //delay to test if the phone has been flicked
                timer += Gdx.graphics.getDeltaTime();
                if(timer > 0.125){
                    originalZ = accelZ;
                    timer = 0;
                }
                if(accelZ > 1 || Gdx.input.isTouched(0)){
                    if(Gdx.input.isTouched(0) || Math.abs(originalZ) - Math.abs(accelZ) > 2.5){
                         Player.up = true;
                    }
                }

                if(accelY > runThreshold){
                    Player.right = true;
                } else if(accelY < -runThreshold){
                    Player.left = true;
                }
                //accY > 0 tilt right, accY < 0 tilt left. use 3.5 as trigger
            }
        }
    }

    public void render(float delta){
        //scoreLabel.setText(String.format("%06d", score));
        stage.act();
        stage.draw();
        updateClock(delta);
        getPhoneInput();
    }

    public void addToStage(Actor actor){
        stage.addActor(actor);
    }

    public Stage getStage(){
        return this.stage;
    }

    public void setScore(int score){
        this.score = score;
        scoreLabel.setText(String.format("%06d", score));
    }

    public void setCoinsCollected(int coins){
        coinsCollectedLabel.setText(String.valueOf(coins));
    }

    public void addScore(int score){
        this.score += score;
    }

    public void setLife(int life){
        currentLife.setText(String.valueOf(life));
    }

    public void dispose(){
        this.skin.dispose();
        this.stage.dispose();
    }

}
