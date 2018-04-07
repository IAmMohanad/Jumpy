package com.jumpy.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Active;
import com.jumpy.Boost;
import com.jumpy.Jumpy;
import com.jumpy.Passive;
import com.jumpy.Screens.ScreenManager;
import com.jumpy.Screens.ShopScreen;

public class ShopScene {

    private Jumpy game;
    private Stage stage;
    private Viewport viewport;
    private Skin skin;
    private Sound click;
    private Preferences upgradePrefs;
    private Preferences userPrefs;

    private ShopScreen shopScreen;

    private Label currentGoldLabel;
    private Label informationDescription;
    private Table scrollPaneTable;
    private Table gravityBootsContainer;
    private Table speedBootsContainer;
    private Table magnetContainer;
    private Table armourContainer;
    private Table laserContainer;

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
        speedBootsContainer = new Table();
        magnetContainer = new Table();
        armourContainer = new Table();
        laserContainer = new Table();
        loadGravityBootsContainer();
        loadSpeedBootsContainer();
        loadMagnetContainer();
        loadArmourContainer();
        loadLaserContainer();

        scrollPaneTable.add(new Label("PASSIVE ITEMS", skin, "skin-normal")).expandX().center();
        scrollPaneTable.row();
        scrollPaneTable.add(gravityBootsContainer).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(speedBootsContainer).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(new Label("BOOSTERS", skin, "skin-normal")).expandX().center();
        scrollPaneTable.row();
        scrollPaneTable.add(magnetContainer).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(armourContainer).expandX();
        scrollPaneTable.row();
        scrollPaneTable.add(new Label("ACTIVE", skin, "skin-normal")).expandX().center();
        scrollPaneTable.row();
        scrollPaneTable.add(laserContainer).expandX();

        ScrollPane upgradesPane = new ScrollPane(scrollPaneTable, skin, "default-no-slider");
        //upgradesPane.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 4);

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
                userPrefs.putString("equippedPassive", Passive.NONE.toString());
                userPrefs.putString("equippedBoost", Boost.NONE.toString());
                userPrefs.putString("equippedActive", Active.NONE.toString());
                upgradePrefs.putInteger(Passive.ANTI_GRAVITY+"Level", 0);
                upgradePrefs.putInteger(Passive.SPEED+"Level", 0);
                upgradePrefs.putInteger(Boost.MAGNET+"Level", 0);
                upgradePrefs.putInteger(Boost.ARMOUR+"Level", 0);
                userPrefs.flush();
                upgradePrefs.flush();
                informationDescription.setText("Choose a item from the left to see the description!");
                loadGravityBootsContainer();
                loadSpeedBootsContainer();
                loadMagnetContainer();
                loadArmourContainer();
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
        final Label gravityEquipLabel = new Label("", skin, "skin-normal");
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

        if(userPrefs.getString("equippedPassive").equals(Passive.ANTI_GRAVITY.name())){
            gravityEquipLabel.setText("EQUIPPED");
        } else{
            gravityEquipLabel.setText("EQUIP");
        }
        gravityEquipLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(upgradePrefs.getInteger(Passive.ANTI_GRAVITY.name()+"Level") > 0){
                    gravityEquipLabel.setText("EQUIPPED");
                    userPrefs.putString("equippedPassive", Passive.ANTI_GRAVITY.name());
                    userPrefs.flush();
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
        gravityBootsContainer.add(gravityEquipLabel).padLeft(5).expandX();
    }

