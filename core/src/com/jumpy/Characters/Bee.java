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

    /*TODO
    You can use the method intersectRectangles provided in the Intersector class to determine if two rectangles are overlapping,
    and if so, where they overlap. You could use this info to determine if they overlap with the left, right, top, and/or bottom.

    Rectangle r1 = new rect
    Rectangle r2 = new rect;
    Rectangle intersection = new Rectangle();
    Intersector.intersectRectangles(r1, r2, intersection);//pass r1,r2 if they interlap, set overlap point to the intersection rect.
    if(intersection.x > r1.x)
        //Intersects with right side
    if(intersection.y > r1.y)
        //Intersects with top side
    if(intersection.x + intersection.width < r1.x + r1.width)
        //Intersects with left side
    if(intersection.y + intersection.height < r1.y + r1.height)
        //Intersects with bottom side
     */

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
        /*if((dead) && (alpha > 0)){
            position.y += velocityY * delta;
            alpha -= 2f * delta;
            this.setAlpha(alpha);
            Color c = this.getColor();
            batch.setColor(c.r, c.g, c.b, alpha);
            batch.draw(currentFrame, position.x, position.y);
            batch.setColor(c.r, c.g, c.b, 1f);
        } else if(alpha <= 0){
            //stop drawing the coin after it becomes invisible
        } else{*/
            batch.draw(currentFrame, position.x, position.y);
       // }
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
