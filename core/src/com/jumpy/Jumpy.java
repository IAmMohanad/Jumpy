package com.jumpy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.XmlReader;
import com.jumpy.Screens.*;


import java.io.IOException;
import java.util.Random;

public class Jumpy extends Game {//ApplicationAdapter {
	private Preferences settings;
	public static boolean mute;
	public static float volume;
	public static final int V_WIDTH = 480;
	public static final int V_HEIGHT = 270;
	public static final String HISCORE_SERVER_URL = "http://hiscores-mas34.apps.devcloud.eecs.qmul.ac.uk/hiscores";
	public static boolean exitPressed = false;
	public static ScreenManager.GAME_STATE gameState;

	private String currentLevel;

	public SpriteBatch batch;

	public static ScreenManager screenManager;
	public static AssetManager assetManager;
	public static SoundManager soundManager;

	private Preferences upgradePrefs;

	private boolean needsUpdate = false;
	@Override
	public void create () {
		//SoundManager.init(this);
		settings = Gdx.app.getPreferences("settings");
		Jumpy.mute = this.settings.getBoolean("mute", false);
		Jumpy.volume = this.settings.getFloat("volume", 1.0f);
		batch = new SpriteBatch();

		soundManager = new SoundManager();
		screenManager = new ScreenManager(this);
		assetManager = new AssetManager();

		upgradePrefs = Gdx.app.getPreferences("upgradePrefs");
		if(needsUpdate){//!upgradePrefs.contains("upToDate") || upgradePrefs.getBoolean("upToDate") == false){
			upgradePrefs.clear();
			updateUpgradePrefs("upgrades.xml");
		}

		setScreen(new LoadingScreen(this, screenManager));
		//soundManager.playMusic(screenManager.getGameState());
		Preferences userPrefs = Gdx.app.getPreferences("userPrefs");


		//different preference file for upgrades and weapons
		//userPrefs.getInteger("upgrades");
		//userPrefs.getInteger("weapons");

		// RESET USER PREFS
		/*userPrefs.putInteger("goldEarned", 0);
		userPrefs.putInteger("pointsEarned", 0);
		userPrefs.flush();*/

		boolean userPrefsKeys = userPrefs.getBoolean("created", false);

		if(userPrefsKeys == false){
			//doesn't exist, so create it
			if(!userPrefs.getBoolean("created", false)){
				userPrefs.putInteger("goldEarned", 10000);
				userPrefs.putInteger("pointsEarned", 0);
				userPrefs.putString("equippedActive", Active.NONE.toString());
				userPrefs.putString("equippedPassive", Passive.NONE.toString());
				userPrefs.putString("equippedBoost", Boost.NONE.toString());
				userPrefs.putString("username", CreatePlayerId());
				userPrefs.putInteger("lifeTimepointsEarned", 0);
				userPrefs.putInteger("lifeTimeEnemiesKilled", 0);
				userPrefs.putInteger("lifeTimeTimePlayed", 0);
				userPrefs.putInteger("lifetimeGoldEarned", 0);
				userPrefs.putBoolean("created", true);
				userPrefs.flush();
			}
		}
	}

	private String CreatePlayerId(){
		Random rand = new Random();

		return String.valueOf(rand.nextInt(99999999) + 1);
	}

	public ScreenManager.GAME_STATE getGameState(){
		return screenManager.getGameState();
	}

	@Override
	public void render () {
		//Gdx.graphics.setTitle(String.valueOf(Gdx.graphics.getFramesPerSecond()));
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		assetManager.dispose();
	}

	public SoundManager getSoundManager(){
		return soundManager;
	}

	public String getCurrentLevel(){
		return currentLevel;
	}

	public void setCurrentLevel(String lvl){
		this.currentLevel = lvl;
	}

