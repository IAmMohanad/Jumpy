package com.jumpy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.jumpy.Screens.ScreenManager;

public class SoundManager {

    public static Music music;
    private static ScreenManager.GAME_STATE currentState;


    private static boolean isGameStateMenu(ScreenManager.GAME_STATE state) {
        if (state == ScreenManager.GAME_STATE.MAIN_MENU || currentState == ScreenManager.GAME_STATE.GAME_SETTINGS || currentState == ScreenManager.GAME_STATE.HISCORE || currentState == ScreenManager.GAME_STATE.LEVEL_SELECT || currentState == ScreenManager.GAME_STATE.SHOP){
            return true;
        }
        return false;
    }

    private static boolean isGameStateInGame(ScreenManager.GAME_STATE state){
        if(state == ScreenManager.GAME_STATE.PLAY){
            return true;
        }
        return false;
    }

    public static void playMusic(ScreenManager.GAME_STATE newState){//Music music){
        currentState = newState;
        if(!(isGameStateMenu(currentState) && isGameStateInGame(newState))){
            if(isGameStateMenu(currentState)){
                music = Gdx.audio.newMusic((Gdx.files.internal("music/Celestial Harps.ogg")));
            }
            if(!Jumpy.mute){
                music.setVolume(Jumpy.volume);
                music.play();
            }
            if(isGameStateInGame(currentState)){
                music.pause();
                music.stop();
                music.dispose();
            }
        }
    }

    public static void stopMusic(ScreenManager.GAME_STATE newState){
        currentState = newState;
        music.stop();
        System.out.println(music.isPlaying());
    }

    public static void updateMusicVolume(float newVolume){
        if(Jumpy.mute){
            music.setVolume(0);
        } else{
            music.setVolume(Jumpy.volume);
        }
    }

    public static void muteMusic(){
        music.setVolume(0);
    }

    public static void unmuteMusic(){
        music.setVolume(Jumpy.volume);
    }

    public static void playSound(Sound sound){
        if(!Jumpy.mute){
            sound.play(Jumpy.volume);
        }
    }
}
