package com.jumpy.Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Move;
import com.jumpy.World.GameMap;
import com.jumpy.Characters.Weapon;

public class Boomerang extends Weapon {

    private Animation<TextureRegion> animation;

    private int WIDTH = 24;
    private int HEIGHT = 12;

    private int B_BOX_WIDTH = 24;
    private int B_BOX_HEIGHT = 12;

    private int B_BOX_X_OFFSET = 0;
    private int B_BOX_Y_OFFSET = 0;

    private Move direction;
    private boolean flip;

    public Boomerang(GameMap map, float x, float y, Move direction){//REMOVE isDead ONLY THERE TO FORCE DEAD STATUS TO SHOW SUPERVISOR.
        width = WIDTH;
        height = HEIGHT;
        super.map = map;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        boundingBox.set(position.x, position.y, B_BOX_WIDTH, B_BOX_HEIGHT);
        movementSpeed = 160;
        movementLimit = 1000;
        this.direction = direction;
        active = true;

        damage = 1;

        create();
    }

    @Override
    public void create() {
        Texture textureSheet = new Texture(Gdx.files.internal("characters/player/weapon/fire_bolt.png"));

        TextureRegion[][] tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 1, textureSheet.getHeight());

        TextureRegion[] idleFrames = new TextureRegion[1];
        for(int i =0; i<1; i++){
            idleFrames[i] = tmp[0][i];
        }
        animation = new Animation<TextureRegion>(0.1f, idleFrames);
    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera) {
        if(active){
            this.stateTime += delta;
            currentFrame = animation.getKeyFrame(stateTime, true);

            if(direction == Move.RIGHT){
                float newX = boundingBox.x + movementSpeed * delta;
                if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                    position.x += movementSpeed * delta;
                } else{
                    die();
                }
                flip = false;
            } else if(direction == Move.LEFT) {
                float newX = boundingBox.x - movementSpeed * delta;
                if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                    position.x -= movementSpeed * delta;
                } else{
                    die();
                }
                flip = true;
            }

            boundingBox.setPosition(position.x + B_BOX_X_OFFSET, position.y + B_BOX_Y_OFFSET);
            updateBoundingBoxPicture(camera,(int) position.x + B_BOX_X_OFFSET, (int) position.y + B_BOX_Y_OFFSET);

            batch.begin();
            batch.draw(currentFrame, !flip ? position.x : position.x + width, position.y, !flip ? width : -width, height);
            batch.end();
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame) {
        batch.begin();
        batch.draw(currentFrame,position.x+ width, position.y);
        batch.end();
    }

    @Override
    public void dispose() {
        textureSheet.dispose();
    }

    @Override
    public void die() {
        active = false;
    }

    public void hit(){
        if(direction == Move.RIGHT){
            direction = Move.LEFT;
        } else if(direction == Move.LEFT){
            direction = Move.RIGHT;
        }
    }
}
