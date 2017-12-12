package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Screens.MainMenuScreen;
import com.jumpy.Screens.ScreenManager;
import com.jumpy.SoundManager;

public class SettingsScene {

    private Jumpy game;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Sound click;
    private Slider volumeSlider;
    private CheckBox touchPadCheckBox;
    private CheckBox accelCheckBox;
    private Label controlTypeLabel;
    private Preferences settings;
    private boolean isTouchPad;

    public SettingsScene(Jumpy game){
        this.game = game;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));
        viewport = new FitViewport(game.V_WIDTH, game.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
    }

    public Stage create(){
        settings = Gdx.app.getPreferences("settings");
        loadSound();

        Table table = new Table();
        table.setFillParent(true);
        table.top();

        Label titleLabel = new Label("SETTINGS", skin, "large");
        TextButton backLabel = new TextButton("BACK", skin);
        backLabel.padTop(5);
        Label volumeLabel = new Label("VOLUME", skin, "large");
        volumeSlider = new Slider(0, 100, 1, false, skin);
        volumeSlider.setValue(settings.getFloat("volume", 1.0f) * 100);

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(volumeSlider.getValue() > 0){
                    game.mute = false;
                } else{
                    game.mute = true;

                }
                game.volume = volumeSlider.getValue() / 100;
                SoundManager.updateBackgroundMusicVolume(game.volume);
                //settings.flush();
            }
        });

        Label controlsLabel = new Label("CONTROLS:", skin, "large");
        controlTypeLabel = new Label("", skin, "large");
        isTouchPad = settings.getBoolean("isTouchPad", true);
        if(isTouchPad){
            controlTypeLabel.setText("PAD");
        } else{
            controlTypeLabel.setText("TILT");
        }

        controlTypeLabel.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Waht the fck " +settings.getBoolean("isTouchPad"));
                if(isTouchPad){
                //if(controlTypeLabel.getText().equals("TOUCH PAD")){
                    controlTypeLabel.setText("TILT");
                    isTouchPad = false;
                    //settings.putBoolean("isTouchPad", false);
                } else{
                    controlTypeLabel.setText("PAD");
                    isTouchPad = true;
                    //settings.putBoolean("isTouchPad", true);
                }
            }
        });

/*
        Label touchPadLabel = new Label("PAD", skin, "large");
        touchPadCheckBox = new CheckBox("", skin, "blue");
        touchPadCheckBox.setChecked(settings.getBoolean("isTouchPad", true));


        Label accelLabel = new Label("TILT", skin, "large");
        accelCheckBox = new CheckBox("", skin, "blue");
        if(touchPadCheckBox.isChecked()){
            accelCheckBox.setChecked(false);
        } else{
            accelCheckBox.setChecked(true);
        }
        //accelCheckBox.setChecked(!settings.getBoolean("isTouchPad", false));
        accelCheckBox.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                touchPadCheckBox.toggle();
                settings.putBoolean("isTouchPad", touchPadCheckBox.isChecked());
            }
        });
        touchPadCheckBox.addListener(new ClickListener() {
            public void clicked (InputEvent event, float x, float y) {
                accelCheckBox.toggle();
                settings.putBoolean("isTouchPad", touchPadCheckBox.isChecked());
            }
        });*/

        backLabel.addListener(new ClickListener(){
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clicked back!"+settings.getBoolean("mute")+" "+settings.getFloat("volume"));
                if(!game.mute){
                    click.play(settings.getFloat("volume", 1.0f));
                }
                settings.putBoolean("mute", game.mute);
                settings.putFloat("volume", volumeSlider.getValue() / 100);
                settings.putBoolean("isTouchPad", isTouchPad);
                settings.flush();
                //game.setMenu();//commented out during screenManager changes
                game.screenManager.setScreen(ScreenManager.STATE.MAIN_MENU);

                //SoundManager.disposeBackgroundMusic();
            }
        });

        table.row();
        table.add(titleLabel).expandX().center().padTop(50).colspan(2);
        table.row();
        table.add(volumeLabel).expandX().padTop(25);
        table.add(volumeSlider).expandX().padTop(25);
        table.row();
        /*table.add(touchPadLabel).width(10).left().padTop(25);
        table.add(touchPadCheckBox).expandX().padTop(25);
        table.add(accelLabel).expandX().padTop(25);
        table.add(accelCheckBox).expandX().padTop(25).padRight(10);*/
        table.add(controlsLabel).expandX().padTop(25).colspan(1);
        table.add(controlTypeLabel).expandX().padTop(25).padRight(10);
        table.row();
        table.add(backLabel).expand().bottom().right().colspan(2).padRight(10).height(20);

        stage.addActor(table);
        return stage;
    }

    public void loadSound(){
        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
    }

    public void render(){
        //if((settings.getFloat("volume") > 0) && settings.getBoolean("soundOn"))
        stage.act();
        stage.draw();
    }
}
