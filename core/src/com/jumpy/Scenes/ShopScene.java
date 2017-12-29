package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;

public class ShopScene {

    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Sound click;

    public ShopScene(){
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));
        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
    }

    public Stage create(){
        /**
        *TABLE DESIGN, LEFT COLUMN HAS SCROLL-PANE, RIGHT COLUMN HAS ITEM INFORMATION LABEL.
         * IF
        * */
        loadSound();

        //create outer table
        Table outerTable = new Table();
        outerTable.setFillParent(true);
        outerTable.top();

        //create the table that goes inside the scroll pane
        Table scrollPaneTable = new Table();
        scrollPaneTable.setFillParent(true);
        scrollPaneTable.top();

        //create scroll pane
        ScrollPane upgradesPane = new ScrollPane(scrollPaneTable, skin);
        upgradesPane.setBounds(outerTable.getX(),outerTable.getY(),outerTable.getWidth() / 2, outerTable.getHeight());

        //RIGHT SIDE information table.
        Table informationTable = new Table();
        informationTable.setFillParent(true);
        informationTable.top();

        //add label to the information table
        Label informationDescription = new Label("Choose a item from the left to see the description!", skin);
        informationDescription.setWrap(true);
        informationDescription.setWidth(informationTable.getWidth());//maybe remove this if any issues with compiling...
        informationTable.add(informationDescription).top().left().expandX();

        //add both left and right sides to outertable, one in each column
        outerTable.add(upgradesPane).expandX();
        outerTable.add(informationTable).expandX();

        return stage;
    }

    public void render(){
        //if((settings.getFloat("volume") > 0) && settings.getBoolean("soundOn"))
        stage.act();
        stage.draw();
    }

    public void loadSound(){
        click = Gdx.audio.newSound(Gdx.files.internal("ui/sounds/click1.ogg"));
    }
}
