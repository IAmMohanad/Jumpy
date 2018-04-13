package com.jumpy.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jumpy.Jumpy;
import com.jumpy.Scenes.LoadingScene;
import com.jumpy.Scenes.MainMenuScene;


public class LoadingScreen implements Screen {

    private Jumpy game;

    private LoadingScene loadingScene;
    private Stage stage;
    private AssetManager assetManager;
    private ScreenManager screenManager;

    public LoadingScreen(Jumpy game, ScreenManager screenManager){
        this.game = game;
        this.screenManager = screenManager;

        loadingScene = new LoadingScene(game);
        stage = loadingScene.create();
        assetManager = game.assetManager;
        load();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(assetManager.update()) {
            // we are done loading, let's move to another screen!
            //if name not chosen set screen as choose name else set screen as main menu
            screenManager.setScreen(ScreenManager.GAME_STATE.MAIN_MENU);
        }

        // display loading information
        float progress = assetManager.getProgress();
        loadingScene.updateProgressBar(progress);
        loadingScene.render();
        // ... left to the reader ...
    }

    public void load() {
        assetManager.load("characters/player/weapon/fire_bolt.png", Texture.class);
        assetManager.load("chaser_updated.png", Texture.class);
        assetManager.load("ui/skin/main_menu.json", Skin.class);
        assetManager.load("coin_animation/coin_animation.png", Texture.class);

        //load player assets
        assetManager.load("characters/player/idle.png", Texture.class);
        assetManager.load("characters/player/run.png", Texture.class);
        assetManager.load("characters/player/jump.png", Texture.class);
        assetManager.load("characters/player/fall.png", Texture.class);
        assetManager.load("characters/player/down.png", Texture.class);
        assetManager.load("characters/player/spinJump_fixed.png", Texture.class);
        assetManager.load("characters/player/bonk.png", Texture.class);
        //load gargoyle assets
        assetManager.load("characters/baddies/gargoyle_32_32/gargoyle_fly_trimmed.png", Texture.class);
        assetManager.load("characters/baddies/gargoyle_32_32/gargoyle_die_trimmed.png", Texture.class);
        assetManager.load("characters/baddies/gargoyle_32_32/gargoyle_hit_trimmed.png", Texture.class);
        //load barbarian assets
        assetManager.load("characters/baddies/barbarian_32_32/barbarian_walk_trimmed.png", Texture.class);
        assetManager.load("characters/baddies/barbarian_32_32/barbarian_die_trimmed.png", Texture.class);
        //load goblin assets
        assetManager.load("characters/baddies/goblin_32_32/goblin_idle_trimmed.png", Texture.class);
        assetManager.load("characters/baddies/goblin_32_32/goblin_walk_trimmed.png", Texture.class);
        assetManager.load("characters/baddies/goblin_32_32/goblin_run_trimmed.png", Texture.class);
        assetManager.load("characters/baddies/goblin_32_32/goblin_die_trimmed.png", Texture.class);
        assetManager.load("characters/baddies/goblin_32_32/goblin_hit_trimmed.png", Texture.class);

        //load Spike assets
        assetManager.load("characters/baddies/Spike_Down.png", Texture.class);
        assetManager.load("characters/baddies/Spike_Up.png", Texture.class);

        assetManager.load("characters/baddies/metal_spike_up.png", Texture.class);
        assetManager.load("characters/baddies/metal_spike_down.png", Texture.class);
        //load totem assets
        assetManager.load("characters/baddies/totem/totem_walk.png", Texture.class);
        assetManager.load("characters/baddies/totem/totem_die.png", Texture.class);

        //load speed boots test
        assetManager.load("ui/new ui/Anti-gravity_boots.png", Texture.class);
        assetManager.load("ui/new ui/Anti-gravity_boots2.png", Texture.class);

        assetManager.load("characters/baddies/iceball_001_up.png", Texture.class);
        assetManager.load("characters/baddies/iceball_001_right.png", Texture.class);

        //load laser sound
        assetManager.load("sound/laser_shot.mp3", Sound.class);
        //load jumping sound
        assetManager.load("sound/jump_1.wav", Sound.class);
        assetManager.load("sound/jump_2.wav", Sound.class);

        assetManager.load("sound/goblin_death.wav", Sound.class);


        //assetManager.load("ui/new ui/speed_boots.png", Texture.class);
        //assetManager.load("", Texture.class);
        //Jumpy.assetManager.get("", Texture.class);//
    }

    public AssetManager getAssetManager(){
        return assetManager;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        assetManager.dispose();
        stage.dispose();
    }
}
