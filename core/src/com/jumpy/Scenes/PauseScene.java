package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Screens.PlayScreen;

public class PauseScene {
    private Jumpy game;
    private PlayScreen playScreen;

    private Stage stage;
    private Viewport viewport;
    private Skin skin;

    private Sound click;

    public PauseScene(Jumpy game, PlayScreen playScreen) {
        this.game = game;
        this.playScreen = playScreen;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));

        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        create();
    }

    public Stage create(){
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
                //resumeGame();
            }
        });

        Stack stack = new Stack();
        stack.add(backgroundTable);
        stack.add(innerInfoTable);


        outerTable.add(stack).center();
        stage.addActor(outerTable);

        return stage;
    }

    //check if prefs exists
    /*Preferences tmprefs = Gdx.app.getPreferences ( prefname );

    Map tmpmap = tmprefs.get();

      if ( tmpmap.isEmpty() == true )
              return false;
      else
              return true;*/

    public void loadSound(){
        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
    }

    public void render(){
        stage.act();
        stage.draw();
    }
}
