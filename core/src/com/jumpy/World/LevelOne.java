package com.jumpy.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jumpy.Active;
import com.jumpy.Characters.*;
import com.jumpy.Jumpy;
import com.jumpy.Objects.Coin;
import com.jumpy.Scenes.LevelSummaryScene;
import com.jumpy.Scenes.PauseScene;
import com.jumpy.Screens.PlayScreen;

import java.util.ArrayList;

public class LevelOne extends GameMap {

    /*
    TODO instead of passing player around to chaser and other classes, make getPlayer in level class, then use that in chaser
     */
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private final String mapLocation = "retro_game_map3.tmx";//"newest_map.tmx";

    private Jumpy game;
    private Player player;
    private Active active;
    private Chaser chaserTwo;

    private ArrayList<Coin> coinList = new ArrayList<Coin>();
    private ArrayList<Enemy> enemiesList = new ArrayList<Enemy>();

    private LevelSummaryScene levelSummary;
    private PauseScene pauseScene;
    private int goldEarned = 0;
    private Stage stage;
    private boolean firstTime = true;

    private PlayScreen playScreen;

    public Bee bee;
    public Totem totem;

    private final String currentLevel = "1-1";

    public LevelOne(Jumpy game, /*Hud hud,*/ PlayScreen playScreen){
        //this.hud = hud;
        this.game = game;
        this.playScreen = playScreen;
    }

    public Player getPlayer(){
        return player;
    }

    public ArrayList<Coin> getCoins(){
        return this.coinList;
    }

    public ArrayList<Enemy> getEnemies() { return this.enemiesList; }

//TODO create summary screen after 10 secs, then rest the game and see if player still doesn't appear.....
    @Override
    public void load(String location){
        map = new TmxMapLoader().load(location);
        Preferences userPrefs = Gdx.app.getPreferences("userPrefs");
        Active equippedActive = Active.valueOf(userPrefs.getString("equippedActive"));
        player = new Player(equippedActive, this, 32, 64, playScreen);
        //active = new Boomerang(this, 50, 125);

        chaserTwo = new Chaser(this, player,200,100, 16, 16);

        coinList.add(new Coin(this, 80, 100));
        coinList.add(new Coin(this, 160, 100));
        coinList.add(new Coin(this, 240, 100));
        coinList.add(new Coin(this, 360, 120));

        enemiesList.add(new Bee(this, 400, 100));
        enemiesList.add(new Totem(this, 400, 150));
        enemiesList.add(new Totem(this, 400, 100));

        renderer = new OrthogonalTiledMapRenderer(map);

        levelSummary = new LevelSummaryScene(game, playScreen);
    }

    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch, float delta) {
        renderer.setView(camera);
        renderer.render();

        if(!player.isDeathComplete()){
            //hud.setScore(player.getPoints());
            hud.setLife(player.getHealth());
            hud.setCoinsCollected(player.getCoinsCollected());
            chaserTwo.update(batch, delta, camera);
            for(Coin coin : coinList){
                coin.update(batch, delta, camera);
            }

            for(Enemy e : enemiesList){
                e.update(batch, delta, camera);
            }
            player.update(batch, delta, camera);
        } else{//level summary screen
            createSummary();

            System.out.println("finito");
        }
    }

    private void createSummary(){
        //TODO save the points / time / score in new preferences file. different preferences file for each level. One main one to check if level is done.
        if(firstTime){
            firstTime = false;
            int numberOfStars = 0;
            if(player.getPoints() > 50) numberOfStars++;
            if(player.getPoints() > 100) numberOfStars++;
            if(player.getPoints() > 149) numberOfStars++;
            stage = levelSummary.create(player.getPoints(), numberOfStars, player.getCoinsCollected());
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
        return ((TiledMapTileLayer) map.getLayers().get(0)).getWidth();
    }

    @Override
    public int getHeight() {
        return ((TiledMapTileLayer) map.getLayers().get(0)).getHeight();
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

    public Chaser getChaser(){
        return this.chaserTwo;
    }
}
