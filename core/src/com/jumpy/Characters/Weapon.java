package com.jumpy.Characters;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Objects.Object;
import com.jumpy.World.GameMap;

public abstract class Weapon extends Object {
    protected boolean active;
    protected float timeOut;
    protected int ammo;
    protected int speed;
    protected int damage;
    protected int movementLimit;

    protected static int WIDTH;
    protected static int HEIGHT;

    protected static int B_BOX_WIDTH;
    protected static int B_BOX_HEIGHT;

    protected static int B_BOX_X_OFFSET;
    protected static int B_BOX_Y_OFFSET;

    public int getDamage(){
        return damage;
    }
}
