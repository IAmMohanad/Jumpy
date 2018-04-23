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
    private float originalX =0;

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
    private ImageButton attackButton;
    private PlayScreen playScreen;

    private Group pauseGroup;
    private boolean isPause = false;
    private Label pauseLabel;

    Skin skin;

    private Stack pausedStack;

    public int getLevelTimer(){
        return Integer.parseInt(clockLabel.getText().toString().split("s")[0]);
    }

    private void updateClock(float delta){
        if(delta == 0){
            System.out.println("here");
        }
        timeCounter += delta;
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
        this.playScreen = playScreen;
        this.level = level;
        clock = CLOCK_START_VALUE;

        settings = Gdx.app.getPreferences("settings");
        isTouchPad = settings.getBoolean("isTouchPad", true);
        if(Gdx.app.getType() == Application.ApplicationType.Android){
            android = true;
        } else{
            android = false;
        }
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

        life = new Stack();
        life.add(new Image(new Texture(Gdx.files.internal("ui/new ui/heart_resized.png"))));
        Table healthAmountTable = new Table();
        healthAmountTable.top();
        healthAmountTable.setFillParent(true);
        currentLife = new Label(String.valueOf("1"), skin, "small");
        healthAmountTable.add(currentLife).center().padTop(12);
        life.add(healthAmountTable);
        topRowTable.add(life).colspan(1).left().padTop(5).padLeft(5).padRight(15);

        Table coinsCollectedTable = new Table();
        dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        coinsCollectedTable.add(dollarSign).expandX().left();
        coinsCollectedLabel = new Label("0", skin, "small");
        coinsCollectedTable.add(coinsCollectedLabel).expandX().left().padTop(10);
        topRowTable.add(coinsCollectedTable).colspan(1).left().padLeft(15).padRight(15);
        //clock label
        clockLabel = new Label(String.format("%ds", clock), skin, "small");
        topRowTable.add(clockLabel).colspan(1).left().padTop(10).padLeft(15);

        //pause button
        pauseButton = new Image(new Texture("ui/new ui/pause_button_16.png"));
        pauseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(!isPause){
                    pauseGame();
                    if(android){
                        pauseLabel.setText("Press BACK button to exit");
                    } else{
                        pauseLabel.setText("Press B to exit");
                    }
                } else{
                    resumeGame();
                    pauseLabel.setText("");
                }

            }
        });
        topRowTable.add(pauseButton).expandX().right().padRight(30);

        table.add(topRowTable).expandX().left();
        table.row();
        pauseLabel = new Label("", skin, "small");
        if(android){
            table.add(pauseLabel).expandX().padLeft(100).padTop(70);
        } else{
            table.add(pauseLabel).expandX().padTop(70);
        }

        //touch pad
        if(android){
            if(isTouchPad){//set touchpad on screen, otherwise leave the area empty, format holder is to keep the table looking the same without the touchpad being there
                touchpad = new Touchpad(10, skin, "touchPad48");
                table.row();
                table.add(touchpad).expandY().expandX().bottom().left().padLeft(35).padBottom(35);
            } else{
                table.row();
                table.add(new Image(new Texture("ui/new ui/format_holder_48.png"))).expandY().expandX().bottom().left().padLeft(35).padBottom(35);
            }
            Table controlButtonsTable = new Table();
            //jump button
            ImageButton jumpButton = new ImageButton(skin, "cButton40");
            attackButton = new ImageButton(skin, "xButton40");

            ImageButton boostButton = new ImageButton(skin, "zButton40");
            controlButtonsTable.add(boostButton).expandX().left().padRight(20);
            controlButtonsTable.add(attackButton).expandX().padRight(20);
            controlButtonsTable.add(jumpButton).expandX().right();

            table.add(controlButtonsTable).expandY().expandX().bottom().right().padBottom(45).padRight(20);

            jumpButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Player.up = true;
                }
            });
            attackButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Player.shootPressed = true;
                }
            });
            boostButton.addListener(new ClickListener(){
            @Override
                public void clicked(InputEvent event, float x, float y) {
                    Player.boostPressed = true;
                }
            });
        }

        stage.addActor(table);
    }

    public void pauseGame(){
        playScreen.pauseGame();
        isPause = true;
    }

    public void resumeGame(){
        if(isPause){
            playScreen.resumeGame();
            isPause = false;
        }
    }

    private void getPhoneInput(){
        accelX = Gdx.input.getAccelerometerX();
        accelY = Gdx.input.getAccelerometerY();
        accelZ = Gdx.input.getAccelerometerZ();

        if(android){
            if(isTouchPad){
                if(touchpad.getKnobPercentX() > 0){
                    Player.right = true;
                }else if(touchpad.getKnobPercentX() < 0){
                    Player.left = true;
                }
            } else{
                //System.out.println("x - "+accelX+" y - "+accelY+ " z - "+accelZ);
                //delay to test if the phone has been flicked
                timer += Gdx.graphics.getDeltaTime();
                float accelerometerReading = 0;
                for(int i=0; i<10; i++){
                    accelerometerReading += Gdx.input.getAccelerometerX();
                }
                if(timer > 0.125){
                    timer = 0;
                    originalX = accelX;
                }
                float accelXAverage = accelerometerReading / 10;
                if(Math.abs(originalX) - Math.abs(accelXAverage) > 2.5){
                    Player.up = true;
                }

                if(accelY > runThreshold){
                    Player.right = true;
                } else if(accelY < -runThreshold){
                    Player.left = true;
                }
                //accY > 0 tilt right, accY < 0 tilt left.
            }
        }
    }

    public void render(float delta){
        stage.act();
        stage.draw();
        if(level.getIsLevelComplete()){
            if(android){
                attackButton.remove();
            }
            delta = 0;
        }
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
