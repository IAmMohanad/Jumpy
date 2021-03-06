package com.jumpy.Characters;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.jumpy.Objects.Object;
import com.jumpy.World.TileType;

public abstract class DynamicObject extends Object{

    protected float gravity = -250;
    protected int health;
    protected float movementSpeed;

    protected float velocityY;

    protected boolean grounded;
    protected boolean flip = false;
    protected boolean firstJump;
    protected boolean doubleJump;
    protected String name;
    protected TiledMap tiledMap;
    protected boolean deathComplete;
    protected Sound deathSound;

    public abstract void moveLeft(float delta);

    public abstract void moveRight(float delta);

    public abstract void die();//float delta);

    public void updateGravity(float delta){
        float newY = position.y;
        //how many pixels should be moved on Y axis. positive == moving up, negative == falling
        velocityY += gravity * delta;
        newY += velocityY * delta;
        if(map.doesRectCollideWithMap(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height)){
            //if colliding with map & falling down
            if(velocityY < 0){//falling downwards
                position.y = (float) Math.floor(position.y);
                grounded = true;
            }
            velocityY = 0;
            firstJump = false;
            doubleJump = false;
        } else{
            if(velocityY > 200){
                velocityY = 200;
            }
            if(newY < 0){
                newY = position.y;
            }
            position.y = newY;
            grounded = false;
        }
    }

    protected boolean collidesWithCollidable(float newX, float newY, float x, float y, int width, int height){
        for (int row = (int) newY / TileType.TILE_SIZE; row < Math.ceil((newY + height) / TileType.TILE_SIZE); row++) {
            for (int col = (int) newX / TileType.TILE_SIZE; col < Math.ceil((newX + width) / TileType.TILE_SIZE); col++) {
                TileType type = map.getTileTypeByCoordinate(1, col, row);
                if (type != null && type.isCollidable()) {
                    return true;
                }
            }
        }
        for (int col = (int) newX / TileType.TILE_SIZE; col < Math.ceil((newX + width) / TileType.TILE_SIZE); col++) {
            TileType type = map.getTileTypeByCoordinate(1, col - 1, ((int) newY / TileType.TILE_SIZE) - 1);
            if (type != null && type.isCollidable()) {
                return true;
            }
        }
        return false;
    }

    protected boolean collidesWithCollidableObject(float newX){
        for(MapObject object : tiledMap.getLayers().get("spawn").getObjects()) {
            if(object.getName().equals("collidable")){
                MapProperties objectProperties = object.getProperties();
                if(objectProperties.containsKey("all") || objectProperties.containsKey(this.name)){
                    Rectangle objectRect = ((RectangleMapObject) object).getRectangle();
                    float oldX = boundingBox.x;
                    boundingBox.setPosition(newX, boundingBox.y);
                    if(this.boundingBox.overlaps(objectRect)){
                        boundingBox.setPosition(oldX, boundingBox.y);
                        return true;

                    }
                }
            }
        }
        return false;
    }
}