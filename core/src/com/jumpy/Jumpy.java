package com.jumpy;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jumpy.Screens.*;


import java.util.Map;

public class Jumpy extends Game {//ApplicationAdapter {
	private Preferences settings;
	public static boolean mute;
	public static float volume;
	public static final int V_WIDTH = 480;
	public static final int V_HEIGHT = 270;

	private String currentLevel;

	public SpriteBatch batch;

	public ScreenManager screenManager;

	
	@Override
	public void create () {
		//SoundManager.init(this);
		settings = Gdx.app.getPreferences("settings");
		Jumpy.mute = this.settings.getBoolean("mute", false);
		Jumpy.volume = this.settings.getFloat("volume", 1.0f);
		batch = new SpriteBatch();

		screenManager = new ScreenManager(this);

		Preferences userPrefs = Gdx.app.getPreferences("userPrefs");
		//different preference file for upgrades and weapons
		//userPrefs.getInteger("upgrades");
		//userPrefs.getInteger("weapons");

		// RESET USER PREFS
		/*userPrefs.putInteger("goldEarned", 0);
		userPrefs.putInteger("pointsEarned", 0);
		userPrefs.flush();*/

		Map userPrefsKeys = userPrefs.get();

		if(userPrefsKeys.isEmpty() == true){
			//doesnt exist, so create it
			userPrefs.putInteger("goldEarned", 0);
			userPrefs.putInteger("pointsEarned", 0);
		} else{
			//does exist, no need to do anything
		}


		screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	public String getCurrentLevel(){
		return currentLevel;
	}

	public void setCurrentLevel(String lvl){
		this.currentLevel = lvl;
	}
}
