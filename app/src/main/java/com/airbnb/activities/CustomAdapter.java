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
import com.google.gson.Gson;
import com.sourcey.activities.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by panagiotis on 9/9/2017.
 */

public class CustomAdapter implements Adapter, ListAdapter {

    private Context context;
    private ArrayList<ImageModel> imageModelArrayList;
    private String mode;
    private Bundle bundle;


    public CustomAdapter(Context context, ArrayList<ImageModel> imageModelArrayList, String mode, Bundle bundle) {

        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
        this.mode = mode;
        this.bundle = bundle;
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
        return imageModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageModelArrayList.get(position);
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

        System.out.println("Position -> " + position);
        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.cost = (TextView) convertView.findViewById(R.id.cost);
            holder.grade = (RatingBar) convertView.findViewById(R.id.ratingBar);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.reviews = (TextView) convertView.findViewById(R.id.num_of_reviews);
            holder.iv = (ImageView) convertView.findViewById(R.id.imgView);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.description.setText(imageModelArrayList.get(position).getDescription());
        holder.type.setText(imageModelArrayList.get(position).getType());
        holder.grade.setRating(Float.parseFloat( imageModelArrayList.get(position).getGrade()));
        holder.cost.setText(imageModelArrayList.get(position).getCost());
        holder.reviews.setText(imageModelArrayList.get(position).getReviews());

        if(imageModelArrayList.get(position) != null){
            holder.iv.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, ResidenceDetailsActivity.class);
                    String residenceId = new Gson().toJson(imageModelArrayList.get(position).getResidenceId());
                    bundle.putString("mode",mode);
                    bundle.putString("residenceId", residenceId);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            if(imageModelArrayList.get(position).getPath() != null){
                Uri uri = Uri.fromFile(new File(imageModelArrayList.get(position).getPath()));
                Picasso.with(context).load(uri)
                        .resize(1000, 400)
                        .centerCrop().into(holder.iv);
            }

        }

        else
            holder.iv.setImageBitmap(null);

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

        protected TextView description;
        protected TextView type;
        protected RatingBar grade;
        protected TextView cost;
        protected TextView reviews;
        protected ImageView iv;

    }
}
