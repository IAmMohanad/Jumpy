package com.jumpy.Characters;

public abstract class Enemy extends DynamicObject {

    @Override
    public void moveLeft(float delta) {
        position.x -= movementSpeed * delta;
    }

    @Override
    public void moveRight(float delta) {
        position.x += movementSpeed * delta;
    }

    public void getsHit(int dmg){
        this.health -= dmg;
        if(health <= 0){
            this.die();
        }
    }
}
