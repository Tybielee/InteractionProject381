package com.cmpt381.tybie.interactionproject381;

import java.util.ArrayList;

/**
 * Created by taylorsummach on 15-03-15.
 * A Model to store information for the application
 */
public class Model {

    public ArrayList<String> images;

    public Model() {
        images = new ArrayList<>();

        // add files *WITHOUT* extensions
        images.add("sample");
    }

}
