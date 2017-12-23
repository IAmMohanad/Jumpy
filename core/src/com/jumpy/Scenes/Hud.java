package com.jumpy.Scenes;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    Skin skin;

    private void updateClock(){
        timeCounter += Gdx.graphics.getDeltaTime();
        if(timeCounter >= 1f){
            timeCounter -= 1;
            clock -= 1;
        }
    }

    public int getClockTime(){
        return clock;
    }

    public Hud(SpriteBatch batch, GameMap level){
        //this.player = level.getPlayer();
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

        Table table = new Table();
        table.top().left();//set table position to top of stage
        table.setFillParent(true);//table is size of stage

        //create heart with current life inside it
        life = new Stack();
        life.add(new Image(new Texture(Gdx.files.internal("ui/new ui/heart_resized.png"))));
        Table healthAmountTable = new Table();
        healthAmountTable.top();
        healthAmountTable.setFillParent(true);
        currentLife = new Label(String.valueOf("1"), skin, "small");
        healthAmountTable.add(currentLife).center().padTop(12);
        life.add(healthAmountTable);
        table.add(life).colspan(1).left().padTop(10).padLeft(5);

        dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        table.add(dollarSign).colspan(1).padTop(10).left();
        coinsCollectedLabel = new Label("0", skin, "small");
        table.add(coinsCollectedLabel).colspan(1).left().padTop(20).padLeft(5);
        //scoreLabel = new Label(String.format("%06d", score), skin, "small");//%06d
        //table.add(scoreLabel).colspan(1).left().padTop(20).padLeft(5);//.expandX().left().padTop(20).padLeft(5);

        //touch pad
        if(isTouchPad && android){
            touchpad = new Touchpad(10, skin, "touchPad3");
            table.row();
            table.add(touchpad).expandY().bottom().left().padLeft(35).padBottom(35);

            //jump button
            ImageButton jumpButton = new ImageButton(skin, "jumpButton24");
            table.add(jumpButton).expandY().bottom().right().padBottom(45).padRight(20);

            jumpButton.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Player.up = true;
                }
            });
        }

        stage.addActor(table);
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
                //delay to test if the phone has been flicked
                timer += Gdx.graphics.getDeltaTime();
                if(timer > 0.125){
                    originalZ = accelZ;
                    timer = 0;
                }
                if(accelZ > 1 || Gdx.input.isTouched(0)){
                    if(Gdx.input.isTouched(0) || Math.abs(originalZ) - Math.abs(accelZ) > 2.5){
                        // Player.up = true;
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

    public void render(){
        //scoreLabel.setText(String.format("%06d", score));
        stage.act();
        stage.draw();
        updateClock();
        getPhoneInput();
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
