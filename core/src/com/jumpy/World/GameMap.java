package com.jumpy.World;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.jumpy.Characters.Enemy;
import com.jumpy.Characters.Player;
import com.jumpy.Intersection;
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

    protected Hud hud;
    protected boolean isLevelComplete;

    //public int[] tiles = new int[25 * 15];
    public abstract void load(String location);
    public abstract void render(OrthographicCamera camera, SpriteBatch batch, float delta);
    //public abstract void update(float delta);
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
                // for (int layer = 1; layer < getLayers(); layer++) {
                TileType type = getTileTypeByCoordinate(1, col, row);
                if (type != null && type.isCollidable()) {
                    return true;
                }
                //}
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

    public abstract void cameraStop(OrthographicCamera camera);

    private Comparator<Node> nodeSorter = new Comparator<Node>(){
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
        Node current = new Node(start, null, 0, getDistance(start, goal));
        openList.add(current);

        while(openList.size() > 0){
            Collections.sort(openList, nodeSorter);//sort openList by lowest to highest fCost
            current = openList.get(0);
            //found path
            if(current.tile.equals(goal)){
                //reverse list, as goal is the first tile in closedList, should be last
                List<Node> path = new ArrayList<Node>();
                while(current.parent != null){
                    path.add(current);
                    current = current.parent;
                }
                openList.clear();
                closedList.clear();
                return path;
            }
            //checked tile. so remove from open and add to closed
            openList.remove(current);
            closedList.add(current);
            //check adjacent tiles to current tile
            for(int i = 0; i<9; i++){
                if(i == 4) continue; // skip current tile
                int x = (int) current.tile.x;
                int y = (int) current.tile.y;
                int xi = (i % 3) - 1;
                int yi = (i / 3) - 1;
                //tile being considered
                TileType at = getTileTypeByCoordinate(2, x + xi, y + yi);
                if(at == null) continue;
                if(at.isCollidable()) continue;
                //calculate costs of tile being considered
                Vector2 a = new Vector2(x + xi, y + yi);
                //cost of current tile to tile being considered
                double gCost = current.gCost + getDistance(current.tile, a);
                //cost of tile being considered to the goal
                double hCost = getDistance(a, goal);
                Node node = new Node(a, current, gCost, hCost);
                //has this tile already been added to closedList
                if(vecInList(closedList, a) && gCost >= node.gCost) continue;
                if(!vecInList(openList, a) || gCost < node.gCost) openList.add(node);
            }
        }
        closedList.clear();
        return null;
    }

    private boolean vecInList(List<Node> list, Vector2 vector){
        for(Node n : list){
            if(n.tile.equals(vector)) return true;
        }
        return false;
    }

    public void setHud(Hud hud){
        this.hud = hud;
    }

    private double getDistance(Vector2 tile, Vector2 goal){
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
