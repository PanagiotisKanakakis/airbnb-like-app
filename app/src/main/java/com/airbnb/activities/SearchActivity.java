package com.airbnb.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sourcey.activities.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.location) EditText location;
    @Bind(R.id.arrival) EditText arrival_date;
    @Bind(R.id.departure) EditText departure_date;
    @Bind(R.id.guests) EditText guests;
    @Bind(R.id.btn_search) Button btn_search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });


    }

    private void search() {
        return;
    }
}
