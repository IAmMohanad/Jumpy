package com.jumpy.Characters;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.jumpy.Objects.Object;

public abstract class DynamicObject extends Object{//if any issues extend sprite

    protected float gravity = -250;//-175;
    protected int health;
    protected int movementSpeed;

    protected float velocityY;

    protected boolean grounded;
    protected boolean flip = false;
    protected boolean firstJump;
    protected boolean doubleJump;
    //TODO add object name e.g. gargoyle_flying, same as TiledMap name
    protected String name;
    protected TiledMap tiledMap;

    public abstract void moveLeft(float delta);

    public abstract void moveRight(float delta);

    public abstract void die();//float delta);

    public void updateGravity(float delta){
        //simulate gravity
        float newY = position.y;
        //how many pixels should be moved on Y axis. positive == moving up, negative == falling
        velocityY += gravity * delta;
        newY += velocityY * delta;
        if(map.doesRectCollideWithMap(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height)){
            //if colliding with map & falling down hit ground
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

    protected boolean collidesWithCollidableObject(float newX){
        //MapLayer tiledLayer = tiledMap.getLayers().get("collisions");
        //MapObjects objects = tiledLayer.getObjects();
        for(MapObject object : tiledMap.getLayers().get("collisions").getObjects()) {
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