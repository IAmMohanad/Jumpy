package com.jumpy.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jumpy.*;
import com.jumpy.Characters.*;
import com.jumpy.Objects.*;
import com.jumpy.Scenes.LevelSummaryScene;
import com.jumpy.Scenes.PauseScene;
import com.jumpy.Screens.PlayScreen;

import java.util.ArrayList;

public class LevelOne extends GameMap {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    //private final String mapLocation = "retro_game_map3_collidable_objects.tmx";

    private Jumpy game;
    private Player player;

    private ArrayList<Coin> coinList = new ArrayList<Coin>();
    private ArrayList<Enemy> enemiesList = new ArrayList<Enemy>();
    private ArrayList<IceBallShooter> projectileShootersList = new ArrayList<IceBallShooter>();
    private ArrayList<Exit> exitList = new ArrayList<Exit>();

    private LevelSummaryScene levelSummary;
    private Stage stage;
    private boolean firstTime = true;
    private boolean exitReached;

    private PlayScreen playScreen;

    private final String currentLevel = "1-1";

    public LevelOne(Jumpy game, PlayScreen playScreen){
        this.game = game;
        this.playScreen = playScreen;
        exitReached = false;
    }

    public void setExitReached(boolean status){
        exitReached = status;
    }

    public Player getPlayer(){
        return player;
    }

    public ArrayList<Coin> getCoins(){
        return this.coinList;
    }

    public ArrayList<Enemy> getEnemies() { return this.enemiesList; }

    public ArrayList<IceBallShooter> getProjectileShootersList() {
        return projectileShootersList;
    }

    public ArrayList<Exit> getExits() { return this.exitList; }

        //TODO create summary screen after 10 secs, then rest the game and see if player still doesn't appear.....
    @Override
    public void load(String location){
        map = new TmxMapLoader().load(location);

        Preferences userPrefs = Gdx.app.getPreferences("userPrefs");
        Active equippedActive = Active.valueOf(userPrefs.getString("equippedActive"));
        Passive equippedPassive = Passive.valueOf(userPrefs.getString("equippedPassive"));
        Boost equippedBoost = Boost.valueOf(userPrefs.getString("equippedBoost"));

        isLevelComplete = false;

        spawnElements(equippedActive, equippedBoost, equippedPassive);

        renderer = new OrthogonalTiledMapRenderer(map);

        levelSummary = new LevelSummaryScene(game, playScreen);
    }

    //Randomly choose the spawn points on a map. //TODO throw dice to choose spawn1/2/3/ etc also change collision layer if needed
    private void spawnElements(Active equippedActive, Boost equippedBoost, Passive equippedPassive){
        String spawnChoice = "spawn1";
        spawnPlayer(spawnChoice, equippedActive, equippedBoost, equippedPassive);
        spawnCoins(spawnChoice);
        spawnEnemies(spawnChoice);
        spawnProjectileShooters(spawnChoice);
    }

    private void spawnProjectileShooters(String spawnChoice){
        for(MapObject object : map.getLayers().get(spawnChoice).getObjects()) {
            if (object.getName().toLowerCase().equals("ice_ball_shooter")) {
                Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                Move shootDirection = Move.valueOf(object.getProperties().get("direction").toString().toUpperCase());
                if(object.getProperties().containsKey("shootInterval")){//TODO add new property shootInterval
                    int shootInterval = Integer.parseInt(object.getProperties().get("shootInterval").toString());
                    projectileShootersList.add(new IceBallShooter(map,this, objectRect.x, objectRect.y, shootDirection, shootInterval));
                } else{
                    projectileShootersList.add(new IceBallShooter(map,this, objectRect.x, objectRect.y, shootDirection));
                }
            }
        }
    }

    private void spawnPlayer(String spawnChoice, Active equippedActive, Boost equippedBoost, Passive equippedPassive){
        for(MapObject object : map.getLayers().get(spawnChoice).getObjects()) {
            if (object.getName().toLowerCase().equals("player")) {
                Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                player = new Player(equippedActive, equippedBoost, equippedPassive, this, objectRect.x, objectRect.y, playScreen);
            }
            if(object.getName().toLowerCase().equals("exit")){
                Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                exitList.add(new Exit(this, objectRect.x, objectRect.y, (int) objectRect.width, (int) objectRect.height));
            }
        }
    }

    private void spawnCoins(String spawnChoice){
        for(MapObject object : map.getLayers().get(spawnChoice).getObjects()) {
            if (object.getName().toLowerCase().equals("coin")) {
                Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                coinList.add(new Coin(this, objectRect.x, objectRect.y));
            }
        }
    }

