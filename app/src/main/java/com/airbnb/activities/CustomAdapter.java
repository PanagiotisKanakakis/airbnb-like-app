package com.airbnb.activities;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sourcey.activities.R;

import java.util.ArrayList;

/**
 * Created by panagiotis on 9/9/2017.
 */

public class CustomAdapter implements Adapter, ListAdapter {

    private Context context;
    private ArrayList<ImageModel> imageModelArrayList;


    public CustomAdapter(Context context, ArrayList<ImageModel> imageModelArrayList) {

        this.context = context;
        this.imageModelArrayList = imageModelArrayList;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

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
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.description.setText(imageModelArrayList.get(position).getDescription());
        holder.type.setText(imageModelArrayList.get(position).getType());
        holder.grade.setRating(Float.parseFloat( imageModelArrayList.get(position).getGrade()));
        holder.cost.setText(imageModelArrayList.get(position).getCost());
        holder.reviews.setText(imageModelArrayList.get(position).getReviews());
        holder.iv.setImageResource(imageModelArrayList.get(position).getImage_drawable());

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
        private ImageView iv;

    }
}
