package com.cmpt381.tybie.interactionproject381;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by taylorsummach on 15-03-17.
 * A view for the EasyExit interaction
 */
public class CustomView extends RelativeLayout {
    private final Context context;
    private View v;
    private OnEasyExitEventListener easyExitListener;
    // add more listeners here for other events
    // also need to add an interface etc

    /**
     * Use super constructor to create a custom view
     * @param c, the parent context
     */
    public CustomView(Context c){
        super(c);
        this.context = c;
    }

    // add the required methods to properly extend relative layout
    @Override
    protected void onFinishInflate(){
        this.v = findViewById(R.id.main_view);
    }

    /**
     * set the easyExitListener of the view to the passed in event
     * @param eventListener, the custom event being passed in
     */
    public void setEasyExitEventListener(OnEasyExitEventListener eventListener){
        this.easyExitListener = eventListener;
    }

}
