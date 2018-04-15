package com.jumpy.Characters;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jumpy.*;
import com.jumpy.Objects.Coin;
import com.jumpy.Objects.Exit;
import com.jumpy.Objects.IceBall;
import com.jumpy.Objects.IceBallShooter;
import com.jumpy.Screens.PlayScreen;
import com.jumpy.World.GameMap;
import com.jumpy.Active;

import java.util.ArrayList;

public class Player extends DynamicObject {

    private int coinsCollected;
    private int points;
    private int enemiesKilled;
    private PlayScreen playScreen;
    public static boolean left;
    public static boolean right;
    public static boolean up;
    public static boolean down;
    public static boolean shootPressed;
    public static boolean boostPressed;
    private boolean doubleJump;
    private boolean firstJump;
    private boolean exitReached;

    //private boolean c

    //private boolean dead;

    private static final int JUMP_VELOCITY = 130;

    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> downAnimation;
    private Animation<TextureRegion> fallAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> spinJumpAnimation;

    private float timeSinceLastJump = 0;

    private final int BBOX_X_OFFSET = 13;
    private final int BBOX_Y_OFFSET = 1;

    private final int BBOX_WIDTH_STANDING = 9;
    private final int BBOX_HEIGHT_STANDING = 40;

    private final int BBOX_WIDTH_CROUCH = 20;
    private final int BBOX_HEIGHT_CROUCH= 25;

    private ArrayList<Weapon> weaponList = new ArrayList<Weapon>();
    private Active equippedWeapon;
    private Boost equippedBoost;
    private Passive equippedPassive;
    private float boostTimer;
    private float boostMaxTime;
    private boolean boostOn;
    private boolean playedPowerWarning;
    private Rectangle magnetBoundingBox;

    private int weaponDamage;
    private int weaponSpeed;
    private float shootLimiter = 0.33f;
    private float shootCounter = 0;
    private Sound jumpSound1;
    private Sound jumpSound2;
    private Sound shootSound;
    private Sound powerUpOnSound;
    private Sound powerUpOffSound;
    private Sound collectCoinSound;

    public Player(Active equippedWeapon, Boost equippedBoost, Passive equippedPassive, GameMap map, float x, float y, PlayScreen playScreen){
        this.playScreen = playScreen;
        super.health = 1;
        super.width = 32;
        super.height = 64;
        super.map = map;
        position = new Vector2(x, y);
        boundingBox = new Rectangle();
        boundingBox.set(position.x + BBOX_X_OFFSET, position.y + BBOX_Y_OFFSET, BBOX_WIDTH_STANDING, BBOX_HEIGHT_STANDING);
        movementSpeed = 150;
        velocityY = 0;
        grounded = false;
        firstJump = false;
        doubleJump = false;
        dead = false;
        playedPowerWarning = false;
        this.equippedWeapon = equippedWeapon;
        this.equippedBoost = equippedBoost;
        this.equippedPassive = equippedPassive;
        points = 0;
        coinsCollected = 0;
        enemiesKilled = 0;
        deathComplete = false;
        exitReached = false;
        shootSound = Jumpy.assetManager.get("sound/laser_shot.mp3", Sound.class);
        jumpSound1 = Jumpy.assetManager.get("sound/jump_1.wav", Sound.class);
        jumpSound2 = Jumpy.assetManager.get("sound/jump_2.wav", Sound.class);
        powerUpOnSound = Jumpy.assetManager.get("sound/power_up_on.mp3", Sound.class);
        powerUpOffSound = Jumpy.assetManager.get("sound/powerup_off.mp3", Sound.class);
        collectCoinSound = Jumpy.assetManager.get("sound/collect_coin.wav", Sound.class);

        if(equippedWeapon == Active.LASER){
            Jumpy.assetManager.load("sound/laser_shot.mp3", Sound.class);
        }

        left = false;
        right = false;
        up = false;
        down = false;
        shootPressed = false;
        boostPressed = false;

        assignBoosts();
        create();
    }

    public boolean getExitReached(){
        return exitReached;
    }

