package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Boost;
import com.jumpy.Jumpy;
import com.jumpy.Passive;
import com.jumpy.Screens.ScreenManager;
import com.jumpy.Screens.ShopScreen;
import com.jumpy.Weapon;

public class ShopScene {

    private Jumpy game;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Sound click;
    private Preferences upgradePrefs;
    private Preferences userPrefs;

    private ShopScreen shopScreen;

    /*
    private final String SpeedUpgradeDescription;
    private final Label SpeedUuyUpgradeLabel;
    private final int SpeedUpgradeCost;
    private final boolean SpeedUpgradeUnlocked;
    private final int SpeedUpgradeLevel;

    private final String LaserUpgradeDescription;
    private final Label LaserBuyUpgradeLabel;
    private final int LaserBpgradeCost;
    private final boolean LaserBpgradeUnlocked;
    private final int LaserBpgradeLevel;

    private final String MagnetUpgradeDescription;
    private final Label MagnetBuyUpgradeLabel;
    private final int MagnetBpgradeCost;
    private final boolean MagnetBpgradeUnlocked;
    private final int MagnetBpgradeLevel;

    private final String ArmourUpgradeDescription;
    private final Label ArmourBuyUpgradeLabel;
    private final int ArmourBpgradeCost;
    private final boolean ArmourBpgradeUnlocked;
    private final int ArmourBpgradeLevel;*/

    private Label currentGoldLabel;
    private Label informationDescription;
    private Table scrollPaneTable;
    private Table gravityBootsContainer;
    private Table magnetContainer;

