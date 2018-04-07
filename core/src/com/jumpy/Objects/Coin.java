package com.jumpy.Objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.Hud;
import com.jumpy.World.GameMap;

public class Coin extends Object{

    private static int COIN_WIDTH = 30;
    private static int COIN_HEIGHT = 30;

    private static int B_BOX_WIDTH = 15;
    private static int B_BOX_HEIGHT = 30;

    private static int B_BOX_X_OFFSET = 10;
    private static int B_BOX_Y_OFFSET = 0;

    private static Hud hud;

    private Animation<TextureRegion> idleAnimation;

    public Coin(GameMap map, float x, float y){
        width = COIN_WIDTH;
        height = COIN_HEIGHT;
        super.map = map;
        velocityY = 75;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        boundingBox.set(position.x + B_BOX_X_OFFSET, position.y, B_BOX_WIDTH, B_BOX_HEIGHT);
        alpha = 1f;

        dead = false;
        create();
    }

    @Override
    public void create() {
        Texture textureSheet = Jumpy.assetManager.get("coin_animation/coin_animation.png", Texture.class);//new Texture(Gdx.files.internal("coin_animation/coin_animation.png"));

        TextureRegion[][] tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 6, textureSheet.getHeight());

        TextureRegion[] idleFrames = new TextureRegion[6];
        for(int i =0; i<6; i++){
            idleFrames[i] = tmp[0][i];
        }
        idleAnimation = new Animation<TextureRegion>(0.1f, idleFrames);
    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera) {
        this.stateTime += delta;
        currentFrame = idleAnimation.getKeyFrame(stateTime, true);

        boundingBox.setPosition(position.x + B_BOX_X_OFFSET, position.y + B_BOX_Y_OFFSET);
        updateBoundingBoxPicture(camera, (int) position.x + B_BOX_X_OFFSET, (int) getPosition().y + B_BOX_Y_OFFSET);
        batch.begin();
        if((dead) && (alpha > 0)){//decrease alpha to make the coin invisible over time
            position.y += velocityY * delta;
            alpha -= 2f * delta;
            this.setAlpha(alpha);
            Color c = this.getColor();
            batch.setColor(c.r, c.g, c.b, alpha);
            batch.draw(currentFrame, position.x, position.y);
            batch.setColor(c.r, c.g, c.b, 1f);
        } else if(alpha <= 0){
            //stop drawing the coin after it becomes invisible
        } else{
            batch.draw(currentFrame, position.x, position.y);
        }
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

    @Override
    public void die() {
        if(!dead) {
            dead = true;
            //alpha += (1f * delta) / 2
            //alpha -= 1 * delta;
            //this.setAlpha(alpha);
        }
    }

    public boolean alive(){
        return !this.dead;
    }

    public void setPosition(float delta, float x, float y){
       // boundingBox.setPosition(x, y);
        if(position.x < (int) x){
            position.x += 250 * delta;//TODO replace 250 with movement speed
        }
        if(position.x > (int) x){
            position.x -= 250 * delta;
        }
        if(position.y < (int) y){
            position.y += 250 * delta;
        }
        if(position.y > (int) y){
            position.y -= 250 * delta;
        }
    }
}
