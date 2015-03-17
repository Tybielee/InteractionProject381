package com.cmpt381.tybie.interactionproject381;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Main Activity functions as the view and controller, programmatically creating
 * view components, and providing controller functions for event handling
 */
public class MainActivity extends ActionBarActivity {

    protected ImageView picture;
    protected CustomView root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //CustomIME rootIME = new CustomIME();
        //root = rootIME.onCreateInputView();

        CustomView root = new CustomView(this);
        root.setVerticalGravity(RelativeLayout.CENTER_VERTICAL);
        root.setHorizontalGravity(RelativeLayout.CENTER_HORIZONTAL);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);


        final TextView t = new TextView(this);
        t.setText("No events yet...");


        // get the model resources and set up the model
        String [] imageNames = {"sample", "sample2"};
        int [] imageIds = new int[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
            imageIds[i] = this.getResources().getIdentifier(imageNames[i], "drawable", this.getPackageName());
        }
        final Model model = new Model(imageNames, imageIds);

        picture = new ImageView(this);
        picture.setImageResource(model.getCurrentId());
        picture.setLayoutParams(params);
        /**
        picture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View e) {
                tempClick(picture, model);
            }
        });
        */

        // add and test the custom EasyExit view and event
        root.setEasyExitEventListener(new OnEasyExitEventListener() {
            @Override
            public void onEvent() {
                tempClick(picture,model);
            }
        });

        /**
         * use a touch listener to get events...
         * maybe need something additional for more complex actions?
         */
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                CustomView cv = (CustomView) v;
                boolean r = interpret(cv,e);
                return r;
            }
        });

        root.addView(t);
        root.addView(picture);
        setContentView(root);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Controller functions
     */


    /**
     * Interpret a motion event, and decide which custom action to perform
     * @param v pass in the custom, root view
     * @param e pass in a motion event
     * @return whether or not the event was consumed/handled properly
     */
    public static boolean interpret(CustomView v, MotionEvent e){

        /** if this event is an easy exit, and this event has an easy
         * exit listener already set up, then call the easyExitListener event
         */
        if (e.getAction() == MotionEvent.ACTION_DOWN){
            if (v.easyExitListener != null){
                v.easyExitListener.onEvent();
                return true;
            }
        }
        return false;
    }


    /**
     * Temporary click handler to switch between images and test model implementation
     */
    public static void tempClick(ImageView v, Model m){
        // test model next()
        m.next();

        //test model prev()
        //m.prev();
        v.setImageResource(m.getCurrentId());
    }




}
