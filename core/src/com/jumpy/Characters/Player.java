package com.jumpy.Characters;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.Active;
import com.jumpy.Intersection;
import com.jumpy.Move;
import com.jumpy.Objects.Coin;
import com.jumpy.Screens.PlayScreen;
import com.jumpy.World.GameMap;
import com.jumpy.Characters.Weapon;
import com.jumpy.Active;

import java.util.ArrayList;

public class Player extends DynamicObject {

    private int coinsCollected;
    private int points;
    //private Hud hud;
    private PlayScreen playScreen;
    public static boolean left;
    public static boolean right;
    public static boolean up;
    public static boolean down;
    public static boolean shootPressed;
    public static boolean boostPressed;
    private Weapon equippedWeapon;
    private int boostTimer;
    private int boostMaxTime;

    private boolean doubleJump;
    private boolean firstJump;

    private boolean deathComplete = false;

    private boolean dead;

    //private final float gravity = -100;

    private static final int JUMP_VELOCITY = 130;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> fallAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> spinJumpAnimation;

    private TextureRegion currentFrame;

    private float timeSinceLastJump = 0;

    private int BBOX_X_OFFSET = 5;
    private int BBOX_Y_OFFSET = 1;

    private Active weapon;
    private ArrayList<Weapon> weaponList = new ArrayList<Weapon>();

    private boolean deleteMe = true; //just used to force only one shot to be created. delete after demo.
    private float shootCounter = 0;

    public Player(Active weapon, GameMap map, float x, float y, PlayScreen playScreen){
        this.playScreen = playScreen;
        //this.hud = h;
        super.health = 1;
        super.width = 32;
        super.height = 64;
        super.map = map;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        boundingBox.set(position.x + BBOX_X_OFFSET, position.y + BBOX_Y_OFFSET, 20, 40);
        movementSpeed = 150;
        velocityY = 0;
        grounded = false;
        firstJump = false;
        doubleJump = false;
        dead = false;
        this.weapon = weapon;
        points = 0;
        coinsCollected = 0;

        create();
    }

    public Player(GameMap map, float x, float y){
        this(null, map, x, y, null);
    }

    public void weaponShot(){
        shootPressed = false;
        if(weapon == Active.LASER){
            weaponList.add(new Boomerang(map, this.position.x, this.position.y, flip ? Move.LEFT : Move.RIGHT));
        }
        if(weapon == Active.NONE){
            weaponList.add(new Boomerang(map, this.position.x, this.position.y, flip ? Move.LEFT : Move.RIGHT));
        }
    }

    @Override
    public void create(){
        textureSheet = new Texture(Gdx.files.internal("characters/player/idle.png"));
        TextureRegion[][] tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 1,
                textureSheet.getHeight() / 1);
        TextureRegion[] idleFrames = new TextureRegion[1];
        idleFrames[0] = tmp[0][0];
        idleAnimation = new Animation<TextureRegion>(0.5f, idleFrames);


        textureSheet = new Texture(Gdx.files.internal("characters/player/run.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 6,
                textureSheet.getHeight() / 1);
        TextureRegion[] walkFrames = new TextureRegion[6];
        for(int i =0; i<6; i++){
            walkFrames[i] = tmp[0][i];
        }
        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);

