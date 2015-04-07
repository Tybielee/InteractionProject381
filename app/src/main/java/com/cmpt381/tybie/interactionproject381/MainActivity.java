package com.cmpt381.tybie.interactionproject381;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
    protected Model model;
    ArrayList<Float> xValues = new ArrayList<>();
    ArrayList<Float> rollValues = new ArrayList<>();
    ArrayList<Float> pitchValues = new ArrayList<>();
    private boolean rotateMode = true;
    private boolean zoomMode = true;

    private long timer;
    private boolean waitForExit;
    private int centerX;
    private int centerY;


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

        picture = new ImageView(this);

        // get the model resources and set up the model
        String [] imageNames = {"image1", "image2", "image3", "image4", "image5",
            "image6", "image7", "image8", "image9", "image10", "image11",
            "image12", "image13", "sample3"};
        //String [] imageNames = {"image1"};
        int [] imageIds = new int[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
            imageIds[i] = this.getResources().getIdentifier(imageNames[i], "drawable", this.getPackageName());
        }

        model = new Model(imageNames, imageIds);
        controller = new Controller(model,picture);

        picture.setImageResource(model.getCurrentId());
        picture.setLayoutParams(params);

        waitForExit = false;

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

        final TextView t = new TextView(this);

        /**
         * use a touch listener to get events...
         * maybe need something additional for more complex actions?
         */
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                centerX = v.getWidth()/2;
                centerY = v.getHeight()/2;

                if (waitForExit) {
                    return controller.interpret(picture, e);
                }

                if (Math.abs(e.getX() - centerX) < Controller.VARIANCE) {
                    if (Math.abs(e.getY() - centerY) < Controller.VARIANCE){
                        timer = System.currentTimeMillis();
                        waitForExit = true;
                    }
                }

                if (waitForExit && System.currentTimeMillis() - timer > Controller.TIMEOUT){
                    waitForExit = false;
                    timer = System.currentTimeMillis();
                }

                return false;
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
            // if touch is down, actually zoom instead of rotate
                picture.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent e) {
                        if (waitForExit) {
                            rotateMode = false;
                            zoomMode = false;

                            rollValues.clear();
                            pitchValues.clear();

                            return controller.interpret(picture, e);

                        }

                        if (e.getAction() == MotionEvent.ACTION_DOWN) {
                            rotateMode = false;
                            zoomMode = true;
                        }
                        if (e.getAction() == MotionEvent.ACTION_UP) {
                            zoomMode = false;
                            rotateMode = true;
                        }

                        centerX = v.getWidth() / 2;
                        centerY = v.getHeight() / 2;

                        if (Math.abs(e.getX() - centerX) < Controller.VARIANCE) {
                            if (Math.abs(e.getY() - centerY) < Controller.VARIANCE) {
                                timer = System.currentTimeMillis();
                                waitForExit = true;
                            }
                        }

                        if (waitForExit && System.currentTimeMillis() - timer > Controller.TIMEOUT) {
                            waitForExit = false;
                            timer = System.currentTimeMillis();
                        }

                        return true;
                    }
                });

                if (event.values.length > 4) {
                    float[] truncatedRotationVector = new float[4];
                    System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                    update(truncatedRotationVector);
                } else {
                    update(event.values);
                }

        }
        else if (event.sensor == accelerometer && (!rotateMode || !zoomMode)){
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

        if (zoomMode) {
            Log.i("update", "zooming picture");
            zoomPicture();

        }
        else {
            Log.i("update", "rotating picture");
            rotatePictures();
        }
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
        if (rollValues.size() >= 3)
        {
            // get the average change over the last three measurements
            changeInRoll = (rollValues.get(0)+rollValues.get(1)+rollValues.get(2))/3;
            changeInPitch = (pitchValues.get(0)+pitchValues.get(1)+pitchValues.get(2))/3;
            //used for testing and debugging purposes
            Log.i("Rotate", "roll: "+changeInRoll + " pitch: "+changeInPitch);

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

            //clear the values in the array for the next sensors
            rollValues.clear();
            pitchValues.clear();
        }
        else if (rollValues.size() > 3){
            rollValues.clear();
            pitchValues.clear();
        }
    }

    public void zoomPicture(){
        Log.i("zoom", "roll values size = " + rollValues.size());
        if (rollValues.size() >= 3)
        {
            int x = rollValues.size();
            int a = x - 3, b = x - 2, c = x -1;
            // get the average change over the last three measurements
            changeInRoll = (rollValues.get(a)+rollValues.get(b)+rollValues.get(c))/3;
            changeInPitch = (pitchValues.get(a)+pitchValues.get(b)+pitchValues.get(c))/3;
            //used for testing and debugging purposes
            Log.i("Zoom", "roll: "+changeInRoll + " pitch: "+changeInPitch);

            //this now really only rotates left because of how the sensors are taken and to have a more clean looking interaction
            //otherwise the interaction is very buggy
            //example if you rotate the device to the right it will both go to the next photo and rotate but when you move to the next photo the imageView resets to the initial view
            if (changeInPitch > 15)
            {
                controller.zoomOut();
            }
            else if (changeInPitch < 15)
            {
                controller.zoomIn();
            }
            zoomMode = false;

            rollValues.clear();
            pitchValues.clear();
        }
        else if (rollValues.size() > 3){
            rollValues.clear();
            pitchValues.clear();
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
