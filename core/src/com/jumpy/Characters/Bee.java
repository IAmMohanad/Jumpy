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
import com.jumpy.World.GameMap;

public class Bee extends Enemy {

    private static int BEE_WIDTH = 30;
    private static int BEE_HEIGHT = 30;

    private static int B_BOX_WIDTH = 40;
    private static int B_BOX_HEIGHT = 30;

    private static int B_BOX_X_OFFSET = 8;
    private static int B_BOX_Y_OFFSET = 8;

    private Animation<TextureRegion> flyAnimation;
    private Animation<TextureRegion> hitAnimation;

    public Bee(GameMap map, float x, float y){
        width = BEE_WIDTH;
        height = BEE_HEIGHT;
        this.map = map;

        velocityY = 75;
        movementSpeed = 150;

        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        boundingBox.set(position.x + B_BOX_X_OFFSET, position.y + B_BOX_Y_OFFSET, B_BOX_WIDTH, B_BOX_HEIGHT);

        alpha = 1f;
        dead = false;

        create();
    }

    @Override
    public void create() {
        Texture textureSheet = new Texture(Gdx.files.internal("characters/baddies/bee/bee_sheet.png"));
        TextureRegion[][] tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 4, textureSheet.getHeight());

        TextureRegion[] flyFrames = new TextureRegion[2];
        flyFrames[0] = tmp[0][0];
        flyFrames[1] = tmp[0][2];

        flyAnimation = new Animation<TextureRegion>(0.2f, flyFrames);

        TextureRegion[] hitFrames = new TextureRegion[1];
        hitFrames[0] = tmp[0][0];

        hitAnimation = new Animation<TextureRegion>(0.3f, hitFrames);
    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera) {
        this.stateTime += delta;
        currentFrame = flyAnimation.getKeyFrame(stateTime, true);

        boundingBox.setPosition(position.x + B_BOX_X_OFFSET, position.y + B_BOX_Y_OFFSET);
        updateBoundingBoxPicture(camera, (int) position.x + B_BOX_X_OFFSET, (int) getPosition().y + B_BOX_Y_OFFSET);
        batch.begin();
        batch.draw(currentFrame, position.x, position.y);
        batch.end();
    }

    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame) {
        batch.begin();
        batch.draw(currentFrame, position.x, position.y);
        batch.end();
    }

    @Override
    public void dispose() {
        textureSheet.dispose();
    }

    public void moveUp(float delta) {
        position.y += movementSpeed * delta;
    }
    public void moveDown(float delta) {
        position.y -= movementSpeed * delta;
    }

    /*@Override
    public void moveLeft(float delta) {
        position.x -= movementSpeed * delta;
    }*/

    /*@Override
    public void moveRight(float delta) {
        position.x += movementSpeed * delta;
    }*/

    @Override
    public void die() {

    }
}
