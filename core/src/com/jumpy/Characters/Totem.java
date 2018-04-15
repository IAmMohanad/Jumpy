package com.jumpy.Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Jumpy;
import com.jumpy.Move;
import com.jumpy.Scenes.Hud;
import com.jumpy.World.GameMap;

public class Totem extends Enemy {

    private final int TOTEM_WIDTH = 32;
    private final int TOTEM_HEIGHT = 32;

    private final int B_BOX_WIDTH = 20;
    private final int B_BOX_HEIGHT = 20;

    private final int B_BOX_X_OFFSET = 5;
    private final int B_BOX_Y_OFFSET = 0;

    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> dieAnimation;

    private Move direction;
    //private boolean deathComplete = false;

    public Totem(TiledMap tiledMap, GameMap map, float x, float y){
        this.tiledMap = tiledMap;
        this.name = "totem";
        health = 1;
        width = TOTEM_WIDTH;
        height = TOTEM_HEIGHT;
        super.map = map;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        boundingBox.set(position.x + B_BOX_X_OFFSET, position.y + B_BOX_Y_OFFSET, B_BOX_WIDTH, B_BOX_HEIGHT);
        movementSpeed = 100;
        velocityY = 0;
        grounded = false;
        dead = false;
        deathComplete = false;
        deathSound = Jumpy.assetManager.get("sound/goblin_death.wav", Sound.class);

        create();
    }

    @Override
    public void create() {
        Texture textureSheet = Jumpy.assetManager.get("characters/baddies/totem/totem_walk.png", Texture.class);//new Texture(Gdx.files.internal("characters/baddies/totem/totem_walk.png"));
        TextureRegion[][] tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 7, textureSheet.getHeight());

        TextureRegion[] walkFrames = new TextureRegion[7];
        for(int i=0; i<walkFrames.length; i++){
            walkFrames[i] = tmp[0][i];
        }
        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);

        textureSheet = Jumpy.assetManager.get("characters/baddies/totem/totem_die.png", Texture.class);//new Texture(Gdx.files.internal("characters/baddies/totem/totem_die.png"));
        tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 7, textureSheet.getHeight());
        TextureRegion[] dieFrames = new TextureRegion[7];
        for(int i=0; i<dieFrames.length; i++){
            dieFrames[i] = tmp[0][i];
        }
        dieAnimation = new Animation<TextureRegion>(0.2f, dieFrames);

        if(MathUtils.random(10) > 5){
            direction = Move.RIGHT;
        } else{
            direction = Move.LEFT;
        }
    }


    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera) {
        this.stateTime += delta;
        updateGravity(delta);

        if(!dead){
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
            if(direction == Move.RIGHT){
                float newX = boundingBox.x + movementSpeed * delta;
                if (collidesWithCollidableObject(newX) || map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) || (map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                    direction = Move.LEFT;
                }
            } else if(direction == Move.LEFT){
                float newX = boundingBox.x - movementSpeed * delta;
                if (collidesWithCollidableObject(newX) || map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) || (map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                    direction = Move.RIGHT;
                }
            }

            //if doesn't collide with an object keep moving
            if(direction == Move.RIGHT){
                float newX = boundingBox.x + movementSpeed * delta;
                if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                    position.x += movementSpeed * delta;
                }
                flip = false;
            } else if(direction == Move.LEFT) {
                float newX = boundingBox.x - movementSpeed * delta;
                if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                    position.x -= movementSpeed * delta;
                }
                flip = true;
            }
        } else{//dead
            if(direction == Move.LEFT){
                flip = true;
            } else{
                flip = false;
            }
            currentFrame = dieAnimation.getKeyFrame(stateTime, false);
        }

        if(dead && dieAnimation.isAnimationFinished(delta)){
            deathComplete = true;
        }

        boundingBox.setPosition(position.x + B_BOX_X_OFFSET, position.y + B_BOX_Y_OFFSET);
        updateBoundingBoxPicture(camera,(int) position.x + B_BOX_X_OFFSET, (int) position.y + B_BOX_Y_OFFSET);
        if(!deathComplete){//!dieAnimation.isAnimationFinished(stateTime) || !dead){
            batch.begin();
            batch.draw(currentFrame, !flip ? position.x : position.x + width, position.y, !flip ? width : -width, height);
            batch.end();
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame) {
        batch.begin();
        batch.draw(currentFrame, !flip ? position.x : position.x + width, position.y, !flip ? width : -width, height);
        batch.end();
    }

    @Override
    public void dispose() {
        textureSheet.dispose();
    }

    public void getsHit(int dmg){
        health -= dmg;
        if(health <= 0 && !dead){
            die();
        }
    }

    @Override
    public void die() {
        if(health <= 0 && !dead){
            dead = true;
            deathSound.play(Jumpy.volume);
            stateTime = 0f;
        }
    }

    public void updateGravity(float delta){
        //simulate gravity
        float newY = position.y;
        velocityY += gravity * delta;
        newY += velocityY * delta;
        if(newY < 0){
            newY = position.y;
        }
        if(map.doesRectCollideWithMap(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height)){
            if(velocityY < 0){//falling downwards
                position.y = (float) Math.floor(position.y);
                grounded = true;
            }
            firstJump = false;
            doubleJump = false;
            velocityY = 0;
        } else{//moving upwards
            if(velocityY > 200){
                velocityY = 200;
            }
            position.y = newY;
            grounded = false;
        }
    }
}
