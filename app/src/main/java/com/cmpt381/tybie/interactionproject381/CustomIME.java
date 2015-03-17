package com.cmpt381.tybie.interactionproject381;

import android.inputmethodservice.InputMethodService;
import android.view.View;

/**
 * Created by taylorsummach on 15-03-17.
 * The Class for the EasyExit interaction
 */
public class CustomIME extends InputMethodService {

    protected View screen;

    @Override
    public View onCreateInputView(){
        this.screen = getLayoutInflater().inflate(R.layout.custom_ime, null);
        return this.screen;
    }

}