        textureSheet = new Texture(Gdx.files.internal("characters/player/jump.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 3,
                textureSheet.getHeight() / 1);
        TextureRegion[] jumpFrames = new TextureRegion[3];
        for(int i =0; i<3; i++){
            jumpFrames[i] = tmp[0][i];
        }
        jumpAnimation = new Animation<TextureRegion>(0.2f, jumpFrames);

        textureSheet = new Texture(Gdx.files.internal("characters/player/fall.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 3,
                textureSheet.getHeight() / 1);
        TextureRegion[] fallFrames = new TextureRegion[3];
        for(int i =0; i<3; i++){
            fallFrames[i] = tmp[0][i];
        }
        fallAnimation = new Animation<TextureRegion>(0.2f, fallFrames);

        textureSheet = new Texture(Gdx.files.internal("characters/player/down.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 1,
                textureSheet.getHeight() / 1);
        TextureRegion[] downFrames = new TextureRegion[1];
        downFrames[0] = tmp[0][0];
        downAnimation = new Animation<TextureRegion>(0.5f, downFrames);

        textureSheet = new Texture(Gdx.files.internal("characters/player/spinJump_fixed.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 10,
                textureSheet.getHeight() / 1);
        TextureRegion[] spinJumpFrames = new TextureRegion[10];
        spinJumpFrames[0] = tmp[0][0];
        spinJumpFrames[1] = tmp[0][1];
        spinJumpFrames[2] = tmp[0][2];
        spinJumpFrames[3] = tmp[0][3];
        spinJumpFrames[4] = tmp[0][4];
        spinJumpFrames[5] = tmp[0][5];
        spinJumpFrames[6] = tmp[0][6];
        spinJumpFrames[7] = tmp[0][7];
        spinJumpFrames[8] = tmp[0][8];
        spinJumpFrames[9] = tmp[0][9];
        spinJumpAnimation = new Animation<TextureRegion>(0.1f, spinJumpFrames);

        textureSheet = new Texture(Gdx.files.internal("characters/player/bonk.png"));
        tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 1, textureSheet.getHeight() / 1);
        TextureRegion[] deathFrames = new TextureRegion[1];
        deathFrames[0] = tmp[0][0];
        deathAnimation = new Animation<TextureRegion>(1f, deathFrames);

        stateTime = 0f;
    }

    public void updateGravity(float delta){
        //simulate gravity
        float test = Gdx.graphics.getDeltaTime();
        float newY = position.y;
        velocityY += gravity * delta;
        newY += velocityY * delta;
        if(map.doesRectCollideWithMap(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height) && !dead){
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
            if(newY < 0){
                newY = position.y;
            }
            position.y = newY;
            grounded = false;
        }
    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera)
    {
        shootCounter += delta;
        timeSinceLastJump += delta;
        //System.out.println("left: "+left+" right: "+right+" up: "+up+" down: "+down);
        System.out.println("x: "+this.position.x+" y: "+this.position.y);
        System.out.println(shootPressed);
        this.stateTime += delta;
        updateGravity(delta);
        //simulate gravity
        //die(delta);

       /* if(shootCounter >= 3){
            shootCounter -= 3;
            weaponShot();
        }*/
        if(shootPressed){
            weaponShot();
        }

        if(weaponList.size() > 0){
            for(int i = 0; i<weaponList.size(); i++){
                Weapon w = weaponList.get(i);
                w.update(batch, delta, camera);
                ArrayList<Enemy> enemies = map.getEnemies();
                for(int j = 0; j<enemies.size(); j++){//for(Enemy e : map.getEnemies()){
                    Enemy e = enemies.get(j);
                    if(w.getBoundingBox().overlaps(e.getBoundingBox()) && e.isAlive()){
                        e.getsHit(w.getDamage());
                        // enemies.remove(j);
                        w.die();
                        weaponList.remove(i);
                        //e.die();
                    }
                }
            }
        }

        if(!playScreen.isGamePaused()){
            if(!dead){
                if (right) {
                    flip = false;
                } else if (left) {
                    flip = true;
                }

                if(deleteMe){
                    weaponShot();
                    deleteMe = false;
                }

                //update animation
                if (grounded) {
                    currentFrame = idleAnimation.getKeyFrame(stateTime, true);
                    if (right) {
                        currentFrame = walkAnimation.getKeyFrame(stateTime, true);
                    } else if (left) {
                        currentFrame = walkAnimation.getKeyFrame(stateTime, true);
                    }
                } else {
                    if(firstJump && doubleJump){
                        if(!spinJumpAnimation.isAnimationFinished(stateTime)){
                            currentFrame = spinJumpAnimation.getKeyFrame(stateTime, false);
                        }
                    } else{
                        currentFrame = jumpAnimation.getKeyFrame(stateTime, true);
                    }
                }

                if (down) {
                    currentFrame = downAnimation.getKeyFrame(stateTime, true);
                } else if ((up) && (grounded) && (timeSinceLastJump > 0.1)) {
                    currentFrame = jumpAnimation.getKeyFrame(stateTime, true);
                }

            /*if(walkAnimation.isAnimationFinished(stateTime)){
                stateTime = 0f;
            }
            System.out.println(walkAnimation.isAnimationFinished(stateTime));*/

                for(Coin coin : map.getCoins()){
                    if((boundingBox.overlaps(coin.getBoundingBox())) && (coin.alive())){
                        addCoin(1);
                        addScore(50);
                        //hud.addScore(50);
                        coin.die();
                        //this.die(delta);
                    }
                }

                for(Enemy e : map.getEnemies()){
                    if(this.getBoundingBox().overlaps(e.getBoundingBox()) && this.isAlive() && e.isAlive()){
                        //Intersection result = intersectsAt(camera, this.getBoundingBox(), e.getBoundingBox());
                        //System.out.println(result);
                        this.die();
                    }
                }

                //update position v2
                if(!down) {
                    if (up) {
                        if(timeSinceLastJump > 0.2){
                            timeSinceLastJump = 0;
                            jump();
                            up = false;
                        }

                    }
                    if (right) {
                        float newX = boundingBox.x + movementSpeed * delta;
                        if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                            moveRight(delta);
                            //right = false;
                        }
                    } else if (left) {
                        float newX = boundingBox.x - movementSpeed * delta;
                        if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                            moveLeft(delta);
                            //left = false;
                        }
                    }
                }
            } else{
                //if(!dead) die(delta);

                currentFrame = deathAnimation.getKeyFrame(stateTime, true);
                System.out.println("finito3");
                //velocityY += JUMP_VELOCITY;
                if(dead && stateTime > 2f){// deathAnimation.isAnimationFinished(delta)){
                    System.out.println("finito1111111111111111111111111");
                    deathComplete = true;
                }
            }
        }


        if(Gdx.app.getType() == Application.ApplicationType.Android ){
            right = false;
            left = false;
            up = false;
        }

        boundingBox.setPosition(position.x + BBOX_X_OFFSET, position.y + BBOX_Y_OFFSET);
        updateBoundingBoxPicture(camera,(int) getPosition().x + BBOX_X_OFFSET, (int) getPosition().y + BBOX_Y_OFFSET);
        batch.begin();
       // this.alpha = 1;
        if(!deathComplete){
            batch.draw(currentFrame, !flip ? position.x : position.x + width, position.y, !flip ? width : -width, height);
        }
        batch.end();
    }

