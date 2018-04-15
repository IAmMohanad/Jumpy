package com.jumpy.World;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jumpy.Characters.Enemy;
import com.jumpy.Characters.Player;
import com.jumpy.Intersection;
import com.jumpy.Jumpy;
import com.jumpy.Objects.Coin;
import com.jumpy.Objects.Exit;
import com.jumpy.Objects.IceBallShooter;
import com.jumpy.Scenes.Hud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class GameMap {
    protected ShapeRenderer shapeRenderer = new ShapeRenderer();

    protected TiledMap map;
    protected OrthogonalTiledMapRenderer renderer;

    protected Jumpy game;
    protected Player player;
    protected ArrayList<Coin> coinList = new ArrayList<Coin>();
    protected ArrayList<Enemy> enemiesList = new ArrayList<Enemy>();
    protected ArrayList<IceBallShooter> projectileShootersList = new ArrayList<IceBallShooter>();
    protected ArrayList<Exit> exitList = new ArrayList<Exit>();

    protected Hud hud;
    protected boolean isLevelComplete;

    public abstract void load(String location);
    public abstract void render(OrthographicCamera camera, SpriteBatch batch, float delta);
    public abstract void dispose();
    /**
     * Gets a tile by pixel position whithin the game world at a specified layer.
     * @param layer
     * @param x
     * @param y
     * @return
     */
    public TileType getTileTypeByLocation(int layer, float x, float y){
        return this.getTileTypeByCoordinate(layer, (int) (x / TileType.TILE_SIZE), (int) (y / TileType.TILE_SIZE));
    }

    /**
     * Gets a tile at its coordinate within the map at a specified layer
     * @param layer
     * @param col
     * @param row
     * @return
     */
    public abstract TileType getTileTypeByCoordinate(int layer, int col, int row);

    public boolean doesRectCollideWithMap(float x, float y, int width, int height) {
        //check tiles from bottom left to top right of given bounding box, if the bounding box collides
        //with a collidable tile than a collision has occurred.
        for (int row = (int) y / TileType.TILE_SIZE; row < Math.ceil((y + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) x / TileType.TILE_SIZE; col < Math.ceil((x + width) / TileType.TILE_SIZE); col++) {
                TileType type = getTileTypeByCoordinate(1, col, row);
                if (type != null && type.isCollidable()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean collideWithMapEdges(float x, float y, int width, int height){
        if (x < 0 || y < 0 || x + width > getPixelWidth() || y + height > getPixelHeight()) {
            return true;
        }
        return false;
    }

    public abstract int getWidth();
    public abstract int getHeight();

    public abstract int getPixelWidth();
    public abstract int getPixelHeight();

    public abstract int getLayers();

    //snaps camera to player
    public abstract void cameraStop(OrthographicCamera camera);

    private Comparator<Node> sortNodes = new Comparator<Node>(){
        @Override
        public int compare(Node n0, Node n1) {
            if(n1.fCost < n0.fCost) return +1;
            if(n1.fCost > n0.fCost) return -1;
            return 0;
        }
    };

    public List<Node> findPath(Vector2 start, Vector2 goal){
        List<Node> openList = new ArrayList<Node>();
        List<Node> closedList = new ArrayList<Node>();
        Node currentNode = new Node(start, null, 0, getEuclideanDistance(start, goal));
        openList.add(currentNode);

        while(openList.size() > 0){
            Collections.sort(openList, sortNodes);//sort openList by lowest to highest fCost
            currentNode = openList.get(0);
            //found path
            if(currentNode.tile.equals(goal)){
                //reverse list, as goal is the first tile in closedList, should be last
                List<Node> ShortestPath = new ArrayList<Node>();
                while(currentNode.parent != null){
                    ShortestPath.add(currentNode);
                    currentNode = currentNode.parent;
                }
                openList.clear();
                closedList.clear();
                return ShortestPath;
            }
            //checked tile. so remove from open and add to closed
            openList.remove(currentNode);
            closedList.add(currentNode);
            //check tiles surrounding to current tile
            for(int i = 0; i<9; i++){
                if(i == 4) {
                    // skip current tile
                    continue;
                }
                int currentTileX = (int) currentNode.tile.x;
                int currentTileY = (int) currentNode.tile.y;
                int offsetX = (i % 3) - 1;
                int offsetY = (i / 3) - 1;
                //tile being considered
                TileType at = getTileTypeByCoordinate(2, currentTileX + offsetX, currentTileY + offsetY);
                if(at == null){
                    continue;
                }
                if(at.isCollidable()){
                    continue;
                }

                //calculate costs of tile being considered
                Vector2 consideredTile = new Vector2(currentTileX + offsetX, currentTileY + offsetY);
                //cost from current tile to tile being considered
                double gCost = currentNode.gCost + getEuclideanDistance(currentNode.tile, consideredTile);
                //cost from tile being considered to the goal
                double hCost = getEuclideanDistance(consideredTile, goal);
                Node consideredNode = new Node(consideredTile, currentNode, gCost, hCost);
                //has this tile already been added to closedList
                if(nodeInList(closedList, consideredTile) && gCost >= consideredNode.gCost) {
                    continue;
                }
                if(!nodeInList(openList, consideredTile) || gCost < consideredNode.gCost) {
                    openList.add(consideredNode);
                }
            }
        }
        closedList.clear();
        return null;
    }

    private boolean nodeInList(List<Node> list, Vector2 vector){
        for(Node n : list){
            if(n.tile.equals(vector)) {
                return true;
            }
        }
        return false;
    }

    public void setHud(Hud hud){
        this.hud = hud;
    }

    private double getEuclideanDistance(Vector2 tile, Vector2 goal){
        double dx = (int) tile.x - (int) goal.x;
        double dy = (int) tile.y - (int) goal.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public abstract ArrayList<Coin> getCoins();
    public abstract ArrayList<Enemy> getEnemies();
    public abstract Player getPlayer();
    public abstract ArrayList<IceBallShooter> getProjectileShootersList();
    public abstract ArrayList<Exit> getExits();
    public abstract void setExitReached(boolean status);
    public boolean getIsLevelComplete(){
        return isLevelComplete;
    }
}
