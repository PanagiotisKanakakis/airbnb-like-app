package com.airbnb.images;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by panagiotis on 9/9/2017.
 */

public class ImageModel {

    private String description;
    private String cost;
    private String type;
    private String grade;
    private String reviews;
    private Bitmap image;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap imagePath) {
        this.image = imagePath;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getReviews() {
        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }
}
