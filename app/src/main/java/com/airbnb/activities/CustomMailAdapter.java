package com.airbnb.activities;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.airbnb.images.ImageModel;
import com.airbnb.images.MailModel;
import com.google.gson.Gson;
import com.sourcey.activities.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by panagiotis on 9/9/2017.
 */

public class CustomMailAdapter implements Adapter, ListAdapter {

    private Context context;
    private ArrayList<MailModel> mailModelArrayList;


    public CustomMailAdapter(Context context, ArrayList<MailModel> mailModelArrayList) {

        this.context = context;
        this.mailModelArrayList = mailModelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return mailModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mailModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.inbox_item, null, true);

            holder.from = (TextView) convertView.findViewById(R.id.from);
            holder.body = (TextView) convertView.findViewById(R.id.body);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.from.setText("From: " + mailModelArrayList.get(position).getFrom());
        holder.body.setText("Body: " + mailModelArrayList.get(position).getBody());

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }

    private class ViewHolder {

        protected TextView from;
        protected TextView body;

    }
}
