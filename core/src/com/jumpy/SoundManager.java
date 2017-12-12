package com.jumpy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class SoundManager {

    public static Music backgroundMusic;

    public static void init(Jumpy game){

    }

    public static void loadBackgroundMusic(){
        backgroundMusic = Gdx.audio.newMusic((Gdx.files.internal("music/Celestial Harps.ogg")));
        backgroundMusic.setLooping(true);
    }

    public static void updateBackgroundMusicVolume(float volume){
        backgroundMusic.setVolume(volume);
    }

    public static void pauseBackgroundMusic(){
        if(backgroundMusic.isPlaying()){
            backgroundMusic.pause();
        }
    }

    public static void stopBackgroundMusic(){
        if(backgroundMusic.isPlaying()){
            backgroundMusic.stop();
        }
    }

    public static void playBackgroundMusic(){
        if(!backgroundMusic.isPlaying()) {
            backgroundMusic.setVolume(Jumpy.volume);
            backgroundMusic.play();
        }
    }

    public static void disposeBackgroundMusic(){
        backgroundMusic.dispose();
    }
}