	private boolean updateUpgradePrefs(String upgradesUrl){
		XmlReader xml = new XmlReader();
		try{
		XmlReader.Element root = xml.parse(Gdx.files.internal(upgradesUrl));
		XmlReader.Element passiveUpgrades = root.getChildByName("passive");
		//speed boots
		XmlReader.Element speedBoots = passiveUpgrades.getChildByName("speed");
		upgradePrefs.putBoolean(Passive.valueOf("SPEED")+"Unlocked", speedBoots.getBoolean("unlocked"));
		upgradePrefs.putInteger(Passive.valueOf("SPEED")+"Level", speedBoots.getInt("level"));
		upgradePrefs.putFloat(Passive.valueOf("SPEED")+"Level-1-boost", speedBoots.getFloat("level-1-boost"));
		upgradePrefs.putFloat(Passive.valueOf("SPEED")+"Level-2-boost", speedBoots.getFloat("level-2-boost"));
		upgradePrefs.putFloat(Passive.valueOf("SPEED")+"Level-3-boost", speedBoots.getFloat("level-3-boost"));
		upgradePrefs.putString(Passive.valueOf("SPEED")+"Description", speedBoots.get("description"));
		upgradePrefs.putInteger(Passive.valueOf("SPEED")+"Level-0-price", speedBoots.getInt("level-0-price"));
		upgradePrefs.putInteger(Passive.valueOf("SPEED")+"Level-1-price", speedBoots.getInt("level-1-price"));
		upgradePrefs.putInteger(Passive.valueOf("SPEED")+"Level-2-price", speedBoots.getInt("level-2-price"));
		upgradePrefs.putInteger(Passive.valueOf("SPEED")+"Level-3-price", speedBoots.getInt("level-3-price"));
		//anti-gravity boots
		XmlReader.Element antiGravityBoots = passiveUpgrades.getChildByName("antiGravity");
		upgradePrefs.putBoolean(Passive.valueOf("ANTI_GRAVITY")+"Unlocked", antiGravityBoots.getBoolean("unlocked"));
		upgradePrefs.putInteger(Passive.valueOf("ANTI_GRAVITY")+"Level", antiGravityBoots.getInt("level"));
		upgradePrefs.putFloat(Passive.valueOf("ANTI_GRAVITY")+"Level-1-boost", antiGravityBoots.getFloat("level-1-boost"));
		upgradePrefs.putFloat(Passive.valueOf("ANTI_GRAVITY")+"Level-2-boost", antiGravityBoots.getFloat("level-2-boost"));
		upgradePrefs.putFloat(Passive.valueOf("ANTI_GRAVITY")+"Level-3-boost", antiGravityBoots.getFloat("level-3-boost"));
		upgradePrefs.putString(Passive.valueOf("ANTI_GRAVITY")+"Description", antiGravityBoots.get("description"));
		upgradePrefs.putInteger(Passive.valueOf("ANTI_GRAVITY")+"Level-0-price", antiGravityBoots.getInt("level-0-price"));
		upgradePrefs.putInteger(Passive.valueOf("ANTI_GRAVITY")+"Level-1-price", antiGravityBoots.getInt("level-1-price"));
		upgradePrefs.putInteger(Passive.valueOf("ANTI_GRAVITY")+"Level-2-price", antiGravityBoots.getInt("level-2-price"));
		upgradePrefs.putInteger(Passive.valueOf("ANTI_GRAVITY")+"Level-3-price", antiGravityBoots.getInt("level-3-price"));
		//active
		//laser
		XmlReader.Element activeUpgrades = root.getChildByName("active");
		XmlReader.Element laser = activeUpgrades.getChildByName("laser");
		upgradePrefs.putBoolean(Active.valueOf("LASER")+"Unlocked", laser.getBoolean("unlocked"));
		upgradePrefs.putInteger(Active.valueOf("LASER")+"Level", laser.getInt("level"));
		upgradePrefs.putFloat(Active.valueOf("LASER")+"Level-1-damage", laser.getFloat("level-1-damage"));
		upgradePrefs.putFloat(Active.valueOf("LASER")+"Level-2-damage", laser.getFloat("level-2-damage"));
		upgradePrefs.putFloat(Active.valueOf("LASER")+"Level-3-damage", laser.getFloat("level-3-damage"));
		upgradePrefs.putString(Active.valueOf("LASER")+"Description", laser.get("description"));
		upgradePrefs.putInteger(Active.valueOf("LASER")+"Level-0-price", laser.getInt("level-0-price"));
		upgradePrefs.putInteger(Active.valueOf("LASER")+"Level-1-price", laser.getInt("level-1-price"));
		upgradePrefs.putInteger(Active.valueOf("LASER")+"Level-2-price", laser.getInt("level-2-price"));
		upgradePrefs.putInteger(Active.valueOf("LASER")+"Level-3-price", laser.getInt("level-3-price"));
		//boost
		//magnet
		XmlReader.Element boostUpgrades = root.getChildByName("boost");
		XmlReader.Element magnet = boostUpgrades.getChildByName("magnet");
		upgradePrefs.putBoolean(Boost.valueOf("MAGNET")+"Unlocked", magnet.getBoolean("unlocked"));
		upgradePrefs.putInteger(Boost.valueOf("MAGNET")+"Level", magnet.getInt("level"));
		upgradePrefs.putFloat(Boost.valueOf("MAGNET")+"Level-1-duration", magnet.getFloat("level-1-duration"));
		upgradePrefs.putFloat(Boost.valueOf("MAGNET")+"Level-2-duration", magnet.getFloat("level-2-duration"));
		upgradePrefs.putFloat(Boost.valueOf("MAGNET")+"Level-3-duration", magnet.getFloat("level-3-duration"));
		upgradePrefs.putString(Boost.valueOf("MAGNET")+"Description", magnet.get("description"));
		upgradePrefs.putInteger(Boost.valueOf("MAGNET")+"Level-0-price", magnet.getInt("level-0-price"));
		upgradePrefs.putInteger(Boost.valueOf("MAGNET")+"Level-1-price", magnet.getInt("level-1-price"));
		upgradePrefs.putInteger(Boost.valueOf("MAGNET")+"Level-2-price", magnet.getInt("level-2-price"));
		upgradePrefs.putInteger(Boost.valueOf("MAGNET")+"Level-3-price", magnet.getInt("level-3-price"));
		//armour
		XmlReader.Element armour = boostUpgrades.getChildByName("armour");
		upgradePrefs.putBoolean(Boost.valueOf("ARMOUR")+"Unlocked", armour.getBoolean("unlocked"));
		upgradePrefs.putInteger(Boost.valueOf("ARMOUR")+"Level", armour.getInt("level"));
		upgradePrefs.putFloat(Boost.valueOf("ARMOUR")+"Level-1-duration", armour.getFloat("level-1-duration"));
		upgradePrefs.putFloat(Boost.valueOf("ARMOUR")+"Level-2-duration", armour.getFloat("level-2-duration"));
		upgradePrefs.putFloat(Boost.valueOf("ARMOUR")+"Level-3-duration", armour.getFloat("level-3-duration"));
		upgradePrefs.putString(Boost.valueOf("ARMOUR")+"Description", armour.get("description"));
		upgradePrefs.putInteger(Boost.valueOf("ARMOUR")+"Level-0-price", armour.getInt("level-0-price"));
		upgradePrefs.putInteger(Boost.valueOf("ARMOUR")+"Level-1-price", armour.getInt("level-1-price"));
		upgradePrefs.putInteger(Boost.valueOf("ARMOUR")+"Level-2-price", armour.getInt("level-2-price"));
		upgradePrefs.putInteger(Boost.valueOf("ARMOUR")+"Level-3-price", armour.getInt("level-3-price"));

		upgradePrefs.putString("equipped", "none");
		upgradePrefs.flush();
		return true;
		} catch(IOException e){
			System.out.println("upgrades.xml file does not exist.");
			return false;
		}
	}
}
