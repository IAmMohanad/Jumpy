package com.jumpy.Characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.World.GameMap;
import com.jumpy.World.Node;
import com.jumpy.World.TileType;

import java.util.List;

public class Chaser extends Enemy {
    private Animation<TextureRegion> moveAnimation;
    private Animation<TextureRegion> dissipateAnimation;

    private static int B_BOX_WIDTH = 10;
    private static int B_BOX_HEIGHT = 10;
    private static int B_BOX_OFFSET_X = 3;
    private static int B_BOX_OFFSET_Y = 3;
    private Player player;

    private TextureRegion currentFrame;

    private List<Node> path = null;
    private int time = 0;

    public Chaser(GameMap map, Player player, float x, float y, int width, int height){
        this.map = map;
        this.player = player;
        position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        this.movementSpeed = 50;

        boundingBox = new Rectangle();
        boundingBox.set(position.x, position.y, B_BOX_WIDTH, B_BOX_HEIGHT);

        create();
    }


    @Override
    public void create() {
        Texture textureSheet = new Texture(Gdx.files.internal("chaser_updated.png"));

        TextureRegion[][] tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 9, textureSheet.getHeight() / 1);
        TextureRegion[] walkFrames = new TextureRegion[2];
        walkFrames[0] = tmp[0][3];
        walkFrames[1] = tmp[0][4];
        moveAnimation = new Animation<TextureRegion>(0.2f, walkFrames);

        TextureRegion[] dissipateFrames = new TextureRegion[3];
        dissipateFrames[0] = tmp[0][5];
        dissipateFrames[1] = tmp[0][6];
        dissipateFrames[2] = tmp[0][7];

        dissipateAnimation = new Animation<TextureRegion>(1f, dissipateFrames);
    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera) {
        stateTime += delta;

        if(player.isAlive()){
            int px = (int) player.getBoundingBox().x + (int) (player.getBoundingBox().width / 2);
            int py = (int) player.getBoundingBox().y + (int) (player.getBoundingBox().height / 2);

            if(position.x < px){
                moveRight(delta);
            }
            if(position.x > px){
                moveLeft(delta);
            }
            if(position.y < py){
                position.y += movementSpeed * delta;
            }
            if(position.y > py){
                position.y -= movementSpeed * delta;
            }
        }


        //begin astar
        //time++;
        /*Vector2 start = new Vector2((int) (position.x / TileType.TILE_SIZE), (int)(position.y / TileType.TILE_SIZE));
        Vector2 destination = new Vector2( (px / TileType.TILE_SIZE), (py / TileType.TILE_SIZE));
        if(time % 60 == 0){
            path = map.findPath(start, destination);
            System.out.println(path.size() + "   x:" + path.get(path.size() - 1).tile.x+"     y:"+path.get(path.size() - 1).tile.y);
        }
        if(path != null){
            if(path.size() > 0){
                //path is stored in reverse order
                Vector2 vec = path.get(path.size() - 1).tile;
                if(position.x < (vec.x * 32)) {
                    position.x += movementSpeed * delta;
                } else if(position.x > (vec.x * 32)){
                    position.x -= movementSpeed * delta;
                }
                if(position.y < (vec.y * 32)){
                    position.y += movementSpeed * delta;
                } else if(position.x > (vec.x * 32)){
                    position.y -= movementSpeed * delta;
                }
            }
        } else{
           // System.out.println("there is no path");
        }
        //  System.out.println("start:"+start.getX() +  "  " + start.getY());
        //  System.out.println("destination: "+destination.getX() +  "  " + destination.getY());
        //end astar*/



        if(moveAnimation.isAnimationFinished(stateTime)){
            stateTime = 0;
        }
        currentFrame = moveAnimation.getKeyFrame(stateTime, false);

        boundingBox.setPosition(position.x, position.y);
        updateBoundingBoxPicture(camera, (int) getPosition().x + B_BOX_OFFSET_X, (int) getPosition().y + B_BOX_OFFSET_Y);

        batch.begin();
        batch.draw(currentFrame, position.x, position.y);
        batch.end();
        //render();

    }

    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame){//camera only there to update boundingBoxPicture, can be removed
        batch.begin();
        batch.draw(currentFrame, position.x, position.y);
        batch.end();
    }

    @Override
    public void dispose() {
        textureSheet.dispose();
    }

    @Override
    public void moveLeft(float delta) {
        position.x -= movementSpeed * delta;
    }

    @Override
    public void moveRight(float delta) {
        position.x += movementSpeed * delta;
    }

    @Override
    public void die(){

    }
}
