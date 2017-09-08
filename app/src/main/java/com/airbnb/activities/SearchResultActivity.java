package com.airbnb.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sourcey.activities.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logged_in);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intent, 5);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        /*int size = casts.size();
        for (int i = 0; i < size; i++) {
            Cast cast = casts.get(i);
            // create dynamic LinearLayout and set Image on it.
            if (cast != null) {
                LinearLayout clickableColumn = (LinearLayout) inflater.inflate(
                        R.layout.clickable_column, null);
                ImageView thumbnailImage = (ImageView) clickableColumn
                        .findViewById(R.id.thumbnail_image);
                TextView titleView = (TextView) clickableColumn.findViewById(R.id.title_view);
                TextView subTitleView = (TextView) clickableColumn.findViewById(R.id.subtitle_view);
                thumbnailImage.setImageResource(R.drawable.ic_launcher);
            }
        }*/
    }

    /*clickableColumn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Cast cast = (Cast) v.getTag();
            Toast.makeText(getApplicationContext(), cast.getName(), Toast.LENGTH_SHORT).show();
        }

    });*/
}
