package com.jumpy.Objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Jumpy;
import com.jumpy.Move;
import com.jumpy.World.GameMap;

public class IceBall extends Object{
    private static int WIDTH = 32;
    private static int HEIGHT = 32;

    private static int B_BOX_UP_WIDTH = 4;
    private static int B_BOX_UP_HEIGHT = 13;

    private static int B_BOX_X_UP_OFFSET = 15;
    private static int B_BOX_Y_UP_OFFSET = 10;

    private static int B_BOX_RIGHT_WIDTH = 13;
    private static int B_BOX_RIGHT_HEIGHT = 4;

    private static int B_BOX_X_RIGHT_OFFSET = 10;
    private static int B_BOX_Y_RIGHT_OFFSET = 14;

    private Animation<TextureRegion> idleAnimation;

    private boolean flip;
    private boolean active;
    private Move direction;

    public IceBall(GameMap map, float x, float y, Move direction){
        width = WIDTH;
        height = HEIGHT;
        super.map = map;
        movementSpeed = 100;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        if(direction == Move.UP || direction == Move.DOWN){
            boundingBox.set(position.x + B_BOX_X_UP_OFFSET, position.y + B_BOX_Y_UP_OFFSET, B_BOX_UP_WIDTH, B_BOX_UP_HEIGHT);
        } else{
            boundingBox.set(position.x + B_BOX_X_RIGHT_OFFSET, position.y + B_BOX_Y_RIGHT_OFFSET, B_BOX_RIGHT_WIDTH, B_BOX_RIGHT_HEIGHT);
        }

        alpha = 1f;

        this.direction = direction;
        dead = false;
        active = true;
        create();
    }

    @Override
    public void create() {
        if(direction == Move.UP || direction == Move.DOWN){
            textureSheet = Jumpy.assetManager.get("characters/baddies/iceball_001_up.png", Texture.class);
        } else{//direction == Move.RIGHT or Move.LEFT
            textureSheet = Jumpy.assetManager.get("characters/baddies/iceball_001_right.png", Texture.class);
        }

        TextureRegion[][] tmp = TextureRegion.split(textureSheet, textureSheet.getWidth(), textureSheet.getHeight());

        TextureRegion[] idleFrames = new TextureRegion[1];
        for(int i =0; i<1; i++){
            idleFrames[i] = tmp[0][i];
        }
        idleAnimation = new Animation<TextureRegion>(1f, idleFrames);
    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera) {
        position.y += velocityY * delta;
        if(active) {
            this.stateTime += delta;
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);

            if (direction == Move.UP) {
                float newY = boundingBox.y + movementSpeed * delta;
                if (!map.collideWithMapEdges(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height))) {
                    position.y += movementSpeed * delta;
                } else {
                    die();
                }
                flip = false;
            } else if (direction == Move.DOWN) {
                float newY = boundingBox.y - movementSpeed * delta;
                if (!map.collideWithMapEdges(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height))) {
                    position.y -= movementSpeed * delta;
                } else {
                    die();
                }
                flip = true;
            } else if (direction == Move.RIGHT) {
                float newX = boundingBox.x + movementSpeed * delta;
                if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                    position.x += movementSpeed * delta;
                } else {
                    die();
                }
                flip = false;
            } else if (direction == Move.LEFT) {
                float newX = boundingBox.x - movementSpeed * delta;
                if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                    position.x -= movementSpeed * delta;
                } else {
                    die();
                }
                flip = true;
            }

            if (direction == Move.UP || direction == Move.UP){
                boundingBox.setPosition(position.x + B_BOX_X_UP_OFFSET, position.y + B_BOX_Y_UP_OFFSET);
                //updateBoundingBoxPicture(camera, (int) position.x + B_BOX_X_UP_OFFSET, (int) position.y + B_BOX_Y_UP_OFFSET);
            } else{
                boundingBox.setPosition(position.x + B_BOX_X_RIGHT_OFFSET, position.y + B_BOX_Y_RIGHT_OFFSET);
                //updateBoundingBoxPicture(camera, (int) position.x + B_BOX_X_RIGHT_OFFSET, (int) position.y + B_BOX_Y_RIGHT_OFFSET);
            }

            batch.begin();
            if(direction == Move.UP || direction == Move.DOWN){
                batch.draw(currentFrame, position.x, flip ? position.y + height : position.y, width, flip ? -height : height);
            } else{
                batch.draw(currentFrame, !flip ? position.x : position.x + width, position.y, !flip ? width : -width, height);
            }
            batch.end();
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
        active = false;
    }

    public boolean isActive(){
        return active;
    }
}
