package com.jumpy.Objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Move;
import com.jumpy.World.GameMap;
import java.util.ArrayList;
import java.util.Iterator;

public class IceBallShooter extends Object{

    private ArrayList<IceBall> iceBallList;
    private String name;
    private TiledMap tiledMap;
    private float timer;
    private int shootInterval;
    private Move direction;

    private boolean canShootProjectiles = true;

    public IceBallShooter(TiledMap tiledMap, GameMap map, float x, float y, Move direction){
        this(tiledMap, map, x, y, direction, 1);
    }

    public IceBallShooter(TiledMap tiledMap, GameMap map, float x, float y, Move direction, int shootInterval){
        this.name = "ice_ball_shooter";
        this.tiledMap = tiledMap;
        width = 10;
        height = 10;
        super.map = map;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        this.shootInterval = shootInterval;
        this.direction = direction;
        iceBallList = new ArrayList<IceBall>();
    }

    public boolean canShootProjectiles(){
        return canShootProjectiles;
    }

    @Override
    public void create() {
    }

    private void shoot(){
        iceBallList.add(new IceBall(map, position.x, position.y, direction));
    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera) {
        timer += delta;
        if(timer >= shootInterval){
            timer = 0;
            shoot();
        }
        if(iceBallList.size() > 0 ){
            for (Iterator<IceBall> iterator = iceBallList.iterator(); iterator.hasNext();){
                IceBall iceBall = iterator.next();
                iceBall.update(batch, delta, camera);
                if(!iceBall.isActive()){
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame) {

    }

    @Override
    public void dispose() {
        textureSheet.dispose();
    }

    @Override
    public void die() {
    }

    public ArrayList<IceBall> getIceBallList() {
        return iceBallList;
    }
}