    private void addCoin(int c){
        coinsCollected++;
    }

    public int getCoinsCollected(){
        return coinsCollected;
    }

    private void addScore(int score){
        points += score;
    }

    public boolean isDeathComplete(){
        if(dead && deathComplete){
            return true;
        }
        return false;
    }

    public int getPoints(){
        return this.points;
    }

    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame){//camera only there to update boundingBoxPicture, can be removed
        batch.begin();
        batch.draw(currentFrame, !flip ? position.x : position.x + width, position.y, !flip ? width : -width, height);
        //batch.draw(currentFrame, position.x, position.y);
        batch.end();
    }

    @Override
    public void dispose(){
        textureSheet.dispose();
    }

    @Override
    public void moveRight(float delta){
        if(grounded){
            position.x += movementSpeed * delta;
        } else{
            position.x += (movementSpeed / 2) * delta;
        }
    }

    @Override
    public void moveLeft(float delta){
        if(grounded){
            position.x -= movementSpeed * delta;
        } else{
            position.x -= (movementSpeed / 2) * delta;
        }
    }

    public void jump(){
        if(grounded && !firstJump){
            firstJump = true;
            velocityY += JUMP_VELOCITY;
        } else{
            if(!grounded && !doubleJump && firstJump){
                doubleJump = true;
                velocityY += JUMP_VELOCITY;
            }
        }
    }

    public void tiltReading(){
        float accZ = Gdx.input.getAccelerometerZ();
        //if(accZ )
    }

    @Override
    public void die(){
        //if(!dead) dead = true;
        //currentFrame = deathAnimation.getKeyFrame(stateTime, false);
        //jump();
        if (health > 0) {
            health--;
        }
        if(health == 0 && !dead){
            dead = true;
            velocityY += JUMP_VELOCITY;
            stateTime = 0f;
            if(left){
                flip = true;
            } else{
                flip = false;
            }
        }
    }

    public static void shoot(){

    }

    public boolean isAlive(){
        return !dead;
    }

    public void setHealth(int health){
        this.health = health;
    }

    public int getHealth(){
        return this.health;
    }

    public void takeDamage(int damage){
        this.health -= damage;
    }

}