    private void assignBoosts(){
        Preferences upgradePrefs = Gdx.app.getPreferences("upgradePrefs");
        if(equippedWeapon != Active.NONE){
            int equippedActiveLevel = upgradePrefs.getInteger(equippedWeapon.toString().toUpperCase()+"Level");
            weaponDamage = (int) upgradePrefs.getFloat(equippedWeapon.toString().toUpperCase()+"Level-"+equippedActiveLevel+"-damage");
        } else{
            weaponDamage = 1;
        }
        if(equippedPassive != Passive.NONE){
            int equippedPassiveLevel = upgradePrefs.getInteger(equippedPassive.toString().toUpperCase()+"Level");
            if(equippedPassive == Passive.ANTI_GRAVITY){
                gravity = gravity * (1 - upgradePrefs.getFloat(equippedPassive.toString().toUpperCase()+"Level-"+equippedPassiveLevel+"-boost"));
            } else if(equippedPassive == Passive.SPEED){
                movementSpeed *= 1 + upgradePrefs.getFloat(equippedPassive.toString().toUpperCase()+"Level-"+equippedPassiveLevel+"-boost");
            }
        }
        if(equippedBoost != Boost.NONE){
            int equippedBoostLevel = upgradePrefs.getInteger(equippedBoost.toString().toUpperCase()+"Level");
            if(equippedBoost == Boost.MAGNET){
                boostMaxTime = upgradePrefs.getFloat(equippedBoost.toString().toUpperCase()+"Level-"+equippedBoostLevel+"-duration");
                magnetBoundingBox = new Rectangle(this.position.x - 32, this.position.y - 45, 96, 135);//width=96, height=135
                //create bounding box
            } else if(equippedBoost == Boost.ARMOUR){
                boostMaxTime = upgradePrefs.getFloat(equippedBoost.toString().toUpperCase()+"Level-"+equippedBoostLevel+"-duration");
                //don't need to do anything else
            }
        }
        boostOn = false;
    }

    private void resolveBoost(float delta){
        boostTimer += delta;
        if(boostPressed){
            boostPressed = false;
            if(!boostOn){
                boostTimer = 0;
                boostOn = true;
                powerUpOnSound.play(Jumpy.volume);
            }
        }
        if(boostOn){
            if((boostTimer >= (boostMaxTime - 2)) && !playedPowerWarning){
                playedPowerWarning = true;
                powerUpOffSound.play(Jumpy.volume);
            }
            if(boostTimer >= boostMaxTime){
                boostOn = false;
                boostTimer = 0;
            }
        }

        if(equippedBoost == Boost.MAGNET){
            magnetBoundingBox.setPosition(this.position.x - 32, this.position.y - 45);
        }
        //System.out.println("boostOn: "+boostOn+"     "+(int) boostTimer);
    }

    public void weaponShot(){
        shootPressed = false;
        if(shootCounter >= shootLimiter){
            shootSound.play(Jumpy.volume);
            shootCounter = 0;
            if(equippedWeapon == Active.NONE){
                weaponList.add(new Laser(map, this.position.x, this.position.y, flip ? Move.LEFT : Move.RIGHT));//TODO remove this!!!
            }
            if(equippedWeapon == Active.LASER){
                weaponList.add(new Laser(weaponDamage, map, this.position.x, this.position.y, flip ? Move.LEFT : Move.RIGHT));
            }
        }
    }

    @Override
    public void create(){
        textureSheet = Jumpy.assetManager.get("characters/player/idle.png", Texture.class);//new Texture(Gdx.files.internal("characters/player/idle.png"));
        TextureRegion[][] tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 1,
                textureSheet.getHeight() / 1);
        TextureRegion[] idleFrames = new TextureRegion[1];
        idleFrames[0] = tmp[0][0];
        idleAnimation = new Animation<TextureRegion>(0.5f, idleFrames);

