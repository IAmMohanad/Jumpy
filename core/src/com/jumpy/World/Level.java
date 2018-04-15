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
import java.util.Random;

public class Level extends GameMap {

    private LevelSummaryScene levelSummary;
    private Stage stage;
    private boolean firstTime = true;
    private boolean exitReached;

    private PlayScreen playScreen;

    private final String currentLevel = "1-1";

    public Level(Jumpy game, PlayScreen playScreen){
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

    @Override
    public void load(String location){
        map = new TmxMapLoader().load(location);

        isLevelComplete = false;

        spawnElements();

        renderer = new OrthogonalTiledMapRenderer(map);

        levelSummary = new LevelSummaryScene(game, playScreen);
    }

    //Randomly choose the spawn points on a map.
    private void spawnElements(){
        int numberOfSpawns = 1;
        if(map.getLayers().get("spawn1").getProperties().containsKey("numberOfSpawns")){
            numberOfSpawns = (Integer.parseInt(map.getLayers().get("spawn1").getProperties().get("numberOfSpawns").toString()));
        }

        int spawnToUse = getRandomSpawnPoint(numberOfSpawns);

        String spawnChoice = "spawn"+String.valueOf(spawnToUse);
        spawnPlayer(spawnChoice);
        spawnCoins(spawnChoice);
        spawnEnemies(spawnChoice);
        spawnProjectileShooters(spawnChoice);
    }

    private int getRandomSpawnPoint(int numberOfSpawns) {
        int min = 1;
        Random r = new Random();
        return r.nextInt(numberOfSpawns-min+1) + min;
    }

    private void spawnProjectileShooters(String spawnChoice){
        for(MapObject object : map.getLayers().get(spawnChoice).getObjects()) {
            if (object.getName().toLowerCase().equals("ice_ball_shooter")) {
                Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                Move shootDirection = Move.valueOf(object.getProperties().get("direction").toString().toUpperCase());
                if(object.getProperties().containsKey("shootInterval")){
                    int shootInterval = Integer.parseInt(object.getProperties().get("shootInterval").toString());
                    projectileShootersList.add(new IceBallShooter(map,this, objectRect.x, objectRect.y, shootDirection, shootInterval));
                } else{
                    projectileShootersList.add(new IceBallShooter(map,this, objectRect.x, objectRect.y, shootDirection));
                }
            }
        }
    }

    private void spawnPlayer(String spawnChoice){
        Preferences userPrefs = Gdx.app.getPreferences("userPrefs");
        Active equippedActive = Active.valueOf(userPrefs.getString("equippedActive"));
        Passive equippedPassive = Passive.valueOf(userPrefs.getString("equippedPassive"));
        Boost equippedBoost = Boost.valueOf(userPrefs.getString("equippedBoost"));
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
        for(MapObject object : map.getLayers().get(spawnChoice).getObjects()) {
            Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
            if(object.getName().toLowerCase().equals("gargoyle_flying")){
                enemiesList.add(new GargoyleFlying(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("goblin")){
                enemiesList.add(new Goblin(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("barbarian")){
                enemiesList.add(new Barbarian(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("gargoyle")){
                enemiesList.add(new Gargoyle(map, this, objectRect.x, objectRect.y));
            } else if(object.getName().toLowerCase().equals("totem")){
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

    private Vector2 rotatePoint(Vector2 position, Rectangle center, double angle){
        angle = Math.toRadians(angle);
        double newX = center.x + (position.x-center.x)*Math.cos(angle) - (position.y-center.y)*Math.sin(angle);

        double newY = center.y + (position.x-center.x)*Math.sin(angle) + (position.y-center.y)*Math.cos(angle);

        return new Vector2((float) newX, (float) newY);
    }

    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch, float delta) {
        renderer.setView(camera);
        renderer.render();

        if(!player.isDeathComplete()){
            hud.setLife(player.getHealth());
            hud.setCoinsCollected(player.getCoinsCollected());

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
        } else{
            createSummary();
            isLevelComplete = true;
        }
    }

    private void createSummary(){
        if(firstTime){
            firstTime = false;
            stage = levelSummary.create(player.getCoinsCollected(), player.getEnemiesKilled(), hud.getLevelTimer(), player.getExitReached());
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

    public int getPixelWidth() { return getWidth() * TileType.TILE_SIZE;}

    public int getPixelHeight() { return getHeight() * TileType.TILE_SIZE;}

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

    public String getCurrentLevel(){
        return this.currentLevel;
    }
}

