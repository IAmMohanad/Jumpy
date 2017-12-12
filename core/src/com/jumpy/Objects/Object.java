package com.jumpy.Objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Intersection;
import com.jumpy.World.GameMap;

public abstract class Object extends Sprite {
    protected float velocityY;
    protected int movementSpeed;
    protected Texture textureSheet;
    protected int width;
    protected int height;
    protected float stateTime;
    protected Vector2 position;
    protected Rectangle boundingBox;
    protected GameMap map;
    protected boolean dead;
    protected float alpha;
    protected TextureRegion currentFrame;

    protected ShapeRenderer shapeRenderer = new ShapeRenderer();

    public abstract void create();

    public abstract void update(SpriteBatch batch, float delta, OrthographicCamera camera);//camera only there to update boundingBoxPicture, can be removed

    public abstract void render(SpriteBatch batch, float delta, TextureRegion currentFrame);

    public abstract void dispose();

    public abstract void die();

    public void updateBoundingBoxPicture(OrthographicCamera camera, int x, int y){
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.rect(x, y, boundingBox.width, boundingBox.height);//getPosition().x + 5, getPosition().y, boundingBox.width, boundingBox.height);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.line(x, y, x+ boundingBox.width, y);
        shapeRenderer.end();
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public Intersection intersectsAt(OrthographicCamera camera, Rectangle r1, Rectangle r2){
        Rectangle intersection = new Rectangle();
        Intersector.intersectRectangles(r1, r2, intersection);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //shapeRenderer.setColor(1, 1, 0, 1);
        //shapeRenderer.rect(x, y, boundingBox.width, boundingBox.height);//getPosition().x + 5, getPosition().y, boundingBox.width, boundingBox.height);
        shapeRenderer.setColor(0, 0, 0, 1);

        if(intersection.y > r1.y) {
            //Intersects with top side
            shapeRenderer.line(intersection.x, intersection.y, intersection.x + intersection.width, intersection.y);
            shapeRenderer.end();
            return Intersection.TOP;
        }
        else if(intersection.x + intersection.width < r1.x + r1.width) {
            //Intersects with left side
            shapeRenderer.line(intersection.x, intersection.y, intersection.x + intersection.width, intersection.y);
            shapeRenderer.end();
            return Intersection.LEFT;
        }
        else if(intersection.y + intersection.height < r1.y + r1.height) {
            shapeRenderer.line(intersection.x, intersection.y, intersection.x + intersection.width, intersection.y);
            shapeRenderer.end();
            //Intersects with bottom side
            return Intersection.BOTTOM;
        } else
        if(intersection.x > r1.x) {
            //Intersects with right side
            shapeRenderer.line(intersection.x, intersection.y, intersection.x + intersection.width, intersection.y);
            shapeRenderer.end();
            return Intersection.RIGHT;
        } else{
            return Intersection.NONE;
        }
    }

    public boolean isAlive(){
        return !dead;
    }
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
}
