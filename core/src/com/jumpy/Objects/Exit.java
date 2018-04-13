package com.jumpy.Objects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Move;
import com.jumpy.World.GameMap;

public class Exit extends Object {

    public Exit(GameMap map, float x, float y, int width, int height){
        this.width = width;
        this.height = height;
        super.map = map;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        boundingBox.set(position.x, position.y , width, height);

        create();
    }

    @Override
    public void create() {

    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera) {
        updateBoundingBoxPicture(camera, (int) position.x, (int) position.y);
    }

    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void die() {

    }
}
