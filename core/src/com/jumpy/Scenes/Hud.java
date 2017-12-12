package com.jumpy.Scenes;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Characters.Player;
import com.jumpy.Jumpy;
import com.jumpy.World.GameMap;

public class Hud{
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

    private int worldTimer;
    private float timeCount;
    private int score;

    private Touchpad touchpad;
    private boolean android;

    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;

    Skin skin;

    public Hud(SpriteBatch batch, GameMap level){
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
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        Table table = new Table();
        table.top();//set table position to top of stage
        table.setFillParent(true);//table is size of strange

        //countdownLabel =;
        scoreLabel = new Label(String.format("%06d", score), skin, "small");//%06d

        //timeLabel =;
        //levelLabel =;
        //worldLabel = new Label(level.getLevel(), skin);
        //playerLabel =;

        table.add(scoreLabel).expandX().left().padTop(20).padLeft(5);
        //table.add(worldLabel).expandX();

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

    public void render(){
        scoreLabel.setText(String.format("%06d", score));
        stage.act();
        stage.draw();
       // System.out.println("+++"+touchpad.getKnobPercentX());
        accelX = Gdx.input.getAccelerometerX();
        accelY = Gdx.input.getAccelerometerY();
        accelZ = Gdx.input.getAccelerometerZ();
      //  System.out.println("accelX: "+accelX+" accelY: "+accelY+" accelZ: "+accelZ);
        if(android){
            if(isTouchPad){
                System.out.println("1");
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
            /*Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    // Do your work
                }
            }, 0.125f);*/

                if(accelY > runThreshold){
                    Player.right = true;
                } else if(accelY < -runThreshold){
                    Player.left = true;
                }
                //accY > 0 tilt right, accY < 0 tilt left. use 3.5 as trigger
            }
        }

    }

    public Stage getStage(){
        return this.stage;
    }

    public void setScore(int score){
        this.score = score;
    }

    public void addScore(int score){
        this.score += score;
    }

    public void dispose(){
        this.skin.dispose();
        this.stage.dispose();
    }

}
