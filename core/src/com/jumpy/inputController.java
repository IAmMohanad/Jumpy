package com.jumpy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.jumpy.Characters.Player;

public class inputController extends InputAdapter {

    public inputController(){
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.LEFT){
            Player.left = true;
        } else if(keycode == Input.Keys.RIGHT){
            Player.right = true;
        }
        if(keycode == Input.Keys.UP){
            Player.up = true;
        } else if(keycode == Input.Keys.DOWN){
            Player.down = true;
        }
        if(keycode == Input.Keys.BACK){
            // Respond to the back button click here
            Jumpy.exitPressed = true;
            //Gdx.app.exit();
        }
        if(keycode == Input.Keys.X){
            Player.shootPressed = true;
        }

        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT){
            Player.left = false;
        } else if(keycode == Input.Keys.RIGHT){
            Player.right = false;
        }
        if(keycode == Input.Keys.UP){
            Player.up = false;
        } else if(keycode == Input.Keys.DOWN){
            Player.down = false;
        }
        return super.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Player.up = true;
       // System.out.println(screenX + "  "+ screenY+" width:"+Gdx.graphics.getWidth());
       /* if(screenX < Gdx.graphics.getWidth() / 2){
            Player.left = true;
        }
        else if(screenX > Gdx.graphics.getWidth() / 2){
            Player.right = true;
        }*/
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Player.up = false;
        /*if(screenX < Gdx.graphics.getWidth() / 2){
            Player.left = false;
        }
        else if(screenX > Gdx.graphics.getWidth() / 2){
            Player.right = false;
        }*/
        return super.touchUp(screenX, screenY, pointer, button);
    }
}
