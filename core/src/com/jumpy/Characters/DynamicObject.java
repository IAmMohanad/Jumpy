package com.jumpy.Characters;

import com.jumpy.Objects.Object;

public abstract class DynamicObject extends Object{//if any issues extend sprite

    protected final float gravity = -250;//-175;
    protected int health;
    protected int movementSpeed;

    protected float velocityY;

    protected boolean grounded;
    protected boolean flip = false;
    protected boolean firstJump;
    protected boolean doubleJump;

    public abstract void moveLeft(float delta);

    public abstract void moveRight(float delta);

    public abstract void die();//float delta);

    public void updateGravity(float delta){
        //simulate gravity
        float newY = position.y;
        velocityY += gravity * delta;
        newY += velocityY * delta;
        if(map.doesRectCollideWithMap(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height)){
            if(velocityY < 0){//falling downwards
                position.y = (float) Math.floor(position.y);
                grounded = true;
            }
            velocityY = 0;
            firstJump = false;
            doubleJump = false;
        } else{//moving upwards
            if(velocityY > 200){
                velocityY = 200;
            }
            position.y = newY;
            grounded = false;
        }
    }
}