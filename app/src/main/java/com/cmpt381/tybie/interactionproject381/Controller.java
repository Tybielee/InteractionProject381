package com.cmpt381.tybie.interactionproject381;

import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by taylorsummach on 15-03-17.
 * The Controller class for the application
 */
public class Controller{

    public Model model;

    private int START_EASY_EXIT = 0;

    /** 2000 ms for an extra long screen press to activate the
     * easy exit event
     */
    public final static int EXTRA_LONG_PRESS_TIME = 2000;

    // the screen x and y for exit touches coordinates
    // ie, the top left of the app
    private final int x = 0;
    private final int y = 0;
    private final static int VARIANCE = 70;

    // tools for gauging length of touch events
    private boolean touchStarted;
    private long touchStartTime;
    private boolean exitTouchWait;
    private boolean secondTouchStarted;



    /**
     * define some constants for event interpreting
     */
    public final static int ZOOM_IN = 1;
    public final static int ZOOM_OUT = 2;
    public final static int ROTATE_LEFT = 3;
    public final static int ROTATE_RIGHT = 4;
    public final static int TILT_LEFT = 5;
    public final static int TILT_RIGHT = 6;
    public final static int EASY_EXIT = 7;


    public Controller(Model m){
        this.model = m;
        this.touchStarted = false;
        this.exitTouchWait = false;
        this.secondTouchStarted = false;
    }

    /**
     * Interpret a motion event, and decide which custom action to perform
     * @param v pass in the custom, root view
     * @param e pass in a motion event
     * @return whether or not the event was consumed/handled properly
     */
    public boolean interpret(ImageView v, MotionEvent e, TextView t){

        /**
         * if this event is an easy exit, type, call the easy
         * exit function
         */
        //if (e.getAction() == MotionEvent.ACTION_DOWN){

        t.setText("x: " + e.getX() + " y: " + e.getY());

            if ((!this.touchStarted) && (!this.exitTouchWait)){
                this.touchStarted = true;
                this.touchStartTime = System.currentTimeMillis();
                return true;
            }
            else if ((this.touchStarted) && (!this.exitTouchWait)){
                long currentTime = System.currentTimeMillis();
                if (Math.abs(e.getX() - (this.x/2)) <= VARIANCE) {
                    if (Math.abs(e.getY() - (this.y/2)) <= VARIANCE) {
                        if (Math.abs(this.touchStartTime - currentTime) < EXTRA_LONG_PRESS_TIME){
                            // keep waiting for the event to happen
                            t.setText("waiting for first touch");
                            return true;
                        }
                        else {
                            t.setText("waiting for exit touch");
                            this.exitTouchWait = true;
                            return true;
                        }
                    }
                }
            }
            else {
                if (Math.abs(e.getX() - (this.x/2)) <= VARIANCE) {
                    if (Math.abs(e.getY() - (this.y)) <= VARIANCE) {
                        if (!this.secondTouchStarted){
                            this.secondTouchStarted = true;
                            this.touchStartTime = System.currentTimeMillis();
                            return true;
                        }
                        else {
                            long currentTime = System.currentTimeMillis();
                            if (Math.abs(this.touchStartTime - currentTime) < EXTRA_LONG_PRESS_TIME) {
                                // keep waiting
                                t.setText("waiting for exit touch");
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
            // adjust this as needed to interpret input events
            //this.temp(v,e);

            //EasyExit.exit();

            return true;
        //}
        //return false;
    }


    /**
     * Figure out which type of custom interaction event we have happening here...
     * @param e the motion event from the touch callback
     * @return the type of event, defined at the top of this controller.java file
     */
    public static int getInteraction(MotionEvent e){
        return 0;
    }




    public void temp(ImageView v, MotionEvent e){
        // test model next()
        this.model.next();

        //test model prev()
        //m.prev();
        v.setImageResource(this.model.getCurrentId());
    }

}
