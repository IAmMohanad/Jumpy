package com.jumpy.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jumpy.Characters.*;
import com.jumpy.Intersection;
import com.jumpy.Objects.Coin;
import com.jumpy.Scenes.Hud;
import com.jumpy.Scenes.LevelSelectScene;
import com.jumpy.Scenes.LevelSummaryScene;

import java.util.ArrayList;

public class LevelOne extends GameMap {

    /*
    TODO instead of passing player around to chaser and other classes, make getPlayer in level class, then use that in chaser
     */
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private final String mapLocation = "retro_game_map2.tmx";//"newest_map.tmx";

    private Player player;
    private Weapon weapon;
    private Chaser chaserTwo;

    private ArrayList<Coin> coinList = new ArrayList<Coin>();
    private ArrayList<Enemy> enemiesList = new ArrayList<Enemy>();

    private LevelSummaryScene levelSummary;
    private Stage stage;
    private boolean firstTime = true;

    protected Hud hud;

    public Bee bee;
    public Totem totem;

    private final String currentLevel = "1-1";

    public LevelOne(Hud hud){
        this.hud = hud;
    }

    public ArrayList<Coin> getCoins(){
        return this.coinList;
    }

    public ArrayList<Enemy> getEnemies() { return this.enemiesList; }

    @Override
    public void load(String location){
        map = new TmxMapLoader().load(location);
        player = new Player("boomerang", this, hud, 32, 64);//, 1, 100);
        //weapon = new Boomerang(this, 50, 125);

        chaserTwo = new Chaser(this, player,200,100, 16, 16);

        coinList.add(new Coin(this, 80, 100));
        coinList.add(new Coin(this, 160, 100));
        coinList.add(new Coin(this, 240, 100));
        coinList.add(new Coin(this, 360, 120));
        //bee = new Bee(this, 400, 100);
        enemiesList.add(new Totem(this, 400, 150));
        enemiesList.add(new Totem(this, 100, 100));

        renderer = new OrthogonalTiledMapRenderer(map);

        levelSummary = new LevelSummaryScene();
    }

    @Override
    public void render(OrthographicCamera camera, SpriteBatch batch, float delta) {
        renderer.setView(camera);
        renderer.render();

        if(!player.isDeathComplete()){
           // hud.render();
            hud.setScore(player.getPoints());
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
        if(firstTime){
            firstTime = false;
            int numberOfStars = 0;
            if(player.getPoints() > 50) numberOfStars++;
            if(player.getPoints() > 100) numberOfStars++;
            if(player.getPoints() > 150) numberOfStars++;
            stage = levelSummary.create(player.getPoints(), numberOfStars);
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
