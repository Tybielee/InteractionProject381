package com.cmpt381.tybie.interactionproject381;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Main Activity functions as the view and controller, programmatically creating
 * view components, and providing controller functions for event handling
 */
public class MainActivity extends ActionBarActivity implements SensorEventListener{


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor mRotationSensor;
    private float deltaX = 0;
    private float changeInRoll = 0, changeInPitch = 0;
    protected ImageView picture;
    protected RelativeLayout root;
    protected Controller controller;
    ArrayList<Float> xValues = new ArrayList<Float>();
    ArrayList<Float> rollValues = new ArrayList<Float>();
    ArrayList<Float> pitchValues = new ArrayList<Float>();
    private boolean rotateMode = true;

    private static final int SENSOR_DELAY_ROTATE = 500 * 1000; // 500ms
    private static final int SENSOR_DELAY_TILT = 300 * 1000; // 300ms
    private static final int FROM_RADS_TO_DEGS = -57;



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
        String [] imageNames = {"image1", "image2", "image3", "image4", "image5",
            "image6", "image7", "image8", "image9", "image10", "image11",
            "image12", "image13", "sample3"};
        //String [] imageNames = {"image1"};
        int [] imageIds = new int[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
            imageIds[i] = this.getResources().getIdentifier(imageNames[i], "drawable", this.getPackageName());
        }

        final Model model = new Model(imageNames, imageIds);
        controller = new Controller(this,model);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        try {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_TILT);
        } catch (Exception e) {
            Toast.makeText(this, "Accelerometer compatibility issue", Toast.LENGTH_LONG).show();
        }
        try {
            mRotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY_ROTATE);
        } catch (Exception e) {
            Toast.makeText(this, "Rotation compatibility issue", Toast.LENGTH_LONG).show();
        }


        picture = new ImageView(this);
        picture.setImageResource(model.getCurrentId());
        picture.setLayoutParams(params);

        final TextView t = new TextView(this);

        /**
         * use a touch listener to get events...
         * maybe need something additional for more complex actions?
         */
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                t.setText("x: " + e.getX() + " y: " + e.getY());
                return controller.interpret(picture,e);

            }
        });

        root.addView(t);
        root.addView(picture);
        setContentView(root);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //needed function for sensors but do not need to implemenet
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            rotateMode = true;
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values);
            }
        }
        else if (event.sensor == accelerometer && !rotateMode){

            xValues.add(event.values[0]);
            navigatePictures();
        }
    }

    //function that grabs all the rotation values that are needed in order to determine rotate
    private void update(float[] vectors) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        pitchValues.add(orientation[1] * FROM_RADS_TO_DEGS);
        rollValues.add(orientation[2] * FROM_RADS_TO_DEGS);
        rotatePictures();
    }

    //function to navigate through the photos
    private void navigatePictures()
    {
        if (xValues.size() == 3) {
            // get the change of the x,y,z values of the accelerometer
            deltaX = (xValues.get(0)+xValues.get(1)+xValues.get(2))/3;
            xValues.clear();

            if (deltaX > 1){
                controller.moveToPrevImage(picture);
            }
            else if (deltaX < -1) {
                controller.moveToNextImage(picture);
            }

        }
    }

    /*
        Rotating pictures happens in one way. On any photo, if you tilt the device forward, the photo will rotate to the left
        This is because of how many sensor events are sent to the OnSensorChanged function, at one time you could be grabbing the tilting sensor
        and the next time you take the rotation vectors.
     */
    public void rotatePictures()
    {
        if (rollValues.size() == 3)
        {
            changeInRoll = (rollValues.get(0)+rollValues.get(1)+rollValues.get(2))/3;
            changeInPitch = (pitchValues.get(0)+pitchValues.get(1)+pitchValues.get(2))/3;
            //used for testing and debugging purposes
            System.out.println("roll: "+changeInRoll);
            System.out.println("pitch: "+changeInPitch);

            //clear the values in the array for the next sensors
            rollValues.clear();
            pitchValues.clear();
            //this now really only rotates left because of how the sensors are taken and to have a more clean looking interaction
            //otherwise the interaction is very buggy
            //example if you rotate the device to the right it will both go to the next photo and rotate but when you move to the next photo the imageView resets to the initial view
            if (changeInRoll > 25)
            {
                controller.rotateLeft(picture);
            }
            else if (changeInRoll < -25)
            {
                controller.rotateRight(picture);
            }
            rotateMode = false;
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
