package com.cmpt381.tybie.interactionproject381;

import android.inputmethodservice.InputMethodService;

/**
 * Created by taylorsummach on 15-03-17.
 * The Class for the custom interactions
 */
public class CustomIME extends InputMethodService {

    protected CustomView screen;

    @Override
    public CustomView onCreateInputView(){
        this.screen = (CustomView) getLayoutInflater().inflate(R.layout.custom_ime, null);

        return this.screen;
    }

}
