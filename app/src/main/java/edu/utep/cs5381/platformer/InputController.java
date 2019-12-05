package edu.utep.cs5381.platformer;

import android.graphics.Rect;
import android.view.MotionEvent;
import java.util.ArrayList;

import edu.utep.cs5381.platformer.visuals.Viewport;

class InputController {
    private Rect left;
    private Rect right;
    private Rect jump;
    private Rect shoot;
    private Rect pause;

    InputController(int screenWidth, int screenHeight) {
        //Configure the player buttons
        int buttonWidth = screenWidth / 8;
        int buttonHeight = screenHeight / 7;
        int buttonPadding = screenWidth / 80;

        left = new Rect(buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                buttonWidth,
                screenHeight - buttonPadding);

        right = new Rect(buttonWidth + buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                buttonWidth + buttonPadding + buttonWidth,
                screenHeight - buttonPadding);

        jump = new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonHeight - buttonPadding - buttonHeight - buttonPadding,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding - buttonHeight - buttonPadding);

        shoot = new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding);

        pause = new Rect(screenWidth - buttonPadding - buttonWidth,
                buttonPadding,
                screenWidth - buttonPadding,
                buttonPadding + buttonHeight);
    }

    ArrayList getButtons() {
        //create an array of buttons for the draw method
        ArrayList<Rect> currentButtonList = new ArrayList<>();
        currentButtonList.add(left);
        currentButtonList.add(right);
        currentButtonList.add(jump);
        currentButtonList.add(shoot);
        currentButtonList.add(pause);
        return  currentButtonList;
    }

    void handleInput(MotionEvent motionEvent,LevelManager l, SoundManager sound, Viewport vp){
        int pointerCount = motionEvent.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            int x = (int) motionEvent.getX(i);
            int y = (int) motionEvent.getY(i);
            if(l.isPlaying()) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (right.contains(x, y)) {
                            l.player().setPressingRight(true);
                            l.player().setPressingLeft(false);
                        } else if (left.contains(x, y)) {
                            l.player().setPressingLeft(true);
                            l.player().setPressingRight(false);
                        } else if (jump.contains(x, y)) {
                            l.player().startJump(sound);
                        } else if (shoot.contains(x, y)) {
                            if (l.player().pullTrigger()) {
                                sound.play(SoundManager.Sound.SHOOT);
                            }
                        } else if (pause.contains(x, y)) {
                            l.switchPlayingStatus();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (right.contains(x, y)) {
                            l.player().setPressingRight(false);
                        } else if (left.contains(x, y)) {
                            l.player().setPressingLeft(false);
                        }
                        break;
                }
            } else { // Not playing
                //Move the viewport around to explore the map
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if ( right.contains(x, y)) {
                            vp.moveViewportRight(l.mapWidth);
                        } else if (left.contains(x, y)) {
                            vp.moveViewportLeft();
                        } else if (jump.contains(x, y)) {
                            vp.moveViewportUp();
                        } else if (shoot.contains(x, y)) {
                            vp.moveViewportDown(l.mapHeight);
                        } else if (pause.contains(x, y)) {
                            l.switchPlayingStatus();
                        }
                        break;
                }
            }
        }
    }
}
