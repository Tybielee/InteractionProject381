package com.cmpt381.tybie.interactionproject381;

import java.util.ArrayList;

/**
 * Created by taylorsummach on 15-03-15.
 * A Model to store information for the application
 */
public class Model {

    protected ArrayList<ImageResource> images;
    protected ImageResource current;
    protected int idx;

    /**
     * Create the model with the given input names and ids
     * @param names - the list of image file names (without extensions)
     * @param ids - identifying codes for images, used to set imageView contents in main
     */
    public Model(String [] names, int [] ids) {

        images = new ArrayList<>();

        for (int i = 0; i < names.length; i++){
            ImageResource r = new ImageResource(names[i], ids[i]);
            images.add(r);
        }

        this.current = images.get(0);
        this.idx = 0;
    }


    /**
     * Move to the next image in the list, and return the new current image
     * If at the end, wrap to the start
     * @return the current ImageResource
     */
    public ImageResource next(){
       if (this.idx < images.size() - 1){
           this.idx += 1;
       }

       this.current = images.get(this.idx);
       return this.current;
    }

    /**
     * Move to the previous image in the list, and return the new current image
     * If at the start, wrap around to the end
     * @return the current ImageResource
     */
    public ImageResource prev(){
        if (this.idx == 0){
            // for wrapping
            //this.idx = this.images.size() - 1;

            // for no wrapping
            this.idx = 0;
        }
        else{
            this.idx -= 1;
        }
        this.current = images.get(this.idx);
        return this.current;
    }

    /**
     * Get the resource id of the currently selected resource
     * @return the id of the current image resource item
     */
    public int getCurrentId(){
        return this.current.id;
    }

}
