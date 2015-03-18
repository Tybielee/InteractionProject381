package com.cmpt381.tybie.interactionproject381;

import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by taylorsummach on 15-03-17.
 * The Controller class for the application
 */
public class Controller{

    public Model model;

    public Controller(Model m){
        this.model = m;
    }

    /**
     * Interpret a motion event, and decide which custom action to perform
     * @param v pass in the custom, root view
     * @param e pass in a motion event
     * @return whether or not the event was consumed/handled properly
     */
    public boolean interpret(ImageView v, MotionEvent e){

        /**
         * if this event is an easy exit, type, call the easy
         * exit function
         */
        if (e.getAction() == MotionEvent.ACTION_DOWN){

            // adjust this as needed to interpret input events
            //this.temp(v,e);

            EasyExit.exit();

            return true;
        }
        return false;
    }


    public void temp(ImageView v, MotionEvent e){
        // test model next()
        this.model.next();

        //test model prev()
        //m.prev();
        v.setImageResource(this.model.getCurrentId());
    }

}
