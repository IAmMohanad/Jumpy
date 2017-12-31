package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Boost;
import com.jumpy.Jumpy;
import com.jumpy.Passive;
import com.jumpy.Weapon;

public class ShopScene {

    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Sound click;
    private Preferences upgradePrefs;

    public ShopScene(){
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));
        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        upgradePrefs = Gdx.app.getPreferences("upgradePrefs");
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
        //scrollPaneTable.setFillParent(true);
        scrollPaneTable.top();

        //create scroll pane
        scrollPaneTable.add(new Label("Passive Items", skin, "skin-normal")).expandX();
        scrollPaneTable.row();

        String upgradeName = "Anti-Gravity";
        boolean upgradeUnlocked = upgradePrefs.getBoolean(Passive.ANTI_GRAVITY.name()+"Unlocked", false);
        String upgradeDescription = upgradePrefs.getString(Passive.ANTI_GRAVITY.name()+"Description", "LEL");
        int upgradeLevel = upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level", -1);
        int upgradeCost = upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level-"+String.valueOf(upgradeLevel)+"-price", -1);
        System.out.println(Passive.ANTI_GRAVITY.name()+"Level-"+upgradeLevel+"-price");

        scrollPaneTable.add(new Label(upgradeName, skin, "skin-normal")).expandX();
        scrollPaneTable.add(new Label(String.valueOf(upgradeLevel), skin, "skin-normal")).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(new Label("Info", skin, "skin-normal")).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(new Label("Cost:" + String.valueOf(upgradeCost), skin, "skin-normal")).expandX();
        if(upgradeLevel > 0){
            scrollPaneTable.add(new Label("Upgrade", skin, "skin-normal")).expandX();
        } else{
            scrollPaneTable.add(new Label("Buy", skin, "skin-normal")).expandX();
        }
       // if(upgradeUnlocked){
            scrollPaneTable.row();
            scrollPaneTable.add(new Label("Equip", skin, "skin-normal")).expandX();
       // }
        scrollPaneTable.row();

        scrollPaneTable.add(new Label(upgradeName, skin, "skin-normal")).expandX();
        scrollPaneTable.add(new Label(String.valueOf(upgradeLevel), skin, "skin-normal")).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(new Label("Info2", skin, "skin-normal")).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(new Label("Cost2:" + String.valueOf(upgradeCost), skin, "skin-normal")).expandX();
        if(upgradeLevel > 0){
            scrollPaneTable.add(new Label("Upgrade2", skin, "skin-normal")).expandX();
        } else{
            scrollPaneTable.add(new Label("Buy2", skin, "skin-normal")).expandX();
        }
        // if(upgradeUnlocked){
        scrollPaneTable.row();
        scrollPaneTable.add(new Label("Equip2", skin, "skin-normal")).expandX();


        // }



        ScrollPane upgradesPane = new ScrollPane(scrollPaneTable, skin, "default-no-slider");
        //upgradesPane.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 4);

/*
        //populateScrollPane();
        //createUpgradeBox(upgradeName, upgradeLevel, upgradeCost, upgradeUnlocked);

        upgradeName = "Speed";
        upgradeUnlocked = upgradePrefs.getBoolean(Passive.SPEED.name()+"Unlocked", false);
        upgradeDescription = upgradePrefs.getString(Passive.SPEED.name()+"Description", "LEL");
        upgradeLevel = upgradePrefs.getInteger(Passive.SPEED.name()+"Level", -1);
        upgradeCost = upgradePrefs.getInteger(Passive.SPEED.name()+"Level-"+upgradeLevel+"-price", -1);
        createUpgradeBox(upgradeName, upgradeLevel, upgradeCost, upgradeUnlocked);

        upgradeName = "Laser";
        upgradeUnlocked = upgradePrefs.getBoolean(Weapon.LASER.name()+"Unlocked", false);
        upgradeDescription = upgradePrefs.getString(Weapon.LASER.name()+"Description", "LEL");
        upgradeLevel = upgradePrefs.getInteger(Weapon.LASER.name()+"Level", -1);
        upgradeCost = upgradePrefs.getInteger(Weapon.LASER.name()+"Level-"+upgradeLevel+"-price", -1);
        createUpgradeBox(upgradeName, upgradeLevel, upgradeCost, upgradeUnlocked);

        upgradeName = "Magnet";
        upgradeUnlocked = upgradePrefs.getBoolean(Boost.MAGNET.name()+"Unlocked", false);
        upgradeDescription = upgradePrefs.getString(Boost.MAGNET.name()+"Description", "LEL");
        upgradeLevel = upgradePrefs.getInteger(Boost.MAGNET.name()+"Level", -1);
        upgradeCost = upgradePrefs.getInteger(Boost.MAGNET.name()+"Level-"+upgradeLevel+"-price", -1);
        createUpgradeBox(upgradeName, upgradeLevel, upgradeCost, upgradeUnlocked);

        upgradeName = "Armour";
        upgradeUnlocked = upgradePrefs.getBoolean(Boost.ARMOUR.name()+"Unlocked", false);
        upgradeDescription = upgradePrefs.getString(Boost.ARMOUR.name()+"Description", "LEL");
        upgradeLevel = upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level", -1);
        upgradeCost = upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level-"+upgradeLevel+"-price", -1);
        createUpgradeBox(upgradeName, upgradeLevel, upgradeCost, upgradeUnlocked);

*/
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
        outerTable.add(upgradesPane).height(100);//scrollpane height must be explicitly set for scrolling to work
        outerTable.add(informationTable).expandX();

        stage.addActor(outerTable);
        return stage;
    }

    private void populateScrollPane(){

    }

    private void createUpgradeBox(String upgradeName, int upgradeLevel, int upgradeCost, boolean upgradeUnlocked){

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