    private void spawnEnemies(String spawnChoice){
        if(game.getIsHardMode()) {
            enemiesList.add(new Chaser(this, player, 200, 100, 16, 16));
        }
        for(MapObject object : map.getLayers().get(spawnChoice).getObjects()) {
            Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
            if(object.getName().toLowerCase().equals("gargoyle_flying")){
                //Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                enemiesList.add(new GargoyleFlying(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("goblin")){
                //Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                enemiesList.add(new Goblin(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("barbarian")){
                //Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                enemiesList.add(new Barbarian(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("gargoyle")){
                //Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                enemiesList.add(new Gargoyle(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("totem")){
                //Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                enemiesList.add(new Totem(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("spike")){
                Move direction;
                if(object.getProperties().containsKey("direction")){
                    direction = Move.valueOf(object.getProperties().get("direction").toString().toUpperCase());
                } else{
                    direction = Move.UP;
                }
                enemiesList.add(new Spike(this, objectRect.x, objectRect.y, direction));
            }
        }
    }

    //private Vector2 rotatePoint(Vector2 position, Rectangle center, double angle){
     //   angle = Math.toRadians(angle);
    /*
        double rotatedX = Math.cos(angle) * (position.x - center.x) - Math.sin(angle) * (position.y-center.y) + center.x;
        double rotatedY = Math.sin(angle) * (position.x - center.x) + Math.cos(angle) * (position.y - center.y) + center.y;
        return new Vector2((float) rotatedX, (float) rotatedY);*/
        /*double x1 = position.x - center.x;
        double y1 = position.y - center.y;

        double x2 = x1 * Math.cos(angle) - y1 * Math.sin(angle);
        double y2 = x1 * Math.sin(angle) + y1 * Math.cos(angle);

        return new Vector2((float) x2 + center.x, (float) y2 + center.y);*/
        /*double newX = center.x + (position.x-center.x)*Math.cos(angle) - (position.y-center.y)*Math.sin(angle);

        double newY = center.y + (position.x-center.x)*Math.sin(angle) + (position.y-center.y)*Math.cos(angle);

        return new Vector2((float) newX, (float) newY);*/


        /*float rotateBy = 90 * delta;
            Rectangle objectRect = new Rectangle(100, 100, 20, 20);
            testCircle testCoin = new testCircle(this, 200, 200);
            Vector2 rotatedPosition = rotatePoint(testCoin.getPosition(), objectRect, rotateBy);
            testCoin.setPosition(rotatedPosition.x, rotatedPosition.y);
            testCoin.update(batch, delta, camera);*/
   // }

    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch, float delta) {
        renderer.setView(camera);
        renderer.render();

        if(!player.isDeathComplete()){
            //hud.setScore(player.getPoints());

            hud.setLife(player.getHealth());
            hud.setCoinsCollected(player.getCoinsCollected());

            //chaserTwo.update(batch, delta, camera);
            for(Coin coin : coinList){
                coin.update(batch, delta, camera);
            }

            for(Enemy e : enemiesList){
                e.update(batch, delta, camera);
            }

            for(IceBallShooter o : projectileShootersList){
                o.update(batch, delta, camera);
            }
            for(Exit exit : exitList){
                exit.update(batch, delta, camera);
            }
            player.update(batch, delta, camera);
            if(hud.getLevelTimer() <= 0){
                player.die();
            }
        } else{//level summary screen
            createSummary();
            isLevelComplete = true;
        }
    }

    private void createSummary(){
        //TODO save the points / time / score in new preferences file. different preferences file for each level. One main one to check if level is done.
        if(firstTime){
            firstTime = false;
            stage = levelSummary.create(player.getCoinsCollected(), player.getEnemiesKilled(), hud.getLevelTimer());//player.getPoints(), numberOfStars, player.getCoinsCollected());
            Gdx.input.setInputProcessor(stage);
        }

        levelSummary.render();
    }

    @Override
    public void dispose(){

    }

    @Override
    public TileType getTileTypeByCoordinate(int layer, int col, int row) {
        Cell cell = ((TiledMapTileLayer) map.getLayers().get(layer)).getCell(col, row);
        if(cell != null){
            TiledMapTile tile = cell.getTile();
            if(tile != null){
                int id = tile.getId();
                return TileType.getTileTypeById(id);
            }
        }
        return null;
    }

    @Override
    public int getWidth() {
        return ((TiledMapTileLayer) map.getLayers().get(1)).getWidth();
    }

    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) map.getLayers().get(1)).getHeight();
    }

    @Override
    public int getLayers() {
        return map.getLayers().getCount();
    }

    public int getPixelWidth() { return getWidth() * TileType.TILE_SIZE;}// TileType.TILE_SIZE; }

    public int getPixelHeight() { return getHeight() * TileType.TILE_SIZE;}// TileType.TILE_SIZE; }

    public void cameraStop(OrthographicCamera camera){
        //Stops camera moving out off the edge of the game world
        float minCameraX = camera.zoom * (camera.viewportWidth / 2);
        float maxCameraX = getPixelWidth() - minCameraX;
        float minCameraY = camera.zoom * (camera.viewportHeight / 2);
        float maxCameraY = getPixelHeight() - minCameraY;
        camera.position.set(Math.min(maxCameraX, Math.max(player.getPosition().x, minCameraX)),
                Math.min(maxCameraY, Math.max(player.getPosition().y, minCameraY)),
                0);
        camera.update();
    }

    public String getLevel(){
        return this.currentLevel;
    }
}

