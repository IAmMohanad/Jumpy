package com.jumpy.Characters;

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
import com.jumpy.World.GameMap;

public class Goblin extends Enemy {

    private final int GOBLIN_WIDTH = 32;
    private final int GOBLIN_HEIGHT = 32;

    private final int B_BOX_WIDTH = 12;
    private final int B_BOX_HEIGHT = 29;

    private final int B_BOX_X_OFFSET = 11;
    private final int B_BOX_Y_OFFSET = 0;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> dieAnimation;
    private Animation<TextureRegion> hitAnimation;

    private Move direction;
    private boolean deathComplete = false;

    public Goblin(TiledMap tiledMap, GameMap map, float x, float y){
        this.name = "goblin";
        this.tiledMap = tiledMap;
        health = 1;
        width = GOBLIN_WIDTH;
        height = GOBLIN_HEIGHT;
        super.map = map;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        boundingBox.set(position.x + B_BOX_X_OFFSET, position.y + B_BOX_Y_OFFSET, B_BOX_WIDTH, B_BOX_HEIGHT);
        movementSpeed = 175;
        velocityY = 0;
        grounded = false;
        dead = false;
        deathSound = Jumpy.assetManager.get("sound/goblin_death.wav", Sound.class);
        create();
    }

    @Override
    public void create() {
        //idle
        Texture textureSheet = Jumpy.assetManager.get("characters/baddies/goblin_32_32/goblin_idle_trimmed.png", Texture.class);
        TextureRegion[][] tmp = TextureRegion.split(textureSheet, textureSheet.getWidth(), textureSheet.getHeight());

        TextureRegion[] idleFrames = new TextureRegion[1];
        idleFrames[0] = tmp[0][0];
        idleAnimation = new Animation<TextureRegion>(0.1f, idleFrames);

        //walk
        textureSheet = Jumpy.assetManager.get("characters/baddies/goblin_32_32/goblin_walk_trimmed.png", Texture.class);
        tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 8, textureSheet.getHeight());
        TextureRegion[] walkFrames = new TextureRegion[8];
        for(int i=0; i<walkFrames.length; i++){
            walkFrames[i] = tmp[0][i];
        }
        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);

        //run
        textureSheet = Jumpy.assetManager.get("characters/baddies/goblin_32_32/goblin_run_trimmed.png", Texture.class);
        tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 8, textureSheet.getHeight());
        TextureRegion[] runFrames = new TextureRegion[8];
        for(int i=0; i<runFrames.length; i++){
            runFrames[i] = tmp[0][i];
        }
        runAnimation = new Animation<TextureRegion>(0.1f, runFrames);

        //die
        textureSheet = Jumpy.assetManager.get("characters/baddies/goblin_32_32/goblin_die_trimmed.png", Texture.class);
        tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 5, textureSheet.getHeight());
        TextureRegion[] dieFrames = new TextureRegion[5];
        for(int i=0; i<dieFrames.length; i++){
            dieFrames[i] = tmp[0][i];
        }
        dieAnimation = new Animation<TextureRegion>(0.2f, dieFrames);

        //hit
        textureSheet = Jumpy.assetManager.get("characters/baddies/goblin_32_32/goblin_hit_trimmed.png", Texture.class);
        tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 2, textureSheet.getHeight());
        TextureRegion[] hitFrames = new TextureRegion[2];
        for(int i=0; i<hitFrames.length; i++){
            hitFrames[i] = tmp[0][i];
        }
        hitAnimation = new Animation<TextureRegion>(0.2f, hitFrames);

        if(MathUtils.random(10) > 5){
            direction = Move.RIGHT;
        } else{
            direction = Move.LEFT;
        }
    }

    private void isPlayerNearby(Player player){
        Move currentDirection = direction;
        if((Math.abs(position.x - player.getBoundingBox().x) < 50) && (Math.abs(position.y - player.getBoundingBox().y)) < 50){
            if(position.x - player.getBoundingBox().x < 0){
                direction = Move.RIGHT;
            } else{
                direction = Move.LEFT;
            }
        } else{
            if(direction != currentDirection){
                direction = currentDirection;
            }
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
                if (collidesWithCollidableObject(newX) || map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) || (map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))){
                    direction = Move.LEFT;
                }
            } else if(direction == Move.LEFT){
                float newX = boundingBox.x - movementSpeed * delta;
                if (collidesWithCollidableObject(newX) || map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) || (map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))){
                    direction = Move.RIGHT;
                }
            }

            if(direction == Move.RIGHT){
                float newX = boundingBox.x + movementSpeed * delta;
                if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))){
                    position.x += movementSpeed * delta;
                }
                flip = false;
            } else if(direction == Move.LEFT) {
                float newX = boundingBox.x - movementSpeed * delta;
                if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))){
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

        if(dead && dieAnimation.isAnimationFinished(stateTime)){
            deathComplete = true;
        }

        boundingBox.setPosition(position.x + B_BOX_X_OFFSET, position.y + B_BOX_Y_OFFSET);
        //updateBoundingBoxPicture(camera,(int) position.x + B_BOX_X_OFFSET, (int) position.y + B_BOX_Y_OFFSET);
        if(!deathComplete){
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
        if(health <= 0){
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