    public ShopScene(ShopScreen shopScreen, Jumpy game){
        this.game = game;
        this.shopScreen = shopScreen;
        skin = new Skin(Gdx.files.internal("ui/skin/main_menu.json"));
        viewport = new FitViewport(Jumpy.V_WIDTH, Jumpy.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport);
        upgradePrefs = Gdx.app.getPreferences("upgradePrefs");
        userPrefs = Gdx.app.getPreferences("userPrefs");
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
        scrollPaneTable = new Table();
        //scrollPaneTable.setFillParent(true);
        scrollPaneTable.top();

        gravityBootsContainer = new Table();
        magnetContainer = new Table();
        loadGravityBootsContainer();
        loadMagnetContainer();
        //add table to the left and table to the right, left table has image, right table has two rows, row one has item name, spans two columns. row 2 has visual level and level 1/2/3 etc.
        /*String upgradeName = "Anti-Gravity";
        final String gravityUpgradeDescription = upgradePrefs.getString(Passive.ANTI_GRAVITY.name()+"Description", "LEL");
        final Label gravityBuyUpgradeLabel = new Label("", skin, "skin-normal");
        final int gravityUpgradeLevel = upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level", -1);
        final int gravityUpgradeCost = upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level-"+String.valueOf(gravityUpgradeLevel)+"-price", -1);
        final boolean gravityUpgradeUnlocked = upgradePrefs.getBoolean(Passive.ANTI_GRAVITY.name()+"Unlocked", false);

        Table gravityBootsContainer = new Table();
        Table upgradeImageTable = new Table();
        Table upgradeLevelTable =  new Table();
        upgradeImageTable.add(new Image(new Texture(("ui/new ui/Anti-gravity_boots.png"))));
        upgradeLevelTable.add(new Label(upgradeName, skin, "skin-normal"));
        upgradeLevelTable.row();
        final Table gravityHorizontalGroup = new Table();
        populateHorizontalGroup(gravityHorizontalGroup, gravityUpgradeLevel);

        upgradeLevelTable.add(gravityHorizontalGroup).left();

        gravityBootsContainer.add(upgradeImageTable);
        gravityBootsContainer.add(upgradeLevelTable).padLeft(5);
        gravityBootsContainer.row();

        //buy or upgrade button
        if(gravityUpgradeUnlocked){
            gravityBuyUpgradeLabel.setText("Upgrade");
        } else{
            gravityBuyUpgradeLabel.setText("Buy");
        }

        //cost label
        HorizontalGroup costGroup = new HorizontalGroup();
        Image dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        Label costLabel = new Label(String.valueOf(gravityUpgradeCost), skin, "skin-normal");
        costGroup.addActor(dollarSign);
        costGroup.addActor(costLabel);

        gravityBuyUpgradeLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int userCurrentGold = userPrefs.getInteger("goldEarned", 0);
                if(userCurrentGold >= gravityUpgradeCost){
                    int upgradeLevel = upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level");
                    if(upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level") < 3){
                        upgradePrefs.putInteger(Passive.ANTI_GRAVITY+"Level", upgradeLevel+1);
                        userPrefs.putInteger("goldEarned", userCurrentGold - gravityUpgradeCost);
                        upgradePrefs.flush();
                        userPrefs.flush();
                        reload();
                    }
                }
            }
        });


        gravityBootsContainer.add(costGroup);
        gravityBootsContainer.add(gravityBuyUpgradeLabel).padLeft(5).expandX();*/

        scrollPaneTable.add(new Label("PASSIVE ITEMS", skin, "skin-normal")).expandX().center();
        scrollPaneTable.row();
        scrollPaneTable.add(gravityBootsContainer).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(new Label("BOOSTERS", skin, "skin-normal")).expandX().center();
        scrollPaneTable.row();
        scrollPaneTable.add(magnetContainer).expandX();

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
        //informationTable.setFillParent(true);
        //informationTable.top();


        //add label to the information table
        informationDescription = new Label("Choose a item from the left to see the description!", skin, "skin-normal");
        informationDescription.setWidth(100);
        informationDescription.setWrap(true);
        //informationDescription.setWidth(informationTable.getWidth());//maybe remove this if any issues with compiling...
        informationTable.add(informationDescription).width(250f);

        //add both left and right sides to outertable, one in each column
        HorizontalGroup availableGoldGroup = new HorizontalGroup();
        Label availableGoldLabel = new Label("Available Gold: ", skin, "skin-normal");
        currentGoldLabel = new Label(String.valueOf(userPrefs.getInteger("goldEarned", -2)), skin, "skin-normal");
        availableGoldGroup.addActor(availableGoldLabel);
        availableGoldGroup.addActor(currentGoldLabel);
        outerTable.add(availableGoldGroup);
        outerTable.row();
        outerTable.add(upgradesPane).height(150).left();//scrollpane height must be explicitly set for scrolling to work
        outerTable.add(informationTable).expandX().left().padLeft(5);


        outerTable.row();
        Label resetLabel = new Label("reset", skin, "skin-normal");

        resetLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                upgradePrefs.putInteger(Passive.ANTI_GRAVITY+"Level", 0);
                upgradePrefs.putInteger(Boost.MAGNET+"Level", 0);
                upgradePrefs.flush();
                informationDescription.setText("Choose a item from the left to see the description!");
                loadGravityBootsContainer();
                loadMagnetContainer();
            }
        });


        outerTable.row();
        outerTable.add(resetLabel);

        //back button
        Label backLabel = new Label("back", skin, "skin-normal");

        backLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
            }
        });
        outerTable.add(backLabel);

        stage.addActor(outerTable);
        return stage;
    }

    public void loadGravityBootsContainer(){
        gravityBootsContainer.clear();
        String upgradeName = "Anti-Gravity";
        final String gravityUpgradeDescription = upgradePrefs.getString(Passive.ANTI_GRAVITY.name()+"Description", "LEL");
        final Label gravityBuyUpgradeLabel = new Label("", skin, "skin-normal");
        final int gravityUpgradeLevel = upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level", -1);
        final int gravityUpgradeCost = upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level-"+String.valueOf(gravityUpgradeLevel)+"-price", -1);
        final boolean gravityUpgradeUnlocked = upgradePrefs.getBoolean(Passive.ANTI_GRAVITY.name()+"Unlocked", false);


        Table upgradeImageTable = new Table();
        Table upgradeLevelTable =  new Table();
        upgradeImageTable.add(new Image(new Texture(("ui/new ui/Anti-gravity_boots.png"))));
        upgradeLevelTable.add(new Label(upgradeName, skin, "skin-normal"));
        upgradeLevelTable.row();
        final Table gravityHorizontalGroup = new Table();
        populateHorizontalGroup(gravityHorizontalGroup, gravityUpgradeLevel);

        upgradeLevelTable.add(gravityHorizontalGroup).left();

        gravityBootsContainer.add(upgradeImageTable);
        gravityBootsContainer.add(upgradeLevelTable).padLeft(5);
        gravityBootsContainer.row();

        //buy or upgrade button
        if(gravityUpgradeUnlocked || gravityUpgradeLevel > 0){
            gravityBuyUpgradeLabel.setText("Upgrade");
        } else{
            gravityBuyUpgradeLabel.setText("Buy");
        }

        //cost label
        HorizontalGroup costGroup = new HorizontalGroup();
        Image dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        Label costLabel = new Label(String.valueOf(gravityUpgradeCost), skin, "skin-normal");
        costGroup.addActor(dollarSign);
        costGroup.addActor(costLabel);

        gravityBuyUpgradeLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int userCurrentGold = userPrefs.getInteger("goldEarned", 0);
                if(userCurrentGold >= gravityUpgradeCost){
                    int upgradeLevel = upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level");
                    if(upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level") < 3){
                        currentGoldLabel.setText(String.valueOf(userCurrentGold - gravityUpgradeCost));
                        upgradePrefs.putInteger(Passive.ANTI_GRAVITY+"Level", upgradeLevel+1);
                        userPrefs.putInteger("goldEarned", userCurrentGold - gravityUpgradeCost);
                        upgradePrefs.flush();
                        userPrefs.flush();
                        loadGravityBootsContainer();
                    }
                }
            }
        });

        gravityBootsContainer.add(costGroup);
        gravityBootsContainer.add(gravityBuyUpgradeLabel).padLeft(5).expandX();
        gravityBootsContainer.row();

        //information button
        Label informationButton = new Label("Info", skin, "skin-normal");
        informationButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                informationDescription.setText(gravityUpgradeDescription);
            }
        });
        gravityBootsContainer.add(informationButton).expandX().center();


    }

    public void loadMagnetContainer(){
        magnetContainer.clear();
        String upgradeName = "Magnet";
        final String magnetUpgradeDescription = upgradePrefs.getString(Boost.MAGNET.name()+"Description", "LEL");
        final Label magnetBuyUpgradeLabel = new Label("", skin, "skin-normal");
        final int magnetUpgradeLevel = upgradePrefs.getInteger(Boost.MAGNET.name()+"Level", -1);
        final int magnetUpgradeCost = upgradePrefs.getInteger(Boost.MAGNET.name()+"Level-"+String.valueOf(magnetUpgradeLevel)+"-price", 9999);
        final boolean magnetUpgradeUnlocked = upgradePrefs.getBoolean(Boost.MAGNET.name()+"Unlocked", false);


        Table upgradeImageTable = new Table();
        Table upgradeLevelTable =  new Table();
        upgradeImageTable.add(new Image(new Texture(("ui/new ui/Anti-gravity_boots.png"))));
        upgradeLevelTable.add(new Label(upgradeName, skin, "skin-normal"));
        upgradeLevelTable.row();
        final Table horizontalGroup = new Table();
        populateHorizontalGroup(horizontalGroup, magnetUpgradeLevel);

        upgradeLevelTable.add(horizontalGroup).left();

        magnetContainer.add(upgradeImageTable);
        magnetContainer.add(upgradeLevelTable).padLeft(5);
        magnetContainer.row();

        //buy or upgrade button
        if(magnetUpgradeUnlocked || magnetUpgradeLevel > 0){
            magnetBuyUpgradeLabel.setText("Upgrade");
        } else{
            magnetBuyUpgradeLabel.setText("Buy");
        }

        //cost label
        HorizontalGroup costGroup = new HorizontalGroup();
        Image dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        Label costLabel = new Label(String.valueOf(magnetUpgradeCost), skin, "skin-normal");
        costGroup.addActor(dollarSign);
        costGroup.addActor(costLabel);

        magnetBuyUpgradeLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int userCurrentGold = userPrefs.getInteger("goldEarned", 0);
                if(userCurrentGold >= magnetUpgradeCost){
                    int upgradeLevel = upgradePrefs.getInteger(Boost.MAGNET.name()+"Level");
                    if(upgradePrefs.getInteger(Boost.MAGNET.name()+"Level") < 3){
                        currentGoldLabel.setText(String.valueOf(userCurrentGold - magnetUpgradeCost));
                        upgradePrefs.putInteger(Boost.MAGNET+"Level", upgradeLevel+1);
                        userPrefs.putInteger("goldEarned", userCurrentGold - magnetUpgradeCost);
                        upgradePrefs.flush();
                        userPrefs.flush();
                        loadMagnetContainer();
                    }
                }
            }
        });

        magnetContainer.add(costGroup);
        magnetContainer.add(magnetBuyUpgradeLabel).padLeft(5).expandX();
        magnetContainer.row();
        //information button
        Label informationButton = new Label("Info", skin, "skin-normal");
        informationButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                informationDescription.setText(magnetUpgradeDescription);
            }
        });
        magnetContainer.add(informationButton).expandX().center();
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

    public void populateHorizontalGroup(Table group, int upgradeLevel){
        if(group != null){
            group.clear();
            for(int i=1; i<=3; i++){
                Stack stack = new Stack();
                stack.add(new Image(new Texture("ui/new ui/Level_Fill_Base_Horizontal.png")));
                if(i <= upgradeLevel){
                    stack.add(new Image(new Texture("ui/new ui/Level_Fill_Horizontal.png")));
                }
                group.add(stack).left().padLeft(2);
            }
        }
    }
}
