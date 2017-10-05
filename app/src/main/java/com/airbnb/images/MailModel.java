package com.airbnb.images;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by panagiotis on 9/9/2017.
 */

public class MailModel {

   private String from;
    private String body;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}