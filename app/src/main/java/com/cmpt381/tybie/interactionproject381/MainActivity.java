package com.cmpt381.tybie.interactionproject381;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
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
public class MainActivity extends ActionBarActivity implements SensorEventListener{

    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private TextView DeltaX, LastX;
    private float vibrateThreshold = 0;
    public Vibrator v;
    protected ImageView picture;
    protected RelativeLayout root;
    protected Controller controller;

    public void initializeViews() {
        DeltaX = new TextView(getApplicationContext());
        LastX = new TextView(getApplicationContext());
    }



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

        // initialize the views
        initializeViews();

        // get the model resources and set up the model
        String [] imageNames = {"sample2", "sample"};
        int [] imageIds = new int[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
            imageIds[i] = this.getResources().getIdentifier(imageNames[i], "drawable", this.getPackageName());
        }

        final Model model = new Model(imageNames, imageIds);
        controller = new Controller(model);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fail! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

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

        root.addView(DeltaX);
        // root.addView(LastX);
        root.addView(picture);
        setContentView(root);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        LastX.setText("LastX: " + Float.toString(lastX));

        // get the change of the x,y,z values of the accelerometer
        deltaX = lastX - event.values[0];

        float threshold = Math.abs(lastX-event.values[0]);

        DeltaX.setText("DeltaX: " + Float.toString(deltaX)+"thresh: "+threshold);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX > 2 && threshold > 0.08){
            controller.moveToPrevImage(picture);
            deltaX=0;
        }
        else if (deltaX < -2 && threshold > 0.08) {
            controller.moveToNextImage(picture);
            deltaX=0;
        }

        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        // set the last know values of x,y,z
        lastY = event.values[1];
        lastX = event.values[0];
        lastZ = event.values[2];

        //vibrate();
    }

    // if the change in the accelerometer value is big enough, then vibrate!
    // our threshold is MaxValue/2
    public void vibrate() {
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
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
