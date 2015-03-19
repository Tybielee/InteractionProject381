package com.cmpt381.tybie.interactionproject381;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * Main Activity functions as the view and controller, programmatically creating
 * view components, and providing controller functions for event handling
 */
public class MainActivity extends ActionBarActivity implements SensorEventListener{

    @Override
    public void onSensorChanged(SensorEvent event) {
        //TODO add the Sensor Stuff HERE
        //although I might need to make the controller implement the sensor still unsure about that...
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {/*no need to worry about accuracy */ }

    protected ImageView picture;
    protected RelativeLayout root;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // CustomLayout is pretty much a relative layout + a
        root = new RelativeLayout(this);
        root.setVerticalGravity(RelativeLayout.CENTER_VERTICAL);
        root.setHorizontalGravity(RelativeLayout.CENTER_HORIZONTAL);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        // get the model resources and set up the model
        String [] imageNames = {"sample2", "sample"};
        int [] imageIds = new int[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
            imageIds[i] = this.getResources().getIdentifier(imageNames[i], "drawable", this.getPackageName());
        }

        final Model model = new Model(imageNames, imageIds);
        final Controller controller = new Controller(model);

        picture = new ImageView(this);
        picture.setImageResource(model.getCurrentId());
        picture.setLayoutParams(params);


        /**
         * use a touch listener to get events...
         * maybe need something additional for more complex actions?
         */
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                return controller.interpret(picture,e);
            }
        });

        root.addView(picture);
        setContentView(root);
    }

    @Override
    public void onResume() {
        super.onResume();
        //will need to add code to unregister the sensor here

    }

    @Override
    public void onStop() {
        super.onStop();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();


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
}
