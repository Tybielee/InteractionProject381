package com.cmpt381.tybie.interactionproject381;

import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by taylorsummach on 15-03-17.
 * The Controller class for the application
 */
public class Controller{

    public Model model;

    /** 2000 ms for an extra long screen press to activate the
     * easy exit event
     */
    public final static int EXTRA_LONG_PRESS_TIME = 2000;

    // the screen x and y for exit touches coordinates
    // ie, the top left of the app
    private boolean isCenterSet;
    private int centerX;
    private int centerY;
    private final static int VARIANCE = 100;

    // tools for gauging length of touch events
    private boolean touchStarted;
    private long touchStartTime;
    private boolean exitTouchWait;
    private boolean secondTouchStarted;

    public Controller(Model m){
        this.model = m;
        this.touchStarted = false;
        this.exitTouchWait = false;
        this.secondTouchStarted = false;
        this.isCenterSet = false;
    }

    /**
     * Interpret a motion event, and decide which custom action to perform
     * @param v pass in the custom, root view
     * @param e pass in a motion event
     * @return whether or not the event was consumed/handled properly
     */
    public boolean interpret(ImageView v, MotionEvent e){


        if (!this.isCenterSet) {
            this.centerX = (v.getWidth() / 2);
            this.centerY = (v.getHeight() / 2);
            this.isCenterSet = true;
        }

        /**
         * if this event is an easy exit, type, call the easy
         * exit function
         */
        if ((!this.touchStarted) && (!this.exitTouchWait)){
            this.touchStarted = true;
            this.touchStartTime = System.currentTimeMillis();
            return true;
        }
        else if ((this.touchStarted) && (!this.exitTouchWait)){
            long currentTime = System.currentTimeMillis();
            if (Math.abs(e.getX() - this.centerX) <= VARIANCE) {
                if (Math.abs(e.getY() - this.centerY) <= VARIANCE) {
                    if (Math.abs(this.touchStartTime - currentTime) < EXTRA_LONG_PRESS_TIME){
                        // keep waiting for the event to happen
                        return true;
                    }
                    else {
                        this.exitTouchWait = true;
                        return true;
                    }
                }
            }
        }
        // else see if we have moved to the top left corner
        else {
            if (e.getX() <= VARIANCE) {
                if (e.getY() <= VARIANCE) {
                    if (!this.secondTouchStarted){
                        this.secondTouchStarted = true;
                        this.touchStartTime = System.currentTimeMillis();
                        return true;
                    }
                    else {
                        long currentTime = System.currentTimeMillis();
                        if (Math.abs(this.touchStartTime - currentTime) < EXTRA_LONG_PRESS_TIME) {
                            // keep waiting
                            return true;
                        }
                        else {
                            this.secondTouchStarted = false;
                            this.touchStartTime = 0;
                            this.touchStarted = false;
                            this.exitTouchWait = false;
                            EasyExit.exit();
                        }
                    }
                }
            }
        }
        //TODO add a timeout for waiting for the second exit touch
        return false;
    }


    public void moveToNextImage(ImageView v){
        this.model.next();
        v.setImageResource(this.model.getCurrentId());
    }
    public void moveToPrevImage(ImageView v)
    {
        this.model.prev();
        v.setImageResource(this.model.getCurrentId());
    }



}
