package com.jumpy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.jumpy.Screens.ScreenManager;

public class SoundManager {

    public Music backgroundMusic;
    private Music music;
    private ScreenManager.GAME_STATE currentState;

    public SoundManager(){
    }

    public void loadBackgroundMusic(){
        backgroundMusic = Gdx.audio.newMusic((Gdx.files.internal("music/Celestial Harps.ogg")));
        backgroundMusic.setLooping(true);
    }

    public void updateBackgroundMusicVolume(float volume){
        backgroundMusic.setVolume(volume);
    }

    public void pauseBackgroundMusic(){
        if(backgroundMusic.isPlaying()){
            backgroundMusic.pause();
        }
    }

    public void stopBackgroundMusic(){
        if(backgroundMusic.isPlaying()){
            backgroundMusic.stop();
        }
    }

    public void playBackgroundMusic(){
        if(!backgroundMusic.isPlaying()) {
            backgroundMusic.setVolume(Jumpy.volume);
            backgroundMusic.play();
        }
    }
    public void upgradeMusicVolume(float volume){
        if(this.music != null){
            this.music.setVolume(volume);
        }
    }

    private boolean isGameStateMenu(ScreenManager.GAME_STATE state) {
        if (state == ScreenManager.GAME_STATE.MAIN_MENU || currentState == ScreenManager.GAME_STATE.GAME_SETTINGS || currentState == ScreenManager.GAME_STATE.HISCORE || currentState == ScreenManager.GAME_STATE.LEVEL_SELECT || currentState == ScreenManager.GAME_STATE.SHOP){
            return true;
        }
        return false;
    }

    private boolean isGameStateInGame(ScreenManager.GAME_STATE state){
        if(state == ScreenManager.GAME_STATE.PLAY){
            return true;
        }
        return false;
    }

    public void playMusic(ScreenManager.GAME_STATE newState){//Music music){
        //game.screenManager.
        currentState = newState;
        if(!(isGameStateMenu(currentState) && isGameStateInGame(newState))){
            if(isGameStateMenu(currentState)){
                music = Gdx.audio.newMusic((Gdx.files.internal("music/Celestial Harps.ogg")));
            } else{
                music = Gdx.audio.newMusic((Gdx.files.internal("music/Celestial Harps.ogg")));//TODO change to in-game music
            }
            //this.music = music;
            if(!Jumpy.mute){
                music.setVolume(Jumpy.volume);
                music.play();
            }
        }
    }

    public void updateMusicVolume(float newVolume){
        if(Jumpy.mute){
            music.setVolume(0);
        } else{
            music.setVolume(Jumpy.volume);
        }
    }

    public void muteMusic(){
        music.setVolume(0);
    }

    public void unmuteMusic(){
        music.setVolume(Jumpy.volume);
    }

    public void playSound(Sound sound){
        if(!Jumpy.mute){
            sound.play(Jumpy.volume);
        }
    }

    public void disposeBackgroundMusic(){
        backgroundMusic.dispose();
    }
}
