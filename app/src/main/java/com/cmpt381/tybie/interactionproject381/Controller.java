package com.cmpt381.tybie.interactionproject381;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by taylorsummach on 15-03-17.
 * The Controller class for the application
 */
public class Controller{

    private Model model;
    private ImageView picture;



    /** 2000 ms for an extra long screen press to activate the
     * easy exit event
     */
    public final static int EXTRA_LONG_PRESS_TIME = 3000;

    // the screen x and y for exit touches coordinates
    // ie, the top left of the app
    private boolean isCenterSet;
    private int centerX;
    private int centerY;
    public final static int VARIANCE = 100;

    // tools for gauging length of touch events
    private boolean touchStarted;
    private long touchStartTime;
    private boolean exitTouchWait;
    private boolean secondTouchStarted;
    private long starttime;
    private long currenttime;
    public final static long TIMEOUT = 10000;

    //variable to hold the rotation values
    public int rotation = 0;


    public Controller(Model m, ImageView v){
        this.model = m;
        this.picture = v;
        Log.i("Controller", "Picture is " + picture);
        this.touchStarted = false;
        this.exitTouchWait = false;
        this.secondTouchStarted = false;
        this.isCenterSet = false;
    }

    /**
     * Interpret a motion event, and decide which custom action to perform
     * @param v pass in the custom, root picture
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
            Log.i("Controller", "Starting custom exit interaction");
            this.starttime = System.currentTimeMillis();
            return true;
        }
        else if ((this.touchStarted) && (!this.exitTouchWait)){
            this.currenttime = System.currentTimeMillis();
            if (Math.abs(e.getX() - this.centerX) <= VARIANCE) {
                if (Math.abs(e.getY() - this.centerY) <= VARIANCE) {
                    if (isTimedOut()) {
                        Log.i("Controller", "Timed Out");
                        this.touchStarted = false;
                        this.exitTouchWait = false;
                        this.secondTouchStarted = false;
                        this.currenttime = 0;
                        this.starttime = 0;
                        return true;
                    }
                    if (Math.abs(this.touchStartTime - this.currenttime) < EXTRA_LONG_PRESS_TIME){
                        // keep waiting for the event to happen
                        //Log.i("Controller", "Waiting for Long Press");
                        return true;
                    }
                    else {
                        Log.i("Controller", "Waiting for exit touch");
                        this.exitTouchWait = true;
                        return true;
                    }
                }
            }
        }
        // else see if we have moved to the top left corner
        else {

            if (e.getX() < VARIANCE) {
                if (e.getY() <= VARIANCE) {
                    if (!this.secondTouchStarted){
                        this.secondTouchStarted = true;
                        this.touchStartTime = System.currentTimeMillis();
                        Log.i("Controller", "Second Touch Started");
                        return true;
                    }
                    else {
                        this.currenttime = System.currentTimeMillis();
                        if (Math.abs(this.touchStartTime - this.currenttime) < EXTRA_LONG_PRESS_TIME) {
                            // keep waiting
                            //Log.i("Controller", "Waiting for Final Long Press");
                            return true;
                        }
                        else {
                            this.secondTouchStarted = false;
                            this.touchStartTime = 0;
                            this.touchStarted = false;
                            this.exitTouchWait = false;
                            Log.i("Controller", "Exiting now");
                            EasyExit.exit();
                        }
                    }
                }
            }
        }


        return false;
    }


    public void moveToNextImage(ImageView v){

        this.model.next();
        v.setImageResource(this.model.getCurrentId());
        v.setScaleX((float) model.DEFAULT_SCALE);
        v.setScaleY((float) model.DEFAULT_SCALE);
        model.CURRENT_SCALE = model.DEFAULT_SCALE;
        resetRotation(v);
    }
    public void moveToPrevImage(ImageView v)
    {
        this.model.prev();
        v.setImageResource(this.model.getCurrentId());
        v.setScaleX((float)model.DEFAULT_SCALE);
        v.setScaleY((float)model.DEFAULT_SCALE);
        model.CURRENT_SCALE = model.DEFAULT_SCALE;
        resetRotation(v);
    }
    public void rotateLeft(ImageView v){
        rotation-=90;
        v.setRotation(rotation);
    }
    public void rotateRight(ImageView v){
        rotation+=90;
        v.setRotation(rotation);
    }
    public void resetRotation(ImageView v){
        v.setRotation(0);
    }

    public boolean isTimedOut(){
        this.currenttime = System.currentTimeMillis();
        return (this.currenttime - this.starttime) > TIMEOUT;
    }

    /**
     * double the size of the image
     */
    public void zoomIn(){
        model.CURRENT_SCALE += 0.25;

        picture.setScaleX((float)model.CURRENT_SCALE);
        picture.setScaleY((float) model.CURRENT_SCALE);
    }

    public void zoomOut(){
        model.CURRENT_SCALE -= 0.25;

        picture.setScaleX((float) model.CURRENT_SCALE);
        picture.setScaleY((float) model.CURRENT_SCALE);
    }




}
