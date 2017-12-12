package com.jumpy.Characters;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class Enemy extends DynamicObject {

    @Override
    public void moveLeft(float delta) {
        /*float newX = boundingBox.x - movementSpeed * delta;
        if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
            //moveLeft(delta);
            //left = false;
            position.x -= movementSpeed * delta;
        }*/
        position.x -= movementSpeed * delta;
    }

    @Override
    public void moveRight(float delta) {
        /*float newX = boundingBox.x + movementSpeed * delta;
        if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
            //moveRight(delta);
            //right = false;
            position.x += movementSpeed * delta;
        }*/
        position.x += movementSpeed * delta;
    }

    public void getsHit(int dmg){
        this.health -= dmg;
        if(health <= 0){
            this.die();
        }
    }
}
