package com.airbnb.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sourcey.activities.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ResidenceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_residence_details);
    }
}