    public void loadSpeedBootsContainer(){
        speedBootsContainer.clear();
        String upgradeName = "Speed";
        final String speedUpgradeDescription = upgradePrefs.getString(Passive.SPEED.name()+"Description", "LEL");
        final Label speedBuyUpgradeLabel = new Label("", skin, "skin-normal");
        final Label speedEquipLabel = new Label("", skin, "skin-normal");
        final int speedUpgradeLevel = upgradePrefs.getInteger(Passive.SPEED.name()+"Level", -1);
        final int speedUpgradeCost = upgradePrefs.getInteger(Passive.SPEED.name()+"Level-"+String.valueOf(speedUpgradeLevel)+"-price", -1);
        final boolean speedUpgradeUnlocked = upgradePrefs.getBoolean(Passive.SPEED.name()+"Unlocked", false);


        Table upgradeImageTable = new Table();
        Table upgradeLevelTable =  new Table();
        upgradeImageTable.add(new Image(new Texture(("ui/new ui/speed_boots.png"))));
        upgradeLevelTable.add(new Label(upgradeName, skin, "skin-normal"));
        upgradeLevelTable.row();
        final Table speedHorizontalGroup = new Table();
        populateHorizontalGroup(speedHorizontalGroup, speedUpgradeLevel);

        upgradeLevelTable.add(speedHorizontalGroup).left();

        speedBootsContainer.add(upgradeImageTable);
        speedBootsContainer.add(upgradeLevelTable).padLeft(5);
        speedBootsContainer.row();

        //buy or upgrade button
        if(speedUpgradeUnlocked || speedUpgradeLevel > 0){
            speedBuyUpgradeLabel.setText("Upgrade");
        } else{
            speedBuyUpgradeLabel.setText("Buy");
        }

        //cost label
        HorizontalGroup costGroup = new HorizontalGroup();
        Image dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        Label costLabel = new Label(String.valueOf(speedUpgradeCost), skin, "skin-normal");
        costGroup.addActor(dollarSign);
        costGroup.addActor(costLabel);

        speedBuyUpgradeLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int userCurrentGold = userPrefs.getInteger("goldEarned", 0);
                if(userCurrentGold >= speedUpgradeCost){
                    int upgradeLevel = upgradePrefs.getInteger(Passive.SPEED.name()+"Level");
                    if(upgradePrefs.getInteger(Passive.SPEED.name()+"Level") < 3){
                        currentGoldLabel.setText(String.valueOf(userCurrentGold - speedUpgradeCost));
                        upgradePrefs.putInteger(Passive.SPEED+"Level", upgradeLevel+1);
                        userPrefs.putInteger("goldEarned", userCurrentGold - speedUpgradeCost);
                        upgradePrefs.flush();
                        userPrefs.flush();
                        loadSpeedBootsContainer();
                    }
                }
            }
        });

        if(userPrefs.getString("equippedPassive").equals(Passive.SPEED.name())){
            speedEquipLabel.setText("EQUIPPED");
        } else{
            speedEquipLabel.setText("EQUIP");
        }
        speedEquipLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(upgradePrefs.getInteger(Passive.SPEED.name()+"Level") > 0){
                    loadGravityBootsContainer();
                    speedEquipLabel.setText("EQUIPPED");
                    userPrefs.putString("equippedPassive", Passive.SPEED.name());
                    userPrefs.flush();
                }
            }
        });

        speedBootsContainer.add(costGroup);
        speedBootsContainer.add(speedBuyUpgradeLabel).padLeft(5).expandX();
        speedBootsContainer.row();

        //information button
        Label informationButton = new Label("Info", skin, "skin-normal");
        informationButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                informationDescription.setText(speedUpgradeDescription);
            }
        });
        speedBootsContainer.add(informationButton).expandX().center();
        speedBootsContainer.add(speedEquipLabel).padLeft(5).expandX();
    }

    public void loadMagnetContainer(){
        magnetContainer.clear();
        String upgradeName = "Magnet";
        final String magnetUpgradeDescription = upgradePrefs.getString(Boost.MAGNET.name()+"Description", "LEL");
        final Label magnetBuyUpgradeLabel = new Label("", skin, "skin-normal");
        final Label magnetEquipLabel = new Label("", skin, "skin-normal");
        final int magnetUpgradeLevel = upgradePrefs.getInteger(Boost.MAGNET.name()+"Level", -1);
        final int magnetUpgradeCost = upgradePrefs.getInteger(Boost.MAGNET.name()+"Level-"+String.valueOf(magnetUpgradeLevel)+"-price", 9999);
        final boolean magnetUpgradeUnlocked = upgradePrefs.getBoolean(Boost.MAGNET.name()+"Unlocked", false);

        Table upgradeImageTable = new Table();
        Table upgradeLevelTable =  new Table();
        upgradeImageTable.add(new Image(new Texture(("ui/new ui/Magnet_48.png"))));
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

        if(userPrefs.getString("equippedBoost").equals(Boost.MAGNET.name())){
            magnetEquipLabel.setText("EQUIPPED");
        } else{
            magnetEquipLabel.setText("EQUIP");
        }
        magnetEquipLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(upgradePrefs.getInteger(Boost.MAGNET.name()+"Level") > 0){
                    loadGravityBootsContainer();
                    magnetEquipLabel.setText("EQUIPPED");
                    userPrefs.putString("equippedBoost", Boost.MAGNET.name());
                    userPrefs.flush();
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
        magnetContainer.add(magnetEquipLabel).padLeft(5).expandX();
    }

    public void loadArmourContainer(){
        armourContainer.clear();
        String upgradeName = "Armour";
        final String armourUpgradeDescription = upgradePrefs.getString(Boost.ARMOUR.name()+"Description", "LEL");
        final Label armourBuyUpgradeLabel = new Label("", skin, "skin-normal");
        final Label armourEquipLabel = new Label("", skin, "skin-normal");
        final int armourUpgradeLevel = upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level", -1);
        final int armourUpgradeCost = upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level-"+String.valueOf(armourUpgradeLevel)+"-price", 9999);
        final boolean armourUpgradeUnlocked = upgradePrefs.getBoolean(Boost.ARMOUR.name()+"Unlocked", false);

        Table upgradeImageTable = new Table();
        Table upgradeLevelTable =  new Table();
        upgradeImageTable.add(new Image(new Texture(("ui/new ui/Armour_48.png"))));
        upgradeLevelTable.add(new Label("Disguise", skin, "skin-normal"));//TODO keep this as disguise because doesn't kill enemy on touch?
        upgradeLevelTable.row();
        final Table horizontalGroup = new Table();
        populateHorizontalGroup(horizontalGroup, armourUpgradeLevel);

        upgradeLevelTable.add(horizontalGroup).left();

        armourContainer.add(upgradeImageTable);
        armourContainer.add(upgradeLevelTable).padLeft(5);
        armourContainer.row();

        //buy or upgrade button
        if(armourUpgradeUnlocked || armourUpgradeLevel > 0){
            armourBuyUpgradeLabel.setText("Upgrade");
        } else{
            armourBuyUpgradeLabel.setText("Buy");
        }

        //cost label
        HorizontalGroup costGroup = new HorizontalGroup();
        Image dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        Label costLabel = new Label(String.valueOf(armourUpgradeCost), skin, "skin-normal");
        costGroup.addActor(dollarSign);
        costGroup.addActor(costLabel);

        armourBuyUpgradeLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int userCurrentGold = userPrefs.getInteger("goldEarned", 0);
                if(userCurrentGold >= armourUpgradeCost){
                    int upgradeLevel = upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level");
                    if(upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level") < 3){
                        currentGoldLabel.setText(String.valueOf(userCurrentGold - armourUpgradeCost));
                        upgradePrefs.putInteger(Boost.ARMOUR+"Level", upgradeLevel+1);
                        userPrefs.putInteger("goldEarned", userCurrentGold - armourUpgradeCost);
                        upgradePrefs.flush();
                        userPrefs.flush();
                        loadArmourContainer();
                    }
                }
            }
        });

        if(userPrefs.getString("equippedBoost").equals(Boost.ARMOUR.name())){
            armourEquipLabel.setText("EQUIPPED");
        } else{
            armourEquipLabel.setText("EQUIP");
        }
        armourEquipLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level") > 0){
                    loadGravityBootsContainer();
                    armourEquipLabel.setText("EQUIPPED");
                    userPrefs.putString("equippedBoost", Boost.ARMOUR.name());
                    userPrefs.flush();
                }
            }
        });

        armourContainer.add(costGroup);
        armourContainer.add(armourBuyUpgradeLabel).padLeft(5).expandX();
        armourContainer.row();
        //information button
        Label informationButton = new Label("Info", skin, "skin-normal");
        informationButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                informationDescription.setText(armourUpgradeDescription);
            }
        });
        armourContainer.add(informationButton).expandX().center();
        armourContainer.add(armourEquipLabel).padLeft(5).expandX();
    }

    public void loadLaserContainer(){
        laserContainer.clear();
        String upgradeName = "Laser";
        final String laserUpgradeDescription = upgradePrefs.getString(Boost.ARMOUR.name()+"Description", "LEL");
        final Label laserBuyUpgradeLabel = new Label("", skin, "skin-normal");
        final Label laserEquipLabel = new Label("", skin, "skin-normal");
        final int laserUpgradeLevel = upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level", -1);
        final int laserUpgradeCost = upgradePrefs.getInteger(Boost.ARMOUR.name()+"Level-"+String.valueOf(laserUpgradeLevel)+"-price", 9999);
        final boolean laserUpgradeUnlocked = upgradePrefs.getBoolean(Boost.ARMOUR.name()+"Unlocked", false);


        Table upgradeImageTable = new Table();
        Table upgradeLevelTable =  new Table();
        upgradeImageTable.add(new Image(new Texture(("ui/new ui/Armour_48.png"))));
        upgradeLevelTable.add(new Label(upgradeName, skin, "skin-normal"));
        upgradeLevelTable.row();
        final Table horizontalGroup = new Table();
        populateHorizontalGroup(horizontalGroup, laserUpgradeLevel);

        upgradeLevelTable.add(horizontalGroup).left();

        laserContainer.add(upgradeImageTable);
        laserContainer.add(upgradeLevelTable).padLeft(5);
        laserContainer.row();

        //buy or upgrade button
        if(laserUpgradeUnlocked || laserUpgradeLevel > 0){
            laserBuyUpgradeLabel.setText("Upgrade");
        } else{
            laserBuyUpgradeLabel.setText("Buy");
        }

        //cost label
        HorizontalGroup costGroup = new HorizontalGroup();
        Image dollarSign = new Image(new Texture(Gdx.files.internal("ui/new ui/dollar_sign.png")));
        Label costLabel = new Label(String.valueOf(laserUpgradeCost), skin, "skin-normal");
        costGroup.addActor(dollarSign);
        costGroup.addActor(costLabel);

        laserBuyUpgradeLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int userCurrentGold = userPrefs.getInteger("goldEarned", 0);
                if(userCurrentGold >= laserUpgradeCost){
                    int upgradeLevel = upgradePrefs.getInteger(Active.LASER.name()+"Level");
                    if(upgradePrefs.getInteger(Active.LASER.name()+"Level") < 3){
                        currentGoldLabel.setText(String.valueOf(userCurrentGold - laserUpgradeCost));
                        upgradePrefs.putInteger(Active.LASER.name()+"Level", upgradeLevel+1);
                        userPrefs.putInteger("goldEarned", userCurrentGold - laserUpgradeCost);
                        upgradePrefs.flush();
                        userPrefs.flush();
                        loadArmourContainer();
                    }
                }
            }
        });

        if(userPrefs.getString("equippedBoost").equals(Active.LASER.name())){
            laserEquipLabel.setText("EQUIPPED");
        } else{
            laserEquipLabel.setText("EQUIP");
        }
        laserEquipLabel.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(upgradePrefs.getInteger(Active.LASER.name()+"Level") > 0){
                    loadLaserContainer();
                    laserEquipLabel.setText("EQUIPPED");
                    userPrefs.putString("equippedBoost", Active.LASER.name());
                    userPrefs.flush();
                }
            }
        });

        laserContainer.add(costGroup);
        laserContainer.add(laserBuyUpgradeLabel).padLeft(5).expandX();
        laserContainer.row();
        //information button
        Label informationButton = new Label("Info", skin, "skin-normal");
        informationButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                informationDescription.setText(laserUpgradeDescription);
            }
        });
        laserContainer.add(informationButton).expandX().center();
        laserContainer.add(laserEquipLabel).padLeft(5).expandX();
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