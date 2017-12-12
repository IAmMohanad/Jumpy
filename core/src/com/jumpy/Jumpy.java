package com.jumpy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Screens.*;
import com.jumpy.World.GameMap;
import com.jumpy.World.LevelOne;
import com.jumpy.World.TileType;

public class Jumpy extends Game {//ApplicationAdapter {
	private Preferences settings;
	public static boolean mute;
	public static float volume;
	public static final int V_WIDTH = 480;
	public static final int V_HEIGHT = 270;

	private String currentLevel;

	public MainMenuScreen mainMenuScreen;
	public SettingsScreen settingsScreen;
	public PlayScreen playScreen;
	public LevelSelectScreen levelScreen;

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

		/*mainMenuScreen = new MainMenuScreen(this);
		settingsScreen = new SettingsScreen(this);

		levelScreen = new LevelSelectScreen(this);

		setMenu();*/
		screenManager.setScreen(ScreenManager.STATE.MAIN_MENU);
	}

	/*public void setMenu(){
		setScreen(mainMenuScreen);
	}*/

	/*public void setSettings(){
		setScreen(settingsScreen);
	}*/

	/*public void setPlay(){
		SoundManager.stopBackgroundMusic();
		playScreen = new PlayScreen(this);
		setScreen(playScreen);
	}*/

	/*public void setLevel(){
		setScreen(levelScreen);
	}*/

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
