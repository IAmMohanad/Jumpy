package com.jumpy.Objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Characters.Enemy;
import com.jumpy.Jumpy;
import com.jumpy.Move;
import com.jumpy.World.GameMap;

public class Spike extends Enemy {
    private static int WIDTH = 32;
    private static int HEIGHT = 32;

    private static int B_BOX_WIDTH = 8;
    private static int B_BOX_HEIGHT = 25;

    private static int B_BOX_X_UP_OFFSET = 12;
    private static int B_BOX_Y_UP_OFFSET = 0;

    private static int B_BOX_X_DOWN_OFFSET = 12;
    private static int B_BOX_Y_DOWN_OFFSET = 7;

    private Animation<TextureRegion> idleAnimation;

    private Move direction;

    public Spike(GameMap map, float x, float y, Move direction){
        width = WIDTH;
        height = HEIGHT;
        super.map = map;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        if(direction == Move.UP){
            boundingBox.set(position.x + B_BOX_X_UP_OFFSET, position.y + B_BOX_Y_UP_OFFSET, B_BOX_WIDTH, B_BOX_HEIGHT);
        } else{
            boundingBox.set(position.x + B_BOX_X_DOWN_OFFSET, position.y + B_BOX_Y_DOWN_OFFSET, B_BOX_WIDTH, B_BOX_HEIGHT);
        }
        this.direction = direction;
        create();
    }

    @Override
    public void create() {
        if(direction == Move.UP){
            textureSheet = Jumpy.assetManager.get("characters/baddies/Spike_Up.png", Texture.class);
        } else{
            textureSheet = Jumpy.assetManager.get("characters/baddies/Spike_Down.png", Texture.class);
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
        this.stateTime += delta;
        currentFrame = idleAnimation.getKeyFrame(stateTime, true);

        batch.begin();
        batch.draw(currentFrame, position.x,position.y, width, height);
        batch.end();

    }

    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame) {

    }

    @Override
    public void dispose() { textureSheet.dispose();

    }

    @Override
    public void die() {
        //doesnt die
    }
}