        textureSheet = Jumpy.assetManager.get("characters/player/run.png", Texture.class);//new Texture(Gdx.files.internal("characters/player/run.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 6,
                textureSheet.getHeight() / 1);
        TextureRegion[] walkFrames = new TextureRegion[6];
        for(int i =0; i<6; i++){
            walkFrames[i] = tmp[0][i];
        }
        walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);

        textureSheet = Jumpy.assetManager.get("characters/player/jump.png", Texture.class);//new Texture(Gdx.files.internal("characters/player/jump.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 3,
                textureSheet.getHeight() / 1);
        TextureRegion[] jumpFrames = new TextureRegion[3];
        for(int i =0; i<3; i++){
            jumpFrames[i] = tmp[0][i];
        }
        jumpAnimation = new Animation<TextureRegion>(0.2f, jumpFrames);

        textureSheet = Jumpy.assetManager.get("characters/player/fall.png", Texture.class);//new Texture(Gdx.files.internal("characters/player/fall.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 3,
                textureSheet.getHeight() / 1);
        TextureRegion[] fallFrames = new TextureRegion[3];
        for(int i =0; i<3; i++){
            fallFrames[i] = tmp[0][i];
        }
        fallAnimation = new Animation<TextureRegion>(0.2f, fallFrames);

        textureSheet = Jumpy.assetManager.get("characters/player/down.png", Texture.class);//new Texture(Gdx.files.internal("characters/player/down.png"));
        tmp = TextureRegion.split(textureSheet,
                textureSheet.getWidth() / 1,
                textureSheet.getHeight() / 1);
        TextureRegion[] downFrames = new TextureRegion[1];
        downFrames[0] = tmp[0][0];
        downAnimation = new Animation<TextureRegion>(0.5f, downFrames);

        textureSheet = Jumpy.assetManager.get("characters/player/spinJump_fixed.png", Texture.class);//new Texture(Gdx.files.internal("characters/player/spinJump_fixed.png"));
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

        textureSheet = Jumpy.assetManager.get("characters/player/bonk.png", Texture.class);//new Texture(Gdx.files.internal("characters/player/bonk.png"));
        tmp = TextureRegion.split(textureSheet, textureSheet.getWidth() / 1, textureSheet.getHeight() / 1);
        TextureRegion[] deathFrames = new TextureRegion[1];
        deathFrames[0] = tmp[0][0];
        deathAnimation = new Animation<TextureRegion>(1f, deathFrames);

        stateTime = 0f;
    }

    public void updateGravity(float delta){
        //simulate gravity
        float newY = position.y;
        //how many pixels should be moved on Y axis. positive == moving up, negative == falling
        velocityY += gravity * delta;
        newY += velocityY * delta;
        if(map.doesRectCollideWithMap(boundingBox.x, newY, (int) boundingBox.width, (int) boundingBox.height) && !dead){
            //if colliding with map & falling down hit ground
            if(velocityY < 0){//snap position to ground
                position.y = (float) Math.floor(position.y);
                grounded = true;
            }
            firstJump = false;
            doubleJump = false;
            velocityY = 0;
        } else{
            if(!exitReached){
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
    }

    @Override
    public void update(SpriteBatch batch, float delta, OrthographicCamera camera)
    {
        if(down){
            boundingBox.height = BBOX_HEIGHT_CROUCH;
        } else{
            boundingBox.height = BBOX_HEIGHT_STANDING;
        }
        shootCounter += delta;
        timeSinceLastJump += delta;
        this.stateTime += delta;
        updateGravity(delta);

        resolveBoost(delta);

        if(shootPressed){
            weaponShot();
        }

        if(!playScreen.isGamePaused()){
            if(!dead){
                if (right){
                    flip = false;
                } else if (left){
                    flip = true;
                }

                //update animation
                if(grounded){
                    currentFrame = idleAnimation.getKeyFrame(stateTime, true);
                    if(right){
                        currentFrame = walkAnimation.getKeyFrame(stateTime, true);
                    } else if(left){
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

                if ((up) && (grounded) && (timeSinceLastJump > 0.1)) {
                    currentFrame = jumpAnimation.getKeyFrame(stateTime, true);
                }

                resolveObjectInteraction(delta, batch, camera);

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
                        }
                    } else if (left) {
                        float newX = boundingBox.x - movementSpeed * delta;
                        if (!map.collideWithMapEdges(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height) && (!map.doesRectCollideWithMap(newX, boundingBox.y, (int) boundingBox.width, (int) boundingBox.height))) {
                            moveLeft(delta);
                        }
                    }
                }
            } else{
                if(exitReached){
                    currentFrame = idleAnimation.getKeyFrame(stateTime, true);
                } else{
                    currentFrame = deathAnimation.getKeyFrame(stateTime, true);
                }
                if(dead && stateTime > 2f){
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
        if(!deathComplete){
            batch.draw(currentFrame, !flip ? position.x : position.x + width, position.y - 1, !flip ? width : -width, height);
        }
        batch.end();
    }

    private void resolveObjectInteraction(float delta, SpriteBatch batch, OrthographicCamera camera){
        if(weaponList.size() > 0){
            for(int i = 0; i<weaponList.size(); i++){
                Weapon w = weaponList.get(i);
                w.update(batch, delta, camera);
                ArrayList<Enemy> enemies = map.getEnemies();
                for(int j = 0; j<enemies.size(); j++){//for(Enemy e : map.getEnemies()){
                    Enemy e = enemies.get(j);
                    if(w.getBoundingBox().overlaps(e.getBoundingBox()) && e.isAlive()){
                        e.getsHit(w.getDamage());
                        if(e.isDead()){
                            enemiesKilled += 1;
                        }
                        w.die();
                        weaponList.remove(i);
                    }
                }
            }
        }

        for(Coin coin : map.getCoins()){
            if(boostOn && magnetBoundingBox.overlaps(coin.getBoundingBox()) && coin.alive()){
                coin.moveTowardsPlayer(delta, this.position.x, this.position.y);
            }
            if((boundingBox.overlaps(coin.getBoundingBox())) && (coin.alive())){
                collectCoinSound.play(Jumpy.volume);
                coinsCollected += 1;
                coin.die();
            }
        }

        for(Enemy e : map.getEnemies()){
            if(this.getBoundingBox().overlaps(e.getBoundingBox()) && this.isAlive() && e.isAlive()){
                this.die();
            }
        }

        for(IceBallShooter shooter : map.getProjectileShootersList()){
            if(shooter.getIceBallList().size() > 0){
                for(IceBall iceBall : shooter.getIceBallList()){
                    if(this.boundingBox.overlaps(iceBall.getBoundingBox())){
                        this.die();
                    }
                }
            }
        }

        for(Exit exit : map.getExits()){
            if(this.boundingBox.overlaps(exit.getBoundingBox())){
                exitReached = true;
                this.die();

            }
        }
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

    public int getPoints(){
        return this.points;
    }

    public int getEnemiesKilled(){
        return this.enemiesKilled;
    }

    public boolean isDeathComplete(){
        if(dead && deathComplete){
            return true;
        }
        return false;
    }



    @Override
    public void render(SpriteBatch batch, float delta, TextureRegion currentFrame){//camera only there to update boundingBoxPicture, can be removed
        batch.begin();
        batch.draw(currentFrame, !flip ? position.x : position.x + width, position.y, !flip ? width : -width, height);
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
            jumpSound1.play(Jumpy.volume);
            velocityY += JUMP_VELOCITY;
        } else{
            if(!grounded && !doubleJump && firstJump){
                doubleJump = true;
                jumpSound2.play(Jumpy.volume);
                velocityY += JUMP_VELOCITY / 1.5;
            }
        }
    }

    @Override
    public void die(){
        if (health > 0 && !exitReached) {
            if(equippedBoost == Boost.ARMOUR && boostOn){
                return;
            } else{
                health--;
            }
        }
        if(exitReached){
            health = 0;
        }
        if(health == 0 && !dead){
            dead = true;
            if(!exitReached){
                velocityY += JUMP_VELOCITY;
            }
            stateTime = 0f;
            if(left){
                flip = true;
            } else{
                flip = false;
            }
        }
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