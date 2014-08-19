package com.zombietweet.android.model;

/**
 * Created by Daniele on 30/07/2014.
 */
public class Media {

    private String image;
    private String text;

    public Media(String image, String text) {
        this.image = image;
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
